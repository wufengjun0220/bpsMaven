package com.mingtech.application.ecd.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>说明:票据历史行为信息</p>
 *
 * @author   张永超
 * @Date	 May 19, 2009 11:46:11 AM 
 * @hibernate.class table="cd_endorsementLog"
 * @hibernate.cache usage="read-write"
 */
public class EndorsementLog {
	
	private 	String	id	;//	id
	private 	String	ownerEDraftId  ;   //	背书所属票据ID
	private 	String	ownerEDraft	   ;   //	背书所属票据号码
	private 	int	count	           ;   //	背书次数

	private 	Date	date	       ;   //	背书回复时间
	private 	String	content	       ;   //	背书显示内容
	private String	msgTpId            ;   //	交易类别代码（即发起交易的报文类型号）如：002、003、010
	private String	endrsrNm    	   ;   //	背书人（交易发起人）名称
	private String	endrsrAcctSvcr     ;   //	背书人（交易发起人）开户行行号
	private String	endrsrAcctId       ;   //	背书人（交易发起人）帐号
	private String	endrsrCmonId       ;   //	背书人（交易发起人）组织机构代码
	private String	endrsrRole         ;   //	背书人（交易发起人）类别
	private String	endrsrAgcyAcctSvcr ;   //	背书人（交易发起人）承接行行号
	private String	endrseeNm          ;   //	被背书人（交易签收人）名称
	private String	endrseeAcctSvcr    ;   //	被背书人（交易签收人）开户行行号
	private String	endrseeAcctId      ;   //	被背书人（交易签收人）帐号
	private String	endrseeCmonId      ;   //	被背书人（交易签收人）组织机构代码
	private String	endrseeRole        ;   //	被背书人（交易签收人）类别
	private String	endrseeAgcyAcctSvcr;   //	被背书人（交易签收人）承接行行号
	private String	linkHistoryMsgId   ;   //	关联历史信息记录ID
	private String  msgTypeText;//背书业务类别
	
	//20090903
	private String banEndrsmtMk;//不得转让标记 el_banEndrsmtMk  m_banEndrsmtMk
	private String guarnteeAdr;//保证人地址    el_guarnteeAdr   m_guarnteeDtGuarntrAdr
	private Date rpdOpenDt;//贴现赎回开放日     el_rpdOpenDt     m_rpdOpenDt
	private Date rpdDueDt;//贴现赎回截止日      el_rpdDueDt      m_rpdDueDt
	private String sgnUpMk;//提示付款回复标记   el_sgnUpMk       m_sgnUpMk
	private String sgnUpmkText;//提示付款回复描述
	private String dshnrCd;//拒付理由代码       el_dshnrCd       m_OvrPrsDtDshnrCd  m_PrsnttnDtDshnrCd
	private String dshnrCdText;//拒付理由描述
	private String rcrsTp;//追索类型            el_rcrsTp        m_Tp
	private String rcrsTpText; // 追索类型中文
	private Date reqDate;//报文申请时间          el_reqDate       m_applDt
	
	private String processMsg041; // 是否出了041报文，Y是已处理,空是为处理
	
	//新增字段
	private Date endorseDate;//背书日期
	private Date signInDate;//签收日期
	private BigDecimal rate;//赎回利率
	private BigDecimal redeemAmt;//赎回金额
	private String address;//保证人地址
	private String discountMode;//贴现方式
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private String draftSource;//票据来源
	/*** 融合改造新增字段  end*/
	
	
	private String getMsgTyText(String msgType){
		Map map = new HashMap();
		map.put("201002", "转让背书");
		map.put("201702", "保证背书");
		map.put("201802", "质押背书");
		map.put("201102", "买断式贴现背书");
		map.put("201104", "回购式贴现背书");
		map.put("201202", "回购式贴现赎回背书信息");
		map.put("201302", "买断式转贴现背书信息");
		map.put("201304", "回购式贴现背书信息");
		map.put("201402", "回购式转贴现赎回背书信息");
		map.put("201502", "买断式再贴现背书信息");
		map.put("201504", "回购式再贴现背书信息");
		map.put("201602", "回购式再贴现赎回背书信息");
		map.put("202502", "央行卖票背书信息");
		map.put("202002", "提示股款背书信息");
		map.put("202302", "追索清偿信息");
		if(null !=map.get(msgType.trim())){
			return (String) map.get(msgType.trim());
		}else{
			return msgType;
		}
	}
	public String getBeginRangeNo() {
		return beginRangeNo;
	}

