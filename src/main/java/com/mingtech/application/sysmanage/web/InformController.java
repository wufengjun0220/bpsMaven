package com.mingtech.application.sysmanage.web;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.druid.util.StringUtils;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.sysmanage.domain.Inform;
import com.mingtech.application.sysmanage.service.InformService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
@Controller
public class InformController extends BaseController{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(InformController.class);
	private String fileName;//文件名
	@Autowired
	private InformService informService;
	
	/**
	 * 查询当前公告
	 */
	@RequestMapping(value="/viewInformList")
	public void viewInformList(String beginDate,String endDate,String keyStr){
		
		try{
			
			String json = informService.viewInformList(beginDate ,endDate ,keyStr ,this.getPage(), this.getCurrentUser());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	@RequestMapping(value="/informSave",method = RequestMethod.POST)
	public void informSave(String id,String title,String contents,String userName,String distributeDepart,String distrubiteDate,String endDate, @RequestParam(value="file",required=false) File file){
		try{
			if(StringUtils.isEmpty(id)){
				Inform inform = new Inform();
				inform.setTitle(title);
				inform.setContents(contents);
				inform.setUserName(userName);
				inform.setDistributeDepart(distributeDepart);
				inform.setDistrubiteDate(DateUtils.StringToDate(distrubiteDate, "yyyy-MM-dd"));
				inform.setEndDate(DateUtils.StringToDate(endDate, "yyyy-MM-dd"));
				inform.setCreateTime(new Date());
				inform.setStatus("00");
				inform.setUserId(this.getCurrentUser().getId());
				inform.setUserName(this.getCurrentUser().getName());
				inform.setUserDept(this.getCurrentUser().getDepartment().getId());
				inform.setShowLevel(0);
				if(null == fileName || "".equals(fileName)){
					inform.setFilepath("");
				}else{
					int i = fileName.lastIndexOf("\\");
					String distFileName = fileName.substring(i + 1); // 要保存的文件名称
					informService.txStoreDto(inform);
					String filePath = informService.uploadAttach(inform, file ,distFileName);
					inform.setFilepath(filePath);
				}
				informService.txStoreDto(inform);
				this.sendJSON("保存公告成功");
			}else{
				Inform inform=informService.loadInformByPrimaryKey(id);
				inform.setTitle(title);
				inform.setContents(contents);
				inform.setUserName(userName);
				inform.setDistributeDepart(distributeDepart);
				inform.setDistrubiteDate(DateUtils.StringToDate(distrubiteDate, "yyyy-MM-dd"));
				inform.setEndDate(DateUtils.StringToDate(endDate, "yyyy-MM-dd"));
				inform.setCreateTime(new Date());
				inform.setStatus("00");
				inform.setUserId(this.getCurrentUser().getId());
				inform.setUserName(this.getCurrentUser().getName());
				inform.setUserDept(this.getCurrentUser().getDepartment().getId());
				inform.setShowLevel(0);
				if(null == fileName || "".equals(fileName)){
					inform.setFilepath("");
				}else{
					int i = fileName.lastIndexOf("\\");
					String distFileName = fileName.substring(i + 1); // 要保存的文件名称
					informService.txStoreDto(inform);
					String filePath = informService.uploadAttach(inform, file ,distFileName);
					inform.setFilepath(filePath);
				}
				informService.txStoreDto(inform);
				this.sendJSON("修改公告成功");
			}
			
		}catch(Exception e){
			logger.error("保存公告失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("保存公告失败："+e.getMessage());
			
		}
	}
	
	/**
	 * 查询当前公告
	 */
	@RequestMapping(value="/informDelete",method = RequestMethod.POST)
	public void informDelete(String infromids){
		try {
			logger.info("===informDelete===:"+infromids);
			this.informService.txDeleInformByIds(infromids);
			//设置页面返回信息
			this.sendJSON("删除成功！");
		} catch (Exception e) {
			logger.error("保存公告失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除失败！本次错误信息为："+e.getMessage());
		}
	}
	/**
	 * 将当前公告置顶
	 */
	@RequestMapping(value="/informAddTop",method = RequestMethod.POST)
	public void informAddTop(String informids){
		try {
			logger.info("===informAddTop===:"+informids);
			List list = informService.getInformByids(informids, null);
			for(int i=0; i<list.size(); i++){
				Inform inform = (Inform)list.get(i);
				inform.setShowLevel(informService.getCurLevel()+1);
				inform.setTopTime(new Date());
				informService.txStoreDto(inform);
			}
			//设置页面返回信息
			this.sendJSON("放置首页成功！");
		} catch (Exception e) {
			logger.error("放置首页失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("放置首页失败！本次错误信息为："+e.getMessage());
		}
	}
	/**
	 * 将当前公告取消置顶
	 */
	@RequestMapping(value="/informCancelTop",method = RequestMethod.POST)
	public void informCancelTop(String informids){
		try {
			logger.info("===informCancelTop===:"+informids);
			List list = informService.getInformByids(informids, null);
			for(int i=0; i<list.size(); i++){
				Inform inform = (Inform)list.get(i);
				inform.setShowLevel(0);
				inform.setTopTime(new Date());
				informService.txStoreDto(inform);
			}
			//设置页面返回信息
			this.sendJSON("取消放置首页成功！");
		} catch (Exception e) {
			logger.error("取消放置首页失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("取消放置首页失败！本次错误信息为："+e.getMessage());
		}
	}
	/**
	 * 将公告发布
	 */
	@RequestMapping(value="/informDistribute",method = RequestMethod.POST)
	public void informDistribute(String informids){
		try {
			logger.info("===informDistribute===:"+informids);
			List list = informService.getInformByids(informids, null);
			for(int i=0; i<list.size(); i++){
				Inform inform = (Inform)list.get(i);
				inform.setStatus("01");
				informService.txStoreDto(inform);
			}
			//设置页面返回信息
			this.sendJSON("发布公告成功！");
		} catch (Exception e) {
			logger.error("发布公告失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("发布公告失败！本次错误信息为："+e.getMessage());
		}
	}
	/**
	 * 公告发布
	 *  @param id 公告id 
	 *  @param showLevel 公告优先等级
	 */
	@RequestMapping(value="updateNotesLevel",method=RequestMethod.POST)
	public void updateNotesLevel(String id,int showLevel){
		try {
			Inform inform2 = informService.loadInformByPrimaryKey(id);
			inform2.setShowLevel(showLevel);
			informService.txUpdateDto(inform2);
			this.sendJSON("更改优先级成功");
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("更改优先级失败");
		}
	
	}
	
	
	/**
	 * 主页根据公告优先级展示公告
	 */
	@RequestMapping(value="mainNotice",method=RequestMethod.POST)
	public void mainNotice() {
		StringBuffer json = new StringBuffer();
		try {
			List list = informService.getUserInformInMainPage(null);
			if(list != null && list.size() > 0) {
				json.append(JsonUtil.fromCollections(list));
			} else {
				json.append("[]");
			}
			this.sendJSON(json.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
