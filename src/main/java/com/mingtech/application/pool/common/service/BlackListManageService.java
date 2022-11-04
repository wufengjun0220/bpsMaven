package com.mingtech.application.pool.common.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.base.domain.BoCcmsPartyinf;
import com.mingtech.application.pool.common.domain.GuarantDiscMapping;
import com.mingtech.application.pool.common.domain.PedBlackList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.PoolCommonQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 黑名单 服务 接口
 * @author tangxiongyu
 *
 */
public interface BlackListManageService extends GenericService{
	

	/**
	* <p>方法名称: loadBlackListJSON|描述: 查询黑名单对象列表JSON</p>
	* @param PedBlackList 黑名单对象
	* @param page 分页对象
	* @return
	* @throws Exception
	*/
	public String loadBlackListJSON(PedBlackList blackList, Page page,User user) throws Exception;

	/**
	* <p>方法名称: loadGrayListJSON|描述: 查询灰名单对象列表JSON</p>
	* @param PedBlackList 黑/灰名单对象
	* @param page 分页对象
	* @return
	* @throws Exception
	*/
	public String loadGrayListJSON(PedBlackList pb, Page page) throws Exception;
	
	/**
	 * 
	 * 黑名单及风险校验方法
	 * @param  bill 票据
	 * @param  custNo  核心客户号
	 * @author Ju Nana
	 * @date 2018-11-20 上午10:37:49
	 */
	public PoolBillInfo txBlacklistAndRiskCheck(PoolBillInfo bill,String custNo ) throws Exception;
	
	/**
	 * 名单校验--2021年票据池升级项目变更内容，配合MIS系统财票改造
	 * @param bill
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-4下午1:39:21
	 */
	public PoolBillInfo txBlacklistCheck(PoolBillInfo bill,String custNo ) throws Exception;
	/**
	 * 查询二代行名行号表
	 * @author liu xiaodong
	 * @date 2019-6-20 上午10:37:49
	 */
	public List loadbankSubordinate(BoCcmsPartyinf pb,Page page) throws Exception;

	/**
	 * 据行号获取名称
	 * @author liu xiaodong
	 * @date 2019-6-20 上午10:37:49
	 */
	public List queryByPrcptcd(String prcptcd) throws Exception;
	
	/**
	 * 据行号获取名称
	 * @author liu xiaodong
	 * @date 2019-6-20 上午10:37:49
	 */
	public BoCcmsPartyinf queryByPrcptcdNo(String prcptcd) throws Exception;
	
	public List exportbankSubordinate(List res, Page page);
	/**
	 * 
	 * 信贷系统额度校验-批量
	 * @param  
	 * @param  billList 需要校验的票据列表
	 * @author gcj
	 * @date 20210525
	 */
	public List<PoolBillInfo> txMisCreditCheck(List<PoolBillInfo> billList) throws Exception;
	
	/**
	 * 入池商票、财票承兑行信息检查：
	 * 		入池商票及财票的承兑行号，需要满足名单制管理：名单分为总名单（AcptCheckMainlist）和子名单（AcptCheckSublist），
	 *      总名单适用于所有承兑人，子名单仅适用于对应的承兑人。只要商票承兑行行号属于总名单或者承兑人对应的子名单，满足任一即可。
	 * @param queryBean:传递承兑人相关校验信息
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-24上午11:07:08
	 */
	public boolean checkAccptr(PoolCommonQueryBean queryBean) throws Exception;
	
	/**
	 * 承兑人行号被列入黑名单
	 * @param acptBankNo	承兑行列表
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-2下午6:47:17
	 */
	public void txAddAcptBankToBlackList(List<String> acptBankNos)throws Exception;
	
	/**
	 * 承兑人行号从黑名单中删除
	 * @param acptBankNos
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-2下午6:47:45
	 */
	public void txDelAcptBankFromBlackList(List<String> acptBankNos)throws Exception;
	
	/**
	 * 承兑人映射信息管理界面主页面查询
	 * @param disc
	 * @param page
	 * @author wfj
	 * @date 2021-8-14下午2:11:37
	 * @throws Exception
	 */
	public List<GuarantDiscMapping> queryAcceptorMappingList(GuarantDiscMapping disc,Page page)throws Exception;
	
