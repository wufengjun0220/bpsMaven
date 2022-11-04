package com.mingtech.application.pool.financial.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
@Service("financialAdviceService")
public class FinancialAdviceServiceImpl extends GenericServiceImpl implements FinancialAdviceService {
    private static final Logger logger = Logger.getLogger(FinancialAdviceServiceImpl.class);
    @Autowired
    private AssetRegisterService registerService;
    @Override
    public void txCreateList(List list) throws Exception {
        dao.storeAll(list);
    }

    @Override
    public void txDelList(List list) throws Exception {
        dao.deleteAll(list);
    }
    @Override
    public String getEntityName() {
        return null;
    }

    @Override
    public Class getEntityClass() {
        return null;
    }

	@Override
	public void txForcedSaveList(List list) throws Exception {
		if(null != list && list.size()>0){
			for(Object obj : list){
				dao.saveEntity(obj);
			}
		}
	}
}
