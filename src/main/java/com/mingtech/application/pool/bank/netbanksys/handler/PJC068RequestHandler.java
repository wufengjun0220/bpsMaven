package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.domain.PedAssetCrdtDaily;
import com.mingtech.application.pool.query.service.AssetCrdtDailyService;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;

/**
 * @Description 资产实点明细查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC068RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC068RequestHandler.class);
	@Autowired
    private AssetCrdtDailyService assetCrdtDailyService;
	@Autowired
    private CommonQueryService commonQueryService;
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Page page = getPage(request.getAppHead());
        Ret ret = new Ret();
        try {
	        Map map = request.getBody();
	        String coreNo = getStringVal(map.get("CORE_CLIENT_NO"));//核心客户号
	        String bpsNo = getStringVal(map.get("BPS_NO"));//票据池编号
	        String queryDate =(String)map.get("QUERY_DATE");//查询日期

	        //...
	        //...
			
			List addList = new ArrayList();
			List detailList = new ArrayList();

			CommonQueryBean queryBean=new CommonQueryBean();
			queryBean.setBpsNo(bpsNo);
			queryBean.setCreateDate(DateUtils.StringToDate(queryDate,"yyyyMMdd"));
			addList=assetCrdtDailyService.loadPedAssetCrdtDaily(queryBean,page);
			QueryResult result =commonQueryService.loadDataByResult(addList, "totalAmt");
			if (addList != null && result.getRecords().size() > 0) {
				for (int i = 0; i < result.getRecords().size(); i++) {
					Map addMap = new HashMap();
					PedAssetCrdtDaily pedAssetCrdtDaily=(PedAssetCrdtDaily)result.getRecords().get(i);
					String busiTypeName="";
					if("01".equals(pedAssetCrdtDaily.getBusiType() )|| StringUtils.isEmpty(pedAssetCrdtDaily.getBusiType())){
						busiTypeName = "ZCLX_01";
					}else if("02".equals(pedAssetCrdtDaily.getBusiType())){
						busiTypeName = "ZCLX_02";
					}else if("03".equals(pedAssetCrdtDaily.getBusiType())){
						continue;
//						busiTypeName = "ZCLX_03";
					}else if("04".equals(pedAssetCrdtDaily.getBusiType())){
						continue;
//						busiTypeName = "ZCLX_04";
					}
					addMap.put("ASSET_INFO_ARRAY.ASSET_TYPE", busiTypeName); //资产类型       ZCLX_01:现金ZCLX_02:票据
					addMap.put("ASSET_INFO_ARRAY.ASSET_AMT", pedAssetCrdtDaily.getTotalAmt()); //资产金额         
					detailList.add(addMap);
					}
				setPage(response.getAppHead(), page,result.getTotalAmount().toString());
				response.setDetails(detailList);
				response.getAppHead().put("TOTAL_ROWS", detailList.size());// 总记录数
				response.getBody().put("TOTAL_AMT", result.getTotalAmount());      //总金额
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
			} else {
			    ret.setRET_MSG("无符合条件数据");
				ret.setRET_CODE(Constants.EBK_03);
			}
        
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("资产实点明细查询异常");
		
		}
		response.setRet(ret);
        return response;
    }

}
