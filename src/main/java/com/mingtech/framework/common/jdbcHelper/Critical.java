package com.mingtech.framework.common.jdbcHelper;

import java.sql.Types;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Critical extends Field{
    static public final String   EQUAL =" = ";
    static public final String   LESSANDEQUAL   ="<=";
    static public final String   LESS           ="<";
    static public final String   MORE		    = ">" ;
    static public final String   MOREANDEQUAL   = ">=" ;
    static public final String   LIKE		    = " LIKE ";
    static public final String   LEFT_LIKE		    = " LLIKE ";
    static public final String   NOT_EQUAL      = "!=";
    
    static public final String   NOT_LIKE       ="not like";    //不包含
    static public final  String  BEGIN          ="begin";       //始于
    static public final String   NOT_BEGIN      ="not begin";   //并非始于
    static public final String   STOP           ="stop";        //止于
    static public final String   NOT_STOP       ="not stop";    //并非止于

    static public final String   IS_NULL      = " IS ";

    static public final String   AND =" AND ";
    static public final String   OR  = " OR ";

//    static final boolean  ISVALUE      = true;
//    static final boolean  ISTABLEVALUE = false;

    private String _critical     ="=";  //">",">=","<>",....
    private String _nextCritical ="AND";  //" AND "," OR "
    private boolean _isValue = true;
    private int     _groupID = 0;

    // 用户已有 where 子句，如( a1 = '333' or a2= '444' or a3 = '444 ' )
    // 需要直接加入 where 子句
    private boolean _isClause = false ;
    
    //组合查询的起始字段   add by jacke
    private boolean isCombin=false;
    /**
     *  Construct a new Critical object
     */
    public Critical()
    {
      	super("",Types.VARCHAR,"");
    }

    /**
     *  Construct a new Critical object by field
     */
    public Critical(Field field)
    {
      	super(field) ;
    }

    /**
     *  Construct a new Critical object by fieldname, fieldtype, fieldvalue
     */
    public Critical(String name, int type, Object value)
    {
      	super(name, type, value) ;
    }

    /**
    * default groupID = 0
    * you can specify a groupID so all critical in one group will be in one (...)
    */
    public Critical(String name, int type, Object value, int groupID)
    {
        super(name, type, value);
        _groupID = groupID;
    }

    /**
     *  Construct a new Critical object by fieldname,fieldvalue
     */
    public Critical(String name, Object value)
    {
      	super(name, Types.VARCHAR, value.toString()) ;
    }

    /**
     *  Construct a new Critical object
     */
    public Critical(String name, int value)
    {
      	super(name, Types.INTEGER, new Integer(value)) ;
    }

    /**
     *  Construct a new Critical object
     */
    public Critical(String name, String value)
    {
      	super(name, Types.VARCHAR, value) ;
    }

    /**
     *  Construct a new Critical object
     */
    public Critical(String name, double value)
    {
      	super(name, Types.DOUBLE, new Double(value)) ;
    }

    /**
     set critical operator
     @param String critical	value as follows:
    	Critical.LESS
    	Critical.LESSANDEQUAL
    	Critical.MORE
    	Critical.MOREANDEQUAL
    	Critical.EQUAL
    	Critical.LIKE
    */
    public Critical setCritical(String critical)
    {
      	this._critical = critical;
      	if(_critical.equals(Critical.LIKE))//包含于与不包含于
      	{
            String value = (String)super.getValue();
            super.setValue(new StringBuffer("%").append(value).append("%").toString());
      	}
      	if(_critical.equals(Critical.LEFT_LIKE)){
      		this._critical = Critical.LIKE;
            String value = (String)super.getValue();
            super.setValue(new StringBuffer(value).append("%").toString());
      	}
        if(_critical.equals(Critical.IS_NULL))
      	{
            if(((String)super.getValue()).equals (""))
            {
                super.setValue(null);
            }
      	}
        
        // add by jacke  
        if(critical.equals(NOT_LIKE)){
        	this._critical=" not like";
        	String value=(String)super.getValue();
        	super.setValue(new StringBuffer("%").append(value).append("%").toString());
        }
        if(critical.equals(BEGIN)){       //始于
        	this._critical=" like";
        	String value=(String)super.getValue();
        	super.setValue(new StringBuffer(value).append("%").toString());
        }
        if(critical.equals(NOT_BEGIN)){     //非始于
        	this._critical=" not like";
        	String value=(String)super.getValue();
        	super.setValue(new StringBuffer(value).append("%").toString());
        }
        if(critical.equals(STOP)){          //止于
        	this._critical=" like";
        	String value=(String)super.getValue();
        	super.setValue(new StringBuffer("%").append(value).toString());
        }
        if(critical.equals(NOT_STOP)){        //非止于
        	this._critical=" not like";
        	String value=(String)super.getValue();
        	super.setValue(new StringBuffer("%").append(value).toString());
        }
        
        return this;
    }

    /**
     *  Set the next critical by nextCritical
     */
    public void setNextCritical(String nextCritical)
    {
      	this._nextCritical = nextCritical;
    }

    /**
     * Set the value of critical
     */
    public void setISVALUE(boolean isValue)
    {
      	this._isValue = isValue;
    }

    /**
     * Set the value of critical
     * 说明这句是已生成好的 where 子句，不需要再处理了
     */
    public Critical setIsClause()
    {
      	this._isClause  = true ;
        return this;
    }

    /**
     * Get the value of critical
     * 判断这句是不是已生成好的 where 子句
     */
    public boolean isClause()
    {
      	return this._isClause  ;
    }


    /**
     * Get the value of critical
     */
    public boolean isValue()
    {
      	return this._isValue ;
    }

    /**
     *  Get the string of critical
     *
     *@return the string of critical
     */
    public String getCritical(){
      	return this._critical ;
    }

    /**
     *  Get the next critical
     *
     *@retrun the string of critical
     */
    public String getNextCritical()
    {
      	return this._nextCritical ;
    }

    /**
     * Get a object of Field
     *
     *@return a object of Field
     */
    public Field getParameter()
    {
      	return super.getSelf();
    }

    public String toString()
    {
        String str = "";
        str += "name=" + super.getName();
        str += "\ttype=" + super.getTypeName();
        str += "\tvalue=" + super.getValue();
        str += "\tcritical=" + this._critical;
        str += "\tisValue=" + this._isValue;
        return str;
    }

    public int getGroupID()
    {
        return _groupID;
    }

    public void setGroupID(int groupID)
    {
        _groupID = groupID;
    }

	/**
	 * @return Returns the isCombin.
	 */
	public boolean isCombin() {
		return isCombin;
	}
	/**
	 * @param isCombin The isCombin to set.
	 */
	public void setCombin(boolean isCombin) {
		this.isCombin = isCombin;
	}
}