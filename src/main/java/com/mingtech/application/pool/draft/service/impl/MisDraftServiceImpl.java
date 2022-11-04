/**
 * 
 */
package com.mingtech.application.pool.draft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.MisEdraft;
import com.mingtech.application.pool.draft.domain.MisPdraft;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.MisDraftService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.framework.common.util.ConnectionUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;



/**
 * @author wbyecheng
 * 
 *         票据资产入池服务实现（代保管存票、质押入池）
 * 
 */
@Service("misDraftService")
public class MisDraftServiceImpl  extends GenericServiceImpl implements MisDraftService {
	
	private static final Logger logger = Logger.getLogger(MisDraftServiceImpl.class);
	
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	
	@Override
	public List<MisEdraft> queryMisEdraftByParam(List<String> custNos) {
		if(custNos!=null &&custNos.size()>0){
			logger.debug("查询mis【"+custNos.size()+"】个客户的签发已记账的电票信息");

			List<String> custs = new ArrayList<String>();//最终要更新的客户号in 字符串
			String nos = "(";
			int count =0;
			for(String no :custNos){
				count++;
				if(count<=100){
					nos = nos+"'"+no+"',";
				}
				
				if(count>=100||(custNos.size()==count)){
					nos = nos.subSequence(0, nos.length()-1) +")";
					custs.add(nos);
					nos = "(";
					count=0;
				}
			}
			
			String inStr = "";
			if(custs!=null && custs.size()>0){
				for(String no : custs){
					inStr = inStr + " CUST_NO IN "+ no +" OR";
				}
			}
			inStr = inStr.subSequence(0, inStr.length()-2)+"";
						
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT CUST_NO,CUST_NAME,ASSET_TYPE,TRANSFER_PHASE,DRAFT_NB,DRAFT_MEDIA,DRAFT_TYPE,ISSE_AMT,ISSE_DT," +
				   "DUE_DT,ED_BAN_ENDRSMT_MK,DRWR_NM,DRWR_ACCT_ID,DRWR_ACCT_SVCR,DRWR_ACCT_SVCR_NM,PYEE_NM,PYEE_ACCT_ID," +
				   "PYEE_ACCT_SVCR,PYEE_ACCT_SVCR_NM,ACCPTR_NM,ACCPTR_ID,ACCPTR_SVCR,ACCPTR_SVCR_NM,TRUSTEESHIP_FALG,DRAFT_OWNER_STS,END_FLAG" +
			       " FROM MIS_EDRAFT WHERE ( END_FLAG IS NULL OR END_FLAG = '0' ) AND ("+ inStr+")");

		List  rslt = this.dao.SQLQuery(hql.toString());
		if(rslt!=null && rslt.size()>0){
			return rslt;
		}

		}
		return null;
		
	}


	@Override
	public List<MisPdraft> queryMisPdraftByParam(List<String> custNos) {		
		if(custNos!=null &&custNos.size()>0){
		logger.debug("查询mis【"+custNos.size()+"】个客户的签发已记账的纸票信息");

		List<String> custs = new ArrayList<String>();//最终要更新的客户号in 字符串
		String nos = "(";
		int count =0;
		for(String no :custNos){
			count++;
			if(count<=100){
				nos = nos+"'"+no+"',";
			}
			
			if(count>=100||(custNos.size()==count)){
				nos = nos.subSequence(0, nos.length()-1) +")";
				custs.add(nos);
				nos = "(";
				count=0;
			}
		}
		
		String inStr = "";
		if(custs!=null && custs.size()>0){
			for(String no : custs){
				inStr = inStr + " CUST_NO IN "+ no +" OR";
			}
		}
		inStr = inStr.subSequence(0, inStr.length()-2)+"";
					
	StringBuffer hql = new StringBuffer();
	hql.append(" SELECT CUST_NO,CUST_NAME,ASSET_TYPE,TRANSFER_PHASE,DRAFT_NB,DRAFT_MEDIA,DRAFT_TYPE,ISSE_AMT,ISSE_DT," +
			   "DUE_DT,ED_BAN_ENDRSMT_MK,DRWR_NM,DRWR_ACCT_ID,DRWR_ACCT_SVCR,DRWR_ACCT_SVCR_NM,PYEE_NM,PYEE_ACCT_ID," +
			   "PYEE_ACCT_SVCR,PYEE_ACCT_SVCR_NM,ACCPTR_NM,ACCPTR_ID,ACCPTR_SVCR,ACCPTR_SVCR_NM,TRUSTEESHIP_FALG,DRAFT_OWNER_STS,END_FLAG" +
		       " FROM MIS_PDRAFT WHERE ( END_FLAG IS NULL OR END_FLAG = '0' )  AND ("+ inStr+")");

	List  rslt = this.dao.SQLQuery(hql.toString());
	if(rslt!=null && rslt.size()>0){
		return rslt;
	}

	}
	return null;
	}
	@Override
	public void txUpdateMisPdraft(List<String> billNos) throws Exception{
		String inStr = "";
		if(billNos!=null && billNos.size()>0){
			for(String nos : billNos){
				inStr = inStr + " c.draft_nb in "+ nos +" or";
			}
		}
		inStr = inStr.subSequence(0, inStr.length()-2)+"";
		String sql = " update Mis_pdraft c set c.end_Flag ='1' where ( c.end_Flag  is null or c.end_Flag ='0' ) and ("+inStr+")";
		logger.info("更新处理完毕的Mis_edraft【电票】信息endFlag置为已处理状态，开始...");
		ConnectionUtils.toExecuteUpdateSql(sql);
		logger.info("更新处理完毕的Mis_edraft【电票】信息endFlag置为已处理状态，结束...");
	}
	
	@Override
	public void txUpdateMisEdraft(List<String> billNos) throws Exception{
		String inStr = "";
		if(billNos!=null && billNos.size()>0){
			for(String nos : billNos){
				inStr = inStr + " c.draft_nb in "+ nos +" or";
			}
		}
		inStr = inStr.subSequence(0, inStr.length()-2)+"";
		String sql = " update Mis_edraft c set c.end_Flag ='1' where ( c.end_Flag  is null or c.end_Flag ='0' ) and ("+inStr+")";
		logger.info("更新处理完毕的Mis_edraft【纸票】信息endFlag置为已处理状态,开始....");
		ConnectionUtils.toExecuteUpdateSql(sql);
		logger.info("更新处理完毕的Mis_edraft【纸票】信息endFlag置为已处理状态,结束....");

	}

	

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Ret releaseMisEdu(DraftPool draft) throws Exception {
		Ret ret = new Ret();
		Map resuMap = new HashMap();
		List<Map> reqList = new ArrayList<Map>();//实际为单条
		CreditTransNotes creditNotes = new CreditTransNotes();

		resuMap.put("billNo", draft.getAssetNb());
		reqList.add(resuMap);
		creditNotes.setReqList(reqList);//上传文件
		
		
		try {
			ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
			if (response1.isTxSuccess()) {
				
				logger.info("出池释放保贴额度");
				draft.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
				PoolQueryBean pBean = new PoolQueryBean();
				pBean.setProtocolNo(draft.getPoolAgreement());
				pBean.setBillNo(draft.getAssetNb());
				
				/********************融合改造新增 start******************************/
				pBean.setBeginRangeNo(draft.getBeginRangeNo());
				pBean.setEndRangeNo(draft.getEndRangeNo());
				/********************融合改造新增 end******************************/
				
				/*
				 * 记录保贴额度处理流水
				 */
				PedGuaranteeCredit pedCredit = poolCreditProductService.queryByBean(pBean);
				pedCredit.setStatus(PoolComm.SP_00);
				pedCredit.setCreateTime(new Date());
				this.txStore(pedCredit);
				this.txStore(draft);
				this.dao.flush();				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);

			}else {
				logger.info("票号["+draft.getAssetNb()+"]额度系统额度释放失败");
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("票号["+draft.getAssetNb()+"]额度系统额度释放失败");
				return ret;
			}
			
		} catch (Exception e) {//保贴额度释放失败
			logger.info("票号["+draft.getAssetNb()+"]额度系统额度释放失败");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票号["+draft.getAssetNb()+"]额度系统额度释放失败");
			return ret;
		}
		return ret;
		
	}


	
	
	
	
}
