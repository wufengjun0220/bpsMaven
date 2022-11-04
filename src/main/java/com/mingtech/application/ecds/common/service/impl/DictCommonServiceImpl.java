package com.mingtech.application.ecds.common.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.ecds.common.query.domain.FieldCodeMapDto;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;



public class DictCommonServiceImpl extends GenericServiceImpl implements DictCommonService{
	private static Logger logger = Logger.getLogger(DictCommonServiceImpl.class);
	/**
	 * <p>根据表名查询该表字段对应的信息信息对象集合 </p>
	 * @param tableName 
	 * @return
	 */
	public List queryDictList(String tableName){
		List param = new ArrayList();
		String hql="select FieldCodeMap from FieldCodeMapDto as FieldCodeMap where FieldCodeMap.tableName = ? order by FieldCodeMap.fieldOrder";
		param.add(tableName);
		return this.find(hql,param);
	}
	/**
	 *<p>根据业务主键ID查询枚举信息对象集合</p>
	 * @param codeKey 枚举型关联字段
	 */
	public List queryDictEnumerateList(String codeKey){
		List param = new ArrayList();
		String hql="select code from CodeDto as code where code.codeKey = ? ";
		param.add(codeKey);
		return this.find(hql,param);
	}
	/**
	 *<p>高级查询中校验sql</p>
	 * @param tableCondtion 查询表名
	 * @param verifySql 高级查询条件
	 */
	public void verifyQuerySql(String tableCondtion,String verifySql)throws Exception{
		try{
			String sql = " from "+tableCondtion+"  where 1 <> 1 "+verifySql;
			logger.info("最终组合校验的sql语句：："+sql);
			this.find(sql);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw new Exception(e.getMessage());	
		}
	}
	/**
	 * <p>组合hql语句查询出对应的数据并且返回excel二进制流</p>
	 * @param tableCondtion 查询表名
	 * @param ColumnNames 查询字段的字符串
	 * @param exportIds 高级查询过滤的条件
	 * @param queryHQL 显示列表基础查询语句
	 * @param pg 分页控制查询导出数据
	 * @return byte[]二进制流
	 */
	public byte[] queryDataToExcel(String tableCondtion, String ColumnNames,
			String exportIds,String queryHQL,Page pg) throws Exception{
		Map maps = getFileCodeMap(tableCondtion);
		Map mapinfo = (HashMap)maps.get("MapCode1");
		Map mapfileds = (HashMap)maps.get("MapCode2");
		Iterator iter = mapinfo.keySet().iterator();
		StringBuffer sb = new StringBuffer("");
		while(iter.hasNext()){
			String objKey = (String)iter.next();
			sb.append(objKey+",");
		}
		if(ColumnNames == null||"".equals(ColumnNames.trim())){
			if(!"".equals(sb.toString())){
				ColumnNames = sb.substring(0, sb.lastIndexOf(","));
			}
		}
		String strHql = " ";
		if(ColumnNames != null && !"".equals(ColumnNames.trim())
				&& tableCondtion != null && !"".equals(tableCondtion.trim())
				){
			strHql = "select " +ColumnNames+ " from "+tableCondtion+" where 1=1 "+queryHQL+" "+exportIds;
		}else{
			throw new Exception("批量导出sql语句出错::"+strHql);
		}
		
		logger.info("批量导出列表的字段的HQL:"+strHql);
		List list=this.find(strHql,new ArrayList(),pg);
		logger.info("批量导出数据的大小为："+list.size());
		//转换成excel
		return creatSheet(list,ColumnNames,mapinfo,mapfileds,tableCondtion);
	}
	
