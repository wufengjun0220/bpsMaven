/************************************************************

  Copyright (C), 1996-2008, mingtech Tech. Co., Ltd.
  FileName: CharacterEncoding.java
  Author: hubo      
  Version: 1.0     
  Date:2008-6-25 上午10:03:23
  Description: 统一编码常量接口   

  Function List:   
    1. -------

  History:         
      <author>    <time>   <version >   <desc>


 ***********************************************************/

package com.mingtech.framework.common.util;

/**
 * @author huboa
 * @since 2008-6-25
 * @version
 */
public interface CharacterEncoding {
	public static final String UTF8 = "UTF-8";
	public static final String GBK = "GBK";
	public static final String GB2312 = "GB2312";
	public static final String ISO_8859_1 = "ISO-8859-1";

	public static final String TEXT_HTML_UTF8 = "text/html;charset=" + UTF8;
}
