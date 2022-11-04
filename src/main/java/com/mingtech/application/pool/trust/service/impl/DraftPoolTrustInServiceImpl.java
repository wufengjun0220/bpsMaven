/**
 * 
 */
package com.mingtech.application.pool.trust.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.draft.domain.DraftPoolInBatch;
import com.mingtech.application.pool.draft.service.impl.DraftPoolInServiceImpl;
import com.mingtech.application.pool.trust.domain.DraftStorage;
import com.mingtech.application.pool.trust.service.DraftPoolTrustInService;

/**
 * @author wbyecheng
 * <p>
 * 代保管存票业务服务实现
 * <P>
 * 由于代保管取票使用的两张业务操作表跟票据池入池一致，
 * 暂时采用继承，复用逻辑
 *
 */
@Service("draftPoolTrustInService")
public class DraftPoolTrustInServiceImpl extends DraftPoolInServiceImpl
		implements DraftPoolTrustInService {
	
	@Override
	public DraftPoolInBatch load(String id) {
		return (DraftPoolInBatch) super.load(id);
	}

	/**
	 * 获取托管池票据池,通过票号
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	@Override
	public DraftStorage getDraftStorageByDraftNb(String DraftNb) throws Exception{
		
		StringBuffer hql = new StringBuffer("select dto from DraftStorage dto where dto.plDraftNb=:plDraftNb ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		parasName.add("plDraftNb");
		parasValue.add(DraftNb);	
		
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		if(list!=null && list.size() == 1){
			return (DraftStorage) list.get(0);
		}else{
			return null;
		}
	}
}
