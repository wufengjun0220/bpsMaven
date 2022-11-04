package com.mingtech.application.ecd.service;

import java.util.List;

import com.mingtech.application.ecd.domain.EndorsementLog;
import com.mingtech.framework.core.service.GenericService;

/**
 * <p>说明:票据历史行为信息接口</p>
 *
 * @author   张永超
 * @Date	 May 19, 2009 3:04:17 PM 
 */
public interface EndorsementLogService extends GenericService{

	/**
	 * getELogsByEId:(根据edraft表id获取票据历史)
	 * @author   张永超
	 * @param  @param id
	 * @param  @return
	 * @param  @throws Exception    
	 * @return List   
	 * @throws
	 */
	public List getELogsByEId(String id) throws Exception;
	
	/**
	 * getLastEnLogByEdraftNb:(根据票据号码获取最后一笔票据历史信息)
	 * @author   张永超
	 * @param  @param eDraftNb
	 * @param  @return    
	 * @return EndorsementLog   
	 * @throws
	 */
	public List<EndorsementLog> getLastEnLogByEdraftNb(String eDraftNb);

}

