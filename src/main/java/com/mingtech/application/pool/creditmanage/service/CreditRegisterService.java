package com.mingtech.application.pool.creditmanage.service;

import java.math.BigDecimal;
import java.util.List;

import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.framework.core.service.GenericService;


/**
 * 融资业务用信登记接口实现
 * @author h2
 * 20210519
 */
public interface CreditRegisterService extends GenericService{
	/**
	 * <p>描述：查询所有的合同融资登记信息</p>
	 */
	public List queryAllCreditCont();

	/**
	 * 根据融资业务ID查询登记表信息
	 * @param ids
	 * @return
	 * @author Ju Nana
	 * @date 2021-9-7下午4:06:42
	 */
	public List<CreditRegister> queryAllCreditContByBusiIds(List ids);

	/**
	 * <p>描述：查询该合同下所有的借据信息</p>
	 */
	public List<CreditRegister> queryCreditContByNo(String contNo,String busiNo,String voucherType) ;

	/**
	 * <p>描述：查询该合同编号下所有有效借据的总有效金额</p>
	 * @param  contNo 合同编号
	 * @return
	 */
	public BigDecimal queryCountAmout(String contNo);

	/**
	 * <p>描述：保存融资业务用信登记信息</p>
	 * @param creditReg 融资业务用信保存
	 *  统一新增保存接口
	 * (用信对象必输项- apId、atId、custPoolName、certType、certCode、custSignNo、assetNo、assetType、riskType、assetAmount、assetDueDt;//资产到期日)
	 * @return
	 * @throws Exception
	 */
	public void txSaveCreditRegister(CreditRegister creditReg) throws Exception;
	
	
	 /**
	  * <p>描述:更新融资业务登记信息 </p>
	  * @param creditReg 融资业务用信
	  * @return void
	  * @throws Exception
	  */
	public void txUpdateCreditRegister(CreditRegister creditReg) throws Exception;

    /**
     * <p>描述:自动保存历史信息</p>
     * @param creditRegister
     */
    public void txCreateRegisterHis(CreditRegister creditRegister) throws  Exception;

	/**
	 * 根据【业务ID】移出融资业务登记表信息
	 * @param ids
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-7下午4:03:33
	 */
	public void txDelCreditRegisterByBusiIds(List ids) throws Exception;

    /**
     * <p>描述:判断该合同项下所有的有效借据（未用退回为无效借据）金额之和是否等于合同金额</p>
     *  若相等删除合同
     */
    public void checkAmtIsSame(CreditRegister register) throws Exception;
    
    /**
     * 根据主协议生成用信业务对象
     * @param product 用信业务对象
     * @param pro 票据池协议对象
     * @param apId assetPool表主键
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-29下午2:18:40
     */
    public CreditRegister createCreditRegister(CreditProduct product,PedProtocolDto pro,String apId)throws Exception;
    
    
    /**
     * 根据借据生成用信业务对象
     * @param loan
     * @param pro
     * @param apId
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-30上午9:59:47
     */
    public CreditRegister createCreditRegister(PedCreditDetail loan,PedProtocolDto pro,String apId)throws Exception;
    /**
     * 根据主协议编号生成用信业务【临时】对象
     * @param product 用信业务对象
     * @param pro 票据池协议对象
     * @param apId assetPool表主键
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-29下午2:18:40
     */
    public CreditRegisterCache createCreditRegisterCache(CreditProduct product,PedProtocolDto pro,String apId)throws Exception;
    
    /**
     * 根据借据生成用信业务【临时】对象
     * @param product 用信业务对象
     * @param pro 票据池协议对象
     * @param apId assetPool表主键
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-29下午2:18:40
     */
    public CreditRegisterCache createCreditRegisterCache(PedCreditDetail crdtDetail,CreditProduct product,PedProtocolDto pro, String apId)throws Exception;
    
    /**
     * 根据业务ID查询用信业务对象（唯一）
     * @param busiId
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-29下午2:28:31
     */
    public CreditRegister queryCreditRegisterByBusiId(String busiId)throws Exception ;
    
    /**
     * 根据主业务合同释放额度
     * @param product
     * @param releaseType 01-结清释放 02-未用释放
     * @param releaseAmt 释放金额
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-29下午6:36:47
     */
    public Ret txReleaseCreditByProduct(CreditProduct product,String releaseType,BigDecimal releaseAmt)throws Exception ;
    
    /**
     * 根据借据释放额度
     * @param loan
     * @param releaseType 01-结清释放 02-未用释放
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-29下午6:37:43
     */
    public Ret txReleaseCreditByLoan(PedCreditDetail loan ,String releaseType)throws Exception ;
    
    /**
     * 主业务合同占用系统调整
     * @param cp
     * @param newCcupy
     * @throws Exception
     * @author Ju Nana
     * @date 2021-8-2上午9:44:36
     */
    public void txChangeProductCcupy(CreditProduct cp ,String newCcupy)throws Exception ;
    
    /**
     * 根据票据池编号及风险类型查询用信业务占用余额
     * @param bpsNo
     * @param riskLevel
     * @return
     * @author Ju Nana
     * @date 2021-8-3上午11:11:02
     */
    public BigDecimal queryCreditBalance(String bpsNo,String riskLevel);
    
    /**
     * 根据主业务合同号查询用信业务占用余额
     * @param crdtNos
     * @return
     * @author Ju Nana
     * @date 2021-8-3上午11:27:43
     */
    public BigDecimal queryCreditBalance(List crdtNos);

    /**
     * 融资业务试算
     * @param onlineNo 在线业务编号
     * @param onlineType 在线业务类型 0：在线银承 1：在线流贷
     * @param billAmt 开票金额
     * @param dueDate 业务到期日
     * @return ReturnMessageNew
     * @throws Exception
     */
    public ReturnMessageNew checkScale(String onlineNo, String onlineType, String billAmt, String dueDate) throws Exception;
    
    /**
     * 根据主业务合同号与凭证类型查询资产登记表信息
     * @param contractNos
     * @param voucherType
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-9-16下午1:53:28
     */
    public List<CreditRegister> queryCreditRegisterBycontractNos(List<String> contractNos,String voucherType) throws Exception;

}