	public void setBeginRangeNo(String beginRangeNo) {
		this.beginRangeNo = beginRangeNo;
	}

	public String getEndRangeNo() {
		return endRangeNo;
	}

	public void setEndRangeNo(String endRangeNo) {
		this.endRangeNo = endRangeNo;
	}

	public String getDraftSource() {
		return draftSource;
	}

	public void setDraftSource(String draftSource) {
		this.draftSource = draftSource;
	}
	/**
	 * 背书显示内容
	 * @hibernate.property type="string" column="el_content"
	 */
	public String getContent() {
		return content;
	}
	public void setContent(String el_content) {
		this.content = el_content;
	}
	/**
	 * 背书时间
	 * @hibernate.property type="timestamp" column="el_date"
	 */
	public Date getDate() {
		return date;
	}
	public void setDate(Date el_date) {
		this.date = el_date;
	}
	
	/**
	 * 背书显示内容
	 * @hibernate.property type="string" column="el_ownerEDraft" length="50"
	 */
	public String getOwnerEDraft() {
		return ownerEDraft;
	}
	public void setOwnerEDraft(String el_ownerEDraft) {
		this.ownerEDraft = el_ownerEDraft;
	}
	
	/**
	 * 背书所属票据ID，即管理的 EDraft表中的票据 主键 ID。
	 * 
	 * @hibernate.property type="string" column="el_ownerEDraftId" length="50"
	 */
	public String getOwnerEDraftId() {
		return ownerEDraftId;
	}
	public void setOwnerEDraftId(String el_ownerEDraftId) {
		this.ownerEDraftId = el_ownerEDraftId;
	}
	
