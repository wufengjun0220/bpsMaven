package com.mingtech.application.pool.draft.service;

import java.util.List;

import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.MisEdraft;
import com.mingtech.application.pool.draft.domain.MisPdraft;
import com.mingtech.framework.core.service.GenericService;

/**
 * MIS系统数据处理服务
 * @author Ju Nana
 * @date 2019-1-16
 */
public interface MisDraftService extends GenericService{
	
	/**
	 * 根据客户号列表查询查询MIS签发已记账的电票信息
	 * @param custNos 客户号列表
	 * @param @return   
	 * @return List<MisEdraft>  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-1-17 下午2:21:38
	 */
	public List<MisEdraft> queryMisEdraftByParam(List<String> custNos);
	
	/**
	 *  根据客户号列表查询查询MIS签发已记账的纸票信息
	 * @param custNos
	 * @param @return   
	 * @return List<MisPdraft>  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-1-17 下午3:55:10
	 */
	public List<MisPdraft> queryMisPdraftByParam(List<String> custNos);
	
	/**
	 * 更新来自于mis系统签发已记账的纸票
	 * @param credit
	 * @throws Exception
	 */
	public void txUpdateMisPdraft(List<String> billNos) throws Exception;
	
	/**
	 * 更新来自于mis系统签发已记账的电票
	 * @param credit
	 * @throws Exception
	 */
	public void txUpdateMisEdraft(List<String> billNos) throws Exception;
	
	/**
	 * mis保贴额度释放
	 * @param draft 
	 * @throws
	 * @author Ju Nana
	 * @date 2019-8-17 下午2:21:38
	 */
	public Ret releaseMisEdu(DraftPool draft) throws Exception;
}
