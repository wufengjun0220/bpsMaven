package com.mingtech.application.common.logic;
import java.util.List;

import org.json.JSONException;

import com.mingtech.application.common.domain.Dictionary;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.core.dao.DAOException;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


/**
 * 通用数据字典Service接口
 * @author huangshiqiang
 * @date Dec 28, 2007
 * @comment
 */
public interface IDictionaryService extends GenericService {

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 * @throws DAOException
	 * @author huangshiqiang
	 * @date Dec 28, 2007
	 * @comment
	 */
	public Dictionary getDictionary(String id)throws DAOException;


	/**
	 * 根据id删除
	 * @param 唯一编码
	 * @return
	 * @throws DAOException
	 * @author huangshiqiang
	 * @date Dec 28, 2007
	 * @comment
	 */
	public boolean txDelete(String distinctCode) throws DAOException;


	/**
	 * 更加字典的唯一编码项来加载字典项
	 * @param code
	 * @return
	 * @throws DAOException
	 * @author huangshiqiang
	 * @date Jan 2, 2008
	 * @comment
	 */
	public Dictionary getDictionaryByCode(String code)throws DAOException;


	/**
	 * 根据父节点加载子项
	 * @param parentCode 父节点
	 * @param status status=1 只加载当前记录 status=0 只加载历史记录 status<0 加载当前和历史记录
	 * @return
	 * @throws DAOException
	 * @author huangshiqiang
	 * @date Jan 5, 2008
	 * @comment
	 */
	public List getDictionaryByParentCode(String parentCode,int status) throws DAOException;
	/**
	 * 根据父节点下所有子项（包括子项的子项）
	 * @param parentCode 父节点
	 * @param status status=1 只加载当前记录 status=0 只加载历史记录 status<0 加载当前和历史记录
	 * @return
	 * @throws DAOException
	 * @author huangshiqiang
	 * @date Jan 5, 2008
	 * @comment
	 */
	public List getAllDictionaryByParentCode(String parentCode,int status) throws DAOException;


	public List queryAllDictionary();

	/**
	* 方法说明: 分页获取所有字典JSON串
	* @param  dic 字典实体
	* @param  page 分页实体
	*
	* @author  ZJY
	* @return
	* @date 20190603
	*/
	public List queryDictionaryList(Dictionary dictionary, Page page)throws Exception;
	/**
	 * 方法说明: 查询下级字典的下一个序号
	 * @param  parentCode 父字典编码
	 */
	public int queryNextLevelOfCHildDic(String parentCode)throws Exception;
}
