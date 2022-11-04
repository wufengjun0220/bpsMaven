package com.mingtech.application.pool.draft.web;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.ecd.domain.EndorsementLog;
import com.mingtech.application.ecd.service.EndorsementLogService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.Trans2RMBUtils;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 票据信息获取
 * @author ice
 * 描述：对票据基本信息、归属信息、行内流转信息、背书信息、影像信息、止付信息进行查询
 */
@Controller
public class BillInfoController extends BaseController {
	private static final Logger logger = Logger.getLogger(BillInfoController.class);
	@Autowired
	private EndorsementLogService endorsementLogService;//背书日志信息
	@Autowired
	private DraftPoolInService draftPoolInService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	
	/**
	 * 查询票据基本信息(含票据背书信息)
	 * 汉口票据池票面信息查询
	 * @param SBillNo 票号
	 * @param id 业务id
	 * @param type 查询票据类型  0：大票表  1：入池表  2：出池表  3：贴现表  4：托收表   5:库存表 	6:银承明细表
	 */
	@RequestMapping("getBillInfo")
	public void getBillInfo(String SBillNo,String beginRangeNo, String endRangeNo,String id,String type){
		String json = "";
		try {
			/*
			 * 票据正面信息组装
			 */
			Date curDate = new Date();
			if(StringUtils.isBlank(beginRangeNo)){
				beginRangeNo = "0";
			}
			if(StringUtils.isBlank(endRangeNo)){
				endRangeNo = "0";
			}
			PoolBillInfo oldBillInfo = draftPoolInService.loadByBillNo(SBillNo,beginRangeNo,endRangeNo);
			
			if(oldBillInfo == null){
				if(type != null && (type.equals("6") || type.equals("7"))){
					oldBillInfo = new PoolBillInfo();
					/**
					 * 实时去电票系统查询票据状态
					 */
					 /*
				     * 调用BBSP系统正面信息查询接口
				     */
					ECDSPoolTransNotes transNotes = new ECDSPoolTransNotes();
					if(type.equals("6")){
						PlOnlineAcptDetail detail = (PlOnlineAcptDetail) draftPoolInService.load(id,PlOnlineAcptDetail.class);
						transNotes.setBillId(detail.getBillId());
						transNotes.setBillSource(detail.getDraftSource());
						transNotes.setAcctNo(detail.getPayeeAcct());
						transNotes.setDataSource("3");
						if(detail.getDraftSource().equals(PoolComm.CS02)){
							transNotes.setBeginRangeNo(detail.getBeginRangeNo());
							transNotes.setEndRangeNo(detail.getEndRangeNo());
						}
						transNotes.setCurrentPage(1);
						transNotes.setPageSize(10);
						
						ReturnMessageNew response = poolEcdsService.txApplyQueryBillFace(transNotes);
						if(response.isTxSuccess()){
							List<Map> list = response.getDetails();//从BBSP系统查回的正面信息
							if(list !=null && list.size() >0 ){
								for (Map map : list) {
									
									List<Map> bills = (List<Map>) map.get("BILL_INFO_ARRAY");
									if(bills != null){
										for (Map billMap : bills) {
											oldBillInfo.setDiscBillId(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_ID")));
											oldBillInfo.setSIssuerName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_NAME")));
											oldBillInfo.setSIssuerAccount(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_ACCT_NO")));
											oldBillInfo.setSIssuerBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NAME")));
											oldBillInfo.setSIssuerBankCode(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NO")));
											oldBillInfo.setSPayeeName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_NAME")));
											oldBillInfo.setSPayeeAccount(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_ACCT_NO")));
											oldBillInfo.setSPayeeBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NAME")));
											oldBillInfo.setSPayeeBankCode(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NO")));
											oldBillInfo.setSAcceptorAccount(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO")));
											oldBillInfo.setSAcceptorBankCode(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")));
											oldBillInfo.setSAcceptorBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));
											oldBillInfo.setSAcceptor(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_NAME")));
											oldBillInfo.setSBanEndrsmtFlag(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.UNENDORSE_FLAG")));
											oldBillInfo.setSBillNo(SBillNo);
											oldBillInfo.setSBillType(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_TYPE")));
											oldBillInfo.setSBillMedia("2");
											oldBillInfo.setDIssueDt(detail.getIsseDate());
											oldBillInfo.setDDueDt(detail.getDueDate());
											oldBillInfo.setFBillAmount(detail.getBillAmt());
											if(detail.getDraftSource().equals(PoolComm.CS01)){
												oldBillInfo.setSDealStatus(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ECDS_BILL_STATUS")));
											}else if (detail.getDraftSource().equals(PoolComm.CS02)){
												oldBillInfo.setSDealStatus(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.MIS_BILL_STATUS")));
											}
										}
									}
								}
								
								logger.info("金额："+detail.getBillAmt());
								logger.info("状态："+oldBillInfo.getSDealStatusName());
							}
							
						}
					}else{	//	在线贴现
						transNotes.setBeginRangeNo(beginRangeNo);
						transNotes.setBillNo(SBillNo);
						transNotes.setDataSource("3");
						transNotes.setEndRangeNo(endRangeNo);
						if(SBillNo.substring(0, 1).equals("1") || SBillNo.substring(0, 1).equals("2")) {
							//票号首号为1或2 表示是ecds来源的票 直接查询正面接口
							transNotes.setBillSource(PoolComm.CS01);
						}else{
							transNotes.setBillSource(PoolComm.CS02);
						}
						transNotes.setCurrentPage(1);
						transNotes.setPageSize(10);
						
						ReturnMessageNew response = poolEcdsService.txApplyQueryBillFace(transNotes);
						if(response.isTxSuccess()){
							List<Map> list = response.getDetails();//从BBSP系统查回的正面信息
							if(list !=null && list.size() >0 ){
								for (Map map : list) {
									
									List<Map> bills = (List<Map>) map.get("BILL_INFO_ARRAY");
									if(bills != null){
										for (Map billMap : bills) {
											oldBillInfo.setDiscBillId(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_ID")));
											oldBillInfo.setSIssuerName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_NAME")));
											oldBillInfo.setSIssuerAccount(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_ACCT_NO")));
											oldBillInfo.setSIssuerBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NAME")));
											oldBillInfo.setSIssuerBankCode(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NO")));
											oldBillInfo.setSPayeeName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_NAME")));
											oldBillInfo.setSPayeeAccount(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_ACCT_NO")));
											oldBillInfo.setSPayeeBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NAME")));
											oldBillInfo.setSPayeeBankCode(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NO")));
											oldBillInfo.setSAcceptorAccount(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO")));
											oldBillInfo.setSAcceptorBankCode(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")));
											oldBillInfo.setSAcceptorBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));
											oldBillInfo.setSAcceptor(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_NAME")));
											oldBillInfo.setSBanEndrsmtFlag(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.UNENDORSE_FLAG")));
											oldBillInfo.setSBillNo(SBillNo);
											oldBillInfo.setSBillType(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_TYPE")));
											oldBillInfo.setSBillMedia("2");
											oldBillInfo.setDIssueDt(DateUtils.parseDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAW_BILL_DATE"))));
											oldBillInfo.setDDueDt(DateUtils.parseDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.EXPIRY_DATE"))));
											oldBillInfo.setFBillAmount(new BigDecimal(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_AMT"))));
											oldBillInfo.setSDealStatus(StringUtil.isNotEmpty(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.MIS_BILL_STATUS")))?StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.MIS_BILL_STATUS")):StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ECDS_BILL_STATUS")));
										}
									}
								}
								
//								logger.info("金额："+detail.getBillAmt());
//								logger.info("状态："+oldBillInfo.getSDealStatusName());
							}
							
						}
					}
				}else{
					this.getResponse().setStatus(400);
					this.sendJSON("票据信息不存在。");
					return;
				}
			}
			if(type != null){
				if(type.equals("1")){
					DraftPoolIn in = (DraftPoolIn) draftPoolInService.load(id,DraftPoolIn.class);
					oldBillInfo.setSDealStatus(in.getPlStatus());
				}else if(type.equals("2")){
					DraftPoolOut out = (DraftPoolOut) draftPoolInService.load(id,DraftPoolOut.class);
					oldBillInfo.setSDealStatus(out.getPlStatus());
				}else if(type.equals("3")){
					PlDiscount discount = (PlDiscount) draftPoolInService.load(id,PlDiscount.class);
					oldBillInfo.setSDealStatus(discount.getSBillStatus());
				}else if(type.equals("4")){
					CollectionSendDto send = (CollectionSendDto) draftPoolInService.load(id,CollectionSendDto.class);
					oldBillInfo.setSDealStatus(send.getSBillStatus());
				}else if(type.equals("5")){
					DraftPool pool = (DraftPool) draftPoolInService.load(id,DraftPool.class);
					oldBillInfo.setSDealStatus(pool.getAssetStatus());
				}
			}
			
			/* 
			 * 金额信息组装 
			 */
			char[] moneyChar=new char[12];
			/* 测试数据未能保存小数点后两位，因此手动加两位小数，后删除 */
			String billMoney=(oldBillInfo.getFBillAmount().setScale(2)).toString();
			/* 设置大写金额 */
			String upperMoney=Trans2RMBUtils.getRMBStr(billMoney);
			double baseMoney=1000000000.00;
			if(Double.valueOf(billMoney).doubleValue()>=baseMoney){
				this.getResponse().setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				this.sendJSON("当前票据金额￥"+billMoney+"超过或等于金额最大上限￥1000000000.00！");
				return;
			}else if(billMoney.length()-1<moneyChar.length){
				billMoney="￥"+billMoney;
			}
			char[] tmp=billMoney.toCharArray();
			int count=tmp.length-4;
			moneyChar[10]=tmp[tmp.length-1];
			moneyChar[9]=tmp[tmp.length-2];
			for(int i=8;i>=0;i--){
				if(count>=0&&tmp[count]!='.'){
					moneyChar[i]=tmp[count];
				}else{
					moneyChar[i]=' ';
				}
				count--;
			}
			
			/*
			 * 贴现前背书信息组装
			 */
			List drwrGuarnteeList = new ArrayList();//出票保证人
			List accptrGuarnteeList = new ArrayList();//承兑保证人
			//查询背书信息
			List endorseInfoList =  endorsementLogService.getELogsByEId(oldBillInfo.getDiscBillId()); // 获取票据历史交易背书信息 
			if(null!=endorseInfoList){
			    int endorseCount = endorseInfoList.size();
			    /*取得出票保证和承兑保证信息*/
				for(int i=0; i<endorseCount; i++){
					// 判断是否是保证出票业务
					if("017".equals(((EndorsementLog)endorseInfoList.get(i)).getMsgTpId())){
						if(i==endorseInfoList.size()-1){
							drwrGuarnteeList.add((EndorsementLog)endorseInfoList.get(i));
							break;
						}
					}
					if("017".equals(((EndorsementLog)endorseInfoList.get(i)).getMsgTpId())&&
							"017".equals(((EndorsementLog)endorseInfoList.get(i+1)).getMsgTpId())){
						drwrGuarnteeList.add((EndorsementLog)endorseInfoList.get(i));
					}
					// 判断是否是保证承兑业务
					if("017".equals(((EndorsementLog)endorseInfoList.get(i)).getMsgTpId())&&
							"002".equals(((EndorsementLog)endorseInfoList.get(i+1)).getMsgTpId())){
						accptrGuarnteeList.add((EndorsementLog)endorseInfoList.get(i));
					}
				}
			}
			
			
			/*
			 * 贴现后信息组装
			 */

			
			Map dataMap = new HashMap();
			dataMap.put("billInfo", oldBillInfo);//票据信息
			dataMap.put("upperMoney", upperMoney);//金额大写
			dataMap.put("moneyChar", moneyChar);//金额小写
			dataMap.put("strCurDateTime", DateUtils.toString(curDate,DateUtils.ORA_DATE_TIMES3_FORMAT));
			dataMap.put("drwrGuarnteeList", drwrGuarnteeList);//出票保证信息
			dataMap.put("accptrGuarnteeList", accptrGuarnteeList);//承兑保证信息
			dataMap.put("endorseInfoList", endorseInfoList);//背书信息

			 json = JsonUtil.fromObject(dataMap);
			 this.sendJSON(json);
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.logger.error("查询票据基本信息失败："+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询票据基本信息失败："+e.getMessage());
		}
	}
}
