package com.mingtech.application.pool.common.util;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;


public class BeanUtil{
	/**
	* <p>方法名称: beanCopy|描述: copy实体属性</p>
	* @param from 源实体
	* @param to  目标实体
	*/
	public static  void beanCopy(Object from,Object to){
		BeanUtils.copyProperties(from,to);
	}
	
	/**
	 * <p>方法名称: DraftPoolCopy|描述: copy实体属性</p>
	 * @param DraftPoolIn源实体
	 * @param DraftPool目标实体
	 */
	public static void DraftPoolCopy(DraftPoolIn in,DraftPool dp){
		beanCopy(in,dp);
		dp.setNULL();
		dp.setAssetNb(in.getPlDraftNb());//资产号码
		dp.setAssetType(in.getPlDraftType());//类型
		dp.setFloatType(PoolComm.ED_FD_NO);//浮动类型
		dp.setRickLevel(in.getRickLevel());//风险等级
		dp.setAcptHeadBankNo(in.getAcptHeadBankNo());
		dp.setAcptHeadBankName(in.getAcptHeadBankName());
		dp.setAssetAmt(in.getPlIsseAmt());//金额
		if(in.getRickLevel().equals(PoolComm.NOTIN_RISK)){
			dp.setIsEduExist(PoolComm.EDU_NOT_EXIST);   //不在风险名单中不产生额度
		}else{
			dp.setIsEduExist(PoolComm.EDU_EXIST);   //用于判断该票据是否可以产生额度
		}
		dp.setAssetStatus(in.getPlStatus());//状态
		dp.setCrtTm(new Date());
		
		dp.setAssetApplyNm(in.getPlApplyNm());//申请人名称
		dp.setAssetCommId(in.getPlCommId());//申请人组织机构代码
		dp.setAssetApplyAcctId(in.getPlApplyAcctId());//申请人账号
		dp.setAssetApplyAcctSvcr(in.getPlApplyAcctSvcr());//申请人开户行行号
		dp.setAssetApplyAcctSvcrNm(in.getPlApplyAcctSvcrNm());//申请人开户行名称
		
		dp.setAssetRecSvcr(in.getPlRecSvcr());//业务经办行行号
		dp.setAssetRecSvcrNm(in.getPlRecSvcrNm());//业务经办行名称
		dp.setWorkerName(in.getWorkerName());// 经办人姓名
		
//		dp.setAcctBankNum(in.get);    //记账网点  未记账所以为空
//		dp.setAssetLimitTotal(in.get);//衍生额度 
//		dp.setAssetLimitUsed(in.get);//已用额度
//		dp.setAssetLimitFree(in.get);//可用额度
//		dp.setAssetLimitFrzd(in.get);//已冻结额度
		dp.setBlackFlag(in.getBlackFlag());//黑灰名单标志
		dp.setChargeRate(in.getChargeRate());   //费率     
		dp.setTotalCharge(in.getTotalCharge());  //总费用
		dp.setGuaranteeNo(in.getGuaranteeNo());//担保编号
		dp.setElsignature(in.getElsignature());//电子签名
		
		dp.setBranchId(in.getBranchId());//存储网点信息 用于权限分配
		dp.setBtFlag(in.getBtFlag());//商票保贴额度标识
		dp.setAccptrOrg(in.getAccptrOrg());//承兑人组织机构代码
		dp.setLockz(PoolComm.BBSPLOCK_02);//未枷锁
		
		
		/*** 融合改造新增字段  start*/
		dp.setBeginRangeNo(in.getBeginRangeNo());//票据开始子区间号
		dp.setEndRangeNo(in.getEndRangeNo());//票据结束子区间号
		dp.setStandardAmt(in.getStandardAmt());//标准金额
		dp.setTradeAmt(in.getTradeAmt());//交易金额(等分化票据实际交易金额)
		dp.setDraftSource(in.getDraftSource());//票据来源
		dp.setSplitFlag(in.getSplitFlag());//是否允许拆分标记 1是 0否
		dp.setHilrId(in.getHilrId());
		dp.setTranId(in.getTransId());
		
		dp.setPlDrwrAcctName(in.getPlDrwrAcctName());//出票人账户名称
		dp.setPlPyeeAcctName(in.getPlPyeeAcctName());//收款人账户名称
		dp.setPlAccptrAcctName(in.getPlAccptrAcctName());//承兑人账户名称
		
		
		/*** 融合改造新增字段  end*/
		
	}
}
