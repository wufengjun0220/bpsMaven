package com.mingtech.application.pool.bank.message;

import java.io.File;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: guoyaofeng
 * @日期: Jun 16, 2009 4:32:16 PM
 * @描述: [Constants]常量表，包含报文描述体元素项
 */
public class Constants {
	/*
	 * 元素类型值
	 *      string        字符类型；
	 *      varstring     可变字符类型，取一个字符串的某个部分
	 * 		int           整型；
	 * 		bigdecimal    金额等带精度的类型；
	 * 		datetime      日期时间型java date类型 可处理日期、时间、日期时间；
	 * 各域均为定长域，值不足长度时应补位：字符类型的，后补空格；数字类型的，前补0；
	 * 强制域必须填值，可选项可以不填值，但应填充占位字符。
	 */
	public static final String STRING_TYPE = "string";
	public static final String VAR_STRING_TYPE = "varstring";
	public static final String INT_TYPE = "int";
	public static final String BIGDECIMAL_TYPE = "bigdecimal";
	public static final String DATE_TIME_TYPE = "datetime";
		
	/*数据项是否必须，暂不用*/
	public static final String M = "m";	    //必须
	public static final String O = "o";     //非必须
	
	/*元素数据源*/
	public static final String XML = "xml";     //只支持字符类型
	public static final String BEAN = "bean";
	public static final String MAP = "map";
	
	/*报文类型值*/
	public static final String MAIN_BODY = "mb";	//主体报文
	public static final String DETAILS_BODY = "db";	//明细报文
	public static final String SYS_HEADER = "sh";	//系统头报文
	public static final String APP_HEADER = "ah";	//应用头报文
	public static final String LOCAL_HEADER = "lh";	//本地扩展头报文
	public static final String FILE_HEADER = "fh";	//文件扩展头报文
	
	public static final String ENCODING = "UTF-8";
	public static final String ENCODING_GBK = "GBK";
	
	/*报文头长度*/
	public static final int MSG_HEAD_LEN = 66;
	/*报文长度段的长度*/
	public static final int MSG_LENGTH_LEN = 8;
	/*文件名称域的名称*/
	public static final String FILE_NAME = "fileName";
	
	
	
    /*模板文件名称后缀*/
	public static final String TEMPLATE_SUFFIX = "_back";	
	/*报文头与报文体分隔符*/
	public static final String SPLICT_CODE = "&&";
	/*明细报文字段间分隔符 汉口银行用*/
	public static final String Detail_SPLICT_CODE = "~";
	
	//主报文域常量
	public static final String PP003 = "PP003";	
	public static final String PP095 = "PP095";	
	public static final String PP034 = "PP034";	
	public static final String PP104 = "PP104";	
	public static final String PP071 = "PP071";	
	public static final String PP036 = "PP036";	
	public static final String PP015 = "PP015";	
	public static final String PP073 = "PP073";	
	public static final String PP021 = "PP021";	
	public static final String PP035 = "PP035";	
	public static final String PP045 = "PP045";	
	public static final String PP039 = "PP039";	
	public static final String PP024 = "PP024";	
	public static final String PP026 = "PP026";	
	public static final String PP068 = "PP068";	
	public static final String PP069 = "PP069";	
	public static final String PP040 = "PP040";	
	public static final String PP044 = "PP044";	
	public static final String PP093 = "PP093";	
	public static final String PP085 = "PP085";	
	public static final String PP084 = "PP084";	
	public static final String PP013 = "PP013";	
	public static final String PP012 = "PP012";	
	public static final String PP033 = "PP033";	
	public static final String PP032 = "PP032";	
	public static final String PP112 = "PP112";	
	public static final String PP086 = "PP086";	
	public static final String PP087 = "PP087";	
	public static final String PP088 = "PP088";	
	public static final String PP027 = "PP027";	
	public static final String PP066 = "PP066";	
	public static final String PP115 = "PP115";
	public static final String PP049 = "PP049";
	public static final String PP101 = "PP101";
	public static final String PP002 = "PP002";
	public static final String PP117 = "PP117";
	public static final String PP100 = "PP100";
	public static final String PP114 = "PP114";
	public static final String PP119 = "PP119";
	public static final String PP011 = "PP011";
	public static final String PP090 = "PP090";
	public static final String PP074 = "PP074";
	

