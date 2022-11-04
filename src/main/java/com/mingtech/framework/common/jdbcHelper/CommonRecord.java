package com.mingtech.framework.common.jdbcHelper;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class CommonRecord implements ITransformable{

    /* (non-Javadoc)
     * @see com.leadmind.framework.core.ITransformable#rename(java.lang.Object, java.lang.Object)
     */
    public void rename(Object oldKey, Object newKey) {
        Field fld = removeField((String)oldKey);
        if (fld != null) {
            fld.setName((String)newKey);
            addField(fld);
        }
    }

    /* (non-Javadoc)
     * @see com.leadmind.framework.core.ITransformable#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        return removeField((String)key);
    }

    /* (non-Javadoc)
     * @see com.leadmind.framework.core.ITransformable#getValue(java.lang.Object)
     */
    public Object getValue(Object key) {
        Field fld = getField((String)key);
        Object obj = null;
        if (fld != null) {
            obj = fld.getValue();
        }
        return obj;
    }
//    private Log _debug = Logger.getLogger(CommonRecord.class);
    private Collection _fields = new Collection() ;
    public static final String IDEN_ID = "iden_id";//如果自增长列名确定，这个名字可以修改！

    public CommonRecord()
    {
    }

    /**
     * Destroy this CommonRecord object
     */
    public void destroy()
    {
        _fields = null ;
    }

    /**
     *  Clear the all value of field
     */
    public void clear()
    {
        _fields.clear() ;
    }

    /**
     *  Add the specified field
     *  if a field with the same name has been stored, then overwrite it
     *
     *@param field the field to be added.
     */
    public CommonRecord addField(Field field)
    {
        //_debug.log("Add a field name ==>" + field.getName() + "\tvalue ==>" + field.getValue());
        _fields.addElement(field.getName(), field);
        return this;
    }

    /**
     *  Add the specified field
     *
     *@param field the field to be added.
     *@param key the key of CommonRecord object
     */
    public CommonRecord addField(String key, Field field)
    {
        _fields.addElement(key ,field);
        return this;
    }

    /**
     *  Delete the field by index
     *
     *@param index the index of the object to remove.
     */
    public Field removeField(int index)
    {
        Field fld = (Field)_fields.getElement(index);
        _fields.removeElement(index);
        return fld;
    }

    public void removeFields(String[] keys) {
        for (int i=0; i<keys.length; i++) {
            _fields.removeElement(keys[i]);
        }
    }

    public void removeFields(List list) {
        String name = null;
        for (int i=0; i<list.size(); i++) {
            name = (String)list.get(i);
            _fields.removeElement(name);
//            _debug.debug("Field [" + name + "] has been removed!").append('\n');
        }
    }

    public void removeFields(Hashtable keys) {
        Enumeration e = keys.keys();
        String name = null;
        StringBuffer sb = new StringBuffer();
        while (e.hasMoreElements()) {
            name = (String)e.nextElement();
            _fields.removeElement(name);
//            _debug.debug("Field [" + name + "] has been removed!").append('/n');
        }
    }

    /**
     *  Remove the field by key
     *
     *@param key the key of CommonRecord object
     */
    public Field removeField(String key)
    {
        Field fld = (Field)_fields.getElement(key);
        _fields.removeElement(key);
        return fld;
    }

    /**
     *  Get the number of the all fields
     *
     *@return Returns the number of the all fields
     */
    public int getTotalFields()
    {
        return _fields.length();
    }

    /**
     *  get the all field object
     *
     *@return Returns the all object of field
     */
    public Field[] getFields()
    {
        int i=0, count = _fields.length() ;
        Field[] ret = new Field[count] ;
        for(i=0; i<count; i++)
        {
            ret[i] = (Field)_fields.getElement(i) ;
        }
        return ret ;
    }


    public Object getRawValue(int index)
    {
        Field field = this.getField(index);
        if(field == null) return null;
        return field.getValue();
    }

    public Object getRawValue(String fieldName)
    {
        Field field = this.getField(fieldName);
        if(field == null) return null;
        return field.getValue();
    }
    /**
     *  Get the value by field name
     *
     *@param fieldName the specified name of field
     *@return the value of field
     */
    public String getValue(String fieldName)
    {
        Field field = getField(fieldName);
        if(field == null) return null;
        String value = null;
        try
        {
            value = (String)field.getValue();
        }
        catch(Exception e)
        {
        }
        return value;
    }

    /**
     *  Get the value by field index
     *
     *@param index the specified index
     *@return the value of field
     */
    public String getValue(int index)
    {
      Field field = getField(index);
      if(field == null) return null;
      String value = null;
      try
      {
        value = (String)field.getValue();
      }
      catch(Exception e)
      {
      }
      return value;
    }

    /**
     *  Get the value by field name
     *
     *@param fieldName the specified field name
     *@return the value of field
     */
    public String getString(String fieldName)
    {
        Field field = getField(fieldName);
        if(field == null) return null;
        String value = null;
        try
        {
            value = (String)field.getValue();
        }
        catch(Exception e)
        {
        }
        return value;
    }

    /**
     *  Set the value by field name
     *
     *@param fieldName the specified field name
     *@param object the specified field name's value
     *@return  None
     */
    public void setValue(String fieldName , Object object )
    {
        Field field = getField(fieldName);
        if(field == null) return;
        field.setValue(object);
    }

    public void setValue(int index , Object object )
    {
        Field field = getField(index);
        if(field == null) return;
        field.setValue ( object );;
    }


    /**
     *  Get the value by field index
     *
     *@param index the specified index
     *@return the value of field
     */
    public String getString(int index)
    {
        Field field = getField(index);
        if(field == null) return null;
        String val = null;
        try
        {
            val = (String)field.getValue();
        }
        catch(Exception e)
        {
        }
        return val;
    }

    /*
    public int getInt(String fieldName)
    {
        Field value = (Field)_fields.getElement(fieldName) ;
        Integer ret = null ;
        switch(value.getType())
        {
        case Types.BIGINT:
        case Types.INTEGER:
            ret = (Integer)value.getValue() ;
            break;
        default:
            ret = new Integer(0) ;
            break;
        }
        return ret.intValue() ;
    }
    */
    /*
    public double getDouble(String fieldName)
    {
        Field value = (Field)_fields.getElement(fieldName) ;
        return ((Double)value.getValue()).doubleValue() ;
    }
    */

    /**
     *  Get the value by index of the Filed object
     *
     *@param index the specified index
     *@return a Field object
     */
    public Field getField(int index)
    {
      Field fld = (Field)_fields.getElement(index);
      return fld;
    }

    /**
     *  Get the Field object by fieldName of the Field object
     *  if this field with the specified name not exist, return null;
     *
     * @param fieldName the specified field name
     * @return a Field object
     */
    public Field getField(String fieldName)
    {
      int i = 0, length = this.length();
      Field fldTmp = null;
      String name = null;
      for(i=0; i<length; i++)
      {
        fldTmp = (Field)_fields.getElement(i);
        name = fldTmp.getName();
        if(name == null) continue;
        if(name.equalsIgnoreCase(fieldName))
        {
            return fldTmp;
        }
      }
      return null;
    }

    /**
     * Get the number of the field
     *
     *@return the number
     */
    public int size()
    {
        return _fields.length() ;
    }

    /**
     *Tests if this field maps no keys to value.
     *
     *@reutrn boolean
     */
    public boolean isEmpty()
    {
        return _fields.isEmpty() ;
    }

    /**
     * @return the specified field name by index
     */
    public String getFieldName(int index)
    {
                return (String)_fields.keyOfIndex(index);
    }

    /**
     * Get the number of all fields
     *
     *@return Return the number of all fields
     */
    public int length()
    {
        return _fields.length() ;
    }

    public void append(CommonRecord rec)
    {
        int i=0, count=rec.length();
        Field field = null;
        for(i=0; i<count; i++)
        {
          addField(rec.getField(i));
        }
    }

    public String displayContent()
    {
        int i=0, count=length() ;
        Field field = null ;
        StringBuffer sb = new StringBuffer();
        for(i=0; i<count; i++)
        {
            field = getField(i) ;
            sb.append(field.getName() + '\t' + field.getTypeName() + '\t' + field.getValue() +'\n') ;
        }
        return sb.toString(); 
    }

    /**
     * Display all fileds of the CommonRecord
     *
     */


    public int getIntValue(int index)
    {
        String value = getString(index);
        int ret = 0;
        try
        {
            ret = Integer.parseInt(value);
        }
        catch(Exception e)
        {
            ret = 0;
        }
        return ret;
    }

    public int getIntValue(String key)
    {
        String value = getString(key);
        int ret = 0;
        try
        {
        	if(value!=null) {
            	int temp = value.indexOf(".");
            	if (temp==-1) {
            		ret = Integer.parseInt(value);
            	} else {
            		ret = Integer.parseInt(value.substring(0,temp));
            	}
        	
        	}
            
        }
        catch(Exception e)
        {
        	
        	
            ret = 0;
        }
        return ret;
    }


    /**
    Get a float field value form record, if the value not a valid format
    throw ApplicationException.

    @param index field index
    @return float value of the field
    @throws ApplicationExcepton
    */
    public float getFloatValue(int index)
    {
        float f = 0.0f;
        Field field = this.getField(index);
        try{
        f = Float.parseFloat((String)field.getValue());
        }catch(Exception e)
        {
        }
        return f;
    }
    public double getDoubleValue(int index)
    {
        double d = 0.0;
        Field field = this.getField(index);
        try{
        d = Double.parseDouble((String)field.getValue());
        }catch(Exception e)
        {
        }
        return d;
    }

    public double getDoubleValue(String key)
    {
        double d = 0.0;
        Field field = this.getField(key);
        try{
        d = Double.parseDouble((String)field.getValue());
        }catch(Exception e)
        {
        }
        return d;
    }

    /**
    Get a float field value form record, if the value not a valid format
    throw ApplicationException.

    @param key field key
    @return float value of the field
    @throws ApplicationExcepton
    */
    public float getFloatValue(String key)
    {
        float f = 0.0f;
        Field field = this.getField(key);
        try{f = Float.parseFloat((String)field.getValue());
        }catch(Exception e)
        {
        }
        return f;
    }

    public String toString(boolean simple) {
        if (true) {
            int i=0, count = length();
            StringBuffer str = new StringBuffer();
            Field field = null;
            for(i=0; i<count; i++) {
                field = getField(i);
                str.append("name=")
                   .append(field.getName())
                   .append("\ttype=")
                   .append(field.getTypeName())
                   .append("\tclass=")
                   .append(field.getValue().getClass().getName())
                   .append("\tvalue=[")
                   .append(field.getValue())
                   .append("]\n");
            }
            return str.toString();
        }
        return toString("");
    }

    public String toString()
    {
        return toString("");
    }

    public String toString(String header)
    {
        int i=0, count = length();
        String str = "<UL>" + header + "\n";
        Field field = null;
        for(i=0; i<count; i++)
        {
            field = getField(i);
            str += "<li>name=" + field.getName() +
                    "\ttype=" + field.getTypeName() +
                    "\tvalue=" + field.getValue() + "</li>\n";
        }
        str += "</UL>\n";
        return str;
    }

    public String toParamString()
    {
        String name = null, value = null;
        int i = 0, length = length();
        String str = "";
        for(i=0; i<length; i++)
        {
            name = getField(i).getName();
            if(i != 0)
            {
                str += "&" + name + "=" + getString(i);
            }
            else
            {
                str += name + "=" + getString(i);
            }
        }
        return str;
    }

    /**
     *  *	以字符串数组的形式返回CommonRecord中由关键字指定的字段的值
     *  如果该字段不存在返回　new String[0]
     *  if the field has a null value, return new String[0]
     *  if the field value has a type of Types.BLOB throw a exception
     *  if the field value has a type of Types.ARRAY, return String[]
     *  if the field's value can not cast to String, throw a exception
     * @param key key of field
     * @return STring[]
     * @exception Exception
     */
    public String[] getValues(String fieldName) throws Exception
    {
        String[] values = new String[0];
        Field field = getField(fieldName);
        Object value = null;
        if(field == null)
        {
            return values;
        }

        if(field.getType() == Types.ARRAY)
        {
            value = field.getValue();
            if(value != null)
            {
                values = (String[])field.getValue();
            }
        }
        else if(field.getType() == Types.BLOB)
        {
            throw new Exception("Binary data can not convert into string[]");
        }
        else
        {
            value = field.getValue();
            try
            {
                if(value != null)
                {
                    values = new String[1];
                    values[0] = (String)value;
                }
            }
            catch(ClassCastException e)
            {
                throw new Exception("Can not cast value(" + value + ") to String!");
            }
        }
        return values;
    }

    private String toString(String[] values)
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

    public Object clone() {
        /*
        CommonRecord c = new CommonRecord();
        c._fields = (Collection)this._fields.clone();

        return c;
        */
        CommonRecord c = new CommonRecord();
        Field f = null;
        int i = 0, length = length();
        for(i=0; i<length; i++) {
            f = getField(i);
            c.addField(new Field(f.getName(), f.getType(), f.getValue()));
        }
        return c;
    }

    public CommonRecord copy()
    {
        CommonRecord copy = new CommonRecord();
        int i = 0, length = length();
        for(i=0; i<length; i++)
        {
            copy.addField(getField(i).copy());
        }
        return copy;
    }

  /*
   *return fieldName = 'fieldValue';
  */
    public String getIdenIDPara() {
        int i=0, count=length() ;
        Field field = null ;
        String fieldName = null;
        Object fieldValue = null;
        String iden_id = "";
        for(i=0; i<count; i++){
            field = getField(i) ;
            fieldName = field.getName();
            if(fieldName.indexOf(IDEN_ID)!=-1) {
              fieldValue = field.getValue();
              iden_id = fieldName + "=\'" + fieldValue + "\'";
              break;
            } else continue;
         }
        return iden_id;
       }

    public String getIdenIDPara(String tableName) {
        int i=0, count=length() ;
        Field field = null ;
        String fieldName = null;
        Object fieldValue = null;
        String iden_id = "";
        for(i=0; i<count; i++){
            field = getField(i) ;
            fieldName = field.getName();
            if(fieldName.indexOf(tableName + "."+IDEN_ID)!=-1) {
              fieldValue = field.getValue();
              iden_id = fieldName + "=\'" + fieldValue + "\'";
              break;
            } else continue;
         }
        return iden_id;
       }
     public boolean containsField(String fieldname) {
        int i=0, count=length() ;
        Field field = null ;
        String fieldName = null;
        Object fieldValue = null;
        String iden_id = "";
        for(i=0; i<count; i++){
            field = getField(i) ;
            fieldName = field.getName();
            if(fieldName.indexOf(fieldname)!=-1) {
              return true;
            }
         }
        return false;
     }

     public Field getStatusTypeField(String tableName){
       int i=0, count=length() ;
       Field field = null ;
       String fieldName = null;
       Object fieldValue = null;
       String iden_id = "";
       for(i=0; i<count; i++){
           field = getField(i) ;
           fieldName = field.getName();
           if(fieldName.indexOf("status-type."+tableName)!=-1) {
             break;
           }
        }
       return field;
     }
     public CommonRecord getColsCommonRecord(){
       CommonRecord cr = new CommonRecord();
       int i=0, count=length() ;
       Field field = null ;
       String fieldName = null;
       Object fieldValue = null;
       String iden_id = "";
       for(i=0; i<count; i++){
           field = getField(i) ;
           fieldName = field.getName();
           if(fieldName.indexOf("col.")!=-1) {
             cr.addField(field);
           }
        }
       int m,n;
       String namem,namen;
       Object valuem,valuen;
       boolean done = false;
       n = 1;
       while((n<cr.length())&&(!done))
       {
         done = true;
         for (m=0;m<cr.length()-n ;m++ )
         {
           Field fm = cr.getField(m);
           Field fn = cr.getField(m+1);
           namem = fm.getName();
           valuem = fm.getValue();
           namen = fn.getName();
           valuen = fn.getValue();

           if(Integer.parseInt(namem.substring(0,namem.indexOf("col."))) > Integer.parseInt(namen.substring(0,namen.indexOf("col."))))
           {
              done = false;
              cr.getField(m).setName(namen);
              cr.getField(m).setValue(valuen);
              cr.getField(m+1).setName(namem);
              cr.getField(m+1).setValue(valuem);
           }
         }
         n++;
           }
       return cr;
     }

     /**
      * @version 1.0 by shanewang 
      * Put the all fields into ViewBean
      * @return
      */
     public ViewBean toViewBean() {
        int i = 0, length = this.length();
        String fieldName = null;
        String[] values = null;
        ViewBean viewBean = new ViewBean();

        for (i = 0; i < length; i++) {
            fieldName = getFieldName(i);

            try {
                values = getValues(fieldName);
            } catch (Exception e) {
                values = new String[0];
            }

            if (values.length == 1) {
                viewBean.addString(fieldName, values[0]);
            } else if (values.length > 1) {
                viewBean.addString(fieldName, toString(values));
            }
        }
        return viewBean;
    }


}
