package com.mingtech.application.pool.vtrust.domain;

import java.math.BigDecimal;
import java.util.Date;


import org.springframework.format.annotation.DateTimeFormat;

import com.mingtech.application.pool.common.PoolComm;




/**
 * 纸票虚拟托管
 * @author wbmengdepeng
 *
 */
public class PoolVtrust {
	private String id;
	private String  vtNb;//票据号码
	private String vtDraftMedia;//票据介质
	private String  vtType;//票据种类 AC01银承	AC02商承
	
	private String  vtTypeName;//票据种类名称
	
	private Date  vtisseDt;//出票日
	
	private Date  vtdueDt;//到期日
	
	private BigDecimal  vtisseAmt;//票据金额
	
	private String  vtdrwrName;//出票人名称
	
	private String  vtdrwrAccount;//出票人账号
	
	private String  vtdrwrOrg;//出票人组织机构代码
	
	private String  vtaccptrName;//承兑人名称
	
	private String  vtaccptrAccount;//承兑人账号
	
	private String  vtaccptrOrg;//承兑人组织机构代码
	
	private String  vtpyeeName;//名称
	
	private String  vtpyeeAccount;//收款人账号
	
	private String  vtpyeeOrg;//组织机构代码
	
	private String  vtEntpNo;//客户号
	
	private String  vtEntpName;//客户名称
	
	private Date  vtDate;//托管日期
	
	private String  vtLogo;//托管标志：1已托管  2未托管
	
	private String  vtLogoName;//托管标志：1已托管  2未托管
	
	private String  vtDeptId;//部门
	
	
	/**
	 * PJC024接口
	 * 网银端传过来的数据
	 * 新增以下12个属性，字段
	 * 
	 */
	
	//交易合同号  length=30
	private String vtTractNm;
	
	//能否转让标记 00不 可转让   01可转让
	private String vtTranSfer;
	
	//出票人开户行行号 length=12
	private String vtdrwrBankNumber;
	
	//出票人开户行名称 length=80
	private String vtdrwrBankName;
	
	//收款人开户行行号 length=12
	private String vtpyeeBankAccount;
	
	//收款人开户行名称 length=80
	private String vtpyeeBankName;
	
	//承兑人开户行全称 length=80
	private String vtaccptrBankName;
	
	//承兑人开户行行号 length=12
	private String vtaccptrBankAccount;
	
	//承兑人开户行地址
	private String vtaccptrBankAddr;
	
	//承兑日期 Date
	private Date vtaccptrDate;
	
	//票据持有人名称 length=120
	private String vtplApplyNm;
	
	//票据持有人账号 length=32
	private String vtplApplyAcctId;
	
	//票据持有人开户行行名 length=80
	private String vtapplyAcctSvcrNm;
	
	private String vtSource ;//数据来源  00：网银   01：票据系统
	private String rickLevel;//风险等级：高低风险使用预留
	private String riskComment;//风险说明：说明riskFliag的原因
	private String payType;//收付类型：QY_01：应收票据 QY_02：应付票据
	private String billPosition;//票据位置 01：本行  02：客户 03：他行
	private String billPositionAddr;//票据位置位于他行时可填入他行保管地址
	private String endorserInfo;//背书人信息   多个背书人信息用|分隔
	private String drwrGuarntrNm;//出票保证人名称
	private String drwrGuarntrAddr;//出票保证人地址
	private String drwrGuarntrDt;//出票保证时间
	private String accptrGuarntrNm;//承兑保证人名称
	private String accptrGuarntrAddr;//承兑保证人地址
	private String accptrGuarntrDt;//承兑保证时间
	private String remarks;//备注
	
	private String contractNo;//交易合同号
	private String  acceptanceAgreeNo;//承兑协议编号
	private String BtFlag;//商票保贴额度处理标识	1是占用成功 	0或空是释放成功或未占用
	private String vtStatus;//在池状态  DS_02  在池
	private String bpsNo;//票据池编号
	
	private String blackFlag;//黑灰名单标志
	
	private String inBatchNo;//入池批次号，柜面风险校验之后录入批次号，入池申请时候根据批次号查询
	private String tranRriorName;//票据直接签收信息
	private String bpsName;//票据池名称

	private Date plTm;//入表时间
	private Date lastOperTm;//最后一次操作时间
	private String lastOperName;//最后一次操作说明
	
	private int delayDays;//顺延天数
	private int holiDays;//遇节假日顺延
	private int days;//设定顺延天数
	
	
	private  String creditObjType;//额度主体类型  1-同业额度  2-对公额度
	private String guarantDiscName;//保贴人名称           
	private String guarantDiscNo;  //保贴编号   
	private String acptHeadBankNo;//承兑行总行行号，用于占用mis额度时候使用
	private String acptHeadBankName;//承兑行总行行名，用于占用mis额度时候使用
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否

