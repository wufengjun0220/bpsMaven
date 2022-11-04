package com.mingtech.application.pool.bank.creditsys.handler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.framework.common.util.StringUtil;

/**
 * @Title: MIS接口 PJE008
 * @Description: 加锁/解锁 描述:加锁/解锁成功调用bbsp同步加锁/解锁,bbsp加锁/解锁失败则锁票标记回滚
 * @author xie cheng
 * @date 2019-05-23
 */
public class PJE008CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE008CreditHandler.class);
	@Autowired
	private PoolCreditService poolCreditService;
	@Autowired
	private PoolEcdsService poolEcdsService;


	/**
	 * MIS接口 PJE008 加锁/解锁
	 */
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			Map body = request.getBody();
			String ids = getStringVal(body.get("BILL_ID")); // 票据ID 可同时上传多个票据ID，通过|分隔
			String startNo = getStringVal(body.get("START_BILL_NO")); // 票据号起
			String endNo = getStringVal(body.get("END_BILL_NO")); // 票据号止
			String type = getStringVal(body.get("OPERATION_TYPE")); // 操作类型  0-锁票/经办 1-解锁/取消经办
			
			if(StringUtils.isEmpty(startNo)){
				startNo = "0";
				endNo = "0";
	        }
			
			//1.根据票据ID(来源电票系统)查询PoolBillinfo表取得需要加锁/解锁的数据
			PoolBillInfo poolListAll = poolCreditService.queryPoolBillinfoPJE008(ids,startNo,endNo);
			int count = ids.split("\\|").length;
			
			if(poolListAll ==null){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("MIS系统发送的票据信息中含有票据池系统不存在的数据信息！");
				logger.info("MIS系统发送的票据信息中含有票据池系统不存在的数据信息！");
				
			}else{	
				/*
				 * 不再调用bbsp加锁,自行加锁
				 */
				
				/*
				 * 如上注释作废，这里也通知电票系统加（这是这里的锁改的第三次） --20210916 Ju Nana
				 */
				
				/*
				 * 如上注释作废，这里老票通知电票系统加，新票不通知（这是这里的锁改的第四次） --20220915 Wufengjun
				 */
				if(poolListAll.getDraftSource() == null || poolListAll.getDraftSource().equals(PoolComm.CS01)){
					//老票
					ECDSPoolTransNotes ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
					ecdsNotes.setBillId(ids);//票据ID
					ecdsNotes.setIsLock(type);//加锁/解锁
					if (poolEcdsService.txApplyLock(ecdsNotes)){//bbsp操作成功
						
						this.doPross(poolListAll, type, ids);
						
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("操作成功!");
						logger.info("锁票成功!");
					} else {
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("BBSP锁票/解锁操作失败!");
						logger.info("BBSP锁票/解锁操作失败!");
					}
					
				}else{
					//新票不通知电票加锁
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("操作成功!");
					logger.info("锁票成功!");
					this.doPross(poolListAll, type, ids);
				}
			}
			
		} catch (Exception e) {
			logger.error("PJE008 加锁/解锁异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("加锁/解锁! 票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * 大票表及pl_pool表的加锁or解锁操作
	 * @Description TODO
	 * @author Ju Nana
	 * @param poolListAll
	 * @param type
	 * @param ids
	 * @throws Exception
	 * @date 2019-7-2下午2:36:33
	 */
	private void doPross(PoolBillInfo pool, String type,String ids) throws Exception {
		
		String[] idArr = StringUtil.splitArray(ids, "\\|");
		String lockOrKey = "";
		String lock ="";
		if(type.equals("0")){//加锁
			lockOrKey = PoolComm.BBSPLOCK_01;
			lock = PoolComm.EBKLOCK_01;
		}else{//解锁
			lockOrKey = PoolComm.BBSPLOCK_02;
			lock = PoolComm.EBKLOCK_02;
		}
		
		logger.info("大票表及pl_pool表的加锁or解锁操作");
		
		List<DraftPool> draftPools = new LinkedList<DraftPool>();
		List<PoolBillInfo> PoolBillInfos = new LinkedList<PoolBillInfo>();		
		
		
		//大票表加锁/解锁操作
//		for (PoolBillInfo bill : pool) {
			pool.setEbkLock(lock);
			PoolBillInfos.add(pool);
//		}
		
		//pl_pool表加锁/解锁操作
		PoolQueryBean queryBean = new PoolQueryBean();
		queryBean.setCirStage(PoolComm.DS_02); //融资池状态
		queryBean.setBillNo(pool.getSBillNo());
		
		queryBean.setBeginRangeNo(pool.getBeginRangeNo());
		queryBean.setEndRangeNo(pool.getEndRangeNo());
		
		List<DraftPool> dPools = poolCreditService.queryDraftInfos(queryBean, null);
		if (null != dPools && dPools.size() > 0) {
			DraftPool draftPool = dPools.get(0);
			draftPool.setLockz(lockOrKey);
			draftPools.add(draftPool);
		}
		
		poolCreditService.txStoreAll(PoolBillInfos);
		poolCreditService.txStoreAll(draftPools);
	}


}
