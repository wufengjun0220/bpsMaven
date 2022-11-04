/**
 * 
 */
package com.mingtech.application.pool.draft.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolInBatch;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author wbyecheng
 * 
 * 票据资产入池服务（代保管存票、质押入池）
 *
 */
public interface DraftPoolInService extends GenericService {

	public DraftPoolInBatch load(String id) ;

	
	/**
	 * 通过票号获取池内票据
	 * @param DraftNb 票号
	 * @param startNo 子票区间起
	 * @param endNo 子票区间止
	 * @return
	 * @throws Exception
	 */
	public DraftPool getDraftPoolByDraftNb(String DraftNb,String startNo, String endNo) throws Exception;
	/**
	 * 通过票号List获取池内票据
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	public List<DraftPool> getDraftPoolByDraftNbList(List draftNbList) throws Exception;

	/**
	 * 通过票号获取入池对象
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	public DraftPoolIn getDraftPoolInByDraftNb(String DraftNb,String beginRangeNo, String endRangeNo) throws Exception;
	

	/**
	 * 票据入池
	 * 
	 * 记账-发送记账通知 单笔票据记账
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public String txSendPoolNotify(DraftPoolIn  poolIn,User user) throws Exception;
	
	/**
	* <p>方法名称: loadByBillNo|描述: 根据票据号码查询票据信息对象</p>
	* @param billNo 票据号码
	* @param startNo 票据起始号
	* @param endNo 票据截至号
	* @return
	*/
	public PoolBillInfo loadByBillNo(String billNo,String startNo, String endNo) throws Exception;

	/**
	 *  根据ID查询DraftPoolIn
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DraftPoolIn loadByPoolInId(String id) throws Exception;
	
	/**
	 * 通过票号状态获取入池对象
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	public DraftPoolIn getDraftPoolInByDraftNb(String DraftNb,String plstatus,String beginRangeNo, String endRangeNo) throws Exception;
	
	/**
	 * 自动入池
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-24下午7:04:01
	 */
	public void txAutoInPool() throws Exception ;
	
	/**
	 * 自动入池落库任务
	 * 1.查询所有签自动质押入池的单户及集团成员的电票签约账号
	 * 2.根据电票签约账号查询BBSP持有票据
	 * 3.进行黑名单及风险校验后落库
	 * @param isAutoInPool 是否自动入池
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-25下午3:10:14
	 */
	public void  txQueryAllBillFromBbsp(boolean isAutoInPool) throws Exception;
	
	/**
	 * 调用BBSP系统接口查询持有票
	 * @author Ju Nana
	 * @param accNos 电票签约账号集合
	 * @param billMap 大票表中已存在的DS_00初始化的票，key为票号，value为大票表对象
	 * @param accMap 电票签约账号与协议的对应map，key为电票签约协议，value为签约对象
	 * @param isAutoInPool  是否自动入池
	 * @throws Exception
	 * @date 2019-10-17下午2:27:05
	 */
	public void txQueryBillsFromBbsp(String accNos, Map billMap ,Map accMap,boolean isAutoInPool) throws Exception;
	
	/**
	 * 查询所有需要向额度系统校验额度的票据列表
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-26上午11:39:13
	 */
	public List<PoolBillInfo> queryCheckBills(boolean isAutoPoolIn)throws Exception;
	/**
	 * pl_pool_in数据处理
	 * @author Ju Nana
	 * @param bill
	 * @return
	 * @throws Exception
	 * @date 2019-10-17下午3:09:46
	 */
	public DraftPoolIn copyBillToPoolIn(PoolBillInfo bill)throws Exception ;
	/**
	 * 通过票号、票据区间号、状态获取池内票据
	 * @param DraftNb assetStatus
	 * @return
	 * @throws Exception
	 */
	public DraftPool getDraftPoolByDraftNbOrStatus(String billNo,String startNo, String endNo,String assetStatus) throws Exception;
	
	/**
	 * 额度系统额度占用--单笔，含有保贴信息记录功能
	 * @param bill
	 * @param poolIn
	 * @param dto
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-21上午12:49:06
	 */
	public ReturnMessageNew txMisCreditOccupy(PoolBillInfo bill,DraftPoolIn poolIn,PedProtocolDto dto) throws Exception;
	
	/**
	 * 额度系统额度占用   出池异常重新占用额度
	 * @param out
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-21上午1:50:46
	 */
	public ReturnMessageNew txMisCreditOccupy(PoolBillInfo bill) throws Exception;
	
	/**
	 * 额度系统额度占用--纸票额度占用
	 * @param bills
	 * @param clientNo
	 * @param dto
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-21上午3:04:00
	 */
	public List<String> txMisCreditOccupy(List<PoolVtrust> bills,String clientNo,PedProtocolDto dto)throws Exception;
	/**
	 * 保贴额度信息保存
	 * @param poolIn
	 * @param dto
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-21上午1:22:07
	 */
	public void txSavePedGuaranteeCredit(DraftPoolIn poolIn,PedProtocolDto dto) throws Exception;
	
	/**
	 * 保贴额度信息保存--纸票
	 * @param vtrust
	 * @param dto
	 * @param clientNo
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-21上午4:03:24
	 */
	public void txSavePedGuaranteeCredit(PoolVtrust vtrust,PedProtocolDto dto,String clientNo) throws Exception;
	
	/**
	 * 根据承兑行总行行号查询大票表对象,查询占用同业额度的、在池的票
	 * @param acptHeadBankNo 承兑行总行行号
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-22上午6:25:37
	 */
	public List<PoolBillInfo> queryPoolBillInfoListByTotalBank(List<String> acptHeadBankNos) throws Exception;
	
	/**
	 * 根据承兑行总行行号查询票据资产表对象,查询占用同业额度的、在池的票
	 * @param acptHeadBankNos
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-22上午6:39:57
	 */
	public List<DraftPool> queryDraftPoolListByTotalBank(List<String> acptHeadBankNos) throws Exception;
	
	/**
	* <p>方法名称: loadByBillNo|描述: 根据拆分id与票据状态查询票据信息对象</p>
	* @param splitId 
	* @param status 
	* @return
	*/
	public PoolBillInfo loadBySplit(String splitId,String status) throws Exception;
	
}
