package com.mingtech.application.pool.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.bbsp.client.EcdsClient;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.base.domain.BoCcmsPartyinf;
import com.mingtech.application.pool.base.domain.BoCcmsPartyinfBean;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.BankMember;
import com.mingtech.application.pool.common.domain.GuarantDiscMapping;
import com.mingtech.application.pool.common.domain.MisBankMember;
import com.mingtech.application.pool.common.domain.PedBlackList;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.PoolCommonQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: pengdaochang
 * @日期: 2010-12-16 下午01:25:56
 * @描述: [BlackListBankManageServiceImpl]黑名单管理实现类
 */
@Service("blackListManageService")
public class BlackListManageServiceImpl extends GenericServiceImpl implements BlackListManageService {
	private static final Logger logger = Logger.getLogger(BlackListManageServiceImpl.class);
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	EcdsClient ecdsClient;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private AssetRegisterService assetRegisterService; 
	@Autowired
	private AuditService auditService;
	@Autowired
	private DraftPoolInService draftPoolInService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	@Autowired
	private RoleService roleService;
	
	private String checkFlagGray = "02";
	private String checkFlagBlack = "01";

	public String loadBlackListJSON(PedBlackList blackList, Page page,User user) throws Exception {
		List list = this.loadBlackList(blackList, page,user);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}

	public String loadGrayListJSON(PedBlackList blackList, Page page) throws Exception {
		List list = this.loadGrayList(blackList, page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}

	/**
	 * <p>
	 * 方法名称: loadBlackList|描述: 查询黑名单列表
	 * </p>
	 * 
	 * @param blackList 黑名单对象
	 * @param page      分页对象
	 * @return
	 */
	private List loadBlackList(PedBlackList blackList, Page page,User user) {

		StringBuffer sb = new StringBuffer();
		sb.append("select b from PedBlackList b where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				sb.append(" and b.netNo in (?) ");
				valueList.add(resultList);
			}
		}

		if (StringUtils.isNotBlank(blackList.getType())) { // 名单类型（黑/灰名单）
			sb.append("and b.type = ? ");
			valueList.add(blackList.getType());
		}
		
		if (StringUtils.isNotBlank(blackList.getOrgCode())) { // 名单类型（黑/灰名单）
			sb.append("and b.orgCode = ? ");
			valueList.add(blackList.getOrgCode());
		}
		if (StringUtils.isNotBlank(blackList.getCustomerName())) { // 校验内容-关键值
			sb.append("and b.customerName like ? ");
			valueList.add("%" + blackList.getCustomerName() + "%");
		}

		if (StringUtils.isNotBlank(blackList.getKeywords())) { // 校验内容-关键词
			sb.append("and b.keywords = ? ");
			valueList.add(blackList.getKeywords());
		}
		if (StringUtils.isNotBlank(blackList.getContent())) { // 校验内容-关键值
			sb.append("and b.content like ? ");
			valueList.add("%" + blackList.getContent() + "%");
		}

		if (StringUtils.isNotBlank(blackList.getBillType())) { // 票据类型
			sb.append("and b.billType = ? ");
			valueList.add(blackList.getBillType());
		}

		if (StringUtils.isNotBlank(blackList.getBillMedia())) { // 票据介质
			sb.append("and b.billMedia = ? ");
			valueList.add(blackList.getBillMedia());
		}

		if (StringUtils.isNotBlank(blackList.getDataFrom())) { // 数据来源
			sb.append("and b.dataFrom = ? ");
			valueList.add(blackList.getDataFrom());
		}

		sb.append("order by b.createTime desc ");
		return find(sb.toString(), valueList, page);

		
	}

	/**
	 * <p>
	 * 方法名称: loadBlackList|描述: 查询灰名单列表
	 * </p>
	 * 
	 * @param PedBlackList 灰名单对象
	 * @param page         分页对象
	 * @return
	 */
	private List loadGrayList(PedBlackList blackList, Page page) {

		StringBuffer sb = new StringBuffer();
		sb.append("select b from PedBlackList b where b.type = ? ");
		List valueList = new ArrayList(); // 要查询的值列表
		valueList.add(blackList.getType());

		if (StringUtils.equals(blackList.getType(), PoolComm.GRAY)) { // 灰名单
			if (StringUtils.isNotBlank(blackList.getKeywords())) { // 关键字
				sb.append("and b.keywords = ? ");
				valueList.add(blackList.getKeywords());
			}
			if (StringUtils.isNotBlank(blackList.getContent())) { // 内容
				sb.append("and b.content like ? ");
				valueList.add("%" + blackList.getContent() + "%");
			}

			if (StringUtils.isNotBlank(blackList.getBillType())) { // 票据类型
				sb.append("and b.billType = ? ");
				valueList.add(blackList.getBillType());
			}

			if (StringUtils.isNotBlank(blackList.getBillMedia())) { // 票据介质
				sb.append("and b.billMedia = ? ");
				valueList.add(blackList.getBillMedia());
			}
		}

		sb.append("order by b.createTime desc ");
		return this.find(sb.toString(), valueList, page);

	}
	
	@Override
	public PoolBillInfo txBlacklistCheck(PoolBillInfo pool, String custNo) throws Exception {
		
		logger.info("票据：" + pool.getSBillNo() + " 黑名单及风险检查开始......");
		String blackComment = " ";// 黑名单说明
		String riskComment = " ";// 风险说明
		/*
		 * 黑名單校驗
		 */
		String checkResult = this.blackListCheck(pool, custNo);
		
		if (PoolComm.BLACK.equals(checkResult)) {// 黑名单中
			pool.setBlackFlag(PoolComm.BLACK);
			blackComment = "黑名单票据";
		} else if (PoolComm.GRAY.equals(checkResult)) {// 灰名单中
			pool.setBlackFlag(PoolComm.GRAY);
			blackComment = "灰名单票据";
			
//			if(PoolComm.NOT_ATTRON_FLAG_YES.equals(pool.getSBanEndrsmtFlag())){//不得转让
//				pool.setRickLevel(PoolComm.NOTIN_RISK);
//				riskComment += "|非本行名单票据";
//			}

		} else {
			pool.setBlackFlag(PoolComm.WHITE);
			blackComment = "非黑灰名单票据";
			
//			if(PoolComm.NOT_ATTRON_FLAG_YES.equals(pool.getSBanEndrsmtFlag())){//不得转让
//				pool.setRickLevel(PoolComm.NOTIN_RISK);
//				riskComment += "|非本行名单票据";
//			}
		}
		pool.setRiskComment(blackComment + "|" + riskComment);
		
		logger.info("票据：" + pool.getSBillNo() + " 黑名单及风险检查结束，检查结果为：" + pool.getRiskComment() + "，黑名单标识blackFlag："+ pool.getBlackFlag() + ",风险标识reskLevel：" + pool.getRickLevel());
		
		return pool;
	}

	/**
	 * 黑名单及风险检查
	 * 
	 * @Description: 若票据在黑名单中，则禁止入池，灰名单可入池，不在黑名单中可入池
	 * @return PoolBillInfo
	 * @author Ju Nana
	 * @date 2018-11-21 上午9:40:47
	 */
	@Override
	public PoolBillInfo txBlacklistAndRiskCheck(PoolBillInfo pool, String custNo) throws Exception {
		logger.info("票据：" + pool.getSBillNo() + " 黑名单及风险检查开始......");
		String blackComment = " ";// 黑名单说明
		String riskComment = " ";// 风险说明
		/*
		 * 黑名單校驗
		 */
		String checkResult = this.blackListCheck(pool, custNo);
		
		if (PoolComm.BLACK.equals(checkResult)) {// 黑名单中
			pool.setBlackFlag(PoolComm.BLACK);
			blackComment = "黑名单票据";
		} else if (PoolComm.GRAY.equals(checkResult)) {// 灰名单中
			pool.setBlackFlag(PoolComm.GRAY);
			blackComment = "灰名单票据";
			/*
			 * 风险校验
			 */
			pool = this.riskCheck(pool);// 风险校验
		} else {
			pool.setBlackFlag(PoolComm.WHITE);
			blackComment = "非黑灰名单票据";
			/*
			 * 风险校验
			 */
			pool = this.riskCheck(pool);// 风险校验
		}

		String riskLevel = pool.getRickLevel();
		if (PoolComm.LOW_RISK.equals(riskLevel)) {
			riskComment = "低风险票据";
		} else if (PoolComm.HIGH_RISK.equals(riskLevel)) {
			riskComment = "高风险票据";
		} else {
			riskComment = "非本行名单票据";
		}

		pool.setRiskComment(blackComment + "|" + riskComment);
		
		logger.info("票据：" + pool.getSBillNo() + " 黑名单及风险检查结束，检查结果为：" + pool.getRiskComment() + "，黑名单标识blackFlag："+ pool.getBlackFlag() + ",风险标识reskLevel：" + pool.getRickLevel());
		return pool;
	}

	/**
	 * 风险检查：
	 * 
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2018-11-21 上午9:44:54
	 */
	private PoolBillInfo riskCheck(PoolBillInfo pool) throws Exception {// DraftPoolBase
		logger.info("票据：" + pool.getSBillNo() + " 风险检查开始......");
		String billNoTopFour = StringUtil.substring(pool.getSBillNo(), 0, 4);// 票号前四位
		boolean lowRiskFlag = false;//低风险标识
		boolean highRiskFlag = false;//高风险标识
		String billType = pool.getSBillType();//票据类型
		String billMedia = pool.getSBillMedia();//票据介质
		String bankNo = pool.getSAcceptorBankCode();// 承兑人开户行
		
		logger.info("票号：" + pool.getSBillNo() + " 票据类型："+ billType +" 票据介质："+billMedia+" 承兑人开户行："+bankNo +"禁止背书标记："+pool.getSBanEndrsmtFlag());
		
		if(!billNoTopFour.equals("1907")&&PoolComm.BILL_TYPE_BANK.equals(billType)){//非1907开头银承
			if(billMedia.equals(PoolComm.BILL_MEDIA_PAPERY)){//纸票
				lowRiskFlag = this.paperLowRiskCheck(pool);
			}else{//电票				
				lowRiskFlag = lowRiskCheck(pool);
			}
		}else{//高风险校验
			PoolCommonQueryBean queryBean = new PoolCommonQueryBean();
			queryBean.setAcptBankNo(pool.getSAcceptorBankCode());
			queryBean.setAcptAcctNo(pool.getSAcceptorAccount());
			queryBean.setAcptname(pool.getSAcceptorBankName());
			
			highRiskFlag = this.checkAccptr(queryBean);
		}
		
		if (lowRiskFlag) {
			pool.setRickLevel(PoolComm.LOW_RISK);
		}
		if (highRiskFlag) {
			pool.setRickLevel(PoolComm.HIGH_RISK);
		}
		if (lowRiskFlag == false && highRiskFlag == false) {
			pool.setRickLevel(PoolComm.NOTIN_RISK);
		}
		return pool;
	}

