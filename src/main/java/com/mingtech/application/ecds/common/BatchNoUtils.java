package com.mingtech.application.ecds.common;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.dao.DataAccessException;
import com.mingtech.application.ecds.common.domain.BatchSequenceDto;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p> 版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: ChenFuLing
 * @日期: Jun 28, 2009 11:52:47 PM
 * @描述: [BatchNoUtils]批次号工具
 */
public class BatchNoUtils extends GenericServiceImpl{
	private Logger logger = Logger.getLogger(BatchNoUtils.class);
	private static BatchNoUtils instance;

	public static BatchNoUtils getInstance(){
		if(instance == null){
			instance = new BatchNoUtils();
		}
		return instance;
	}

	public Class getEntityClass(){
		// TODO Auto-generated method stub
		return BatchSequenceDto.class;
	}

	public String getEntityName(){
		// TODO Auto-generated method stub
		return StringUtil.getClass(BatchSequenceDto.class);
	}

	/**
	 * 根据日期查询对应序号
	 */
	public BatchSequenceDto getSeqNoByCurDate(String curDate) throws Exception{
		List param = new ArrayList();
		String hql = "select dto from BatchSequenceDto as dto where dto.SDate=?";
		param.add(curDate);
		List list = this.find(hql, param);
		BatchSequenceDto dto = list != null && list.size() > 0 ? ((BatchSequenceDto) list
				.get(0))
				: new BatchSequenceDto();
		return dto;
	}

	/**
	 * 批次号生成器20091017于飞修改增加了事物并发引起的批次号相同的问题，使用了LockMode.UPGRADE属性，前提是必须使用
	 * load方法进行查询.
	 */
	public synchronized String txGetBatchNo() throws Exception{
		/* 设置前辍 */
		String prefix = "";
		/* 设置后续位数 */
		int length = 4;
		String sSeq = "0";
		StringBuffer batchNo = new StringBuffer(prefix);
		/* 首先获得当前系统日期 */
		String curDate = DateUtils.dtuGetCurDatTimStr().substring(4, 8);
		batchNo.append(curDate);
		BatchSequenceDto dto = null;
		try{
			/* 根据系统日期查询序列 */
			dto = (BatchSequenceDto) this.load(curDate, BatchSequenceDto.class,
					LockMode.UPGRADE);
			sSeq = dto.getSSeq();
		}catch (DataAccessException e){
			dto = new BatchSequenceDto();
			dto.setBatchId(curDate);
		}
		sSeq = Integer.parseInt(sSeq) + 1 + "";
		if(sSeq.length() < length){
			for(int i = 0; i < length - sSeq.length(); i++){
				batchNo.append("0");
			}
			batchNo.append(sSeq);
			/* 保存并更新序列对象 */
			dto.setSDate(curDate);
			dto.setSSeq(sSeq);
			this.txStore(dto);
			logger.info("生成批次号为~~~~~~~~~~~~~~"+batchNo.toString());
			return batchNo.toString();
		}else{
			throw new Exception("错误：序号长度超过规定上限！");
		}
	}

	public static void main(String[] args) throws Exception{
		BatchNoUtils bn = (BatchNoUtils) SpringContextUtil
				.getBean("batchNoUtils");
		String no = bn.txGetBatchNo();
	}
}
