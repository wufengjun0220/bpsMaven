package com.mingtech.framework.common.jdbcHelper;

import java.io.Serializable;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NameString implements Serializable{
    private String _name = null;

    private String _value = null;

    public NameString() {
    }

    /**
     * Constructor of NameString
     * @param name the name of NameString
     * @param value the value of NameString
     */
    public NameString(String name, String value) {
        _name = name;
        _value = value;
    }

    /**
     * get name from a NameString
     * @return String
     */
    public String getName() {
        return _name;
    }

    /**
     * get value from a NameString
     * @return String
     */
    public String getValue() {
        return _value;
    }

    /**
     * set name into a NameString
     * @return void
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * set value into a NameString
     * @return void
     *  
     */
    public void setValue(String value) {
        _value = value;
    }
    
    public String toString(){
        return "name=" + this.getName()+"\tvalue=" + this.getValue();
    }

}