	/**
	 * 电子银承票据，低风险校验 ，校验条件：
	 *  1.票据类型满足：  1）电子银行承兑汇票 
	 *                 2）非1907开头财务公司承兑的票据
	 *                 3）非代理票据（即“承兑行账号”为0的票据） 
	 *                 4）非禁止背书票据
	 *  2.票据承兑行满足：
	 *    1）国股：四大行、政策性银行、邮储
	 *            四大行：102-工商银行、103-农业银行、104-中国银行、105-建设银行
	 *            政策性银行：201-国家开发银行、202-中国进出口银行、203-中国农业发展银行
	 *            邮储：403-邮政储汇局
	 *    2）股份制：301-交通、302-中信、303-光大、304-华夏、305-民生、306-广发、783-平安、308-招商、309-兴业、310浦发
	 *    3）城商行：313-城商行、315-恒丰、317-浙商、318-渤海、319-徽商、321-重庆三峡、322-上海农商、325-上海银行、323-民营银行
	 *    4）暂不接受“三农类银行”、“外资银行”以及其他类的银行
	 *    
	 *    
	 *    备注：按照林丛林要求，去掉315恒丰银行
	 * 
	 * @param pool
	 * @return boolean 低风险标识
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2018-11-26 下午1:53:51
	 */
	private boolean lowRiskCheck(PoolBillInfo pool) throws Exception {// DraftPoolBase
		logger.info("电子银承票据：" + pool.getSBillNo() + " 低风险检查开始......");
		String billTppe = pool.getSBillType();// 票据类型 AC01：银承 AC02：商承
		String billNoTopFour = StringUtil.substring(pool.getSBillNo(), 0, 4);// 票号前四位
		String accptrAcct = pool.getSAcceptorAccount();// 承兑人开户行账号
		String forbidFlag = pool.getSBanEndrsmtFlag();// 禁止背书标识


		// 票据规则校验
		if (PoolComm.BILL_TYPE_BANK.equals(billTppe) && "1907" != billNoTopFour && "0" != accptrAcct && PoolComm.NOT_ATTRON_FLAG_NO.equals(forbidFlag)) {
			return true;
		}
		
		return false;
	}


	/**
	 * 黑名单及灰名单检查 （1）灰名单检查 （2）黑名单检查：如果票据同时在灰名单与黑名单中，则记录黑名单信息
	 * 
	 * @param bill
	 * @return String WHITE-不在黑名单中 BLACK-黑名单中 GRAY-灰名单中
	 * @author Ju Nana
	 * @date 2018-11-22 上午10:46:02
	 */
	public String blackListCheck(PoolBillInfo bill, String orgCode) throws Exception {
		logger.info("票据：" + bill.getSBillNo() + " 黑灰名单检查开始......");

		String checkResult = PoolComm.WHITE;
		String flag = "";
		checkResult = grayAndBlackCheck(bill, orgCode, checkFlagGray);// 灰名单校验
		flag = checkResult;// 记录灰名单状态
		checkResult = grayAndBlackCheck(bill, orgCode, checkFlagBlack);// 黑名单校验
		if (flag.equals(PoolComm.GRAY) && !checkResult.equals(PoolComm.BLACK)) {
			checkResult = flag;
		}
		return checkResult;
	}

	/**
	 * 黑名单灰名单检查
	 * 
	 * @param bill
	 * @param flag 检查标识：graylist-灰名单 blacklist-黑名单
	 * @return String 返回是否在黑名单中
	 * @author Ju Nana
	 * @date 2018-11-21 下午2:44:55
	 */
	/*
	 * 查询sql示例：ped_datafrom=’01‘黑名单录入来源为银行 ped_datafrom=’00‘黑名单录入来源为客户网银 select *
	 * from ped_blacklist where ped_type='01' and (1=2 or(ped_keywords='1' and
	 * ped_content= ？ and ped_datafrom='01') or(ped_keywords='2' and ped_content= ？
	 * and ped_datafrom='01') or(ped_keywords='3' and ped_content= ？ and
	 * ped_datafrom='01') or(ped_keywords='4' and ped_content= ？ and
	 * ped_datafrom='01') or(ped_keywords='5' and ped_content= ？ and
	 * ped_datafrom='01') or(ped_keywords='1' and ped_content= ？ and
	 * ped_datafrom='00' and ped_orgcode=？) or(ped_keywords='2' and ped_content= ？
	 * and ped_datafrom='00' and ped_orgcode=？) or(ped_keywords='3' and ped_content=
	 * ？ and ped_datafrom='00' and ped_orgcode=？) or(ped_keywords='4' and
	 * ped_content= ？ and ped_datafrom='00' and ped_orgcode=？) or(ped_keywords='5'
	 * and ped_content= ？ and ped_datafrom='00' and ped_orgcode=？) )
	 */
	private String grayAndBlackCheck(PoolBillInfo bill, String orgCode, String flag) {

		String drwrNm = bill.getSIssuerName();// 出票人名称
		String acptNm = bill.getSAcceptor();// 承兑人名称
		String acptBank = bill.getSAcceptorBankCode();// 承兑人开户行行号
		String acptBankAdd = acptBank.substring(3, 7); // 截取承兑行号4-8位与城市代码进行比较，字符串从0开始计算
		String billNo = bill.getSBillNo();// 票据号码
		String checkResult = PoolComm.WHITE;
		//int transTimes = 0;// 背书次数
		int residualMaturity = DateUtils.getDayInRange(DateUtils.getWorkDayDate(), bill.getDDueDt());// 剩余期限 =
		logger.info("工作日为:"+DateUtils.getWorkDayDate()+",到日期为:"+bill.getDDueDt()+",剩余期限为:"+residualMaturity);
		//String endorserString = "";// 背书人字段拼接
		String billMedia = bill.getSBillMedia();//票据介质
		
		
		Date today = new Date();//当前日期
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE,-1);//当前日期前一天
		Date todayBef =  cal.getTime();

		// 调用BBSP系统票据背面信息查询接口，获取背书数据
		// 到背面信息表查数据
//		if(billMedia.equals(PoolComm.BILL_MEDIA_ELECTRONICAL)){//电票
//			try {
//				List list = endorsementLogService.getELogsByEId(bill.getDiscBillId());
//				if (list != null && list.size() > 0) {
//					transTimes = list.size();
//					for (int i = 0; i < list.size(); i++) {
//						EndorsementLog sement = (EndorsementLog) list.get(i);
//						endorserString = endorserString + "'" + sement.getEndrsrNm() + "',";
//					}
//					if (transTimes > 0) {// 去除末尾逗号
//						endorserString = endorserString.substring(0, endorserString.length() - 1);
//					}
//				}
//			} catch (Exception e) {
//				logger.error(e.getMessage(),e);
//			}
//		}
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer sb = new StringBuffer();
		sb.append("select b from PedBlackList b where   b.type = :type and b.dueDt > :dueDt and (1=2 ");

		paramName.add("type");
		paramValue.add(flag);
		
		paramName.add("dueDt");
		paramValue.add(todayBef);
		
		if (StringUtil.isNotEmpty(drwrNm)) {// 出票人
			sb.append(" or(b.keywords='01' and b.content = :drwrNm1 and b.dataFrom='01' )");
			paramName.add("drwrNm1");
			paramValue.add(drwrNm);

			sb.append(" or(b.keywords='01' and b.content = :drwrNm2 and b.dataFrom='00' and b.orgCode=:orgCode )");
			paramName.add("drwrNm2");
			paramValue.add(drwrNm);
		}
		
		if (StringUtil.isNotEmpty(acptNm)) {// 承兑人
			logger.info("承兑人为:"+acptNm+",客户号为:"+orgCode+"。。。。。。。。。。。。");
			sb.append(" or(b.keywords='02' and b.content = :acptNm1 and b.dataFrom='01' )");
			paramName.add("acptNm1");
			paramValue.add(acptNm);

			sb.append(" or(b.keywords='02' and b.content = :acptNm2 and b.dataFrom='00' and b.orgCode=:orgCode  )");
			paramName.add("acptNm2");
			paramValue.add(acptNm);
		}
		if (StringUtil.isNotEmpty(acptBank)) {// 承兑行
			
			String upBankNo;
			try {
				upBankNo = this.queryTotalBankNo(acptBank);
				if(StringUtil.isNotBlank(upBankNo)){
					sb.append(" or(b.keywords='03' and b.content=:upBankNo and b.dataFrom='01'  )");
					paramName.add("upBankNo");
					paramValue.add(upBankNo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			sb.append(" or(b.keywords='03' and b.content=:acptBank1 and b.dataFrom='01'  )");
			paramName.add("acptBank1");
			paramValue.add(acptBank);
		

			sb.append(" or(b.keywords='03' and b.content=:acptBank2 and b.dataFrom='00' and b.orgCode=:orgCode )");
			paramName.add("acptBank2");
			paramValue.add(acptBank);
		}
		if (StringUtil.isNotEmpty(acptBankAdd)) {// 承兑行所在地区
			sb.append(" or(b.keywords='04' and b.city=:acptBankAdd1  and b.dataFrom='01'  )");
			paramName.add("acptBankAdd1");
			paramValue.add(acptBankAdd);

			sb.append(" or(b.keywords='04' and b.city=:acptBankAdd2  and b.dataFrom='00' and b.orgCode=:orgCode   )");
			paramName.add("acptBankAdd2");
			paramValue.add(acptBankAdd);
		}
		if (StringUtil.isNotEmpty(billNo)) {// 票号
			sb.append(" or(b.keywords='05' and b.content = :billNo1  and b.dataFrom='01'  )");
			paramName.add("billNo1");
			paramValue.add(billNo);

			sb.append(" or(b.keywords='05' and b.content = :billNo2  and b.dataFrom='00' and b.orgCode=:orgCode   )");
			paramName.add("billNo2");
			paramValue.add(billNo);
		}

//		if (StringUtil.isNotEmpty(endorserString)) {// 背书人
//			sb.append(" or(b.keywords='06' and :endorserString1 like '%'||b.content||'%'  and b.dataFrom='01'  )");
//			paramName.add("endorserString1");
//			paramValue.add(endorserString);
//
//			sb.append(" or(b.keywords='06' and :endorserString2 like '%'||b.content||'%'  and b.dataFrom='00' and b.orgCode=:orgCode   )");
//			paramName.add("endorserString2");
//			paramValue.add(endorserString);
//		}
//
//		if (transTimes > 0) {// 流转次数
//			sb.append(" or(b.keywords='07' and b.content<=:transTimes1 and b.dataFrom='01'  )");
//			paramName.add("transTimes1");
//			paramValue.add(transTimes+"");
//
//			sb.append(" or(b.keywords='07' and b.content<=:transTimes2 and b.dataFrom='00' and b.orgCode=:orgCode )");
//			paramName.add("transTimes2");
//			paramValue.add(transTimes+"");
//		}

		if (residualMaturity > 0) {// 剩余期限
			sb.append(" or(b.keywords='08' and b.content> "+residualMaturity+" and b.dataFrom='01'  )");
//			paramName.add("residualMaturity1");
//			paramValue.add(residualMaturity);

			sb.append(" or(b.keywords='08' and b.content> "+residualMaturity+"  and b.dataFrom='00' and b.orgCode=:orgCode )");
//			paramName.add("residualMaturity2");
//			paramValue.add(residualMaturity);
		}
		
		paramName.add("orgCode");
		paramValue.add(orgCode);
		
		sb.append(")");
		System.out.println(paramName);
		System.out.println(paramValue);
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List resultList = this.find(sb.toString(), paramNames, paramValues);

		if(flag.equals("02")){
			logger.info("查询类型为灰名单:查询结果是否在灰名单中根据查询的数据有:"+resultList.size()+"条判断");
		}else{
			logger.info("查询类型为黑名单:查询结果是否在黑名单中根据查询的数据有:"+resultList.size()+"条判断");
		}
		if (flag == checkFlagGray && resultList != null && resultList.size() > 0) {
			checkResult = PoolComm.GRAY;
		}
		if (flag == checkFlagBlack && resultList != null && resultList.size() > 0) {
			checkResult = PoolComm.BLACK;
		}
		logger.info("01为黑名单，02为灰名单；返回的黑灰名单类型为:"+checkResult);
		return checkResult;
		
		//获取该行下辖的行号
//		List acceptorBankNos = this.querySubBankNo(bankNo);		
//		paramName.add("acceptorBankNos");
//		paramValue.add(acceptorBankNos);

	}

	/**
	 * 纸质银票低风险校验 ，校验条件：
	 *  1.票据类型满足：  1）纸质银行承兑汇票 
	 *                 2）非禁止背书票据
	 *  2.票据承兑行满足：
	 *    1）国股：四大行、政策性银行、邮储
	 *            四大行：102-工商银行、103-农业银行、104-中国银行、105-建设银行
	 *            政策性银行：201-国家开发银行、202-中国进出口银行、203-中国农业发展银行
	 *            邮储：403-邮政储汇局
	 *    2）股份制：301-交通、302-中信、303-光大、304-华夏、305-民生、306-广发、783-平安、308-招商、309-兴业、310浦发
	 *    3）城商行：313-城商行、315-恒丰、317-浙商、318-渤海、319-徽商、321-重庆三峡、322-上海农商、325-上海银行、323-民营银行
	 *    4）暂不接受“三农类银行”、“外资银行”以及其他类的银行
	 *    
	 *    
	 *    备注：按照林丛林要求去掉 315恒丰银行
	 *    
	 * @author Ju Nana
	 * @param pool
	 * @return
	 * @throws Exception 
	 * @date 2019-6-12上午8:43:20
	 */
	private boolean paperLowRiskCheck(PoolBillInfo pool) throws Exception {
		logger.info("纸质银承票据：" + pool.getSBillNo() + " 低风险检查开始......");
		String billTppe = pool.getSBillType();// 票据类型 AC01：银承 AC02：商承
		String draftMedia = pool.getSBillMedia();// 票据介质
		String forbidFlag = pool.getSBanEndrsmtFlag();// 禁止背书标识
		
		logger.info("票号：" + pool.getSBillNo() + " 票据类型："+ billTppe +" 票据介质："+draftMedia +"禁止背书标记："+forbidFlag);
		
		boolean lowRiskFlag = false;

		// 票据规则校验
		if (PoolComm.BILL_TYPE_BANK.equals(billTppe) && PoolComm.NOT_ATTRON_FLAG_NO.equals(forbidFlag)) {
			lowRiskFlag = true;
		}

		return lowRiskFlag;
	}

	public Class getEntityClass() {
		return PedBlackList.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}
	/**
	 * 根据行号获取下级行号
	 * @Description 
	 * @author liuxiaodong
	 * @return
	 * @date 2019-6-25上午11:39:01
	 */
	public List loadbankSubordinate(BoCcmsPartyinf pb,Page page) throws Exception{
		
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		String hql= "from BoCcmsPartyinf b where 1=1";
		if(StringUtils.isNotEmpty(pb.getSubdrtbkcd())){
			hql = hql+"  and b.subdrtbkcd =:subdrtbkcd  ";
			paramName.add("subdrtbkcd");
			paramValue.add(pb.getSubdrtbkcd());	
		}
		if(StringUtils.isNotEmpty(pb.getPrcptcd())){
			hql = hql+" and b.prcptcd =:prcptcd  ";
			paramName.add("prcptcd");
			paramValue.add(pb.getPrcptcd());	
		}
		hql = hql+" order by b.upddt ";
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		if(null!=page){
			resultList = this.find(hql,paramNames,paramValues,page);
		}else{
			resultList = this.find(hql,paramNames,paramValues);
		}
		
		if(resultList!=null && resultList.size()>0){
			return resultList;
		}
		return null;
		
	}
	@Override
	public List exportbankSubordinate(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			BoCcmsPartyinfBean dto = null;
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[4];
				dto = (BoCcmsPartyinfBean) res.get(i);
				s[0] = dto.getPrcptcd();
				s[1] = dto.getPtcptnm();
				s[2] = dto.getPrcptcdHigh();
				s[3] = dto.getPtcptnmHigh();
				list.add(s);
			}
		}
		return list;
	}

