package com.mingtech.framework.common.jdbcHelper;
import java.io.Serializable;
import java.sql.Types;

public class Condition implements Serializable {
	private int pageNo = -1;

	private int pageSize = -1;
	
	private int totalSize=-1;

	private Collection criticals;

	private Collection orders;

	private String staticWhereCondition;

	private String staticOrderByCondition;
	
	//add by jacke for search divided by page on key column
	private int isPageSearchByOrder=-1;  
	private String keyColumn;//用于按关键字进行索引查询
	private int  combinNum;   //组合查旬起始字段顺序
	/**
	 * @return Returns the combiColumn.
	 */
	public int getcCombinNum() {
		return combinNum;
	}
	/**
	 * @param combiColumn The combiColumn to set.
	 */
	public void setCombinNum(int combinNum) {
		this.combinNum = combinNum;
	}
    /**
     * @return Returns the keyColumn.
     */
    public String getKeyColumn() {
        return keyColumn;
    }
    /**
     * @param keyColumn The keyColumn to set.
     */
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }
	

	public Condition(Collection theCriticals) {
		this.criticals = theCriticals;
	}
	
	//获取排序条件集合
	public Collection getOrders() {
		return orders;
	}
	
	//设置排序条件集合
	public void setOrders(Collection orders) {
		this.orders = orders;
	}
	
	//获取静态排序条件
	public String getStaticOrderByCondition() {
		return staticOrderByCondition;
	}
	
	//设置静态排序条件
	public void setStaticOrderByCondition(String staticOrderByCondition) {
		this.staticOrderByCondition = staticOrderByCondition;
	}
	
	//获取静态条件
	public String getStaticWhereCondition() {
		return staticWhereCondition;
	}
	
	//设置静态条件
	public void setStaticWhereCondition(String staticWhereCondition) {
		this.staticWhereCondition = staticWhereCondition;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void addCritical(Critical critical) {
		getCreatedCriticals().addElement(critical);
	}

	public void addCritical(String logicName, int type, String value) {
		Critical critical = new Critical(logicName, type, value);
		getCreatedCriticals().addElement(critical);
	}

	public void addCritical(String logicName, String operator, String value) {
		String[] valueArray=value.split(",");
		String Sql="";
		for(int i=0;i<valueArray.length;i++){	
			if(i!=0) Sql +=" or ";
			Sql += logicName;
			if(operator.equals(Critical.LEFT_LIKE)){				
				Sql += " like '"+valueArray[i]+"%' ";
			}else
			if(operator.equals(Critical.EQUAL)){
				Sql += " = '"+valueArray[i]+"' ";	
			}
		}
		if(!"".equals(Sql)){
			Critical critical = new Critical(Sql, "");
			critical.setIsClause();
			getCreatedCriticals().addElement(critical);
		}

	}
	public void addCritical(String logicName, int type, String operator,
			String value) {
		if((value.split(",").length>1)&&(operator.equals(Critical.LEFT_LIKE)||operator.equals(Critical.EQUAL))){
			addCritical(logicName, operator, value);
			return;
		}
		Critical critical = new Critical(logicName, type, value);
		critical.setCritical(operator);
		getCreatedCriticals().addElement(critical);
	}

	public Collection getCriticals() {
		if (criticals == null) {
			criticals = new Collection();
		}
		return criticals;
	}

	public String getWhereSQL() {
		String str = "";
		Critical critical = null;
		Field field = null;
		int i = 0;
		int count = 0;
		if (criticals != null) {
			count = criticals.length();
		}
		String strTmp = "";
		for (i = 0; i < count; i++) {			
			strTmp = "(";
			critical = (Critical) criticals.getElement(i);
			field = critical.getParameter();
			// 首先，判断这个 cirtical 是不是一个已由用户直接
			// 输入的条件的子句
			if (critical.isClause()) {
				strTmp += critical.getName();
				strTmp += ")";
				if (i != count - 1) {
					strTmp += " " + critical.getNextCritical() + " ";
				}
				str += strTmp;
				continue;
			}
			strTmp += critical.getName();
			strTmp += critical.getCritical();
			if (critical.isValue()) {
				switch (field.getType()) {
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:

					//strTmp += "to_date(?, 'YYYY-MM-DD HH24:MI:SS')" ;
					strTmp += "?";
					//strTmp += "convert(datetime, ?)";
					break;
				default:
					strTmp += "?";
					break;
				}
			} else {
				//strTmp += field.getValue().toString();
				if (field.getValue() == null) {
					strTmp += null;
				} else {
					strTmp += field.getValue().toString();
				}
			}
			strTmp += ")";

			if (i != count - 1) {
				strTmp += " " + critical.getNextCritical() + " ";
			}
			str += strTmp;
		}
		if (staticWhereCondition != null) {
			if (str.length() > 0) {
				str += " and " + staticWhereCondition;
			} else {
				str = staticWhereCondition;
			}
		}
		return str;
	}
	
	//获取where条件包含主键查询功能
	public String getWhereAndOrderSql(){
	    String sql=getWhereSQL();
	    if(orders!=null){
	        sql+=" order by ";
	        for(int i=0;i<orders.length();i++){
	            SortOrderUnit order=(SortOrderUnit)orders.getElement(0);
	            sql+=order.getName()+" "+order.getOrderType();
	            if(i!=(orders.length()-1)){
	                sql+=",";
	            }
	        }
	    }
	    return sql;
	}
	
//	获取Order条件包含主键查询功能
	public String getOrderSql(){
	    String sql="";
	    if(orders!=null){
	        sql+=" order by ";
	        for(int i=0;i<orders.length();i++){
	            SortOrderUnit order=(SortOrderUnit)orders.getElement(i);
	            sql+=order.getName()+" "+order.getOrderType();
	            if(i!=(orders.length()-1)){
	                sql+=",";
	            }
	        }
	    }
	    return sql;
	}
	
	//设定搜索按关键字段的排序搜索
	public void setIsPageSearchByOrder(int psbo){
	    this.isPageSearchByOrder=psbo;
	}
	//返回是否按关键字段进行排序搜索
	public int getIsPageSearchByOrder(){
	    return this.isPageSearchByOrder;
	}

	public CommonRecord getParamters() {
		CommonRecord tmpCR = new CommonRecord();
		if (criticals == null) {
			return tmpCR;
		}
		int size = criticals.length();
		for (int i = 0; i < size; i++) {
			Field tmp = (Field) criticals.getElement(i);
//			add by sch ,to support add critical in busihanler
			if(tmp instanceof Critical){
				Critical obj=(Critical)tmp;
				if(obj.isClause()){
					continue;
				}
			}
			//end
			Field tmpCory = new Field(tmp);
			tmpCory.setName("Field" + i);
			tmpCR.addField(tmpCory);
		}
		return tmpCR;
	}

	public boolean isDivPage() {
		return pageNo > -1 && pageSize > 0;
	}

	private Collection getCreatedCriticals() {
		if (criticals == null) {
			criticals = new Collection();
		}
		return criticals;
	}
	
	
	
	//大哥票据 金钩参数对照
	public String bill_branchid;
	public String sum_branchid;
	public String comm_amt;//用于比较大小
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
}