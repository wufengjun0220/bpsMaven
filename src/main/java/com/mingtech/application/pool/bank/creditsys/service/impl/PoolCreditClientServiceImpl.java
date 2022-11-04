package com.mingtech.application.pool.bank.creditsys.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.bbsp.service.impl.PoolEcdsServiceImpl;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.creditsys.client.MisClient;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.HKBConstants;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.BankMember;
import com.mingtech.application.pool.common.domain.CpesBranch;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.query.service.impl.CommonQueryServiceImpl;

/**
 *  MIS系统作为服务端的处理实现类
 *  
 * @author Ju Nana
 * @version v1.0
 * @date 2019-10-29
 */
@Service("poolCreditClientService")
public class PoolCreditClientServiceImpl implements PoolCreditClientService {
	
	private static final Logger logger = Logger.getLogger(PoolEcdsServiceImpl.class);
	
	@Autowired
	MisClient misClient;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private CommonQueryServiceImpl commonQueryServiceImpl;
	
	@Override
	public ReturnMessageNew txPJE011(List<PoolBillInfo> list) throws Exception{
		
	logger.info("PJE011查询额度系统额度开始.....");

		ReturnMessageNew request = new ReturnMessageNew();
    	Map map = null;
		List details =new ArrayList();
		for(int i=0;i<list.size();i++){
    		map = new HashMap();
    		PoolBillInfo pool= (PoolBillInfo) list.get(i);
    		if(PoolComm.BILL_TYPE_BANK.equals(pool.getSBillType())){
        		map.put("queryType", "01");//查詢类型  01银票   02 商票
        		if(StringUtils.isBlank(pool.getSAcceptorBankName())){
        			//查询名称上送
        			String name = commonQueryServiceImpl.queryAcceptNameJson(pool.getSAcceptorBankCode());
        			if(StringUtils.isNotEmpty(name)){
        				map.put("customerName", name);//客户名称
        			}
//        			ProtocolQueryBean bean = new ProtocolQueryBean();
//        			bean.setTransBrchBankNo(pool.getSAcceptorBankCode());
//        			List res = blackListManageService.loadCpesBranch(bean,null,null);
//        			if(res != null && res.size() > 0){
//        				CpesBranch cpes = (CpesBranch) res.get(0);
//        				map.put("customerName", cpes.getBrchFullNameZh());//客户名称
//        			}
        		}else{
        			map.put("customerName", pool.getSAcceptorBankName());//客户名称
        		}
        		map.put("bankId", pool.getSAcceptorBankCode());//支付系统行行号
        	
    		}else{
    			map.put("queryType", "02");//查詢类型  01银票   02 商票
        		map.put("customerName", pool.getSAcceptor());//客户名称
        		map.put("bankId", pool.getSAcceptorBankCode());//支付系统行行号
    		}
    		if(pool.getSplitFlag().equals("1")){
    			//可拆分的等分化票据
    			map.put("billNo", pool.getSBillNo()+"-"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo());//票号/票据包号
    		}else{
    			map.put("billNo", pool.getSBillNo());//票号/票据包号
    		}
    		details.add(map);
    	}

		String path = "";
		String remark = "";
		if(details.size() >0 ){//存在文件
			path = poolBatchNoUtils.txGetBatchNo("/BPS/PJE011", 6);
			remark = HKBConstants.FILE_FLAG_1;
			request.setDetails(details);
			logger.info("需要上传的文件的长度为【"+details.size()+"】文件名称为【"+path+"】");
		}else {//不存在文件
			remark = HKBConstants.FILE_FLAG_0;
		}
		request.getFileHead().put("FILE_PATH", path+".txt");//文件路径 
		request.getFileHead().put("FILE_FLAG", remark);//文件路上传标识
		
		ReturnMessageNew response = misClient.processECDS("PJE011", request);
		String responseCode = response.getRet().getRET_CODE();
		logger.info("PJE011查询额度系统额度结束，查询结果："+responseCode);
		
		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			response.setTxSuccess(true);
			return response;  
		}else {
			response.setTxSuccess(false);
			return response;
		}
	}

	@Override
	public ReturnMessageNew txPJE012(CreditTransNotes creditNotes)
			throws Exception {
		
		logger.info("PJE012占用额度系统额度开始.....");
		
		if (creditNotes == null) {
			logger.error("txPJE012:票据池参数组装错误！");
			throw new Exception("txPJE012:票据池参数组装错误");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		String path = "";
		String remark = "";
		
		if(creditNotes.getReqList().size() >0 ){//存在文件
			path = poolBatchNoUtils.txGetBatchNo("/BPS/PJE012", 6);
			remark = HKBConstants.FILE_FLAG_1;
			request.setDetails(creditNotes.getReqList());
			logger.info("需要上传的文件的长度为【"+creditNotes.getReqList().size()+"】文件名称为【"+path+"】");
		}else {//不存在文件
			remark = HKBConstants.FILE_FLAG_0;
		}
		request.getFileHead().put("FILE_PATH", path+".txt");//文件路径 
		request.getFileHead().put("FILE_FLAG", remark);//文件路上传标识
		
		ReturnMessageNew response = misClient.processECDS("PJE012", request);
		
		String responseCode = response.getRet().getRET_CODE();
		
		logger.info("PJE012占用额度系统额度结束，占用结果："+responseCode);
		
		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			response.setTxSuccess(true);
			return response;  
		}else {
			response.setTxSuccess(false);
			return response;
		}
		
	}

	@Override
	public ReturnMessageNew txPJE013(CreditTransNotes creditNotes)
			throws Exception {
		
		logger.info("PJE013释放额度系统额度开始.....");
		
		if (creditNotes == null) {
			logger.error("txPJE013:票据池参数组装错误！");
			throw new Exception("txPJE013:票据池参数组装错误");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		String path = "";
		String remark = "";
		if(creditNotes.getReqList().size() >0 ){//存在文件
			path = poolBatchNoUtils.txGetBatchNo("/BPS/PJE013", 6);
			remark = HKBConstants.FILE_FLAG_1;
			request.setDetails(creditNotes.getReqList());
			logger.info("需要上传的文件的长度为【"+creditNotes.getReqList().size()+"】文件名称为【"+path+"】");
		}else {//不存在文件
			remark = HKBConstants.FILE_FLAG_0;
		}
		request.getFileHead().put("FILE_PATH", path+".txt");//文件路径 
		request.getFileHead().put("FILE_FLAG", remark);//文件路上传标识
		
		
		ReturnMessageNew response = misClient.processECDS("PJE013", request);
		String responseCode = response.getRet().getRET_CODE();
		
		
		logger.info("PJE013释放额度系统额度结束，释放结果："+responseCode);
		
		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			response.setTxSuccess(true);
			return response;  
		}else {
			response.setTxSuccess(false);
			return response;
		}
		
	}

	
	/**
	 * @author gcj
	 * @date 2021-7-19
	 * @description 业务模式变更通知
	 * @param CreditTransNotes
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE018(CreditTransNotes note) throws Exception {
		ReturnMessageNew request =  new ReturnMessageNew();
		request.getBody().put("BPS_NO", note.getBpsNo());//票据池编号
		request.getBody().put("BUSS_MODE", note.getPoolMode());//票据池模式
		ReturnMessageNew response = misClient.processECDS("PJE018", request);
		String responseCode = response.getRet().getRET_CODE();
		logger.info("PJE018额度模式变更结果："+responseCode);
		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			response.setTxSuccess(true);
			return response;  
		}else {
			response.setTxSuccess(false);
			return response;  
		}
	}

	@Override
	public ReturnMessageNew txPJE021(CreditTransNotes note) throws Exception {
		 
		ReturnMessageNew request =  new ReturnMessageNew();
		request.getBody().put("ONLINE_PROTOCOL_NO", note.getOnlineNo());//在线协议编号
		request.getBody().put("RELATE_LIMIT_NO", note.getoNlineCreditNo());//关联额度编号
		request.getBody().put("CORE_CLIENT_NO", note.getCustomerId());//核心客户号
		request.getBody().put("OPERATION_TYPE", note.getOperationType());//操作类型
		request.getBody().put("ONLINE_PROTOCOL_TYPE", note.getOnlineType());//在线协议类型
		request.getBody().put("CONTRACT_NO", note.getContractNo());//合同编号
		request.getBody().put("CCY", "01");//币种
		request.getBody().put("EFFECTIVE_DATE", note.getEFFECTIVE_DATE());//生效日期
		request.getBody().put("EXPIRY_DATE", note.getEXPIRY_DATE());//到期日
		request.getBody().put("CONTRACT_AMT", note.getBusinessSum());//合同金额
		
		ReturnMessageNew response = misClient.processECDS("PJE021", request);
		String responseCode = response.getRet().getRET_CODE();
//		ReturnMessageNew response=new ReturnMessageNew();
//		response.setRet(new Ret());
//		String responseCode=Constants.TX_SUCCESS_CODE;
		logger.info("PJE021风险探测（含额度校验/占用）结果："+responseCode);

		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			 List detail=response.getDetails();
			 if(null != detail && detail.size()>0){
				 String result="";
			     for(int i=0;i<detail.size();i++){
			    	 Map map=(Map)detail.get(i);
			    	 String checkCode=(String)map.get("CHECK_RESULT_ARRAY.CHECK_CODE");
			    	 String checkResult=(String)map.get("CHECK_RESULT_ARRAY.CHECK_RESULT");
			    	 result += checkCode+"-"+checkResult+"|";
			    	 response.getRet().setRET_MSG("信贷系统风险探测不通过："+result);
			    	 response.setTxSuccess(false); 
			     }
			 }else{//如果额度系统返回的报文中没有detail表示：风险校验通过/额度占用成功/额度释放成功
				 response.getRet().setRET_MSG("PJE021额度系统处理返回成功");
				 response.setTxSuccess(true); 
			 }
		     
			return response;  
		}else {
			response.setTxSuccess(false);
			return response;
		}
	}

	@Override
	public ReturnMessageNew txPJE022(CreditTransNotes note) throws Exception {
		
	logger.info("PJE022查询MIS系统额度开始.....");

		ReturnMessageNew request = new ReturnMessageNew();
		List<Map>  reqList = note.getReqList();


		String path = "";
		String remark = "";
		if(reqList.size() >0 ){//存在文件
			path = poolBatchNoUtils.txGetBatchNo("/BPS/PJE022", 6);
			remark = HKBConstants.FILE_FLAG_1;
			request.setDetails(reqList);
			logger.info("需要上传的文件的长度为【"+reqList.size()+"】文件名称为【"+path+"】");
		}else {//不存在文件
			remark = HKBConstants.FILE_FLAG_0;
		}
		request.getFileHead().put("FILE_PATH", path+".txt");//文件路径 
		request.getFileHead().put("FILE_FLAG", remark);//文件路上传标识
		
		ReturnMessageNew response = misClient.processECDS("PJE022", request);
		String responseCode = response.getRet().getRET_CODE();
		
		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			response.setTxSuccess(true);
			return response;  
		}else {
			response.setTxSuccess(false);
			return response;
		}
	}
	
	@Override
	public ReturnMessageNew txPJE023(CreditTransNotes note) throws Exception {
		
		ReturnMessageNew response = new ReturnMessageNew();
		try {
			logger.info("PJE023占用额度系统额度开始.....");
			String billClass = null;
			String billType = note.getBillType();//票据类型
			String billMedia = note.getBillMedia();//票据介质
			if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
				if(PoolComm.BILL_MEDIA_PAPERY.equals(billMedia)){//纸票
					billClass = "02";//纸质商票
				}else{//电票
					billClass = "04";//电子商票
				}
			}else{//银票
				if(PoolComm.BILL_MEDIA_PAPERY.equals(billMedia)){//纸票
					billClass = "01";//纸质银票
				}else{//电票
					billClass = "03";//电子银票
				}
			}
			ReturnMessageNew request =  new ReturnMessageNew();
			request.getBody().put("BILL_NO", note.getBillNo());//票号            
			request.getBody().put("BILL_AMT", note.getBillsum());//票面金额           
			request.getBody().put("BILL_CLASS", billClass);//票据类型         
			request.getBody().put("CNAPS2_BANK_CODE", note.getBankId());//二代支付系统行号   
			request.getBody().put("ACCEPTOR_NAME", note.getCustomerName());//承兑人名称      
			request.getBody().put("ACCEPTOR_CLIENT_NO", note.getCORE_CLIENT_NO());//承兑人核心客户号 
			
			/********************融合改造新增 start******************************/
			request.getBody().put("BeginRangeNo",note.getBeginRangeNo());//票据开始子区间号
			request.getBody().put("EndRangeNo",note.getEndRangeNo());//票据结束子区间号
			/********************融合改造新增 end******************************/
			
			response = misClient.processECDS("PJE023", request);
			
			String responseCode = response.getRet().getRET_CODE();
			
			if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
				response.setTxSuccess(true);
				return response;  
			}else {
				logger.info("票据【"+note.getBillNo()+"】额度系统额度占用失败:"+response.getRet().getRET_MSG());
				response.setTxSuccess(false);
				return response;  
			}
		} catch (Exception e) {
			logger.info("票据【"+note.getBillNo()+"】额度系统额度占用异常:",e);
			response.setTxSuccess(false);
			return response;  
		}
		
		
	}
	
	
	@Override
	public ReturnMessageNew txPJE024(CreditTransNotes note) throws Exception {		 
		ReturnMessageNew request =  new ReturnMessageNew();
		if(null!=note.getMemberId() && !StringUtils.isBlank(note.getMemberId())){
			request.getBody().put("MEMBER_ID", note.getMemberId());//会员代码	
		}
		ReturnMessageNew response = misClient.processECDS("PJE024", request);
		String responseCode = response.getRet().getRET_CODE();
		
		logger.info("承兑行高低风险会员信息同步结果："+responseCode);
		try {
			
			if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
				response.setTxSuccess(true);				
			}else {
				response.setTxSuccess(false);				
			}
		} catch (Exception e) {
			response.setTxSuccess(false);
			response.getRet().setRET_CODE(Constants.TX_FAIL_CODE);
			response.getRet().setRET_MSG("同步MIS系统承兑行高低风险会员信息异常！");
		}
		return response;
	}
	/**
	 * @author gcj
	 * @date 2021-8-17
	 * @description MIS保贴信息查询
	 * @param CreditTransNotes
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE025(CreditTransNotes note) throws Exception {		 
		ReturnMessageNew request =  new ReturnMessageNew();
		request.getBody().put("CLIENT_NAME", note.getGuarantDiscNo());//保贴人名称	
		ReturnMessageNew response = misClient.processECDS("PJE025", request);
		String responseCode = response.getRet().getRET_CODE();
		
		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			   response.getRet().setRET_MSG("MIS保贴信息查询成功");
			   response.setTxSuccess(true);
			   return response;  	   
		}else {
			  response.setTxSuccess(false);
			  return response;
		}
	}
	
	@Override
	public ReturnMessageNew txPJE027(CreditTransNotes note) throws Exception {
		
		ReturnMessageNew response = new ReturnMessageNew();
		try {
			logger.info("PJE027占用额度系统额度开始.....");
			
			ReturnMessageNew request =  new ReturnMessageNew();
			request.setDetails(note.getReqList());
			
			response = misClient.processECDS("PJE027", request);
			
			String responseCode = response.getRet().getRET_CODE();
			
			if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
				response.setTxSuccess(true);
				return response;  
			}else {
				logger.info("票据【"+note.getBillNo()+"】额度系统额度占用失败:"+response.getRet().getRET_MSG());
				response.setTxSuccess(false);
				return response;  
			}
		} catch (Exception e) {
			logger.info("票据【"+note.getBillNo()+"】额度系统额度占用异常:",e);
			response.setTxSuccess(false);
			return response;  
		}
		
		
	}
	
	@Override
	public ReturnMessageNew txPJE028(CreditTransNotes note) throws Exception {
		
		ReturnMessageNew response = new ReturnMessageNew();
		try {
			logger.info("PJE028在线流程流贷合同金额变更接口开始.....");
			
			ReturnMessageNew request =  new ReturnMessageNew();
			
			request.getBody().put("CONTRACT_NO", note.getContractNo());//合同编号
			request.getBody().put("EXPEND_FAIL_AMT", note.getExpendFailAmt());//出账失败金额
			request.getBody().put("UN_USED_RETURN_AMT", note.getUnUsedReturnAmt());//未用退回金额
			request.getBody().put("ORI_CONTRACT_AMT", note.getOriContractAmt());//原合同金额
			
			response = misClient.processECDS("PJE028", request);
			
			String responseCode = response.getRet().getRET_CODE();
			
			if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
				response.setTxSuccess(true);
				return response;  
			}else {
				logger.info("合同编号【"+note.getContractNo()+"】在线流程流贷合同金额变更失败:"+response.getRet().getRET_MSG());
				response.setTxSuccess(false);
				return response;  
			}
		} catch (Exception e) {
			logger.info("合同编号【"+note.getContractNo()+"】在线流程流贷合同金额变更异常:",e);
			response.setTxSuccess(false);
			return response;  
		}
		
		
	}
}
