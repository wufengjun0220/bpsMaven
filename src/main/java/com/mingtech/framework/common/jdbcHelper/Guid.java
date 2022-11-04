package com.mingtech.framework.common.jdbcHelper;
import java.io.Serializable;
import java.net.InetAddress;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Guid implements Serializable, Comparable {

    static String _inetAddr = initInetAddr();

    private String _guid;

    /**
     * Constructor
     *  
     */
    public Guid() {
        _guid = _inetAddr + ":" + new java.rmi.server.UID().toString();
    }

    /**
     * Constructor
     * 
     * @param guid
     */
    public Guid(String guid) {

        if (guid == null) {
            throw new NullPointerException(
                    "Cannot create guid with a null value");
        }

        _guid = guid.trim();
    }

    /**
     * @return guid
     */

    public String toString() {
        return _guid;
    }

    /**
     * @param o
     * @return true if equal else false
     */
    public boolean equals(Object o) {

        if (o instanceof Guid) {
            return toString().equals(o.toString());
        }

        return false;
    }

    /**
     * @return hash code
     */

    public int hashCode() {
        return (this.toString().hashCode());
    }

    /*
     * 
     * @return int value > 0, < 0,== 0
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        if ((o instanceof Guid) || (o instanceof String)) {
            return o.toString().compareTo(toString());
        }

        throw new ClassCastException("Can only compare Guid to Guid or String");
    }

    /**
     * 
     * @return String
     */

    private static String initInetAddr() {
        try {
            byte[] bytes = InetAddress.getLocalHost().getAddress();
            StringBuffer b = new StringBuffer();
            String s = null;
            for (int i = 0; i < bytes.length; i++) {
                s = Integer.toHexString(bytes[i]);
                if (bytes[i] < 0) {
                    b.append(s.substring(s.length() - 2));
                } else {
                    b.append(s);
                }
            }
            return b.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 
     * @return is empty return true else false
     */

    public boolean isEmpty() {

        return (((_guid == null) || (_guid.trim().length() == 0)) ? true
                : false);
    }

    public static void main(String[] args) {
    }
}