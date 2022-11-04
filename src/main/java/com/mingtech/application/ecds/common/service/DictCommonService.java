package com.mingtech.application.ecds.common.service;

import java.util.List;
import java.util.Map;
import com.mingtech.framework.core.page.Page;


public interface DictCommonService{
	/**
	 * <p>方法名称: queryDictList|描述:根据业务主键ID查询该业务所有字典表信息对象集合 </p>
	 * @param tableName 
	 * @return
	 */
	public List queryDictList(String tableName);
	/**
	 * <p>方法名称: queryDictEnumerateList|描述:根据业务主键ID查询枚举信息对象集合 </p>
	 * @param id 业务主键ID
	 * @return
	 */
	public List queryDictEnumerateList(String id);
	/**
	 * <p>方法名称: verifyQuerySql|描述:根据传输过来的条件校验sql</p>
	 * @param tableCondtion 查询表名
	 *  @param verifySql 高级查询条件
	 * @return
	 */
	public void verifyQuerySql(String tableCondtion,String verifySql)throws Exception;
	/**
	 * <p>方法名称: queryDataToExcel|描述:根据传输过来的条件组合Hql语句查询出数据导出对应的excel二进制流</p>
	 * @param tableCondtion 查询表名
	 * @param ColumnNames 查询字段的字符串
	 * @param exportIds 高级查询过滤的条件
	 * @param queryHQL 显示列表基础查询语句
	 * @param pg 分页控制查询导出数据
	 * @return byte[]二进制流
	 */
	public byte[] queryDataToExcel(String tableCondtion,String ColumnNames,String exportIds,String queryHQL,Page pg)throws Exception;
	
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
	public byte[] queryDataToExcel(String tableCondtion,String ColumnNames,String exportIds,String queryHQL,List parasName,List parasValue,Page pg)throws Exception;
	
	/**
	 * <p>将符合条件的查询转换成对应的数据流</p>
	 * @param list 查询数据集合
	 * @param ColumnNames 字段集合的字符串
	 * @param mapinfo 相关字段中英键值对
	 * @param mapfileds 相关字段编码对应中文名称
	 * @return byte[]二进制流
	 */
	public byte[] creatSheetModel(List list, String ColumnNames, Map mapinfo,Map mapfileds, int[] Num, String[] NameType) throws Exception ;
	public byte[] creatSheetModel(List<Object> list, String[] titles,Map<String, String> mapfileds, int[] Num, String[] NameType) throws Exception;	
	/**
	 * <p>方法名称: query2DataToExcel|描述:根据传输过来的条件组合Hql语句查询出数据导出对应的excel二进制流</p>
	 * @param tableCondtion 查询表名
	 * @param ColumnNames 查询字段的字符串
	 * @param exportIds 高级查询过滤的条件
	 * @param queryHQL 显示列表基础查询语句
	 * @param pg 分页控制查询导出数据
	 * @return byte[]二进制流
	 */
	public byte[] query2DataToExcel(String tableCondtion, String ColumnNames,
			String exportIds, String queryHQL, Page pg, int[] Num, String[] NameType) throws Exception;
}

