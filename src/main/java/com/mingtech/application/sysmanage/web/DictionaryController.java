package com.mingtech.application.sysmanage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.common.domain.Dictionary;
import com.mingtech.application.common.logic.IDictionaryService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.vo.TreeDepartment;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;
@Controller
public class DictionaryController extends BaseController{
	
	private static final Logger logger = Logger.getLogger(DictionaryController.class);
	@Autowired
	private IDictionaryService dictionaryService;
	@Autowired
	private CacheUpdateService cacheUpdateService;
	
	@RequestMapping(value="/queryDictionaryList",method = RequestMethod.POST)
	public void queryDictionaryList(Dictionary dictionary){
		try {
			Page page = this.getPage();
			List result = dictionaryService.queryDictionaryList(dictionary,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	@RequestMapping(value="/queryDictionaryByParent")
	public void queryDictionaryByParent(String parentCode){
		try {
			Page page = this.getPage();
			Dictionary dictionary=new Dictionary();
			dictionary.setParentCode(parentCode);
			List result = dictionaryService.queryDictionaryList(dictionary,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	@RequestMapping(value="/saveDic",method = RequestMethod.POST)
	public void saveDictionary(Dictionary dictionary){
		String json="??????????????????";
		try {
			boolean cacheUpdate = false;
			if(StringUtils.isNotBlank(dictionary.getId())){ //id??????????????????
				Dictionary queryDic = dictionaryService.getDictionary(dictionary.getId());
				//??????????????????????????????
				if(!queryDic.getName().equals(dictionary.getName())
						|| !queryDic.getShortName().equals(dictionary.getShortName())
						|| !queryDic.getCode().equals(dictionary.getCode())
						|| !queryDic.getParentCode().equals(dictionary.getParentCode())
						|| queryDic.getLevel() != dictionary.getLevel()
						){
					cacheUpdate = true;
				}
				queryDic.setName(dictionary.getName());
				queryDic.setShortName(dictionary.getShortName());
				queryDic.setCode(dictionary.getCode());
				queryDic.setParentCode(dictionary.getParentCode());
				queryDic.setLevel(dictionary.getLevel());
				dictionaryService.txStore(queryDic);
				json=RESULT_EMPTY_DEFAULT;
				
			}else{ // ??????
				dictionaryService.txStore(dictionary);
				json=RESULT_EMPTY_DEFAULT;
				cacheUpdate = true;
			}
			//if(cacheUpdate == true){//????????????
				//????????????????????????
//				cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_DIC);
			//}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("????????????????????????"+e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????"+e.getMessage());
		}
		
	}
	@RequestMapping(value="/deleteDic",method = RequestMethod.POST)
	public void deleteDictionary(String dicIds){
		String json ="";
		try {
			String[] idsStrings = dicIds.split(",");
			List list = Arrays.asList(idsStrings);
			int size = list.size();
			for(int i=0;i<size;i++){
				dictionaryService.txDelete(list.get(i).toString());
				json=RESULT_EMPTY_DEFAULT;
				sendJSON(json);		
			}
		} catch (Exception e) {
			logger.error("????????????????????????",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????"+e.getMessage());
		}
	}
	/**
	* ????????????: ????????????????????????
	* @param pid ?????????id
	* @author  ice
	* @return void
	* @date 2019-03-15 ??????10:30:47
	*/
	@RequestMapping(value="/listTreeDictionary")
	public void listTreeDictionary(String pid){
		try {
			List dictionarys = dictionaryService.getDictionaryByParentCode(pid,1);
			List result=new ArrayList();
			for(int i=0;i<dictionarys.size();i++){
				Dictionary dic=(Dictionary) dictionarys.get(i);
				TreeDepartment tree=new TreeDepartment();
				tree.setId(dic.getCode());
				tree.setPid(dic.getParentCode());
				tree.setName(dic.getName());
				result.add(tree);
			}
			this.sendJSON(JsonUtil.buildJson(result, 0));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * ?????????????????????
	 * @param parentCode ???????????????
	 */
	@RequestMapping(value="/queryParentDicInf",method = RequestMethod.POST)
	public void queryParentDicInf(String parentCode){
		try {
			User user = this.getCurrentUser();
			Dictionary dic = dictionaryService.getDictionaryByCode(parentCode);
			Map resultMap = new HashMap();
			if(dic != null){
				resultMap.put("parentNm", dic.getName()+"-"+parentCode);
			}else{
				resultMap.put("parentNm", parentCode);
			}
			String json = JSON.toJSONString(resultMap);
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}

	/**
	 * ????????????????????????????????????
	 * @param parentCode ???????????????
	 */
	@RequestMapping(value="/queryNextLevelOfCHildDic",method = RequestMethod.POST)
	public void queryNextLevelOfCHildDic(String parentCode)throws Exception{
		try {
			User user = this.getCurrentUser();
			int level = dictionaryService.queryNextLevelOfCHildDic(parentCode);
			Dictionary parentDic = dictionaryService.getDictionaryByCode(parentCode);
			Map resultMap = new HashMap();
			//????????????????????????8?????????????????????????????????2???
			if(parentDic.getLevel() > 10000000){
 				if(level == 0){//???????????????????????????????????????
					String strLevel = String.valueOf(parentDic.getLevel());
					//??????????????????00
					while(strLevel.endsWith("00")){
						strLevel = strLevel.substring(0,strLevel.length() - 2);
					}
					int count = (8-strLevel.length()) / 2;//??????00????????????
					strLevel = strLevel + "01";
					for(int i=1; i<count; i++){
						strLevel = strLevel + "00";	
					}
					resultMap.put("level", strLevel);
				}else{
					String strLevel = String.valueOf(level);
					//??????????????????00
					while(strLevel.endsWith("00")){
						strLevel = strLevel.substring(0,strLevel.length() - 2);
					}
					int count = (8-strLevel.length()) / 2;//??????00????????????
					strLevel = String.valueOf(Integer.valueOf(strLevel).intValue() + 1);
					for(int i=0; i<count; i++){
						strLevel = strLevel + "00";	
					}
					resultMap.put("level", strLevel);
				}
			}else{
				resultMap.put("level", String.valueOf(level+1));
			}
			String json = JSON.toJSONString(resultMap);
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
		
	}
}
