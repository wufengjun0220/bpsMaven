package com.mingtech.application.pool.draft.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.InPoolBillBean;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 贴现服务
 * @author Wu fengjun
 *
 */ 
public interface DraftPoolDiscountServer extends GenericService{
	
	/**
	 * 通过票据状态查询贴现对象
	 * @param SBillStatus
	 * @param billNo	票号
	 * @param beginNo	子票区间起、
	 * @param endNo 子票区间止
	 * @param page 分页
	 * @return
	 * @throws Exception
	 * 融合改造修改
	 * @author Wu
	 * @date 2022-4-7
	 */
	public List<PlDiscount> getDiscountsListByParam(String SBillStatus, String billNo, String beginNo,String endNo,Page page) throws Exception;
	
	/**
	 * 通过处理标志查询后续处理票据批次信息表
	 * @param doFlag	处理标志
	 * @param str	预留字段
	 * @return
	 * @throws Exception
	 */
	public  List<PlBatchInfo> getBatchInfoByParam(String doFlag ,String str) throws Exception;
	
	/**
	 * 
	 * 批量导出 强制贴现跟踪查询
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findForcedDiscountExpt(List res, Page page) throws Exception;
	
	/**
	 * 通过bean对象  查询票据list 
	 * liuxiaodong
	 * @return
	 * @throws Exception
	 */
	public List<InPoolBillBean> queryInPoolBillByBean(InPoolBillBean bean,User user,Page page) throws Exception;
	
	/**
	 * 
	 * 批量导出 纸票在池票据
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findInPoolBillByBeanExpt(List res, Page page) throws Exception;
	
	/**
	 * 通过票据状态查询贴现对象
	 * @param SBillStatus
	 * @param beginRangeNo	子票区间起
	 * @param endRangeNo	子票区间止
	 * @param billNo	票号
	 * @param bpsNo	票据池编号
	 * @param bpsName	票据池名称
	 * @param user	用户
	 * @param page	分页对象
	 * @param ids	
	 * @param busiId 业务id
	 * @return
	 * @throws Exception
	 */
	public List<PlDiscount> getDiscountsListByParamView(DraftQueryBean bean, User user,Page page) throws Exception;
	/**
	 *  根据ID查询PlDiscount
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PlDiscount loadByPlDiscountId(String id) throws Exception;
	
	/**
	 * 强帖签收记账后处理
	 * @param discount
	 * @param transResult	交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
	 * @param acctResult	记账结果 0:未记账	1：记账成功	2：记账失败
	 * @return
	 * @throws Exception
	 */
	public void txTaskDiscountSynchroniza(PlDiscount discount,String transResult,String acctResult) throws Exception;
	
}