	/**
	 * ID
	 * @hibernate.id generator-class="uuid" type="string" length="50" column="id"
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 背书次数
	 * @hibernate.property type="integer" column="el_count"
	 */
	public int getCount() {
		return count;
	}
	public void setCount(int el_count) {
		this.count = el_count;
	}
	/**
	 * 	交易类别代码（即发起交易的报文类型号）如：002、003、010
	 * @hibernate.property type="string" column="el_msgTpId" length="5"
	 */
	public String getMsgTpId() {
		return msgTpId;
	}
	public void setMsgTpId(String msgTpId) {
		this.msgTpId = msgTpId;
	}
	/**
	 * 背书人（交易发起人）名称,  如： 贴出人、 出质人、出票人等
	 * @hibernate.property type="string" column="el_endrsrNm" length="200"
	 */
	public String getEndrsrNm() {
		return endrsrNm;
	}
	public void setEndrsrNm(String endrsrNm) {
		this.endrsrNm = endrsrNm;
	}
	/**
	 * 背书人（交易发起人）开户行行号
	 * @hibernate.property type="string" column="el_endrsrAcctSvcr" length="20"
	 */
	public String getEndrsrAcctSvcr() {
		return endrsrAcctSvcr;
	}
	public void setEndrsrAcctSvcr(String endrsrAcctSvcr) {
		this.endrsrAcctSvcr = endrsrAcctSvcr;
	}
	/**
	 * 背书人（交易发起人）帐号
	 * @hibernate.property type="string" column="el_endrsrAcctId" length="40"
	 */
	public String getEndrsrAcctId() {
		return endrsrAcctId;
	}
	public void setEndrsrAcctId(String endrsrAcctId) {
		this.endrsrAcctId = endrsrAcctId;
	}
	/**
	 * 背书人（交易发起人）组织机构代码
	 * @hibernate.property type="string" column="el_endrsrCmonId" length="20"
	 */
	public String getEndrsrCmonId() {
		return endrsrCmonId;
	}
	public void setEndrsrCmonId(String endrsrCmonId) {
		this.endrsrCmonId = endrsrCmonId;
	}
	/**
	 * 背书人（交易发起人）类别
	 * @hibernate.property type="string" column="el_endrsrRole" length="10"
	 */
	public String getEndrsrRole() {
		return endrsrRole;
	}
	public void setEndrsrRole(String endrsrRole) {
		this.endrsrRole = endrsrRole;
	}
	/**
	 * 背书人（交易发起人）承接行行号
	 * @hibernate.property type="string" column="el_endrsrAgcyAcctSvcr" length="20"
	 */
	public String getEndrsrAgcyAcctSvcr() {
		return endrsrAgcyAcctSvcr;
	}
	public void setEndrsrAgcyAcctSvcr(String endrsrAgcyAcctSvcr) {
		this.endrsrAgcyAcctSvcr = endrsrAgcyAcctSvcr;
	}
	/**
	 * 被背书人（交易签收人）名称
	 * @hibernate.property type="string" column="el_endrseeNm" length="200"
	 */
	public String getEndrseeNm() {
		return endrseeNm;
	}
	public void setEndrseeNm(String endrseeNm) {
		this.endrseeNm = endrseeNm;
	}
	/**
	 * 被背书人（交易签收人）开户行行号
	 * @hibernate.property type="string" column="el_endrseeAcctSvcr" length="20"
	 */
	public String getEndrseeAcctSvcr() {
		return endrseeAcctSvcr;
	}
	public void setEndrseeAcctSvcr(String endrseeAcctSvcr) {
		this.endrseeAcctSvcr = endrseeAcctSvcr;
	}
	/**
	 * 被背书人（交易签收人）组织机构代码
	 * @hibernate.property type="string" column="el_endrseeCmonId" length="20"
	 */
	public String getEndrseeCmonId() {
		return endrseeCmonId;
	}
	public void setEndrseeCmonId(String endrseeCmonId) {
		this.endrseeCmonId = endrseeCmonId;
	}
	/**
	 * 被背书人（交易签收人）类别
	 * @hibernate.property type="string" column="el_endrseeRole" length="10"
	 */
	public String getEndrseeRole() {
		return endrseeRole;
	}
	public void setEndrseeRole(String endrseeRole) {
		this.endrseeRole = endrseeRole;
	}
	/**
	 * 被背书人（交易签收人）承接行行号
	 * @hibernate.property type="string" column="el_endrseeAgcyAcctSvcr" length="20"
	 */
	public String getEndrseeAgcyAcctSvcr() {
		return endrseeAgcyAcctSvcr;
	}
	public void setEndrseeAgcyAcctSvcr(String endrseeAgcyAcctSvcr) {
		this.endrseeAgcyAcctSvcr = endrseeAgcyAcctSvcr;
	}
	/**
	 * 关联历史信息记录ID
	 * @hibernate.property type="string" column="el_linkHistoryMsgId" length="50"
	 */
	public String getLinkHistoryMsgId() {
		return linkHistoryMsgId;
	}
	public void setLinkHistoryMsgId(String linkHistoryMsgId) {
		this.linkHistoryMsgId = linkHistoryMsgId;
	}
	/**
	 * 被背书人（交易签收人）帐号	
	 * @hibernate.property type="string" column="el_endrseeAcctId" length="40"
	 */
	public String getEndrseeAcctId() {
		return endrseeAcctId;
	}
	/**
	* <p>方法名称: setEndrseeAcctId|描述:被背书人（交易签收人）帐号 </p>
	* @param endrseeAcctId
	*/
	public void setEndrseeAcctId(String endrseeAcctId) {
		this.endrseeAcctId = endrseeAcctId;
	}
	/**
	* <p>方法名称: getMsgTypeText|描述:  交易类型名称</p>
	* @return
	*/
	public String getMsgTypeText() {
		return getMsgTyText(msgTpId);
	}

	public String getBanEndrsmtMk() {
		return banEndrsmtMk;
	}