	//明细报文键常量
	public static final String ENTERPRISE_ACCOUNT_KEY = "enterpriseAccount"; //企业帐号
	public static final String ELECTRONIC_SIGNATURE_KEY = "electronicSignature"; //电子签名
	public static final String INSTRUCTION_SERIAL_KEY = "instructionSerialNumber"; //指令序号
	
	//响应代码常量
	public static final String RESPONSE_CODE_0000 = "0000";
	public static final String RESPONSE_CODE_E001 = "E001";   
	public static final String RESPONSE_CODE_E002 = "E002";     //请求报文格式有误
	public static final String RESPONSE_CODE_E003 = "E003";	    //不支持的交易类型
	public static final String RESPONSE_CODE_E004 = "E004";		//根据网点号获取大额支付行号失败
	public static final String RESPONSE_CODE_E005 = "E005";		//根据帐号获取组织机构代码失败
	public static final String RESPONSE_CODE_E006 = "E006";		//企业帐号不存在指定票号的票据
	public static final String RESPONSE_CODE_E007 = "E007";		//（回购式贴现）贴现申请日期小于贴现赎回开放日
	public static final String RESPONSE_CODE_E008 = "E008";		//重复提交的交易
	public static final String RESPONSE_CODE_E009 = "E009";		//本行帐号校验失败
	public static final String RESPONSE_CODE_E010 = "E010";		//系统内提示付款不能选择线上清算
	public static final String RESPONSE_CODE_E011 = "E011";		//此票在贴现批次中不存在
	public static final String RESPONSE_CODE_E012 = "E012";		//交易码不存在
	public static final String RESPONSE_CODE_CF04 = "CF04";		//4103的成功码，CF04 0000都认为是成功
	
	//银票，商票常量
	public static final String BANK_BILL = "AC01";
	public static final String BUSINESS_BILL = "AC02";
	
	public static final String SIGN_UP_ACCEPT = "SU00";
	public static final String SIGN_UP_CANCEL = "SU01";
	
	
	//交易码
	public static final String TX_SUCCESS = "TX_SUCCESS";//交易成功
	public static final String TX_FAIL = "TX_FAIL";//交易失败
	public static final String TX_FAIL_DECODE = "TX_FAIL_DECODE";//解码失败
	public static final String TX_FAIL_NOTSUPPORTED_TX = "TX_FAIL_NOTSUPPORTED_TX";//不支持本交易
	
	/**
	 *******************汉口银行网银接收查询请求报文部分*********************
	 */
	public static final String JYM= "JYM";						//交易码
	

	
	
	public static final String FKRMC = "FKRMC";               // 承兑人名称
	public static final String FKRKHHHM = "FKRKHHHM";         // 承兑人开户行行名
	public static final String FKRKHHHH = "FKRKHHHH";         // 承兑人开户行行号
	public static final String FKRKHHDZ = "FKRKHHDZ";         // 承兑人开户行地址
	public static final String FKRZH = "FKRZH";         // 承兑人账号
	public static final String PJJE = "PJJE";         // 票据金额
	public static final String CPRMC = "CPRMC";         // 出票人名称
	public static final String CPRKHHHM = "CPRKHHHM";         // 出票人开户行行名
	public static final String DQR = "DQR";         // 到期日
	public static final String TCYDBZ = "TCYDBZ";         // 同城异地标识
	public static final String CDRQ = "CDRQ";         // 承兑日期
	public static final String PJCYRMC = "PJCYRMC";         //票据持有人名称
	public static final String PJCYRKHHHM = "PJCYRKHHHM";         //票据持有人开户行行名
	
