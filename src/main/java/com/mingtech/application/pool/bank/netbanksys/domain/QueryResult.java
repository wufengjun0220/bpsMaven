package com.mingtech.application.pool.bank.netbanksys.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ognl.Ognl;
import ognl.OgnlException;


/**
 * 
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 15, 2009 9:20:54 AM
 * @描述: [QueryResult]描述票据查询结果，包括总笔数，总金额和业务对象列表
 */
public class QueryResult{
	private int count;
	private BigDecimal totalAmount;
	private List records = new ArrayList();
	
	public QueryResult()
	{
		
	}
	
	public void add(QueryResult result)
	{
		records.addAll(result.getRecords());
		count += result.getTotalCount();
		if(null == totalAmount)
		totalAmount = new BigDecimal(0);
		else
		totalAmount = totalAmount.add(result.getTotalAmount());
	}
	
	
	
	public static QueryResult buildQueryResult(List records,String fieldName)
	{
		QueryResult result = new QueryResult();
		try{
			BigDecimal totalAmount = new BigDecimal(0);
			Object amountTemp = null;
			if(records!=null&&records.size()>0){
				for(int i=0;i<records.size();i++){
					amountTemp = Ognl.getValue(fieldName, records.get(i));
					if(null == amountTemp){
						amountTemp = new BigDecimal(0);
					}
					totalAmount = totalAmount.add((BigDecimal)amountTemp);
				}
				result.setRecords(records);
				result.setTotalAmount(totalAmount);
				result.setTotalCount(records.size());
				}
			return result;
		}catch (OgnlException e){
			throw new RuntimeException("不能从对象的" + fieldName + "属性中获取金额!",e);
			
		}
		
	}
	
	public int getTotalCount(){
		return count;
	}
	
	public void setTotalCount(int totalCount){
		this.count = totalCount;
	}
	
	public BigDecimal getTotalAmount(){
		return totalAmount;
	}
	
	public void setTotalAmount(BigDecimal totalAmount){
		this.totalAmount = totalAmount;
	}
	
	public List getRecords(){
		return records;
	}
	
	public void setRecords(List records){
		this.records = records;
	}
}