	public void setBanEndrsmtMk(String banEndrsmtMk) {
		this.banEndrsmtMk = banEndrsmtMk;
	}

	public String getGuarnteeAdr() {
		return guarnteeAdr;
	}

	public void setGuarnteeAdr(String guarnteeAdr) {
		this.guarnteeAdr = guarnteeAdr;
	}

	public Date getRpdOpenDt() {
		return rpdOpenDt;
	}

	public void setRpdOpenDt(Date rpdOpenDt) {
		this.rpdOpenDt = rpdOpenDt;
	}

	public Date getRpdDueDt() {
		return rpdDueDt;
	}

	public void setRpdDueDt(Date rpdDueDt) {
		this.rpdDueDt = rpdDueDt;
	}

	public String getSgnUpMk() {
		return sgnUpMk;
	}

	public void setSgnUpMk(String sgnUpMk) {
		this.sgnUpMk = sgnUpMk;
	}

	public String getDshnrCd() {
		return dshnrCd;
	}

	public void setDshnrCd(String dshnrCd) {
		this.dshnrCd = dshnrCd;
	}

	public String getRcrsTp() {
		return rcrsTp;
	}

	public void setRcrsTp(String rcrsTp) {
		this.rcrsTp = rcrsTp;
	}

	public Date getReqDate() {
		return reqDate;
	}

	public void setReqDate(Date reqDate) {
		this.reqDate = reqDate;
	}

	public String getSgnUpmkText() {
		if("SU00".equals(sgnUpMk))
			return "同意签收";
		if("SU01".equals(sgnUpMk))
			return "拒绝签收";
		return "";
	}

	public void setSgnUpmkText(String sgnUpmkText) {
		this.sgnUpmkText = sgnUpmkText;
	}

	public String getDshnrCdText() {
		if("DC01".equals(dshnrCd))
			return "持票人以欺诈、偷盗或者胁迫等手段取得票据";
		if("DC02".equals(dshnrCd))
			return "持票人明知有欺诈、偷盗或者胁迫等情形，出于恶意取得票据";
		if("DC03".equals(dshnrCd))
			return "持票人明知债务人与出票人或者持票人的前手之间存在抗辩事由而取得票据";
		if("DC04".equals(dshnrCd))
			return "持票人因重大过失取得不符合《票据法》规定的票据";
		if("DC05".equals(dshnrCd))
			return "超过提示付款期";
		if("DC06".equals(dshnrCd))
			return "被法院冻结或收到法院止付通知书";
		if("DC07".equals(dshnrCd))
			return "票据未到期";
		if("DC08".equals(dshnrCd))
			return "商业承兑汇票承兑人账户余额不足";
		if("DC09".equals(dshnrCd))
			return "其他（必须注明）";
		return "";
	}

	public void setDshnrCdText(String dshnrCdText) {
		this.dshnrCdText = dshnrCdText;
	}

	public String getProcessMsg041(){
		return processMsg041;
	}

	public void setProcessMsg041(String processMsg041){
		this.processMsg041 = processMsg041;
	}

	public String getRcrsTpText(){
		if("RT00".equals(this.getRcrsTp()))
			return "拒付追索";
		if("RT01".equals(this.getRcrsTp()))
			return "非拒付追索";
		return "";
	}

	public void setRcrsTpText(String rcrsTpText){
		this.rcrsTpText = rcrsTpText;
	}

	public Date getEndorseDate() {
		return endorseDate;
	}

	public void setEndorseDate(Date endorseDate) {
		this.endorseDate = endorseDate;
	}

	public Date getSignInDate() {
		return signInDate;
	}

	public void setSignInDate(Date signInDate) {
		this.signInDate = signInDate;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getRedeemAmt() {
		return redeemAmt;
	}

	public void setRedeemAmt(BigDecimal redeemAmt) {
		this.redeemAmt = redeemAmt;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDiscountMode() {
		return discountMode;
	}

	public void setDiscountMode(String discountMode) {
		this.discountMode = discountMode;
	}

	
}

