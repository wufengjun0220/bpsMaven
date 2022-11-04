package test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import junit.framework.TestCase;
import com.mingtech.framework.common.util.DateUtils;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: huangshiqiang
* @日期: Aug 3, 2009 2:23:10 PM
* @描述: [TestMath]测试计算公式相关
*/
public class TestMath extends TestCase {

	public void qtestDecimal(){
		BigDecimal math1 = new BigDecimal(34.56);
		BigDecimal math2 = new BigDecimal(657.5632);
		BigDecimal math3 = math1.multiply(math2);
		System.out.println(math3);
		BigDecimal math4 = math3.setScale(2, BigDecimal.ROUND_HALF_UP);
		System.out.println(math4);
	}
	
	public static BigDecimal interest(BigDecimal amount, BigDecimal rate, Date from, Date end){
		BigDecimal result = amount.multiply(new BigDecimal(1000000000));
		result = result.multiply(rate);
		result = result.multiply(new BigDecimal(DateUtils.getDayInRange(from,end)));
		result = result.divide(new BigDecimal(360), BigDecimal.ROUND_HALF_UP);
		result = result.multiply(new BigDecimal(0.000000001));
		result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
		return result;
	}
	public void testIntrest(){
		BigDecimal amount = new BigDecimal(12.00);
		BigDecimal rate = new BigDecimal(0.00050000);
		Date from = DateUtils.StringToDate("2009-11-20", "yyyy-MM-dd");
		Date end = DateUtils.StringToDate("2010-11-3", "yyyy-MM-dd");
		System.out.println(amount.subtract(interest(amount, rate, from, end)));
	}
	
	public static double interest(double amount, double rate, Date from, Date end){
		return ((amount*(rate*(DateUtils.getDayInRange(from,end))*100000000))/360)*0.00000001;
	}
	public void atestIntrest2(){
		double amount = 104;
		double rate = 0.05;
		Date from = DateUtils.StringToDate("2010-10-30", "yyyy-MM-dd");
		Date end = DateUtils.StringToDate("2011-10-25", "yyyy-MM-dd");
		System.out.println(DateUtils.getDayInRange(from,end));
		BigDecimal result = new BigDecimal(amount-(interest(amount, rate, from, end)));
		result = result.setScale(2,BigDecimal.ROUND_HALF_UP);
		System.out.println(result);
	}
	
	public void testArrayList(){
		List targetArr = Arrays.asList(new String[]{"8001","8002","8003","8005","8006","8007","8008","8009"});
		System.out.println(targetArr.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("'111'");
		sb.append("'222'");
		sb.replace(0, 1, "(");
		sb.replace(sb.length()-1, sb.length(), ")");
		System.out.println(sb.toString());
		
	}
	
	public void testSplit(){
		String number = "23152.21";
		String[] arr = number.split(".");
		System.out.println("数组长度："+number.substring(number.indexOf(".")+1));
		
		String xml = "<MainBody><MsgId><Id>100035000000510201711060000000255</Id><CreDtTm>2017-11-06T19:02:31</CreDtTm></MsgId><RgtInf><RgtBrId>000000510</RgtBrId><RgtDt>2017-11-06</RgtDt></RgtInf><CdInfs><CdInf><CdType>AC01</CdType><CdNo>3145600000100000</CdNo><CdAmt>20000</CdAmt><IssDt>2017-11-06</IssDt><DueDt>2017-11-30</DueDt></CdInf><AcptInf><AcptDt>2017-11-06</AcptDt><Note>7899999</Note></AcptInf><AcptPsnInf><Name>承兑人</Name><Acct>89741000000</Acct><AcctSvcr>314563000001</AcctSvcr><SocCode>232323333333333333</SocCode></AcptPsnInf><DwrInf><Name>出票人</Name><Acct>7895222200002121222</Acct><AcctSvcr>314560000011</AcctSvcr><SocCode>155556555555555555</SocCode></DwrInf><PyeeInf><Name>收款人</Name><Acct>78925222223333 </Acct><AcctSvcr>122322222222</AcctSvcr><AcctBkName>2212121</AcctBkName><SocCode>212222222222222222</SocCode></PyeeInf><RlvBnkInf><AcctSvcr>314563000001</AcctSvcr></RlvBnkInf><AddInfElmt><IndCls>A0000</IndCls><CorpScale>SC00</CorpScale><ArcFlag>0</ArcFlag><Area>11</Area><GrnFlag>0</GrnFlag></AddInfElmt></CdInfs><ImgInfs><ImgInf><ImgType></ImgType><ImgBtNo></ImgBtNo></ImgInf></ImgInfs></MainBody>";
		try {
			System.out.println("==============");
			Document text = DocumentHelper.parseText(xml);
			//获取根
			Element element = text.getRootElement();
			
			childEleIsEmpty(element);
			
			System.out.println( element.asXML()); 
			
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
//			logger.error(e.getMessage(),e);
		}
		
		
	}
	
	/**
	 * 子节点值是否全是空节点
	 * 如果子节点值全是空 删除父节点及其下所有子节点
	 * 如果某个子节点值为空 删除为空的子节点
	 * @param parentEle
	 * @return
	 */
	public boolean childEleIsEmpty(Element parentEle){
		Boolean isEmpty = true;
		List list = parentEle.elements();
		//遍历此节点下所有的节点
		for (Object object : list) {
			//获取节点信息
			Element element = (Element) object;
			//获取到当前节点位置
			Element ele = parentEle.element(element.getName());
			//获取此节点下节点  
			//判断是否有子节点  
			//如果有说明此节点为父节点
			//如果没有说明此节点为子节点
			List childEles = ele.elements();
			if (childEles == null || childEles.size() == 0 ) {
				System.out.println(ele.getName() + "此节点为子节点 :"+ele.getText()  );
				// 判断子节点  值是否为空
				if (ele.getText() == null || "".equals(ele.getText())) {
					//如果为空  删除子节点
					System.out.println("删除节点" + ele.getName());
					parentEle.remove(ele);
				}else{
					isEmpty = false;
				}
				
			}else{
				System.out.println(ele.getName() + "此节点为父节点 :"+ele.getText()  );
				isEmpty = childEleIsEmpty(ele);
				if (isEmpty) {
					System.out.println("删除此父节点:" + ele.getName());
					parentEle.remove(ele);
				}
			}
			
		}
		
		return isEmpty;
	}
	
	
}
