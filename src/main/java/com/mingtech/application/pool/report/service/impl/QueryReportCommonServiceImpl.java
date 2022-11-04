package com.mingtech.application.pool.report.service.impl;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.mingtech.application.pool.report.domain.RReportModel;
import com.mingtech.application.pool.report.domain.ReportFile;
import com.mingtech.application.pool.report.domain.ReportModelAmtBean;
import com.mingtech.application.pool.report.service.QueryReportCommonService;
import com.mingtech.application.pool.report.service.ReportModelService;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * 
 * @作者: h2
 * @描述: [QueryReportCommonServiceImpl]通用报表查询Service实现类
 */
@Service("queryReportCommonService")
public class QueryReportCommonServiceImpl extends GenericServiceImpl implements QueryReportCommonService {
	Logger logger = Logger.getLogger(QueryReportCommonServiceImpl.class);
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private ReportModelService reportModelService;
	/**
	 * 报表生成
	 * @param file 报表生成对象
	 * @throws Exception 
	 */
	public void txCreateReportFile(ReportFile file)throws Exception{
		RReportModel model = (RReportModel) this.load(file.getTemplateId(),RReportModel.class);
		String modelPath = model.getFilePath()+File.separator+file.getReportName();
		String filePath =  model.getFilePath()+File.separator+file.getFileName();//文件名称
		ExcelWriter excelWriter = EasyExcel.write(filePath).withTemplate(modelPath).build();
		EasyExcel.writerSheet().build();
		// 千万别忘记关闭流
		excelWriter.finish();
		//数据封装到报表中
		List result = reportModelService.findBusiList(file);
		List<Map> list  = reportModelService.findReportHeads(model.getBusiType());
		// 根据用户传入字段  现实字段
		Set<String> includeColumnFiledNames = new HashSet<String>();
		Map<String,String> map = list.get(1);
		for(String key:map.keySet()){//keySet获取map集合key的集合  然后在遍历key即可
			includeColumnFiledNames.add(key);
		}
		EasyExcel.write(filePath,ReportModelAmtBean.class).withTemplate(modelPath).sheet().includeColumnFiledNames(includeColumnFiledNames)
				.needHead(false).doWrite(result);
		//删除报表中的两条案例数据
		this.removeSheet(filePath);
		file.setFinishTime(new Date());//完成时间
		file.setStatus(PublicStaticDefineTab.Report_STATUS_1);
		this.txStore(file);
	}
	public void removeSheet(String path){
		try {
			FileInputStream is = new FileInputStream(path);
			//将文件的输入流转换成Workbook
			Workbook wb = WorkbookFactory.create(is);
			//获得第一个工作表
			Sheet sheet = wb.getSheetAt(0);
            int lastRowNum=sheet.getLastRowNum();
            //把表格数据重新组装，从第三行以后的数据重新排版到第一行，以此类推往下
			for (int i=1;i<=lastRowNum-2;i++){
				//获得行
				Row row = sheet.getRow(i);
				int cellNum =row.getFirstCellNum();
				for (int j=cellNum;j<row.getLastCellNum();j++){
					//获得列
					Cell cell =  row.getCell(j);
					if(null!=cell){
						Double value=sheet.getRow(i+2).getCell(j).getNumericCellValue();
						cell.setCellValue(value);
					}
				}
			}
			//清空最后两行数据
			Row row1 = sheet.getRow(lastRowNum-1);
			Row row2 = sheet.getRow(lastRowNum);
			sheet.removeRow(row1);
			sheet.removeRow(row2);

			// 刷新公式
			wb.setForceFormulaRecalculation(true);
			//使用evaluateFormulaCell对函数单元格进行强行更新计算
			wb.getCreationHelper().createFormulaEvaluator().evaluateAll();

			FileOutputStream os = new FileOutputStream(path);
			wb.write(os);
			is.close();
			os.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 更新报表生成文件状态
	 * @param reportFile 报表生成对象
	 * @parm status 更新状态
	 * @throws Exception 
	 */
	public void txUpdateReportFileStatus(ReportFile reportFile,String status)throws Exception{
		ReportFile oldReportFile =(ReportFile) dao.load(ReportFile.class, reportFile.getId());
		oldReportFile.setStatus(status);
		this.txStore(oldReportFile);
	}
	
	/**
	 * 报表生成结果查询
	 * @param user 当前用户
	 * @author reportFile 查询条件
	 * @param page 分页对象
	 * @throws Exception
	 * List 报表模板
	 */
	public List queryCreateReportResult(User user,RReportModel reportFile,Page page)throws Exception{
		StringBuffer hql=new StringBuffer("from RReportModel  where 1=1 ");
		List parasName = new ArrayList();
		List parasValue = new ArrayList();
		return this.find(hql.toString(), (String[]) parasName.toArray(new String[parasName.size()]), parasValue.toArray(),page);
	}
	
	/**
	 * 根据id查询报表生成文件信息
	 * @param id 报表生成文件主键
	 * @throws Exception
	 * @return ReportFile 报表生成文件
	 */
	public RReportModel getReportFileById(String id)throws Exception{
		return (RReportModel) dao.load(RReportModel.class, id);
	}
	
	

	/**
	 * 根据名称获取对象get值
	 * @param ob
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Object getGetMethod(Object ob , String name)throws Exception{
		Method[] m = ob.getClass().getMethods();
		for(int i = 0;i < m.length;i++){
			if(("get"+name).toLowerCase().equals(m[i].getName().toLowerCase())){
				return m[i].invoke(ob);
			}
		}
		return null;
	}
	
	@Override
	public String getEntityName() {
		return StringUtil.getClass(RReportModel.class);
	}

	@Override
	public Class getEntityClass() {
		return RReportModel.class;
	}
	
	
}