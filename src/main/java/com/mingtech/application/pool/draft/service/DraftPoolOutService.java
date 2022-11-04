/**
 * 
 */
package com.mingtech.application.pool.draft.service;

import java.math.BigDecimal;
import java.util.List;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftPoolOutBatch;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author wbyecheng
 * 
 *         票据资产出池服务（代保管取票、解质押出池）
 * 
 */
public interface DraftPoolOutService extends GenericService {

	public DraftPoolOutBatch load(String id);

	/**
	 * 通过票号获取出池对象
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	public DraftPoolOut getDraftPoolOutByDraftNb(String DraftNb,String beginRangeNo, String endRangeNo) throws Exception;

	
	/**
	* <p>方法名称: loadByBillNo|描述: 根据票据号码查询票据信息对象</p>
	* @param billNo1 票据号码
	* @param beginRangeNo 票据号起始
	* @param endRangeNo 票据号截至
	* @return
	*/
	public PoolBillInfo loadByBillNo(String billNo,String beginRangeNo, String endRangeNo) throws Exception;
	
	/** 20210517 gcj
	* <p>方法名称: loadByBillDiscID|描述: 根据票据票据系统票据ID,子票区间起始截止号查询票据信息对象</p>
	* @param discBillId 票据系统票据ID
	* @return
	*/
	public PoolBillInfo loadByBillDiscID(String discBillId,String beginRangeNo, String endRangeNo) throws Exception;

	/**
	 * 通过票号获取出池对象
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public DraftPoolOut getDraftPoolOutBybean(PoolQueryBean bean) throws Exception;
	/**gcj 20210513
	 * 通过id查询DraftPoolOut对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DraftPoolOut loadByOutId(String id) throws Exception;
	/**gcj 20210513
	 * 通过id查询DraftPoolOut对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PlBatchInfo getPlBatchInfoBybean(PoolQueryBean bean) throws Exception;
	/**gcj 20210517
	 * 质押出池
	 * @param DraftPool 基础信息表
	 * @param PlBatchInfo 续接批次表
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew sendPoolOutMsg(DraftPool pool ,PlBatchInfo info,String hildId) throws Exception;
	/**gcj 20210517
	 * 背书出池
	 * @param DraftPool 基础信息表
	 * @param PlBatchInfo 续接批次表
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew sendPoolendorseeMsg(DraftPool pool ,PlBatchInfo info,String hildId) throws Exception;
	/**gcj 20210519
	 * 贴现出池
	 * @param DraftPool 基础信息表
	 * @param PlBatchInfo 续接批次表
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew sendPoolDiscountMsg(DraftPool pool ,PlBatchInfo info,String hildId) throws Exception;
	/**
	 * @param 票据出池查询
	 * @param gcj 20210701
	 * @return
	 * @throws Exception
	 */
	public QueryResult toPoolOutByQueryBean(DraftQueryBean bean, Page page) throws Exception ;
	
	/**
	 * 解质押签收最后一步bbsp通知或查证接口后的处理逻辑
	 * @param draftPoolOut	出池对象
	 * @param transResult	解质押签收结果
	 * @throws Exception
	 */
	public ReturnMessageNew txTransTypePoolOut(DraftPoolOut draftPoolOut,String transResult,PoolBillInfo info) throws Exception;
}
