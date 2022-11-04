/************************************************************

  Copyright (C), 1996-2008, mingtech Tech. Co., Ltd.
  FileName: CollectionUtil.java
  Author: hubo       
  Version: 1.0     
  Date:2008-7-15 下午01:27:17
  Description: 集合工具类    

  Function List:   
    1. -------

  History:         
      <author>    <time>   <version >   <desc>


 ***********************************************************/

package com.mingtech.framework.common.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author huboA
 * @since 2008-7-15
 * @version 1.0
 */
public class CollectionUtil extends CollectionUtils {
	/**
	 * 判断一个集合是否为NULL
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isNull(Collection collection) {
		return collection == null ? true : false;
	}

	/**
	 * 判断一个集合是否不为NULL
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isNotNull(Collection collection) {
		return !isNull(collection);
	}

	/**
	 * 判断一个集合是否为空
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection collection) {
		if (isNull(collection)) {
			return true;
		}
		if (collection.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断一个集合是否不为空
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isNotEmpty(Collection collection) {
		return !isEmpty(collection);
	}
	
    public static <T> void sort(List<T> list, Comparator<? super T> c) {
	Object[] a = list.toArray();
	sort(a, (Comparator)c);
	ListIterator i = list.listIterator();
	for (int j=0; j<a.length; j++) {
	    i.next();
	    i.set(a[j]);
	}
    }
	
    private static <T> void sort(T[] a, Comparator<? super T> c) {
    	T[] aux = (T[])a.clone();
        resourceSort(aux, a, 0, a.length, 0, c);
    }
    
	private static void resourceSort(Object[] src,
			  Object[] dest,
			  int low, int high, int off,
			  Comparator c) {
	int length = high - low;
	
	// Insertion sort on smallest arrays
	if (length < 7) {
	  for (int i=low; i<high; i++)
		for (int j=i; j>low && c.compare(dest[j-1], dest[j])>0; j--)
		    swap(dest, j, j-1);
	  return;
	}
	
	  // Recursively sort halves of dest into src
	  int destLow  = low;
	  int destHigh = high;
	  low  += off;
	  high += off;
	  int mid = (low + high) >>> 1;
	  resourceSort(dest, src, low, mid, -off, c);
	  resourceSort(dest, src, mid, high, -off, c);
	
	  // If list is already sorted, just copy from src to dest.  This is an
	  // optimization that results in faster sorts for nearly ordered lists.
	  if (c.compare(src[mid-1], src[mid]) <= 0) {
	     System.arraycopy(src, low, dest, destLow, length);
	     return;
	  }
	
	  // Merge sorted halves (now in src) into dest
	  for(int i = destLow, p = low, q = mid; i < destHigh; i++) {
	      if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0)
	          dest[i] = src[p++];
	      else
	          dest[i] = src[q++];
	  }
	}
    private static void swap(Object[] x, int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
    }
	
}
