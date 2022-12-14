package com.mingtech.application.pool.report.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.report.domain.RReportModel;
import com.mingtech.application.pool.report.domain.ReportFile;
import com.mingtech.application.pool.report.domain.ReportModelAmtBean;
import com.mingtech.application.pool.report.queue.ReportDispatchQueue;
import com.mingtech.application.pool.report.service.ReportModelService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ExcelUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

@Controller
public class ReportModelController extends BaseController {
	private static final Logger logger = Logger.getLogger(ReportModelController.class);
	@Autowired
	private ReportModelService reportModelService;
	@Autowired
	private ReportDispatchQueue reportDispatchQueue;
	/**
	 * <p>
	 * ????????????: list|??????:??????????????????
	 * </p>
	 */
	@RequestMapping("/queryReportModel")
	public void queryReportModel(RReportModel reportForm) {
		try {
			Page page = new Page();
			String json = reportModelService.queryReportModelJSON(reportForm, page,this.getCurrentUser());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
	}
	/**
	 * ???????????????????????????
	 * 
	 */
	@RequestMapping("/reportModelExpt")
	public void reportModelExpt(HttpServletResponse response, String busiType) {
		try {
			List<Map> list  = reportModelService.findReportHeads(busiType);//???????????????
			String reportName  = (String) list.get(0).get("reportName");//????????????
			List<ReportModelAmtBean> result =  reportModelService.findBusiModelList(busiType);//????????????
			response.setContentType("application/vnd.ms-excel");
			response.setCharacterEncoding("utf-8");
			String fileName = URLEncoder.encode(reportName, "UTF-8");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

			// ???????????????????????? ???????????????????????? date
			Set<String> includeColumnFiledNames = new HashSet<String>();
			Map<String,String> map = list.get(1);
			for(String key:map.keySet()){//keySet??????map??????key????????? ?????????????????key??????
				String value = map.get(key).toString();//
				includeColumnFiledNames.add(key);
			}
			// ?????? ????????????????????????class??????????????????????????????sheet?????????????????? ??????????????????????????????
			EasyExcel.write(response.getOutputStream(), ReportModelAmtBean.class).includeColumnFiledNames(includeColumnFiledNames).sheet(reportName)
					.doWrite(result);
		} catch (Exception e) {
			logger.error("??????????????????",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
	/**
	 * ???????????????????????????
	 *
	 */
	@RequestMapping(value="upLoadReportModel",method=RequestMethod.POST)
	public void upLoadReportModel(@RequestParam(value="file",required=false) CommonsMultipartFile file, String remark) {
		try {
			//????????????
			reportModelService.txUploadReportModel(file,this.getCurrentUser(),remark);
			logger.info("?????????????????????????????????"+this.getCurrentUser().getLoginName()+"??????");
			this.sendJSON("?????????????????????");
		} catch (Exception e) {
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("??????????????????"+e.getMessage());
		}
	}
	/**
	 * <p>
	 * ????????????: list|??????:????????????????????????
	 * </p>
	 */
	@RequestMapping("/queryReportFile")
	public void queryReportFile(ReportFile reportForm) {
		try {
			Page page = new Page();
			String json = reportModelService.queryReportFileJSON(reportForm, page,this.getCurrentUser());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
	}
	/**
	 * <p>??????: ???????????????????????? </p>
	 * @return void
	 */
	@RequestMapping("/queryCommonReport")
	public void queryCommonReport(String id,String timeModel,String timeSelect) {
		try {
			User user = this.getCurrentUser();
			ReportFile reportFile = reportModelService.txSaveCommonReportFile(user,id,timeModel,timeSelect);
			reportDispatchQueue.putRequest(reportFile);
			this.sendJSON("???????????????");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	/**
	 * <p>??????: ?????????????????????????????? </p>
	 * @return void
	 */
	@RequestMapping("/viewReportFileDetial")
	public void viewReportFileDetial(String id) {
		try {
			RReportModel reportFile = reportModelService.getReportFileById(id);
			String json =JsonUtil.fromObject(reportFile);
			if(!(StringUtils.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	/**
	 * <p>??????: ?????????????????? </p>
	 * @return void
	 */
	@RequestMapping("/delReportModelById")
	public void delReportModelById(String id){
		try{
			String rootPath = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DOCUMENT_ROOT_PATH);
			//??????ID?????????????????????
			RReportModel file = (RReportModel) reportModelService.load(id, RReportModel.class);
			if(!rootPath.endsWith(File.separator)){
				rootPath = rootPath  + File.separator;
			}
			String filePath =file.getFilePath(); //????????????-????????????
			String full = filePath+File.separator+file.getReportName();
			File files = new File(full);
			files.delete();
			reportModelService.txDelete(file);
			this.sendJSON("???????????????");
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	/**
	 * <p>??????: ?????????????????? </p>
	 * @return void
	 */
	@RequestMapping("/delReportFileById")
	public void delReportFileById(String id){
		try{
			//??????ID?????????????????????
			ReportFile file = (ReportFile) reportModelService.load(id, ReportFile.class);
			String filePath =file.getFilePath(); //????????????
			String full = filePath+File.separator+file.getFileName();
			File files = new File(full);
			files.delete();
			reportModelService.txDelete(file);
			this.sendJSON("???????????????");
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}

	/**
	 * ????????????????????????
	 * @return
	 */
	@RequestMapping(value="/toDownGeneratedReprotModel",method= RequestMethod.POST)
	public ResponseEntity<byte[]> toDownGeneratedReprotModel(String id){
		//????????????
		try {
			//??????ID?????????????????????
			RReportModel file = (RReportModel) reportModelService.load(id, RReportModel.class);
			String filePath =file.getFilePath(); //????????????
			String full = filePath+File.separator+file.getReportName();
			File files = new File(full);
			InputStream is = new FileInputStream(files);
			byte[] bytes = new byte[is.available()];
			is.read(bytes);
			//???????????????
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attchement;filename=" + file.getReportName());
			//??????HTTP???????????????
			org.springframework.http.HttpStatus statusCode = org.springframework.http.HttpStatus.OK;
			//????????????????????????
			ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(bytes, headers, statusCode);
			return entity;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	/**
	 * ??????????????????????????????
	 * @return
	 */
	@RequestMapping(value="/toDownGeneratedReprotFile",method= RequestMethod.POST)
	public ResponseEntity<byte[]> toDownGeneratedReprotFile(String id){
		//????????????
		try {
			//??????ID?????????????????????
			ReportFile file = (ReportFile) reportModelService.load(id, ReportFile.class);
			String filePath =file.getFilePath(); //????????????-
			String full = filePath+File.separator+file.getFileName();
			File files = new File(full);
			InputStream is = new FileInputStream(files);
			byte[] bytes = new byte[is.available()];
			is.read(bytes);
			//???????????????
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attchement;filename=" + file.getFileName());
			//??????HTTP???????????????
			org.springframework.http.HttpStatus statusCode = org.springframework.http.HttpStatus.OK;
			//????????????????????????
			ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(bytes, headers, statusCode);
			return entity;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	/**
	 * ??????JSON
	 * @param page
	 * @param list
	 * @return
	 * @throws Exception
	 */
	protected String toJson(Page page,List list) throws Exception{
		
		Map jsonMap = new HashMap();
		jsonMap.put("totalProperty", "results," + page.getTotalCount());
		jsonMap.put("root", "rows");
		String json= JsonUtil.fromCollections(list, jsonMap);
		if(!(StringUtil.isNotBlank(json))){
			json = RESULT_EMPTY_DEFAULT;
		}
		
		return json;
	}
}
