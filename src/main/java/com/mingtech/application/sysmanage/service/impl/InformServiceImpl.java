package com.mingtech.application.sysmanage.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Inform;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.InformService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class InformServiceImpl extends GenericServiceImpl implements InformService {
	
	private static final int BUFFER_SIZE = 16 * 1024;
	private static final Logger logger = Logger.getLogger(InformServiceImpl.class);
	
	private DepartmentService departmentService;
	
	/**
	 * 查询当前公告
	 */
	public String viewInformList(String beginDate,String endDate ,String keyStr ,Page page,User user)throws Exception{
		List list = this.getInforList(beginDate ,endDate ,keyStr ,page ,user);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}
	/**根据条件查询公告公告
	 * 
	 * @param beginDate
	 * @param endDate
	 * @param page
	 * @return Exception
	 */
	public List getInforList(String beginDate,String endDate ,String keyStr ,Page page,User user)throws Exception{
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		sb.append("from Inform as t where t.userId = ? ");
		paras.add(user.getId());
		if(beginDate != null && beginDate.trim().length()>0){
			sb.append(" and t.distrubiteDate >= ?");
			paras.add(DateFormat.getDateInstance().parse(beginDate));
		}
		if(endDate != null && endDate.trim().length()>0){
			sb.append(" and t.distrubiteDate <= ?");
			Date tempDate = DateFormat.getDateInstance().parse(endDate);
//			paras.add(DateUtils.modDay(tempDate, 1));
			paras.add(tempDate);
		}
		if(keyStr != null && keyStr.trim().length()>0){
			sb.append(" and ( t.title like '%");
			sb.append(keyStr);
			sb.append("%' ");
			sb.append(" or t.contents like '%");
			sb.append(keyStr);
			sb.append("%' ");
			sb.append(" or t.distributeDepart like '%");
			sb.append(keyStr);
			sb.append("%' ");
			sb.append(" )");
		}
		
		//增加排序功能
		sb.append(" order by t.createTime desc ");
		return find(sb.toString(), paras, page);
	}

	/**
	 * 保存上传附件
	 * @param inform
	 * @param file
	 */
	public String uploadAttach(Inform inform ,File file ,String fileName)throws Exception{
		String filePath = this.upload(file, fileName, inform.getId());
		return filePath;
	}

	/**
	* <p>方法名称: upload|描述: 上传公告附件</p>
	* @param src 源文件
	* @param fileName 需要保存的文件名
	* @param id 业务记录主键ID
	* @return
	* @throws Exception
	*/
	private String upload(File src, String fileName,String id) throws Exception{
		String rootpath = ProjectConfig.getInstance().getAttachPath(); // 获取基础目录
		String filepath = rootpath + File.separator
				+ DateUtils.toString(new Date(), DateUtils.ORA_DATES_FORMAT)
				+ File.separator + id; // 拼装新的目录
		File file = new File(filepath);
		if(file.exists()){
			File[] fileArray = file.listFiles();
			if(fileArray != null && fileArray.length > 0){
				for(int i = 0; i < fileArray.length; i++){
					File tmp = fileArray[i];
					tmp.delete();
				}
			}
		}else{
			file.mkdirs();
		}
		InputStream in = null;
		OutputStream out = null;
		try{
			in = new BufferedInputStream(new FileInputStream(src),
					BUFFER_SIZE);
			file = new File(filepath + File.separator + fileName);
			out = new BufferedOutputStream(new FileOutputStream(file),
					BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			while(in.read(buffer) > 0){
				out.write(buffer);
			}
		}catch (Exception e){
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(null != in){
				in.close();
			}
			if(null != out){
				out.close();
			}
		}
		return file.getAbsolutePath();
		
	}
	
	/**
	 * 查询用户主页显示出的公告
	 * @param deptId
	 * @return
	 */
	public List getUserInformInMainPage(String deptId){
		try{
			StringBuffer sb = new StringBuffer();
			List parasValue = new ArrayList();//查询条件值
	 		List parasName = new ArrayList();//查询条件名

			sb.append("from Inform as t where t.status = '01'  and t.showLevel > '0' ");
			//查询其上级和相同机构下的公告
			/*sb.append(" and t.userDept in (:deptIds) ");
			parasName.add("deptIds");
			List deptIds = this.getDeptAllParent(deptId);
			logger.info("当前父节点数："+deptIds.size());
	  		parasValue.add(deptIds);*/
	  		
	  		//增加有效期的过滤
	  		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String endDt = dateFormat.format(new Date());   //判断是否超过摊销截止日
	  		sb.append("and (t.endDate is null or t.endDate>=TO_DATE ('"+endDt+"', 'yyyy-mm-dd'))");
	  		
	  		//增加排序功能
			sb.append(" order by t.showLevel desc , t.createTime desc ");
			
			String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
			Object[] parameters = parasValue.toArray(); //查询条件值
			List list = this.find(sb.toString(), nameForSetVar, parameters, null);
			
			return list;
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return null;
		}
		
	}
	/**
	 * 获取该部门所有父节点
	 * @param deptId
	 * @return
	 */
	private List getDeptAllParent(String deptId)throws Exception{
		List list = new ArrayList();
		Department dept = (Department)departmentService.load(deptId);
		list.add(dept.getId());
		while(null !=dept.getParent()){
			list.add(dept.getParent().getId());
			dept = dept.getParent();
		}
		return list;
	}
	
	/**
	 * 根据ID删除对象
	 */
	public void txDeleInformByIds(String ids) throws Exception{
		List list = this.getInformByids(ids, null);
		for(int i=0 ;i<list.size() ;i++){
			Inform inform = (Inform)list.get(i);
			dao.delete(inform);
		}
	}
	
	/**
	 * 根据ID获取公告
	 * @param ids
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List getInformByids(String ids,Page page) throws Exception{
		List informIds = StringUtil.splitList(ids, ",");
		String hql = "from Inform as inform where inform.id in (:informIds)";
		String[] parasName = new String[] {"informIds"};
		Object[] parasValue = new Object[] {informIds};
		
		List list = null;
		if(null==page) list = this.find(hql, parasName, parasValue);
		else list = this.find(hql, parasName, parasValue, page);
		return list;
	}
	
	/**
	 * 查询所有公告
	 * @param deptId
	 * @return
	 */
	public List getUserInformList(String beginDate,String endDate,String keyStr,Page page,String deptId)throws Exception{
		
		StringBuffer sb = new StringBuffer();
		List parasValue = new ArrayList();//查询条件值
 		List parasName = new ArrayList();//查询条件名

		sb.append("from Inform as t where t.status = '01' ");
		//查询其上级和相同机构下的公告
		sb.append(" and t.userDept in (:deptIds) ");
		
		List deptIds = this.getDeptAllParent(deptId);
		parasName.add("deptIds");
  		parasValue.add(deptIds);
  		
  		if(beginDate != null && beginDate.trim().length()>0){
			sb.append(" and t.distrubiteDate >= :beginDate");
			parasName.add("beginDate");
			Date tempDate = DateFormat.getDateInstance().parse(beginDate);
			parasValue.add(tempDate);
		}
		if(endDate != null && endDate.trim().length()>0){
			sb.append(" and t.distrubiteDate <= :endDate");
			parasName.add("endDate");
			Date tempDate = DateFormat.getDateInstance().parse(endDate);
			parasValue.add(DateUtils.modDay(tempDate, 1));
		}
		if(keyStr != null && keyStr.trim().length()>0){
			sb.append(" and ( t.title like '%");
			sb.append(keyStr);
			sb.append("%' ");
			sb.append(" or t.contents like '%");
			sb.append(keyStr);
			sb.append("%' ");
			sb.append(" or t.distributeDepart like '%");
			sb.append(keyStr);
			sb.append("%' ");
			sb.append(" )");
		 }
		
		//增加排序功能
		sb.append(" order by t.showLevel desc , t.createTime desc ");
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(sb.toString(), nameForSetVar, parameters, page);
		
		return list;
	}
	
	/**
	 * 获取当前最高显示级别
	 * @return
	 */
	public int getCurLevel(){
		
		String sql = "select max(inform.showLevel) from Inform as inform ";
		List list = this.find(sql);
		Integer level = (Integer)list.get(0);
		return level.intValue();
	}
	/**
	 * 获取当前放置首页的公告
	 * @return
	 */
	public List getTopList(){
		String sql = "from Inform as inform where inform.showLevel != 0";
		return this.find(sql);
	}
	/**
	 * 根据ID获取公告
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Inform loadInformByPrimaryKey(String id) throws Exception{
		Inform inform = (Inform)dao.load(Inform.class, id);
		return inform;
	}
	
	/**
	 * 保存对象
	 */
	public void txStoreDto(Object object)throws Exception{
		this.txStore(object);
	}
	/**
	 * 删除对象
	 */
	public void txDeleDto(Object object)throws Exception{
		this.txDelete(object);
	}
	
	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	
	/**
	 * 更新对象
	 */
	public void txUpdateDto(Object object)throws Exception{
		this.txStore(object);
	}
	
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

}
