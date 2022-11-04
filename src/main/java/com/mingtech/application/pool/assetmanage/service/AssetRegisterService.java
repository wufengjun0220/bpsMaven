package com.mingtech.application.pool.assetmanage.service;

import java.math.BigDecimal;
import java.util.List;

import com.mingtech.application.pool.assetmanage.domain.AssetQueryBean;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.framework.core.service.GenericService;


/**
 * 资产出入池登记接口实现
 * @author h2
 * 20210519
 */
public interface AssetRegisterService extends GenericService{
	
	/**
	 * 票据入池登记
	 * @param draftPool 票据池票据对象
	 * @param pro 票据池协议对象
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-28下午5:31:07
	 */
	public AssetRegister txBillAssetRegister(DraftPool draftPool,PedProtocolDto pro) throws Exception;

	/**
	 * 保证金首次登记/修改登记
	 * @param bail 保证金对象
	 * @param pro 票据池协议对象
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-28下午5:31:23
	 */
	public AssetRegister txBailAssetRegister(BailDetail bail,PedProtocolDto pro) throws Exception;

	
	/**
	 * <p>方法名称: txAssetRegister|描述: 资产类型入池登记</p>
	 * @param assetReg 资产登记信息
	 * (资产登记对象必输项- apId、atId、custPoolName、certType、certCode、custSignNo、assetNo、assetType、riskType、assetAmount、assetDueDt;//资产到期日)
	 * @return
	 * @throws Exception
	 */
	public void txSaveAssetRegister(AssetRegister assetReg) throws Exception;
	
	
	 /**
	  * <p>描述:活期保证金资产资金变更 </p>
	  * @param bpsNo 签约池编号
	  * @param account 账号
	  * @param assetAmount 账户余额
	  * @return boolean 保证金发生变化返回 true 未发生变化返回 false
	  * @throws Exception
	  */
	public boolean txCurrentDepositAssetChange(String bpsNo,String account,BigDecimal assetAmount ) throws Exception;

	
	/**
	 * <p>描述:票据出库资产变更 </p>
	 * @param bpsNo 签约池编号
	 * @param pool 票据对象
	 * @param tradeAmt 交易金额
	 * @param stockOutType 出库类型-02到期、04出库（从PublicStaticDefineTab中获取）
	 * @return void
	 * @throws Exception
	 */
	public void txDraftStockOutAssetChange(String bpsNo, DraftPool pool,BigDecimal tradeAmt,String stockOutType) throws Exception;
	
	/**
	 * 根据票号（AssetRegister表中的资产编号assetNo）查询资产信息
	 * @param assetNo
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-28上午9:04:53
	 */
	public List<AssetRegister> queryAssetRegisterByAssetNo(List<String> assetNo) throws Exception;

	/**
	 * 查询一个票据池中除去  List<String> assetNos 资产之后的资产信息，该方法可用于资产出池试算
	 * @param bpsNo
	 * @param assetNos
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-12下午7:48:48
	 */
	public List<AssetRegister> queryAssetRegisterExceptAssetNos(String bpsNo,List<String> assetNos) throws Exception;
	 
	/**
	* <p>描述:根据客户池签约编号和资产编号查询资产登记信息 </p>
	* @param bpsNo 签约池编号
	* @param account 账号
	* @param assetAmount 账户余额
	* @return 资产登记信息
	*/
	public AssetRegister getAssetRegisterByCustSignNoAndAssetNo(String bpsNo,String account) throws Exception;
	
	/**
	 * 资产历史保存
	 * @param assetRegister
	 * @param stockOutType
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-22上午7:03:54
	 */
	public void txSaveAssetRegisterHis(AssetRegister assetRegister,String stockOutType) throws Exception;
	
	/**
	 * 资产登记列表查询
	 * @param bean
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-10-14下午1:07:32
	 */
	public List<AssetRegister> queryAssetRegisterList(AssetQueryBean bean) throws Exception;

}