	public static final String SKRMC = "SKRMC";         //收款人名称
	public static final String SKRKHH = "SKRKHH";         //收款人开户行
	public static final String SKRZH = "SKRZH";         //收款人账号
	public static final String CDRHM = "CDRHM";         //承兑人行名
	public static final String SXF = "SXF";             //手续费
	public static final String SXFZH = "SXFZH";         //手续费扣款账号
	public static final String SJS = "SJS";             //发出时间开始
	public static final String SJE = "SJE";             //发出查询结束
	
	
	public static final String KHMC = "KHMC";                   //客户名称
	public static final String RCPJLL = "RCPJLL";               //入池票据利率
	public static final String XYDQR = "XYDQR";                 //协议到期日
	public static final String PJCDHHH = "PJCDHHH";             //票据承兑行行号
	public static final String KSSJ = "KSSJ";                  //开始时间
	public static final String JESJ = "JESJ";                  //结束时间
	public static final String XYLX = "XYLX";                  //协议类型（融资类型）
	public static final String ZYDBHTBH = "ZYDBHTBH";          //质押担保合同编号
	
	/**
	 * PJC012新增返回项   20181016 yy
	 */
	public static final String YINCHENG = "YINCHENG";                //银承(信贷银承是否开立
	public static final String LIUDAI = "LIUDAI";                    //流贷(信贷流贷是否开立)
	public static final String DUANDAI = "DUANDAI";                  //短贷(信贷短贷是否开立)
	public static final String CHAODUANDAI = "CHAODUANDAI";          //超短贷(信贷超短贷是否开立)
	public static final String DUANDAIRATE = "DUANDAIRATE";          //短贷利率
	
	
	/**
	 * 预留票据池网银字段，后期用
	 */
	public static final String MESSINFOR = "MESSINFOR";              //预留字段1
	public static final String MESSINFOT = "MESSINFOT";              //预留字段2
	
	
	
	
	public static final String CXLX = "CXLX";         // 查询类型
	public static final String PJZL = "PJZL";         // 票据种类
	public static final String PJJZ = "PJJZ";         // 票据介质
	public static final String CPRS = "CPRS";         // 出票日开始
	public static final String CPRE = "CPRE";         // 出票日截止
	public static final String DQRS = "DQRS";         // 到期日开始
	public static final String DQRE = "DQRE";         // 到期日截止
	public static final String PMJEXX = "PMJEXX";     // 票面金额下限  PJC001
	public static final String PMJESX = "PMJESX";      // 票面金额上限   PJC001
	public static final String PJHM = "PJHM";         // 电子票据号码
	public static final String CPRKHHMC = "CPRKHHMC";         // 出票人开户行名称
	
	public static final String QYZH= "QYZH";			//企业账号
	public static final String PJZT= "PJZT";			//票据状态
	public static final String ZJLX= "ZJLX";			//证件类型
	public static final String ZJH= "ZJH";			    //证件号
	
	
	
	
	
	//PJC024
	public static final String BillNo= "BillNo";			    //票据号码
	public static final String BillKind= "BillKind";			//票据种类
	public static final String BillAmt= "BillAmt";			    //票据金额
	public static final String StartDate="StartDate";           //出票日期
	public static final String EndDate="EndDate";               //到期日期
	public static final String JYContNo="JYContNo";             //交易合同号
	public static final String SFAttornId="SFAttornId";         //能否转让标记
	public static final String CPRName="CPRName";               //出票人全称
	public static final String CPRAcct="CPRAcct";               //出票人帐号
	public static final String CPRBankNo="CPRBankNo";           //出票人开户行行号
	public static final String CPRBankNanme="CPRBankNanme";     //出票人开户行名称
	public static final String SKRName="SKRName";               //收款人全称
	public static final String SKRAcct="SKRAcct";               //收款人帐号
	public static final String SKRBankNo="SKRBankNo";           //收款人开户行行号
	public static final String SKRBankName="SKRBankName";       //收款人开户行名称
	public static final String CDRName="CDRName";               //承兑人全称
	public static final String CDRAcct="CDRAcct";               //承兑人帐号
	public static final String CDRBankname="CDRBankname";       //承兑人开户行全称
	public static final String CDRBankNo="CDRBankNo";           //承兑人开户行行号
	public static final String CDDate="CDRBankNo";              //承兑日期

	
	
	
	
	
	public static final String BGHHH = "BGHHH";          // 保管行行号
	public static final String BGHMC = "BGHMC";          // 保管行行名
	public static final String PJCYRZH = "PJCYRZH";      // 票据持有人帐号  
	public static final String BZ = "BZ";                // 备注
	public static final String YWMXID = "YWMXID";        // 业务明细ID,是纸票时填值，电票放空
	public static final String DPPH = "DPPH";            // 电票票号,是电票时填，纸票放空
	public static final String CPRZH = "CPRZH";          // 持票人账户  /出票人账号
	public static final String BBRE = "BBRE";            // 日期
	
	
	public static final String RCS = "RCS";            // 入池开始时间
	public static final String RCE = "RCE";            // 入池时间结束
	
	
	
