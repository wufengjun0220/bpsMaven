/**
 * 
 */
package com.mingtech.application.pool.trust.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.service.impl.DraftPoolOutServiceImpl;
import com.mingtech.application.pool.trust.domain.DraftStorage;
import com.mingtech.application.pool.trust.service.DraftPoolTrustOutService;

/**
 * @author wbyecheng
 * <p>
 * 代保管取票业务服务实现
 * <P>
 * 由于代保管取票使用的两张业务操作表跟票据池出池一致，
 * 暂时采用继承，复用逻辑
 *
 */
@Service("draftPoolTrustOutService")
public class DraftPoolTrustOutServiceImpl extends DraftPoolOutServiceImpl
		implements DraftPoolTrustOutService {

	/**
	 * 取票申请驳回
	 * @param list
	 * @throws Exception
	 */
	public void txCancelFetchBill(List list) throws Exception {
		for(int i = 0; i < list.size(); i++){
			   DraftPoolOut poolout = (DraftPoolOut)this.load((String)list.get(i), DraftPoolOut.class);
			   this.txDelete(poolout);
			   DraftStorage draft = poolout.getDraftStroage();
			   draft.setPlStatus(PoolComm.DBG_YCP);//代保管已存票
			   this.txStore(draft);
		}
	}

	/**
	 * 获取托管池票据池,通过票号
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	@Override
	public DraftStorage getDraftStorageByDraftNb(String DraftNb) throws Exception{
		
		StringBuffer hql = new StringBuffer("select dto from DraftStorage dto where dto.plDraftNb=:plDraftNb and dto.plStatus=(:plStatus) ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		parasName.add("plDraftNb");
		parasValue.add(DraftNb);	
		List assetStatus = new ArrayList();
		assetStatus.add(PoolComm.DBG_YCP);
		parasName.add("plStatus");
		parasValue.add(assetStatus);	
		
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
