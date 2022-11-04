package com.mingtech.application.audit.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.framework.adapter.Response;
import com.mingtech.framework.common.util.MvelUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.audit.domain.AuditNodeDto;
import com.mingtech.application.audit.service.AuditBusiTableConfigService;
import com.mingtech.application.audit.service.AuditExtendService;
import com.mingtech.application.audit.service.AuditExtendServiceFactory;
import com.mingtech.application.audit.service.AuditRouteService;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.domain.ApproveDto;
import com.mingtech.application.audit.domain.ApproveParamterDto;
import com.mingtech.application.audit.domain.AuditNodeBean;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.audit.domain.AuditRouteDto;
import com.mingtech.application.audit.domain.AuditRouteParamterDto;
import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.common.domain.GuarantDiscMapping;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.discount.domain.TxRateMaintainInfo;
import com.mingtech.application.pool.discount.service.TxRateMaintainInfoService;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.adapter.InnerHttpAdapter;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: ice
* @日期: 2019-07-15 上午10:28:06
* @描述: [AuditServiceImpl]审批接口实现
*/
@Service("auditService")
public class AuditServiceImpl extends GenericServiceImpl implements AuditService{
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private DepartmentService departmentService;//机构管理接口
	@Autowired
	private AuditRouteService auditRouteService;//审核路线管理接口
	@Autowired
	private AuditBusiTableConfigService auditBusiTableConfigService;
	//审批扩展服务工厂
	private AuditExtendServiceFactory auditExtendServiceFactory;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private TxRateMaintainInfoService txRateMaintainInfoService;
	/**
	 * 【第一步 提交审批流】
	 * @param user 当前系统登录用户
	 * @param nodeNum 当前要提交的审批节点 默认为 1 第一岗，如有特殊需要 请传入岗位顺序号
	 * @param approveAudit 审批受理参数对象-所需参数说明如下：
	 * 一、必输参数说明：
	 * 1、auditType 审批类型 【如果为空 则为01 批次审批】参考 PublicStaticDefineTab AUDIT_TYPE_
	 * 2、busiType  业务类型【可以为空】 参考PublicStaticDefineTab AUDIT_BUSI_TYPE*****
	 * 3、busiId  业务清单id 
	 * 4、productId 产品号
	 * 5、applyNo 业务申请号(可以为批次号、票据明细、自定义编号)
	 * 二、当auditType=01批次、02票据明细时，以下参数为必输项
	 * 6、custName 客户(交易对手)名称
	 * 7、custCertNo 客户(交易对手)证件号码
	 * 8、custBankNm 客户(交易对手)开户行名称
	 * 9、auditAmt 审批金额
	 * 10、billType 票据类型
	 * 11、billMedia 票据介质
	 * 12、billTotalNum; 票据总笔数
	 * @param mvelDataMap 存放mvel表达式中使用的动态变量-常用参数存放Map的key值和数据类型说明如下:
	 * 1、金额：amount 类型：BigDecimal
	 * 2、业务笔数：totalNum 类型：int
	 * 
	 * <p>20201016 审批优化 增加提交审批时可选择审批人</p>
	 * @param approverUserId 已选择的审批人id
	 * 
	 * @return AuditResultDto retCode 00成功；01 未找到审批路线配置、02审批金额过大 ，所有审批节点 都没有权限 、03未找到审批人员、04请求参数检测失败
	 */
	public AuditResultDto txCommitApplyAudit(User user,String nodeNum,ApproveAuditBean approveAudit,Map mvelDataMap)throws Exception{
		logger.info("进入提交审批方法");
		AuditResultDto retDto = new AuditResultDto();
		retDto.setIfSuccess(false);
		
		//必输参数检查
		AuditResultDto retDtoParaChk = this.chkSubmitAuditParams(approveAudit);
		if(!retDtoParaChk.isIfSuccess()){
			retDtoParaChk.setRetCode("04");
			return retDtoParaChk;
		}
		//当前要提交的审批节点 
		if(StringUtils.isEmpty(nodeNum)){
			nodeNum="1";
		}
		String productId = approveAudit.getProductId();//产品ID
		
		//调用第三方
		if(null != mvelDataMap && StringUtils.isNotBlank((String)mvelDataMap.get(PublicStaticDefineTab.TX_CODE))){
			InnerHttpAdapter innerHttpAdapter = new InnerHttpAdapter();
			Response response = innerHttpAdapter.sendMsgToDSSB((String) mvelDataMap.get(PublicStaticDefineTab.TX_CODE), approveAudit.getBusiId(), approveAudit.getApplyNo(),
					approveAudit.getAuditAmt(), approveAudit.getTotalNum(), approveAudit.getBillType(), approveAudit.getBillMedia(),
					approveAudit.getProductId(), user, null);
			//如果调用第三方
			if (response.isSendToThird()){
				if (PublicStaticDefineTab.DSSB_RSPCODE_SUCCESS.equals(response.getCode())){
					retDto.setReqNo(response.getFlowNo());//将请求流水号放到审批返回对象中
					retDto.setRetCode(PublicStaticDefineTab.AUDIT_SUCCESS_CODE_00);
					retDto.setIfSuccess(true);
					retDto.setRetMsg("处理成功");
				}else{
					retDto.setReqNo(response.getFlowNo());//将请求流水号放到审批返回对象中
					retDto.setRetCode(PublicStaticDefineTab.AUDIT_FAIL_CODE_05);
					retDto.setIfSuccess(false);
					retDto.setRetMsg(response.getDesc());
				}
				return retDto;
			}
		}
		//【0】查询审核路线
		logger.info("查询审核路线：入参：机构id："+user.getDepartment().getId()+"、产品productId："+productId);
		AuditRouteDto routeDto = getAuditRouteByBrchIdAndProdId(user.getDepartment().getId(),productId);
		if(routeDto==null){
			//没有找到审批配置
			retDto.setRetCode(PublicStaticDefineTab.AUDIT_FAIL_CODE_01);
			retDto.setRetMsg("该机构未分配审核路线，请与管理员联系。");
			return  retDto;
		}
		//【1】查询初审审核节点
		logger.info("【1】查询初审审核节点：入参：审批表主键id："+routeDto.getRouteId()+"、当前要提交的审批节点："+nodeNum);
		AuditNodeDto node = this.getAuditNodeByRouteId(routeDto.getRouteId(), nodeNum);
		if(node==null){
			//没有找到审批配置
			retDto.setRetCode(PublicStaticDefineTab.AUDIT_FAIL_CODE_01);
			retDto.setRetMsg("未找到审批节点信息，节点编号为【"+nodeNum+"】");
			return  retDto;
		}
		//查询审批路线所需参数
		logger.info("查询审批路线所需参数：入参：审批路线id："+node.getRouteId());
		List routeParaList = auditRouteService.queryRouteParamtersByRouterId(node.getRouteId(),null);
		int paraCount = routeParaList.size();
		if(paraCount > 0 && mvelDataMap == null){
			retDto.setRetCode(PublicStaticDefineTab.AUDIT_FAIL_CODE_01);
			retDto.setRetMsg("审批路线已经配置了请求参数，提交审批时没有传入。");
			return  retDto;
		}
		//审批受理请求参数
		List<ApproveParamterDto> approveList = new ArrayList<ApproveParamterDto>();
		for(int i=0; i < paraCount; i++){
			AuditRouteParamterDto routeParam = (AuditRouteParamterDto)routeParaList.get(i);
			if(!mvelDataMap.containsKey(routeParam.getParamCode())){
				retDto.setRetCode(PublicStaticDefineTab.AUDIT_FAIL_CODE_01);
				retDto.setRetMsg("审批路线已经配置了请求参数【"+routeParam.getParamCode()+routeParam.getParamName()+"】，提交审批时没有传入。");
				return  retDto;
			}
			ApproveParamterDto approveParam = new ApproveParamterDto();
			approveParam.setParamCode(routeParam.getParamCode());
			approveParam.setParamName(routeParam.getParamName());
			approveParam.setParamValue(String.valueOf(mvelDataMap.get(routeParam.getParamCode())));
			approveParam.setDataType(routeParam.getDataType());
			approveList.add(approveParam);
			try {
				//数据转换
				if("String".equalsIgnoreCase(routeParam.getDataType())) {
					mvelDataMap.put(routeParam.getParamCode(), String.valueOf(mvelDataMap.get(routeParam.getParamCode())));
				}else if("int".equalsIgnoreCase(routeParam.getDataType())) {
					mvelDataMap.put(routeParam.getParamCode(), Integer.valueOf(String.valueOf(mvelDataMap.get(routeParam.getParamCode()))).intValue());
				}else if("BigDecimal".equalsIgnoreCase(routeParam.getDataType())) {
					mvelDataMap.put(routeParam.getParamCode(), new BigDecimal(String.valueOf(mvelDataMap.get(routeParam.getParamCode()))));
				}
			}catch(Exception e){
				logger.error("审批路线请求参数数据类型转换错误",e);
				throw new Exception("审批路线请求参数数据类型转换错误！!");
			}
		}
		
		if("1".equals(node.getNodeType())){//自动节点
			//根据MVEL表达式计算下一节点
			String nextNodeNo = null;
			try{
				Object objNextNodeNo =(Object) MvelUtil.eval(node.getMvelExpr(),mvelDataMap);
				nextNodeNo = String.valueOf(objNextNodeNo);
			}catch(Exception e1){
				logger.error("当前审批节点中MVEL动态表达式设定有误",e1);
				throw new Exception("当前审批节点中动态表达式规则设定不正确，请联系管理员!");
			}
			if("-1".equals(nextNodeNo.trim())){//当前审批节点权限内直接结束
				throw new Exception("未找到人工审核节点，请联系管理员!");
			}
			//查找下一个审批节点
			node  = getAllNodesByRouteIdAndNodeNo(node.getRouteId(),nextNodeNo);
			if(node == null){
					throw new Exception("未找到人工审核节点，请联系管理员!");
			}
		}
				
		//【2】查询符合条件的 审批柜员 信息
		logger.info("【2】查询符合条件的 审批柜员 信息：入参：提交人归属机构："+user.getDepartment().getId()+"、执行角色："+node.getOptRole()+"审批级别："+node.getNodeLevel());
		List userList = this.getAuditUsersOfNextRoute(user.getDepartment(),node.getOptRole(),node.getNodeLevel());
		if (userList==null || userList.isEmpty()) {
			//未找到审批人员
			retDto.setRetCode(PublicStaticDefineTab.AUDIT_FAIL_CODE_NO_USERS_03);
			retDto.setRetMsg("未找到审批人员.");
			return  retDto;
		}
		StringBuffer curAuditUserLoginNo=new StringBuffer("");
		StringBuffer curAuditUserNm=new StringBuffer("");
		//提交审批时 是否可选择相应审批人信息
		logger.info("提交审批时 是否可选择相应审批人信息：入参：productId："+productId);
		List busiTabConfList = auditBusiTableConfigService.queryBusiTabConfigsByProductId(productId);
		String approverUserId = approveAudit.getApproverUserId(); //提交审批时选择的第一岗审批人id
		logger.info("提交审批时选择的第一岗审批人id："+approverUserId);
		for(int j=0;j<userList.size();j++){
			User  dto = (User)userList.get(j);
			//若该开关开启且提交审批时已选择相应审批人
			if(busiTabConfList != null && busiTabConfList.size() > 0){
				BusiTableConfig busiTableConfig = (BusiTableConfig) busiTabConfList.get(0);
				if("true".equals(busiTableConfig.getSubmitNode()) && StringUtils.isNotBlank(approverUserId) && !"1".equals(node.getNodeType())){
					if(dto.getId().equals(approverUserId)){
						curAuditUserLoginNo.append(dto.getLoginName()).append(",");
						curAuditUserNm.append(dto.getName()).append(",");
						break;
				     }
				}else{
					curAuditUserLoginNo.append(dto.getLoginName()).append(",");
					curAuditUserNm.append(dto.getName()).append(",");
				}
			}	
		}
		logger.info("审批人工号："+curAuditUserLoginNo.toString()+"、审批人名称：productId："+curAuditUserNm.toString());
		
		logger.info("查询审批流信息：入参：productId："+productId+"、业务id:"+approveAudit.getBusiId());
		ApproveAuditDto alAudit=this.getOpenApproveAudit(productId, approveAudit.getBusiId());
		if(alAudit!=null){
			if(null != mvelDataMap){
				String cirAuditFlag = (String) mvelDataMap.get("cirAuditFlag"); 
				if(!"true".equals(cirAuditFlag)){
					throw new Exception("该业务已经存在打开的审批流程，不允许再次提交审批流程，请联系系统管理员");
				}else{
					String hisId = (String) mvelDataMap.get("HIS_ID"); 
					alAudit.setBusiId(hisId);
					this.txStore(alAudit);
				}
			}
		}
		//【3】生成审批流程信息 
		Date workDate = DateUtils.getWorkDateTime();
		ApproveAuditDto  appAudit = new ApproveAuditDto();
		appAudit.setAuditType(approveAudit.getAuditType());
		appAudit.setBusiType(approveAudit.getBusiType());
		appAudit.setBusiId(approveAudit.getBusiId());//原业务ID
		appAudit.setProductId(approveAudit.getProductId());//产品编码-例如：1001买断式贴现
		appAudit.setApplyNo(approveAudit.getApplyNo());//业务申请号(可以为批次号、票据明细、自定义编号)
		
		appAudit.setCustName(approveAudit.getCustName());//客户(交易对手)名称
		appAudit.setCustCertNo(approveAudit.getCustCertNo());//客户(交易对手)证件号码
		appAudit.setCustBankNm(approveAudit.getCustBankNm());//客户(交易对手)开户行名称
		appAudit.setAuditAmt(approveAudit.getAuditAmt());//审批金额
		appAudit.setBillType(approveAudit.getBillType());//票据类型
		appAudit.setBillMedia(approveAudit.getBillMedia());//票据介质
		appAudit.setTotalNum(approveAudit.getTotalNum());//票据总笔数
		appAudit.setOpenDate(new Date());//启动日期
		appAudit.setOpenUserId(user.getId());//启动审批流人员ID
		appAudit.setOpenUserNm(user.getName());//启动用户名称
		appAudit.setOpenBranchId(user.getDepartment().getId());//启动机构ID
		appAudit.setOpenBranchNm(user.getDepartment().getName());//启动机构名称
		appAudit.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);//审批状态-1提交审批
		appAudit.setRouteId(node.getRouteId());//审核路线ID
		appAudit.setCurAuditNodeId(node.getNodeId());//当前审批节点ID
		if(curAuditUserNm.length() > 0){//
			appAudit.setCurAuditUser(curAuditUserLoginNo.toString());//当前审批人账号
			appAudit.setCurAuditUserNm(curAuditUserNm.substring(0, curAuditUserNm.length()-1));//当前审批人名称
		}
		//当前节点审批人角色
		if(StringUtils.isNotBlank(node.getOptRole())){
			Role role = (Role)this.load(node.getOptRole(), Role.class);
					appAudit.setCurRoleName(role.getName());
		}
		User  curAuditUser = (User)userList.get(0);
		Department curAuditDept = (Department)this.load(curAuditUser.getDeptId(), Department.class);
		appAudit.setCurAuditBranchId(curAuditDept.getId());//当前审批人所属机构ID
		appAudit.setCurAuditBranchNm(curAuditDept.getName());//当前审批人所属机构名称
		appAudit.setLastUpdate(new Date());//最后更新时间
		if(StringUtils.isNotBlank(approveAudit.getInnerBranchId())){
			appAudit.setInnerBranchId(approveAudit.getInnerBranchId());
		}
		this.txStore(appAudit);
		//保存审批受理参数
		if(paraCount > 0){
			//审批受理参数设定受理申请id
			for(ApproveParamterDto approveParam : approveList){
				approveParam.setApproveAuditId(appAudit.getId());
			}
			this.txStoreAll(approveList);
		}
		retDto.setRetCode(PublicStaticDefineTab.AUDIT_SUCCESS_CODE_00);
		retDto.setIfSuccess(true);
		retDto.setRetMsg("处理成功");
		return  retDto;
	}
	//提交审批请求参数检测
	/**
	 *提交审批请求参数检测
	 * @param approveAudit 审批受理参数对象-所需参数说明如下：
	 * 一、必输参数说明：
	 * 1、auditType 审批类型 【如果为空 则为01 批次审批】参考 PublicStaticDefineTab AUDIT_TYPE_
	 * 2、busiType  业务类型【可以为空】 参考PublicStaticDefineTab AUDIT_BUSI_TYPE*****
	 * 3、busiId  业务清单id 
	 * 4、productId 产品号
	 * 5、applyNo 业务申请号(可以为批次号、票据明细、自定义编号)
	 * 二、当auditType=01批次、02票据明细时，以下参数为必输项
	 * 6、custName 客户(交易对手)名称
	 * 7、custCertNo 客户(交易对手)证件号码
	 * 8、custBankNm 客户(交易对手)开户行名称
	 * 9、auditAmt 审批金额
	 * 10、billType 票据类型
	 * 11、billMedia 票据介质
	 * 12、billTotalNum; 票据总笔数
	 * @return AuditResultDto.ifSuccess true通过、false不通过
	 */
	private AuditResultDto chkSubmitAuditParams(ApproveAuditBean approveAudit){
		AuditResultDto retDto = new AuditResultDto();
		retDto.setRetCode(PublicStaticDefineTab.AUDIT_FAIL_CODE_04);
		retDto.setIfSuccess(false);
		if(approveAudit == null){
			retDto.setRetMsg("未设定审批请求参数。");
			return  retDto;
		}
		if(StringUtils.isBlank(approveAudit.getAuditType())){
			retDto.setRetMsg("审批类型不能为空。");
			return  retDto;
		}
		if(StringUtils.isBlank(approveAudit.getBusiType())){
			retDto.setRetMsg("业务类型不能为空。");
			return  retDto;
		}
		if(StringUtils.isBlank(approveAudit.getBusiId())){
			retDto.setRetMsg("原业务申请明细id不能为空。");
			return  retDto;
		}
		if(StringUtils.isBlank(approveAudit.getProductId())){
			retDto.setRetMsg("原业务所属产品编号不能为空。");
			return  retDto;
		}
		if(StringUtils.isBlank(approveAudit.getApplyNo())){
			retDto.setRetMsg("业务申请号不能为空。");
			return  retDto;
		}
		if(PublicStaticDefineTab.AUDIT_TYPE_BATCH.equals(approveAudit.getAuditType())//批次
				|| PublicStaticDefineTab.AUDIT_TYPE_BILLS.equals(approveAudit.getAuditType())){//票据明细
			/*if(PjsQutoValues.PROD_8001.equals(approveAudit.getProductId())
			   || PjsQutoValues.PROD_8002.equals(approveAudit.getProductId())
			   || PjsQutoValues.PROD_8015.equals(approveAudit.getProductId())){
				if(StringUtils.isBlank(approveAudit.getCustName())){
					retDto.setRetMsg("交易对手名称不能为空。");
					return  retDto;
				}
				if(StringUtils.isBlank(approveAudit.getCustCertNo())){
					retDto.setRetMsg("交易对手组织机构代码不为空。");
					return  retDto;
				}
				if(StringUtils.isBlank(approveAudit.getCustBankNm())){
					retDto.setRetMsg("交易对手开户行名称不能为空。");
					return  retDto;
				}
				if(approveAudit.getTotalNum() == 0){
					retDto.setRetMsg("票据笔数不能为0。");
					return  retDto;
				}
			}*/
			
//			if(approveAudit.getAuditAmt() == null){
//				retDto.setRetMsg("审批金额不能为空。");
//				return  retDto;
//			}
//			if(StringUtils.isBlank(approveAudit.getBillType())){
//				retDto.setRetMsg("票据类型不能为空。");
//				return  retDto;
//			}
//			if(StringUtils.isBlank(approveAudit.getBillMedia())){
//				retDto.setRetMsg("票据介质不能为空。");
//				return  retDto;
//			}
			
		}
		retDto.setRetCode(PublicStaticDefineTab.AUDIT_SUCCESS_CODE_00);
		retDto.setIfSuccess(true);
		return retDto;
	}
	
	/**
	 * 查询 打开的 审批流程 对象
	 * @param productId 产品号
	 * @param busiId  业务ID
	 * @return
	 */
	public ApproveAuditDto getOpenApproveAudit(String productId,String busiId){
		List parasNameList = new ArrayList();
		List parasValueList = new ArrayList();
		parasNameList.add("productId");
		parasValueList.add(productId);
		parasNameList.add("busiId");
		parasValueList.add(busiId);
		String hql ="from  ApproveAuditDto as info ";
		hql+=" where info.busiId =:busiId and info.productId=:productId and info.auditStatus in (:status)";
		//状态
		parasNameList.add("status");
		List status = new ArrayList();
		status.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);//提交审批
		status.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING);//审批中
		status.add(PublicStaticDefineTab.AUDIT_STATUS_PASS);//通过
		parasValueList.add(status);
		
		ApproveAuditDto app = null;
		List list = this.find(hql.toString(), (String[]) parasNameList.toArray(new String[parasNameList.size()]), parasValueList.toArray());
		if(list!=null && !list.isEmpty()){
			app = (ApproveAuditDto)list.get(0);
		}
		return app;
	}
	
	/**
	 * 通过 审批模板ID+审批节点号  查询 审批节点信息
	 * @param routeId 审批模板ID
	 * @param nodeNum 审批节点号
	 * @return
	 * @throws Exception
	 */
	public AuditNodeDto getAllNodesByRouteIdAndNodeNo(String routeId,String nodeNum) throws Exception {
		String hql="from AuditNodeDto node where node.routeId=? and node.nodeNum=? ";

		List params=new ArrayList();
		params.add(routeId);
		params.add(nodeNum);
		
		List list=this.find(hql, params);
		if(list!=null && !list.isEmpty()){
			return (AuditNodeDto)list.get(0);
		}
		return null;
	}
	/**
	 *通过机构+角色+当前审批节点级别   查询所有符合条件的 用户信息  
	 *@param dept 当前机构
	 * @param roleIds 角色ID列表  -多个角色ID用逗号隔开
	 * @param nodeLevel 审核岗位级别 0-本机构、1-本级+上级、2-本级+上级+上级、3总行
	 */
	public List getAuditUsersOfNextRoute(Department dept,String roleIds,String nodeLevel) throws Exception{
		logger.info("通过机构+角色+当前审批节点级别   查询所有符合条件的 用户信息 -----开始 ");
		List branchIdList = new ArrayList();
		if("0".equals(nodeLevel)){//本级
			branchIdList.add(dept.getId());
		}else if("1".equals(nodeLevel)){//本级+上级
			branchIdList.add(dept.getId());
			if(StringUtils.isNotEmpty(dept.getParent().getId())){
				branchIdList.add(dept.getParent().getId());
			}
		}else if("2".equals(nodeLevel)){//本级+上级+上级
			logger.info("本级+上级+上级类型查询机构信息：入参："+dept.getParent().getId());
			branchIdList.add(dept.getId());
			if(StringUtils.isNotEmpty(dept.getParent().getId())){
				branchIdList.add(dept.getParent().getId());
				Department deptFather = departmentService.getDeptById(dept.getParent().getId());
				if(deptFather.getParent()!=null&& StringUtils.isNotEmpty(deptFather.getParent().getId())){
					//当前机构的 父机构  的 父机构ID
					branchIdList.add(deptFather.getParent().getId());
				}
				logger.info("本级+上级+上级类型查询机构信息：获取的机构id："+branchIdList);
			}
		}else if("3".equals(nodeLevel)){//总行审批
			logger.info("总行审批类型查询机构信息：入参："+dept.getPjsMemberCode());
			//根据票交所会员编码获取对应总行机构信息
			Department headDept = departmentService.getHeadBankByMemberCode(dept.getPjsMemberCode());
			branchIdList.add(headDept.getId());
			logger.info("总行审批类型查询机构信息：获取的机构id："+branchIdList);
		}
		StringBuffer hql = new StringBuffer("select user from User as user  left join user.roleList role where");
		List parasName = new ArrayList();
		List parasValue = new ArrayList();
		if(branchIdList.size() > 1){
			hql.append(" user.deptId in(:brachId)");
			parasName.add("brachId");
			parasValue.add(branchIdList);//下一岗审核人员所在机构
		}else{
			hql.append(" user.deptId =:brachId ");
			parasName.add("brachId");
			parasValue.add(branchIdList.get(0));//下一岗审核人员所在机构
		}
		String[] arrRoleId = roleIds.split(",");
		logger.info("审批角色id："+arrRoleId);
		if(arrRoleId.length > 1){
			hql.append(" and role.id in(:roleIds)");
			parasName.add("roleIds");
			parasValue.add(Arrays.asList(arrRoleId));//审核人角色
		}else{
			hql.append(" and role.id =:roleId");
			parasName.add("roleId");
			parasValue.add(arrRoleId[0]);//审核人角色
		
		}
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		
		logger.info("/获取任务移交用户-----------------开始：");
		//获取任务移交用户
		parasName = new ArrayList();
		parasValue = new ArrayList();
		StringBuffer sb =  new StringBuffer("select user from User as user ");
		sb.append(" where user.id in ( ");
		sb.append(" select tr.transferedUid from TaskTransfer as tr, TaskTransferRole as trr where tr.id = trr.transferId ");
		/*sb.append(" and tr.transferStatus =:TransferStatus ");
		parasName.add("TransferStatus");
		parasValue.add(TaskTransfer.STATU_EFFECT);//生效*/
		sb.append(" and tr.transferStartTime<=:StartTime ");
		parasName.add("StartTime");
		parasValue.add(DateUtils.getWorkDateTime());//
		sb.append(" and tr.transferStopTime>=:StopTime ");
		parasName.add("StopTime");
		parasValue.add(DateUtils.getWorkDateTime());//
		if(branchIdList.size() > 1){
			sb.append(" and tr.transferedUdeptId in(:UdeptId) ");
			parasName.add("UdeptId");
			parasValue.add(branchIdList);//
		}else{
			sb.append(" and tr.transferedUdeptId =:UdeptId ");
			parasName.add("UdeptId");
			parasValue.add(branchIdList.get(0));//下一岗审核人员所在机构
		}
		if(arrRoleId.length > 1){
			sb.append(" and trr.transferedRoleId in(:roleIds)");
			parasName.add("roleIds");
			parasValue.add(Arrays.asList(arrRoleId));//审核人角色
		}else{
			sb.append(" and trr.transferedRoleId  =:roleId");
			parasName.add("roleId");
			parasValue.add(arrRoleId[0]);//审核人角色
		
		}
		sb.append(" ) ");
		String [] nameForTask = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] paraTask = parasValue.toArray(); //查询条件值
		List listTask = this.find(sb.toString(), nameForTask, paraTask);
		logger.info("获取任务移交用户查询入参："+nameForTask);
		if(null!= listTask&& !listTask.isEmpty()){
			list.addAll(listTask);
		}
		logger.info("通过机构+角色+当前审批节点级别   查询所有符合条件的 用户信息 -----结束 ");
		return list;
	}
	
	/**
     *根据就ID、产品ID查询审核路线
     *@param brchId 机构信息
     *@param prodId 产品id
     */
	public AuditRouteDto getAuditRouteByBrchIdAndProdId(String brchId, String prodId) throws Exception {
		logger.info("查询审核路线:机构brchId="+brchId+",产品prodId="+prodId);
		String hql="select route from RouteBrchProd rbp,AuditRouteDto route where" +
							" rbp.routeId=route.routeId  " +
									" and rbp.brchId=:brchId and rbp.prodId=:prodId";
		List list=this.find(hql,new String[]{"brchId","prodId"},new Object[]{brchId,prodId},new Page());
		if(!list.isEmpty())
			return (AuditRouteDto) list.get(0);
		return null;
	}

    /**
     *根据审核路线ID和审批节点编号查询审核节点信息
     *@param routeId 审核路线id
     *@param nodeNum 审核节点编号
     */
	public AuditNodeDto getAuditNodeByRouteId(String routeId,String nodeNum) throws Exception {
		logger.info("查询审核路线条件:审核路线roteId="+routeId+",开始节点nodeNum="+nodeNum);
		String hql="select node from AuditNodeDto node,AuditRouteDto route where" +
							"  node.routeId=route.routeId and node.nodeNum=:nodeNum" +
									" and route.routeId=:routeId";
		List list=this.find(hql,new String[]{"nodeNum","routeId"},new Object[]{nodeNum,routeId},new Page());
		if(!list.isEmpty())
			return (AuditNodeDto) list.get(0);
		return null;
	}
	/**
	 * 根据产品号、机构ID 查询 所有审批节点
	 * @param brchId 机构号
	 * @param prodId 产品号
	 * @return
	 * @throws Exception
	 */
	public List getAuditNodeByBrchIdAndProdId(String brchId, String prodId) throws Exception {
		logger.info("查询审核路线条件:机构号---"+brchId+",产品ID---"+prodId);
		String hql="select node from RouteBrchProd rbp,AuditRouteDto route,AuditNodeDto node where" +
							" rbp.routeId=route.routeId and node.routeId=route.routeId " +
									" and rbp.brchId=:brchId and rbp.prodId=:prodId order by node.nodeNum asc ";
		return this.find(hql,new String[]{"brchId","prodId"},new Object[]{brchId,prodId},new Page());
	}
	
	
	/**
	 * 根据MVEL动态表达式查询 下一岗 审批 信息  【审批岗，点击审批按钮时查询 下一岗审批人 显示到审批页面】
	 * 1、如果  当前审批节点，配置了下一岗审批节点，但是找不到下一岗节点，会直接报错；
	 * 2、如果  有下一可用的审批节点，但是因为 金额不足，会直接提示 金额过大 ，所有人都无权审批；
	 * 3、其他如果是最后一个审批节点 则返回NULL
	 * 4、如果有下一节点，但是节点配置的角色下面 没有柜员，会直接报错；
	 * 5、其他正常情况下 会返回 带 有权人的 对象ApproveAuditDto---nextAuditUser 
	 * @param curUser  当前登录用户
	 * @param srcBusiId 原业务申请ID：批次/清单ID
	 * @param productId 产品号
	 * @return 
	 * @throws Exception
	 */
	public ApproveAuditBean queryNextAuditInfo(User curUser,String srcBusiId,String productId)throws Exception{
		ApproveAuditDto appAudit = getOpenApproveAudit(productId,srcBusiId);
		if(appAudit == null){
			throw new Exception("审批流程未找到，请确认该笔流程是否已结束!");
		}
		//查找审批受理参数
		List approveParamList = queryApproveParamtersByApproveAuditId(appAudit.getId(),null);
		int paramCount = approveParamList.size();
		Map mvelDataMap = new HashMap();
		for(int i=0; i < paramCount; i++){
			ApproveParamterDto approveParam = (ApproveParamterDto) approveParamList.get(i);
			try {
				//数据转换
				if("String".equalsIgnoreCase(approveParam.getDataType())) {
					mvelDataMap.put(approveParam.getParamCode(), approveParam.getParamValue());
				}else if("int".equalsIgnoreCase(approveParam.getDataType())) {
					mvelDataMap.put(approveParam.getParamCode(), Integer.valueOf(approveParam.getParamValue()).intValue());
				}else if("BigDecimal".equalsIgnoreCase(approveParam.getDataType())) {
					mvelDataMap.put(approveParam.getParamCode(), new BigDecimal(approveParam.getParamValue()));
				}
			}catch(Exception e){
				logger.error("审批路线请求参数数据类型转换错误",e);
				throw new Exception("审批路线请求参数数据类型转换错误！!");
			}
		}
		//当前的活动审批节点
		AuditNodeDto curnode = (AuditNodeDto)this.load(appAudit.getCurAuditNodeId(),AuditNodeDto.class);
		AuditNodeDto nextNode = null;//下一个审核节点
		ApproveAuditBean ret =null;
		if(curnode.getMvelExpr() != null && curnode.getMvelExpr().trim().length() > 0){//按照mvel表达式获取下一个审核节点
			String nextNodeNo = null;
			try{
				Object objNextNodeNo =(String) MvelUtil.eval(curnode.getMvelExpr(),mvelDataMap);
				nextNodeNo = String.valueOf(objNextNodeNo);
			}catch(Exception e1){
				logger.error("当前审批节点中MVEL动态表达式设定有误："+e1.getMessage());
				throw new Exception("当前审批节点中动态表达式规则设定不正确，请联系管理员!");
			}
			if("-1".equals(nextNodeNo.trim())){//当前审批节点权限内直接结束
				return null;
			}
			//查找下一个审批节点
			nextNode  = getAllNodesByRouteIdAndNodeNo(curnode.getRouteId(),nextNodeNo);
			if(nextNode == null){
				throw new Exception("当前审批节点配置了下一岗审批节点，但是没有找到下一个审批节点，请联系管理员!");
			}
			String openBranchId = appAudit.getOpenBranchId();//申请机构ID
			//判断下一岗审批节点是否为交易对手审批
			if(StringUtils.isNotBlank(nextNode.getCpAuditFlag()) 
					&& "1".equals(nextNode.getCpAuditFlag())){
				openBranchId=appAudit.getInnerBranchId();
				if(openBranchId == null){
					throw new Exception("审核节点中未设定交易对手机构ID，请联系管理员!");
				}
			}
			//业务申请机构信息
			Department openDept = (Department)dao.load(Department.class, openBranchId);
			ret = new ApproveAuditBean();
			ret.setNextAuditNodeId(nextNode.getNodeId());
			//【2】查询符合条件的 审批柜员 信息
			List userList = this.getAuditUsersOfNextRoute(openDept,nextNode.getOptRole(),nextNode.getNodeLevel());
			if(userList!=null && userList.size()==0){
				throw new Exception("下一个审批节点【"+nextNodeNo+nextNode.getNodeName()+"】设定的角色下 没有设定审批人员，请联系管理员！");
			}
			ret.setNextAuditUserList(userList);
		}else{
			throw new Exception("当前审核节点【"+curnode.getNodeNum()+"】未设定下一岗计算规则，请联系管理员！");
		}
		
		return ret;
	}
	
	/**
	 * 【审批流程中 提交审批 】
	 * @param appAudit 审批受理对象
	 * @param auditMind 审批意见(0不同意、1同意)
	 * @param auditContent 审核意见
	 * @param user 当前柜员
	 * @param nextUserId 选择的 下一岗审批人员ID列表，多个用户ID用户逗号,分隔
	 * @param curAuditNodeId 当前审批节点ID(从待审批查询列表中进行获取,用户检查当前审批节点可能已经被其他相同角色的用户审批)
	 * @return 
	 * 01:同意，流程正常结束
	 * 02:同意，流程 继续流转到 下一岗审批人；
	 * 03:拒绝，成功找到配置的拒绝岗  审批人，并将流程流转到 指定的拒绝岗；
	 * 04:拒绝，直接将审批流程结束，需要申请岗重新发起审批流程；
	 * @throws Exception
	 * 
	 * <p> 20201016 审批优化  增加驳回时可选驳回节点<p/>
	 * @param rejustNodeNum   驳回节点 
	 * @param rejustUserId    已选择的审批人ID
	 */
	public String txCommitAudit(ApproveAuditDto appAudit,String auditMind, String auditContent,
			User user,String nextUserId,String curAuditNodeId,String rejustNodeNum,String rejustUserId) throws Exception{
		
		if(appAudit == null){
			throw new Exception("未找到审批受理申请信息!");
		}
		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(appAudit.getAuditStatus())//提交审批
				&& !PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(appAudit.getAuditStatus())//审批中
						){
			throw new Exception("您已无权执行当前审批，请确认是否有其他审批人员已经完成审批!");
		}
		if(!curAuditNodeId.equals(appAudit.getCurAuditNodeId())){
			throw new Exception("您已无权执行当前审批，请确认是否有其他审批人员已经完成审批!");
		}
		if(appAudit.getCurAuditUser().indexOf(user.getLoginName())<0){
			throw new Exception("您已无权执行当前审批，请确认是否有其他审批人员已经完成审批!");
		}
		//查找审批受理参数
		List approveParamList = queryApproveParamtersByApproveAuditId(appAudit.getId(),null);
		int paramCount = approveParamList.size();
		Map mvelDataMap = new HashMap();
		for(int i=0; i < paramCount; i++){
			ApproveParamterDto approveParam = (ApproveParamterDto) approveParamList.get(i);
			try {
				//数据转换
				if("String".equalsIgnoreCase(approveParam.getDataType())) {
					mvelDataMap.put(approveParam.getParamCode(), approveParam.getParamValue());
				}else if("int".equalsIgnoreCase(approveParam.getDataType())) {
					mvelDataMap.put(approveParam.getParamCode(), Integer.valueOf(approveParam.getParamValue()).intValue());
				}else if("BigDecimal".equalsIgnoreCase(approveParam.getDataType())) {
					mvelDataMap.put(approveParam.getParamCode(), new BigDecimal(approveParam.getParamValue()));
				}
			}catch(Exception e){
				logger.error("审批路线请求参数数据类型转换错误",e);
				throw new Exception("审批路线请求参数数据类型转换错误！!");
			}
		}
		String allAuditUsers = appAudit.getAllUsers();//已审批用户
		if(allAuditUsers == null){
			allAuditUsers = "";
		}
		
