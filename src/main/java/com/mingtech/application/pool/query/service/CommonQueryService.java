package com.mingtech.application.pool.query.service;
import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.query.domain.AcptCheckSublist;
import com.mingtech.application.pool.query.domain.BusiolControlConfig;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 通用查询服务
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 * @copyright 北明明润（北京）科技有限责任公司
 */
public interface CommonQueryService extends GenericService {

	/**
	 * 查询在线业务禁入名单
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210607
	 */
	public String loadDebarJSON( CommonQueryBean commonQueryBean, User user, Page page) throws Exception;
	/**
	 * 查询在线业务禁入名单
	 * 
	 * @param CommonQueryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadDebarList(CommonQueryBean queryBean, User user, Page page) throws Exception;
	/**
	 * 商票承兑行名单查询
	 * 
	 * @param pedProtocolDto
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadCommercialList(CommonQueryBean queryBean,User user, Page page) throws Exception ;
	/**
	 * 保存或更新商票承兑行名单
	 * @author gcj
	 * @param AcptCheckMainlist
	 * @return
	 * @throws Exception
	 * @date 20210607
	 */
	public String txSavaCommercial(AcptCheckSublist acptList) throws Exception ;
	/**
	 * 删除商票承兑行名单
	 * @author gcj
	 * @param busIds  AcptCheckSublist列表ID
	 * @return
	 * @throws Exception
	 * @date 20210607
	 */
	public String txDeleteCommercial(String busIds) throws Exception ;
	/**
	 * 票据池资产实点查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @date 20210610
	 * @return
	 * @throws Exception
	 */
	public String loadPointList(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 票据池每日资产查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @date 20210610
	 * @return
	 * @throws Exception
	 */
	public String loadPedAssetDaily(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 票据池每日融资业务查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @date 20210610
	 * @return
	 * @throws Exception
	 */
	public String loadPedCrdtDaily(CommonQueryBean queryBean,User user, Page page) throws Exception ;
	/**
	 * 短信信息查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadInformatioNoteList(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 在线流贷支付查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadOnlinePayList(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 在线流贷信息查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadPlOnlineCrdtList(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 在线流贷支付查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadOnlinePayByBeanExp(CommonQueryBean queryBean,User user, Page page) throws Exception ;
	/**
	 * 在线流贷支付查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadOnlinePayHisByBeanExp(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * 业务控制清单查询
	 * @param config
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List queryDisolControlConfigList(BusiolControlConfig config, Page page) throws Exception;
	/**
	 * 保存控制参数
	 * @param keys
	 * @param values
	 * @throws Exception
	 */
	public void txSaveControlConfig(Map<String,String> map,User user) throws Exception;
	/**
	 *票据池合同协议表查询
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadPoolQuotaList(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 *票据池合同协议表查询
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadPoolQuotaToExpt(CommonQueryBean queryBean,User user, Page page) throws Exception ;
	/**
	 *  获取开户行名称
	 * @param bankNo 行号
	 * @return String  行名
	 * @throws Exception
	 */
	public String queryAcceptNameJson(String bankNo) throws Exception;
	/**
	 * 业务控制清单查询
	 * @param config
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<SystemConfig> queryControlConfigList(SystemConfig config, Page page, User user) throws Exception;
	/**
	 *票据池签约客户查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadCustomerRegisterList(CommonQueryBean queryBean,User user, Page page) throws Exception;
	/**
	 * list 类型转换 QueryResult
	 * 
	 * @param list 
	 * @return
	 * @throws Exception
	 */
	public QueryResult  loadDataByResult(List list ,String amtName) throws Exception;
}
