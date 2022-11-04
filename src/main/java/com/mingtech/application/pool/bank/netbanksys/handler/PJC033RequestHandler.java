
package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.query.domain.PedCrdtDaily;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.framework.common.util.DateTimeUtil;

/**
 *
 * @Title: 单户签约信息维护（银行端）
 * @Description: 单户签约接口
 * @author Ju Nana
 * @date 2019-06-04
 */
public class PJC033RequestHandler extends PJCHandlerAdapter {

    private static final Logger logger = Logger.getLogger(PJC033RequestHandler.class);
    @Autowired
    private PoolEBankService poolEBankService; // 网银方法类
    @Autowired
    private PedProtocolService pedProtocolService;
    @Autowired
    private PoolCoreService poolCoreService;
    @Autowired
    private PedAssetTypeService pedAssetTypeService;
    @Autowired
    private  UserService userService;
    @Autowired
    private PoolBailEduService poolBailEduService;
    @Autowired
    private PedAssetPoolService pedAssetPoolService;
    @Autowired
    private FinancialService financialService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private DepartmentService departmentService;
	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
        // 构建信息接收对象
        PedProtocolDto protocol = QueryProtocolMap(request);
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        String poolAgreement = null;
        try {
            /**【签约标识】为：01签约
             * 【签约类型】为：
             * a.基础签约QYLX_01:
             * 协议相关校验
             * 1.客户经理信息是否存在
             * b.融资签约QYLX_02:
             * 协议相关校验
             * 1.客户经理信息是否存在
             * 2.保证金账号是否复用以及保证金账号是否开户
             * 3.该客户是否在其它途径签署自动入池
             */
            if (protocol.getEbankFlag().equals("01")) {//签约
                ProtocolQueryBean  queryBeanped = new ProtocolQueryBean();
                queryBeanped.setCustnumber(protocol.getCustnumber());
                queryBeanped.setIsGroup(PoolComm.NO);
                queryBeanped.setvStatus(PoolComm.VS_01);
                PedProtocolDto  pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBeanped);//根据客户号与是否集团查询票据池协议
                //判断根据当前客户号判断是否存在基础功能签约,为null则为初次签约,不为null则判定为融资功能补签
                if(null!=pedProtocolDto){
                    if (PoolComm.VS_01.equals(pedProtocolDto.getvStatus()) && PoolComm.QYLX_01.equals(protocol.getpSignType())) {
                        ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("该客户已基础签约，不允许重复签约！");
                        response.setRet(ret);
                        return response;
                    }
                    if(PoolComm.OPEN_01.equals(pedProtocolDto.getOpenFlag()) && PoolComm.QYLX_02.equals(protocol.getpSignType())){
                        ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("该客户已融资签约，不允许重复签约！");
                        response.setRet(ret);
                        return response;
                    }
                    User user = userService.getUserByLoginName(protocol.getAccountManagerId());//校验客户经理
                    if (null == user) {
                        ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                        response.setRet(ret);
                        return response;
                    }else{
                    	boolean isManager = false;//是否为客户经理
            			if(user.getRoleList()!=null && user.getRoleList().size()>0){
            				for(Role role : (List<Role>)user.getRoleList()){
            					if( StringUtil.isNotBlank(role.getCode())){						
            						String roleCode  = role.getCode();
            						if(PoolComm.roleCode6.equals(roleCode)){
            							isManager = true;
            							break;
            						}
            					}
            				}
            			}
            			if(!isManager){
            				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                             ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                             response.setRet(ret);
                             return response;
            			}
            			if(!StringUtils.isBlank(protocol.getAccountManager())&&!protocol.getAccountManager().equals(user.getName())){
            				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                             ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                             response.setRet(ret);
                             return response;
            			}
                    }
                    
                    if(StringUtil.isNotBlank(protocol.getMarginAccount())){
                    	Map marginAccount = this.queryAccountFromCoreSys(protocol);
                        if(marginAccount.containsKey("error")){//判断保证金账号是否在核心开户
                            ret.setRET_CODE(Constants.TX_FAIL_CODE);
                            ret.setRET_MSG((String) marginAccount.get("error"));
                            response.setRet(ret);
                            return response;
                        }
                    }else{
                    	 response=pedProtocolService.txMarginAccount(protocol);
                         if(!response.isTxSuccess()){//保证金账号在核心开户
                             ret.setRET_CODE(Constants.TX_FAIL_CODE);
                             ret.setRET_MSG("核心保证金开户失败："+response.getRet().getRET_MSG());
                             response.setRet(ret);
                             return response;
                         }
                    }

                    List listMargin = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null, null, protocol.getMarginAccount());
                    if (null != listMargin && listMargin.size() >= 1) {
                        ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("该保证金账号有对应的【其他】有效的签约协议，不允许签约！");
                        response.setRet(ret);
                        return response;
                    }
                    //自动入池校验
                    if (PoolComm.ZY_FLAG_01.equals(protocol.getZyflag())) {
                        boolean pedIsAuto = pedProtocolService.isAutoCheck(protocol.getPoolAgreement(), protocol.getCustnumber());
                        if (pedIsAuto) {//自动入池判断
                            ret.setRET_CODE(Constants.TX_FAIL_CODE);
                            ret.setRET_MSG("已经存在签约自动入池，不允许签约!");
                            response.setRet(ret);
                            return response;
                        }
                    }
                    poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                    response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                    ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                    ret.setRET_MSG("执行成功！");
                }else{
                    if(PoolComm.QYLX_02.equals(protocol.getpSignType())){//初次签约_融资功能签约
                        User user = userService.getUserByLoginName(protocol.getAccountManagerId());//校验客户经理
                        if (null == user) {
                            ret.setRET_CODE(Constants.TX_FAIL_CODE);
                            ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                            response.setRet(ret);
                            return response;
                        }else{
                        	boolean isManager = false;//是否为客户经理
                			if(user.getRoleList()!=null && user.getRoleList().size()>0){
                				for(Role role : (List<Role>)user.getRoleList()){
                					if( StringUtil.isNotBlank(role.getCode())){						
                						String roleCode  = role.getCode();
                						if(PoolComm.roleCode6.equals(roleCode)){
                							isManager = true;
                							break;
                						}
                					}
                				}
                			}
                			if(!isManager){
                				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                 ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                                 response.setRet(ret);
                                 return response;
                			}
                        }
                        
                        if(StringUtil.isNotBlank(protocol.getMarginAccount())){
                        	Map marginAccount = this.queryAccountFromCoreSys(protocol);
                            if(marginAccount.containsKey("error")){//判断保证金账号是否在核心开户
                                ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                ret.setRET_MSG((String) marginAccount.get("error"));
                                response.setRet(ret);
                                return response;
                            }
                        }else{
                        	 response=pedProtocolService.txMarginAccount(protocol);
                             if(!response.isTxSuccess()){//保证金账号在核心开户
                                 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                 ret.setRET_MSG(response.getRet().getRET_MSG());
                                 response.setRet(ret);
                                 return response;
                             }
                        }
                        if(!StringUtils.isBlank(protocol.getAccountManager())&&!protocol.getAccountManager().equals(user.getName())){
           				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                            ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                            response.setRet(ret);
                            return response;
           			}

                        List listMargin = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null, null, protocol.getMarginAccount());
                        if (null != listMargin && listMargin.size() >= 1) { //保证金校验
                            ret.setRET_CODE(Constants.TX_FAIL_CODE);
                            ret.setRET_MSG("该保证金账号有对应的【其他】有效的签约协议，不允许签约！");
                            response.setRet(ret);
                            return response;
                        }
                        boolean bo = pedProtocolService.isAutoCheck(protocol.getPoolAgreement(),protocol.getCustnumber());
                        if(PoolComm.ZY_FLAG_01.equals(protocol.getZyflag())&&bo){ //自动入池校验
                            ret.setRET_CODE(Constants.TX_FAIL_CODE);
                            ret.setRET_MSG("已经存在签约自动入池，不允许签约!");
                            response.setRet(ret);
                            return response;
                        }
                        poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                        response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                        ret.setRET_MSG("执行成功！");
                    }else{//初次签约_基础签约
                        poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                        response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                        ret.setRET_MSG("执行成功！");
                    }
                }
            }
            //【签约标识】为：02解约
            if (protocol.getEbankFlag().equals("02")){// 解约时候判断有无已用额度，若有则不允许解约
                ProtocolQueryBean  queryBean= new ProtocolQueryBean();
                queryBean.setCustnumber(protocol.getCustnumber());
                queryBean.setPoolAgreement(protocol.getPoolAgreement());
                queryBean.setIsGroup(PoolComm.NO);
                queryBean.setvStatus(PoolComm.VS_01);
                PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
                if (null != pedProtocolDto) {
                        if (PoolComm.OPEN_01.equals(pedProtocolDto.getOpenFlag())) {//已签约状态
                            ret = poolEBankService.endCreditCheck(pedProtocolDto);//融资解约判断
                            if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){//校验失败直接返回
                                response.getBody().put("BPS_NO", pedProtocolDto.getPoolAgreement());//返回协议编号
                                ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                ret.setRET_MSG(ret.getRET_MSG());
                                response.setRet(ret);
                                return response;
                            } else {//解约_全解约
                                poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                                response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                                ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                                ret.setRET_MSG("解约成功！");
                            }
                        } else {//解约_基础解约
                            poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                            response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                            ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                            ret.setRET_MSG("解约成功！");
                        }
                } else {
                    ret.setRET_CODE(Constants.EBK_03);
                    ret.setRET_MSG("未找到该客户有效的签约信息！");
                }
            }
            //【签约标识】为：03签约修改
            if (protocol.getEbankFlag().equals("03")) {
                ProtocolQueryBean  queryBeanped = new ProtocolQueryBean();
                queryBeanped.setCustnumber(protocol.getCustnumber());
                queryBeanped.setPoolAgreement(protocol.getPoolAgreement());
                queryBeanped.setvStatus(PoolComm.VS_01);
                queryBeanped.setIsGroup(PoolComm.NO);
                PedProtocolDto  pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBeanped);
                if(null!=pedProtocolDto) {
                    //判断电票签约账号是否被修改
                    String[] oldElecAccounts = null;// 原电票签约账号
                    String[] newElecAccounts = null; // 新电票签约账号
                    List<String> inOldNotinNew = new LinkedList<String>(); // 在原签约账号中有在新签约账号中没有的账号信息
                    List<PoolBillInfo> poolList1 = new LinkedList<PoolBillInfo>();// 大票表中初始化的票据集合
                    List<PoolBillInfo> poolList2 = new LinkedList<PoolBillInfo>();// 大票表中非初始化的票据集合
                    if (pedProtocolDto.getElecDraftAccount() != null) {
                        oldElecAccounts = pedProtocolDto.getElecDraftAccount().split("\\|");
                    }
                    if (protocol.getElecDraftAccount() != null) {
                        newElecAccounts = protocol.getElecDraftAccount().split("\\|");
                    }
                    if (oldElecAccounts != null && oldElecAccounts.length > 0) {
                        for (String old : oldElecAccounts) {
                            if (!ArrayUtils.contains(newElecAccounts, old)) {// 如果新的电票签约账号中不包含原电票签约账号
                                inOldNotinNew.add(old);
                            }
                        }
                    }
                    if (inOldNotinNew != null && inOldNotinNew.size() > 0) {
                        for (String acc : inOldNotinNew) {
                            List<PoolBillInfo> poolListA = poolEBankService.queryBillByElecAccAndTypeNew(acc, "0", protocol.getPoolAgreement());// 初始化的票
                            if (poolListA != null && poolListA.size() > 0) {
                                poolList1.addAll(poolListA);
                            }
                            List<PoolBillInfo> poolListB = poolEBankService.queryBillByElecAccAndTypeNew(acc, "1", protocol.getPoolAgreement());// 全部
                            if (poolListB != null && poolListB.size() > 0) {
                                poolList2.addAll(poolListB);
                            }
                        }
                        if (poolList2 != null && poolList2.size() > 0) {
                            response.getBody().put("BPS_NO", protocol.getPoolAgreement()); // 返回协议编号
                            ret.setRET_CODE(Constants.EBK_09);
                            ret.setRET_MSG("该客户已签约电票签约账户下有未处理完毕的业务，不允许更换电票账号！");
                            response.setRet(ret);
                            return response;
                        }
                    }
                    if (PoolComm.JYLX_01.equals(protocol.getpBreakType())) {//签约修改-融资功能解约
                        if (PoolComm.OPEN_01.equals(pedProtocolDto.getOpenFlag())) {// 已融资功能签约
                            if(null!=protocol.getAccountManagerId()) {
                                User user = userService.getUserByLoginName(protocol.getAccountManagerId());//校验客户经理
                                if (null == user) {
                                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                    ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                                    response.setRet(ret);
                                    return response;
                                }else{
                                	boolean isManager = false;//是否为客户经理
                        			if(user.getRoleList()!=null && user.getRoleList().size()>0){
                        				for(Role role : (List<Role>)user.getRoleList()){
                        					if( StringUtil.isNotBlank(role.getCode())){						
                        						String roleCode  = role.getCode();
                        						if(PoolComm.roleCode6.equals(roleCode)){
                        							isManager = true;
                        							break;
                        						}
                        					}
                        				}
                        			}
                        			if(!isManager){
                        				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                         ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                                         response.setRet(ret);
                                         return response;
                        			}
                        			if(!StringUtils.isBlank(protocol.getAccountManager())&&!protocol.getAccountManager().equals(user.getName())){
                       				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                        ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                                        response.setRet(ret);
                                        return response;
                       			}
                                }
                            }
                            ret = poolEBankService.endCreditCheck(pedProtocolDto);//融资解约判断
                            if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){//校验失败直接返回
                                response.getBody().put("BPS_NO", pedProtocolDto.getPoolAgreement());//返回协议编号
                                ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                ret.setRET_MSG(ret.getRET_MSG());
                                response.setRet(ret);
                                return response;
                            }else{// 解约
                                poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                                if (null != poolList1 && poolList1.size() > 0) {// 如果该电票签约账号下只有初始化的票据，则删除所有初始化的数据，然后可以修改签约
                                    poolEBankService.txDeleteAll(poolList1);
                                }
                                response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                                ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                                ret.setRET_MSG("签约修改执行成功！");
                            }
                        } else {
                            ret.setRET_CODE(Constants.EBK_06);
                            ret.setRET_MSG("该客户融资已解约，不允许解约操作！");
                            response.getBody().put("BPS_NO", pedProtocolDto.getPoolAgreement()); // 返回协议编号
                        }
                    }else { //签约修改-修改
                        if (PoolComm.OPEN_01.equals(pedProtocolDto.getOpenFlag())){//已开通融资签约
                            if(null!=protocol.getAccountManagerId()) {
                                User user = userService.getUserByLoginName(protocol.getAccountManagerId());//校验客户经理
                                if (null == user) {
                                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                    ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                                    response.setRet(ret);
                                    return response;
                                }else{
                                	boolean isManager = false;//是否为客户经理
                        			if(user.getRoleList()!=null && user.getRoleList().size()>0){
                        				for(Role role : (List<Role>)user.getRoleList()){
                        					if( StringUtil.isNotBlank(role.getCode())){						
                        						String roleCode  = role.getCode();
                        						if(PoolComm.roleCode6.equals(roleCode)){
                        							isManager = true;
                        							break;
                        						}
                        					}
                        				}
                        			}
                        			if(!isManager){
                        				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                         ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                                         response.setRet(ret);
                                         return response;
                        			}
                        			if(!StringUtils.isBlank(protocol.getAccountManager())&&!protocol.getAccountManager().equals(user.getName())){
                       				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                        ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                                        response.setRet(ret);
                                        return response;
                       			}
                                }
                            }
                            if(!protocol.getFeeType().equals(pedProtocolDto.getFeeType())
                                &&PoolComm.SFMS_01.equals(pedProtocolDto.getFeeType())){//判断年费是否修改
                                if(pedProtocolDto.getFeeDueDt().compareTo(DateTimeUtil.getWorkday()) > 0){
                                    ret.setRET_CODE(Constants.EBK_03);
                                    ret.setRET_MSG("该客户年费到期日未到期，不允许修改操作！");
                                    response.getBody().put("BPS_NO", pedProtocolDto.getPoolAgreement()); // 返回协议编号
                                    response.setRet(ret);
                                    return response;
                                }
                            }
                            if(!protocol.getMarginAccount().equals(pedProtocolDto.getMarginAccount())){ //判断原保证金账号是否被修改
                            	/**
                            	 * 新加逻辑  保证金账号变更时:存量该票据池担保的业务结清且在线协议失效才允许跨分行（在线协议的入账机构与新保证金账号的归属机构）；
                            	 * 1、查询是否有生效的在线银承或流贷协议
                            	 * 2、根据协议判断是否有未结清的存量业务
                            	 * 3、
                            	 */
                            	boolean flag = departmentService.checkBranch(protocol.getMarginAccount(),protocol.getAccountManagerId());
                            	
                            	if(!flag){
                            		
                            		boolean Flag;
                            		//查询是否存在存量未结清的在线融资业务
                                	CreditQueryBean bean = new CreditQueryBean();
                                	bean.setBpsNo(pedProtocolDto.getPoolAgreement());
                                	List status = new ArrayList();
                                	status.add(PoolComm.JJ_04);//结清
                                	status.add(PoolComm.JJ_05);//未用退回
                                	bean.setLoanStatusNotInLsit(status);
                                	//查询该票据池的流贷业务是否有未结清数据
                                	List<PedCreditDetail> cdList = poolCreditProductService.queryCreditDetailList(bean);
                                	if(cdList != null && cdList.size() > 0){
                                		//存量业务未结清
                                		Flag = false;
                                	}else{
                                		//存量业务已结清
                                		Flag = true;
                                	}
                                	if(!Flag){
                            			ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                        ret.setRET_MSG("该票据池存在未结清的融资业务，不允许做跨分行保证金账号变更！");
                                        response.setRet(ret);
                                        return response;
                            		}
                            		
                            		//不同分行的保证金账号变更
                            		OnlineQueryBean queryBean = new OnlineQueryBean();
                                	queryBean.setBpsNo(pedProtocolDto.getPoolAgreement());
                                	queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);//生效
                                	
                                	List<PedOnlineAcptProtocol> acptList = pedOnlineAcptService.queryOnlineAcptProtocolList(queryBean);
                                	List<PedOnlineCrdtProtocol> crdtList = pedOnlineCrdtService.queryOnlineProtocolList(queryBean);
                                	if((acptList != null && acptList.size() > 0) || (crdtList != null && crdtList.size() > 0)){
                                		ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                        ret.setRET_MSG("该票据池在线协议未失效，不允许做跨分行保证金账号变更！");
                                        response.setRet(ret);
                                        return response;
                                	}
                            	}
                            	
                            	
                                List listMargin = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null, null, protocol.getMarginAccount());
                                if (null != listMargin && listMargin.size() >= 1) {
                                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                    ret.setRET_MSG("新保证金账号有对应的【其他】有效的签约协议，不允许修改！");
                                    response.setRet(ret);
                                    return response;
                                }
                                //A：重组融资业务
                                
                                /*
                                 * assetPool锁
                                 */
                        		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pedProtocolDto);
                        		String apId = ap.getApId();
                        		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
                        		if(!isLockedSucss){//加锁失败
                        			ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        			ret.setRET_MSG("该票据池有其他额度相关任务正在处理中，请稍后再试！");
                        			response.setRet(ret);
                                    return response;
                        		}
                        		
                                /*
                        		 * 同步核心保证金，并重新计算额度
                        		 */
                        		try {
                					financialService.txBailChangeAndCrdtCalculation(pedProtocolDto);
                				} catch (Exception e) {
                					ret.setRET_CODE(Constants.TX_FAIL_CODE);
                					ret.setRET_MSG(e.getMessage());
                					response.setRet(ret);
                					return response;
                				}
                        		
                        		/*
                        		 * 解锁AssetPool表，并重新计算该表数据
                        		 */
                        		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
                        		
                                //B:获取保证金已用额度
                                AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(pedProtocolDto, PoolComm.ED_BZJ_HQ);
                                BigDecimal usedAmt = at.getCrdtUsed();//已用保证金额度
                                //C：核心获取新保证金的余额
                                BigDecimal freeAmt = poolBailEduService.queryCoreBailFree(protocol.getMarginAccount());
                                //D:如果新保证金账户中的可用额度小于已占用的额度，则不允许更换。核心查询无结果不允许更换。
                                if(freeAmt==null){
                                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                    ret.setRET_MSG("该保证金账号无核心开户信息，不允许更换！");
                                    response.setRet(ret);
                                    return response;
                                }
                                if(freeAmt.compareTo(usedAmt)<0){
                                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                    ret.setRET_MSG("新保证金账户余额小于票据池保证金已用额度，不允许更换！");
                                    response.setRet(ret);
                                    return response;
                                }
                            }
                            //判断当前自动入池标志与传自动入池标记值是否一致,不一致则说明被修改,如当前值为非自动则需判断
                            if (!protocol.getZyflag().equals(pedProtocolDto.getZyflag())&&PoolComm.ZY_FLAG_00.equals(pedProtocolDto.getZyflag())){
                                boolean pedIsAuto = pedProtocolService.isAutoCheck(protocol.getPoolAgreement(), protocol.getCustnumber());
                                if (pedIsAuto) {
                                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                    ret.setRET_MSG("已经存在签约自动入池，不允许修改!");
                                    response.setRet(ret);
                                    return response;
                                }
                            }
                            //保存协议信息
                           String marginAccount= pedProtocolDto.getMarginAccount();//原保证金账号
                            AssetType assetTypeDto = pedAssetTypeService.queryPedAssetTypeByProtocol(pedProtocolDto,PoolComm.ED_BZJ_HQ);
                            if(assetTypeDto!=null){
                            	
                                if (null!=protocol.getMarginAccount()&&!protocol.getMarginAccount().equals(marginAccount)) {
                                	response=pedProtocolService.txMarginAccChange(marginAccount,protocol.getMarginAccount(),protocol.getPoolAgreement()); 
                                  if(!response.isTxSuccess()){//票据池保证金账户变更
                                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                                    ret.setRET_MSG("核心保证金账户变更出错:"+response.getRet().getRET_CODE());
                                    response.setRet(ret);
                                    return response;
                                  }else{
                                	BailDetail detail = poolBailEduService.queryBailDetail(assetTypeDto.getId());
                                    detail.setAssetNb(protocol.getMarginAccount());
                                    poolBailEduService.txStore(detail); 
                                  	//保证金更换时资产登记表中数据同步更新  先查询登记表中是否存在,存在则更新,不存在不处理
                                  	poolEBankService.txChangeBailAccNo(pedProtocolDto.getPoolAgreement(), protocol.getMarginAccount(), marginAccount);     	
                                  }
                                }
                            }else{
                                logger.error("签约信息维护，获取资产池为空！");
                            }
                            poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                            response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                            if (null != poolList1 && poolList1.size() > 0) {// 如果该电票签约账号下只有初始化的票据，则删除所有初始化的数据，然后可以修改签约
                                poolEBankService.txDeleteAll(poolList1);
                            }
                            ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                            ret.setRET_MSG("签约修改执行成功！");
                            response.setRet(ret);
                            return response;
                        }
                        poolAgreement = poolEBankService.txPedProtocolDtoPJC033(protocol);
                        if (null != poolList1 && poolList1.size() > 0) {// 如果该电票签约账号下只有初始化的票据，则删除所有初始化的数据，然后可以修改签约
                            poolEBankService.txDeleteAll(poolList1);
                        }
                        response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
                        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                        ret.setRET_MSG("签约修改执行成功！");
                        response.setRet(ret);
                        return response;
                    }
                }else{
                    ret.setRET_CODE(Constants.EBK_03);
                    ret.setRET_MSG("未找到该客户有效的签约信息！");
                }
            }
        } catch (Exception e) {
            logger.error("虚拟票据池签约处理异常", e);
            ret.setRET_CODE(Constants.TX_FAIL_CODE);
            ret.setRET_MSG("虚拟票据池签约处理异常");
            response.setRet(ret);
            return response;
        }
        response.setRet(ret);
        if(ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
            /*
			 * 协议信息及集团协议子户信息备份
			 */
			if(StringUtil.isNotBlank(protocol.getPoolAgreement())){				
				pedProtocolService.txProtocolAndListHist(protocol.getPoolAgreement());
			}
			
			//生成协议客户信息对象
			CustomerRegister customer = new CustomerRegister();
			customer.setCustNo(protocol.getCustnumber());
			customer.setCustName(protocol.getCustname());
			customer.setFirstDateSource("PJC033");
			customerService.txSaveCustomerRegister(customer);
        }
        return response;
    }

    public  PedProtocolDto QueryProtocolMap(ReturnMessageNew request) throws Exception {

        PedProtocolDto protocol = new PedProtocolDto();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String custnumber = (String) request.getBody().get("CORE_CLIENT_NO");// 核心客户号
        String custname = (String) request.getBody().get("CLIENT_NAME");// 客户名称
        String custOrgcode = (String) request.getBody().get("ORG_CODE"); //组织机构代码
        String plUSCC = (String) request.getBody().get("UNIFIED_CREDIT_CODE"); //社会信用代码
        String poolAgreement = (String) request.getBody().get("BPS_NO");// 票据池编号
        String ebankFlag = (String)  request.getBody().get("SIGN_FLAG"); //签约标识
        String pSignType = (String) request.getBody().get("SIGN_TYPE"); //签约类型
        String pBreakType = (String) request.getBody().get("SIGN_OFF_TYPE");  //解约类型
        String elecDraftAccount = (String) request.getBody().get("SIGN_BILL_ID");//签约电票账号
        String elecDraftAccountName = (String) request.getBody().get("SIGN_BILL_NAME");//签约电票账号名称
        String busiType = (String) request.getBody().get("BUSS_TYPE");//业务类型
        String signingFunction = (String) request.getBody().get("SUB_SIGN_TYPE");//签约类型
        String poolMode = (String) request.getBody().get("SIGN_MODE");//签约方式
        String isGroup = (String) request.getBody().get("IS_GROUP");//是否集团
        String stDate = (String) request.getBody().get("AGREE_EFFECTIVE_DATE");//协议生效日
        String edDate = (String) request.getBody().get("AGREE_EXPIRY_DATE");//协议到期日
        String xyflag = (String) request.getBody().get("AUTO_RENEW_FLAG");//自动续约标志
        String signDeptNo = (String) request.getBody().get("SIGN_BRANCH_ID");//签约机构号
        String signDeptName = (String) request.getBody().get("SIGN_BRANCH_NAME");//签约机构名称
        String marginAccount = (String) request.getBody().get("DEPOSIT_ACCT_NO");//保证金账户
        String marginAccountName = (String) request.getBody().get("DEPOSIT_ACCT_NAME");//池保证金账户名称
        String poolAccount = (String) request.getBody().get("SETTLE_ACCT_NO");//结算账户
        String poolAccountName = (String) request.getBody().get("SETTLE_ACCT_NAME");//结算账户名
        String zyflag = (String) request.getBody().get("AUTO_INPOOL_FLAG");//是否自动入池
        String officeNet = (String) request.getBody().get("RCV_BRANCH_ID");//受理网点
        String officeNetName = (String) request.getBody().get("RCV_BRANCH_NAME");//受理网点名称
        String accountManagerId = (String) request.getBody().get("CUST_MANAGER_ID");//客户经理id
        String accountManager = (String) request.getBody().get("CUST_MANAGER_NAME");//客户经理姓名
        String feeType = (String) request.getBody().get("FEE_MODE");//收费模式


        Date effstartdate =null;
        if(StringUtils.isNotBlank(stDate)){
            effstartdate = sdf.parse(stDate);//协议生效日期
        }
        Date effenddate =null;
        if(StringUtils.isNotBlank(edDate)){
            effenddate = sdf.parse(edDate);// 协议到期日
        }
        if(StringUtil.isNotBlank(custnumber)){
            protocol.setCustnumber(custnumber);
        }
        if(StringUtil.isNotBlank(custname)){
            protocol.setCustname(custname);
        }
        if(StringUtil.isNotBlank(custOrgcode)){
            protocol.setCustOrgcode(custOrgcode);
        }
        if(StringUtil.isNotBlank(poolAgreement)){
            protocol.setPoolAgreement(poolAgreement);
        }
        if(StringUtil.isNotBlank(ebankFlag)){
            protocol.setEbankFlag(ebankFlag);
        }
        if(StringUtil.isNotBlank(elecDraftAccount)){
            protocol.setElecDraftAccount(elecDraftAccount);
        }
        if(StringUtil.isNotBlank(elecDraftAccountName)){
            protocol.setElecDraftAccountName(elecDraftAccountName);
        }
        if(StringUtil.isNotBlank(busiType)){
            protocol.setBusiType(busiType);
        }
        if(StringUtil.isNotBlank(isGroup)){
            protocol.setIsGroup(isGroup);
            //protocol.setContractType(PoolComm.QY_01);//单户票据池
        }
        if(StringUtil.isNotBlank(xyflag)){
            protocol.setXyflag(xyflag);
        }
        if(StringUtil.isNotBlank(signDeptNo)){
            protocol.setSignDeptNo(signDeptNo);
        }
        if(StringUtil.isNotBlank(signDeptName)){
            protocol.setSignDeptName(signDeptName);
        }
        if(StringUtil.isNotBlank(marginAccount)){
            protocol.setMarginAccount(marginAccount);
        }
        if(StringUtil.isNotBlank(marginAccountName)){
            protocol.setMarginAccountName(marginAccountName);
        }
        if(StringUtil.isNotBlank(poolAccount)){
            protocol.setPoolAccount(poolAccount);
        }
        if(StringUtil.isNotBlank(poolAccountName)){
            protocol.setPoolAccountName(poolAccountName);
        }
        if(StringUtil.isNotBlank(officeNet)){
            protocol.setOfficeNet(officeNet);
        }
        if(StringUtil.isNotBlank(officeNetName)){
            protocol.setOfficeNetName(officeNetName);
        }
        if(StringUtil.isNotBlank(accountManagerId)){
            protocol.setAccountManagerId(accountManagerId);
           /* User user = userService.getUserByLoginName(accountManagerId);//校验客户经理
            if(null != user){            	
            	Department dept =  (Department)userService.load(user.getDeptId(),Department.class);
            	String deptNo = dept.getInnerBankCode();
            	String deptName = dept.getName();
            	protocol.setOfficeNet(deptNo);
            	protocol.setOfficeNetName(deptName);
            }*/
        }
        if(StringUtil.isNotBlank(accountManager)){
            protocol.setAccountManager(accountManager);
        }
        protocol.setCreditamount(new BigDecimal("0.00"));
        if(effstartdate!=null){
            protocol.setEffstartdate(effstartdate);
        }
        if(effenddate!=null){
            protocol.setEffenddate(effenddate);
        }
        if(StringUtil.isNotBlank(pSignType)){
            protocol.setpSignType(pSignType);
        }
        if(StringUtil.isNotBlank(pBreakType)){
            protocol.setpBreakType(pBreakType);
        }
        if(StringUtil.isNotBlank(feeType)){
            protocol.setFeeType(feeType);
        }
        if(StringUtil.isNotBlank(plUSCC)){
            protocol.setPlUSCC(plUSCC);
        }
        if(StringUtil.isNotBlank(signingFunction)){
            protocol.setSigningFunction(signingFunction);
        }
        if(StringUtil.isNotBlank(poolMode)){
            protocol.setPoolMode(poolMode);
        }
        if(StringUtil.isNotBlank(zyflag)){
            protocol.setZyflag(zyflag);
        }
        //签约默认保证金划转审批为否
        protocol.setIsAcctCheck(PoolComm.NO);
//		protocol.setXyflag(PoolComm.YES);
        return protocol;

    }


    /** 描述:修改保证金账号时根据新保证金账号去核心同步余额,判断可用余额是否大于已使用保证金余额
     * @param protocol  新签约信息
     * @param pedProtocolDto 原签约信息
     * @return Map
     * @throws Exception
     */
    /*public Map queryAccountFromCoreSys(PedProtocolDto protocol,PedProtocolDto pedProtocolDto ) throws Exception {
        Map map = new HashMap();
        CoreTransNotes transNotes = new CoreTransNotes();
        transNotes.setAccNo(protocol.getMarginAccount());
        transNotes.setCurrentFlag("1");
        ReturnMessageNew returnMessageNew = poolCoreService.PJH716040Handler(transNotes, "1");
        if (returnMessageNew.isTxSuccess()) {
            Map bodyMap = returnMessageNew.getBody();
            String proNo = getStringVal(bodyMap.get("PRODUCT_NO"));// 产品编号
            String clientNo=getStringVal(bodyMap.get("CLIENT_NO"));//客户号
            BigDecimal balnce= getBigDecimalVal(bodyMap.get("AVAL_BALANCE"));//余额
            BailDetail bailDetail=(BailDetail) pedProtocolService.queryBailDetail(pedProtocolDto.getMarginAccount(),pedProtocolDto.getPoolAgreement());
            if(proNo != null && !proNo.equals("2209022")){// 码值表示票据池保证金
                map.put("error","该保证金账户非票据池保证金账户");
                return map;
            }
            if(null!=clientNo&&clientNo.equals(protocol.getCustnumber())){
                map.put("error","该保证金账号非本客户下账号");
                return map;
            }
            if(null != bailDetail){
                BigDecimal assetLimitUsed=bailDetail.getAssetLimitUsed();//已使用的保证金
                if(assetLimitUsed.compareTo(balnce) >0){
                    map.put("error","原账户保证金已用额度大于该保证金可用余额");
                    return map;
                }else{
                    map.put("success",null);
                    return map;
                }
            }else{
                map.put("error","原账号查询不到资产信息");
                return map;
            }
        }else{
            map.put("error", returnMessageNew.getSysHead().get("RET.RET_MSG"));
            return map;
        }
    }*/
    /** 描述:判断保证金账户是否在核心开户
     * @param protocol  新签约信息
     * @return Map
     * @throws Exception
     */
    public Map queryAccountFromCoreSys(PedProtocolDto protocol) throws Exception {
        Map map = new HashMap();
        CoreTransNotes transNotes = new CoreTransNotes();
        transNotes.setAccNo(protocol.getMarginAccount());
        transNotes.setCurrentFlag("1");
        ReturnMessageNew returnMessageNew = poolCoreService.PJH716040Handler(transNotes, "1");
        if (returnMessageNew.isTxSuccess()) {
            map.put("success",null);
            return map;
        }else{
            map.put("error", returnMessageNew.getSysHead().get("RET.RET_MSG"));
            return map;
        }
    }
    
    public PoolEBankService getPoolEBankService() {
        return poolEBankService;
    }

    public void setPoolEBankService(PoolEBankService poolEBankService) {
        this.poolEBankService = poolEBankService;
    }

}
