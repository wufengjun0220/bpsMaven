package com.mingtech.application.pool.enginee.factor;

/**
 * <p>数字集合</p>
 * @author Albert Li
 * @date 2017年11月3日
 * @version 1.0
 * <p>修改记录</p>
 * Albert Li   新建类    2017年11月3日
 */
public class IntegerSet {
	public IntegerSet(int num) {
		this.num = num;
	}
	
	/**
	 * 是否在某个集合中
	 * */
	public boolean memberOf(String strArr) {
		String[] arr = strArr.split(",");
		for(String str : arr) {
			try {
				int y = Integer.parseInt(str);
				if(num == y) {
					return true;
				}
			} catch(Exception e) {}
		}
		return false;
	}
	
	private int num;
}