	public static final String PJJESX = "PJJESX";         // 票面金额上限   PJC004
	public static final String PJJEXX = "PJJEXX";         // 票面金额下限   PJC004
	
	
	
	public static final String ORGCODE = "ORGCODE";         // 三证合一信息  PJC005  ;pjc006:核心客户号
	public static final String PH = "PH";                   // pjc006:票号
	public static final String CDRHH = "CDRHH";                   // pjc006:承兑人行号
	public static final String CPR = "CPR";                   // 出票日
	public static final String BZJZH = "BZJZH";                   // pjc006:保证金账号
	public static final String ZRHM = "ZRHM";                   // 转入户名
	public static final String ZRZH = "ZRZH";                   // 转入账号
	
	public static final String EBANKCARD = "EBANKCARD";                   //网银人员证件信息
	public static final String EBANKNAME = "EBANKNAME";                   //网银人员姓名
	public static final String EBANKTYPE = "EBANKTYPE";                   //网银人员证件类型
	

	
	
	public static final String DHBZ = "DHBZ";                   // pjc007:定期活期
	
	
	
	public static final String KSRS = "KSRS";                   // pjc008:到期日开始
	public static final String KSRE = "KSRE";                   // pjc008:到期日结束
	
	

	public static final String PMJE = "PMJE";                //票据金额
	public static final String YWZJID = "YWZJID";            //业务主键id
	public static final String YWLX = "YWLX";                //业务类型
	
	
	public static final String ZCCXX = "ZCCXX";         //资产池业务开立信息
	public static final String TGXX = "TGXX";           //托管业务开立信息
	public static final String XYBH = "XYBH";           //协议编号
	public static final String BZZ = "BZZ";             //保证金余额
	public static final String SXED = "SXED";           //配套授信额度
	public static final String TOTALED = "TOTALED";     //池融资额度
	public static final String FREEED = "FREEED";       //池融资可用额度
	public static final String USEDED = "USEDED";       //池融资已用额度
	public static final String DEPT = "DEPT";           //业务主办行
	public static final String ZYCOUNT = "ZYCOUNT";     //已质押票据份数
	public static final String ZYAMT = "ZYAMT";         //已质押票据金额
	public static final String TGCOUNT = "TGCOUNT";     //已托管票据份数
	public static final String TGAMT = "TGAMT";         //已托管票据金额
	
	
	
	
	public static final String HZJE = "HZJE";         //划转金额
	public static final String HZLX = "HZLX";         //划转类型
	public static final String QX = "QX";             //定期期限
	public static final String GNLB = "GNLB";             //功能列表
	public static final String KHZJE = "KHZJE";             //可划转金额
	
	
	
	public static final String RESULT = "RESULT";				 //PJC015,反馈结果
	
	
	
