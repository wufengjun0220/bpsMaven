package com.mingtech.application.sysmanage.service;

import java.io.File;
import java.util.List;

import com.mingtech.application.sysmanage.domain.Inform;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;


public interface InformService {
	/**
	 * 查询当前公告
	 */
	public String viewInformList(String beginDate,String endDate ,String keyStr ,Page page ,User user)throws Exception;
	
	/**
	 * 保存对象
	 */
	public void txStoreDto(Object object)throws Exception;
	/**
	 * 删除对象
	 */
	public void txDeleDto(Object object)throws Exception;
	
	/**
	 * 更新对象
	 */
	public void txUpdateDto(Object object)throws Exception;
	/**
	 * 保存上传附件
	 * @param inform
	 * @param file
	 */
	public String uploadAttach(Inform inform ,File file ,String fileName)throws Exception;
	/**
	 * 查询用户主页显示出的公告
	 * @param deptId
	 * @return
	 */
	public List getUserInformInMainPage(String deptId);
	
	/**
	 * 查询所有公告
	 * @param deptId
	 * @return
	 */
	public List getUserInformList(String beginDate,String endDate ,String keyStr,Page page,String deptId)throws Exception;
	
	/**
	 * 根据ID删除对象
	 */
	public void txDeleInformByIds(String ids)throws Exception;
	/**
	 * 根据ID获取公告
	 * @param ids
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List getInformByids(String ids,Page page) throws Exception;
	
	/**
	 * 根据ID获取公告
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Inform loadInformByPrimaryKey(String id) throws Exception;
	/**
	 * 获取当前最高显示级别
	 * @return
	 */
	public int getCurLevel();
	/**
	 * 获取当前放置首页的公告
	 * @return
	 */
	public List getTopList();
	
}