	private String plDrwrAcctName;// 出票人账号名称
	private String plPyeeAcctName;// 收款人账号名称
	private String plAccptrAcctName;// 承兑人账号名称
	
	
	/*** 融合改造新增字段  end*/
	
	
	public String getAcptHeadBankName() {
		return acptHeadBankName;
	}

	public String getPlDrwrAcctName() {
		return plDrwrAcctName;
	}

	public void setPlDrwrAcctName(String plDrwrAcctName) {
		this.plDrwrAcctName = plDrwrAcctName;
	}

	public String getPlPyeeAcctName() {
		return plPyeeAcctName;
	}

	public void setPlPyeeAcctName(String plPyeeAcctName) {
		this.plPyeeAcctName = plPyeeAcctName;
	}

	public String getPlAccptrAcctName() {
		return plAccptrAcctName;
	}

	public void setPlAccptrAcctName(String plAccptrAcctName) {
		this.plAccptrAcctName = plAccptrAcctName;
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

	public BigDecimal getStandardAmt() {
		return standardAmt;
	}

	public void setStandardAmt(BigDecimal standardAmt) {
		this.standardAmt = standardAmt;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public String getDraftSource() {
		return draftSource;
	}

	public void setDraftSource(String draftSource) {
		this.draftSource = draftSource;
	}

	public String getSplitFlag() {
		return splitFlag;
	}

	public void setSplitFlag(String splitFlag) {
		this.splitFlag = splitFlag;
	}

	public void setAcptHeadBankName(String acptHeadBankName) {
		this.acptHeadBankName = acptHeadBankName;
	}

	public String getAcptHeadBankNo() {
		return acptHeadBankNo;
	}

	public void setAcptHeadBankNo(String acptHeadBankNo) {
		this.acptHeadBankNo = acptHeadBankNo;
	}
	
	
	public String getCreditObjType() {
		return creditObjType;
	}

	public void setCreditObjType(String creditObjType) {
		this.creditObjType = creditObjType;
	}

	public String getGuarantDiscName() {
		return guarantDiscName;
	}

	public void setGuarantDiscName(String guarantDiscName) {
		this.guarantDiscName = guarantDiscName;
	}

	public String getGuarantDiscNo() {
		return guarantDiscNo;
	}

	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}

	public int getDelayDays() {
		return delayDays;
	}

	public void setDelayDays(int delayDays) {
		this.delayDays = delayDays;
	}

	public int getHoliDays() {
		return holiDays;
	}

	public void setHoliDays(int holiDays) {
		this.holiDays = holiDays;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getTranRriorName() {
		return tranRriorName;
	}

	public Date getPlTm() {
		return plTm;
	}

	public void setPlTm(Date plTm) {
		this.plTm = plTm;
	}

	public Date getLastOperTm() {
		return lastOperTm;
	}

	public void setLastOperTm(Date lastOperTm) {
		this.lastOperTm = lastOperTm;
	}

	public String getLastOperName() {
		return lastOperName;
	}

	public void setLastOperName(String lastOperName) {
		this.lastOperName = lastOperName;
	}

	public void setTranRriorName(String tranRriorName) {
		this.tranRriorName = tranRriorName;
	}

	public String getInBatchNo() {
		return inBatchNo;
	}
	public void setInBatchNo(String inBatchNo) {
		this.inBatchNo = inBatchNo;
	}
	public String getBlackFlag() {
		return blackFlag;
	}
	public void setBlackFlag(String blackFlag) {
		this.blackFlag = blackFlag;
	}
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getVtStatus() {
		return vtStatus;
	}
	public void setVtStatus(String vtStatus) {
		this.vtStatus = vtStatus;
	}
	public String getBtFlag() {
		return BtFlag;
	}
	public void setBtFlag(String btFlag) {
		BtFlag = btFlag;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getAcceptanceAgreeNo() {
		return acceptanceAgreeNo;
	}
	public void setAcceptanceAgreeNo(String acceptanceAgreeNo) {
		this.acceptanceAgreeNo = acceptanceAgreeNo;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDrwrGuarntrNm() {
		return drwrGuarntrNm;
	}
	public void setDrwrGuarntrNm(String drwrGuarntrNm) {
		this.drwrGuarntrNm = drwrGuarntrNm;
	}
	public String getDrwrGuarntrAddr() {
		return drwrGuarntrAddr;
	}
	public void setDrwrGuarntrAddr(String drwrGuarntrAddr) {
		this.drwrGuarntrAddr = drwrGuarntrAddr;
	}
	public String getDrwrGuarntrDt() {
		return drwrGuarntrDt;
	}
	public void setDrwrGuarntrDt(String drwrGuarntrDt) {
		this.drwrGuarntrDt = drwrGuarntrDt;
	}
	public String getAccptrGuarntrNm() {
		return accptrGuarntrNm;
	}
	public void setAccptrGuarntrNm(String accptrGuarntrNm) {
		this.accptrGuarntrNm = accptrGuarntrNm;
	}
	public String getAccptrGuarntrAddr() {
		return accptrGuarntrAddr;
	}
	public void setAccptrGuarntrAddr(String accptrGuarntrAddr) {
		this.accptrGuarntrAddr = accptrGuarntrAddr;
	}
	public String getAccptrGuarntrDt() {
		return accptrGuarntrDt;
	}
	public void setAccptrGuarntrDt(String accptrGuarntrDt) {
		this.accptrGuarntrDt = accptrGuarntrDt;
	}
	public String getEndorserInfo() {
		return endorserInfo;
	}
	public void setEndorserInfo(String endorserInfo) {
		this.endorserInfo = endorserInfo;
	}
	public String getVtaccptrBankAddr() {
		return vtaccptrBankAddr;
	}
	public void setVtaccptrBankAddr(String vtaccptrBankAddr) {
		this.vtaccptrBankAddr = vtaccptrBankAddr;
	}
	public String getBillPosition() {
		return billPosition;
	}
	public void setBillPosition(String billPosition) {
		this.billPosition = billPosition;
	}
	public String getBillPositionAddr() {
		return billPositionAddr;
	}
	public void setBillPositionAddr(String billPositionAddr) {
		this.billPositionAddr = billPositionAddr;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getRickLevel() {
		return rickLevel;
	}
	public void setRickLevel(String rickLevel) {
		this.rickLevel = rickLevel;
	}
	public String getRiskComment() {
		return riskComment;
	}
	public void setRiskComment(String riskComment) {
		this.riskComment = riskComment;
	}
	public String getVtSource() {
		return vtSource;
	}
	public void setVtSource(String vtSource) {
		this.vtSource = vtSource;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVtNb() {
		return vtNb;
	}
	public void setVtNb(String vtNb) {
		this.vtNb = vtNb;
	}
	public String getVtType() {
		return vtType;
	}
	public void setVtType(String vtType) {
		this.vtType = vtType;
	}
	public String getVtTypeName() {
		if(PoolComm.BILL_TYPE_BANK.equals(this.getVtType())){
			return "银承";
		}else if(PoolComm.BILL_TYPE_BUSI.equals(this.getVtType())){
			return "商承";
		}else{
			return "";
		}
		
	}
	public void setVtTypeName(String vtTypeName) {
		this.vtTypeName = vtTypeName;
	}
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getVtisseDt() {
		return vtisseDt;
	}
	public void setVtisseDt(Date vtisseDt) {
		this.vtisseDt = vtisseDt;
	}
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getVtdueDt() {
		return vtdueDt;
	}
	public void setVtdueDt(Date vtdueDt) {
		this.vtdueDt = vtdueDt;
	}
	public BigDecimal getVtisseAmt() {
		return vtisseAmt;
	}
	public void setVtisseAmt(BigDecimal vtisseAmt) {
		this.vtisseAmt = vtisseAmt;
	}
	public String getVtdrwrName() {
		return vtdrwrName;
	}
	public void setVtdrwrName(String vtdrwrName) {
		this.vtdrwrName = vtdrwrName;
	}
	public String getVtdrwrAccount() {
		return vtdrwrAccount;
	}
	public void setVtdrwrAccount(String vtdrwrAccount) {
		this.vtdrwrAccount = vtdrwrAccount;
	}
	public String getVtdrwrOrg() {
		return vtdrwrOrg;
	}
	public void setVtdrwrOrg(String vtdrwrOrg) {
		this.vtdrwrOrg = vtdrwrOrg;
	}
	public String getVtaccptrName() {
		return vtaccptrName;
	}
	public void setVtaccptrName(String vtaccptrName) {
		this.vtaccptrName = vtaccptrName;
	}
	public String getVtaccptrAccount() {
		return vtaccptrAccount;
	}
	public void setVtaccptrAccount(String vtaccptrAccount) {
		this.vtaccptrAccount = vtaccptrAccount;
	}
	public String getVtaccptrOrg() {
		return vtaccptrOrg;
	}
	public void setVtaccptrOrg(String vtaccptrOrg) {
		this.vtaccptrOrg = vtaccptrOrg;
	}
	public String getVtpyeeName() {
		return vtpyeeName;
	}
	public void setVtpyeeName(String vtpyeeName) {
		this.vtpyeeName = vtpyeeName;
	}
	public String getVtpyeeAccount() {
		return vtpyeeAccount;
	}
	public void setVtpyeeAccount(String vtpyeeAccount) {
		this.vtpyeeAccount = vtpyeeAccount;
	}
	public String getVtpyeeOrg() {
		return vtpyeeOrg;
	}
	public void setVtpyeeOrg(String vtpyeeOrg) {
		this.vtpyeeOrg = vtpyeeOrg;
	}
	public String getVtEntpNo() {
		return vtEntpNo;
	}
	public void setVtEntpNo(String vtEntpNo) {
		this.vtEntpNo = vtEntpNo;
	}
	public String getVtEntpName() {
		return vtEntpName;
	}
	public void setVtEntpName(String vtEntpName) {
		this.vtEntpName = vtEntpName;
	}
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getVtDate() {
		return vtDate;
	}
	public void setVtDate(Date vtDate) {
		this.vtDate = vtDate;
	}
	public String getVtLogo() {
			return vtLogo;
	}
	public void setVtLogo(String vtLogo) {
		this.vtLogo = vtLogo;
	}
	public String getVtLogoName() {
		if("1".equals(this.getVtLogo())){
			return "已托管";
		}else if("2".equals(this.getVtLogo())){
			return "未托管";
		}else{
			return "";
		}
	}
	public void setVtLogoName(String vtLogoName) {
		this.vtLogoName = vtLogoName;
	}
	public String getVtDeptId() {
		return vtDeptId;
	}
	public void setVtDeptId(String vtDeptId) {
		this.vtDeptId = vtDeptId;
	}
	
	
	/**
	 * 新增属性set/get方法
	 */
	
	public String getVtTractNm() {
		return vtTractNm;
	}
	public void setVtTractNm(String vtTractNm) {
		this.vtTractNm = vtTractNm;
	}
	public String getVtTranSfer() {
		return vtTranSfer;
	}
	public void setVtTranSfer(String vtTranSfer) {
		this.vtTranSfer = vtTranSfer;
	}
	public String getVtdrwrBankNumber() {
		return vtdrwrBankNumber;
	}
	public void setVtdrwrBankNumber(String vtdrwrBankNumber) {
		this.vtdrwrBankNumber = vtdrwrBankNumber;
	}
	public String getVtdrwrBankName() {
		return vtdrwrBankName;
	}
	public void setVtdrwrBankName(String vtdrwrBankName) {
		this.vtdrwrBankName = vtdrwrBankName;
	}
	public String getVtpyeeBankAccount() {
		return vtpyeeBankAccount;
	}
	public void setVtpyeeBankAccount(String vtpyeeBankAccount) {
		this.vtpyeeBankAccount = vtpyeeBankAccount;
	}
	public String getVtpyeeBankName() {
		return vtpyeeBankName;
	}
	public void setVtpyeeBankName(String vtpyeeBankName) {
		this.vtpyeeBankName = vtpyeeBankName;
	}
	public String getVtaccptrBankName() {
		return vtaccptrBankName;
	}
	public void setVtaccptrBankName(String vtaccptrBankName) {
		this.vtaccptrBankName = vtaccptrBankName;
	}
	public String getVtaccptrBankAccount() {
		return vtaccptrBankAccount;
	}
	public void setVtaccptrBankAccount(String vtaccptrBankAccount) {
		this.vtaccptrBankAccount = vtaccptrBankAccount;
	}
	public Date getVtaccptrDate() {
		return vtaccptrDate;
	}
	public void setVtaccptrDate(Date vtaccptrDate) {
		this.vtaccptrDate = vtaccptrDate;
	}
	public String getVtplApplyNm() {
		return vtplApplyNm;
	}
	public void setVtplApplyNm(String vtplApplyNm) {
		this.vtplApplyNm = vtplApplyNm;
	}
	public String getVtplApplyAcctId() {
		return vtplApplyAcctId;
	}
	public void setVtplApplyAcctId(String vtplApplyAcctId) {
		this.vtplApplyAcctId = vtplApplyAcctId;
	}
	public String getVtapplyAcctSvcrNm() {
		return vtapplyAcctSvcrNm;
	}
	public void setVtapplyAcctSvcrNm(String vtapplyAcctSvcrNm) {
		this.vtapplyAcctSvcrNm = vtapplyAcctSvcrNm;
	}
	public String getVtDraftMedia() {
		return vtDraftMedia;
	}
	public void setVtDraftMedia(String vtDraftMedia) {
		this.vtDraftMedia = vtDraftMedia;
	}

	public String getBpsName() {
		return bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}
}