	/**
	 * 根据行号获取名称
	 * @Description 
	 * @author liuxiaodong
	 * @return
	 * @date 2019-6-25上午11:39:01
	 */
	public List queryByPrcptcd(String prcptcd) throws Exception{
		Map reMap = new HashMap();
		List list = new ArrayList();
		BoCcmsPartyinf boCcmsPartyinf = this.queryByPrcptcdNo(prcptcd);
		if(null != boCcmsPartyinf){
			reMap.put("msg", boCcmsPartyinf.getPtcptnm());
			list.add(reMap);
			return list;
		}
		return null;
	}
	public BoCcmsPartyinf queryByPrcptcdNo(String prcptcd) throws Exception {
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		String hql= "from BoCcmsPartyinf b where 1=1 ";
		if(StringUtils.isNotEmpty(prcptcd)){
			hql = hql+" and b.prcptcd =:prcptcd  ";
			paramName.add("prcptcd");
			paramValue.add(prcptcd);	
		}
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues);
		if(resultList!=null && resultList.size()>0){
			BoCcmsPartyinf boCcmsPartyinf = (BoCcmsPartyinf) resultList.get(0);
			return boCcmsPartyinf;
		}
		return null;
	}
	
	
	/**
	 * 根据行号校验是否为汉口银行
	 * @author Ju Nana
	 * @param bankNo
	 * @return
	 * @date 2019-7-18下午2:21:32
	 */
	public boolean isHkbAccept(String bankNo){
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		String hql= "from PlHkbBank b where 1=1 ";
		
		hql = hql+" and b.bankNo =:bankNo  ";
		paramName.add("bankNo");
		paramValue.add(bankNo);	
				
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues);
		if(resultList!=null && resultList.size()>0){
			return true;	
		}
		
		return false;
		
	}
	
	/**
	 * 
	 * 批量向信贷系统进行风险校验
	 * @param  
	 * @param  
	 * @author gcj
	 * @date 20210525
	 */
	public List<PoolBillInfo> txMisCreditCheck(List<PoolBillInfo> billList ) throws Exception{
		
		logger.info("向信贷系统进行额度校验开始...");
		
		try {		
			
			//财票改造开关
			String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//在线业务总开关 
			
			if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){	
				if(null != billList && billList.size()>0){		
					
					/*
					 * 额度系统额度查询
					 */
					billList = this.txMisCreditQuery(billList);
					financialAdviceService.txStoreAll(billList);//事务保存
				}
				return billList;
			}

			if(null != billList && billList.size()>0){
				
				 /*
				  * 批量向额度系统发送额度查询
				  */
				 ReturnMessageNew response1 = poolCreditClientService.txPJE011(billList);
				 List<PoolBillInfo> storeList = new ArrayList<PoolBillInfo>();//存放需要落库的票据信息
				 if(response1.isTxSuccess()){
					 /*
					  * 获取额度系统返回的列表
					  */
					 List respList = response1.getDetails();
					 
					 if(respList!=null && respList.size()>0){
						 
						 for(int i=0;i<respList.size();i++){
							 Map map =  (Map) respList.get(i);
							 if((map.get("BILL_NO")==null && map.get("begin_RangeNo")==null && map.get("end_RangeNo")==null) || map.get("AVAIL_NOMINAL_AMT")==null || map.get("AVAIL_EXEC_EXPOSURE_AMT") ==null){
								 logger.info("额度系统返回的字段有 null 值，票号：" + map.get("BILL_NO") +" 子票起始号：" + map.get("begin_RangeNo") +" 子票截止号：" + map.get("end_RangeNo") + " 可用名义金额："+ map.get("AVAIL_NOMINAL_AMT") +" 可用敞口金额："+map.get("AVAIL_EXEC_EXPOSURE_AMT") );
								 continue;
							 }
							 
							 String misBillNo = (String)map.get("BILL_NO");//票号

							 /********************融合改造新增 start******************************/
							 String misbeginRangeNo = "0";
							 String misendRangeNo = "0";
							 
							 String[] str = misBillNo.split("+");
							 if(str.length > 1){
								 misBillNo = str[0];
								 misbeginRangeNo = str[1];//子票起始号
								 misendRangeNo = str[2];//子票截止
							 }
							 
							 
							 
							/********************融合改造新增 end******************************/

							 String misNominalAmt = (String)map.get("AVAIL_NOMINAL_AMT");//可用名义金额
							 String misExecAmt = (String)map.get("AVAIL_EXEC_EXPOSURE_AMT");//可用敞口金额
							 
							 BigDecimal nominalAmt = new BigDecimal(misNominalAmt);
							 BigDecimal execAmt = new BigDecimal(misExecAmt);
							 
							 for(int j=0;j<billList.size();j++){
								 PoolBillInfo bill =(PoolBillInfo)billList.get(j);
								 if(bill.getSBillNo().equals(misBillNo) && bill.getBeginRangeNo().equals(misbeginRangeNo) && bill.getEndRangeNo().equals(misendRangeNo)){
									 if(nominalAmt.compareTo(bill.getFBillAmount())<0 || execAmt.compareTo(bill.getFBillAmount())<0 
											 || PoolComm.NOT_ATTRON_FLAG_YES.equals(bill.getSBanEndrsmtFlag())){//可用名义金额与可用均小于票面金额  或者 禁止背书的数据
										 bill.setRickLevel(PoolComm.NOTIN_RISK);
										 String[] comment = bill.getRiskComment().split("//|");
										 bill.setRiskComment(comment[0] + "|非本行额度名单票据" );
										 storeList.add(bill);
									 }
									 
								 }
								 
							 }
							 this.txStoreAll(storeList);
						 }
						 
					 }
					 
				 }else{
					 for(int j=0;j<billList.size();j++){
						 PoolBillInfo bill =(PoolBillInfo)billList.get(j);
						 bill.setRickLevel(PoolComm.NOTIN_RISK);
						 String[] comment = bill.getRiskComment().split("//|");
						 bill.setRiskComment(comment[0] + "|非本行额度名单票据" );
						 storeList.add(bill);
					 }
					 this.txStoreAll(storeList);
				 }
			}
			
		} catch (Exception e) {
			logger.info("额度系统查询处理异常："+ e);
			List<PoolBillInfo> storeList = new ArrayList<PoolBillInfo>();//存放需要落库的票据信息
			for(int j=0;j<billList.size();j++){
				PoolBillInfo bill =(PoolBillInfo)billList.get(j);
				bill.setRickLevel(PoolComm.NOTIN_RISK);
				if(bill.getRiskComment() != null){
					String[] comment = bill.getRiskComment().split("//|");
					bill.setRiskComment(comment[0] + "|非本行额度名单票据" );
				}
				storeList.add(bill);
				
			}
			return billList;
		}
		
		return null;
				
	}
	

	@Override
	public boolean checkAccptr(PoolCommonQueryBean queryBean) throws Exception {
		
		logger.info("入池商票、财票承兑行信息检查...");
		
		if(this.checkAccptrMainList(queryBean)){//承兑行主表校验
			return true;
		}
		
		if(this.checkAccptrSubList(queryBean)){//承兑行子表校验
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * 入池商票、财票承兑行主表校验
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-24上午11:42:03
	 */
	private boolean checkAccptrMainList(PoolCommonQueryBean queryBean) throws Exception {
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		String hql = "select main from AcptCheckMainlist main where main.bankNo = :acceptorBank ";

		paramName.add("acceptorBank");
		paramValue.add(queryBean.getAcptBankNo());//承兑行行号
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List resultList = this.find(hql, paramNames, paramValues);
		
		if(resultList!=null && resultList.size()>0){
			return true;
		}
		
		return false;
		
	}
	/**
	 * 入池商票、财票承兑行子表校验
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-24上午11:42:56
	 */
	private boolean checkAccptrSubList(PoolCommonQueryBean queryBean) throws Exception {
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		String hql = "select sub from AcptCheckSublist sub where 1=1 ";
		
		hql+=" and sub.bankNo = :acceptorBank ";
		paramName.add("acceptorBank");
		paramValue.add(queryBean.getAcptBankNo());//承兑行行号
		
		hql+=" and sub.acceptName = :acceptName ";
		paramName.add("acceptName");
		paramValue.add(queryBean.getAcptname());//承兑人名称
		
		hql+=" and sub.account = :account ";
		paramName.add("account");
		paramValue.add(queryBean.getAcptAcctNo());//承兑人账号
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List resultList = this.find(hql, paramNames, paramValues);
		
		if(resultList!=null && resultList.size()>0){
			return true;
		}
		
		return false;
		
	}
	/**
	 * 向MIS系统发送额度校验申请
	 * @param billList
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-31下午5:25:11
	 */
	public List<PoolBillInfo> txMisCreditQuery(List<PoolBillInfo> billList) throws Exception{
		
		logger.info("向信贷系统进行额度校验开始...");
		
		//返回数组list
		List<PoolBillInfo> returnList = new ArrayList<PoolBillInfo>();
		
		/*
		 * 【1】请求数据组装
		 */
		ReturnMessageNew retMsg = new ReturnMessageNew();		
    	Map<String,Object> map = null;
		List<Map> reqList =new ArrayList<Map>();
		for(int i=0;i<billList.size();i++){
    		map = new HashMap<String,Object>();
    		PoolBillInfo pool= billList.get(i);

    		String billNo = pool.getSBillNo();//票号
    		BigDecimal billAmt = pool.getFBillAmount();//票面金额
    		String billType = pool.getSBillType();//票据类型
    		String bankNo = pool.getSAcceptorBankCode();//承兑行行号
    		String totalBankNo = pool.getAcptHeadBankNo();//承兑行行号--总行
    		String totalBankName = pool.getAcptHeadBankName();//承兑行行名--总行
    		String acceptor = pool.getSAcceptor();//承兑人全称
    		String acptAcctNo = pool.getSAcceptorAccount();//承兑人账号
//    		String guarantDiscNo = this.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
    		Map rsuMap = this.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
    		
    		String billClass = null;
    		String billMedia = pool.getSBillMedia();//票据介质
    		logger.info("票号为："+billNo+"总行行号为："+totalBankNo);
    		if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
    			if(PoolComm.BILL_MEDIA_PAPERY.equals(billMedia)){//纸票
    				billClass = "02";//纸质商票
    			}else{//电票
    				billClass = "01";//电子商票
    			}
    			totalBankNo ="";
    			totalBankName = acceptor;
    		}else{//银票
    			if(PoolComm.BILL_MEDIA_PAPERY.equals(billMedia)){//纸票
    				billClass = "01";//纸质银票
    			}else{//电票
    				billClass = "03";//电子银票
    			}
    		}
    		
    		
			/********************融合改造新增 start******************************/
    		if(pool.getSplitFlag() != null && pool.getSplitFlag().equals("1")){
    			//可拆分的等分化票据
    			map.put("billNo", pool.getSBillNo()+"-"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo());//票号/票据包号
    		}else{
    			map.put("billNo", pool.getSBillNo());//票号/票据包号
    		}
    		
			/********************融合改造新增 end******************************/

    		map.put("billSum",billAmt );//票面金额
    		map.put("billType", billClass);//票据类型
    		map.put("bankId", totalBankNo);//承兑行行号
    		if(rsuMap != null){
    			map.put("customerName",rsuMap.get("guarantDiscName"));//承兑人名称
    			map.put("customerId",rsuMap.get("guarantDiscNo"));//保贴人编号
    		}else{
    			map.put("customerName",totalBankName);//承兑人名称
    		}

    		reqList.add(map);
    	}
		
		/*
		 * 【2】信贷额度查询接口处理
		 */
		CreditTransNotes note = new CreditTransNotes();
		note.setReqList(reqList);
		retMsg = poolCreditClientService.txPJE022(note);
		
		if(retMsg.isTxSuccess()){
			/*
			 * 获取额度系统返回的列表
			 */
			List respList = retMsg.getDetails();
			
			if(respList!=null && respList.size()>0){
				
				String billNo = ""; 
				
				String rickLevel = "";//风险等级 低风险、高风险、不在风险名单
				String riskComment = "";//风险说明
				String creditObjType = "";//额度主体类型  1-同业额度  2-对公额度
				String guarantDiscName = "";//保贴人名称           
				String guarantDiscNo = "";  //保贴编号   
				
				for(int i=0;i<respList.size();i++){
					String beginRangeNo = "0"; 
					String endRangeNo = "0"; 
					
					//额度系统返回数据处理
					
					Map misRetBillMap =  (Map) respList.get(i);
					
					//没有返回票号直接返回，这里是因为MIS系统返回的文件中会存在空行所做的异常处理
					if(null == misRetBillMap.get("billNo")){
						continue;
					}else if(StringUtil.isBlank((String)misRetBillMap.get("billNo"))){
						continue;						
					}

					billNo                            = (String)misRetBillMap.get("billNo");//票号       
					/********************融合改造新增 start******************************/
					String [] str = billNo.split("-");
					if(str.length >1){
						billNo = str[0];
						beginRangeNo = str[1];//子票起始号      
						endRangeNo = str[2];//子票截止  
						
					}
					/********************融合改造新增 end******************************/

					BigDecimal billsum                = misRetBillMap.get("billsum")!=null?new BigDecimal((String)misRetBillMap.get("billsum")):new BigDecimal("99999999999");//票面金额     
					String ty_creditNo                = misRetBillMap.get("ty_creditNo")!=null ? (String)misRetBillMap.get("ty_creditNo"):null;//同业-额度系统授信编号 
					String ty_riskFlag                = misRetBillMap.get("ty_riskFlag")!=null ? (String)misRetBillMap.get("ty_riskFlag"):null;//同业-占用票据池额度类型
					String ty_customerId              = misRetBillMap.get("ty_customerId")!=null ? (String)misRetBillMap.get("ty_customerId"):null;//同业-客户编号     
					String ty_customerName            = misRetBillMap.get("ty_customerName")!=null ? (String)misRetBillMap.get("ty_customerName"):null;//同业-客户名称     
					BigDecimal ty_availableNominalAmount = null;//同业-可用名义金额   
					if(misRetBillMap.get("ty_availableNominalAmount")!=null && StringUtils.isNotBlank((String)misRetBillMap.get("ty_availableNominalAmount"))){
						ty_availableNominalAmount = new BigDecimal((String)misRetBillMap.get("ty_availableNominalAmount"));
					}
					BigDecimal ty_availableExposureAmount = null;//同业-可用敞口金额   
					if(misRetBillMap.get("ty_availableExposureAmount")!=null && StringUtils.isNotBlank((String)misRetBillMap.get("ty_availableExposureAmount"))){
						ty_availableExposureAmount = new BigDecimal((String)misRetBillMap.get("ty_availableExposureAmount"));
					}
					
					String dg_creditNo                = misRetBillMap.get("dg_creditNo")!=null ? (String)misRetBillMap.get("dg_creditNo"):null;//对公-额度系统授信编号 
					String dg_riskFlag                = misRetBillMap.get("dg_riskFlag")!=null ? (String)misRetBillMap.get("dg_riskFlag"):null;//对公-占用票据池额度类型
					String dg_customerId              = misRetBillMap.get("dg_customerId")!=null ? (String)misRetBillMap.get("dg_customerId"):null;//对公-客户编号     
					String dg_customerName            = misRetBillMap.get("dg_customerName")!=null ? (String)misRetBillMap.get("dg_customerName"):null;//对公-客户名称     
					
					BigDecimal dg_availableNominalAmount = null;//对公-可用名义金额   
					if(misRetBillMap.get("dg_availableNominalAmount")!=null && StringUtils.isNotBlank((String)misRetBillMap.get("dg_availableNominalAmount"))){
						dg_availableNominalAmount = new BigDecimal((String)misRetBillMap.get("dg_availableNominalAmount"));
					}
					BigDecimal dg_availableExposureAmount = null;//对公-可用敞口金额   
					if(misRetBillMap.get("ty_availableExposureAmount")!=null && StringUtils.isNotBlank((String)misRetBillMap.get("dg_availableExposureAmount"))){
						dg_availableExposureAmount = new BigDecimal((String)misRetBillMap.get("dg_availableExposureAmount"));
					}
					
					//同业额度校验
					
					boolean tyIsEnough = false;//同业额度是否足够
					if(ty_availableNominalAmount!=null  && ty_availableExposureAmount!=null){					
						//可用名义金额与可用敞口金额均大于票面金额才可产生额度
						if(ty_availableNominalAmount.compareTo(billsum)>=0 && ty_availableExposureAmount.compareTo(billsum)>=0){					
							tyIsEnough = true;
						}
						if("1".equals(ty_riskFlag)){//低风险
							rickLevel = PoolComm.LOW_RISK;
						}else if("2".equals(ty_riskFlag)){//高风险
							rickLevel = PoolComm.HIGH_RISK;
						}else{//0-无风险，正常是不存在的
							rickLevel = PoolComm.NOTIN_RISK;
							riskComment = "|非本行额度名单票据";
						}
						creditObjType = PoolComm.CREDIT_OBJ_TYPE_1;//同业额度
						guarantDiscName = ty_customerName;//保贴人名称
						guarantDiscNo = ty_customerId;//保贴人编号

					}
					
					//对公额度校验
					boolean dgIsEnough = false;//保贴（对公）额度是否足够
					if(!tyIsEnough){//同业不足						
						if(dg_availableNominalAmount!=null  && dg_availableExposureAmount!=null){					
							//可用名义金额与可用敞口金额均大于票面金额才可产生额度
							if(dg_availableNominalAmount.compareTo(billsum)>=0 && dg_availableExposureAmount.compareTo(billsum)>=0){					
								dgIsEnough = true;
							}
							if("1".equals(dg_riskFlag)){//低风险
								rickLevel = PoolComm.LOW_RISK;
							}else if("2".equals(dg_riskFlag)){//高风险
								rickLevel = PoolComm.HIGH_RISK;
							}else{//0-无风险，正常是不存在的
								rickLevel = PoolComm.NOTIN_RISK;
								riskComment = "|非本行额度名单票据";
							}
							creditObjType = PoolComm.CREDIT_OBJ_TYPE_1;//同业额度
							guarantDiscName = dg_customerName;//保贴人名称
							guarantDiscNo = dg_customerId;//保贴人编号
							
						}
					}
					
					//同业/对公额度均不足时的处理
					
					if(!tyIsEnough && !dgIsEnough){//同业/对公均不足						
						rickLevel = PoolComm.NOTIN_RISK;
						creditObjType = "";
						guarantDiscName = "";//保贴人名称
						guarantDiscNo = "";//保贴人编号
						riskComment = "|非本行额度名单票据";

					}
					for(int j=0;j<billList.size();j++){
						PoolBillInfo bill =(PoolBillInfo)billList.get(j);
						if(bill.getSBillNo().equals(billNo) && (bill.getDraftSource().equals(PoolComm.CS01) || (bill.getDraftSource().equals(PoolComm.CS02) 
								&& bill.getBeginRangeNo().equals(beginRangeNo) && bill.getEndRangeNo().equals(endRangeNo)))){
							bill.setRickLevel(rickLevel);//风险等级
							bill.setCreditObjType(creditObjType);//额度主体类型
							bill.setGuarantDiscName(guarantDiscName);//保贴人名称
							bill.setGuarantDiscNo(guarantDiscNo);//保贴人核心客户号
							if(null != bill.getRiskComment()){
								String[] comment = bill.getRiskComment().split("//|");
								bill.setRiskComment(comment[0] + riskComment);
							}else{		
								bill.setRiskComment(riskComment);
							}
							logger.info("票号："+bill.getSBillNo()+"添加至返回数组中，该票风险类型为："+bill.getRickLevel());
							returnList.add(bill);
						}
						
					}
				}

			}	
		}else{
			for(int j=0;j<billList.size();j++){
				PoolBillInfo bill =(PoolBillInfo)billList.get(j);
				bill.setRickLevel(PoolComm.NOTIN_RISK);//风险等级
				bill.setCreditObjType("");//额度主体类型
				bill.setGuarantDiscName("");//保贴人名称
				bill.setGuarantDiscNo("");//保贴人核心客户号
				bill.setRiskComment("额度查询接口异常");
				logger.info("额度查询接口异常........");
				returnList.add(bill);
				
			}
		}
		return returnList;

	}
	
	/**
	 * 根据承兑人全称、承兑人账号、承兑人开户行行号查询保贴编号
	 * @param acceptor
	 * @param acptAcctNo
	 * @param acptBankNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-31下午5:27:58
	 */
	public Map queryGuarantNo(String acceptor,String acptAcctNo,String acptBankNo,String billType, String totalBankNo){
		

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String hql = "select mp.guarantDiscNo,mp.guarantDiscName from GuarantDiscMapping mp where 1=1  ";

		if(PoolComm.BILL_TYPE_BANK.equals(billType)){//银票
			List list = new ArrayList();
			list.add(acptBankNo);
			list.add(totalBankNo);
			hql += " and mp.checkType = '1' ";
			hql += " and mp.acptBankCode in(:acptBankNo) ";
			paramName.add("acptBankNo");
			paramValue.add(list);//承兑行行号
//			paramName.add("acptBankNo2");
//			paramValue.add(totalBankNo);//承兑行总行号
			
		}else{//商票
			logger.info("承兑人全称:"+acceptor+"承兑人账号:"+acptAcctNo+"承兑人开户行行号:"+acptBankNo);
			hql += " and mp.checkType = '2' ";
			hql += " and mp.acptBankCode = :acptBankNo  and mp.acceptor = :acceptor  and mp.acptAcctNo = :acptAcctNo ";
			
			paramName.add("acptBankNo");
			paramValue.add(acptBankNo);//承兑行行号
			
			paramName.add("acceptor");
			paramValue.add(acceptor);//承兑人全称
			
			paramName.add("acptAcctNo");
			paramValue.add(acptAcctNo);//承兑人账号
			
		}
		hql += " and mp.status = '1' and mp.delFlag != 'D' ";//生效的数据
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List resultList = this.find(hql, paramNames, paramValues);
		
		if(resultList!=null && resultList.size()>0){
			Object[] obj = (Object[]) resultList.get(0);
			
			String guarantNo = (String) obj[0];
			String guarantDiscName = (String) obj[1];
			Map map = new HashMap();
			map.put("guarantNo", guarantNo);
			map.put("guarantDiscName", guarantDiscName);
			return map;
		}
		
		return null;
		
	}
	
	public List<DraftPool> queryDraftPools(PoolQueryBean pq, Page page) {

		StringBuffer hql = new StringBuffer("select dto from DraftPool dto where 1=1 ");
		List keys = new ArrayList();
		List values = new ArrayList();
		
		
		if (StringUtil.isNotBlank(pq.getIsEdu())) {//是否产生额度
			hql.append(" and dto.isEduExist=:isEduExist");
			keys.add("isEduExist");
			values.add(pq.getIsEdu());
		}
		
		if(StringUtil.isNotBlank(pq.getsAcceptorBankCode())){//承兑行
			hql.append(" and dto.plAccptrSvcr=:plAccptrSvcr");
			keys.add("plAccptrSvcr");
			values.add(pq.getsAcceptorBankCode());
		}
		if(null!= pq.getCustAccts() && pq.getCustAccts().size()>0){//承兑行列表
			hql.append(" and dto.plAccptrSvcr in(:acptBankList)");
			keys.add("acptBankList");
			values.add(pq.getCustAccts());
		}
		if(StringUtil.isNotBlank(pq.getBlackFlag())){//黑名单标识
			hql.append(" and dto.blackFlag=:blackFlag");
			keys.add("blackFlag");
			values.add(pq.getBlackFlag());
		}
		
		if(StringUtil.isNotBlank(pq.getBtFlag())){//保贴额度占用标识
			hql.append(" and dto.BtFlag=:BtFlag");
			keys.add("BtFlag");
			values.add(pq.getBtFlag());
		}
		
		hql.append(" order by dto.plDueDt desc ");
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		logger.info("执行的SQL为---------------------------------------------------------------------");
		logger.info(hql);
		logger.info("执行的SQL为---------------------------------------------------------------------");
		List rsut = this.find(hql.toString(), keis, values.toArray(), page);
		if (rsut != null && rsut.size() > 0) {
			return rsut;
		}
		return null;
	
	}

	@Override
	public void txAddAcptBankToBlackList(List<String> acptBankNos) throws Exception {
		
		logger.info("承兑行【"+acptBankNos.toString()+"】加入黑名单后处理开始...");
		
		List<String> bpsNoList = new ArrayList<String>();
		List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>();//需要更新池额度的票据池协议
		
		//【1】查询出在池的，已产生额度的，所有承兑行为该承兑行号的票据
		PoolQueryBean bean = new PoolQueryBean();
		bean.setCustAccts(acptBankNos);//承兑行行号
		bean.setIsEdu(PoolComm.YES);//额度存在
		List<DraftPool> poolList = this.queryDraftPools(bean, null); 
		//【2】修改该票据为不产生额度，打上黑名单标识，打上不产生额度的标记
		if(null !=poolList && poolList.size()>0){
			logger.info("产生额度的票据有"+poolList.size()+"............................................");
			for(DraftPool pool : poolList){
				pool.setLastRiskLevel(pool.getRickLevel());
				pool.setIsEduExist(PoolComm.NO);//不产生额度
				pool.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
				pool.setBlackFlag(PoolComm.BLACK);//黑名单标识
				
				//【3】从资产登记表中移除信息
				assetRegisterService.txDraftStockOutAssetChange(pool.getPoolAgreement(), pool,pool.getTradeAmt(), PublicStaticDefineTab.STOCK_OUT_TYPE_OUTPOOL);
			
				bpsNoList.add(pool.getPoolAgreement());//记录票据池编号
			}
			//落库
			this.txStoreAll(poolList);
		}
		logger.info("移除资产登记表的票据有"+bpsNoList.size()+"............................................");
		//【4】重新计算池额度
		if(bpsNoList.size()>0){		
			for(String bpsNo : bpsNoList){
				PedProtocolDto pro  = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
				proList.add(pro);
			}
			financialService.txReCreditCalculationTask(proList);
		}
		
	}

	@Override
	public void txDelAcptBankFromBlackList(List<String> acptBankNos)throws Exception {
		
		logger.info("承兑行【"+acptBankNos.toString()+"】移出黑名单后的处理开始...");
		List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>();//需要更新池额度的票据池协议
		
		//【1】查询出在池的，黑名单中的，未产生额度的，所有承兑行为该承兑行号的票据（注意：这样的票据只可能是入池时候正常入池，后来将该承兑行添加到黑名单的时候会产生）
		PoolQueryBean bean = new PoolQueryBean();
		bean.setCustAccts(acptBankNos);//承兑行行号列表
		bean.setIsEdu(PoolComm.NO);//额度不存在
		bean.setBlackFlag(PoolComm.BLACK);//黑名单中
		bean.setBtFlag(PoolComm.SP_01);//保贴额度有占用的才可恢复额度
		List<DraftPool> poolList = this.queryDraftPools(bean, null); 
		
		//【2】修改该票据为产生额度，去除黑名单标识，恢复额度类型标记
		if(null !=poolList && poolList.size()>0){
			for(DraftPool pool : poolList){
				pool.setBlackFlag(PoolComm.WHITE);//不在黑名单中
				if(PoolComm.LOW_RISK.equals(pool.getLastRiskLevel())||PoolComm.HIGH_RISK.equals(pool.getLastRiskLevel())){//之前是产生高低风险额度的
					pool.setIsEduExist(PoolComm.YES);//产生额度
					pool.setRickLevel(pool.getLastRiskLevel());//风险类型
				}
				
				//【3】登记到资产登记表中
				PedProtocolDto pro  = pedProtocolService.queryProtocolDto(null, null, pool.getPoolAgreement(), null, null, null);
				assetRegisterService.txBillAssetRegister(pool, pro);
				
				if(!proList.contains(pro)){
					proList.add(pro);
				}
			}
			
			//落库
			this.txStoreAll(poolList);
		}
		
		
		//【4】重新计算池额度
		if(proList.size()>0){		
			financialService.txReCreditCalculationTask(proList);
		}
		
	}

	@Override
	public List<GuarantDiscMapping> queryAcceptorMappingList(GuarantDiscMapping disc, Page page)
			throws Exception {
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List paramValue = new ArrayList<String>();// 值
		String hql= "from GuarantDiscMapping gd where 1=1";
		if(disc != null){
			//承兑人名称
			if(StringUtils.isNotEmpty(disc.getAcceptor())){
				hql = hql+"  and gd.acceptor like :acceptor  ";
				paramName.add("acceptor");
				paramValue.add("%"+disc.getAcceptor()+"%");	
			}
			
			//承兑人开户行行号
			if(StringUtils.isNotEmpty(disc.getAcptBankCode())){
				hql = hql+"  and gd.acptBankCode =:acptBankCode  ";
				paramName.add("acptBankCode");
				paramValue.add(disc.getAcptBankCode());	
			}
			//承兑人开户行账号
			if(StringUtils.isNotEmpty(disc.getAcptAcctNo())){
				hql = hql+"  and gd.acptAcctNo =:acptAcctNo  ";
				paramName.add("acptAcctNo");
				paramValue.add(disc.getAcptAcctNo());	
			}
			
			//承兑人开户行行名
			if(StringUtils.isNotEmpty(disc.getAcptBankname())){
				hql = hql+"  and gd.acptBankAnme Like :acptBankAnme  ";
				paramName.add("acptBankAnme");
				paramValue.add("%"+disc.getAcptBankname()+"%");	
			}
			
			//保贴人名称
			if(StringUtils.isNotEmpty(disc.getGuarantDiscName())){
				hql = hql+"  and gd.guarantDiscName like :guarantDiscName  ";
				paramName.add("guarantDiscName");
				paramValue.add("%"+disc.getGuarantDiscName()+"%");	
			}
			//保贴人编号
			if(StringUtils.isNotEmpty(disc.getGuarantDiscNo())){
				hql = hql+"  and gd.guarantDiscNo =:guarantDiscNo  ";
				paramName.add("guarantDiscNo");
				paramValue.add(disc.getGuarantDiscNo());	
			}
			
			if(StringUtils.isNotEmpty(disc.getCheckType())){
				hql = hql+"  and gd.checkType =:checkType  ";
				paramName.add("checkType");
				paramValue.add(disc.getCheckType());	
			}
			if(StringUtils.isNotBlank(disc.getId())){
				hql = hql+"  and gd.id !=:id  ";
				paramName.add("id");
				paramValue.add(disc.getId());
			}
			if(StringUtils.isNotBlank(disc.getStatus())){
				hql = hql+"  and gd.status =:status  ";
				paramName.add("status");
				paramValue.add(disc.getStatus());
			}
		}
		hql = hql+" and gd.delFlag != 'D' order by gd.updatetime ";
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		if(null!=page){
			resultList = this.find(hql,paramNames,paramValues,page);
		}else{
			resultList = this.find(hql,paramNames,paramValues);
		}
		
		if(resultList!=null && resultList.size()>0){
			return resultList;
		}
		return null;
		
	}
	
	@Override
	public List<GuarantDiscMapping> queryChangeAcceptorMapping(GuarantDiscMapping disc, List banks, Page page)
			throws Exception {
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List paramValue = new ArrayList<String>();// 值
		String hql= "from GuarantDiscMapping gd where 1=1";
		if(disc != null){
			//承兑人名称
			if(StringUtils.isNotEmpty(disc.getAcceptor())){
				hql = hql+"  and gd.acceptor = :acceptor  ";
				paramName.add("acceptor");
				paramValue.add(disc.getAcceptor());	
			}
			
			//承兑人开户行行号
			if(StringUtils.isNotEmpty(disc.getAcptBankCode())){
				hql = hql+"  and gd.acptBankCode =:acptBankCode  ";
				paramName.add("acptBankCode");
				paramValue.add(disc.getAcptBankCode());	
			}
			//承兑人开户行账号
			if(StringUtils.isNotEmpty(disc.getAcptAcctNo())){
				hql = hql+"  and gd.acptAcctNo =:acptAcctNo  ";
				paramName.add("acptAcctNo");
				paramValue.add(disc.getAcptAcctNo());	
			}
			
			//承兑人开户行行名
			if(StringUtils.isNotEmpty(disc.getAcptBankname())){
				hql = hql+"  and gd.acptBankAnme = :acptBankAnme  ";
				paramName.add("acptBankAnme");
				paramValue.add(disc.getAcptBankname());	
			}
			
			//保贴人名称
			if(StringUtils.isNotEmpty(disc.getGuarantDiscName())){
				hql = hql+"  and gd.guarantDiscName = :guarantDiscName  ";
				paramName.add("guarantDiscName");
				paramValue.add(disc.getGuarantDiscName());	
			}
			//保贴人编号
			if(StringUtils.isNotEmpty(disc.getGuarantDiscNo())){
				hql = hql+"  and gd.guarantDiscNo =:guarantDiscNo  ";
				paramName.add("guarantDiscNo");
				paramValue.add(disc.getGuarantDiscNo());	
			}
			
			if(StringUtils.isNotEmpty(disc.getCheckType())){
				hql = hql+"  and gd.checkType =:checkType  ";
				paramName.add("checkType");
				paramValue.add(disc.getCheckType());	
			}
			if(StringUtils.isNotBlank(disc.getId())){
				hql = hql+"  and gd.id !=:id  ";
				paramName.add("id");
				paramValue.add(disc.getId());
			}
			if(StringUtils.isNotBlank(disc.getStatus())){
				hql = hql+"  and gd.status =:status  ";
				paramName.add("status");
				paramValue.add(disc.getStatus());
			}
		}
		if(banks != null){
			hql = hql+"  and gd.acptBankCode in (:banks)  ";
			paramName.add("banks");
			paramValue.add(banks);	
		}
		hql = hql+" and gd.delFlag != 'D' order by gd.updatetime ";
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		if(null!=page){
			resultList = this.find(hql,paramNames,paramValues,page);
		}else{
			resultList = this.find(hql,paramNames,paramValues);
		}
		
		if(resultList!=null && resultList.size()>0){
			return resultList;
		}
		return null;
		
	}

	@Override
	public ReturnMessageNew txSynchBankMember(String memId) throws Exception {
		
		//MIS承兑行会员信息同步
		
		ReturnMessageNew response= new ReturnMessageNew();
		CreditTransNotes note =new CreditTransNotes();
		if(!StringUtils.isBlank(memId)){ //memberId 为空全量更新
			note.setMemberId(memId);
		}
		response=poolCreditClientService.txPJE024(note);
		
		if(response.isTxSuccess()){
			

			logger.info("【1】删除原MisBankMember表中的数据");
			
			String hql = " select mbm from  MisBankMember mbm ";
			List<MisBankMember>  list  = this.find(hql);
			if(null!= list && list.size()>0){
				this.txDeleteAll(list);
			}
			
			
			
		}
		
		return response;
		
		
	}
	
	@Override
	public void txBankMemberChangedHandle(ReturnMessageNew response) throws Exception{
		
		
		/*
		 * 注意：该方法耗时将会非常长 ！！！
		 */
		
		List detail=response.getDetails();
		if(null != detail && detail.size()>0){
			
			logger.info("【2】保存同步回来最新的MisBankMember数据");						
			
			List<MisBankMember> mbmList = new ArrayList<MisBankMember>();
			for(int i=0;i<detail.size();i++){
				Map map=(Map)detail.get(i);
				String serialNo=(String)map.get("SERIALNO");//批次流水号
				String memberId=(String)map.get("MEMBER_ID");//会员代码
				String bankName=(String)map.get("BANKNAME");//会员名称
				String bankNo=(String)map.get("BANKNO");//支付行号
				String LowFlag=(String)map.get("LOWFLAG");//是否低风险银行
				String status=(String)map.get("STATUS"); //是否有效
				
				if(StringUtils.isNotBlank(bankNo)){
					MisBankMember member=new MisBankMember();
					member.setSerialno(serialNo);
					member.setMemberId(memberId);
					member.setBankname(bankName);
					member.setBankno(bankNo);
					member.setLowflag(LowFlag);
					member.setStatus(status);
					member.setCreatetime(new Date());
					mbmList.add(member);
				}
				
			}
			this.txStoreAll(mbmList);
			response.getRet().setRET_MSG("承兑行高低风险会员信息同步成功!");
			
		}
		
		logger.info("MIS承兑行信息变化处理后的处理，开始...");
		String flowNo = Long.toString(System.currentTimeMillis());//流水号
		
		//MIS系统中同步回来的最新高风险承兑行信息
		List<String>  newList  = this.find("select mbm.bankno from  MisBankMember mbm");
		List<String>  newListBak = new ArrayList<String>();
		newListBak.addAll(newList);
		
		//原高风险承兑行信息
		List<String>  oldList  = this.find("select bm.bankno from  BankMember bm");
		
		logger.info("【1】比对新的MisBankMember与BankMember数据的差异,MisBankMember比BankMember中多的资产登记表中的低风险变高风险...");
		newList.removeAll(oldList);
		logger.info("对比对新的MisBankMember与BankMember数据的差异去除后的数据有："+newList.size()+"条");
		for (String string : newList) {
			logger.info("低变高行号为："+string);
		}
		logger.info("对比对新的MisBankMember与BankMember数据的差异对比结束");
//		List<String> misMoreThanLocal =  newList;
		List<String> misMoreThanLocal =  new ArrayList<String>();
		List<List<String>> misList =  new ArrayList<List<String>>();
		int count = 0;
		
		if(newList != null && newList.size() > 0){
			if(newList.size() > 1000){
				for (int i = 0; i < newList.size(); i++) {
					misMoreThanLocal.add(newList.get(i));
					if(count == 950){
						misList.add(misMoreThanLocal);
						misMoreThanLocal =  new ArrayList<String>();
						count = 0;
						continue;
					}
					count++;
				}
			}else{
				misMoreThanLocal =  newList;
				misList.add(misMoreThanLocal);
			}
			if(misList != null && misList.size() > 0){
			for (int i = 0; i < misList.size(); i++) {
				List<String> list = (List<String>) misList.get(i);
				//更新大票表中的风险类型  低风险 to 高风险
				List<PoolBillInfo> billList = draftPoolInService.queryPoolBillInfoListByTotalBank(list);

				if(null != billList){
					logger.info("根据行号查询的大票表票据有："+billList.size()+"条");
					List<PoolBillInfo> billListStore = new ArrayList<PoolBillInfo>();
					for(PoolBillInfo bill : billList){
						logger.info("票号为："+bill.getSBillNo()+"。。。。。。。。。。。。。。。。。。。");
						String sql = " update cd_edraft set RICK_LEVEL = 'FX_02' where ed_idnb = '"+bill.getSBillNo()+"'";
						dao.updateSQLReturnRows(sql);
//						bill.setRickLevel(PoolComm.HIGH_RISK);
//						billListStore.add(bill);
					}
//					this.txStoreAll(billListStore);
				}
				
				//更新PL_POOL中的资产风险类型  低风险 to 高风险
				List<DraftPool> draftList = draftPoolInService.queryDraftPoolListByTotalBank(list);
				if(null != draftList){
					logger.info("根据行号查询的池票据表票据有："+draftList.size()+"条");
					List<DraftPool> draftListStore = new ArrayList<DraftPool>();
					for(DraftPool draft : draftList){
						logger.info("票号为："+draft.getAssetNb()+"。。。。。。。。。。。。。。。。。。。");
						draft.setRickLevel(PoolComm.HIGH_RISK);
						draftListStore.add(draft);
						
						
						//更新资产登记表中的风险类型  低风险 to 高风险
						PedProtocolDto pro  = pedProtocolService.queryProtocolDto( null, null,  draft.getPoolAgreement(),null, null, null);
						assetRegisterService.txBillAssetRegister(draft, pro);
						
						String sql = " update pl_pool set RICK_LEVEL = 'FX_02' where pl_draftNb = '"+draft.getAssetNb()+"'";
						dao.updateSQLReturnRows(sql);
						
					}
//					this.txStoreAll(draftListStore);
				}
			
			}
			}
			
			
			
		}
		
		logger.info("【2】比对新的MisBankMember与BankMember数据的差异,MisBankMember比BankMember中少的资产登记表中的高风险变低风险...");
		logger.info("newListBak的数据有："+newListBak.size()+"条");
		oldList.removeAll(newListBak);
		for (String string : oldList) {
			logger.info("高变低行号为："+string);
		}
		logger.info("对比对新的MisBankMember与BankMember数据的差异去除后的数据有："+oldList.size()+"条");
		
//		List<String> localMoreThanMis = oldList;
		
		List<String> localMoreThanMis =  new ArrayList<String>();
		List<List<String>> changeList =  new ArrayList<List<String>>();
		count = 0;
		
		if(oldList != null && oldList.size() > 0){
			if(oldList.size() > 1000){
				for (int i = 0; i < oldList.size(); i++) {
					localMoreThanMis.add(oldList.get(i));
					if(count == 950){
						changeList.add(localMoreThanMis);
						localMoreThanMis =  new ArrayList<String>();
						count = 0;
						continue;
					}
					count++;
				}
			}else{
				localMoreThanMis = oldList;
				changeList.add(localMoreThanMis);
			}
		}
		
		
		if(null!=changeList&&changeList.size()>0){
			for (int i = 0; i < changeList.size(); i++) {
				//更新大票表中的风险类型  高风险 to 低风险
				List<PoolBillInfo> billList = draftPoolInService.queryPoolBillInfoListByTotalBank(changeList.get(i));
				logger.info("根据行号查询的大票表票据有："+billList.size()+"条");
				if(null != billList){
					List<PoolBillInfo> billListStore = new ArrayList<PoolBillInfo>();
					for(PoolBillInfo bill : billList){
//						bill.setRickLevel(PoolComm.LOW_RISK);
//						billListStore.add(bill);
						logger.info("票号为："+bill.getSBillNo()+"。。。。。。。。。。。。。。。。。。。");
						String sql = " update cd_edraft set RICK_LEVEL = 'FX_01' where ed_idnb = '"+bill.getSBillNo()+"'";
						dao.updateSQLReturnRows(sql);
					}
//					this.txStoreAll(billListStore);
				}
				
				//更新PL_POOL中的资产风险类型  高风险 to 低风险
				List<DraftPool> draftList = draftPoolInService.queryDraftPoolListByTotalBank(changeList.get(i));
				logger.info("根据行号查询的池票据表票据有："+draftList.size()+"条");
				if(null != draftList){
					List<DraftPool> draftListStore = new ArrayList<DraftPool>();
					for(DraftPool draft : draftList){
						draft.setRickLevel(PoolComm.LOW_RISK);
						draftListStore.add(draft);
						logger.info("票号为："+draft.getAssetNb()+"。。。。。。。。。。。。。。。。。。。");					
						//更新资产登记表中的风险类型  高风险 to 低风险
						PedProtocolDto pro  = pedProtocolService.queryProtocolDto( null, null,  draft.getPoolAgreement(),null, null, null);
						assetRegisterService.txBillAssetRegister(draft, pro);
						String sql = " update pl_pool set RICK_LEVEL = 'FX_01' where pl_draftNb = '"+draft.getAssetNb()+"'";
						dao.updateSQLReturnRows(sql);
					}
//					this.txStoreAll(draftListStore);
				}
			}
			
		}
		
		
		if((null!=misList&&misList.size()>0)||(null!=changeList&&changeList.size()>0)){
			
			logger.info("【3】重置票据池额度--更新所有签约融资票据池的客户的额度信息开始......");
			
			List<PedProtocolDto> proInfos = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null, null, null);
			if(proInfos!=null && proInfos.size()>0){
				financialService.txReCreditCalculationTask(proInfos);
			}
			
			
			logger.info("【4】将BankMember表中数据更新为最新的...");
			
			String hql = " select bm from  BankMember bm ";
			List<BankMember>  list  = this.find(hql);
			if(null!= list && list.size()>0){
				this.txDeleteAll(list);
			}
			
			List<MisBankMember>  mList  = this.find("select mbm from  MisBankMember mbm");
			if(null!=mList && mList.size()>0){
				List<BankMember> newBankMemberList = new ArrayList<BankMember>();
				for(MisBankMember mis : mList){
					BankMember newBankMember = new BankMember();
					BeanUtils.copyProperties(mis, newBankMember);
					newBankMember.setId(null);
					newBankMember.setCreatetime(new Date());
					newBankMember.setUpdatetime(new Date());
					newBankMemberList.add(newBankMember);
				}
				this.txStoreAll(newBankMemberList);
			}
			
		}
		
	}
	
	public String loadMemberBankList(ProtocolQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select bm from BankMember bm where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getMemberId())) {//会员代码
				sb.append(" and bm.memberId = :memberId ");
				keyList.add("memberId");
				valueList.add(queryBean.getMemberId());
			}
			if (StringUtils.isNotBlank(queryBean.getBankname())) {//行名
				sb.append(" and bm.bankname = :bankname ");
				keyList.add("bankname");
				valueList.add(queryBean.getBankname());
			}
			if (StringUtils.isNotBlank(queryBean.getBankno())) {//支付行号
				sb.append(" and bm.bankno = :bankno ");
				keyList.add("bankno");
				valueList.add(queryBean.getBankno());
			}
			if (StringUtils.isNotBlank(queryBean.getSerialNo())) {//序号
				sb.append(" and bm.serialNo = :serialNo ");
				keyList.add("serialNo");
				valueList.add(queryBean.getSerialNo());
			}
			
			
		}
		sb.append(" order by bm.createtime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}
	
	public ReturnMessageNew txPJE024Hander(String memberId)throws Exception {
		ReturnMessageNew response=new ReturnMessageNew();
		CreditTransNotes note =new CreditTransNotes();
		if(!StringUtils.isBlank(memberId)){//memberId为空全量更新
			note.setMemberId(memberId);
		}
		response=poolCreditClientService.txPJE024(note);
		
		return response;
		
	}
	
	@Override
	public List loadMemberBankInfo(ProtocolQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select bm from BankMember bm where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getMemberId())) {//会员代码
				sb.append(" and bm.memberId = :memberId ");
				keyList.add("memberId");
				valueList.add(queryBean.getMemberId());
			}
			if (StringUtils.isNotBlank(queryBean.getBankname())) {//行名
				sb.append(" and bm.bankname = :bankname ");
				keyList.add("bankname");
				valueList.add(queryBean.getBankname());
			}
			if (StringUtils.isNotBlank(queryBean.getBankno())) {//支付行号
				sb.append(" and bm.bankno = :bankno ");
				keyList.add("bankno");
				valueList.add(queryBean.getBankno());
			}
			if (StringUtils.isNotBlank(queryBean.getSerialNo())) {//序号
				sb.append(" and bm.serialno = :serialno ");
				keyList.add("serialno");
				valueList.add(queryBean.getSerialNo());
			}
			
			
		}
		sb.append(" order by bm.createtime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		if(list!=null && list.size()>0){
			return list;
		}else{
			return null;
		}
	}
	
	
	@Override
	public String loadGuarantDiscMappingList(ProtocolQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select bm from GuarantDiscMapping bm where delFlag =:delFlag  ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		keyList.add("delFlag");
		valueList.add(PublicStaticDefineTab.DELETE_FLAG_NO);
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getCheckType())) {//校验类型  1-财票 2-商票 
				sb.append(" and bm.checkType = :checkType ");
				keyList.add("checkType");
				valueList.add(queryBean.getCheckType());
			}
			
			if (StringUtils.isNotBlank(queryBean.getAcceptor())) {//承兑人名称
				sb.append(" and bm.acceptor like :acceptor ");
				keyList.add("acceptor");
				valueList.add("%"+queryBean.getAcceptor()+"%");
			}
			if (StringUtils.isNotBlank(queryBean.getGuarantDiscName())) {//保贴人名称
				sb.append(" and bm.guarantDiscName like :guarantDiscName ");
				keyList.add("guarantDiscName");
				valueList.add("%"+queryBean.getGuarantDiscName()+"%");
			}
			if (StringUtils.isNotBlank(queryBean.getGuarantDiscNo())) {//保贴人编号
				sb.append(" and bm.guarantDiscNo = :guarantDiscNo ");
				keyList.add("guarantDiscNo");
				valueList.add(queryBean.getGuarantDiscNo());
			}
			if (StringUtils.isNotBlank(queryBean.getId())) {
				sb.append(" and bm.id = :id ");
				keyList.add("id");
				valueList.add(queryBean.getId());
			}
			if (StringUtils.isNotBlank(queryBean.getAcptBankCode())) {//承兑人开户行行号
				sb.append(" and bm.acptBankCode = :acptBankCode ");
				keyList.add("acptBankCode");
				valueList.add(queryBean.getAcptBankCode());
			}
			if (StringUtils.isNotBlank(queryBean.getAcptBankName())) {//承兑人开户行行名
				sb.append(" and bm.acptBankname like :acptBankname ");
				keyList.add("acptBankname");
				valueList.add("%"+queryBean.getAcptBankName()+"%");
			}
			
		}
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			//商票承兑行名单管理
			if(!str.equals("0") && !str.equals("2") ){//管理员可查询所有数据
				return null;
			}
		}
		
		
		sb.append(" order by bm.updatetime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	
	@Override
	public String queryPjsAcptName(String acptBankCode,String type) throws Exception {
		String s="1";
		ProtocolQueryBean queryBean =new ProtocolQueryBean();
		queryBean.setTransBrchBankNo(acptBankCode);
		List list =this.loadCpesBranch(queryBean, null, null);
		if(null!=list && list.size()>0){
//			CpesBranch branch=(CpesBranch)list.get(0);
			Object[] obj=(Object[])list.get(0);
			if(type != null && type.equals("3")){
				if(obj[0].equals("301")){
					s=(String) obj[1];
				}else{
					s="3";
				}
			}else{
				s=(String) obj[1];
			}
		}
		
		return s;	
	}
	
	@Override
	public String queryGuarantDiscNo(String queryGuarantDiscNo) throws Exception {
		String creditCode =  "2";
		ReturnMessageNew response= new ReturnMessageNew();
		CreditTransNotes note =new CreditTransNotes();
		note.setGuarantDiscNo(queryGuarantDiscNo);
	    response=poolCreditClientService.txPJE025(note);
        if(response.isTxSuccess()){
        	List respList = response.getDetails();
			if(respList!=null && respList.size()>0){
				Map map =  (Map) respList.get(0);
				//保贴人编号
				creditCode = (String) map.get("LIMIT_ARRAY.CORE_CLIENT_NO");
			}
        }
		return creditCode;
		
	}
	
	@Override
	public String txsaveGuarantDiscMapping(GuarantDiscMapping grarant ,User user) throws Exception{
	  String json="保存失败";
	  Department depart=(Department)this.load(user.getDeptId(),Department.class);

	  if(grarant.getId()!=null&&!grarant.getId().equals("")){// 更新
		  
		  if(grarant.getCheckType().equals("2")){//商票修改保存
			//商票修改时，根据承兑人名称、账号、行号判断如果存在生效的承兑人映射则不允许添加
			  GuarantDiscMapping bean = new GuarantDiscMapping();
			  bean.setAcceptor(grarant.getAcceptor().trim());
			  bean.setAcptAcctNo(grarant.getAcptAcctNo());//承兑人账号
			  bean.setAcptBankCode(grarant.getAcptBankCode());//承兑行行号
			  bean.setCheckType(grarant.getCheckType());
			  bean.setId(grarant.getId());
			  bean.setStatus("1");//生效
			  List list = this.queryChangeAcceptorMapping(bean,null, null);
			  if(list != null && list.size() > 0){
				  return "3";//存在生效的商票映射不可修改
			  }
		  }else{
			  Map map = this.queryCpesMember(grarant.getAcptBankCode());//
			  String bankCode = (String) map.get("totalBankNo");//总行行号
			  List<String> banks = this.queryAllBranchBank(bankCode);//总行及下的分行
			  
			  GuarantDiscMapping bean = new GuarantDiscMapping();
//			  bean.setAcptBankCode(bankCode);
			  bean.setCheckType(grarant.getCheckType());
			  bean.setId(grarant.getId());
			  bean.setStatus("1");//生效
			  List list = this.queryChangeAcceptorMapping(bean, banks, null);
			  if(list != null && list.size() > 0){
				  return "2";//存在相同总行生效的财票映射不可修改
			  }
			  
			  //财票修改时，根据行号判断如果存在承兑人映射则不允许添加
//			  bean.setAcptBankCode(grarant.getAcptBankCode());
//			  bean.setCheckType(grarant.getCheckType());
//			  bean.setId(grarant.getId());
//			  bean.setStatus("1");//生效
//			  List list1 = this.queryAcceptorMappingList(bean, null);
//			  if(list1 != null && list1.size() > 0){
//				  return "5";//存在生效的财票映射不可修改
//			  }
		  }
		  
		 
		  
		  grarant.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);
		  grarant.setUserNo(user.getLoginName());
		  grarant.setUserName(user.getName());
		  grarant.setBranchNo(depart.getInnerBankCode());
		  grarant.setBranchName(depart.getName());
		  grarant.setUpdatetime(new Date());
		  this.txStore(grarant);
		  json="更新成功";
	  }else{
		  //新增时，如果存在承兑人映射则不允许添加
//		  GuarantDiscMapping bean = new GuarantDiscMapping();
//		  bean.setAcptBankCode(grarant.getAcptBankCode());
//		  bean.setCheckType(grarant.getCheckType());
//		  List list = this.queryAcceptorMappingList(bean, null);
//		  if(list != null && list.size() > 0){
//			  return "1";
//		  }
//		  
		  
		  if(grarant.getCheckType().equals("2")){//商票新增保存
			  //财票新增时，根据承兑人名称判断如果存在承兑人映射则不允许添加
			  GuarantDiscMapping bean = new GuarantDiscMapping();
			  bean.setAcceptor(grarant.getAcceptor().trim());
			  bean.setAcptAcctNo(grarant.getAcptAcctNo());//承兑人账号
			  bean.setAcptBankCode(grarant.getAcptBankCode());//承兑行行号
			  bean.setCheckType(grarant.getCheckType());
			  bean.setId(grarant.getId());
			  bean.setStatus("1");//生效
			  List list = this.queryChangeAcceptorMapping(bean, null, null);
			  if(list != null && list.size() > 0){
				  return "4";//存在生效的商票映射不可新增
			  }
		  }else{
			  
			  Map map = this.queryCpesMember(grarant.getAcptBankCode());//
			  String bankCode = (String) map.get("totalBankNo");//总行行号
			  List<String> banks = this.queryAllBranchBank(bankCode);//总行及下的分行

			  GuarantDiscMapping bean = new GuarantDiscMapping();
//			  bean.setAcptBankCode(bankCode);
			  bean.setCheckType(grarant.getCheckType());
			  bean.setId(grarant.getId());
			  bean.setStatus("1");//生效
			  List list = this.queryChangeAcceptorMapping(bean, banks, null);
			  if(list != null && list.size() > 0){
				  return "1";//存在相同总行生效的财票映射
			  }
			  
			  //财票修改时，根据行号判断如果存在承兑人映射则不允许添加
//			  bean.setAcptBankCode(grarant.getAcptBankCode());
//			  bean.setCheckType(grarant.getCheckType());
//			  bean.setId(grarant.getId());
//			  bean.setStatus("1");//生效
//			  List list1 = this.queryAcceptorMappingList(bean, null);
//			  if(list1 != null && list1.size() > 0){
//				  return "6";//存在生效的财票映射不可新增
//			  }
			  
		  }
		  
		  
		  grarant.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);
		  grarant.setUserNo(user.getLoginName());
		  grarant.setUserName(user.getName());
		  grarant.setBranchNo(depart.getInnerBankCode());
		  grarant.setBranchName(depart.getName());
		  grarant.setStatus("0");//初始化
		  grarant.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);//未处理
		  grarant.setUpdatetime(new Date());
		  grarant.setCreatetime(new Date());
		  grarant.setId(null);
		  this.txStore(grarant);
		  json="保存成功";
	  }

	  return json;
	}
	
	@Override
	public void txSubmitAuditFinance(String id, User user) throws Exception {
		//查询票据信息
		GuarantDiscMapping grarant = (GuarantDiscMapping) this.load(id,GuarantDiscMapping.class);
		if(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(grarant.getAuditStatus()) || PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(grarant.getAuditStatus())){
			throw new Exception(grarant.getAcceptor()+"当前状态不允许提交审批!");
		}else{
						
			 
			//生成审批对象
			ApproveAuditBean  approveAudit = new ApproveAuditBean();
			approveAudit.setAuditType("03");
			approveAudit.setBusiId(id); //
			approveAudit.setProductId("4001001"); //产品码
			
//			approveAudit.setCustCertNo(batch.getCpbranch()); //客户组织机构代码
//			approveAudit.setCustName(batch.getCpbankname()); //客户名称
//			approveAudit.setCustBankNm(batch.getCpbankname()); //客户开户行名称
//			approveAudit.setAuditAmt(grarant.getAssetAmt()); //总金额
//			approveAudit.setBillType(pool.getAssetType()); //票据类型
//			approveAudit.setBillMedia(pool.getPlDraftMedia()); // 票据介质  纸质
			approveAudit.setTotalNum(1); //总笔数
			approveAudit.setBusiType("40010");
			approveAudit.setApplyNo(poolBatchNoUtils.txGetBatchNo("CP",6));
			Map<String,String> mvelDataMap = new HashMap<String,String>();
//			mvelDataMap.put("amount", pool.getAssetAmt().toString());
//			mvelDataMap.put("totalNum", String.valueOf(pool.getAssetAmt()));
			AuditResultDto retAudit = auditService.txCommitApplyAudit(user, null, approveAudit, mvelDataMap);
			if(!retAudit.isIfSuccess()){
				//没有配置审批路线
				if("01".equals(retAudit.getRetCode())){
					throw new Exception("没有配置审批路线");
				}else if("02".equals(retAudit.getRetCode())){
					throw new Exception("审批金额过大 ，所有审批节点 都没有权限");
				}else{
					throw new Exception(retAudit.getRetMsg());
				}
			}
			grarant.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); //
			this.txStore(grarant);
		}
	}
	
	@Override
	public void txCancelAuditFinance(String id, User user) throws Exception {
		//查询票据信息
		GuarantDiscMapping grarant = (GuarantDiscMapping) this.load(id,GuarantDiscMapping.class);
		
		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(grarant.getAuditStatus())){
			throw new Exception("当前授信额度状态不允许撤销审批。");
		}
		auditService.txCommitCancelAudit("4001001", grarant.getId());
		//状态回滚
		grarant.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);
		this.txStore(grarant);
		
	}

	@Override
	public List loadCpesBranch(ProtocolQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select bm.transBrchClass,bm.brchFullNameZh from CpesBranch bm where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getTransBrchBankNo())) {//交易机构大额行号
				sb.append(" and bm.transBrchBankNo = :transBrchBankNo ");
				keyList.add("transBrchBankNo");
				valueList.add(queryBean.getTransBrchBankNo());
			}
	
		}
