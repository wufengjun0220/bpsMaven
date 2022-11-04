package com.mingtech.application.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ognl.Ognl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.query.domain.QueryProductType;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.ReturnMessage;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.application.sysmanage.vo.DepartmentNode;
import com.mingtech.application.sysmanage.vo.QueryProductTypeNode;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

public class DepartmentServiceImpl extends GenericServiceImpl implements
		DepartmentService {
	private static final Logger logger = Logger.getLogger(DepartmentServiceImpl.class);
	@Autowired
	private RoleService roleService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private UserService userService;
	
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;

	public Class getEntityClass() {
		return Department.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(Department.class);
	}
	
	public String getDeptsJSON(Department dept, Page page,User user) throws Exception {
//		if("1".equals(dept.getTxFlag())){
//			queryAndUpdateDept(dept, user);
//		}
		
		List paras = new ArrayList();
		String sb = " select dept from Department as dept where 1=1 ";
		if (StringUtil.isNotBlank(dept.getName())) {
			sb += " and dept.name like ?";
			paras.add("%" + dept.getName() + "%");
		}
		if (StringUtil.isNotBlank(dept.getInnerBankCode())) {
			sb += " and dept.innerBankCode like ?";
			paras.add("%" + dept.getInnerBankCode() + "%");
		}
//		if (dept.getLevel() == 0 ) {
//			sb += " and dept.level in ('5','7')";
//		}else{
//			sb += " and dept.level = ?";
//			paras.add("%" + dept.getInnerBankCode() + "%");
//		}
		
		if(dept.getParent() != null) {
			sb += " and dept.parent.id like ?";
			paras.add("%" + dept.getParent().getId() + "%");
		}
		sb += " order by dept.order DESC";
		List roles = roles = find(sb, paras, page);;

		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		List list = new ArrayList();
		if(roles != null && roles.size() > 0){
			for (Object object : roles) {
				Department dept1 = (Department) object;
				if(dept1.getParent() != null){
					dept1.setParentName(dept1.getParent().getName());
					
				}
				list.add(dept1);
			}
		}
		return JsonUtil.fromCollections(list, map);
	}
	
	public String getDeptsJSON1(Department dept, Page page,User user) throws Exception {
//		if("1".equals(dept.getTxFlag())){
//			queryAndUpdateDept(dept, user);
//		}
		
		List paras = new ArrayList();
		String sb = " select dept from Department as dept where 1=1 ";
		if (StringUtil.isNotBlank(dept.getName())) {
			sb += " and dept.name like ?";
			paras.add("%" + dept.getName() + "%");
		}
		if (StringUtil.isNotBlank(dept.getInnerBankCode())) {
			sb += " and dept.innerBankCode like ?";
			paras.add("%" + dept.getInnerBankCode() + "%");
		}
		if (dept.getLevel() == 0 || dept.getLevel() == 1) {
			sb += " and dept.level in ('5','7')";
		}else{
			sb += " and dept.level = ?";
			paras.add(dept.getLevel());
		}
		
		if(dept.getParent() != null) {
			sb += " and dept.parent.id like ?";
			paras.add("%" + dept.getParent().getId() + "%");
		}
		sb += " order by dept.order DESC";
		List roles = roles = find(sb, paras, page);;

		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		List list = new ArrayList();
		if(roles != null && roles.size() > 0){
			for (Object object : roles) {
				Department dept1 = (Department) object;
				if(dept1.getParent() != null){
					dept1.setParentName(dept1.getParent().getName());
					
				}
				list.add(dept1);
			}
		}
		return JsonUtil.fromCollections(list, map);
	}
	
	/**
	 * 查询中台机构开关信息   并更新本地
	 * @throws Exception 
	 * */	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//	public void queryAndUpdateDept(Department dept,User user) throws Exception{
//		CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
//		
//		if(dept != null && StringUtil.isEmpty(dept.getName())){
//			centerPlatformBean.setBranchCode(dept.getInnerBankCode());
//			centerPlatformBean.setBranchName(dept.getName());
//			if(dept.getParent() != null){
//				centerPlatformBean.setSuperBranchNo(dept.getParent().getInnerBankCode());
//			}
//		}
//		ReturnMessageNew messageNew = centerPlatformSysService.txQueryDepartMentConfig(centerPlatformBean, user);
//		
//		List<Map> details = messageNew.getDetails();
//		for(int i = 0;i < details.size();i++){
//			Map map = details.get(i);
//	    	String sql1 = "update t_department set TX_FLAG = '" +  map.get("RULE_ARRAY.BRANCH_ONLINE_DISCOUNT_STATUS") 
//	    			+ "', TX_ID = '" + map.get("RULE_ARRAY.BRANCH_ONLINE_DISCOUNT_ID") 
//	    			+ "'  where d_innerBankCode = '" + map.get("RULE_ARRAY.BRANCH_NO") 
//	    			+ "' and d_name = '" + map.get("RULE_ARRAY.BRANCH_NAME") + "'";
//	    	dao.updateSQL(sql1);
//		}
//	}
	
	/**
	 * 根据机构层级编码 查询所有的 本机构及子机构
	 * @param levCode 层级编码
	 * @return
	 */
	public List getAllSubDepartmentByLevelCode(String levCode){
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		sb.append("from Department dept WHERE dept.levelCode like '"+levCode+"%'");
		sb.append(" order by levelCode ASC");
		return this.find(sb.toString());
	}

	public String getDepartments(Tree tree) throws JSONException {
		List children = null;
		if (StringUtil.equals(tree.getId(), "-1")) {// 取根节点
			children = this.getAllChildren(null, 1);
		} else {
			Department department = (Department) this.load(tree.getId());
			children = this.getAllChildren(department.getId(), 1);
		}
		StringBuffer sb = new StringBuffer();
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		} else {
			List list = new ArrayList();
			for (int i = 0; i < children.size(); i++) {
				Department child = (Department) children.get(i);
				DepartmentNode tmp = new DepartmentNode(child);
				tmp.setText(child.getName());
				if (CollectionUtil.isEmpty(this
						.getAllChildren(child.getId(), 1))) {
					tmp.setLeaf(true);
				}
				list.add(tmp);
			}
			sb.append(JsonUtil.fromCollections(list));
		}
		return sb.toString();
	}

	/**
	 * 根据机构id加载机构及子机构json串
	 *
	 * @param id
	 *            机构id
	 * @return
	 * @author huangshiqiang
	 * @date Mar 18, 2009
	 * @comment
	 */
	public String getDepartments(String id) {
		Department department = null;
		if (StringUtil.equals(id, "-1")) {// 取根节点
			List result = this.getAllChildren(null, 1);
			if (!CollectionUtil.isEmpty(result)) {
				department = (Department) result.get(0);
			}
		} else {
			department = (Department) this.load(id);
		}
		return buildJson(department);
	}

	/**
	 * 加载所有子机构json
	 *
	 * @param dept
	 * @return
	 * @author huangshiqiang
	 * @date Mar 18, 2009
	 * @comment
	 */
	private String buildJson(Department dept) {
		StringBuffer sb = new StringBuffer("[");
		// Department department = (Department) this.load(dept.getId());
		List children = this.getAllChildren(dept.getId(), 1);
		Tree tree = new Tree();
		tree.setId(dept.getId());
		tree.setText(dept.getName());
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		} else {
			StringBuffer sbChildren = new StringBuffer();
			for (int i = 0; i < children.size(); i++) {
				Department temp = (Department) children.get(i);
				sbChildren.append(buildJson(temp));
			}
			tree.setChildren(sbChildren.toString());
		}
		sb.append(JsonUtil.fromObject(tree));
		sb.append("]");
		return sb.toString();
	}

	public List getAllChildren(String pid, int status) {
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		if (null == pid) {
			sb.append("from Department dept WHERE dept.parent is null");
		} else {
			sb.append("from Department dept WHERE dept.parent =?");
			paras.add(pid);
		}
		if (status >= 0) {
			sb.append(" and dept.status = ?");
			paras.add(new Integer(status));
		}
		sb.append(" order by order ASC");
		// return this.find(sb.toString());
		return this.find(sb.toString(), paras);
	}
	
	public List getAllChildrenProductType(String pid) {
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		if (null == pid) {
			sb.append("from QueryProductType dept where dept.parentTypeId is null");
		} else {
			sb.append("from QueryProductType dept where dept.parentTypeId=?");
			paras.add(pid);
		}
		return this.find(sb.toString(), paras);
	}

	public List getAllChildrenIdList(String pid, int status) {
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		if (null == pid) {
			sb
					.append("select id from Department dept WHERE dept.parent is null");
		} else {
			sb.append("select id from Department dept WHERE dept.parent =?");
			paras.add(pid);
		}
		if (status >= 0) {
			sb.append(" and dept.status = ?");
			paras.add(new Integer(status));
		}
		sb.append(" order by order ASC");
		// return this.find(sb.toString());
		return this.find(sb.toString(), paras);
	}

	public List getAllChildrenBankCodeList(String bankNumber, int status) {
		List bankList = new ArrayList();
		List numberList = new ArrayList();
		if(null !=bankNumber){
			bankList.add(bankNumber);
			numberList.add(bankNumber);
			getAllChildrenBankCodeListNew(numberList,bankList,status);
		}else{
			getAllChildrenBankCodeListNew(numberList,null,status);
		}
		return numberList;
	}

	public void getAllChildrenBankCodeList(List numberList, String bankNumber,
			int status) {
		//2010-11-08 修改递归查询下级机构方法--张永超
		List bankList = new ArrayList();
		if(null !=bankNumber){
			bankList.add(bankNumber);
			numberList.add(bankNumber);
			getAllChildrenBankCodeListNew(numberList,bankList,status);
		}else{
			getAllChildrenBankCodeListNew(numberList,null,status);
		}
	}
	
	/**
	* <p>方法名称: getAllChildrenBankCodeListNew|描述: 新增递归方法2010-11-3 张永超</p>
	* @param numberList
	* @param bankList
	* @param status
	*/
	private void getAllChildrenBankCodeListNew(List numberList, List bankList,int status) {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();// 查询条件值
		List values = new ArrayList();// 查询条件名
		if(null != bankList && bankList.size()>0){
			sb.append("select dept.bankNumber from Department dept WHERE dept.parent.bankNumber in(:bankNumber) and dept.isOrg = '1' ");
			keys.add("bankNumber");
			values.add(bankList);
			if (status >= 0) {
				sb.append(" and dept.status =:status ");
				keys.add("status");
				values.add(new Integer(status));
			}
		}else{
			sb.append("select dept.bankNumber from Department dept WHERE dept.parent is null");
		}
		String[] nameForSetVar = (String[]) keys.toArray(new String[keys.size()]);// 查询条件名
		List deptList = (ArrayList) this.find(sb.toString(), nameForSetVar,values.toArray());
		if (null != deptList && deptList.size()>0) {
			List banks = new ArrayList();
			for (int i = 0; i < deptList.size(); i++) {
//				Department dept = (Department) deptList.get(i);
				if (deptList.get(i) == null)
					continue;
				numberList.add(deptList.get(i));
				banks.add(deptList.get(i));
			}
			this.getAllChildrenBankCodeListNew(numberList, banks, status);
		}
	}
	
	public List getAllChildrenInnerCodeList(String innerCode, int status) {
		StringBuffer sb = new StringBuffer();
		if(StringUtil.isNotBlank(innerCode)){
			sb.append("select dept.d_innerbankcode from t_department dept");
			sb.append(" start with dept.d_innerbankcode = '"+innerCode+"' ");
			sb.append(" and dept.D_ISORG = '1' ");
			if (status >= 0) {
				sb.append(" and dept.D_STATUS = "+status+" ");
			}
			sb.append(" connect by prior dept.id=dept.d_pid ");
			return this.dao.SQLQuery(sb.toString());
		}
		
		return null;
	}

	public boolean hasChildren(String departmentId, int type) {
		String query = " from Department as department where department.parent = ? and department.status=1";
		long size = 0;
		List paras = new ArrayList();
		paras.add(departmentId);
		if (type >= 0) {// 暂时没有子部门的需求
		// query += " and department.isOrg = ?";
		// paras.add(Integer.toString(type));
		}
		size = this.getRowCount(query, paras).longValue();
		if (size > 0)
			return true;
		return false;
	}
	
	public boolean hasChildren(String departmentId , int type, int status){
		String query = " from Department as department where department.parent = ?";
		long size = 0;
		List paras = new ArrayList();
		paras.add(departmentId);
		if(type >=0){
			query += " and department.isOrg = ?";
			paras.add(Integer.toString(type));
		}
		if(status >= 0){
			query += " and department.status = ?";
			paras.add(Integer.toString(status));
		}
		size = this.getRowCount(query, paras).longValue();
		if (size > 0)
			return true;
		return false;
	}

	public String getDeptXml(String pid, int status) throws Exception {
		StringBuffer sb = new StringBuffer();
		List deptArr = new ArrayList();
		deptArr = this.getAllChildren(pid, status);
		String tempId = "";
		Department dept;
		if (null != deptArr) {
			for (int i = 0; i < deptArr.size(); i++) {
				dept = (Department) deptArr.get(i);
				tempId = dept.getId();
				sb.append("<item text='");
				sb.append(dept.getName());
				sb.append("' id='");
				sb.append(tempId);
				sb.append("' child='1");
				sb.append("'>");
				// sb.append("<userdata
				// name='levelType'>"+Integer.toString(dept.getType())+"</userdata>");
				sb.append("</item>");
			}
		}
		return sb.toString();
	}

	public List getAllDept() throws Exception {
		String hql = "from Department department";
		return this.find(hql);
	}
	
	public List getAllDeptNoDif() throws Exception {
		
		List list = new ArrayList(); 	
		List list1 = new ArrayList(); 		
		List list2 = new ArrayList(); 	
		
		String sql1 = " SELECT a.d_innerbankcode ,a.d_name FROM t_department a where  ";
		sql1 = sql1 + "  a.d_innerbankcode  in (   SELECT b.d_innerbankcode  FROM  t_department b GROUP BY  b.d_innerbankcode having count(*)>1 )  ";
		sql1 = sql1 + "  and a.rowid not in (   SELECT min(rowid)  FROM  t_department c GROUP BY  c.d_innerbankcode having count(*)>1 ) ";
		list1 = this.dao.SQLQuery(sql1);
		
		
		String sql2 = " SELECT a.d_innerbankcode ,a.d_name FROM t_department a where   ";
		sql2 = sql2 + "  a.d_innerbankcode not in  ( SELECT b.d_innerbankcode  FROM  t_department b GROUP BY  b.d_innerbankcode having count(*)>1 )  ";
		list2 = this.dao.SQLQuery(sql2);
		
		if(list1!=null && list1.size()>0){
			list.addAll(list1);
		}
		if(list2!=null && list2.size()>0){
			list.addAll(list2);
		}
		
		List<Department> Deptlist = new ArrayList<Department>(); 
		if(list!=null && list.size()>0){
			for(int i = 0 ;i<list.size();i++ ){
				Department dept = new Department();
				Object[] obj = (Object[]) list.get(i);
				if(obj[0]!=null){
					dept.setInnerBankCode(obj[0].toString());
				}
				if(obj[1]!=null){
					dept.setName(obj[1].toString());
				}
				Deptlist.add(dept);
			}
			return Deptlist;
		}
		
		
		return null;

	}
	

	public Department getDeptById(String deptId) throws Exception {
		return (Department) dao.load(getEntityClass(), deptId);
	}

	/**
	 * 查询得到所有机构的列表 Msg065专用 by chenyuefeng
	 */
	public List getAllDepartmentsList(int status, int isOrg) throws Exception {
		String hql = "from Department department where department.status ="
				+ status + " and department.isOrg =" + isOrg;
		return this.find(hql);
	}
	/**
	 * 根据大额行号 查询 机构信息
	 * @param netPointCode
	 * @return
	 * @throws Exception
	 */
	public Department getDepartmentByBankNo(String bankNo)throws Exception {
		String sql = "select dto from Department dto where dto.bankNumber=?";
		List param = new ArrayList();
		param.add(bankNo);
		List result = this.find(sql, param);
		return (result != null && result.size() > 0) ? (Department) result
				.get(0) : null;
	
	}
	public Department getDepartmentByInnerBankCode(String netPointCode)
			throws Exception {
		String sql = "select dto from Department dto where dto.innerBankCode=?";
		List param = new ArrayList();
		param.add(netPointCode);
		List result = this.find(sql, param);
		return (result != null && result.size() > 0) ? (Department) result
				.get(0) : null;

	}
	public Department getCenterAcptDept()throws Exception {
		String sql = "select dto from Department dto where dto.acptDept=?";
		List param = new ArrayList();
		param.add("3");
		List result = this.find(sql, param);
		return (result != null && result.size() > 0) ? (Department) result
				.get(0) : null;
	
	}

	public Department getOrgCodeByBankNumber(String bankNumber) {
		String query = " from Department as department where department.bankNumber = ? and department.status = '1' and department.isOrg='1' ";
		List paras = new ArrayList();
		paras.add(bankNumber);
		List list = this.find(query, paras);
		if (list != null && list.size() > 0) {
			return (Department) list.get(0);
		}
		return null;
	}

	public void txmodifyOrgIds(String id, String p_id) throws Exception {
		Department dep = this.getDeptById(id);
		Department dep_p = this.getDeptById(p_id);
		dep.setParent(dep_p);
		//如果将支行转移到总行之下，则修改当前行级别为分行
		if(dep_p.getLevel()==1)
			dep.setLevel(2);
		this.txStore(dep);
	}

	public Department queryByInnerBankCode(String innerBankCode) {
		String sql = " from Department as department where department.innerBankCode=?";
		List param = new ArrayList();
		param.add(innerBankCode);
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (Department) result.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 增加部门校验输入信息是否合法 by chenfl
	 */
	public String verifyAddDeptIsRight(Department _dept) throws Exception {
		String isRight="true";
		if (_dept == null)
			throw new Exception("$$$$错误信息：调用verifyAddDeptIsRight服务传入参数为空！$$$$");
		List deptList=this.getAllDept();
		//如果是更新操作
		if(_dept.getId()!=null&&!_dept.getId().equals("")){
			Department srcDept=(Department)this.load(_dept.getId());
			if(myEquals(srcDept,_dept))return isRight;
			//否则有修改,删除原对象，将该对象以新增对象对待
			deptList.remove(srcDept);
		}
		Iterator it=deptList.iterator();
		while(it.hasNext()){
			Department dept=(Department)it.next();
			//判断是否输入重复
			if(StringUtils.equals(_dept.getName(),dept.getName())){
				isRight="机构名称不能重复，请重新输入！";
				break;
			}
			if (_dept.getIsOrg() != Department.DEPT_DEPARTMENT) {
				if(StringUtils.equals(_dept.getOrgCode(),dept.getOrgCode())){
					isRight="组织机构代码不能重复，请重新输入！";
					break;
				}
				if(StringUtils.equals(_dept.getBankNumber(), dept.getBankNumber())){
					isRight="行号重复，请重新输入！";
					break;
				}
			}
			if(StringUtils.equals(_dept.getInnerBankCode(), dept.getInnerBankCode())){
				isRight="系统内部行号重复，请重新输入！";
				break;
			}
//			if(StringUtils.equals(_dept.getAuditBankCode(), dept.getAuditBankCode())){
//				isRight="核算中心网点号重复，请重新输入！";
//				break;
//			}
			continue;
		}
		return isRight;
	}
	private boolean myEquals(Department d1,Department d2){
		if(StringUtils.equals(d1.getAuditBankCode(),d2.getAuditBankCode())&&
				StringUtils.equals(d1.getName(),d2.getName())&&
				StringUtils.equals(d1.getOrgCode(),d2.getOrgCode())&&
				StringUtils.equals(d1.getInnerBankCode(),d2.getInnerBankCode())&&
				StringUtils.equals(d1.getBankNumber(),d2.getBankNumber())&&
				d1.getIsOrg()==d2.getIsOrg()&&
				d1.getLevel()==d2.getLevel())
			return true;
		return false;
	}


	public String getDepartments(Tree tree,User user) throws JSONException {
		List children =new ArrayList();
		if (StringUtil.equals(tree.getId(), "-1")) {// 取根节点
			//children = this.getAllChildren(null, 1);
			Department curDept = user.getDepartment();
			if(null == curDept.getParent()) {
				// 1.当前机构为总行（上级机构为空）
				// 什么都不处理，默认curDept为总行
			} else {
				//2.当前机构级别为总行且上级机构为总行 ，说明有总行权限
				Department curParent = curDept.getParent();
				curParent = (Department) this.load(curParent.getId());
				if (curDept.getLevel() == 1 && curParent.getParent() == null) {
					// 替换当前机构为总行，查询全行机构
					curDept = curParent;
				}
			}
			children.add(this.load(curDept.getId()));
		} else {
			Department department = (Department) this.load(tree.getId());
			children = this.getAllChildren(department.getId(), 1);
		}
		StringBuffer sb = new StringBuffer();
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		} else {
			List list = new ArrayList();
			for (int i = 0; i < children.size(); i++) {
				Department child = (Department) children.get(i);
				DepartmentNode tmp = new DepartmentNode(child);
				tmp.setText(child.getName());
				if (CollectionUtil.isEmpty(this.getAllChildren(child.getId(),1))) {
					tmp.setLeaf(true);
				}
				list.add(tmp);
			}
			sb.append(JsonUtil.fromCollections(list));
		}
		return sb.toString();
	}
	
	public String getProIdJson(Tree tree,String billMedia) throws Exception {
		List children =new ArrayList();
		if (StringUtil.equals(tree.getId(), "-1")) {// 取根节点
			String hql = "from QueryProductType qp where qp.id='"+billMedia+"'";
			List hqlList = this.find(hql);
			children.add(hqlList.get(0));
		} else {
			QueryProductType childPt = (QueryProductType)this.dao.load(QueryProductType.class,tree.getId());
			children = this.getAllChildrenProductType(childPt.getId());
		}
		StringBuffer sb = new StringBuffer();
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		}else{
			List list = new ArrayList();
			for (int i = 0; i < children.size(); i++) {
				QueryProductType childPro = (QueryProductType)children.get(i);
				QueryProductTypeNode tmp = new QueryProductTypeNode(childPro);
				tmp.setText(childPro.getProductTypeName());
				tmp.setProNumber(childPro.getId());
				if (CollectionUtil.isEmpty(this.getAllChildrenProductType(childPro.getId()))){
					tmp.setLeaf(true);
				}
				list.add(tmp);
			}
			sb.append(JsonUtil.fromCollections(list));
		}
		return sb.toString();
	}

	public String getDepartByisBranch(String bankNum) throws Exception {
		
		String hql = "select dto from Department dto where dto.bankNumber='"+bankNum+"'";
		
		List dept = this.find(hql);
		if(null != dept && dept.size()>0){
			Department deptOld = (Department)dept.get(0);
			if("Y".equals(deptOld.getIsBranch())){
				return bankNum;
			}else{
				Department deptParent = deptOld.getParent();
				return deptParent.getBankNumber();
			}
		}else{
			throw new Exception("数据库中没有大额行号为【"+bankNum+"】的机构");
		}
	}
	
	public Department getDepartmentByInnerBankNumber(String bankNumber)
		throws Exception {
			String sql = "select dto from Department dto where dto.bankNumber=?";
			List param = new ArrayList();
			param.add(bankNumber);
			List result = this.find(sql, param);
			return (result != null && result.size() > 0) ? (Department) result
					.get(0) : null;

	}
	
	public List getAllChildrenDepartmentByDeptId(String deptId,boolean onlyOrg,boolean onlyUsed,String returnParam)throws Exception{
		
		List respList = new ArrayList();
		List reqList = new ArrayList();
		if(null !=deptId){
			reqList.add(deptId);
			//查询本身
			Department dp = (Department) this.load(deptId);
			respList.add(Ognl.getValue(returnParam, dp));
			getAllChildrenDepartmentByDeptIdList(respList,reqList,onlyOrg,onlyUsed,returnParam);
		}else{
			getAllChildrenDepartmentByDeptIdList(respList,null,onlyOrg,onlyUsed,returnParam);
		}
		return respList;
	}
	
	private void getAllChildrenDepartmentByDeptIdList(List respList, List reqList,boolean onlyOrg,boolean onlyUsed,String returnParam) {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();// 查询条件值
		List values = new ArrayList();// 查询条件名
		if(null != reqList && reqList.size()>0){
			sb.append("select dept."+returnParam+",dept.id from Department dept WHERE dept.parent.id in(:reqList) ");
			keys.add("reqList");
			values.add(reqList);
			if(onlyOrg){
				sb.append(" and dept.isOrg = '1'  ");
			}
			if(onlyUsed){
				sb.append(" and dept.status = '1' ");
			}
		}else{
			sb.append("select dept."+returnParam+",dept.id from Department dept WHERE dept.parent.id is null");
		}
		String[] nameForSetVar = (String[]) keys.toArray(new String[keys.size()]);// 查询条件名
		List deptList = (ArrayList) this.find(sb.toString(), nameForSetVar,values.toArray());
		if (null != deptList && deptList.size()>0) {
			List banks = new ArrayList();
			for (int i = 0; i < deptList.size(); i++) {
				Object[] obj = (Object[]) deptList.get(i);
				if (obj[0] == null)
					continue;
				respList.add(obj[0]);
				banks.add(obj[1]);
			}
			this.getAllChildrenDepartmentByDeptIdList(respList, banks, onlyOrg,onlyUsed,returnParam);
		}
	}
	
	public List getAllStockDepartmentByDeptId(String deptId)throws Exception{
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();// 查询条件值
		List values = new ArrayList();// 查询条件名
		sb.append("select dept.bankNumber from Department dept WHERE dept.stockDept.id =:deptId or dept.discountDtockDept.id =:deptId1");
		keys.add("deptId");
		keys.add("deptId1");
		values.add(deptId);
		values.add(deptId);
		String[] nameForSetVar = (String[]) keys.toArray(new String[keys.size()]);// 查询条件名
		List deptList = (ArrayList) this.find(sb.toString(), nameForSetVar,values.toArray());
		return deptList;
	}
	
	public List getAllStockDeptIdByDeptId(String deptId)throws Exception{
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();// 查询条件值
		List values = new ArrayList();// 查询条件名
		sb.append("select dept.id from Department dept WHERE dept.stockDept.id =:deptId or dept.discountDtockDept.id =:deptId1");
		keys.add("deptId");
		keys.add("deptId1");
		values.add(deptId);
		values.add(deptId);
		String[] nameForSetVar = (String[]) keys.toArray(new String[keys.size()]);// 查询条件名
		List deptList = (ArrayList) this.find(sb.toString(), nameForSetVar,values.toArray());
		return deptList;
	}
	
	//根据机构id获取所有下级机构
	public List getAllChildrenDepartmentDtosByDeptId(String deptId,boolean onlyOrg,boolean onlyUsed)throws Exception{
		
		List respList = new ArrayList();
		List reqList = new ArrayList();
		if(null !=deptId){
			reqList.add(deptId);
			//查询本身
			Department dp = (Department) this.load(deptId);
			respList.add(dp);
			getAllChildrenDepartmentByDeptIdList2(respList,reqList,onlyOrg,onlyUsed);
		}else{
			getAllChildrenDepartmentByDeptIdList2(respList,null,onlyOrg,onlyUsed);
		}
		return respList;
	}
	
	private void getAllChildrenDepartmentByDeptIdList2(List respList, List reqList,boolean onlyOrg,boolean onlyUsed) {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();// 查询条件值
		List values = new ArrayList();// 查询条件名
		if(null != reqList && reqList.size()>0){
			sb.append("select dept from Department dept WHERE dept.parent.id in(:reqList) ");
			keys.add("reqList");
			values.add(reqList);
			if(onlyOrg){
				sb.append(" and dept.isOrg = '1'  ");
			}
			if(onlyUsed){
				sb.append(" and dept.status = '1' ");
			}
		}else{
			sb.append("select dept from Department dept WHERE dept.parent.id is null");
		}
		String[] nameForSetVar = (String[]) keys.toArray(new String[keys.size()]);// 查询条件名
		List deptList = (ArrayList) this.find(sb.toString(), nameForSetVar,values.toArray());
		if (null != deptList && deptList.size()>0) {
			List banks = new ArrayList();
			for (int i = 0; i < deptList.size(); i++) {
				Department obj = (Department) deptList.get(i);
				respList.add(obj);
				banks.add(obj.getId());
			}
			this.getAllChildrenDepartmentByDeptIdList2(respList, banks, onlyOrg,onlyUsed);
		}
	}
	
	public List getAllOrgCod(){
		List orgListCod = new ArrayList();
		try {
			List allDepartMent=this.getAllDepartmentsList(1,1);
			
			Iterator it = allDepartMent.iterator();
			while(it.hasNext()){
				Department departMent =(Department)it.next();
				
				if(null==departMent||null==departMent.getDiscountDtockDept()){
					continue;
				}
				String orgCode = departMent.getDiscountDtockDept().getId();
				if(!orgListCod.contains(orgCode)){
					orgListCod.add(orgCode);
				}
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
			return orgListCod;
	}
	
		public Department queryByOrgCode(String orgCode){
		String sql = " from Department as department where department.orgCode=?";
		List param = new ArrayList();
		param.add(orgCode);
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (Department) result.get(0);
		} else {
			return null;
		}
	}

	public Department getHeadBank() throws Exception{
		String hql = "select department from Department department where department.parent is null and department.level = 1 ";
		List list = this.find(hql);
		if(!list.isEmpty()){
			return (Department) list.get(0);
		}
		return null;
		
	}
	/**
	* <p>方法名称: getDeptByPjsBrchNo|描述: 通过票交所机构号</p>
	* @param 票交所机构号
	* @return
	* @throws Exception
	*/
	public Department getDeptByPjsBrchNo(String pjsBrchNo){
		String sql = " from Department as department where department.pjsBrchNo=?";
		List param = new ArrayList();
		param.add(pjsBrchNo);
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (Department) result.get(0);
		} else {
			return null;
		}
	}
	
	public Department getSubsidiaryBank(String bankNumber) throws Exception{
		Department dept = this.getOrgCodeByBankNumber(bankNumber);
		if(dept != null){
			if(dept.getLevel() == 2){ // 传入的行号是分行，直接返回本行对象
				return dept;
			}else{
				dept = this.getSubsidiaryBank(dept.getParent().getBankNumber()); // 查询本行的上级行是否是分行
			}
		}
		return dept;
	}
	
	public Department getParentBank(String bankNumber) throws Exception{
		Department dept = this.getOrgCodeByBankNumber(bankNumber);
		if(dept != null){
			if(dept.getLevel() == 2 && !dept.getBankNumber().equals("313658010190")){ // 传入的行号是分行，直接返回本行对象
				return dept;
			}else{
				if(dept.getParent() != null){
				    dept = this.getParentBank(dept.getParent().getBankNumber()); //查询
				}
				return dept;
			}
		}
		return dept;
	}

	public List getAcceptionBankList() throws Exception{
		String hql = "select department from Department department where department.acptDept = '1' ";
		return this.find(hql);
	}

	public List getAcceptionBankNumber() throws Exception{
		String hql = "select department.bankNumber from Department department where department.acptDept = '1' ";
		return this.find(hql);
		
	}
	public String txDepartmentToSave(ReturnMessage request) throws Exception {
		Map head = request.getHead();
		//---------------------三菱项目------------------start
		// 在删除的时候使用 zhangjunshuai 2012-05-04 
	    UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		//---------------------三菱项目------------------end
		Department dep = this.queryByInnerBankCode((String) head.get("innerBankCode"));//查询机构是否存在
		
//		if(null == dep){//新增
//			Department department = new Department();
//			Department superDep = new Department();
//			superDep.setId((String) head.get("superBankCode"));//上级机构对象
//			
//			department.setId((String) head.get("innerBankCode"));//id
//			department.setInnerBankCode((String) head.get("innerBankCode"));//网点号
//			department.setName((String) head.get("bankName"));//行名
//			department.setBankNumber((String) head.get("bankNumber"));//大额行号
//			department.setOrgCode((String) head.get("orgCode"));//组织机构代码
//			department.setParent(superDep);//上级行号
//			department.setLevel(Integer.parseInt((String) head.get("bankLevel")));//级别
//			department.setIsOrg(1);//机构
//			
//			this.txStore(department);
//			return "机构新增成功";
//		}else{
//			//修改
//			
//			Department superDep = new Department();
//			superDep.setId((String) head.get("superBankCode"));//上级机构对象
//			
//			dep.setId((String) head.get("innerBankCode"));//id
//			dep.setInnerBankCode((String) head.get("innerBankCode"));//网点号
//			dep.setName((String) head.get("bankName"));//行名
//			dep.setBankNumber((String) head.get("bankNumber"));//大额行号
//			dep.setOrgCode((String) head.get("orgcode"));//组织机构代码
//			dep.setParent(superDep);//上级行号
//			dep.setLevel(Integer.parseInt((String) head.get("bankLevel")));//级别
//			dep.setIsOrg(1);//机构
//			
//			this.txStore(dep);
//			return "机构信息修改成功";
//		}
		
		if("0".equals((String)head.get("deleteFlag"))){
			if(null == dep){//新增
			Department department = new Department();
			Department superDep = new Department();
			
			//如果为2级机构，上级为总行"0" 2级以下有接口数据为准 20120619 xingyu add
			if(StringUtils.equals("2", (String) head.get("bankLevel"))){
				superDep.setId("0");
			}else{
				superDep.setId((String) head.get("superBankCode"));//上级机构对象
			}
			
			
			department.setId((String) head.get("innerBankCode"));//id
			department.setInnerBankCode((String) head.get("innerBankCode"));//网点号
			department.setName((String) head.get("bankName"));//行名
			department.setBankNumber((String) head.get("bankNumber"));//大额行号
			department.setOrgCode((String) head.get("orgCode"));//组织机构代码
			department.setParent(superDep);//上级行号
			department.setLevel(Integer.parseInt((String) head.get("bankLevel")));//级别
			department.setIsOrg(1);//机构
			
			this.txStore(department);
			return "机构新增成功";	
			
			}else{
			
			//修改
			
			Department superDep = new Department();
			//如果为2级机构，上级为总行"0" 2级以下有接口数据为准 20120619 xingyu add
			if(StringUtils.equals("2", (String) head.get("bankLevel"))){
				superDep.setId("0");
			}else{
				superDep.setId((String) head.get("superBankCode"));//上级机构对象
			}
			
			dep.setId((String) head.get("innerBankCode"));//id
			dep.setInnerBankCode((String) head.get("innerBankCode"));//网点号
			dep.setName((String) head.get("bankName"));//行名
			dep.setBankNumber((String) head.get("bankNumber"));//大额行号
			dep.setOrgCode((String) head.get("orgCode"));//组织机构代码
			dep.setParent(superDep);//上级行号
			dep.setLevel(Integer.parseInt((String) head.get("bankLevel")));//级别
			dep.setIsOrg(1);//机构
			
			this.txStore(dep);
			return "机构信息修改成功";
			}
		}else{
				
			//根据deleteFlag字段执行者删除  zhangjunshuai 2012-05-04
				Department dept = (Department) load((String) head.get("innerBankCode"));
				
				//if(dept !=null){
				if(null != dept){
					//
					if (null == dept.getParent()) {
						return "根部门不允许被删除！";
					}else if (hasChildren(dept.getId(), -1, -1)) {
						return "该部门下已包含子部门，不允许删除！";
					}else if(isDeptHasUser(dept.getId(), false)){
						return "该部门下已包含用户信息，不允许删除！";
					}else{
						txDelete(dept);
						return "部门删除成功！";
					}
				}else{
					return "该部门不存在！";
				}
			
				
			}
		
		
		
	}

	public boolean isDeptHasUser(String departmentId, boolean isActive)
	throws Exception{
			String query = "";
			long size = 0;
			List paras = new ArrayList();
			paras.add(departmentId);
			if(isActive){
				query = "select user from User as user where user.department = ? and user.status = ?";
				paras.add("1");
				size = this.getRowCount(query, paras).longValue();
			}else{
				query = "select user from User as user where user.department = ?";
				size = this.getRowCount(query, paras).longValue();
			}
			if(size > 0)
				return true;
			return false;
	}

	
	public String getDepartByBranch(String bankNum) throws Exception {
	
		String hql = "select dto from Department dto where dto.bankNumber='"+bankNum+"'";
		
		List dept = this.find(hql);
		if(null != dept && dept.size()>0){
			Department deptOld = (Department)dept.get(0);
			if("Y".equals(deptOld.getIsBranch())){
				return bankNum;
			}else{
				Department deptParent = deptOld.getParent();
				return deptParent.getBankNumber();
			}
		}else{
			return null;
		}
	}
	
	public Department getOrgCodeByBankNumber4RecMsg(String bankNumber) {
		String query = " from Department as department where department.bankNumber = ? and department.status = '1' and department.isOrg='1' and department.mainBraFlg='0' ";
		List paras = new ArrayList();
		paras.add(bankNumber);
		List list = this.find(query, paras);
		if (list != null && list.size() > 0) {
			return (Department) list.get(0);
		}
		return null;
	}
	/**
	 * <p>方法说明:查询机构已分配的资源树</p>
	 * @param user 当前登录用户
	 * @param pid 资源父id
	 * @param sync 是否异步加载 1是 、0否
	 * @throws Exception
	 */
	public List queryAssignBranchAndResourceTree(User user,String pid,String sync)throws Exception{
		StringBuffer hql = new StringBuffer("select rs from Resource rs");
		List paras = new ArrayList();
		//从缓存中获取是否按照机构分配的资源进行权限分配
		String assignRightFlag = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DEPARTMENT_RESOURCE_ASSIGN);
		if("1".equals(assignRightFlag)){//查询机构已分配的资源信息
			hql.append(",DepartmentResource drs where rs.id=drs.resourceId and drs.deptId=?");
			paras.add(user.getDepartment().getId());
		}else{//查询所有资源信息
	    	hql.append(" where 1=1");
	    }
		if("1".equals(sync)){//异步加载
			if("null".equals(pid)){
				String dbType = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DATABASE_TYPE);		
				if(StringUtils.isNotBlank(dbType) && "mysql".equalsIgnoreCase(dbType)){
					hql.append("and (rs.parent.id is null or rs.parent.id = '')");
				}else{
					hql.append("and rs.parent.id is null");
				}
			}else{
				hql.append("and rs.parent.id=?");
				paras.add(pid);
			}
		}
		hql.append(" order by rs.order ASC");
		return  dao.find(hql.toString(), paras);
	}

	/**
	 * <p>方法名称: getHeadBankByMemberCode|描述: 根据票交所会员编码查询该所属总行信息</p>
	 * @param memberCode
	 * @param return Department 总行
	 */
	public Department getHeadBankByMemberCode(String memberCode){
		String query = " from Department as dto where dto.pjsMemberCode =? and dto.level=1 and dto.status=1";
		List paras = new ArrayList();
		paras.add(memberCode);
		List list = this.find(query, paras);
		if (list != null && !list.isEmpty()) {
			return (Department) list.get(0);
		}
		return null;
	}
	/**
	 * 根据机构父id查询当前法人下属机构信息
	 * 如果pid为空，则查询当前法人下属 所有机构信息
	 * @param pid
	 * @param user 当前用户
	 * @param status 状态
	 * @param async 是否异步加载 1是、0否
	 * @return  list 机构信息
	 * @throws Exception
	 */
	public List getAllDepartments(String pid,User user,int status,String async)throws JSONException{
		StringBuffer sb = new StringBuffer("SELECT dept.* from T_DEPARTMENT dept WHERE 1=1");
		List paras = new ArrayList();
		if( null!=user.getDepartment().getPjsMemberCode() && !"-1".equals(user.getDepartment().getPjsMemberCode())){//-1为虚拟机构
			sb.append(" and dept.PJS_MEMBER_CODE= '"+user.getDepartment().getPjsMemberCode()+"' ");
//			paras.add(user.getDepartment().getPjsMemberCode());

		}

		if (StringUtils.isNotBlank(pid)) {
			sb.append(" and dept.D_PID = '"+pid+"' ");
//			paras.add(pid);
		}else{
			if("0".equals(async)){//同步加载
				Department curDept = user.getDepartment();
				sb.append(" and dept.level_Code like '"+curDept.getLevelCode() + "%' ");
//				paras.add( curDept.getLevelCode() + "%");
			}else{//异步加载
				sb.append(" start with dept.id = '"+user.getDepartment().getId()+"' connect by PRIOR dept.id = dept.D_PID ");
//				sb.append(" and dept.parent.id =?");
//				paras.add(user.getDepartment().getId());
			}
		}
		if(status > 0){
			sb.append(" and dept.D_STATUS = '"+new Integer(status)+"' ");
//			paras.add(new Integer(status));
		}
//		sb.append(" order by order");
		return this.dao.queryList(sb.toString(), "dept", Department.class);

//		return this.find(sb.toString(), paras);
	}

	@Override
	public String queryDeptInnerbankcode(String innerCode, int status)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		if(StringUtil.isNotBlank(innerCode)){
			sb.append("select dept.d_innerbankcode from t_department dept");
			sb.append(" start with dept.d_innerbankcode = '"+innerCode+"' ");
			sb.append(" and dept.D_ISORG = '1' ");
			if (status >= 0) {
				sb.append(" and dept.D_STATUS = "+status+" ");
			}
			sb.append(" connect by prior dept.id=dept.d_pid ");
			return sb.toString();
		}
		
		return null;
	}
	
	@Override
	public boolean checkBranch(String marginAccount, String managerId)
			throws Exception {
		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setAccNo(marginAccount);
		transNotes.setCurrentFlag("1");
		ReturnMessageNew response1 = poolCoreService.PJH716040Handler(transNotes, "0");
		if(response1.isTxSuccess()){
			String branch = (String) response1.getBody().get("OPEN_BRANCH");//机构号
			String accBranch = (String) response1.getBody().get("ACCOUNT_BRANCH_ID");//核算机构
			User user = userService.getUserByLoginName(managerId);//校验客户经理
	        logger.info("查询的用户的机构信息："+user.getDeptId()+"............................");
	        Department dept = (Department) this.load(user.getDeptId(),Department.class);
	        if(!dept.getAuditBankCode().equals(accBranch)){
                return false;
	        }
		}
		return true;
	}

	@Override
	public String queryParentBranch(String id) throws Exception {
		String sql = " SELECT d_name FROM T_DEPARTMENT WHERE D_LEVEL in('5','7')" +
				" start with ID = '"+id+"'  connect by ID  = prior D_PID";
		List list = this.find(sql);
		if(list != null && list.size() > 0){
			return (String) list.get(0);
		}
		return null;
	}
}
