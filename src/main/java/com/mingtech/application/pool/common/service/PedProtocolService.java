package com.mingtech.application.pool.common.service;

import java.util.Date;
import java.util.List;

import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PedProtocolModDto;
import com.mingtech.application.pool.common.domain.PlFeeScale;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.edu.domain.PedCheckList;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface PedProtocolService extends GenericService {

	// 提交开通审批
	public void txCommitPedpro(PedProtocolDto pedProtocolDto)
			throws Exception;
	//提交额度冻结/解冻
	public void txCommitPedproFor(PedProtocolDto pedProtocolDto)
			throws Exception;

	public void txSaveApproveOpclose(PedProtocolDto pedProtocolDto,PedProtocolModDto newPed) throws Exception;
	
	/**
	* <p>方法名称: loadProolJSON|描述: 查询协议JSON字符串</p>
	* @param acceptanceProtocol 承兑协议对象
	* @param queryBean 查询参数对象
	* @param user 当前用户
	* @param page 分页对象
	* @return
	* @throws Exception
	*/
	public String loadProolJSON(PedProtocolDto pedProtocolDto, PoolQueryBean queryBean, User user, Page page) throws Exception;
	
	/**
	* <p>方法名称: loadProolJSON|描述: 查询协议JSON字符串</p>
	* @param acceptanceProtocol 承兑协议对象
	* @param queryBean 查询参数对象
	* @param user 当前用户
	* @param page 分页对象
	* @return
	* @throws Exception
	*/
	public String loadProolJSONQuery(PedProtocolDto pedProtocolDto, PoolQueryBean queryBean, User user, Page page) throws Exception;
	public List  loadProolListQuery(PedProtocolDto pedProtocolDto, PoolQueryBean queryBean, User user,Page page)throws Exception ;
	public List exportProolListQuery(List res, Page page);
	public List queryBatchForAudit(List prdList,PedProtocolDto ppd,User user,Page page)throws Exception;
	
	
	/**
	 * 通过大额行号得到机构
	 * @param orgcode
	 * @return
	 * @throws Exception
	 * @author wufengjun 2018-12-4
	 */
	public Department queryDertByNumer(String officeNet);
	
	/**
	 * 通过大额行号得到机构
	 * @param orgcode
	 * @return
	 * @throws Exception
	 * @author wufengjun 2018-12-4
	 */
	public Department queryDertById(String id);
	
	/**
	 * 资产基本信息创建：用于签约完成后创建AssetPool相关信息
	 * @param pedProtocolDto   
	 * @author Ju Nana
	 * @date 2018-12-14 下午3:01:57
	 */
	public void createAssetPoolInfo(PedProtocolDto pedProtocolDto) throws Exception;
	
	/**
	 * 资产基本信息创建：用于签约完成后创建AssetType相关信息
	 * @author Ju Nana
	 * @date 2018-12-14 下午4:51:45
	 */
	public void txCreateAssetTypeInfo(PedProtocolDto pedProtocolDto,String assetType) throws Exception;
	
	/**
	 * 根据主键id查询CollectionSendDto对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CollectionSendDto queryDtoById(String id) throws Exception;
	
	/**
	 * 根据票据池编号、核心客户号、组织机构代码、保证金账号查询签约协议信息
	 * @param poolAgreement  票据池编号
	 * @param custnumber 核心客户号
	 * @param custOrgcode 组织机构代码
	 * @param marginAccount 保证金账户
	 * @param @throws Exception   
	 * @return PedProtocolDto  
	 * @author Ju Nana
	 * @date 2018-12-28 下午2:42:54
	 */
	public List<PedProtocolDto> queryProtocolByParam(String poolAgreement,String custnumber,String custOrgcode,String marginAccount)throws Exception;
	
	/**
	 * 担保合同自动续约
	 * @author Ju Nana
	 * @date 2019-2-19 下午2:07:18
	 */
	public void contractExtension()throws Exception;
	
	/**
	 * 查询签署了开通虚拟票据池的所有客户信息
	 * @return
	 * @param openFlag 开通状态
	 * @param status 状态
	 * @param poolAgreement  票据池编号
	 * @param custnumber 核心客户号
	 * @param custOrgcode 组织机构代码
	 * @param marginAccount 保证金账户
	 * @throws Exception
	 * @author wufengjun 2018-12-14
	 */
	public List<PedProtocolDto> queryProtocolInfo(String openFlag,String status,String poolAgreement,String custnumber,String custOrgcode,String marginAccount) throws Exception;
	
	/**
	 * 根据传入参数查询协议实体
	 * @Description TODO
	 * @author Ju Nana
	 * @param openFlag
	 * @param status
	 * @param poolAgreement
	 * @param custnumber
	 * @param custOrgcode
	 * @param marginAccount
	 * @return
	 * @throws Exception
	 * @date 2019-6-24上午10:57:13
	 */
	public PedProtocolDto queryProtocolDto(String openFlag,String status,String poolAgreement,String custnumber,String custOrgcode,String marginAccount) throws Exception;
	/**
	 * 通过客户号得到协议实体
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @author wufengjun 2018-12-4
	 */
	public PedProtocolModDto queryModProtocolByCode(String custNo) throws Exception;
	
	public PedProtocolModDto queryModProtocolById(String poolInfoId) throws Exception;
	
	/**
	 * 通过票据池编号，客户号，签约状态查询集团成员信息
	 * @param bpsNo	票据池编号
	 * @param custNo 核心客户号
	 * @param status 签约状态
	 * @param types  客户类型 	1 包含出质人(该条件只为查签约成员里的出质人,不做其他使用)  2查询融资人   3 查询票据池下的出质人融资人
	 * @param financingStatus  融资状态
	 * @return
	 * @throws Exception
	 * @author wu fengjun
	 * @date 2019-06-05
	 */
	public List<PedProtocolList> queryPedListByParam(String bpsNo ,String custNo ,String status ,String type ,String financingStatus) throws Exception;
	
	/**
	 * 通过票据池编号,签约状态,客户类型查询集团成员客户号集合
	 * @param bpsNo	票据池编号
	 * @param status 签约状态
	 * @param type 客户类型 	1 包含出质人(该条件只为查签约成员里的出质人,不做其他使用)    3 查询票据池下的出质人融资人
	 * @return
	 * @throws Exception
	 * @author wu fengjun
	 * @date 2019-06-05
	 */
	public List queryPedListCustNoByParam(String bpsNo ,String status ,String type) throws Exception;
	
	/**
	 * 查询有效的票据池收费标准
	 * @Description TODO
	 * @author Ju Nana
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午5:18:43
	 */
	public PlFeeScale queryFeeScale()throws Exception;
	
	/**
	 * 每日对账文件查询
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @param accountDate
	 * @param batchNo
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午7:05:43
	 */
	public List<PedCheckBatch> queryPedCheckBatch(String bpsNo,String custNo,Date accountDate,String batchNo,Page page)throws Exception;
	
	/**
	 * 每日对账文件查询
	 * @Description TODO
	 * @author liu xiaodong
	 * @param id
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午7:05:43
	 */
	public PedCheckBatch queryPedCheckBatchId(String batchNo)throws Exception;
	
	/**
	 * 对账任务查询
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNo 票据池编号
	 * @param custNo 客户号
	 * @param accountDate 账务日期
	 * @param batchNo 对账批次号
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午5:55:50
	 */
	public List<PedCheck> queryPedCheck(String bpsNo,String custNo,Date accountDate,String batchNo,Page page)throws Exception;
	
	/**
	 * 对账任务查询
	 * @Description TODO
	 * @author liu xiaodong
	 * @param batchNo 对账批次号
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午5:55:50
	 */
	public PedCheck queryPedCheckNo(String batchNo)throws Exception;
	
	/**
	 * 对账任务明细查询
	 * @Description TODO
	 * @author Ju Nana
	 * @param batchNo
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午6:06:54
	 */
	public List<PedCheckList> queryPedCheckListBybatch(String batchNo,Page page)throws Exception;
	
	/**
	 * 根据queryBean查询PedProtocolList信息，返回查询List
	 * @Description TODO
	 * @author Ju Nana
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @date 2019-6-21下午3:21:52
	 */
	public List<PedProtocolList> queryProListByQueryBean(ProListQueryBean queryBean) throws Exception;
	
	/**
	 * 根据queryBean查询PedProtocolList信息，返回实体
	 * @Description TODO
	 * @author Ju Nana
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @date 2019-6-21下午3:23:47
	 */
	public PedProtocolList queryProtocolListByQueryBean(ProListQueryBean queryBean) throws Exception;
	
	/**
	 * 根据对账结果判断是否可以发生融资业务
	 * @Description 
	 * 		（1）最后一次对账后2个月内无最新未对账记录
	 * 		（2）最新对账结果为“核对不一致”
	 * @author Ju Nana
	 * @param protocol 
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午9:03:49
	 */
	public Ret isCanCreateCreditByCheckResult(PedProtocolDto protocol) throws Exception;
	
	
	/**
	 * 根据queryBean查询PedProtocol信息，返回集合
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @author Wu Fengjun
	 * @date 2019-7-1
	 */
	public List<PedProtocolDto> queryProtocolDtoListByQueryBean(ProtocolQueryBean queryBean) throws Exception;
	
	/**
	 * 根据queryBean查询PedProtocol信息，返回实体
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @author Wu Fengjun
	 * @date 2019-7-1
	 */
	public PedProtocolDto queryProtocolDtoByQueryBean(ProtocolQueryBean queryBean) throws Exception;
	
	/**
	 * 是否有有效对账记录查询
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-3下午6:32:03
	 */
	public boolean isEveryCheckPass(String bpsNo,String custNo)throws Exception;

	
	/**
	 * 通过票据池编号,签约状态,客户类型查询集团成员客户号集合
	 * @param bpsNo	票据池编号
	 * @param status 签约状态
	 * @param custIdentity 客户类型 	1 包含出质人(该条件只为查签约成员里的出质人,不做其他使用)
	 * @return
	 * @throws Exception
	 * @author liu xiaodong 
	 * @date 2019-06-05
	 */
	public List queryPedListByParamDetail(String bpsNo,String custIdentity,String status,Page page) throws Exception;
	
	/**
	 * 是否自动入池
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-6下午2:14:16
	 */
	public boolean isAutoCheck(String bpsNo,String custNo) throws Exception;
	
	/**@Description 根据queryBean查询PedProtocolList信息，返回查询List
	 * @param queryBean
	 * @param page
	 * @return QueryResult
	 * @throws Exception
	 * @author xieCheng
	 */
	public List<PedProtocolList> queryProListByQueryBean(ProListQueryBean queryBean, Page page) throws Exception;

	/**
	 * @Description 对账任务明细查询
	 * @param batchNo
	 * @param batchNo
	 * @return QueryResult
	 * @throws Exception
	 * @author xieCheng
	 */
	public QueryResult queryPedCheckList(String batchNo ,Page page)throws Exception;
	
	
	/**
	 * 是否已对账：校验最新的一笔对账记录
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-24上午9:15:12
	 */
	public boolean isChecked(String bpsNo,String custNo)throws Exception;


	/** 根据票据池编号、核心客户号获得票据池对账任务实体
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * xieCheng 20190729
	 */
	public PedCheck queryPedCheck(String bpsNo,String custNo)throws Exception;
	
	/** 
	 * @return
	 * @throws Exception
	 * liuxiaodong 20190729
	 */
	public List<PedCheck> queryPedCheckNew(PedCheck ped,User user,Page page)throws Exception;
	
	/**
	 * 每日对账文件查询
	 * @Description TODO
	 * @author liu xiaodong	
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午7:05:43
	 */
	public List<PedCheckBatch> queryPedCheckBatchNew(PedCheckBatch ped,User user,Page page)throws Exception;

	/**
	 * 对像保存或修改
	 * @param o
	 * @throws Exception
	 * @author wufengjun
	 * @date 2019-8-12下午8:50:43
	 */
	public void merge(Object o) throws Exception;

	/**
	 * 立即将修改后的对象写入数据库
	 * @throws Exception
	 * @author wufengjun
	 * @date 2019-8-13下午15:50:43
	 */
	public void flush() throws Exception;
	
	/**
	 * 协议及子户信息备份
	 * @author Ju Nana
	 * @param bpsNo
	 * @throws Exception
	 * @date 2019-9-10下午5:43:50
	 */
	public void txProtocolAndListHist(String bpsNo) throws Exception;
	
	/**
	 * 客户经理移交检查
	 * @author Ju Nana
	 * @param accountManagerId
	 * @param accountManagerIdOld
	 * @return
	 * @throws Exception
	 * @date 2019-10-22上午9:55:19
	 */
	public String userManageChangeCheck(String accountManagerId ,String accountManagerIdOld,User userLogon) throws Exception;
	/** 描述:票据池保证金账户开立
     * @param protocol  新签约信息
     * @return Map
     * @throws Exception
     */
    public ReturnMessageNew txMarginAccount(PedProtocolDto protocol) throws Exception;
	/** 描述:票据池保证金账户变更
     * @param oldAcc  旧账号
     * @param newAcc 新账号
     * @param poolAgreement 票据池编号
     * @return Map
     * @throws Exception
     */
    public ReturnMessageNew txMarginAccChange(String oldAcc,String newAcc,String poolAgreement) throws Exception;
    /** 描述:票据池担保合同查询
     * @return String
     * @throws Exception
     */
	public String loadContractJSON(ProtocolQueryBean pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception;

	
	/** 描述:票据池模式变更
     * @param pedProtocolDto  协议
     * @return ReturnMessageNew
     * @throws Exception
     */
	public ReturnMessageNew txChangeEdu(PedProtocolDto pedProtocolDto)  throws Exception;
	
	/**
	 * 票据池是否已缴纳年费
	 * @param pro
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-22上午10:40:53
	 */
	public boolean isPaid(PedProtocolDto pro)throws Exception;
	
	/**
	 * 生成在线业务合同序列号
	 * @param custId
	 * @param length
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-23上午10:46:51
	 */
	public  String txCreateOnlineProductNo(String custId , int length) throws Exception;

	
	/**
	 * 根据客户号查询客户登记信息
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-23上午10:58:31
	 */
	public CustomerRegister queryCustomerRegisterByCustNo(String custNo) throws Exception ;
	
	/**
	 * 票据池担保合同查询
	 * 
	 * @param pedProtocolDto
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadContractList(ProtocolQueryBean pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception ;
}






