package com.mingtech.framework.common.jdbcHelper;

import java.io.Serializable;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ITransformable extends Cloneable, Serializable{
    public void rename(Object oldKey, Object newKey);
    public Object remove(Object key);
    public Object getValue(Object key);
}