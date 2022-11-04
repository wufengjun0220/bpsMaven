package com.mingtech.application.pool.bank.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用来描述一条报文,主报文放在一个Map中，键为域名称，值为经过类型转换后的域值。
 * 明细报文放在一个List中，list的每一个元素是表示一条明细，键为域名称，值为经过转换后的域值。
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
* @作者: gaofubing
* @日期: Jul 14, 2009 10:54:53 AM
* @描述: [ReturnMessage]请在此简要描述类的功能
 */
public class ReturnMessage extends AbstractMessage {
	
	private static final long serialVersionUID = -940833727168119141L;
	
	/**
	 * 交易号，即时报文中有交易码
	 */
	public static final String TX_CODE = "TXCODE";
	
	/**
	 * 响应码
	 */
	public static final String RESPONSE_CODE = "RESPONSECODE";
	
	
	/**
	 * 响应的处理结果--文字描述
	 */
	public static final String RESPONSE_RMK = "RESPONSERMK";
	
	/**
	 * 交易是否成功，本子段作为服务器端的处理结果，由应用层输入，而且必填
	 */
	private boolean txSuccess;
	

    private Map head = new HashMap();          //主报文内容Map
    private List details = new ArrayList();    //明细列表，每个明细是一个Map
    

    public ReturnMessage() {
    	
    }

	public Map getHead() {
		return head;
	}

	public void setHead(Map head) {
		this.head.putAll(head);
	}

	public List getDetails() {
		return details;
	}

	public void setDetails(List details) {
		this.details.addAll(details);
	}

	public String getCode() {
		return (String)head.get(TX_CODE);
	}

	public void setCode(String code) {
		head.put(TX_CODE, code);
	}

	public String getResponseCode() {
		return (String)head.get(RESPONSE_CODE);
	}

	public void setResponseCode(String responseCode) {
		head.put(RESPONSE_CODE, responseCode);
	}
	
	public String getResponseRmk(){
		return (String)head.get(RESPONSE_RMK);
	}
	
	public void setResponseRmk(String responseRmk){
		head.put(RESPONSE_RMK, responseRmk);
	}
	
	/**
	* <p>方法名称: putHead|描述: 放入报文头的内容</p>
	* @param key
	* @param value
	 */
	public void putHead(String key,String value){
		this.head.put(key, value);
	}
	
	/**
	* <p>方法名称: addList|描述: 放入明细数据</p>
	* @param alist
	 */
	public void addList(Object alist){
		this.details.add(alist);
	}

	public boolean isTxSuccess() {
		return txSuccess;
	}

	public void setTxSuccess(boolean txSuccess) {
		this.txSuccess = txSuccess;
	}
	
	public String toString(){
		String msg = "Head:" + this.head + "\n" + "detail:" + this.details;
		return msg;
	}
}
