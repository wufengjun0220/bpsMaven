package com.mingtech.application.pool.discount.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.discount.domain.BankRoleMappingBean;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.service.GenericService;


/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 调用中台系统业务接口
 *
 */
@Service
public interface CenterPlatformSysService extends GenericService {
	
	/**
	 * 在线业务开关信息变更通知(CP20220426)
	 * */
	public boolean txChangeOnlineConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception ;
	
	
	/**
	 * 机构在线业务开关信息变更通知接口(CP20220426)
	 * */
	public boolean txChangeDepartMentConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception ;
	
	/**
	 * 机构在线业务开关信息查询接口(CP20220425)
	 * */
	ReturnMessageNew txQueryDepartMentConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	
	/**
	 * 客户在线业务开关信息变更通知接口(CP20220424)
	 * */
	public boolean txChangeCustConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception ;
	
	/**
	 * 客户在线业务开关信息查询接口(CP20220423)
	 * */
	public ReturnMessageNew txCustConfigQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception ;
	
	/**
	 * 承兑行黑名单查询接口(CP20220422)
	 * */
	public ReturnMessageNew txAcceptBankBlackListQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception ;
	
	/**
	 * 承兑行黑名单维护接口(CP20220421)
	 * */
	public String txAcceptBankBlackListMt(List<CenterPlatformBean> centerPlatformBeans,User user) throws Exception;
	
	/**
	 * 出票人黑名单查询接口(CP20220420)
	 * */
	public ReturnMessageNew txDrawerBlackListQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception ;
	
	/**
	 * 出票人黑名单维护接口(CP20220419)
	 * */
	public boolean txDrawerBlackListMt(List<CenterPlatformBean> centerPlatformBeans,User user) throws Exception;
	
	/**
	 *	贴现控制查询接口(CP20220418)
	 * */
	public ReturnMessageNew txQueryConfig(User user) throws Exception;
	
	/**
	 * 贴现控制维护接口(CP20220417)
	 * */
	public boolean txSaveConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 贴现指导利率查询接口(CP20220416)
	 * */
	public ReturnMessageNew txQueryGuideRate(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 贴现有效指导利率查询接口(CP20220415)
	 * */
	ReturnMessageNew txQueryAviRate(User user) throws Exception;
	
	/**
	 * 在线贴现优惠利率查询接口(CP20220414)
	 * */
	public ReturnMessageNew txQueryFavorRate(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 在线贴现指导利率维护接口(CP20220413)
	 * */
	public boolean txGuideRateMaintain(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 在线贴现指导利率维护接口(CP20220412)
	 * */
	public boolean txFavorRateMaintain(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 在线贴现指导利率维护接口(CP20220411)
	 * */
//	public boolean txBestFavorRateMaintain(TxRateMaintainInfo txRateMaintainInfo,User user) throws Exception;
	
	
	/**
	 * 票据审价信息查询 (CP20220410)
	 * */
	public ReturnMessageNew billPriceQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception;

	
	/**
	 * 票据审价维护接口(CP20220409)
	 * */
	ReturnMessageNew txBillPriceMaintain(TxReviewPriceInfo info,User user) throws Exception ;
	
	/**
	 * 行别映射关系查询接口(CP20220408)
	 * */
	public ReturnMessageNew txQueryBankRoleMapping(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 行别映射关系维护接口(CP20220407)
	 * */
	public boolean txBankRoleMappingSave(BankRoleMappingBean bankRoleMappingBean,User user) throws Exception;
	
	
	/**
	 * 在线贴现协议查询接口(CP20220406)
	 * */
	public ReturnMessageNew txQueryOnlineProtocol(CenterPlatformBean centerPlatformBean,User user) throws Exception;

	/**
	 * 额度审价信息设置(CP2022040501)
	 * */
	public ReturnMessageNew txAmtReciewPriceMaintain1(TxReviewPriceInfo info,User user) throws Exception;
	
	/**
	 * 额度审价信息设置(CP2022040502)
	 * */
	public ReturnMessageNew txAmtReciewPriceMaintain2(TxReviewPriceInfo info,User user) throws Exception;
	
	/**
	 * 票据审价信息查询 (CP20220404)
	 * */
	public ReturnMessageNew AmtBillPriceQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 在线贴现协议变更查询(CP20220403)
	 * */
	ReturnMessageNew OnLineTxAgreeChangeQuery(CenterPlatformBean centerPlatformBean,User user)throws Exception;
	
	/**
	 * 在线贴现协议变更查询(CP20220402)
	 * */
	ReturnMessageNew OnLineChangeDetailQuery(CenterPlatformBean centerPlatformBean,User user)throws Exception;
	
	/**
	 * 在线贴现最优惠利率查询(CP20220401)
	 * */
//	public ReturnMessageNew txQueryBestRate(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	ReturnMessageNew txQueryOnlineConfig(CenterPlatformBean centerPlatformBean,User user)throws Exception;
	
	/**
	 * 在线贴现业务跟踪查询
	 * */
	public ReturnMessageNew onlineTxBusinessQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 在线贴现业务详情查询
	 * */
	public ReturnMessageNew onlineTxBusinessDetail(CenterPlatformBean centerPlatformBean,User user) throws Exception;
	
	/**
	 * 获取实际利率查询(CP20220431)
	 * */
	public ReturnMessageNew ActualRateInfos(String protocolNo, User user) throws Exception ;
	
	public void closeBusiness(User user,CenterPlatformBean centerPlatformBean) throws Exception ;
}
