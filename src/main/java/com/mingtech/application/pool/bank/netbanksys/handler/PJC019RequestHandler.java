package com.mingtech.application.pool.bank.netbanksys.handler;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.PoolDictionaryCache;
import com.mingtech.application.pool.common.domain.PedBlackList;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 
 * @Title: 网银接口 PJC019
 * @Description: 风险管理-名单登记接口
 * @author Ju Nana
 * @date 2018-10-22
 */
public class PJC019RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC019RequestHandler.class);

	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类
	
	@Autowired
	private PedProtocolService pedProtocolService;// 协议服务

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();

		try {
			String doType = getStringVal(request.getBody().get("OPERATION_TYPE"));
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			if(dto == null ) {
				throw new Exception("票据池客户信息不存在");
			}
			List list = request.getDetails();
			if (list != null && list.size() > 0) {
				Map map = null;
				for (Iterator<Map> iterator = list.iterator(); iterator.hasNext();) {
					map = iterator.next();
					PedBlackList black = dataProcess(map, doType);
					black.setOrgCode(custNo);
					black.setCustomerName(dto.getCustname());
					poolEBankService.txBlackListHandler(black, doType);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("操作成功！");
				}
			}

		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("黑名单登记异常");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * 
	 * @Description: 数据加工处理
	 * @return DraftPoolIn
	 * @author Ju Nana
	 * @throws ParseException
	 * @date 2018-10-17 下午3:25:24
	 */
	private PedBlackList dataProcess(Map map, String doType) throws Exception {

		PedBlackList black = new PedBlackList();
		if (!"01".equals(doType)) {
			String id = (String) map.get("BUSINESS_ARRAY.SERIAL_NO");
			black.setId(id);
		}
		String type = (String) map.get("BUSINESS_ARRAY.BUSS_TYPE");
		String keywords = (String) map.get("BUSINESS_ARRAY.KEY_WORDS");
		String content = (String) map.get("BUSINESS_ARRAY.CONTENT");
		Object expipyDateTemp = map.get("BUSINESS_ARRAY.VALIDITY_DATE");
		if (expipyDateTemp != null && !"".equals((String) expipyDateTemp)) {
			Date dueDt = DateUtils.parseDatStr2Date((String) expipyDateTemp, "yyyyMMdd");
			black.setDueDt(dueDt);
		}
		String dataFrom = PoolComm.SOUR_EBK;// 数据来源:网银
		Date createTime = DateUtils.formatDate(new Date(), DateUtils.ORA_DATES_FORMAT);
		// 如果关键词为承兑行所在地区，需要特殊处理
		if (StringUtil.isNotBlank(keywords) && StringUtils.equals(keywords, PoolComm.KEY_WAYS_04)) {
			if(!"03".equals(doType)&&StringUtil.isNotBlank(content)) {
				String[] strArr = content.split(";");
				if(strArr != null && strArr.length ==2) {
					black.setProvince(strArr[0]);
					black.setCity(strArr[1]);
					// 根据网银传过来的码值转换成汉字
					String province = PoolDictionaryCache.getPjcAreaName(strArr[0]);
					String city = PoolDictionaryCache.getPjcAreaName(strArr[1]);
					if(StringUtil.isBlank(province)) {
						throw new Exception("票据池未维护["+strArr[0]+"]省份代码");
					}
					if(StringUtil.isBlank(city)) {
						throw new Exception("票据池未维护["+strArr[1]+"]城市代码");
					}
					black.setContent(province + "" + city);
				} else {
					throw new Exception("承兑行所在地区["+content+"]有误");
				}
			}else if("03".equals(doType)){
				//删除承兑人所在地区
				black.setProvince(null);
				black.setCity(null);
				black.setContent(null);
			}else {
				throw new Exception("承兑行所在地区为空");
			}
		} else {
			black.setContent(content);
		}
		black.setType(type);
		black.setKeywords(keywords);
		black.setDataFrom(dataFrom);
		black.setCreateTime(createTime);

		return black;
	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