	public static final String TXKIND= "TXKind";				//贴现种类
	public static final String DISTRANSFER= "DisTransfer";		//不得转让标记
	public static final String LQSID= "LQSid";					//线上清算标记
	public static final String TXSHSDATE= "TXSHSDate";			//贴现赎回开放日
	public static final String TXSHEDATE= "TXSHEDate";			//贴现赎回截止日
	public static final String CONTRACTNO= "ContractNo";		//交易合同编号
	public static final String TCRBOXMEMO= "TCRBoxMemo";		//贴出人备注
	public static final String RZACCT= "RZAcct";				//入账账号
	public static final String RZBANKNO= "RZBankNo";				//入账行号
	public static final String TRRACCT= "TRRAcct";				//贴入人账号
	public static final String TRRBANKNO= "TRRBankNo";			//贴入人开户行行号
	public static final String TCRATE= "TCRate";				//贴现利率
	public static final String TXSHRATE= "TXSHRate";			//贴现赎回利率
	public static final String TRRBANKNAME= "TRRBankName";		//贴入人开户行名称
	public static final String TRRNAME= "TRRName";				//贴入人名称
	public static final String INVOICENO= "InvoiceNo";			//发票号码
	public static final String ORDERNO= "OrderNo";				//指令序号
	public static final String DZID= "DZid";					//电子签名
	public static final String BOXMEMO= "BoxMemo";				//备注-回购式贴现赎回应答备注
	public static final String REPLYSIGN= "ReplySign";			//贴现赎回标记
	public static final String BSSDATE= "BSsDate";				//背书申请日期
	public static final String BSRBOXMEMO= "BSRBoxMemo";		//背书人备注
	public static final String BSRACCT= "BSRAcct";				//被背书人账号
	public static final String BSRNAME= "BSRName";				//被背书人名称
	public static final String BSRBANKNO= "BSRBankNo";			//被背书人开户行行号
	public static final String BSRBANKNAME= "BSRBankName";		//被背书人开户行名称
	public static final String BSRSPDATE= "BSRspDate";			//背书回复日期
	public static final String BSRSPID= "BSRspId";				//背书回复标记
	public static final String YWTYPE= "YWType";				//业务类型
	public static final String ZSKIND = "ZSKind";				//追索类型
	public static final String ZSMEGDATE = "ZSMegDate";			//追索通知日期
	public static final String ZSRESCODE = "ZSResCode";         //追索理由代码
	public static final String ZSBOXMEMO = "ZSBoxMemo";			//追索通知备注
	public static final String BZSRNAME = "BZSRName";			//被追索人名称
	public static final String BZSRACCT = "BZSRAcct";			//被追索人账号
	public static final String BZSRBANKNAME = "BZSRBankName";	//被追索人开户行名称
	public static final String BZSRBANKNO = "BZSRBankNo";		//被追索人开户行行号
	public static final String BZSRORGCODE = "BZSROrgCode";		//被追索人组织机构代码
	public static final String TYQCDATE = "TYQCDate";			//同意清偿日期
	public static final String ZSQCBOXMEMO = "ZSQCBoxMemo";		//追索同意清偿备注
	public static final String BZSTARTDATE = "BZStartDate";		//保证申请日期
	public static final String BZRNAME = "BZRName";				//保证人名称
	public static final String RZRACCT = "RZRAcct";				//保证人账号
	public static final String BZRBANKNAME = "BZRBankName";		//保证人开户行名称
	public static final String BZRBANKNO = "BZRBankNo";			//保证人开户行行号
	public static final String BZRSPDATE = "BZRspDate";			//保证回复日期
	public static final String BZRADDRESS = "BZRAddress";		//保证人地址
	public static final String DQWT = "DQWT";					//到期无条件支付委托
	public static final String CPRBOXMemo = "CPRBoxMemo";		//到期无条件支付委托
	
	public static final String CDRCreditLeve = "CDRCreditLeve";		//承兑人信用等级	
	public static final String LevelOrg = "LevelOrg";				//承兑人评级机构
	public static final String LevelEdate = "LevelEdate";				//承兑人评级到期日
	public static final String CDRspDate = "CDRspDate";				//承兑回复日期
	public static final String CDRspId = "CDRspId";					//承兑回复标记
	public static final String CDAgreeNo = "CDAgreeNo";				//承兑协议编号
	
