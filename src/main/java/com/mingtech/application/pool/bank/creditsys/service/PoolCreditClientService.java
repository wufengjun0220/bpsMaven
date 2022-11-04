package com.mingtech.application.pool.bank.creditsys.service;

import java.util.List;

import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.domain.PoolBillInfo;

/**
 * MIS系统作为服务端的处理接口
 * @author Ju Nana
 * @version v1.0
 * @date 2019-10-29
 */

public interface  PoolCreditClientService {
	
	/**
	 * 客户额度信息查询 批量
	 * @author gcj
	 * @param list 大票表list
	 * @return
	 * @throws Exception
	 * @date 20210525
	 */
	public ReturnMessageNew txPJE011(List<PoolBillInfo> list) throws Exception;
	
	/**
	 * 票据额度占用
	 * @author Ju Nana
	 * @param creditNotes
	 * @return
	 * @throws Exception
	 * @date 2019-10-30上午10:07:17
	 */
	public ReturnMessageNew txPJE012(CreditTransNotes creditNotes) throws Exception;
	
	/**
	 * 票据额度释放
	 * @author Ju Nana
	 * @param creditNotes
	 * @return
	 * @throws Exception
	 * @date 2019-10-30上午10:07:35
	 */
	public ReturnMessageNew txPJE013(CreditTransNotes creditNotes) throws Exception;
	
	/**
	 * @author gcj
	 * @date 2021-7-19
	 * @description 业务模式变更通知
	 * @param CreditTransNotes
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE018(CreditTransNotes note) throws Exception;
	/**
	 * @author wss
	 * @date 2021-5-10
	 * @description 风险探测（含额度校验/占用）
	 * @param batch
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE021(CreditTransNotes note) throws Exception;
	
	/**
	 * 额度系统额度查询
	 * @param list
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-31上午11:53:48
	 */
	public ReturnMessageNew txPJE022(CreditTransNotes note) throws Exception;
	
	/**
	 * 额度系统额度占用
	 * @param note
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-30上午9:41:46
	 */
	public ReturnMessageNew txPJE023(CreditTransNotes note) throws Exception;
	
	/**
	 * @author gcj
	 * @date 2021-8-17
	 * @description 系统承兑行高低风险会员信息同步接口
	 *  MEMBER_ID 不传时候全量返回全量有效的同业承兑行信息
	 * @param CreditTransNotes
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE024(CreditTransNotes note) throws Exception;
	/**
	 * @author gcj
	 * @date 2021-8-17
	 * @description MIS保贴信息查询
	 * @param CreditTransNotes
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE025(CreditTransNotes note) throws Exception;
	
	/**
	 * @author wfj
	 * @date 2022-4-21
	 * @description MIS保贴信息占用:大票换小票
	 * @param CreditTransNotes
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE027(CreditTransNotes note) throws Exception;
	
	/**
	 * @author wfj
	 * @date 2022-4-21
	 * @description MIS在线银承流贷合同变更接口
	 * @param CreditTransNotes
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE028(CreditTransNotes note) throws Exception;
}
