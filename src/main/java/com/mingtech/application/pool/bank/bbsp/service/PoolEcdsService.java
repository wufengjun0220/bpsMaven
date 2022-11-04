package com.mingtech.application.pool.bank.bbsp.service;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;


/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 
 *
 */
@Service
public interface PoolEcdsService {
	
	public ReturnMessageNew txApplyImplawn(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 * 解质押申请输送报文字段(30610001)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
//	public ReturnMessageNew txApplyHypothecation(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 * 提示付款申请报文输送字段（30610002）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyTSPayment(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	
	/**
	 *查询票据正面信息报文输送字段（30600105）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBillFace(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *查询票据背面信息报文输送字段（30600096）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBillCon(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *电子票据交易类查询报文输送字段（30600106）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBusiness(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *电子票据交易类查询报文输送字段（30610006）(批量文件)
	 *接口可用于质押解质押签收结果查询等
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBusinessBatch(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *老票电子票据交易类查询报文输送字段（03030337）(批量文件)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBusinessBatch2(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	
	/**
	 *查询质押待签收的票据报文输送字段（30610003）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyImplawnForSign(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *查询待签收的票据报文输送字段（30600104）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
//	public ReturnMessageNew txApplyForSign(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *查询持有票据信息报文输送字段（30610004）
	 *
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyPossessBill(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *查询全量票据信息报文输送字段（30610005）(批量文件)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyFullBill(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 *统一撤销申请报文输送字段（30600122）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public boolean txApplyRevokeApply(ECDSPoolTransNotes poolNotes) throws Exception ;
	/**
	 * 老票统一撤销申请报文输送字段（03010810）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public boolean txApplyRevokeApplyOld(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	
	/**
	 *统一签收、拒绝报文输送字段（30600123）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplySignReject(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 * 贴现申请报文输送字段(30020801)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
//	public boolean txApplyDiscount(ECDSPoolTransNotes poolNotes) throws Exception;
	
	/**
	 * BBSP系统加锁接口(30020207)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public boolean txApplyLock(ECDSPoolTransNotes poolNotes) throws Exception;
	
	/**
	 * BBSP系统加锁接口(11025116)网银经办锁
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public boolean txApplyLockEbk(ECDSPoolTransNotes poolNotes) throws Exception;
	
	/**
	 * 贴现签收及记账接口(30020802)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplySignBookkeep(ECDSPoolTransNotes poolNotes) throws Exception;
	
	/**
	 * 强制贴现结果查询接口即mis同步人行交易接口(30031802)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplySynchronization(ECDSPoolTransNotes poolNotes) throws Exception;


	/**
	 * bbsp背书转让(30020007)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
//	public boolean txApplyEndorsee(ECDSPoolTransNotes poolNotes) throws Exception;
	/**
	 * @param note
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 批量新增
	 */
	public ReturnMessageNew txApplyNewBills(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 出票登记
	 */
//	public ReturnMessageNew txApplyDrawBill(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 承兑申请
	 */
//	public ReturnMessageNew txApplyAcception(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 承兑签收
	 */
	public ReturnMessageNew txApplyAcceptionSign(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 提示收票
	 */
//	public boolean txApplyRecvBill(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 记账查询
	 */
	public ReturnMessageNew txApplyQueryAcctStatus(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @date 2021-5-12
	 * @description 删除电票
	 * @param note
	 * @throws Exception 
	 */
	public ReturnMessageNew txDeleteApplyBill(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 汇票撤销
	 */
//	public ReturnMessageNew txApplyCancleBill(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @Title 统一撤销查询
	 * @author wss
	 * @date 2021-5-17
	 * @Description TODO
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
//	public ReturnMessageNew txApplyQueryCancle(ECDSPoolTransNotes note) throws Exception;
	/**
	 * @Title txApplyAcptSign
	 * @author wss
	 * @date 2021-7-14
	 * @Description 提示承兑签收
	 * @return boolean
	 */
	public boolean txApplyAcptSign(ECDSPoolTransNotes note) throws Exception;
	
	/**
	 *查询票据等分化票据查询子票区间报文输送字段（30610007）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBillRange(ECDSPoolTransNotes poolNotes) throws Exception ;
	
	/**
	 * BBSP系统查询持票id(11025129)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txQueryHildId(ECDSPoolTransNotes poolNotes) throws Exception;
	
	/**
	 * 承兑出账撤销报文输送字段（11011502）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyRevokeSign(ECDSPoolTransNotes poolNotes) throws Exception;
	
}
