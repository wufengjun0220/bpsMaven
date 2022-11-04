package com.mingtech.framework.common.jdbcHelper;
import java.sql.Types;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Field
{
    private String _name = null;
    private int	   _type = 0 ;
    private Object _value = null;

    private boolean _serial         = false;         // whether is a sequence no
    private boolean _primaryKey     = false;     // whether is a primary key
    private boolean _unique         = false;    // whether or not an unique field
    private String  _description    = null;     // field description
    private int     _length         = 0;             // field length

    public Field(String name, String description, int type, Object value, boolean serial, boolean primaryKey, int length)
    {
        _name = name;
        _description = description;
        _type = type;
        _value = value;
        _serial = serial;
        _primaryKey = primaryKey;
        _length = length;
    }

    public Field(String name, String description, int type, Object value, boolean unique)
    {
        _name = name;
        _description = description;
        _type = type;
        _value = value;
        _unique = unique;
    }

    // is this field an unique field
    public boolean isUnique()
    {
        return _unique | _serial;
    }

    public void setUnique()
    {
        _unique = true;
    }

    public boolean isSerial()
    {
        return _serial;
    }

    public boolean isPrimaryKey()
    {
        return _primaryKey;
    }

    public String getDescription()
    {
        if(_description == null) return "";
        return _description;
    }

    public void setDescription(String description)
    {
        _description = description;
    }
    /* end of extention*/

    /**
     * constructor of this class
     *
     */
    public Field()
    {
    }

    /**
     *  Construct this class by field
     */
    public Field(Field field)
    {
        _name = field.getName() ;
        _type = field.getType() ;
        _value = field.getValue() ;
    }

    /**
     *  Construct this class by name,type,value
     *
     */
    public Field(String name, int type, Object value)
    {
        this._name = name;
        this._type = type;
        this._value = value;
    }

    /**
     *  Construct this class by name,value
     *
     */
    public Field(String name, Object value)
    {
        this(name, Types.VARCHAR, value) ;
    }

    /**
     * Get the name of field
     *
     *@return the current name of field
     */
    public String getName()
    {
        return _name ;
    }

    /**
     * Get the type of field
     *
     *@return the current type of field
     */
    public int getType()
    {
        return _type ;
    }

    /**
     * Get the object of field
     *
     *@return the object of current field
     */
    public Object getValue()
    {
        return _value ;
    }

    /**
     * Set the name of current field
     *
     */
    public void setName(String name)
    {
        _name = name ;
    }

    /**
     * Set the type of current field
     *
     */
    public void setType(int type)
    {
        _type = type ;
    }

    /**
     * Set the object of current field
     *
     */
    public void setValue(Object value)
    {
        _value = value ;
    }

    /**
    * Get data type name
    */
    public String getTypeName()
    {
        String typeName = "Unknown Type " + _type;
        switch(_type)
        {
        case Types.ARRAY:
            typeName = "ARRAY";
            break;
        case Types.BIGINT:
            typeName = "BIGINT";
            break;
        case Types.BINARY:
            typeName = "BINARY";
            break;
        case Types.BIT:
            typeName = "BIT";
            break;
        case Types.BLOB:
            typeName = "BLOB";
            break;
        case Types.CHAR:
            typeName = "CHAR";
            break;
        case Types.CLOB:
            typeName = "CLOB";
            break;
        case Types.DATE:
            typeName = "DATE";
            break;
        case Types.DECIMAL:
            typeName = "DECIMAL";
            break;
        case Types.DISTINCT:
            typeName = "DISTINCT";
            break;
        case Types.DOUBLE:
            typeName = "DOUBLE";
            break;
        case Types.FLOAT:
            typeName = "FLOAT";
            break;
        case Types.INTEGER:
            typeName = "INTEGER";
            break;
        case Types.JAVA_OBJECT:
            typeName = "JAVA_OBJECT";
            break;
        case Types.LONGVARBINARY:
            typeName = "LONGVARBINARY";
            break;
        case Types.LONGVARCHAR:
            typeName = "LONGVARCHAR";
            break;
        case Types.NULL:
            typeName = "NULL";
            break;
        case Types.NUMERIC:
            typeName = "NUMERIC";
            break;
        case Types.OTHER:
            typeName = "OTHER";
            break;
        case Types.REAL:
            typeName = "REAL";
            break;
        case Types.REF:
            typeName = "REF";
            break;
        case Types.SMALLINT:
            typeName = "SMALLINT";
            break;
        case Types.STRUCT:
            typeName = "STRUCT";
            break;
        case Types.TIME:
            typeName = "TIME";
            break;
        case Types.TIMESTAMP:
            typeName = "TIMESTAMP";
            break;
        case Types.TINYINT:
            typeName = "TINYINT";
            break;
        case Types.VARBINARY:
            typeName = "VARBINARY";
            break;
        case Types.VARCHAR:
            typeName = "VARCHAR";
            break;
        }
        return typeName;
    }

    public String toString()
    {
        String value = null;
        String str = "";
        str += "name=" + getName();
        str += "\ttype=" + getTypeName();

        if(_type == Types.ARRAY)
        {
            value = arrayToString((String[])getValue());
        } else {
            value = this._value.toString();
        }
        str += "\tvalue=" + value;
        return str;
    }

    private String arrayToString( String[] values)
    {
        String str = "";
        if(values == null) return str;
        int i = 0, length = values.length;
        for(i=0; i<length; i++)
        {
            str += values[i];
            if(i != length - 1)
            {
                str += ",";
            }
        }
        return str;
    }

    /**
     * same as clone
     */
    public Field copy()
    {
        Field field = new Field();
        field._description = _description;
        field._length = _length;
        field._name = _name;
        field._primaryKey = _primaryKey;
        field._serial = _serial;
        field._type = _type;
        field._unique = _unique;
        field._value = _value;
        return field;
    }

    /**
     * @return this
     */
    public Field getSelf()
    {
        return this;
    }
}