	public static final String CmtIdNo = "CmtIdNo";					//报文标识号
	
	public static final String TURNPAGEBEGINPOS = "turnPageBeginPos";	//翻页数据起始位置	
	public static final String TURNPAGESHOWNUM = "turnPageShowNum";		//翻页一次显示数量	
	
	
	//response
	public static final String RspCode = "RspCode";				 //响应代码
	public static final String totalSize = "totalSize";          //记录总数
	public static final String totalMoney = "totalMoney";       //总金额
	
	
	
	
	public static final String CMTIDNO = "CmtIdNo";				//报文标识号
	
	/**报文返回码定义*/
	public static final String TX_SUCCESS_CODE = "000000";     //处理成功返回码
	public static final String TX_FAIL_CODE = "999999";		   //处理失败返回码
	public static final String TX_RETURN_MSG = "RspMsg";//错误提示信息
	
	/**信贷返回码*/
	public static final String CREDIT_01="CREDIT_01";//该客户未开通票据池功能
	public static final String CREDIT_02="CREDIT_02";//票据池额度不足
	public static final String CREDIT_03="CREDIT_03";//信贷产品已占用
	public static final String CREDIT_04="CREDIT_04";//信贷产品已结清
	public static final String CREDIT_05="CREDIT_05";//该票据池信息不存在
	public static final String CREDIT_06="CREDIT_06";//该主业务信息不存在
	public static final String CREDIT_07="CREDIT_07";//票据池担保余额不足
	public static final String CREDIT_08="CREDIT_08";//担保合同过期
	public static final String CREDIT_09="CREDIT_09";//占用额度大于融资人最高限额
	public static final String CREDIT_10="CREDIT_10";//客户最新一笔对账结果为不一致或近两个月未对账
	public static final String CREDIT_11="CREDIT_11";//票据池assetPool表上锁
	
	/**网银返回码定义*/
	public static final String EBK_01="EBK_01";//该客户有未结清用信业务，不允许解约操作
	public static final String EBK_02="EBK_02";//该客户未开通票据池业务
	public static final String EBK_03="EBK_03";//无符合条件数据
	public static final String EBK_04="EBK_04";//客户额度已冻结，不能进行出池申请
	public static final String EBK_05="EBK_05";//票据池可用额度不足，不允许进行出池申请
	public static final String EBK_06="EBK_06";//该票据池已解约
	public static final String EBK_07="EBK_07";//该票据为黑名单票据，不允许入池
	public static final String EBK_08="EBK_08";//该客户已开通融资业务，不允许解约
	public static final String EBK_09="EBK_09";//该客户已签约电票签约账户下有未处理完毕的业务，不允许更换电票账号
	public static final String EBK_10="EBK_10";//到期日当天不允许出池操作
	public static final String EBK_11="EBK_11";//处理中
	
	/**额度占用返回码值*/
	 public static final String ED_01 ="ED_01";//额度充足
	 public static final String ED_02 ="ED_02";//额度不足
	 public static final String ED_03 ="ED_03";//占用额度大于最高额担保合同
	 public static final String ED_04 ="ED_04";//占用额度大于融资人限额

	
	public static final String RetCode = "RetCode";                   //返回代码
	public static final String RetMsg = "RetMsg";                     //返回信息
	public static final String RESPONSE_CODE="response_code";

	/***************纸票导入常量定义************************/
	public static final String MSG_NODE_NAME = "name";
	public static final String MSG_NODE_DESCRIPTION = "description";
	public static final String MSG_NODE_DATATYPE = "dataType";
	public static final String MSG_NODE_FORMAT = "format";
	public static final String MSG_NODE_MULTIVALUE = "multiValue";
	public static final String MSG_NODE_MANDATORY = "mandatory";

	public static final String MSG_TYPE_INT = "int";
	public static final String MSG_TYPE_BIGDECIMAL = "bigdecimal";
	public static final String MSG_TYPE_DATETIME = "datetime";
	public static final String MSG_TYPE_STRING = "string";
//	public static final String MSG_TYPE_VAR_STRING = "string";
	
