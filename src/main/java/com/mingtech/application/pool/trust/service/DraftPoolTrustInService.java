/**
 * 
 */
package com.mingtech.application.pool.trust.service;

import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.trust.domain.DraftStorage;

/**
 * @author wbyecheng
 * <p>
 * 代保管存票业务服务
 * <P>
 * 由于代保管取票使用的两张业务操作表跟票据池入池一致，
 * 暂时采用继承，复用逻辑
 *
 */
public interface DraftPoolTrustInService extends DraftPoolInService {

	/**
	 * 获取托管池票据池,通过票号
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	public DraftStorage getDraftStorageByDraftNb(String DraftNb) throws Exception;
}
