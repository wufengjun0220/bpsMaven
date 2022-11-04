package com.mingtech.application.pool.financial.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.financial.domain.CreditCalcuCache;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.service.GenericService;

/**
 * 资产及额度处理服务
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-5
 * @copyright 北明明润（北京）科技有限责任公司
 */
public interface FinancialService extends GenericService {


    /**
     * 额度计算表生成-期限配比模式
     * @param proDto
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7上午10:21:53
     */
    public List<CreditCalculation> txCreditCalculationTerm(PedProtocolDto proDto) throws  Exception;
    
    /**
     * 额度计算表更新
     * 该方法实现保证金同步与额度计算表异步计算的功能
     * @param proDto
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7上午11:45:27
     */
    public Ret txCreditCalculationByProtocol(PedProtocolDto proDto) throws  Exception;
    
    /**
     * 额度计算生成表-总量模式
     * @param proDto
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7上午10:24:42
     */
    public CreditCalculation txCreditCalculationTotal(PedProtocolDto proDto) throws  Exception;
    
    
    /**
     * 日终额度重新生成-含AssetPool无条件解锁
     * @param proList 协议列表
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-30下午2:03:34
     */
    public void txReCreditCalculationTask(List<PedProtocolDto> proList) throws  Exception;
    

    /**
     * 额度占用方法
     * @param crdtReg
     * @param proDto
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午3:50:32
     */
    public Ret txCreditUsed(List<CreditRegister> crdtRegList,PedProtocolDto proDto) throws  Exception;
    
    
    /**
     * 用信业务发生-额度计算缓存表生成-期限配比模式
     * @param proDto
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午3:50:13
     */
    public List<CreditCalcuCache> txCreditCalculationCacheTerm(PedProtocolDto proDto,String flowNo)  throws  Exception;
    
    /**
     * 用信业务发生-额度计算缓存生成-总量模式
     * @param proDto
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午4:48:32
     */
    public CreditCalcuCache txCreditCalculationCacheTotal(PedProtocolDto proDto,String flowNo)  throws  Exception;
    
    /**
     * 资产出池-额度计算缓存表生成-期限配比模式
     * @param proDto
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午3:50:13
     */
    public List<CreditCalcuCache> txOutCreditCalculationCacheTerm(PedProtocolDto proDto,String flowNo)  throws  Exception;
    
    /**
     * 资产出池-额度计算缓存生成-总量模式
     * @param proDto
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午4:48:32
     */
    public CreditCalcuCache txOutCreditCalculationCacheTotal(PedProtocolDto proDto,String flowNo)  throws  Exception;
    
    /**
     * 额度试算:做融资业务之前的试算
     * @param crdtRegCacheList
     * @param proDto
     * @param flowNo
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午4:07:37
     */
    public Ret txCreditUsedCheck(List<CreditRegisterCache> crdtRegCacheList,PedProtocolDto proDto,String flowNo) throws  Exception;

    /**
     * 资产出池
     * @param assetOutList
     * @param proDto
     * @param mapList 网银或票据池发送过来的要处理的出池的数据
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午2:30:24
     */
    public Ret txAssetOut(List<AssetRegister> assetOutList,PedProtocolDto proDto,Map<String,PoolBillInfo> mapList)throws  Exception;
    
    /**
     * 资产出池校验
     * @param assetOutList：校验出池资产列表
     * @param proDto ：协议实体
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-8-12下午7:19:01
     */
    public Ret txAssetOutCheck(List<AssetRegister> assetOutList,PedProtocolDto proDto)throws  Exception;
    
    /**
     * 查询额度计算表
     * @param bpsNo
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-7-10下午5:15:57
     */
    public List<CreditCalculation> queryCreditCalculationList(String bpsNo) throws  Exception;
    
    /**
     *<p>描述:期限匹配现金流表查询</p>
     * @param user
     */
    public String queryCreditCalculation(String bpsNo, User user) throws  Exception;
    /**
     *<p>描述:期限匹配新增流出试算</p>
     * @param crdtRegCacheList
     * @param pedProtocolDto
     */
    public String txCreditCalCahceForAddOut(List<CreditCalcuCache> crdtRegCacheList, PedProtocolDto pedProtocolDto,String flowNo)throws  Exception;
    
    /**
     * 在线业务释放额度方法
     * @param ReleseIds 要释放的ID，即CreditRegister对象中的BUSIID
     * @param bpsNo 票据池编号
     * @throws Exception
     * @author Ju Nana
     * @date 2021-7-7上午9:16:37
     */
    public void txOnlineBusiReleseCredit(List<String> releseIds,String bpsNo)throws  Exception;
    
    /**
     * 在线业务额度占用转换
     * @param delIds  需要删除的资产对象ID
     * @param crdtDetailList  需要新增占用的额度明细对象
     * @param bpsNo
     * @throws Exception
     * @author Ju Nana
     * @date 2021-7-7下午1:03:01
     */
    public void txOnlineBusiCreditChange(String contractNo,List<PedCreditDetail> crdtDetailList,String bpsNo)throws  Exception;
    
    /**
     * 保证金同步，重新计算额度，重新更新AssetPool及AssetType表  --不加锁处理
     * @param apId
     * @param dto
     * @throws Exception
     * @author Ju Nana
     * @date 2021-7-8上午11:11:25
     */
    public void txUpdateBailAndCalculationCredit(String apId,PedProtocolDto dto) throws Exception;
    
    /**
     * 根据票据池编号查询额度计算表信息
     * @param bpsNo
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-7-9上午9:59:13
     */
	public List<CreditCalculation> queryCreditCalculationListByBpsNo(String bpsNo) throws Exception;
	
	/**
	 * 保证金同步及额度重新计算
	 * @param pro
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-11下午6:57:38
	 */
	public BailDetail txBailChangeAndCrdtCalculation(PedProtocolDto pro) throws Exception;
	
	/**
	 * 在线流贷额度占用--事务方法
	 * @param batch
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-19下午1:14:15
	 */
	public Ret txOnlineCreditUsed(PlOnlineCrdt batch);

	/**
	 * 额度刷新
	 * @param pro
	 * @param apId
	 * @author Ju Nana
	 * @date 2021-9-1下午9:25:30
	 */
    public void txRefreshFinancial(PedProtocolDto pro,String apId) throws Exception;;
}
