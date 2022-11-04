/**
 * 
 */
package com.mingtech.application.pool.edu.service;

import java.util.List;

import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.CreditPedBean;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.MisCredit;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author wbyecheng
 *
 */
public interface PoolCreditProductService extends GenericService {
	
	/**
	 * 数据平台中间表——MIS系统出账明细表
	 * @return
	 * @throws Exception
	 * @author WuFengjun
	 * @date 2019-2-13
	 */
	public List<MisCredit> queryDetails(String loanNo,String crdtNo) throws Exception;
	
	/**
	 * 更新MisCredit表数据
	 * @param credit
	 * @param @throws Exception   
	 * @author Ju Nana
	 * @date 2019-3-7 下午7:21:45
	 */
	public void txUpdateMisCredit(MisCredit credit) throws Exception;
	
	/**
	 * 信贷主业务合同查询
	 * @author Ju Nana
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @date 2019-6-26上午10:40:26
	 */
	public List<CreditProduct> queryCedtProductList(CreditQueryBean queryBean) throws Exception;
	/**
	 * 通过信贷产品号及结清状态获取信贷产品信息
	 * @author Ju Nana
	 * @param CreditNo
	 * @param sttlFlag
	 * @return
	 * @throws Exception
	 * @date 2019-6-26上午10:51:48
	 */
	public CreditProduct queryProductByCreditNo(String CreditNo,String sttlFlag) throws Exception;
	
	/**
	 * 查询借据信息
	 * @author Ju Nana
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @date 2019-6-26上午11:09:16
	 */
	public List<PedCreditDetail> queryCreditDetailList(CreditQueryBean queryBean) throws Exception;
	
	/**
	 * 根据借据号或者账号查询借据信息
	 * @author Ju Nana
	 * @param transAccount
	 * @param loanNo
	 * @return
	 * @throws Exception
	 * @date 2019-6-26上午11:43:05
	 */
	public PedCreditDetail queryCreditDetailByTransAccountOrLoanNo(String transAccount,String loanNo) throws Exception;
	

	/**
	 * @Title txUpdateCreditProduct
	 * @author wss
	 * @date 2021-5-19
	 * @Description 修改合同状态
	 * @return void
	 * @throws Exception 
	 */
	public void txUpdateCreditProduct(String contractNo, String string) throws Exception;
	
	
	/**
	 * 保贴人的保贴额度统计
	 * @param query 查询条件
	 * @return
	 * @throws Exception
	 */
	public List<CreditPedBean> loadPosterCount(String acceptor,Page page) throws Exception;

	
	/**
	 * 票据池占用保贴额度查询
	 * @param query 查询条件
	 * @return
	 * @throws Exception
	 */
	public List<CreditPedBean> loadPoolPasteount(String bpsNo,String isGroup,Page page) throws Exception;

	
	/**
	 * 已解约融资人生效标识日终同步
	 * @Description 将融资人生效标识为 SF_01 生效的非融资人（融资功能已解约）在全部信贷业务处理完毕后置为无效SF_00
	 * @author Ju Nana
	 * @throws Exception
	 * @date 2019-8-12下午7:18:50
	 */
	public void txEndFinancier()  throws Exception;

	/**
	 * 根据条件查询保贴占用记录
	 * @return
	 * @author Wufengjun
	 * @throws Exception
	 * @date 2019-8-14
	 */
	public PedGuaranteeCredit queryByBean(PoolQueryBean bean) throws  Exception;
	
	/**
	 * 同步借据信息
	 * @param detail
	 * @param product
	 * @param loanType
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-21下午8:00:21
	 */
	public PedCreditDetail txSynchroLoan(PedCreditDetail detail,CreditProduct product,String loanType) throws Exception;
	
}