	/**
	 * <p>
	 * 组合hql语句查询出对应的数据并且返回excel二进制流
	 * </p>
	 * 
	 * @param tableCondtion
	 *            查询表名
	 * @param ColumnNames
	 *            查询字段的字符串
	 * @param exportIds
	 *            高级查询过滤的条件
	 * @param queryHQL
	 *            显示列表基础查询语句
	 * @param pg
	 *            分页控制查询导出数据
	 * @return byte[]二进制流
	 */
	public byte[] query2DataToExcel(String tableCondtion, String ColumnNames,
			String exportIds, String queryHQL, Page pg, int[] Num, String[] NameType) throws Exception {
		Map maps = getFileCodeMap(tableCondtion);
		Map mapinfo = (HashMap) maps.get("MapCode1");
		Map mapfileds = (HashMap) maps.get("MapCode2");
		Iterator iter = mapinfo.keySet().iterator();
		StringBuffer sb = new StringBuffer("");
		while (iter.hasNext()) {
			String objKey = (String) iter.next();
			if("FBuyPayment".equals(objKey.trim())){
				objKey = "(FBillAmount-FInt) AS FBillAmount";
			}
			sb.append(objKey + ",");
		}
		if(mapinfo.containsKey("FBuyPayment")){
			mapinfo.remove("FBuyPayment");
			mapinfo.put("(FBillAmount-FInt) AS FBillAmount", "实付");
		}
		if (ColumnNames == null || "".equals(ColumnNames.trim())) {
			if (!"".equals(sb.toString())) {
				ColumnNames = sb.substring(0, sb.lastIndexOf(","));
			}
		}
		String strHql = " ";
		if (ColumnNames != null && !"".equals(ColumnNames.trim())
				&& tableCondtion != null && !"".equals(tableCondtion.trim())) {
			strHql = "select " + ColumnNames + " from " + tableCondtion
					+ " where 1=1 " + queryHQL + " " + exportIds;
		} else {
			throw new Exception("批量导出sql语句出错::" + strHql);
		}

		logger.info("批量导出列表的字段的HQL:" + strHql);
		List list = this.find(strHql, new ArrayList(), pg);
   		Iterator it = list.iterator();
		Object[] objs=new Object[50];
		while(it.hasNext()){
			objs = (Object[]) it.next();
			objs[8] = DictionaryCache.getBillMedia((String)objs[8]);
			objs[7] = DictionaryCache.getBillType((String)objs[7]);
		}
		logger.info("批量导出数据的大小为：" + list.size());
		// 转换成excel
//		return creatSheet(list, ColumnNames, mapinfo, mapfileds, tableCondtion);
		return creatSheetModel(list, ColumnNames, mapinfo, mapfileds, Num, NameType);
	}
	