	/**
	 * 承兑人映射信息管理数据变更查询
	 * @param disc
	 * @param page
	 * @author wfj
	 * @date 2021-8-14下午2:11:37
	 * @throws Exception
	 */
	public List<GuarantDiscMapping> queryChangeAcceptorMapping(GuarantDiscMapping disc,List banks,Page page)throws Exception;
	
	
	/**
	 * 根据承兑人全称、承兑人账号、承兑人开户行行号查询保贴编号--用于商票
	 * @param acceptor
	 * @param acptAcctNo
	 * @param acptBankNo
	 * @param totalBankNo 总行
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-31下午5:27:58
	 */
	public Map queryGuarantNo(String acceptor,String acptAcctNo,String acptBankNo,String billType,String totalBankNo) throws Exception;
	
	/**
	 * 承兑行高低风险会员信息同步
	 * @param memberId
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-22上午1:25:35
	 */
	public ReturnMessageNew txSynchBankMember(String memberId) throws Exception;
	
	/**
	 * 
	 * 
	 * @param ProtocolQueryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadMemberBankList(ProtocolQueryBean queryBean,User user, Page page) throws Exception ;
	/**
	 * 
	 * 
	 * @param loadMemberBankInfo 承兑行会员信息同步查询
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadMemberBankInfo(ProtocolQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 
	 * 
	 * @param loadGuarantDiscMappingList 保贴映射关系查询
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadGuarantDiscMappingList(ProtocolQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 
	 * @param 查询票交所承兑行行名
	 * @param acptBankCode
	 * @return
	 * @throws Exception
	 */
	public String queryPjsAcptName(String acptBankCode,String type) throws Exception;
	/**
	 * 
	 * @param 查询保贴人编号
	 * @param acptBankCode
	 * @return
	 * @throws Exception
	 */
	public String queryGuarantDiscNo(String acptBankCode) throws Exception;
	/**
	 * 
	 * @param 更新保存保贴映射关系表
	 * @param acptBankCode
	 * @return
	 * @throws Exception
	 */
	public String txsaveGuarantDiscMapping(GuarantDiscMapping grarant ,User user) throws Exception;
	/**
	 * 
	 * @param 财票提交审批
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void txSubmitAuditFinance(String id, User user) throws Exception;
	/**
	 * 
	 * @param 财票撤销审批
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void txCancelAuditFinance(String id, User user) throws Exception ;

	/**
	 * 
	 * 
	 * @param loadCpesBranch 票交所机构表信息
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadCpesBranch(ProtocolQueryBean queryBean,User user, Page page) throws Exception;
	
	/**
	 * 
	 * 
	 * @param loadCpesBranch 票交所机构表信息查询
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List queryCpesBranch(ProtocolQueryBean queryBean,User user, Page page) throws Exception;
	
	/**
	 * MIS承兑行信息变化处理
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-22上午2:24:25
	 */
	public void txBankMemberChangedHandle(ReturnMessageNew response) throws Exception;
	
	/**
	 * 根据行号查询总行行号
	 * @param bankNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-22上午3:54:21
	 */
	public String queryTotalBankNo(String bankNo)throws Exception;
	
	/**
	 * 查询黑名单对象
	 * @param bean	查询条件
	 * @param accptBank	承兑行列表
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 2021-9-28
	 */
	public List queryBlackListByBean(PedBlackList bean ,List<String> accptBank) throws Exception;
	
	/**
	 * 根据总行行号查询其下所有分行行号在黑名单的数据
	 * @param bankNo
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 2021-9-28
	 */
	public List queryAllBankNo(String bankNo) throws Exception;
	
	/**
	 * 根据总行行号查询其下所有分行行号
	 * @param bankNo
	 * @return
	 * @throws Exception
	 */
	public List<String> queryAllBranchBank(String bankNo) throws Exception;
	
	
	public  List<PoolBillInfo> txMisCreditQuery(List<PoolBillInfo> billList) throws Exception;
	
	/**
	 * 根据行号查询总行会员信息
	 * @param bankNo
	 * @return
	 * @throws Exception
	 * @author Wu fengjun
	 * @date 2021-10-14
	 */
	public Map<String,String> queryCpesMember(String bankNo)throws Exception;
	
}