//		sb.append(" order by bm.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		if(list!=null && list.size()>0){
			return list;
		}else{
			return null;
		}
	}
	
	@Override
	public List queryCpesBranch(ProtocolQueryBean queryBean,User user, Page page) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		sb.append("from CpesBranch bm where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getTransBrchBankNo())) {//交易机构大额行号
				sb.append(" and bm.transBrchBankNo = :transBrchBankNo ");
				keyList.add("transBrchBankNo");
				valueList.add(queryBean.getTransBrchBankNo());
			}
			
		}
//		sb.append(" order by bm.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
		                                                          .size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		if(list!=null && list.size()>0){
			return list;
		}else{
			return null;
		}
	}

	@Override
	public String queryTotalBankNo(String bankNo) throws Exception {
		
		String totalBankNo = null;
		
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT m.MEMBER_BANK_NO,m.MEMBER_NAME from BO_CPES_MEMBER m ,BO_CPES_BRANCH b WHERE m.MEMBER_ID = b.MEMBER_ID ");
		
		if(StringUtils.isNotBlank(bankNo)){
			hql.append("  AND b.TRANS_BRCH_BANK_NO='"+bankNo+"'");
		}

		List  rslt = this.dao.SQLQuery(hql.toString());
		if(rslt!=null && rslt.size()>0){
			Object[] headBankNo = (Object[]) rslt.get(0);
			if(null !=  headBankNo[0]){				
				totalBankNo = (String)headBankNo[0];
			}
		}
		
		return totalBankNo;
	}

	@Override
	public List queryBlackListByBean(PedBlackList blackList, List<String> accptBank)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select b from PedBlackList b where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		

		if (StringUtils.isNotBlank(blackList.getType())) { // 名单类型（黑/灰名单）
			sb.append("and b.type = ? ");
			valueList.add(blackList.getType());
		}
		
		if (StringUtils.isNotBlank(blackList.getOrgCode())) { // 名单类型（黑/灰名单）
			sb.append("and b.orgCode = ? ");
			valueList.add(blackList.getOrgCode());
		}
		if (StringUtils.isNotBlank(blackList.getCustomerName())) { // 校验内容-关键值
			sb.append("and b.customerName like ? ");
			valueList.add("%" + blackList.getCustomerName() + "%");
		}

		if (StringUtils.isNotBlank(blackList.getKeywords())) { // 校验内容-关键词
			sb.append("and b.keywords = ? ");
			valueList.add(blackList.getKeywords());
		}
		if (StringUtils.isNotBlank(blackList.getContent())) { // 校验内容-关键值
			sb.append("and b.content like ? ");
			valueList.add("%" + blackList.getContent() + "%");
		}

		if (StringUtils.isNotBlank(blackList.getBillType())) { // 票据类型
			sb.append("and b.billType = ? ");
			valueList.add(blackList.getBillType());
		}

		if (StringUtils.isNotBlank(blackList.getBillMedia())) { // 票据介质
			sb.append("and b.billMedia = ? ");
			valueList.add(blackList.getBillMedia());
		}

		if (StringUtils.isNotBlank(blackList.getDataFrom())) { // 数据来源
			sb.append("and b.dataFrom = ? ");
			valueList.add(blackList.getDataFrom());
		}
		
		if(accptBank != null && accptBank.size() > 0){
			sb.append("and b.content in (?) ");
			valueList.add(accptBank);
		}

		sb.append("order by b.createTime desc ");
		List result = find(sb.toString(), valueList);
		if(result != null && result.size() > 0){
			return result;
		}
		return null;
	}

	@Override
	public List queryAllBankNo(String bankNo) throws Exception {
		
		String totalBankNo = null;
		StringBuffer sb = new StringBuffer();

		sb.append("select b from PedBlackList b where b.content in(select bra.transBrchBankNo from CpesBranch bra,CpesMember ber where " +
				" bra.memberId = ber.memberId  and ber.memberBankNo = '" +bankNo+"') and b.type = '02' and b.keywords ='03' ");

		sb.append("order by b.createTime desc ");
		List result = find(sb.toString());
		if(result != null && result.size() > 0 ){
			return result;
		}
		return null;
	}

	@Override
	public List<String> queryAllBranchBank(String bankNo) throws Exception {

		String totalBankNo = null;
		
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT b.TRANS_BRCH_BANK_NO from BO_CPES_MEMBER m ,BO_CPES_BRANCH b WHERE m.MEMBER_ID = b.MEMBER_ID ");
		
		if(StringUtils.isNotBlank(bankNo)){
			hql.append("  AND m.MEMBER_BANK_NO='"+bankNo+"'");
		}

		List  rslt = this.dao.SQLQuery(hql.toString());
		if(rslt!=null && rslt.size()>0){
			return rslt;
		}
		
		return null;
	}
	
	@Override
	public Map<String,String> queryCpesMember(String bankNo) throws Exception {
		
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT m.MEMBER_BANK_NO,b.TRANS_BRCH_CLASS,m.MEMBER_NAME from BO_CPES_MEMBER m ,BO_CPES_BRANCH b WHERE m.MEMBER_ID = b.MEMBER_ID ");
		
		hql.append("  AND b.TRANS_BRCH_BANK_NO='"+bankNo+"'");

		List  rslt = this.dao.SQLQuery(hql.toString());
		if(rslt!=null && rslt.size()>0){
			Object[] obj = (Object[]) rslt.get(0);
			if(null !=  obj[0]){	
				Map map = new HashMap();
				String totalBankNo = (String)obj[0];
				String transBrchClass = (String)obj[1];
				String memberName = (String)obj[2];
				map.put("totalBankNo", totalBankNo);
				map.put("transBrchClass", transBrchClass);
				map.put("memberName", memberName);
				return map;
			}
		}
		
		return null;
	}

}
