package test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;


public class TestDelBatch {

	/**
	 * <p>方法名称: main|描述: </p>
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {	
		/*RediscountBuyServiceImpl res=(RediscountBuyServiceImpl) SpringContextUtil.getBean("rediscountBuyService");
		

		String sql="from RediscountBuyInBatchDto d ";
		List list=res.find(sql);
		for(int i=0;i<list.size();i++){
			RediscountBuyInBatchDto batchDto=(RediscountBuyInBatchDto) list.get(i);
			res.txDelete(batchDto);
		}*/
		if(new BigDecimal(2700000).compareTo(new BigDecimal(3000000).subtract(new BigDecimal(0)))>0){
		     System.out.println("在线银承合同余额小于客户申请金额^");
//		     error = error+"在线银承合同余额小于客户申请金额^";
		    }
//		Date sdate1 = DateUtils.parse("2021-10-01");
//		Date edate1 = DateUtils.parse("2022-01-29");
//		Date date2 =  DateUtils.parse("2022-01-30");
//		
//		if((DateTimeUtil.compartdate(sdate1,date2 )&&
//				!DateTimeUtil.compartdate(edate1,date2)	||
//				(edate1.compareTo(date2)==0))){
//			// 到期日在[开始时间，截至时间)之内;     即： 开始日期<=到期日，!截止日<=到期日，
//			System.out.println("........................................");
//		}
//		boolean flag = verfiyDate(date1, date2);
//		if(!verfiyDate(date2, date1)){
//			System.out.println("贷款期限超过一年|");
//		}
//		
		
		Map map = new HashMap();
		map.put("amt", 50.0);
		
		BigDecimal value = null;
		String a = (String) map.get("amt");
		if (StringUtil.isNotBlank(a.trim())) {
			value = new BigDecimal(a);
		}
		
		 System.out.println("-----------------"+value);
	}
	
	public static boolean verfiyDate(Date iDate, Date dDate) throws Exception {
		if (iDate == null || iDate.equals("") || dDate == null
				|| dDate.equals(""))
			throw new Exception("!!!!错误信息：调用verfiyDate服务传入参数为空!!!!");
		boolean rsut = false;
		int year = 365;
		Long result = Math.abs(new Long((dDate.getTime() - iDate.getTime())
				/ (1000 * 60 * 60 * 24)));
		/* 判断到期日期所在年是否为闰年 */
		year = DateUtils.isLeapYear(dDate) ? 366 : year;
		rsut = result.intValue() >= 0 && result.intValue() <= year ? true
				: rsut;
		return rsut;
	}
	
//	
//	  /********************************测试代码****************************************/
//	  try {
//	   PedOnlineAcptProtocol protocol = (PedOnlineAcptProtocol)pedOnlineAcptService.load("ff80808178f33a630178f370dbba023b", PedOnlineAcptProtocol.class);
//	   OnlineQueryBean queryBean = new OnlineQueryBean();
//	   queryBean.setOnlineAcptNo("YCOL20210421001");
//	   List list = pedOnlineAcptService.queryOnlineAcptProtocolList(queryBean, null);
//	   if(null != list && list.size()>0){
//	    OnlineQueryBean pro = (OnlineQueryBean) list.get(0);
//	    if(queryBean.getOnlineAcptTotal().compareTo(protocol.getOnlineAcptTotal().subtract(pro.getUsedAmt()))>0){
//	     System.out.println("在线银承合同余额小于客户申请金额^");
////	     error = error+"在线银承合同余额小于客户申请金额^";
//	    }
//	   }
//	   
//	   
//	  } catch (Exception e) {
//	   logger.error("具体错误：", e);
//	  }
	  /************************************************************************/

}
