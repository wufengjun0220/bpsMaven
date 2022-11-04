package test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.common.util.UUID;



public class test {

	/**
	 * @param args
	 * @author Ju Nana
	 * @date 2021-6-23下午5:18:44 
	 */
	public static void main(String[] args) {
		
		String b = "1";
		for (int i = 1; i < 5; i++) {
			String a = String.valueOf(i);
			if(b.equals(a)){
				break;
			}
		}
		
		/*Date date = DateTimeUtil.parse("2099-12-31");
		System.out.println(date);
		
		String contractNo = "LDOL2021070600121071500032";
        String loanNo = "LDOL"+DateTimeUtil.get_YYYYMMDD_Date(new Date()).substring(2)+"000"+contractNo.substring(21);
        System.out.println("-----------------"+loanNo);
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        System.out.println(uuid);*/
	
		
		
	}
	public BigDecimal getBigDecimalVal(Object obj) throws Exception {
		BigDecimal value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = new BigDecimal(temp);
			}
		}
		return value;
	}


}
