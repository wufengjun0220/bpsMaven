package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;

/**
 *自动任务业务处理类
 *解质押记账
 *gcj 20210513
 */


public class AutoTaskPoolOutAccService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolOutAccService.class);
	DraftPoolOutService draftPoolOutService=PoolCommonServiceFactory.getDraftPoolOutService();
	ConsignService consignService = ConsignServiceFactory.getConsignService();
	PoolCreditClientService poolCreditClientService = PoolCommonServiceFactory.getPoolCreditClientService();
	PoolCreditProductService productService = PoolCommonServiceFactory.getPoolCreditProductService();
	PedProtocolService pedProtocolService =  PoolCommonServiceFactory.getPedProtocolService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();
	DepartmentService departmentService = DepartmentServiceFactory.getDepartmentService();
	PoolCoreService poolCoreService = PoolCommonServiceFactory.getPoolCoreService();
	AutoTaskPublishService autoTaskPublishService =PoolCommonServiceFactory.getAutoTaskPublishService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();

	/**
	 * gcj 20210513 解质押记账
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		DraftPoolOut out=  draftPoolOutService.loadByOutId(busiId);
		if(null==out&&!PoolComm.CC_00.equals(out.getPlStatus())){
			this.response(Constants.TX_FAIL_CODE, "根据票号："+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "未能找到出池业务明细实体或出池状态不对", response, ret);
			return response;
		}
		
		/**
		 * 额度释放与记账已分开,记账成功与否不影响保贴额度的释放,走到这一步时表示保贴额度已释放;
		 
		
		记账失败后的重新占用保贴额度，再次记账时，直接先释放保贴额度
		
		out.setTaskDate(new Date());
		
		PoolQueryBean pBean1 = new PoolQueryBean();
		pBean1.setProtocolNo(out.getPoolAgreement());
		pBean1.setBillNo(out.getPlDraftNb());
		
		*//********************融合改造新增 start******************************//*
		pBean1.setBeginRangeNo(out.getBeginRangeNo());
		pBean1.setEndRangeNo(out.getEndRangeNo());
		*//********************融合改造新增 end******************************//*
		
		PedGuaranteeCredit pedCredit1 = productService.queryByBean(pBean1);
		
		PoolQueryBean bean1 = new PoolQueryBean();
		bean1.setBillNo(out.getPlDraftNb());
		bean1.setSStatusFlag(PoolComm.DS_03);
		
		*//********************融合改造新增 start******************************//*
		bean1.setBeginRangeNo(out.getBeginRangeNo());
		bean1.setEndRangeNo(out.getEndRangeNo());
		*//********************融合改造新增 end******************************//*
		
		DraftPool pool1=consignService.queryDraftByBean(bean1).get(0);
		String txFlag1 = pool1.getTXFlag();
		
		logger.info("票据【"+pool1.getAssetNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "出池处理开始......");
		//---------回滚使用数据------- 后期移除 额度释放跟记账分开
		logger.info("票据【"+pool1.getAssetNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "的保贴额度占用标识【"+out.getBtFlag()+"】，贴现标识【"+txFlag1+"】");
		//新增MIS保贴接口	注意：当强制贴现的票据时，不释放原占用额度5
		if(null!=pedCredit1 && PoolComm.SP_01.equals(pedCredit1.getStatus()) && !(PoolComm.TX_FLAG_1).equals(txFlag1)){//占用成功			
			logger.info("票据【"+pool1.getAssetNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "释放商票保贴or银票额度释放处理开始......");
			Map resuMap1 = new HashMap();
			List<Map> reqList1 = new ArrayList<Map>();//实际为单条
			CreditTransNotes creditNotes1 = new CreditTransNotes();
			resuMap1.put("billNo", out.getPlDraftNb());
			
			resuMap1.put("beginRangeNo", out.getBeginRangeNo());
			resuMap1.put("endRangeNo", out.getEndRangeNo());
			
			
			reqList1.add(resuMap1);
			creditNotes1.setReqList(reqList1);//上传文件
			ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes1);
			if(response1.isTxSuccess()){
				out.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
				PoolQueryBean poolQueryBean1 = new PoolQueryBean();
				poolQueryBean1.setBillNo(out.getPlDraftNb());
				poolQueryBean1.setSStatusFlag(PoolComm.DS_03);
				
				*//********************融合改造新增 start******************************//*
				poolQueryBean1.setBeginRangeNo(out.getBeginRangeNo());
				poolQueryBean1.setEndRangeNo(out.getEndRangeNo());
				*//********************融合改造新增 end******************************//*
				
				DraftPool dpool=consignService.queryDraftByBean(poolQueryBean1).get(0);
				dpool.setBtFlag(PoolComm.SP_00);
				pedCredit1.setStatus(PoolComm.SP_00);
				pedCredit1.setCreateTime(new Date());
				draftPoolOutService.txStore(pedCredit1);
				draftPoolOutService.txStore(out);
				draftPoolOutService.txStore(dpool);
				this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "额度系统额度释放成功！", response, ret);
			}else{
				this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "额度系统额度释放失败,额度系统错误："+response1.getRet().getRET_MSG(), response, ret);	
				return response;
			}
		}*/
		
		
		
		if(null!=queryType){
			/**
			 * 查询记账是否已经记完账  查证系统待开发
			 * 记账失败情况 会占用额度  记账前需要重新释放 （1记账成功占用失败可以不需要释放2记账失败额度占用成功 需要先释放额度） 额度查询暂无接口 必要!
			 */
			ReturnMessageNew res=this.doAccountConfirm(out);
			if(res.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){//查证成功
				/**
				 *  唤醒解质押申请子任务。。。。
				 */
				Map<String, String> reqParams =new HashMap<String,String>();
		    	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLOUT_SEND_TASK_NO, out.getId(), AutoTaskNoDefine.BUSI_TYPE_JSQ,reqParams);
		    	this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】记账成功！", response, ret);
				return response;
			}
		}
		
		out.setTaskDate(new Date());
		
		/**
		 * 调取出池记账接口
		 */
		PoolQueryBean bean = new PoolQueryBean();
		bean.setBillNo(out.getPlDraftNb());
		bean.setSStatusFlag(PoolComm.DS_03);
		
		/********************融合改造新增 start******************************/
		bean.setBeginRangeNo(out.getBeginRangeNo());
		bean.setEndRangeNo(out.getEndRangeNo());
		/********************融合改造新增 end******************************/
		
		DraftPool pool=consignService.queryDraftByBean(bean).get(0);
		logger.info("票据【"+pool.getAssetNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "出池调用核心记账处理开始......");
		
		CoreTransNotes transNotes = new CoreTransNotes();
		PedProtocolDto dto = (PedProtocolDto) pedProtocolService.queryProtocolDto(null, null, out.getPoolAgreement(), null, null, null);
		
		transNotes.setBilCode(out.getPlDraftNb());//票号
		
		
		String colNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);//流水号
		
		String OutcolNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);//流水号
		
		if(StringUtils.isNotBlank(out.getSplitId())){
			/**
			 * 原始未拆分数据
			 */
			PoolBillInfo info = (PoolBillInfo) pedProtocolService.load(out.getSplitId(),PoolBillInfo.class);//
			if(StringUtils.isNotBlank(info.getDraftSource()) && !info.getDraftSource().equals(PoolComm.CS01)){
				transNotes.setBeginRangeNo(info.getBeginRangeNo());
				transNotes.setEndRangeNo(info.getEndRangeNo());
			}
			transNotes.setAmt(BigDecimalUtils.setScale(out.getPlIsseAmt()).toString());
			
			transNotes.setColNo(out.getGuaranteeNo());//担保编号
			transNotes.setInColNo(colNo);//入库担保编号
			transNotes.setOutColNo(OutcolNo);//出库担保编号
		}else{
			/**
			 * 原始未拆分数据
			 */
			transNotes.setBeginRangeNo(pool.getBeginRangeNo());
			transNotes.setEndRangeNo(pool.getEndRangeNo());
			transNotes.setAmt(BigDecimalUtils.setScale(out.getPlIsseAmt()).toString());
			
			transNotes.setColNo(out.getGuaranteeNo());//担保编号
		}
		
		if(out.getDevSeqNo() != null && !"".equals(out.getDevSeqNo())){
			transNotes.setDevSeqNo(out.getDevSeqNo());//第三方流水号
		}else{
			String str = poolBatchNoUtils.txGetFlowNo();
			transNotes.setDevSeqNo(str);//第三方流水号
			out.setDevSeqNo(str);//保存流水号
			draftPoolOutService.txStore(out);
		}
		transNotes.setRemark("解质押");
		//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
		if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
			transNotes.setBrcNo(dto.getCreditDeptNo());//融资机构号
		}else{
			transNotes.setBrcNo(dto.getOfficeNet());//受理网点
		}
		Department dept = departmentService.getDepartmentByInnerBankCode(transNotes.getBrcNo());
		transNotes.setBrcBld(dept.getAuditBankCode());//报文头需赋值的机构
		/*
		 * 出池记账操作
		 */
		ReturnMessageNew resp = poolCoreService.PJH580316Handler(transNotes);
		if(resp.isTxSuccess()){//记账成功
			/**
			 * 记录拆分入库的担保品编号
			 */
			if(StringUtils.isNotBlank(out.getSplitId())){
				PoolBillInfo info = draftPoolInService.loadBySplit(out.getSplitId(), "DS_02");

				bean = new PoolQueryBean();
				bean.setBillNo(out.getPlDraftNb());
				bean.setSStatusFlag(PoolComm.DS_02);
				
				/********************融合改造新增 start******************************/
				bean.setBeginRangeNo(info.getBeginRangeNo());
				bean.setEndRangeNo(info.getEndRangeNo());
				/********************融合改造新增 end******************************/
				
				DraftPool draft=consignService.queryDraftByBean(bean).get(0);
				draft.setGuaranteeNo(colNo);
				draftPoolOutService.txStore(draft);
			}
			
			out.setPlStatus(PoolComm.CC_01);
			out.setHostSeqNo((String)response.getBody().get("HOST_SEQ_NO"));//记账完成后记录
			draftPoolOutService.txStore(out);
			this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】记账成功！", response, ret);
		}else{
			logger.info("AutoPoolOutNO1Task出池记账失败，核心返回错误："+response.getRet().getRET_MSG());
			/*
			 * 如果发生释放则重新占用
			 */

				logger.error("出池记账失败，重新占用额度系统已释放额度...");
				ReturnMessageNew result = null;
				String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//财票改造开关 
				if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){
					
					PoolBillInfo bill = draftPoolInService.loadByBillNo(out.getPlDraftNb(), out.getBeginRangeNo(), out.getEndRangeNo());
					
					result = draftPoolInService.txMisCreditOccupy(bill);
					
				}else{					
					result = this.doPJE012(out);
				}
				
				/*
				 * 回滚落库操作
				 */
				PoolQueryBean pBean = new PoolQueryBean();
				pBean.setProtocolNo(out.getPoolAgreement());
				pBean.setBillNo(out.getPlDraftNb());
				
				/********************融合改造新增 start******************************/
				pBean.setBeginRangeNo(out.getBeginRangeNo());
				pBean.setEndRangeNo(out.getEndRangeNo());
				/********************融合改造新增 end******************************/
				
				PedGuaranteeCredit pedCredit = productService.queryByBean(pBean);
				if(result.isTxSuccess()){
					pedCredit.setStatus(PoolComm.SP_01);
					pedCredit.setCreateTime(new Date());
					pool.setBtFlag(PoolComm.SP_01);
					out.setBtFlag(PoolComm.SP_01);//保贴额度占用成功
				}else{
					pedCredit.setStatus(PoolComm.SP_00);
					pedCredit.setCreateTime(new Date());
					pool.setBtFlag(PoolComm.SP_00);
					out.setBtFlag(PoolComm.SP_00);//保贴额度占用失败
				}
				draftPoolOutService.txStore(pedCredit);
				draftPoolOutService.txStore(pool);
				draftPoolOutService.txStore(out);
				
				
				
				this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "记账失败，额度重新占用！", response, ret);
				return response;
			}
		
		/**
		 *  唤醒解质押申请子任务。。。。
		 */
		Map<String, String> reqParams =new HashMap<String,String>();
    	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLOUT_SEND_TASK_NO, out.getId(), AutoTaskNoDefine.BUSI_TYPE_JSQ,reqParams);

		return response;
		}

	


	/**
	 * 额度系统额度占用
	 * @param out
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2019-11-20下午2:40:49
	 */
	private ReturnMessageNew doPJE012(DraftPoolOut out) throws Exception{
		String media = out.getPlDraftMedia();//票据介质
		String type = out.getPlDraftType();//票据类型
		String billType ="";//票据种类  01 纸质银票 02 纸质商票 03电子银票  04电子商票
		if(PoolComm.BILL_MEDIA_PAPERY.equals(media) && PoolComm.BILL_TYPE_BANK.equals(type)){//纸质银票
			billType = "01";
		}
		if(PoolComm.BILL_MEDIA_PAPERY.equals(media)&&PoolComm.BILL_TYPE_BUSI.equals(type)){//纸质商票
			billType = "02";
		}
		if(PoolComm.BILL_MEDIA_ELECTRONICAL.equals(media)&&PoolComm.BILL_TYPE_BANK.equals(type)){//电子银票
			billType = "03";
		}
		if(PoolComm.BILL_MEDIA_ELECTRONICAL.equals(media)&&PoolComm.BILL_TYPE_BUSI.equals(type)){//电子商票
			billType = "04";
		}
		
		Map resuMap = new HashMap();
		List<Map> reqList = new ArrayList<Map>();//实际为单条
		CreditTransNotes creditNotes = new CreditTransNotes();
	
		resuMap.put("billNo", out.getPlDraftNb());//票号                    
		resuMap.put("billsum", out.getPlIsseAmt());//票面金额               
		resuMap.put("currency", "156");//币种:人民币                  
		resuMap.put("billType", billType);//票据种类              
		resuMap.put("billBusinessType", "02");//票据业务类型  01 贴现 02 质押  03 转贴现
	
		resuMap.put("customerId", "");//承兑人核心客户号    
		resuMap.put("bankId", "");//承兑人二代支付系统行号  
		resuMap.put("customerName", out.getPlAccptrNm());//承兑人名称        
		resuMap.put("execNominalAmount", out.getPlIsseAmt());//占用名义金额 
		
		BigDecimal amt = BigDecimal.ZERO;//占用敞口金额:银票输0；商票输票面金额
		if(PoolComm.BILL_TYPE_BUSI.equals(type)){//商票
			amt = out.getPlIsseAmt();
		}
		resuMap.put("execExposureAmount", amt);//占用敞口金额
	
		reqList.add(resuMap);
		creditNotes.setReqList(reqList);//上传文件
		
		/*
		 *额度系统额度占用 
		 */
		ReturnMessageNew response1 = poolCreditClientService.txPJE012(creditNotes);
		return response1;
	 }
	
	/**
	 * 出池核心记账查证
	 * @param poolIn
	 * @param dto
	 * @throws Exception
	 * @author gcj
	 * @date 20210524
	 */
	private ReturnMessageNew doAccountConfirm(DraftPoolOut out) throws Exception{
		
		logger.info("出池电票"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "查证-----------------开始...");
		ReturnMessageNew res=new ReturnMessageNew();
		Ret ret = new Ret();
		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setSerSeqNo(out.getDevSeqNo());//流水号
		transNotes.setAcctDate(DateUtils.formatDate(new Date(),DateUtils.ORA_DATE_FORMAT));//记账日期
		/*
		 * 查证记账操作
		 */
		String status ="";
		ReturnMessageNew response  = poolCoreService.CORE002Handler(transNotes);
		if(response.isTxSuccess()){//查证返回成功
			this.response(Constants.TX_SUCCESS_CODE, "票号为["+transNotes.getBilCode()+"],票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "的票"+"查证成功,不用再次记账", response, ret);
		}else{
			   this.response(Constants.TX_FAIL_CODE, "票号为["+transNotes.getBilCode()+"],票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "的票"+"查证失败", response, ret);
		}
		return response;
	}
	
	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}
}