	public static final String E_DRAFT_TYPE = "draftType";
	public static final String E_DRAFTNB = "draftNb";
	public static final String E_ISSEAMT = "isseAmt";
	public static final String E_ISSEDT = "isseDt";
	public static final String E_DUEDT = "dueDt";
	public static final String E_SACCEPTORDT = "SAcceptorDt";
	public static final String E_CONTRACTNB = "contractNb";
	public static final String E_INVOICENB = "invoiceNb";
	public static final String E_SACCEPAGREENO = "SAccepAgreeNo";
	public static final String E_ACCPTRNM = "accptrNm";
	public static final String E_ACCPTRSVCR = "accptrSvcr";
	public static final String E_DRWRNM = "drwrNm";
	public static final String E_PYEENM = "pyeeNm";
	public static final String E_REMRK = "remrk";
	public static final String E_RVETDT = "rvetDt";
	public static final String E_prncplNm = "prncplNm";
	public static final String E_sspdgPmtTp = "sspdgPmtTp";
	public static final String E_sspdgPmtDt = "sspdgPmtDt";
	public static final String E_propsrNm = "propsrNm";
	public static final String E_oprtrNm = "oprtrNm";
	public static final String E_anlgSspdgPmtTp = "anlgSspdgPmtTp";
	public static final String E_anlgSspdgPmtDt = "anlgSspdgPmtDt";
	public static final String E_annulingPropsrNm = "annulingPropsrNm";
	public static final String E_annulingOprtrNm = "annulingOprtrNm";
	public static final String FIRST_SPLIT = ";";
	public static final String MANDATORY_M = "m";
	/**
	 *******************汉口银行网银接收查询请求报文部分*********************
	 */
	
	/**
	 *******************汉口银行与普兰交互部分begin*********************
	 */
	public static final String RECIVE_MSG_END = "</root>";//接收报文的结尾字段
	public static final String ID= "ID";				//电子票据号码
	public static final String QUERYTYPE= "QUERYTYPE";				//查询类型
	public static final String RESPONSECODE = "RESPONSECODE";//响应代码
	public static final String RESPONSERMK = "RESPONSERMK";//响应备注
	public static final String PL_SUCCESS_CODE = "0000";     //处理成功返回码
	public static final String PL_FAIL_CODE = "9999";		   //处理失败返回码
	
	
	/*
	 * 票据池
	 * 
	 */
	/**行内接口报文处理状态*/
	public static final String MS_SEND = "send"; // 发送
	public static final String MS_REC = "receive"; // 接收
	public static final String MS_DAY_END = "dayEnd"; // 日终处理交易
	public static final String MS_SEND_REFUSE = "sendRefuse";//发出被拒绝
	public static final String MS_REC_REFUSE = "recRefuse";//接收发送拒绝
	public static final String MS_SEND_CANCEL = "sendCancel";//发出申请后撤销
	public static final String MS_REC_CANCEL = "recCancel";//接收申请后撤销
	public static final String MSG_REC_UNDO = "待处理"; // 接收报文 待处理
	public static final String MSG_REC_DOING = "正在处理"; // 接收报文正在处理状态。
	public static final String MSG_REC_DONESUCC = "处理成功"; // 接收报文处理成功
	public static final String MSG_REC_DONEFAIL = "处理失败"; //接收报文处理失败
	public static final String MSG_SEND_UNSEND = "待发送";//待发送
	public static final String MSG_SEND_SENDING = "发送中";//发送中
	public static final String MSG_SEND_SENDSUCC = "发送成功";//发送成功
	public static final String MSG_SEND_SENDFAIL = "发送失败";//发送失败

	public static final String PAGER = "1"; //纸票
	public static final String ELETRICITY = "2"; //电票
	
	public static final String JYMT = "O";//mapfile文件名前缀
	public static final String JYM_REQUEST = "1";//请求交易mapfile文件名后缀
	public static final String JYM_RESPONSE = "2";//响应交易mapfile文件名后缀
	public static final String CHAR_ENCODING = "GBK";//字符编码
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final String RESPONSE_CODE_E111="ECDS111@";//其他异常
	
