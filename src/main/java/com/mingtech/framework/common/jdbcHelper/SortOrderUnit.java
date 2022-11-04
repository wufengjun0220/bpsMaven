package com.mingtech.framework.common.jdbcHelper;
import java.io.Serializable;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SortOrderUnit implements Serializable{
        public static final String ASC = "ASC" ;
    	public static final String DESC = "DESC" ;

        private NameString nameString = new NameString() ;

        public SortOrderUnit()
        {
        }

        /**
         * Set order by the field name
         *
         *@param name the specifies fieldname of order
         *@param ascOrDesc specifies ascending or descending order
         */
    	public SortOrderUnit(String name, String ascOrDesc)
        {
        	nameString.setName(name) ;
            nameString.setValue(ascOrDesc) ;
    	}

        /**
         * Get the name of order
         *
         *@return the name of order
         */
        public String getName()
        {
        	return nameString.getName() ;
        }

        /**
         * Get the type of order
         *
         *@return the type of order
         */
        public String getOrderType()
        {
        	return nameString.getValue() ;
        }

        /**
         *  Set fieldname of order
         *
         */
        public void setName(String name)
        {
        	nameString.setName(name) ;
        }

        /**
         *  Set fieldtype of order
         *
         */
        public void setOrderType(String orderType)
        {
        	nameString.setValue(orderType) ;
        }

        public String toString()
        {
            String str = "";
            str += "name=" + getName();
            str += "\t" + this.getOrderType();
            return str;
        }

}