//		Date curDate = DateUtils.getWorkDateTime();
		Date curDate = new Date();
		String retStr="";
		AuditNodeDto curnode = (AuditNodeDto)this.load(appAudit.getCurAuditNodeId(),AuditNodeDto.class);
		String curRoleNm = appAudit.getCurRoleName();//当前角色名称
		String nextNodeNum = "-1";
		if(curnode.getMvelExpr() != null && curnode.getMvelExpr().trim().length() > 0){//按照mvel表达式获取下一个审核节点
			String nextNodeNo = null;
			try{
				
				Object objNextNodeNo = MvelUtil.eval(curnode.getMvelExpr(),mvelDataMap);
				nextNodeNo = String.valueOf(objNextNodeNo);
			}catch(Exception e1){
				logger.error("当前审批节点中MVEL动态表达式设定有误：",e1);
				throw new Exception("当前审批节点【node="+curnode.getNodeNum()+"】中动态表达式规则设定不正确，请联系管理员!");
			}
			//设定已审批用户信息
			if(allAuditUsers.length() > 0){
				allAuditUsers = allAuditUsers + "," + user.getName()+"["+user.getLoginName()+"]";
			}else{
				allAuditUsers = user.getName()+"["+user.getLoginName()+"]";
			}
			appAudit.setAllUsers(allAuditUsers);//已审批用户信息
			if("1".equals(auditMind)){//同意
				if("-1".equals(nextNodeNo.trim())){//当前审批节点权限内直接结束
					appAudit.setLastUpdate(curDate);
					//关闭 审批流程
					appAudit.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_PASS);//审核状态-4审批通过
					retStr="01";
				}else{//继续下一岗
					//查找下一个审批节点
					AuditNodeDto nextNode  = getAllNodesByRouteIdAndNodeNo(curnode.getRouteId(),nextNodeNo);
					if(nextNode == null){
						throw new Exception("当前审批节点【node="+curnode.getNodeNum()+"】配置了下一岗审批节点，但是没有找到下一个审批节点，请联系管理员!");
					}
					String openBranchId = appAudit.getOpenBranchId();//申请机构ID
					//判断下一岗审批节点是否为交易对手审批
					if(StringUtils.isNotBlank(nextNode.getCpAuditFlag()) 
							&& "1".equals(nextNode.getCpAuditFlag())){
						openBranchId=appAudit.getInnerBranchId();
						if(openBranchId == null){
							throw new Exception("当前审批审批流中未设定交易对手机构ID，请联系管理员!");
						}
					}
					appAudit.setLastAuditNodeId(appAudit.getCurAuditNodeId());
					//上一岗审批人 设置为当前用户
					appAudit.setLastAuditUser(user.getLoginName());//上一岗审批用户账号
					appAudit.setLastAuditUserNm(user.getName());//上一岗审批用户名称
					appAudit.setLastAuditBranchId(user.getDepartment().getId());//上一岗审批机构ID
					appAudit.setLastAuditBranchNm(user.getDepartment().getName());//上一岗审批机构ID
					//设置 当前审批节点  为查询出来的  下一审批节点
					appAudit.setCurAuditNodeId(nextNode.getNodeId());
					//业务申请机构信息
					Department openDept = (Department)dao.load(Department.class, openBranchId);
					//【2】查询符合条件的 审批柜员 信息
					List userList = this.getAuditUsersOfNextRoute(openDept,nextNode.getOptRole(),nextNode.getNodeLevel());
					if(userList!=null && !userList.isEmpty()){
						StringBuffer curAuditUserLoginNo=new StringBuffer("");
						StringBuffer curAuditUserNm=new StringBuffer("");
						Map<String,User> allUserMap = new HashMap<String,User>();
						for(int j=0;j<userList.size();j++){
							User  dto = (User)userList.get(j);
							allUserMap.put(dto.getId(),dto);
							curAuditUserLoginNo.append(dto.getLoginName()).append(",");
							curAuditUserNm.append(dto.getName()).append(",");
						}
						//前台页面指定了下一岗审批用户
						if(nextUserId!=null && nextUserId.length()>1){
							curAuditUserLoginNo=new StringBuffer("");
							curAuditUserNm=new StringBuffer("");
							String[] arrNextUserId = nextUserId.split(",");
							for(String tmpNextUserId : arrNextUserId){
								if(allUserMap.containsKey(tmpNextUserId)){
									User  dto =allUserMap.get(tmpNextUserId);
									curAuditUserLoginNo.append(dto.getLoginName()).append(",");
									curAuditUserNm.append(dto.getName()).append(",");
								}
							}
						}
						
						if(curAuditUserNm.length() > 0){//
							appAudit.setCurAuditUser(curAuditUserLoginNo.toString());//当前审批人账号
							appAudit.setCurAuditUserNm(curAuditUserNm.substring(0, curAuditUserNm.length()-1));//当前审批人名称
						}
						//当前节点审批人角色
						if(StringUtils.isNotBlank(nextNode.getOptRole())){
							Role role = (Role)this.load(nextNode.getOptRole(), Role.class);
						    appAudit.setCurRoleName(role.getName());
						}
						User  curAuditUser = (User)userList.get(0);
						Department curAuditDept = (Department)this.load(curAuditUser.getDeptId(), Department.class);
						appAudit.setCurAuditBranchId(curAuditDept.getId());//当前审批人所属机构ID
						appAudit.setCurAuditBranchNm(curAuditDept.getName());//当前审批人所属机构名称
						appAudit.setLastUpdate(curDate);//最后更新时间
						appAudit.setCurAuditNodeId(nextNode.getNodeId());//当前审批节点id
						nextNodeNum = nextNode.getNextNode();
					}else{
						throw new Exception("下一个审批节点【"+nextNodeNo+"】设定的角色下 没有设定审批人员，请联系管理员！");
					}
					retStr="02";
				}
			}else{//不同意
				List busiTabConfList = auditBusiTableConfigService.queryBusiTabConfigsByProductId(appAudit.getProductId());
				if(busiTabConfList != null && busiTabConfList.size() > 0){
					BusiTableConfig busiTableConfig = (BusiTableConfig) busiTabConfList.get(0);
					if(!"true".equals(busiTableConfig.getRejustNode())){
						if(curnode.getBackNode()!=null && !PublicStaticDefineTab.AUDIT_NODE_END_NUM.equals(curnode.getBackNode())){
							AuditNodeDto refuseNode  = this.getAllNodesByRouteIdAndNodeNo(curnode.getRouteId(),curnode.getBackNode());
							if(refuseNode==null){
								throw new Exception("当前岗位【node="+curnode.getNodeNum()+"】设的审批拒绝审批节点不存在，请联系管理员！");
							}
							//【2】查询符合条件的 审批柜员 信息
							String openBranchId= appAudit.getOpenBranchId();
							//判断驳回岗是否为交易对手审批节点
							if(StringUtils.isNotBlank(refuseNode.getCpAuditFlag()) 
									&& "1".equals(refuseNode.getCpAuditFlag())){
								openBranchId=appAudit.getInnerBranchId();
								if(openBranchId == null){
									throw new Exception("当前审批流中未设定交易对手机构ID，请联系管理员!");
								}
							}
							//业务申请机构信息
							Department openDept = (Department)dao.load(Department.class, openBranchId);
							List userList = this.getAuditUsersOfNextRoute(openDept,refuseNode.getOptRole(),refuseNode.getNodeLevel());
							if(userList!=null && userList.size()>0){
								//设定上一岗审批节点信息
								appAudit.setLastAuditNodeId(appAudit.getCurAuditNodeId());
								appAudit.setLastAuditUser(user.getLoginName());//上一岗审批用户账号
								appAudit.setLastAuditUserNm(user.getName());//上一岗审批用户名称
								appAudit.setLastAuditBranchId(user.getDepartment().getId());//上一岗审批机构ID
								appAudit.setLastAuditBranchNm(user.getDepartment().getName());//上一岗审批机构ID
								
								
								StringBuffer curAuditUserLoginNo=new StringBuffer("");
								StringBuffer curAuditUserNm=new StringBuffer("");
								for(int j=0;j<userList.size();j++){
									User  dto = (User)userList.get(j);
									curAuditUserLoginNo.append(dto.getLoginName()).append(",");
									curAuditUserNm.append(dto.getName()).append(",");
								}
								
								if(curAuditUserNm.length() > 0){//
									appAudit.setCurAuditUser(curAuditUserLoginNo.toString());//当前审批人账号
									appAudit.setCurAuditUserNm(curAuditUserNm.substring(0, curAuditUserNm.length()-1));//当前审批人名称
								}
								//当前节点审批人角色
								if(StringUtils.isNotBlank(refuseNode.getOptRole())){
									Role role = (Role)this.load(refuseNode.getOptRole(), Role.class);
								    appAudit.setCurRoleName(role.getName());
								}
								User  curAuditUser = (User)userList.get(0);
								Department curAuditDept = (Department)this.load(curAuditUser.getDeptId(), Department.class);
								appAudit.setCurAuditBranchId(curAuditDept.getId());//当前审批人所属机构ID
								appAudit.setCurAuditBranchNm(curAuditDept.getName());//当前审批人所属机构名称
								appAudit.setLastUpdate(curDate);//最后更新时间
								appAudit.setCurAuditNodeId(refuseNode.getNodeId());//当前审批节点
								nextNodeNum = refuseNode.getNextNode();
								retStr="03";
							}else{
								throw new Exception("驳回岗审批节点配置的角色下面没有审批人员，请联系管理员！");
							}
						}else{//【2】没有配置审批拒绝岗  直接结束审批 直接拒绝到原点
							appAudit.setLastUpdate(curDate);
							appAudit.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_GOBACK);//审批状态-3驳回
							retStr="04";
						}
					}else{
						//审批人驳回时是否可以选择退回岗位和退回人  当此开关开启时 驳回时驳回到相应节点
						if(StringUtils.isNotBlank(rejustNodeNum) && !PublicStaticDefineTab.AUDIT_NODE_END_NUM.equals(rejustNodeNum)){
							//查询该驳回节点是否存在
							AuditNodeDto refuseNode  = this.getAllNodesByRouteIdAndNodeNo(curnode.getRouteId(),rejustNodeNum);
							if(refuseNode==null){
								throw new Exception("当前岗位【node="+curnode.getNodeNum()+"】设的审批拒绝审批节点已不存在，请联系管理员！");
							}
							//【2】查询符合条件的 审批柜员 信息
							String openBranchId= appAudit.getOpenBranchId();
							//判断驳回岗是否为交易对手审批节点
							if(StringUtils.isNotBlank(refuseNode.getCpAuditFlag()) 
									&& "1".equals(refuseNode.getCpAuditFlag())){
								openBranchId=appAudit.getInnerBranchId();
								if(openBranchId == null){
									throw new Exception("当前审批流中未设定交易对手机构ID，请联系管理员!");
								}
							}
							//业务申请机构信息
							Department openDept = (Department)dao.load(Department.class, openBranchId);
							List userList = this.getAuditUsersOfNextRoute(openDept,refuseNode.getOptRole(),refuseNode.getNodeLevel());
							if(userList!=null && userList.size()>0){
								//设定上一岗审批节点信息
								appAudit.setLastAuditNodeId(appAudit.getCurAuditNodeId());
								appAudit.setLastAuditUser(user.getLoginName());//上一岗审批用户账号
								appAudit.setLastAuditUserNm(user.getName());//上一岗审批用户名称
								appAudit.setLastAuditBranchId(user.getDepartment().getId());//上一岗审批机构ID
								appAudit.setLastAuditBranchNm(user.getDepartment().getName());//上一岗审批机构ID
								
								StringBuffer curAuditUserLoginNo=new StringBuffer("");
								StringBuffer curAuditUserNm=new StringBuffer("");
								for(int j=0;j<userList.size();j++){
									User  dto = (User)userList.get(j);
									//校验当前审批人是否正常且驳回至具体节点具体审批人
									if(dto.getId().equals(rejustUserId)){
										curAuditUserLoginNo.append(dto.getLoginName()).append(",");
										curAuditUserNm.append(dto.getName()).append(",");
									}
								}
								
								if(curAuditUserNm.length() > 0){
									appAudit.setCurAuditUser(curAuditUserLoginNo.toString());//当前审批人账号
									appAudit.setCurAuditUserNm(curAuditUserNm.substring(0, curAuditUserNm.length()-1));//当前审批人名称
								}
								//当前节点审批人角色
								if(StringUtils.isNotBlank(refuseNode.getOptRole())){
									Role role = (Role)this.load(refuseNode.getOptRole(), Role.class);
								    appAudit.setCurRoleName(role.getName());
								}
								User  curAuditUser = (User)userList.get(0);
								Department curAuditDept = (Department)this.load(curAuditUser.getDeptId(), Department.class);
								appAudit.setCurAuditBranchId(curAuditDept.getId());//当前审批人所属机构ID
								appAudit.setCurAuditBranchNm(curAuditDept.getName());//当前审批人所属机构名称
								appAudit.setLastUpdate(curDate);//最后更新时间
								appAudit.setCurAuditNodeId(refuseNode.getNodeId());//当前审批节点
								nextNodeNum = refuseNode.getNextNode();
								retStr="03";
							}else{
								throw new Exception("驳回岗审批节点配置的角色下面没有审批人员，请联系管理员！");
							}
							
						}else{
							//当选择的驳回节点为-1时  则直接驳回至业务申请岗
							appAudit.setLastUpdate(curDate);
							appAudit.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_GOBACK);//审批状态-3驳回
							retStr="04";
						}
				}	
					}
			}
			
		}else{
			throw new Exception("当前审核节点【"+curnode.getNodeNum()+"】未设定下一岗计算规则，请联系管理员！");
		}

		//记录审批详细信息
		ApproveDto app = new ApproveDto();
		app.setApproveDate(curDate);// 审批日期
		app.setBussinessId(appAudit.getBusiId());// 业务主键ID
		app.setApproveUserNm(user.getName());// 审批人名称
		app.setApproveUserId(user.getId());// 审批人ID
		app.setApproveDept(user.getDepartment().getName());//审批机构名称
		app.setApproveDeptId(user.getDepartment().getId());//审批机构id
		app.setProcessId(appAudit.getId());// 流程ID
		app.setApproveRole(curRoleNm);//审批人角色
		app.setApproveComment(auditContent);// 审批内容
		app.setApproveFlag(auditMind);// 审批标识
		app.setNodeNum(curnode.getNodeNum());//审批节点编号
		app.setNextNodeNum(nextNodeNum);//下一岗审批节点编号
		this.txStore(app);
		this.txStore(appAudit);
		
		return retStr;
	}
	
	/**
	 * 撤销审批 使用  
	 * 查询  当前开启的审批流，并删除
	 * @param productId 产品号
	 * @param srcBusiId 原业务id
	 * @return
	 */
	public void txCommitCancelAudit(String productId,String srcBusiId)throws Exception{	
		ApproveAuditDto app = this.getOpenApproveAudit(productId, srcBusiId);
		String withApply = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.AUDIT_WITHDRAWAL_APPLY);
		if(app!=null){
			if(StringUtils.isNotBlank(withApply) && "true".equals(withApply)){
				if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(app.getAuditStatus())
						&& !PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(app.getAuditStatus())){
					throw new Exception("该笔业务审批受理状态为【"+app.getAuditStatusDes()+"】，不允许撤销。");
				}else{
					this.txDelete(app);
				}
			}else{
				if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(app.getAuditStatus())){
					throw new Exception("该笔业务审批受理状态为【"+app.getAuditStatusDes()+"】，不允许撤销。");
				}else{
					this.txDelete(app);
				}
			}
			}else{
			throw new Exception("审批受理流程未找到，请检查该笔业务审批流程是否已经撤销！");  
		 }
		
	}
	
	/**
	 * 查询审批待受理业务信息
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception
	 */
	public List queryAuditAcceptsBusiness(User user, QueryBean queryBean, Page page) throws Exception{
		StringBuffer sb = new StringBuffer(" from ApproveAuditDto as dto where dto.auditStatus in(:auditStatus) ");
		sb.append(" and dto.curAuditBranchId=:curAuditBranchId and dto.curAuditUser like :curAuditUser");
		//业务发起人不允许参与审批
		sb.append(" and (dto.openUserId != :openUserId or dto.openUserId is null)");
		List stateList = new ArrayList(); //查询参数
		List valueList = new ArrayList(); //查询参数值
		
		List keyList = new ArrayList(); // 要查询的字段列表
		//审批受理状态
		stateList.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); // 提交审批
		stateList.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING); // 审批中
		keyList.add("auditStatus");
		valueList.add(stateList);
		//当前审批机构
		keyList.add("curAuditBranchId");
		valueList.add(user.getDepartment().getId());
		//当前审批人
		keyList.add("curAuditUser");
		valueList.add("%"+user.getLoginName()+",%");
		//业务申请人不允许参与审批
		keyList.add("openUserId");
		valueList.add(user.getId());
		
		if (queryBean != null) {
			//经办机构id
			if(StringUtils.isNotBlank(queryBean.getApplyBranchId())){
				sb.append(" and dto.openBranchId =:openBranchId");
				keyList.add("openBranchId");
				valueList.add(queryBean.getApplyBranchId());
			}
			//业务类型
			if(StringUtils.isNotBlank(queryBean.getBusiType())){
				sb.append(" and dto.busiType =:busiType");
				keyList.add("busiType");
				valueList.add(queryBean.getBusiType());
			}
			if (queryBean.getStartDate() != null) { // 申请开始日期
				sb.append(" and dto.openDate >= :StartDate ");
				keyList.add("StartDate");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getStartDate()));
			}
			if (queryBean.getEndDate() != null) { // 申请结束日期
				sb.append(" and dto.openDate <= :EndDate ");
				keyList.add("EndDate");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getEndDate()));
			}
			//经办人名称
			if(StringUtils.isNotBlank(queryBean.getApplyUserNm())){
				sb.append(" and dto.openUserNm like :openUserNm");
				keyList.add("openUserNm");
				valueList.add("%"+queryBean.getApplyUserNm()+"%");
			}
			//业务申请号
			if (StringUtils.isNotBlank(queryBean.getApplyNo())) {
				sb.append(" and dto.applyNo = :applyNo ");
				keyList.add("applyNo");
				valueList.add(queryBean.getApplyNo());
			}
			
			//交易对手名称
			if (StringUtils.isNotBlank(queryBean.getCpCustNm())) {
				sb.append(" and dto.custName like :custName ");
				keyList.add("custName");
				valueList.add("%"+queryBean.getCpCustNm()+"%");
			}
			//交易对手证件号
			if (StringUtils.isNotBlank(queryBean.getCpCustCertNo())) {
				sb.append(" and dto.custCertNo like :custCertNo ");
				keyList.add("custCertNo");
				valueList.add("%"+queryBean.getCpCustCertNo()+"%");
			}
			
			//票据类型
			if (StringUtils.isNotBlank(queryBean.getBillType())) {
				sb.append(" and dto.billType = :billType ");
				keyList.add("billType");
				valueList.add(queryBean.getBillType());
			}
			//票据介质
			if (StringUtils.isNotBlank(queryBean.getBillMedia())) {
				sb.append(" and dto.billMedia = :billMedia ");
				keyList.add("billMedia");
				valueList.add(queryBean.getBillMedia());
			}
			
		}
		
		sb.append(" order by dto.openDate desc,dto.id ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray(), page);
		return list;
	}
	
	/**
	 * 根据id查询审批受理申请信息
	 * @param id 审批受理申请信息ID
	 * @return ApproveAuditDto 审批受理申请
	 * @throws Exception
	 */
	public ApproveAuditDto getApproveAuditDtoById(String id)throws Exception{
		return (ApproveAuditDto)dao.load(ApproveAuditDto.class, id);
	}
	
	/**
	 * 根据审批受理申请id或原业务id查询该笔业务审核路线
	 * @param id 审批受理申请信息ID
	 * @param srcBusiId 原业务ID
	 * @return Map key=auditInf value=审批受理信息；key=nodes value=审核批节点信息
	 * @throws Exception
	 */
	public Map<String,Object> queryAuditBusiAuditRoute(String id,String srcBusiId,User user)throws Exception{
		
		List parasNames = new ArrayList();
		List parasValues = new ArrayList();
		
		StringBuffer hql = new StringBuffer("from  ApproveAuditDto as dto where ");
		if(StringUtils.isNotBlank(id)){
			hql.append(" dto.id =:id ");
			parasNames.add("id");
			parasValues.add(id);
		}else if(StringUtils.isNotBlank(srcBusiId)){
			hql.append(" dto.busiId =:busiId ");
			parasNames.add("busiId");
			parasValues.add(srcBusiId);
		}
		hql.append(" order by dto.openDate desc ");
		
		List list = this.find(hql.toString(), (String[]) parasNames.toArray(new String[parasNames.size()]), parasValues.toArray());
		if(list.isEmpty()){
			return new HashMap<String, Object>();
		}
		ApproveAuditDto auditDto  = (ApproveAuditDto)list.get(0); 
		
		//查询当前审批流已审批结果
		List audtiResultList = this.queryAuditResultByBusiIdAndAuditId(auditDto.getBusiId(),auditDto.getId(),null);
		int count = audtiResultList.size();
		//临时存放审批结果-key为审批节点编号
		Map<String,ApproveDto> audtiResultMap = new HashMap<String,ApproveDto>();
	    for(int i = 0; i < count; i++){
	    	ApproveDto approveDto = (ApproveDto)audtiResultList.get(i);
	    	audtiResultMap.put(approveDto.getNodeNum(), approveDto);
	    }
	    //根据审核路线id查询审批节点信息
	    List nodeList = auditRouteService.getAllNodesByRouteId(auditDto.getRouteId(),null);
	    
	    List queryEndNodeList = this.queryEndNodeList(auditDto, nodeList);
	    
	    int nodeCount = queryEndNodeList.size();
	    
	    List nodes = new ArrayList();
	    for(int i = 0; i < nodeCount; i++){
	    	AuditNodeDto node = (AuditNodeDto)nodeList.get(i);
	    	AuditNodeBean nodeBean = new AuditNodeBean();
	    	nodeBean.setNodeType(node.getNodeType());//节点类型:1自动、0人工
	    	nodeBean.setNodeName(node.getNodeName());//岗位名称
	    	nodeBean.setNodeNum(node.getNodeNum());//执行序号
	    	nodeBean.setAuditResult("-2");//未执行
	    	nodeBean.setNextNodeNum(node.getNextNode() != null ? node.getNextNode() : "");//下一岗节点编号
	    	if("0".equals(node.getNodeType())){//人工节点
	    		if(audtiResultMap.containsKey(node.getNodeNum())){//说明当前节点已经被执行
	    			ApproveDto approveDto  = audtiResultMap.get(node.getNodeNum());
	    			nodeBean.setOptRole(approveDto.getApproveRole());
	    			nodeBean.setAuditUserNm(approveDto.getApproveUserNm());//审批人名称
	    			nodeBean.setAuditDate(approveDto.getApproveDate());//审批时间
	    			nodeBean.setAuditResult(approveDto.getApproveFlag());//审批结果:1通过、0驳回、-1终止
	    			
	    		}else{
	    			//正在执行
	    			if(auditDto.getCurAuditNodeId().equals(node.getNodeId())){
	    				nodeBean.setAuditResult("2");//正在执行
	    				nodeBean.setOptRole(auditDto.getCurRoleName());
		    			nodeBean.setAuditUserNm(auditDto.getCurAuditUserNm());//审批人名称
	    			}
	    		}
	    	
	    	}
	    	nodes.add(nodeBean);
	    }
	    
	    Map<String,Object> resultMap = new HashMap<String,Object>();
	    ApproveAuditBean auditBean = new ApproveAuditBean();
	    auditBean.setId(auditDto.getId());//审批受理id
	    auditBean.setProductId(auditDto.getProductId());//产品编码
		auditBean.setBusiId(auditDto.getBusiId());//原业务ID
	    auditBean.setAuditStatus(auditDto.getAuditStatus());//审批受理状态
	    auditBean.setOpenUserNm(auditDto.getOpenUserNm());//业务经办人
	    auditBean.setOpenDate(auditDto.getOpenDate());//启动时间
	    
	    if(StringUtils.isNotBlank(id)){
	    	 List<ApproveDto> canRejustdNodes = auditRouteService.queryCanRejustdNodes(id, user, auditDto.getRouteId(), auditDto.getCurAuditNodeId(),auditDto.getProductId());
	    	 if(canRejustdNodes!=null)
	    	 resultMap.put("rejust", canRejustdNodes);//审批可驳回节点
	    }
	   
	    resultMap.put("auditInf", auditBean);//审批节受理信息
	    resultMap.put("nodes", nodes);//审批节点信息
	   
		return resultMap;
	}
	
	/**
	 * 根据原业务id、审批受理申请id查询该业务审批结果
	 * @param srcBusiId 原业务ID
	 * @param auditId 审批受理申请id
	 * @return List 审核路线
	 * @throws Exception
	 */
	public List queryAuditResultByBusiIdAndAuditId(String srcBusiId,String auditId,Page page)throws Exception{
		List parasNames = new ArrayList();
		List parasValues = new ArrayList();
		
		StringBuffer hql = new StringBuffer("from  ApproveDto as dto where ");
		hql.append(" dto.bussinessId =:bussinessId ");
		parasNames.add("bussinessId");
		parasValues.add(srcBusiId);
		if(StringUtils.isNotBlank(auditId)){
			hql.append(" and dto.processId =:processId ");
			parasNames.add("processId");
			parasValues.add(auditId);
		}
		hql.append(" order by dto.approveDate desc ");
		
		List list = this.find(hql.toString(), (String[]) parasNames.toArray(new String[parasNames.size()]), parasValues.toArray(),page);
		return list;
	}
	
	/**
	 * 通用审批方法
	 * @param user 当前登录用户
	 * @param approveId 审批受理申请id
	 * @param approveComment 审批意见
	 * @param curAuditNodeId 当前审批节点id
	 * @param nextUserId 下一岗审批人ID，多个id使用,逗号分隔
	 * @throws Exception
	 */
	public String txCommonCommitAudit(User user,String approveId,String approveFlag,String approveComment,String curAuditNodeId,String nextUserId,String rejustNodeNum,String rejustUserId)throws Exception{
		//获取审批受理对象
		ApproveAuditDto auditDto = this.queryAuditInfoById(approveId);
		String flag = this.txCommitAudit(auditDto,approveFlag,approveComment,user,nextUserId,curAuditNodeId,rejustNodeNum,rejustUserId);
		String status = null;
		if("01".equals(flag)){  
			status=PublicStaticDefineTab.AUDIT_STATUS_PASS;  //审批通过
		}else if("02".equals(flag)){
			status=PublicStaticDefineTab.AUDIT_STATUS_RUNNING;  //审批中
		}else if("03".equals(flag)){
			status = PublicStaticDefineTab.AUDIT_STATUS_RUNNING; //审批中
		}else{
			status = PublicStaticDefineTab.AUDIT_STATUS_GOBACK;  //不同意
		}
		
		//查询业务表配置信息
		List busiTabConfList = auditBusiTableConfigService.queryBusiTabConfigsByProductId(auditDto.getProductId());
		int tabCount = busiTabConfList.size();
		if(tabCount == 0){
			return "未找到产品ID对应的业务表审批配置信息。";
		}
		for(int i=0; i<tabCount; i++){
			BusiTableConfig busiTabConfig = (BusiTableConfig)busiTabConfList.get(i);
			//如果审批业务表中配置了审批状态,则使用审批业务表中配置的审批状态值
			if("01".equals(flag) && StringUtils.isNotBlank(busiTabConfig.getAuditPassStatus())){  
				status=busiTabConfig.getAuditPassStatus();  //审批通过
			}else if("02".equals(flag) && StringUtils.isNotBlank(busiTabConfig.getAuditRunningStatus())){
				status=busiTabConfig.getAuditRunningStatus();  //审批中
			}else if("03".equals(flag) && StringUtils.isNotBlank(busiTabConfig.getAuditRunningStatus())){
				status = busiTabConfig.getAuditRunningStatus(); //审批中
			}else if("04".equals(flag) && StringUtils.isNotBlank(busiTabConfig.getAuditRefuseStatus())){
				status = busiTabConfig.getAuditRefuseStatus();  //不同意
			}
			//如果审批通过，并且配置了扩展服务，则调用扩展服务进行审批结果处理
			if(StringUtils.isNotBlank(busiTabConfig.getExtendService())){
				AuditExtendService auditExtendService = auditExtendServiceFactory.getAuditExtendService(busiTabConfig.getExtendService().trim());
			    if(auditExtendService==null){
			    	return "根据扩展服务名未找到对应的扩展服务，请检查。";
			    }
			     String result = auditExtendService.txDealWithAuditResult(user,approveId, auditDto.getBusiId(), status,busiTabConfig);
		    	 return result;
			}else{
				//更新业务表状态
				logger.info("----------------------------------------");
				System.out.println("====================="+busiTabConfig.getBusiTable()+"=================");
				if("T_RATE_MAINTAININFO".equals(busiTabConfig.getBusiTable())){
					TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(auditDto.getBusiId(),TxRateMaintainInfo.class);
					
					String result =  txRateMaintainInfoService.checkAndUpdate(auditDto.getBusiId());
					
					if(StringUtil.isNotEmpty(result)){
						return result;
					}
					
					//	未到期   待生效
					if("03".equals(info.getRateType())){
						if(DateUtils.checkOverLimited(DateUtils.parse(info.getEffTime()), new Date())){
							status = "2";
						}else{
							String sql = "update T_RATE_MAINTAININFO set EFFSTATE = '0' where RATETYPE = '03' and EFFSTATE = '1'";
							dao.updateSQL(sql);
						}
					}
					
					info.setEffState(status);
					info.setReviewer(user.getName());
					info.setReviewerNo(user.getLoginName());
					String currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					info.setMaintainTime(currTime);
					
					txRateMaintainInfoService.updateTxRateInfo(info);
				}else{
					StringBuffer sql = new StringBuffer("update ");
					sql.append(busiTabConfig.getBusiTable()).append(" Set ").append(busiTabConfig.getBusiStatusField());
					sql.append("='").append(status).append("' where ");
					sql.append(busiTabConfig.getBusiIdField()).append("='").append(auditDto.getBusiId()).append("' ");
					dao.updateSQL(sql.toString());
				}
				
				if(auditDto.getProductId().equals("4001001") && status.equals(PublicStaticDefineTab.AUDIT_STATUS_PASS)){
					//商票财票审批改变生效状态
					GuarantDiscMapping grarant = (GuarantDiscMapping) this.load(auditDto.getBusiId(),GuarantDiscMapping.class);
					if(grarant.getCheckType().equals("2")){//商票
						//商票，根据承兑人名称、账号、行号判断如果存在生效的承兑人映射则不允许添加
						  GuarantDiscMapping bean = new GuarantDiscMapping();
						  bean.setAcceptor(grarant.getAcceptor().trim());
						  bean.setAcptAcctNo(grarant.getAcptAcctNo());//承兑人账号
						  bean.setAcptBankCode(grarant.getAcptBankCode());//承兑行行号
						  bean.setCheckType(grarant.getCheckType());
						  bean.setId(grarant.getId());
						  bean.setStatus("1");//生效
						  List list = blackListManageService.queryChangeAcceptorMapping(bean,null, null);
						  if(list != null && list.size() > 0){
							  auditDto.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);//提交审批
							  grarant.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);//提交审批
							  this.txStore(auditDto);
							  this.txStore(grarant);
							  return "存在生效的商票映射!";
						  }
					  }else{
						  Map map = blackListManageService.queryCpesMember(grarant.getAcptBankCode());//
						  String bankCode = (String) map.get("totalBankNo");//总行行号
						  List<String> banks = blackListManageService.queryAllBranchBank(bankCode);//总行及下的分行
						  
						  GuarantDiscMapping bean = new GuarantDiscMapping();
//						  bean.setAcptBankCode(bankCode);
						  bean.setCheckType(grarant.getCheckType());
						  bean.setId(grarant.getId());
						  bean.setStatus("1");//生效
						  List list = blackListManageService.queryChangeAcceptorMapping(bean, banks, null);
						  if(list != null && list.size() > 0){
							  auditDto.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);//提交审批
							  grarant.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);//提交审批
							  this.txStore(auditDto);
							  this.txStore(grarant);
							  return "存在相同总行生效的财票映射!";
						  }
						  
					  }
					if (status.equals(PublicStaticDefineTab.AUDIT_STATUS_PASS)){
						grarant.setStatus("1");//生效
					}
					this.txStore(grarant);
					
				}else if (auditDto.getProductId().equals("5001006") && status.equals(PublicStaticDefineTab.AUDIT_STATUS_PASS)) {
					TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(auditDto.getBusiId(),TxRateMaintainInfo.class);
					info.setReviewer(user.getName());
					info.setReviewerNo(user.getLoginName());
					info.setMaintainTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					this.txStore(info);
					try {
						txRateMaintainInfoService.txRateSend(info,user);
					} catch (Exception e) {
						return "发送中台报错"+e;
					}
					
				}else if (auditDto.getProductId().equals("5001007") && status.equals(PublicStaticDefineTab.AUDIT_STATUS_PASS)) {
					TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(auditDto.getBusiId(),TxRateMaintainInfo.class);
					info.setReviewer(user.getName());
					info.setReviewerNo(user.getLoginName());
					info.setMaintainTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					this.txStore(info);
					try {
						txRateMaintainInfoService.txRateSend(info,user);
					} catch (Exception e) {
						return "发送中台报错"+e;
					}
					
				}
			}
			
		}
		return "success";
	}
	
	/**
	 * 终止审批
	 * @param user 当前登录用户
	 * @param id 审批受理id
	 * @param auditResult 审批结果
	 * @param auditComment 审批意见
	 * @throws Exception
	 */
	public void txStopAudit(User user,String id,String auditComment)throws Exception{

		ApproveAuditDto auditDto = this.getApproveAuditDtoById(id);
		/**
		 * 查询业务类型为审价的，需要找到第一岗的审批信息（拿到客户经理信息）（只有客户经理与当前审批人才可以做终止）
		 */
		if(auditDto.getBusiType().equals("01") || auditDto.getBusiType().equals("02") || auditDto.getBusiType().equals("03") || 
				auditDto.getBusiType().equals("04") || auditDto.getBusiType().equals("05") ){
			if(auditDto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_01)){
				//如果是经办行审批,则表示该审批为第一层审批,直接判断当前登录人是否为业务经办人或者是当前审批人，是的话可以审批终止，否的花不允许做审批终止
				/**
				 * 1、查询当前审批的提交业务经办人
				 */
				User openUser = (User) this.load(auditDto.getOpenUserId(), User.class);
				
				/**
				 * 2、判断当前登录人员与当前审批的提交业务经办人或当前审批的当前审批人是否一直
				 */
				logger.info("当前为分支行审批。。。。。。。。。。。。。。。。");
				String[] userNames = auditDto.getCurAuditUser().split(",");
				String curAuditUser = userNames[userNames.length-1];
				logger.info("当前登录人员是："+user.getLoginName()+"、 审批打开人员："+openUser.getLoginName()+"、 当前审批人员："+curAuditUser);
				if(!user.getLoginName().equals(openUser.getLoginName()) && !user.getLoginName().equals(curAuditUser)){
					throw new Exception("您不允许此终止操作，若要终止请联系【"+openUser.getName()+"、"+auditDto.getCurAuditUserNm()+"】");
				}
			}else{
				//如果不是经办行审批,则需要查询经办行的审批流，获取提交审批的业务经办人,判断当前登录人是否为业务经办人或者是当前审批人，是的话可以审批终止，否的花不允许做审批终止
				/**
				 * 1、查询此业务的经办行审批流
				 */
				ApproveAuditDto openAudit = this.getOpenApproveAudit(PublicStaticDefineTab.APPROVAL_ROUTE_01, auditDto.getBusiId());
				
				/**
				 * 2、查询分支行审批的提交业务经办人
				 */
				User openUser = (User) this.load(openAudit.getOpenUserId(), User.class);
				/**
				 * 2、判断当前登录人员与当前审批的提交业务经办人或当前审批的当前审批人是否一直
				 */
				logger.info("当前为其他审批。。。。。。。。。。。。。。。。");
				String[] userNames = auditDto.getCurAuditUser().split(",");
				String curAuditUser = userNames[userNames.length-1];
				logger.info("当前登录人员是："+user.getLoginName()+"、 审批打开人员："+openUser.getLoginName()+"、 当前审批人员："+curAuditUser);
				if(!user.getLoginName().equals(openUser.getLoginName()) && !user.getLoginName().equals(curAuditUser)){
					throw new Exception("您不允许此终止操作，若要终止请联系【"+openUser.getName()+"、"+auditDto.getCurAuditUserNm()+"】");
				}
				
			}
		}


		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(auditDto.getAuditStatus())
				&& !PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(auditDto.getAuditStatus())){
			throw new Exception("该笔审批受理申请状态为"+auditDto.getAuditStatusDes()+"，不允许终止流程");
		}
		//查询业务表配置信息
	    List busiTabConfList = auditBusiTableConfigService.queryBusiTabConfigsByProductId(auditDto.getProductId());
		int tabCount = busiTabConfList.size();
		if(tabCount == 0){
			throw new Exception("未找到产品ID对应的业务表审批配置信息。");
		}
		
		auditDto.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_STOP);//终止
		auditDto.setCurAuditUser(user.getLoginName());
		auditDto.setCurAuditUserNm(user.getName());
		auditDto.setCurAuditBranchId(user.getDepartment().getId());
		auditDto.setCurAuditBranchNm(user.getDepartment().getName());
		Date curDate = DateUtils.getWorkDateTime();
		auditDto.setLastUpdate(curDate);
		//记录审批详细信息
		ApproveDto app = new ApproveDto();
		app.setApproveDate(curDate);// 审批日期
		app.setBussinessId(auditDto.getBusiId());// 业务主键ID
		app.setApproveUserNm(user.getName());// 审批人名称
		app.setApproveUserId(user.getId());// 审批人ID
		app.setApproveDept(user.getDepartment().getName());//审批机构名称
		app.setApproveDeptId(user.getDepartment().getId());//审批机构id
		app.setProcessId(auditDto.getId());// 流程ID
		app.setApproveRole("管理员");//审批人角色
		app.setApproveComment(auditComment);// 审批内容
		app.setApproveFlag("-1");// 审批标识
		app.setNodeNum("-1");//审批节点编号
		app.setNextNodeNum("-1");//下一岗审批节点编号
		this.txStore(app);
		this.txStore(auditDto);
		//更新业务表状态
		for(int i=0; i<tabCount; i++){
			BusiTableConfig busiTabConfig = (BusiTableConfig)busiTabConfList.get(i);
			if(StringUtils.isNotBlank(busiTabConfig.getExtendService())){
				AuditExtendService auditExtendService = auditExtendServiceFactory.getAuditExtendService(busiTabConfig.getExtendService().trim());
			    if(auditExtendService==null){
			    	throw new Exception("根据扩展服务名未找到对应的扩展服务，请检查。");
			    }
			    String result = auditExtendService.txDealWithAuditResult(user,null, auditDto.getBusiId(), PublicStaticDefineTab.AUDIT_STATUS_STOP,busiTabConfig);
			    if(!"success".equals(result)){
			    	 throw new Exception(result);
			     }
			}else{
				StringBuffer sql = new StringBuffer("update ");
				sql.append(busiTabConfig.getBusiTable()).append(" Set ").append(busiTabConfig.getBusiStatusField());
				sql.append("='").append(PublicStaticDefineTab.AUDIT_STATUS_STOP).append("' where ");
				sql.append(busiTabConfig.getBusiIdField()).append("='").append(auditDto.getBusiId()).append("' ");
				dao.updateSQL(sql.toString());
			}
		}
		
	}
	
	/**
	 * 首页待办任务查询
	 * @param user 当前登录用户
	 * @throws Exception
	 */
	public List queryAuditTask(User user)throws Exception{
		StringBuffer sql = new StringBuffer("select PRODUCT_ID,count(1)  from BT_APPROVE_AUDIT a ");
		sql.append(" where a.AUDIT_STATUS in (:auditStatus)");//审批状态
		sql.append(" and a.CUR_AUDIT_BRANCHID =:auditBranchId");//当前审批机构
		sql.append(" and a.CUR_AUDIT_USER  like :auditUser ");//审批用户
		sql.append("  group by product_id");
		List valueList = new ArrayList(); //查询参数值
		List keyList = new ArrayList(); // 要查询的字段列表
		//审批受理状态
		List stateList = new ArrayList();
		stateList.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); // 提交审批
		stateList.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING); // 审批中
		keyList.add("auditStatus");
		valueList.add(stateList);
		//审批机构
		keyList.add("auditBranchId");
		valueList.add(user.getDepartment().getId());
		//审批用户
		keyList.add("auditUser");
		valueList.add("%"+user.getLoginName()+"%");
		
	
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = dao.SQLQuery(sql.toString(), keyArray, valueList.toArray(), null);
		int count = list.size();
		List results = new ArrayList();
		for(int i=0; i < count; i++){
			Object[] arrObj = (Object[])list.get(i);
			ApproveAuditBean auditBean = new ApproveAuditBean();
			auditBean.setProductId((String)arrObj[0]);//产品idID
			auditBean.setTotalNum((new BigDecimal(arrObj[1].toString())).intValue());//待审批业务数
			results.add(auditBean);
		}
		return results;

	}
	
	/**
	 * 查询历史审批业务信息
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception
	 */
	
	public List queryHistoryAuditBusiness(User user, QueryBean queryBean,
			Page page) throws Exception {
		StringBuffer sb = new StringBuffer(" from ApproveAuditDto as dto where ((dto.curAuditBranchId=:curAuditBranchId  ");
		sb.append(" and dto.allUsers like :allUsers) ");
		List stateList = new ArrayList(); //查询参数
		List valueList = new ArrayList(); //查询参数值
		
		List keyList = new ArrayList(); // 要查询的字段列表
		//审批受理状态
	
		
		//当前审批机构
		keyList.add("curAuditBranchId");
		valueList.add(user.getDepartment().getId());
		
		//所有审批人中包含当前审批人
		keyList.add("allUsers");
		valueList.add("%"+user.getLoginName()+"%");
		sb.append(" or ( dto.openBranchId =:openBranchId and dto.openUserId =:openUserId )) ");
		//启动机构ID
		keyList.add("openBranchId");
		valueList.add(user.getDepartment().getId());
		
		//启动审批流人员ID
		keyList.add("openUserId");
		valueList.add(user.getId());
		
		if (queryBean != null) {
			//经办机构id
			if(StringUtils.isNotBlank(queryBean.getApplyBranchId())){
				sb.append(" and dto.openBranchId =:openBranchId");
				keyList.add("openBranchId");
				valueList.add(queryBean.getApplyBranchId());
			}
			//业务类型
			if(StringUtils.isNotBlank(queryBean.getBusiType())){
				sb.append(" and dto.busiType =:busiType");
				keyList.add("busiType");
				valueList.add(queryBean.getBusiType());
			}
			if (queryBean.getStartDate() != null) { // 申请开始日期
				sb.append(" and dto.openDate >= :StartDate ");
				keyList.add("StartDate");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getStartDate()));
			}
			if (queryBean.getEndDate() != null) { // 申请结束日期
				sb.append(" and dto.openDate <= :EndDate ");
				keyList.add("EndDate");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getEndDate()));
			}
			//经办人名称
			if(StringUtils.isNotBlank(queryBean.getApplyUserNm())){
				sb.append(" and dto.openUserNm like :openUserNm");
				keyList.add("openUserNm");
				valueList.add("%"+queryBean.getApplyUserNm()+"%");
			}
			//业务申请号
			if (StringUtils.isNotBlank(queryBean.getApplyNo())) {
				sb.append(" and dto.applyNo = :applyNo ");
				keyList.add("applyNo");
				valueList.add(queryBean.getApplyNo());
			}
			
			//交易对手名称
			if (StringUtils.isNotBlank(queryBean.getCpCustNm())) {
				sb.append(" and dto.custName like :custName ");
				keyList.add("custName");
				valueList.add("%"+queryBean.getCpCustNm()+"%");
			}
			//交易对手证件号
			if (StringUtils.isNotBlank(queryBean.getCpCustCertNo())) {
				sb.append(" and dto.custCertNo like :custCertNo ");
				keyList.add("custCertNo");
				valueList.add("%"+queryBean.getCpCustCertNo()+"%");
			}
			
			//业务办理模式
			if (StringUtils.isNotBlank(queryBean.getAuditType())) {
				sb.append(" and dto.auditType like :auditType ");
				keyList.add("auditType");
				valueList.add("%"+queryBean.getAuditType()+"%");
			}
			
			//票据类型
			if (StringUtils.isNotBlank(queryBean.getBillType())) {
				sb.append(" and dto.billType = :billType ");
				keyList.add("billType");
				valueList.add(queryBean.getBillType());
			}
			//票据介质
			if (StringUtils.isNotBlank(queryBean.getBillMedia())) {
				sb.append(" and dto.billMedia = :billMedia ");
				keyList.add("billMedia");
				valueList.add(queryBean.getBillMedia());
			}
			
			//审批状态
			if(StringUtils.isNotBlank(queryBean.getAuditStatus())){
				sb.append(" and dto.auditStatus = :auditStatus");
				keyList.add("auditStatus");
				valueList.add(queryBean.getAuditStatus());
			}else{
				sb.append(" and dto.auditStatus in(:auditStatus)");
				stateList.add(PublicStaticDefineTab.AUDIT_STATUS_STOP); // 终止 -1
				stateList.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); // 提交审批
				stateList.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING); // 处理中
				stateList.add(PublicStaticDefineTab.AUDIT_STATUS_GOBACK); // 驳回 3
				stateList.add(PublicStaticDefineTab.AUDIT_STATUS_PASS); // 通过 4
				keyList.add("auditStatus");
				valueList.add(stateList);
			}
			
		}else{
			sb.append(" and dto.auditStatus in(:auditStatus)");
			stateList.add(PublicStaticDefineTab.AUDIT_STATUS_STOP); // 终止 -1
			stateList.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); // 提交审批
			stateList.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING); // 处理中
			stateList.add(PublicStaticDefineTab.AUDIT_STATUS_GOBACK); // 驳回 3
			stateList.add(PublicStaticDefineTab.AUDIT_STATUS_PASS); // 通过 4
			keyList.add("auditStatus");
			valueList.add(stateList);
		}
		
		sb.append(" order by dto.openDate desc,dto.id ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		return this.find(sb.toString(), keyArray, valueList.toArray(), page);
		
	}
	
	
	
	/**
	 * 查询审批流程信息
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception
	 */
	
	public List queryAuditProcess(User user, QueryBean queryBean, Page page)
			throws Exception {
		StringBuffer sb = new StringBuffer(" from ApproveAuditDto as dto where dto.curAuditBranchId=:curAuditBranchId");
		List stateList = new ArrayList(); //查询参数
		List valueList = new ArrayList(); //查询参数值
		
		List keyList = new ArrayList(); // 要查询的字段列表
		
		
		//当前审批机构
		keyList.add("curAuditBranchId");
		valueList.add(user.getDepartment().getId());
		
		
		
		if (queryBean != null) {
			//经办机构id
			if(StringUtils.isNotBlank(queryBean.getApplyBranchId())){
				sb.append(" and dto.openBranchId =:openBranchId");
				keyList.add("openBranchId");
				valueList.add(queryBean.getApplyBranchId());
			}
			//业务类型
			if(StringUtils.isNotBlank(queryBean.getBusiType())){
				sb.append(" and dto.busiType =:busiType");
				keyList.add("busiType");
				valueList.add(queryBean.getBusiType());
			}
			if (queryBean.getStartDate() != null) { // 申请开始日期
				sb.append(" and dto.openDate >= :StartDate ");
				keyList.add("StartDate");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getStartDate()));
			}
			if (queryBean.getEndDate() != null) { // 申请结束日期
				sb.append(" and dto.openDate <= :EndDate ");
				keyList.add("EndDate");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getEndDate()));
			}
			//经办人名称
			if(StringUtils.isNotBlank(queryBean.getApplyUserNm())){
				sb.append(" and dto.openUserNm like :openUserNm");
				keyList.add("openUserNm");
				valueList.add("%"+queryBean.getApplyUserNm()+"%");
			}
			//业务申请号
			if (StringUtils.isNotBlank(queryBean.getApplyNo())) {
				sb.append(" and dto.applyNo = :applyNo ");
				keyList.add("applyNo");
				valueList.add(queryBean.getApplyNo());
			}
			
			//交易对手名称
			if (StringUtils.isNotBlank(queryBean.getCpCustNm())) {
				sb.append(" and dto.custName like :custName ");
				keyList.add("custName");
				valueList.add("%"+queryBean.getCpCustNm()+"%");
			}
			//交易对手证件号
			if (StringUtils.isNotBlank(queryBean.getCpCustCertNo())) {
				sb.append(" and dto.custCertNo like :custCertNo ");
				keyList.add("custCertNo");
				valueList.add("%"+queryBean.getCpCustCertNo()+"%");
			}
			
			//业务办理模式
			if (StringUtils.isNotBlank(queryBean.getAuditType())) {
				sb.append(" and dto.auditType like :auditType ");
				keyList.add("auditType");
				valueList.add("%"+queryBean.getAuditType()+"%");
			}
			
			//票据类型
			if (StringUtils.isNotBlank(queryBean.getBillType())) {
				sb.append(" and dto.billType = :billType ");
				keyList.add("billType");
				valueList.add(queryBean.getBillType());
			}
			//票据介质
			if (StringUtils.isNotBlank(queryBean.getBillMedia())) {
				sb.append(" and dto.billMedia = :billMedia ");
				keyList.add("billMedia");
				valueList.add(queryBean.getBillMedia());
			}
			
			if(StringUtils.isNotBlank(queryBean.getAuditStatus())){
				sb.append(" and dto.auditStatus = :auditStatus");
				keyList.add("auditStatus");
				valueList.add(queryBean.getAuditStatus());
			}else{
				sb.append(" and dto.auditStatus in(:auditStatus)");
				//审批受理状态
				stateList.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING); // 审批中  2
				stateList.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); // 提交  1
				keyList.add("auditStatus");
				valueList.add(stateList);
			}
		}else{
			sb.append(" and dto.auditStatus in(:auditStatus)");
			//审批受理状态
			stateList.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING); // 审批中  2
			stateList.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); // 提交  1
			keyList.add("auditStatus");
			valueList.add(stateList);
		}
		
		sb.append(" order by dto.openDate desc,dto.id ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		return this.find(sb.toString(), keyArray, valueList.toArray(), page);
		
	}
	
	/**
	 * 根据id查询审批信息
	 * @param id 审批id
	 * @throws Exception	 
	 * */
	
	public ApproveAuditDto queryAuditInfoById(String id) throws Exception {
		return (ApproveAuditDto)dao.load(ApproveAuditDto.class, id);
		
	}
	
	/**
	 * <p>方法说明:根据审核路线id查询配置的请求参数</p>
	 * @param approveAuditId
	 * @return list 审批路线请求参数
	 */
	public List queryApproveParamtersByApproveAuditId(String approveAuditId,Page pg) throws Exception{
		String hql="from ApproveParamterDto dto where dto.approveAuditId=? ";
		List params=new ArrayList();
		params.add(approveAuditId);
		List list;
		if(pg!=null){
			list=this.find(hql, params,pg);
		}else{
			list=this.find(hql, params);
		}
	    return list;
	}
	
	
	public List getOpenApproveAuditDto(String productId,String busiId){
		List parasNameList = new ArrayList();
		List parasValueList = new ArrayList();
		String hql ="from  ApproveAuditDto as info where 1 = 1";
		if(StringUtil.isNotEmpty(productId)){
			parasNameList.add("productId");
			parasValueList.add(productId);
			hql+=" and info.productId=:productId";
		}
		parasNameList.add("busiId");
		parasValueList.add(busiId);
		hql+=" and info.busiId =:busiId order by openDate desc";
		return  this.find(hql , (String[]) parasNameList.toArray(new String[parasNameList.size()]), parasValueList.toArray());
	}

	
	/**
	 * 根据当前用户机构信及产品信息查询提交审批时可选择的审批人
	 * @param productId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List<User> queryChooseSubmit(String productId,User user) throws Exception{
		
		List userList = new ArrayList();
		//根据产品编号查询该产品是否配置 可选审批岗
		List busiTabConfList = auditBusiTableConfigService.queryBusiTabConfigsByProductId(productId);
		if(busiTabConfList != null && busiTabConfList.size() > 0){
			BusiTableConfig busiTableConfig = (BusiTableConfig) busiTabConfList.get(0);
			if(!"true".equals(busiTableConfig.getSubmitNode())){
				return userList;
			}
		}
		String deptId = user.getDepartment().getId();
		AuditRouteDto routeDto = getAuditRouteByBrchIdAndProdId(deptId,productId);
		if(routeDto==null){
			//没有找到审批配置
			throw new Exception("该机构未分配审核路线，请与管理员联系");
		}
		//【1】查询初审审核节点
		AuditNodeDto node = null;
		String hql="from AuditNodeDto node where node.routeId=? order by node.nodeNum asc ";
		List params=new ArrayList();
		params.add(routeDto.getRouteId());
		List list1 = this.find(hql, params);
		if(list1==null || list1.size() <= 0){
			//没有找到审批配置
			throw new Exception("未找到审批节点信息");
		}else{
			node = (AuditNodeDto)list1.get(0);
		}
		//若首岗为自动节点  则不允许选择提交审批人  执行默认审批系节点
		if("1".equals(node.getNodeType())){
			return userList;
		}
		userList = this.getAuditUsersOfNextRoute(user.getDepartment(),node.getOptRole(),node.getNodeLevel());
		if(userList!=null && userList.size()>0){
			//在查询结果中排除业务申请人
			Iterator iterator = userList.iterator();
			while (iterator.hasNext()) {
				User user1 = (User)iterator.next();
				if(user1.getId().equals(user.getId())){
					iterator.remove();
				}
			}
			return userList;
		}else{
			throw new Exception("未找到审批人员");
		}
	}
	
	/**
	 * 根据当前审批信息 确定该审批流审批节点
	 * @return
	 */
	public List queryEndNodeList(ApproveAuditDto auditDto,List nodeList) throws Exception{
		List list = new ArrayList();
		Map dataMap = new HashMap();
		//根据审批路线id查询该审批路线下审批参数配置信息
		List approveParamList = queryApproveParamtersByApproveAuditId(auditDto.getId(),null);
		int paramCount = approveParamList.size();
		for(int i=0; i < paramCount; i++){
			ApproveParamterDto approveParam = (ApproveParamterDto) approveParamList.get(i);
			try {
				//数据转换
				if("String".equalsIgnoreCase(approveParam.getDataType())) {
					dataMap.put(approveParam.getParamCode(), approveParam.getParamValue());
				}else if("int".equalsIgnoreCase(approveParam.getDataType())) {
					dataMap.put(approveParam.getParamCode(), Integer.valueOf(approveParam.getParamValue()).intValue());
				}else if("BigDecimal".equalsIgnoreCase(approveParam.getDataType())) {
					dataMap.put(approveParam.getParamCode(), new BigDecimal(approveParam.getParamValue()));
				}
			}catch(Exception e){
				this.logger.error("审批路线请求参数数据类型转换错误",e);
				throw new Exception("审批路线请求参数数据类型转换错误！!");
			}
		}
		for (int i = 0; i < nodeList.size(); i++) {
			AuditNodeDto node = (AuditNodeDto)nodeList.get(i);
			String nextNodeNo = null;
			try{
				Object objNextNodeNo =(Object) MvelUtil.eval(node.getMvelExpr(),dataMap);
				nextNodeNo = String.valueOf(objNextNodeNo);
				list.add(node);
				if("-1".equals(nextNodeNo)){
					break;
				}
			}catch(Exception e1){
				this.logger.error("当前审批节点中MVEL动态表达式设定有误",e1);
				throw new Exception("当前审批节点中动态表达式规则设定不正确，请联系管理员!");
			}
		}
		
		return list;
	}
	
	
	public Class getEntityClass() {
		return AuditServiceImpl.class;
	}

	public String getEntityName() {
		return null;
	}
	public void setAuditExtendServiceFactory(
			AuditExtendServiceFactory auditExtendServiceFactory) {
		this.auditExtendServiceFactory = auditExtendServiceFactory;
	}
	
	
	
	
	
}
