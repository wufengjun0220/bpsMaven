package com.mingtech.framework.common.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mingtech.application.common.domain.Dictionary;
import com.mingtech.application.common.logic.DictionaryServiceFactory;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;

public class CommonUtil {

	/**
	 * 根据字典码 获取字典配置
	 * @param parentCode
	 * @return
	 */
	public static  List<Dictionary> getDictionaryByParentCode(String parentCode){
		List list = DictionaryServiceFactory.getDictionaryService().getDictionaryByParentCode(parentCode, 1);
		return list;
	}
	/**
	 * 通过三证合一代码 获取组织机构代码
	 * @param certCode
	 * @return
	 */
	public static String getOrgCodeByCertCode(String certCode){
		if(StringUtil.isNotBlank(certCode) && certCode.length()==18){
			String org = certCode.substring(8,17);
			return org.substring(0,8)+"-"+org.substring(8);
		}
		return certCode;
	}
	/**
	 * 通过组织机构代码生成三证合一码
	 * @param orgCode
	 * @return
	 */
	public static String getCertCodeByOrgCode(String orgCode){
		String certCode="";
		if(StringUtil.isNotBlank(orgCode) && orgCode.length()==10){
			certCode = "00000000"+orgCode.substring(0,8)+orgCode.substring(9)+"0";
		}else{
			certCode="000000000000000000";
		}
		return certCode;
	}
	/**
	 * 判断指定的用户是否有授权角色；
	 * @param user
	 * @return
	 */
	public static boolean validateUserIfHasAuthRole(User user){
		List roleList = user.getRoleList();
		for(int i=0;i<roleList.size();i++){
			Role role = (Role)roleList.get(i);
			if(role.getName()!=null && role.getName().indexOf("授权")>-1){
				return true;
			}
		}
		return false;
	}
	/**
	 * 获取总行机构号
	 * @return
	 */
	public static String getRootBranchNo(){
		return ProjectConfig.getInstance().getRootBrachNo();
	}
	/**
	 * 判断是否是总行柜员
	 * @param user
	 * @return
	 */
	public static boolean isRootUser(User user){
		//ROOT_BRANCH_NO  总机构号配置；
		if(user !=null){
			if(user.getDepartment().getInnerBankCode().equals(getRootBranchNo())){
				return true;
			}
			return true;
		}
		return false;
	}
	/**
	 * 是否存在上级部门
	 * @param user
	 * @return
	 */
	public static boolean  existParentDepartment(User user)
	{
		boolean is = false;
        if(null != user && 
        		null != user.getDepartment().getParent() && 
        		1==user.getDepartment().getStatus())
        {
        	is = true;
        }
		return is;
	}
	
	/**
	* 将利率同意换算成 年利率 
	* 利率换算，将日、月利率换算成年利率 
	* @param rate 利率
	* @param rateType 利率类型
	* @return
	*/
	public static  BigDecimal parseToYearRate(BigDecimal rate,String rateType){
		if(PublicStaticDefineTab.DISCOUNT_RATE_MONTH.equals(rateType)){
			return rate.multiply(new BigDecimal(1.2));//年利率=1.2*月利率
		}
		if(PublicStaticDefineTab.DISCOUNT_RATE_DATE.equals(rateType)){
			return rate.multiply(new BigDecimal(3.6));//年利率=3.6*日利率
		}
		return rate;
	}
	/**
	 * 判断a b两个值是否相同
	 * @param a 
	 * @param b
	 * @return
	 */
	public static boolean isSameValue(Object a,Object b){
		if (a == null && b == null) {
			return true;
		}else if (a != null && b !=null) {
			
		}else{
			return false;
		} 
		
		if (a instanceof BigDecimal ) {
			BigDecimal bigA = (BigDecimal) a;
			BigDecimal bigB = (BigDecimal) b;
			return bigA.equals(bigB);
		}
		if (a instanceof Date ) {
			Calendar cal1 = Calendar.getInstance();
		    cal1.setTime((Date) a);
		    cal1.set(Calendar.HOUR_OF_DAY, 0);  
		    cal1.set(Calendar.MINUTE, 0);  
		    cal1.set(Calendar.SECOND, 0);  
		    cal1.set(Calendar.MILLISECOND, 0); 
		    Calendar cal2 = Calendar.getInstance();
		    cal2.setTime((Date) b);
		    cal2.set(Calendar.HOUR_OF_DAY, 0);  
		    cal2.set(Calendar.MINUTE, 0);  
		    cal2.set(Calendar.SECOND, 0);  
		    cal2.set(Calendar.MILLISECOND, 0); 
		    return cal1.getTime().equals(cal2.getTime());
		}
		if (a instanceof String ) {
			String strA = (String) a;
			String strB = (String) b;
			return strA.equals(strB);
		}
		return false;
	}
}
