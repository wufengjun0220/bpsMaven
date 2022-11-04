package com.mingtech.framework.common.jdbcHelper;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Collection implements Serializable, Cloneable {
    /**
     * this collection can maitain how many element,
     * when exceed, auto plus 100
     */
    private int _capacity = 200;

    private Hashtable _data;
    private Object[]  _keys;
    private int      _length = 0;

    /**
     * constructor, specified capacity
     */
    public Collection(int capacity)
    {
        _capacity = capacity;
        _data = new Hashtable(_capacity);
        _keys = new Object[_capacity];
    }

    /**
     * use default capacity 200
     */
    public Collection()
    {
        this(200);
    }

    /**
     * how many element in this collection
     */
    public int length()
    {
        return _length;
    }

    /**
     * add an element into collection with specified key,
     * if the key exist, overwrite the old value with that key,
     * else appeng this element to the end of collection
     */
    public void addElement(Object key, Object value)
    {
        int index = indexOfKey(key);
        if(index != -1)
        {
            _data.put(key, value);
            return;
        }

        int length = _length + 1;
        if(length > _capacity)
        {
            enlargeTo(_capacity + 100);
        }

        _data.put(key, value);
        _length++;
        _keys[_length - 1] = key;
    }

    /**
     * add an element into collection with an auto generated key which is unique
     */
    public void addElement(Object value)
    {
        Object key = getNewKey();
        addElement(key, value);
    }

    /**
     * get the element with the specified key
     *
     * @param key key that indicate a value in collection
     *
     * @return value associated with specified key,
     * if the key not found in collection, return null.
     */
    public Object getElement(Object key)
    {
        Object value = _data.get(key);
        return value;
    }

    /**
     * get an element from collection with specified index.
     * index is 0 based.
     * @param index in collection
     * @return element in collection with specified index, if index out of range return null.
     */
    public Object getElement(int index)
    {
        if(index < 0 || index >= _length)
        {
            return null;
        }

        Object key = _keys[index];
        if(key == null) return null;

        Object value = _data.get(key);
        return value;
    }

    /**
     * remove an element from collection with specified key,
     * if key is null, nothing is done.
     */
    public void removeElement(Object key)
    {
        if(key == null || !_data.containsKey(key)) return;

        int index = indexOfKey(key);
        if(index != -1) removeElement(index);
    }

    /**
     * remove an element from collection with specified index,
     * if index out of range, nothing is done.
     */
    public void removeElement(int index)
    {
        if(index < 0 || index >= _length) return;
        Object key = _keys[index];
        for(int i =index; i<_length - 1; i++)
        {
            _keys[i] = _keys[i + 1];
        }
        _keys[_length - 1] = null;
        _data.remove(key);
        _length--;
    }

    /**
     * augment capacity, when current capacity can not satisfy requirement
     */
    private void enlargeTo(int capacity)
    {
        _capacity = capacity;

        Object[] keys = new Object[capacity];
        Object key = null;

        int i = 0;
        for(i=0; i<_length; i++)
        {
            key = _keys[i];
            keys[i] = key;
        }
        _keys = keys;
    }

    /**
     * generate a unique key
     */
    private String getNewKey()
    {
        String guid = new Guid().toString();
        return guid;
    }

    /**
     * find index of a specified key
     *
     * @return -1 if not found
     */
    public int indexOfKey(Object key)
    {
        // make sure, this line may be redundant
        //if(_length == 0) return -1;

        int i = 0, length = _length;
        for(i=0; i<length; i++)
        {
            if(_keys[i].equals(key)) break;
        }

        return i != length ? i : -1;
    }

    /**
     * find key of a specified index
     *
     * @return null if not found
     */
    public Object keyOfIndex(int index)
    {
        if(index < 0 || index >= _length) return null;
        Object key = _keys[index];
        return key;
    }

    /**
     * add all elements in another collection into myself
     */
    public void addElements(Collection values)
    {
        int i = 0, length = values.length();
        Object key = null, value = null;
        for(i=0; i<length; i++)
        {
            key = values.keyOfIndex(i);
            value = values.getElement(key);
            addElement(key, value);
        }
    }

    /**
     * whether or not collection empty(length equal to 0)
     */
    public boolean isEmpty()
    {
        return _length == 0;
    }

    /**
     * whether a specified value is contained in collection
     */
    public boolean containElement(Object value)
    {
        boolean r = _data.containsValue(value);
        return r;
    }

    /**
     * clear all elements in collection
     */
    public void clear()
    {
        _data.clear();
        int i = 0, length = _length;
        for(i=0; i<length; i++)
        {
            _keys[i] = null;
        }
        _length = 0;
    }

    /**
     * clear
     */
    public void destroy()
    {
        clear();
    }

    /**
     * clone
     */
    public Object clone()
    {
        Collection collection = new Collection(this._length);
        for(int i=0; i<this._length; i++)
        {
            collection.addElement(this._keys[i], this._data.get(this._keys[i]));
        }
        return collection;
    }

    /**
     * clone
     *
     * @return collection
     */
    public Collection copy()
    {
        return (Collection)clone();
    }

    /**
     * current capacity
     */
    public int getCapacity()
    {
        return _capacity;
    }

    /**
     * get an element information
     * @return element information
     */
    private String elementInfo(int index)
    {
        String key = null, value = null;
        try
        {
            key = (String)keyOfIndex(index);
        }
        catch(Exception e)
        {
            key = keyOfIndex(index).toString();
        }

        try
        {
            value = (String)getElement(index);
        }
        catch(Exception e)
        {
            value = getElement(index).toString();
        }

        String str = "{ ";
        str += "index  = " + index
            + ";\tkey   = " + key
            + ";\tvalue = " + value;
        str += " }";
        return str;
    }

    /**
     * get a string represents collection object,
     * this string will contain collection capacity,
     * collection length, values in collection
     *
     * @return a String that description Collection status
     */
    public String toString()
    {
        String str = "Collection object:\n";
        str       += "\tCapacity : " + getCapacity() + "\n";
        str       += "\tLength   : " + length() + "\n";
        str       += "\tValues   : ";

        if(_length == 0) str += "empty!";
        str += "\n";

        int i = 0, length = length();
        for(i=0; i<length; i++)
        {
            str += "\t\t" + elementInfo(i) + "\n";
        }


        return str;
    }

    public boolean containsKey(String key) {
        return this._data.containsKey(key);
    }

    /**
     * test and show the usage of Collection class
     */
    public static void main(String[] args)
    {
        Collection collection = new Collection(5);

        collection.addElement("manual-key-0", "manual-added-0");

        int i = 0, length = 10;
        for(i=0; i<length; i++)
        {
            collection.addElement(String.valueOf(i) + "00000");
        }

        collection.removeElement(6);
        collection.removeElement(10);
        collection.addElement("manual-key-0", "manual added-1");

        collection.removeElement("not-exist-key");
        collection.removeElement(null);
    }

}