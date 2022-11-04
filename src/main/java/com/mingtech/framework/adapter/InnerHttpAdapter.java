package com.mingtech.framework.adapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.sequence.SequenceUtil;
import com.mingtech.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 票据5.0服务总线内部通讯适配器
 * @author h2
 */
public class InnerHttpAdapter {
	/*
	 *将报文发送到DSSB系统进行处理
	 */
	public JSONObject sendMsgToDSSB(Map reqMsg) throws Exception{
		JSONObject respMsg = new JSONObject();
		//获取票据服务总线-发送第三方系统请求转发服务名称
		String micSrvName = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DSSB_Forward_MicSrv_Name);
		RedisUtils redisrCache = (RedisUtils) SpringContextUtil.getBean("redisrCache");
		Map instanceMap = redisrCache.hmget(micSrvName);
		if(instanceMap==null){
			respMsg.put("RespCode", "9999");
			respMsg.put("RespDesc", "未找到微服务【"+micSrvName+"】的可用健康实例信息");
			return respMsg;
		}else{
			Iterator iterator = instanceMap.keySet().iterator();
			List instanceList = new ArrayList();
			while(iterator.hasNext()){
				String key = (String)iterator.next();
				instanceList.add(key);
			}
			if(instanceList.isEmpty()){
				respMsg.put("RespCode", "9999");
				respMsg.put("RespDesc", "未找到微服务【"+micSrvName+"】的可用健康实例信息");
				return respMsg;
			}
			Random r = new Random();
			int number = r.nextInt(instanceList.size());
			String url = "http://"+instanceList.get(number)+"/"+micSrvName;
			String strRspMsg = HttpUtil.sendJsonByPost(url,reqMsg);
			respMsg = JSON.parseObject(strRspMsg);
			return  respMsg;
		}

	}

	/**
	 * 发送到DSSB服务 除了map 均为必须值
	 * @param txCode dssb接口编号
	 * @param batchId 业务id
	 * @param batchNo 批次或票号(或者用于单一字段传送)
	 * @param totalAmt 总金额
	 * @param totalNum 总笔数
	 * @param billType 票据类型
	 * @param billMedia 票据介质
	 * @param prodId 产品号
	 * @param user 用户信息
	 * @param map 其他值 拓展使用  key-value 均为String类型
	 * @throws Exception
	 */
	public Response sendMsgToDSSB(String txCode, String batchId, String batchNo, BigDecimal totalAmt,
                                  int totalNum, String billType, String billMedia, String prodId, User user, Map map)throws Exception{
		Response response = new Response();
		String reqNo = SequenceUtil.getInstance().getSysCommonSendReqNo();

		//如果不存在txcode 则默认不走第三方
		if (StringUtils.isEmpty(txCode)){
			response.setSendToThird(false);
			response.setFlowNo(reqNo);
			return response;
		}
		//判断是否开启开关
		String item = SystemConfigCache.getSystemConfigItemByCode(txCode);

		if (StringUtils.isEmpty(item) || "false".equals(item)) {
			response.setSendToThird(false);
			response.setFlowNo(reqNo);
			return response;
		}

		Map reqMsg = new HashMap();
		reqMsg.put("TxCode",txCode);
		reqMsg.put("TxDate", DateUtils.toString(DateUtils.getWorkDayDate(), DateUtils.ORA_DATES_FORMAT));
		reqMsg.put("ReqSeqNo",reqNo);
		reqMsg.put("BatchId",batchId);
		reqMsg.put("BatchNo",batchNo);
		reqMsg.put("TotalAmt",totalAmt.toString());
		reqMsg.put("TotalNum",totalNum +"");
		reqMsg.put("BillType",billType);
		reqMsg.put("BillMedia",billMedia);
		reqMsg.put("ProdId",prodId);
		if(user != null){
			reqMsg.put("BranchId",user.getDepartment().getId());
			reqMsg.put("BranchNo",user.getDepartment().getInnerBankCode());
			reqMsg.put("UserNo",user.getLoginName());
		}
		if(map != null && map.size() > 0){
			Set<String> keySet = map.keySet();
			for (String key :keySet) {
				reqMsg.put(key, map.get(key));
			}
		}

		JSONObject rspObj =this.sendMsgToDSSB(reqMsg);

		response.setSendToThird(true);
		response.setCode(rspObj.getString(PublicStaticDefineTab.DSSB_RSPCODE));
		response.setDesc(rspObj.getString(PublicStaticDefineTab.DSSB_RSPDESC));
		if (rspObj.containsKey(PublicStaticDefineTab.DSSB_BODY)) {
			response.setBody((JSONObject)rspObj.get(PublicStaticDefineTab.DSSB_BODY));
		}
		response.setFlowNo(reqNo);

		return response;
	}

}
