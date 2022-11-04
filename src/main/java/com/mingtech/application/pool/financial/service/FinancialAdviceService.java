package com.mingtech.application.pool.financial.service;

import java.util.List;

import com.mingtech.framework.core.service.GenericService;

/**
 * 资产及额度处理独立事务
 * @author zjt
 * @version v1.0
 * @date 2021-6-10
 * @copyright 北明明润（北京）科技有限责任公司
 */
public interface FinancialAdviceService extends GenericService {

    /**
     * <p>描述：独立事务-保存对象集合使用</p>
     * @param list list对象集合
     *
     */
    public void txCreateList(List list) throws Exception;

    /**
     * <p>描述：独立事务-删除对象集合使用</p>
     * @param list list对象集合
     *
     */
    public void txDelList(List list) throws Exception;
    
    /**
     * 强制性保存--独立事务保存，该方法用于需要先生成ID的特殊情况
     * @param list
     * @throws Exception
     * @author Ju Nana
     * @date 2021-7-19上午9:21:54
     */
    public void txForcedSaveList(List list) throws Exception;

}