	/**
	 * <p>方法名称: queryDataToExcel|描述:根据传输过来的条件组合Hql语句查询出数据导出对应的excel二进制流</p>
	 * @param tableCondtion 查询表名
	 * @param ColumnNames 查询字段的字符串
	 * @param exportIds 高级查询过滤的条件
	 * @param queryHQL 显示列表基础查询语句
	 * @param parasName 查询条件属性名称
	 * @param parasValue 查询条件值
	 * @param pg 分页控制查询导出数据
	 * @return byte[]二进制流
	 */
	public byte[] queryDataToExcel(String tableCondtion,String ColumnNames,String exportIds,String queryHQL,List parasName,List parasValue,Page pg)throws Exception{
		Map maps = getFileCodeMap(tableCondtion);
		Map mapinfo = (HashMap)maps.get("MapCode1");
		Map mapfileds = (HashMap)maps.get("MapCode2");
		Iterator iter = mapinfo.keySet().iterator();
		StringBuffer sb = new StringBuffer("");
		while(iter.hasNext()){
			String objKey = (String)iter.next();
			sb.append(objKey+",");
		}
		if(ColumnNames == null||"".equals(ColumnNames.trim())){
			if(!"".equals(sb.toString())){
				ColumnNames = sb.substring(0, sb.lastIndexOf(","));
			}
		}
		String strHql = " ";
		if(ColumnNames != null && !"".equals(ColumnNames.trim())
				&& tableCondtion != null && !"".equals(tableCondtion.trim())
				){
			strHql = "select " +ColumnNames+ " from "+tableCondtion+" dto where 1=1 "+queryHQL+" "+exportIds;
		}else{
			throw new Exception("批量导出sql语句出错::"+strHql);
		}
		
		logger.info("批量导出列表的字段的HQL:"+strHql);
		List list = this.find(strHql,(String[])parasName.toArray(new String[parasName.size()]),parasValue.toArray(),pg);
		logger.info("批量导出数据的大小为："+list.size());
		//转换成excel
		return creatSheet(list,ColumnNames,mapinfo,mapfileds,tableCondtion);
	}
	
	
	/**
	 * <p>将符合条件的查询转换成对应的数据流</p>
	 * @param list 查询数据集合
	 * @param ColumnNames 字段集合的字符串
	 * @param mapinfo 相关字段中英键值对
	 * @return byte[]二进制流
	 */
	public byte[] creatSheet(List list,String ColumnNames,Map mapinfo,Map mapfileds,String tableName) throws Exception {
		ByteArrayOutputStream os = null;
		try{
			os = new ByteArrayOutputStream();
			WritableWorkbook rwb = Workbook.createWorkbook(os);
			WritableSheet sheet=rwb.createSheet("sheet",0);  
			//将定义好的单元格添加到工作表中
			int headNum =0;
			if(ColumnNames != null && !"".equals(ColumnNames.trim())){
				String[] agrsColum = ColumnNames.split(",");
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
					String nameLabel = (String)mapinfo.get(agrsColum[n]);
					sheet.addCell(new Label(n,0,nameLabel,formatHead));  
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
				    	
				    	String nameLabel2 = obj==null?"":obj.toString();
				    	String key = (tableName+"_"+agrsColum[0]+"_"+nameLabel2).trim();
				    	
				    	if(!mapfileds.isEmpty()&&null!=mapfileds.get(key)){
							nameLabel2 =(String)mapfileds.get(key);
						}
						sheet.addCell(new Label(0,m+1,nameLabel2,formatData));
					}else{
					    Object[] obj = (Object[])list.get(m);
						int colNum = obj.length;
						if(colNum==0){colNum=headNum;}
						for(int i=0; i<colNum; i++){
							String nameLabel2=" ";
							if(obj[i]!=null){
								nameLabel2 =obj[i].toString();
							}
							String Colum = (String)mapinfo.get(agrsColum[i]);
							String key = (tableName+"_"+agrsColum[i]+"_"+nameLabel2).trim();
							if(!mapfileds.isEmpty()&&null!=mapfileds.get(key)){
								nameLabel2 =(String)mapfileds.get(key);
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
			throw new Exception("数据转换成excel数据流出错："+ex.getMessage());
		}
	}
	/**
	 * <p>获取字段对应键值对</p>
	 * @param tableName
	 * @return
	 */
	
	public Map getFileCodeMap(String tableName){
		List param = new ArrayList();
		String hql="select FieldCodeMap from FieldCodeMapDto as FieldCodeMap where FieldCodeMap.tableName = ? order by FieldCodeMap.fieldOrder";
		param.add(tableName);
		List list = this.find(hql,param);
		Map mapinfo = new LinkedHashMap();
		for(int m = 0 ; m < list.size() ;m++){
			FieldCodeMapDto dto = (FieldCodeMapDto)list.get(m);
			mapinfo.put(dto.getFieldName(),dto.getFieldCnName());
			String fName = dto.getFieldName();
		}
		

		HashMap mapCode = new HashMap();
		String hqlCode = "select codeMap.tableName,codeMap.fieldName,code.codeNo,code.codeNoName " 
				+ " from CodeDto as code,FieldCodeMapDto as codeMap "
				+ " where code.codeKey = codeMap.relateData "
				+ " and codeMap.tableName ='" + tableName + "'";
		List listCode = this.find(hqlCode);
		for(int n = 0 ; n < listCode.size() ;n++){
			Object[] dto = (Object[])listCode.get(n);
			String table_Name = (String)dto[0];
			String field_Name = (String)dto[1];
			String code_No = (String)dto[2];
			String code_NoName = (String)dto[3];
			String key =table_Name+"_"+field_Name+"_"+code_No;
			mapCode.put(key, code_NoName);
		}
		Map reMap = new HashMap();
		reMap.put("MapCode1",mapinfo);
		reMap.put("MapCode2",mapCode);
		return reMap;
	}
	/**
	 * <p>
	 * 将符合条件的查询转换成对应的数据流
	 * </p>
	 * 
	 * @param list
	 *            查询数据集合
	 * @param ColumnNames
	 *            字段名称字符串，以“,”隔开，示例："discountInName,discountInAccount,SBillNo"
	 * @param mapinfo
	 *            相关字段中英键值对,示例：{discountInName=贴现支行, discountInAccount=贴现帐号,
	 *            SBillNo=票号}
	 * @param mapfileds
	 *            相关字段编码显示为对应中文名称,示例：{1=纸质, AC01=银票}
	 *            
	 *            , int[] Num, String[] NameType      
	 * 
	 * @param int[] Num  指定需要转换格式的位置
	 * @return byte[]二进制流         
	 */
	public byte[] creatSheetModel(List list, String ColumnNames, Map mapinfo,Map mapfileds, int[] Num, String[] NameType) throws Exception {
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			jxl.write.WritableWorkbook rwb = Workbook.createWorkbook(os);
			jxl.write.WritableSheet sheet = rwb.createSheet("sheet", 0);
			// 将定义好的单元格添加到工作表中
			int headNum = 0;
			if (ColumnNames != null && !"".equals(ColumnNames.trim())) {
				String[] agrsColum = ColumnNames.split(",");
				// 表头样式
				WritableFont fontHead = new WritableFont(WritableFont.TIMES,
						10, WritableFont.BOLD);
				WritableCellFormat formatHead = new WritableCellFormat(fontHead);
				// 把水平对齐方式指定为居中
				formatHead.setAlignment(jxl.format.Alignment.CENTRE);
				// 把垂直对齐方式指定为居中
				formatHead
						.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				formatHead.setBackground(jxl.format.Colour.BLUE_GREY);
				jxl.write.NumberFormat nf1 = new jxl.write.NumberFormat("#0.00%");         //单元格格式
				jxl.write.WritableCellFormat wf1 = new jxl.write.WritableCellFormat(nf1);
				jxl.write.NumberFormat nf2 = new jxl.write.NumberFormat("#0.00");
				jxl.write.WritableCellFormat wf2 = new jxl.write.WritableCellFormat(nf2);
				jxl.write.NumberFormat nf3 = new jxl.write.NumberFormat("##,##0.00");
				jxl.write.WritableCellFormat wf3 = new jxl.write.WritableCellFormat(nf3);
				jxl.write.DateFormat df = new jxl.write.DateFormat("yyyy-mm-dd;@");
				jxl.write.WritableCellFormat wf4 = new jxl.write.WritableCellFormat(df);
				
				
				headNum = agrsColum.length;
				for (int n = 0; n < headNum; n++) {
					String nameLabel = (String) mapinfo.get(agrsColum[n]);
					sheet.addCell(new Label(n, 0, nameLabel, formatHead));
					sheet.setColumnView(n, 20);
				}
				// 数据
				int dataNum = list.size();
				WritableCellFormat formatData = new WritableCellFormat();
				formatData.setAlignment(jxl.format.Alignment.CENTRE);
				formatData.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				for (int m = 0; m < dataNum; m++) {
					
					if (headNum == 1) {
						Object obj = (Object) list.get(m);
						sheet.addCell(new Label(0, m + 1, obj.toString(),
								formatData));
					} else {
						Object[] obj = (Object[]) list.get(m);
						int colNum = obj.length;
						if (colNum == 0) {
							colNum = headNum;
						}
						for (int i = 0; i < colNum; i++) {
							String nameLabel2 = "";
							if(obj[i]!=null){
								nameLabel2 = obj[i].toString();
							}else{
								obj[i] = "";
							}
							String key = (nameLabel2).trim();
							if (!mapfileds.isEmpty()
									&& null != mapfileds.get(key)) {
								nameLabel2 = (String) mapfileds.get(key);
							}
							if (obj[i].toString().indexOf("PTS_") > -1) {
								nameLabel2 = DictionaryCache
										.getStatusName(obj[i].toString());}
							String flag="0";
							for (int a = 0; a < Num.length; a++) {
								//判断数据是什么格式的，并设置相应的单元格格式
								if (i == Num[a]) {
									if(nameLabel2.equals("") || null == nameLabel2 ||"null".equals(nameLabel2)){
										nameLabel2 = "0";
									}
									if (NameType[a] == "rate") {   //利率格式
										jxl.write.Number tempName = new jxl.write.Number(i, m + 1,Double.parseDouble(nameLabel2),wf1);
										sheet.addCell(tempName);
										flag="1";
										break;
									}else if (NameType[a] == "frate") { //利率
											jxl.write.Number tempName = new jxl.write.Number(i, m + 1,(Double.parseDouble(nameLabel2)*100),wf2);
											sheet.addCell(tempName);
											flag="1";
											break;
									}else if (NameType[a] == "amount") {  //金额
										jxl.write.Number tempName = new jxl.write.Number(i, m + 1,Double.parseDouble(nameLabel2),wf3);
										sheet.addCell(tempName);
										flag="1";
										break;
									} else if (NameType[a] == "number") {  //数字
										jxl.write.Number tempName = new jxl.write.Number(i, m + 1,Double.parseDouble(nameLabel2));
										sheet.addCell(tempName);
										flag="1";
										break;
									}else if (NameType[a] == "date") {   //日期
										jxl.write.DateTime tempName = new jxl.write.DateTime(i, m + 1,DateUtils.stringToDateTime(nameLabel2),wf4);
										sheet.addCell(tempName);
										flag="1";
										break;
									}
								} 
							}
							if(flag.equals("0")){
								sheet.addCell(new Label(i, m + 1,nameLabel2, formatData));
							}
						}

					}
				}
			} else {
				throw new Exception("不存在导出的字段列!");
			}
			rwb.write();
			rwb.close();

			return os.toByteArray();
		} catch (Exception ex) {
			logger.error(ex, ex);
			throw new Exception("数据转换成excel数据流出错：" + ex.getMessage());
		}

	}
	
	
	/**
	 * <p>
	 * 将符合条件的查询转换成对应的数据流
	 * </p>
	 * 
	 * @param list
	 *            导出表格主体内容
	 * @param titles[]
	 *            导出表格头
	 * @param mapfileds
	 *            相关字段编码显示为对应中文名称,示例：{1=纸质, AC01=银票}
	 * 
	 * @param int[] Num  指定需要转换格式的位置
	 * 
	 * @param String[] NameType	需要转换的对应格式
	 * 
	 * @return byte[]二进制流         
	 */
	public byte[] creatSheetModel(List<Object> list, String[] titles,Map<String, String> mapfileds, int[] Num, String[] NameType) throws Exception {
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			jxl.write.WritableWorkbook rwb = Workbook.createWorkbook(os);
			jxl.write.WritableSheet sheet = rwb.createSheet("sheet", 0);
			// 将定义好的单元格添加到工作表中
			if (titles != null && titles.length != 0 ) {
				// 表头样式
				WritableFont fontHead = new WritableFont(WritableFont.TIMES,
						10, WritableFont.BOLD);
				WritableCellFormat formatHead = new WritableCellFormat(fontHead);
				// 把水平对齐方式指定为居中
				formatHead.setAlignment(jxl.format.Alignment.CENTRE);
				// 把垂直对齐方式指定为居中
				formatHead.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				formatHead.setBackground(jxl.format.Colour.BLUE_GREY);
				//单元格格式
				WritableCellFormat wf1 = new WritableCellFormat(new NumberFormat("#0.00%"));
				WritableCellFormat wf2 = new WritableCellFormat(new NumberFormat("#0.00"));
				WritableCellFormat wf3 = new WritableCellFormat(new NumberFormat("##,##0.00"));
				WritableCellFormat wf4 = new WritableCellFormat(new DateFormat("yyyy-mm-dd;@"));
				
				//	写入标题
				int headNum = titles.length;
				for (int n = 0; n < headNum; n++) {
					sheet.addCell(new Label(n, 0, titles[n], formatHead));
					sheet.setColumnView(n, 20);
				}
				
				// 数据
				int dataNum = list.size();
				WritableCellFormat formatData = new WritableCellFormat();
				formatData.setAlignment(Alignment.CENTRE);
				formatData.setVerticalAlignment(VerticalAlignment.CENTRE);
				
				for (int m = 0; m < dataNum; m++) {
					if (headNum == 1) {
						Object obj = (Object) list.get(m);
						sheet.addCell(new Label(0, m + 1, obj.toString(),formatData));
					} else {
						Object[] obj = (Object[]) list.get(m);
						int colNum = obj.length;
						if (colNum == 0) {
							colNum = headNum;
						}
						for (int i = 0; i < colNum; i++) {
							String nameLabel2 = "";
							if(obj[i]!=null){
								nameLabel2 = obj[i].toString().trim();
							}else{
								obj[i] = "";
							}
							//	特殊转换
							if (!mapfileds.isEmpty() && null != mapfileds.get(nameLabel2)) {
								nameLabel2 = (String) mapfileds.get(nameLabel2);
							}
							if (nameLabel2.indexOf("PTS_") > -1) {
								nameLabel2 = DictionaryCache.getStatusName(nameLabel2);
							}
							
							WritableCell tempName = null;
							
							if(Num.length > 0){
								for (int a = 0; a < Num.length; a++) {
									//判断数据是什么格式的，并设置相应的单元格格式
									if (i == Num[a]) {
										if(nameLabel2.equals("") || null == nameLabel2 ||"null".equals(nameLabel2)){
											nameLabel2 = "0";
											tempName = new Label(i, m + 1,nameLabel2, formatData);
											break;
										}
										if (NameType[a] == "rate") {   //利率格式
											tempName = new Number(i, m + 1,Double.parseDouble(nameLabel2),wf1);
											break;
										}else if (NameType[a] == "frate") { //利率
											tempName = new Number(i, m + 1,(Double.parseDouble(nameLabel2)),wf2);
											break;
										}else if (NameType[a] == "amount") {  //金额
											tempName = new Number(i, m + 1,Double.parseDouble(nameLabel2),wf3);
											break;
										} else if (NameType[a] == "number") {  //数字
											tempName = new Number(i, m + 1,Double.parseDouble(nameLabel2));
											break;
										}else if (NameType[a] == "date") {   //日期
											tempName = new DateTime(i, m + 1,DateUtils.stringToDateTime(nameLabel2),wf4);
											break;
										}
									} else{
										if(nameLabel2.equals("") || null == nameLabel2 ||"null".equals(nameLabel2)){
											nameLabel2 = "";
										}
										tempName = new Label(i, m + 1,nameLabel2, formatData);
										break;
									}
								}
							}else{
								tempName = new Label(i, m + 1,nameLabel2, formatData);
							}
							
							sheet.addCell(tempName);
						}
					}
				}
			} else {
				throw new Exception("不存在导出的字段列!");
			}
			rwb.write();
			rwb.close();

			return os.toByteArray();
		} catch (Exception ex) {
			logger.error(ex, ex);
			throw new Exception("数据转换成excel数据流出错：" + ex.getMessage());
		}
	}
	
	public byte[] creatSheetModel(List<Object> list, String[] titles) throws Exception {
		Map<String, String> mapfileds = new LinkedHashMap<String, String>();
		int[] Num = {};
		String[] NameType = {};
		
		return this.creatSheetModel(list, titles, mapfileds, Num, NameType);
	}
	
	public Class getEntityClass(){
		// TODO Auto-generated method stub
		return null;
	}
	public String getEntityName(){
		// TODO Auto-generated method stub
		return null;
	}




}
