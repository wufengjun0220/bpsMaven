package com.mingtech.framework.common.jdbcHelper;
import java.text.DecimalFormat;


/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewBean implements ITransformable{
    
    
//    private Log log = Logger.getLogger(ViewBean.class);
    private Collection _datas = new Collection();
    private String _pattern = "##,###.00";
    /**
     * default constructor
     *
     */
    public ViewBean() {}
    
    public ViewBean(String[][] params) {
        if(params == null) {
            return;
        }
        int i = 0;
        int length = params.length;
        for (i = 0; i < length; i++) {
            addString(params[i][0],params[i][1]);
        }
    }

    /**
     * add the field by key & value
     * @param key
     * @param value
     */
    public void addString(String key, String value) {
        _datas.addElement(key,value);
    }
    /**
     * get an object from ViewBean
     * @param key
     * @return
     */
    public Object getObject(String key) {
        return _datas.getElement(key);
    }
    /**
     * save an Object to viewbean
     * @param key
     * @param value
     */
    public void addObject(String key,Object value) {
        _datas.addElement(key,value);
    }
    /**
     * Add the field by value
     * @param value
     */
    public void addString(String value) {
        _datas.addElement(value);
    }
    
    /**
     * append a ViewBean content into myself
     * if key same as original, overwrite its value
     * @param srcViewBean
     */
    public void append(ViewBean srcViewBean) {
        int i = 0;
        int length = _datas.length();
        for (i = 0; i < length; i++) {
            addString(srcViewBean.keyOfIndex(i),getString(i));
        }
    }
        
    /**
     * get the field's key by index
     * @param index
     * @return
     */
    public String keyOfIndex(int index) {
        return (String)_datas.keyOfIndex(index);
    }
    /**
     * display all content
     *
     */
    public String display() {
        int i=0, count=_datas.length();
        String key=null, value = null;
        StringBuffer sb = new StringBuffer();
        for(i=0; i<count; i++)
        {
            key = (String)_datas.keyOfIndex(i);
            value = (String)_datas.getElement(i);
            sb.append(key + "\t\t" + value);
        }
       return sb.toString(); 
    }
    /**
     * Get the string Array by the field's index and separator char.
     * @param index index of field's position
     * @param sep separator char such as '-',':'
     * @return data Array such as "2004-10-27"
     */
    public String[] getArray(int index, char sep) {
        String src = this.getString(index);
        return this.getStrArray(src, sep);
    }    
    /**
     * 
     * @param key   key of field
     * @param sep   separator char such as '-',':'
     * @return      data Array such as "2004-10-27"
     */
    public String[] getArray(String key, char sep)
    {
        String src = this.getString(key);
        return this.getStrArray(src, sep);
    }

    /**
     * Split a string into an array of String such as "aa|aa|cc|dd"
     * @param src  The String be splitted
     * @param sep  Separate character such as '|'
     * @return     An array of string
     */
    public String[] getStrArray(String src, char sep)
    {
        if(src.length() < 1)
        {
            return new String[0];
        }
        int i=0, length=src.length();
        int[] sepPos = new int[100];
        int counter = 0;
        char chr = sep;
        for(i=0; i<length; i++)
        {
            chr = src.charAt(i);
            if(chr == sep)
            {
                sepPos[counter++] = i;
            }
        }
        sepPos[counter++] = length;

        String[] ret = new String[counter];
        for(i=0; i<counter; i++)
        {
            if( i==0 )
            {
                ret[i] = src.substring(0, sepPos[i]);
            }
            else
            {
                ret[i] = src.substring(sepPos[i-1] + 1, sepPos[i]);
            }
        }

        sepPos = null;

        return ret;
    }
    
    /**
     * get Value by index
     * @param index
     * @return
     */
    public String getString(int index) {
        Object ret =_datas.getElement(index);
        return ret != null ? (String)ret : "" ;
    }
    /**
     * Get the value by key.The value means the field's value.
     * @param key key of Display collection
     * @return value of display field's name
     */
    public String getString(String key)
    {
        Object ret = _datas.getElement(key);
        return ret != null ? (String)ret : "" ;
    }
    /**
     * Get the value by key.if value is "",replace with alt
     * @param key
     * @param alt
     * @return
     */
    public String getString(String key, String alt){
        String value = getString(key);
        if(value.length() == 0)	value = alt;
        return value;
    }
    /**
     * Get the value by index.if value is "",replace with alt
     * @param index
     * @param alt
     * @return
     */
    public String getString(int index, String alt){
        String value = getString(index);
        if(value.length() == 0)	value = alt;
        return value;
    }
    
    /**
     * Get the date Array by index.
     * @param index index of field
     * @return date Array such as "2004-10-27"
     */
    public String[] getDateArray(int index){
        String dateStr = this.getString(index);
        return  dateStr.split("-");
    }
    
    
    /**
     * Get string from Date type. The string will be cut before a blank appeared.
     * @param index the index to lookup a string in the ViewBean
     * @param isSecond if true ,return YYYY-MM-DD HH:MM:SS,else return YYYY-MM-DD HH:MM 
     * @return 
     */
    public String getDateString(int index,boolean isSecond) {
        String val = getString(index);
        String retString = null ;
        val = val.trim();
        if(val.equals("")) return val;
        int blankPos = val.indexOf(' ');
        if(blankPos == -1)
        {
          if(isSecond)
                retString = val+" "+ "00:00:00";
          else
                retString = val+" "+ "00:00";
        }
        else
        {
                if(isSecond)
                        retString = val ;
                else
                {
                        String prePart =  val.substring(0,blankPos);
                        String nextPart = val.substring(blankPos);
                        nextPart = nextPart.trim();
                        retString = prePart + " " ;


                        blankPos = nextPart.indexOf(':');
                        if(blankPos==-1)
                        {
                          retString =  retString + nextPart ;
                          return retString ;
                        }
                        prePart =  nextPart.substring(0,blankPos);
                        nextPart = nextPart.substring(blankPos+1);
                        retString =  retString + prePart + ":" ;

                        blankPos = nextPart.indexOf(':');
                        if(blankPos==-1)
                          retString =  retString + nextPart ;
                        else
                        {
                          prePart =  nextPart.substring(0,blankPos);
                          retString =  retString + prePart ;
                        }
                }
        }
        return retString;

    }
    
    /**
     * Get string from Date type. The string will be cut before a blank appeared.
     * @param key the key to lookup a string in the ViewBean
     * @param isSecond if true ,return YYYY-MM-DD HH:MM:SS,else return YYYY-MM-DD HH:MM 
     * @return 
     */
    public String getDateString(String key,boolean isSecond)
    {
        String val = getString(key);
        String retString = null ;
        val = val.trim();
        if(val.equals("")) return val;
        int blankPos = val.indexOf(' ');
        if(blankPos == -1)
        {
          if(isSecond)
                retString = val+" "+ "00:00:00";
          else
                retString = val+" "+ "00:00";
        }
        else
        {
                if(isSecond)
                        retString = val ;
                else
                {

                        String prePart =  val.substring(0,blankPos);
                        String nextPart = val.substring(blankPos);
                        nextPart = nextPart.trim();
                        retString = prePart + " " ;


                        blankPos = nextPart.indexOf(':');
                        if(blankPos==-1)
                        {
                          retString =  retString + nextPart ;
                          return retString ;
                        }
                        prePart =  nextPart.substring(0,blankPos);
                        nextPart = nextPart.substring(blankPos+1);
                        retString =  retString + prePart + ":" ;

                        blankPos = nextPart.indexOf(':');
                        if(blankPos==-1)
                          retString =  retString + nextPart ;
                        else
                        {
                          prePart =  nextPart.substring(0,blankPos);
                          retString =  retString + prePart ;
                        }

                }
        }
        return retString;
    }
    
    /**
     * Get string from Date type. The string will be cut before a blank appeared
     * @param index the index to lookup date string in the ViewBean
     * @return
     */
    public String getDateString(int index){
        String val = getString(index);

        int blankPos = val.indexOf(' ');
        if(blankPos == -1)
        {
            return val;
        }
        return val.substring(0, blankPos);

    }

    /**
     * Get string from Date type. The string will be cut before a blank appeared
     * @param key the key to lookup date string in the ViewBean
     * @return
     */
    public String getDateString(String key){
        String val = getString(key);
        int blankPos = val.indexOf(' ');
        if(blankPos == -1)
        {
          return val;
        }
        return val.substring(0, blankPos);
    }
    
    /**
     * 
     * @param index
     * @return
     */
    public String getDoubleString(int index)
    {
        double d = 0;
                String ret = getString(index);
                try{
                    d = new Double(ret).doubleValue();
                }catch(Exception e)
                {}
        return format(d);
    }
    /**
     * 
     * @param key
     * @return
     */
    public String getDoubleString(String key) {
        double d= 0;
        String ret = getString(key);
        try {
            d = new Double(ret).doubleValue();
        }catch(Exception e){}
        return format(d);
    }
    
    public String getFloatString(int index) {
        return getDoubleString(index);
    }
    
    public String getFloatString(String key) {
        return getDoubleString(key);
    }
    
    public float getFloatValue(int index){
        String value = getString(index);
        float ret = 0;
        try{
            ret = Float.parseFloat(value);
        }catch(Exception e){
            ret = 0;
        }
        return ret;
    }
    
    public float getFloatValue(String key) {
        String value = getString(key);
        float ret = 0;
        try {
            ret = Float.parseFloat(value);
        } catch (Exception e) {
            ret = 0;
        }
        return ret;
    }
    public double getDoubleValue(int index){
        String value = getString(index);
        double ret = 0;
        try{
            ret = Double.parseDouble(value);
        }catch(Exception e){
            ret = 0;
        }
        return ret;
    }
    
    public double getDoubleValue(String key) {
        String value = getString(key);
        double ret = 0;
        try {
            ret = Double.parseDouble(value);
        } catch (Exception e) {
            ret = 0;
        }
        return ret;
    }

    public String getHtmlString(int index){
        String ret = getString(index);
        return ret.length() > 0 ? ret : "&nbsp;";
    }

    public String getHtmlString(String key){
        String ret = getString(key);
        return ret.length() > 0 ? ret : "&nbsp;";
    }
    
    public int getIntegerValue(String key){
        String value = getString(key);
        int index = value.indexOf(".");
        value = value.substring(0,index);
        int ret = 0;
        try
        {
            ret = Integer.parseInt(value.trim());
        }
        catch(Exception e)
        {
            ret = 0;
        }
        return ret;
    }

    public int getIntValue(int index){
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
    
    public int getIntValue(String key){
        String value = getString(key);
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
    /**
     * util method
     * @return
     */
    public CommonRecord ToCommRecord() {
        CommonRecord cr = new CommonRecord();
        for (int i = 0; i < this._datas.length(); i++) {
            String name = _datas.keyOfIndex(i).toString();
            String value = _datas.getElement(i).toString();
            cr.addField(new Field(name,value));
        }
        return cr;
    }
    
    /**
     * 
     * @param d
     * @return
     */
    private String format(double d){
        DecimalFormat df = new DecimalFormat(this._pattern);
        String str = df.format(d);
        if(str.equals(".00")){
           str="0";
        }
        return str;
    }


    /**
     * 
     * @return ViewBean's length
     */
    public int length() {
        return _datas.length();
    }
    /**
     * clear all data in the collection
     *
     */
    public void clear() {
        _datas.clear();
    }
    
    /**
     * clear the ViewBean's datas
     *
     */
    public void destroy() {
        clear();
        _datas = null;
    }
    
    /* (non-Javadoc)
     * @see cn.com.ccb.bdb.framework.core.ITransformable#getValue(java.lang.Object)
     */
    public Object getValue(Object key) {
        return _datas.getElement(key);
    }
    /* (non-Javadoc)
     * @see cn.com.ccb.bdb.framework.core.ITransformable#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        Object obj = getObject((String) key);
        if (obj != null) {
            this._datas.removeElement(key);
        }

        return obj;
    }
    
    /* (non-Javadoc)
     * @see cn.com.ccb.bdb.framework.core.ITransformable#rename(java.lang.Object, java.lang.Object)
     */
    public void rename(Object oldKey, Object newKey) {
        Object obj = remove((String)oldKey);

        if (obj != null)
            addObject((String)newKey, obj);

    }
}