package com.mingtech.framework.common.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Excel的工具类
 * @author Administrator
 *
 */
public class ExcelUtil {
	
	/**
	 * 字符或字符串
	 */
	public static String TYPE_STRING = "4";
	
	/**
	 * 日期
	 */
	public static String TYPE_DATE = "3";
	
	/**
	 * 枚举
	 */
	public static String TYPE_EMUN = "2";
	
	/**
	 * 数字
	 */
	public static String TYPE_NUMBER = "1";
	
	/**
	 * 将查询结果导出为Excel
	 * @param list
	 * @param heads
	 * @return
	 * @throws Exception
	 */
	public static byte[] creatSheet(List list,Map heads,Map codeMap) throws Exception {
		ByteArrayOutputStream os = null;
		try{
			os = new ByteArrayOutputStream();
			WritableWorkbook rwb = Workbook.createWorkbook(os);
			WritableSheet sheet=rwb.createSheet("sheet",0);  
			//将定义好的单元格添加到工作表中
			int headNum =0;
			Object[] agrsColum = null;
			if(heads != null && heads.size() > 0){
				agrsColum = heads.keySet().toArray();
				//表头样式
				WritableFont fontHead = 
					  new  WritableFont(WritableFont.TIMES, 10 ,WritableFont.BOLD);
					 WritableCellFormat formatHead = new  WritableCellFormat(fontHead); 
				// 把水平对齐方式指定为居中 
				formatHead.setAlignment(jxl.format.Alignment.CENTRE);
				// 把垂直对齐方式指定为居中 
				formatHead.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				headNum = agrsColum.length;
				for(int n=0;n<headNum;n++){
					String headDef[] = (String[])heads.get(agrsColum[n].toString());
					String headName = headDef[0];
					sheet.addCell(new Label(n,0,headName,formatHead));  
					sheet.setColumnView( n , 20 );
				}		
				//数据
				int dataNum = list.size();
				for(int m=0;m<dataNum;m++){
					WritableCellFormat formatData = new  WritableCellFormat();
					formatData.setAlignment(jxl.format.Alignment.CENTRE);
					formatData.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				    if(headNum==1){
				    	Object obj = (Object)list.get(m);
						sheet.addCell(new Label(0,m+1,obj.toString(),formatData));
					}else{
					    Object[] obj = (Object[])list.get(m);
						int colNum = obj.length;
						if(colNum==0){colNum=headNum;}
						for(int i=0; i<colNum; i++){
							String nameLabel2=" ";
							if(obj[i]!=null){
								//nameLabel2 =obj[i].toString();
								String headDef[] = (String[])heads.get(agrsColum[i].toString());
								Object value = obj[i];
								String headKey = agrsColum[i].toString();
								String type = headDef[1];
								nameLabel2 = formatData(value,type,headKey,codeMap);
							}
							
							sheet.addCell(new Label(i,m+1,nameLabel2,formatData));
						}
					}
				}
			}else{
				throw new Exception("不存在导出的字段列!");
			}
			rwb.write();
			rwb.close();
			return os.toByteArray();	
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("数据转换成excel数据流出错："+ex.getMessage());
		}
	}
	
	/**
	 * 转化数据成字符串
	 * @param value
	 * @param type
	 * @return
	 */
	public static String formatData(Object value,String type,String headKey,Map codeMap){
		
		String rs = "";
		
		if(TYPE_DATE.equals(type)){
			//日期类型
			Date d = (Date)value;
			rs = DateUtils.toString(d, DateUtils.ORA_DATES_FORMAT);
		}else if(TYPE_EMUN.equals(type)){
			//枚举类型
			Map cm = (Map)codeMap.get(headKey);
			if(cm == null){
				rs = value.toString();
			}else{
				rs = (String)cm.get(value);
			}
			
			
		}else{
			rs = value.toString();
		}
		
		return rs;
	}
	
	/**
	 * 将Javabean转化成数组
	 * @param beans
	 * @return
	 * @throws Exception
	 */
	public static List convertBeanToArray(List beans,Map heads) throws Exception{
		
		List result = new ArrayList();
		
		Object columnKeys[] = heads.keySet().toArray();
		
		for(int b = 0; b < beans.size();b++){
			Object bean = beans.get(b);
			
			Object values [] = new Object[columnKeys.length];
			
			for(int i = 0; i < columnKeys.length;i++){
				String propertyName = (String)columnKeys[i];
				values[i] = BeanUtil.getValue(propertyName, bean);
			}
			
			result.add(values);
			
		}
		return result;
	}

}
