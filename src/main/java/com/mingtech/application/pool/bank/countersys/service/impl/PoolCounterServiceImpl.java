package com.mingtech.application.pool.bank.countersys.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.countersys.service.PoolCounterService;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("poolCounterService")
public class PoolCounterServiceImpl extends GenericServiceImpl implements PoolCounterService{

	@Override
	public boolean isUsedEdu(String ORGCODE) throws Exception {
		boolean flag  = false;
		String sql = "select as from AssetType as at where at.apId =(select ap.id from AssetPool as ap where ap.custNo=?)";
		List param = new ArrayList();
		param.add(ORGCODE);
		List result = this.find(sql, param);
		for (int i = 0; i < result.size(); i++) {
			AssetType at = (AssetType) result.get(i);
			if(at.getCrdtUsed().equals(BigDecimal.ZERO)){
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssetType getTypebyCode(String ORGCODE) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