	/*TUXEDO常量定义*/
	public static final String TUXEDO_CONNECTION = "tuxedo.services.TuxedoConnection";//WTC方式TUXEDO调用时获取tuxedo连接使用
	
	public static final String FILE_SPLIT_XD = "|"; //文件分隔符
	public static final String FILE_SPLIT_HX = "\t"; //文件分隔符
	public static final String FILE_SEPARTOR = File.separator; //路径分隔符
	public static final String SH_UPLOAD_NAME = "upload.sh";//上传文件名
	public static final String SH_DOWNLOAD_NAME = "download.sh";//上传文件名
	
	public static final String DAY_END_LOG_DZ = "对账";//对账
	public static final String DAY_END_LOG_BK = "备款";//备款
	public static final String DAY_END_LOG_TX = "摊销";//摊销
	
	public static final String DAY_END_LOG_STATUS_SEND = "已发送";//已发送
	public static final String DAY_END_LOG_STATUS_SEND_SUCC = "发送成功";//发送成功
	public static final String DAY_END_LOG_STATUS_SEND_ERR = "发送失败";//发送失败
	public static final String DAY_END_LOG_STATUS_SEND_YHZ = "已回执";//已回执
	public static final String DAY_END_LOG_STATUS_SEND_HZCLCG = "回执处理成功";//回执处理成功
	public static final String DAY_END_LOG_STATUS_SEND_HZCLSB = "回执处理失败";//回执处理失败
	public static final String DAY_END_LOG_STATUS_NO_DATA = "无业务记录";//无业务记录
	
	
	public static final String BK_001 = "BK_001";//未备款
	public static final String BK_002 = "BK_002";//备款申请
	public static final String BK_003 = "BK_003";//备款成功
	public static final String BK_004 = "BK_004";//备款失败
	public static final String BK_005 = "BK_005";//垫款成功
	public static final String BK_006 = "BK_006";//垫款失败
	
	public static final String TX_001 = "TX_001";//未记账
	public static final String TX_002 = "TX_002";//已发记账
	public static final String TX_003 = "TX_003";//记账成功
	public static final String TX_004 = "TX_004";//记账失败
	
	public static final String HB_WH = "01";//我行
	public static final String HB_ZYYH = "02";//中央银行
	public static final String HB_GYYH = "03";//国有商业银行
	public static final String HB_ZCXYH = "04";//政策性银行
	public static final String HB_CSSY = "05";//城市商业银行
	public static final String HB_NCSY = "06";//农村商业银行
	public static final String HB_GFZSY = "07";//股份制商业银行
	public static final String HB_NCXYS = "08";//农村信用社
	public static final String HB_QTYH = "09";//其他银行
	public static final String HB_WZYH = "10";//外资银行
	
	/*模板路径及节点配置常量*/
	public static final String MSG_TEMPLATE_PATH = "hbyh/";//报文解析模板路径，相对工程根目录末尾加“/”
	public static final String MSG_TEMPLATE_REQUEST_HEAD_NODE = "/head/requestMsg/messageHead";//模板数据项请求节点名称
	public static final String MSG_TEMPLATE_REQUEST_NODE = "/head/requestMsg/messageElement";//模板数据项请求节点名称
	public static final String MSG_TEMPLATE_RESPONSE_NODE = "/head/responseMsg/messageElement";//模板数据项响应节点名称
	public static final String MSG_TEMPLATE_RESPONSE_DETAILS_NODE = "/head/responseDetailMsg/messageElement";//模板数据项响应明细节点名称
	/*模板属性*/
	public static final String MSG_NODE_LENGTH = "@length";//长度
	public static final String MSG_NODE_DATASOURCE="@dataSource";//数据来源
	public static final String MSG_NODE_VALUE = "@value";
	public static final int TFT_TIME =10000;//TFT文件传输等待时间
	
	
}
