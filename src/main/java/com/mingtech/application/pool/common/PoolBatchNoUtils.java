package com.mingtech.application.pool.common;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.domain.BatchSequenceDto;
import com.mingtech.application.ecds.common.domain.FlowNoDto;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.DAOException;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 
 * 汉口银行批次号生成工具
 * @author Ju Nana
 * @date 2018-11-20
 */
@Service
public class PoolBatchNoUtils extends GenericServiceImpl{
	private Logger logger = Logger.getLogger(PoolBatchNoUtils.class);
	private static PoolBatchNoUtils instance;

	public static PoolBatchNoUtils getInstance(){
		if(instance == null){
			instance = new PoolBatchNoUtils();
		}
		return instance;
	}

	public Class getEntityClass(){
		return BatchSequenceDto.class;
	}

	public String getEntityName(){
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
	 * 
	 * 批次号生成方法
	 * @param prefix 设置前缀
	 * @param length 后续位数  
	 * @author Ju Nana
	 * @date 2018-11-20 下午4:02:08
	 */
	public synchronized String txGetBatchNo(String prefix,int length ) throws Exception{
		synchronized (this) {
			
//			String batchNoNotContain4 ="";//不含4的编号，因客户对4的敏感性，对票据池中相关编号中的4改为F代替 
			String sSeq = "0";
			StringBuffer batchNo = new StringBuffer(prefix);
			String curDate = DateUtils.dtuGetCurDatTimStr().substring(2, 8);/* 首先获得当前系统日期 */
			batchNo.append(curDate);
			BatchSequenceDto dto = null;
			try {
				/* 根据系统日期查询序列 */
				dto = (BatchSequenceDto) this.load(curDate,BatchSequenceDto.class, LockMode.UPGRADE);
				sSeq = dto.getSSeq();
			} catch (Exception e) {
				dto = new BatchSequenceDto();
				dto.setBatchId(curDate);
			}
			sSeq = Integer.parseInt(sSeq) + 1 + "";
			if (sSeq.length() < length) {
				for (int i = 0; i < length - sSeq.length(); i++) {
					batchNo.append("0");
				}
				batchNo.append(sSeq);
				dto.setSDate(curDate);/* 保存并更新序列对象 */
				dto.setSSeq(sSeq);
				this.txStore(dto);
//				batchNoNotContain4 = batchNo.toString().replace("4", "F");
				logger.info("生成协议号为:~" + batchNo.toString());
				return batchNo.toString();
			} else {
				throw new Exception("错误：序号长度超过规定上限！");
			}
		}
	}
	public synchronized String txGetBatchNoBySession(String prefix,int length) throws DAOException{
		GenericHibernateDao dao = (GenericHibernateDao)this.getDao();
		Session session = null;
		String fSeq = "0";
		/* 设置后续位数 */
		StringBuffer guarNo = new StringBuffer(prefix);
		try{
			session = dao.getHibernateTemplate().getSessionFactory().openSession();
			//查询核心序列对象
			/* 首先获得当前系统日期 */
			String curDate = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
			Query query = session.createQuery("from BatchSequenceDto as seq where seq.SDate = '" + curDate + "'");
			List sequences = query.list();
			
			//如果还不存在该类型的流水记录,新建
			BatchSequenceDto dto=null;
			//是否重置流水序号表的按纸票日期判断 2011-04-14
			if(sequences == null || sequences.size() == 0){
				dto = new BatchSequenceDto();
				dto.setBatchId(curDate);
				dto.setSDate(curDate);
			}else{
				dto = (BatchSequenceDto)sequences.get(0);
				fSeq = dto.getSSeq();
			}
			fSeq = getNextSeq(fSeq,length);
			//将流水号加1
			dto.setSSeq(fSeq);
			session.save(dto);
			session.flush();
			guarNo.append(curDate.substring(2, 8));
			guarNo.append(fSeq);
			logger.info("生成票据池编号为:~~"+guarNo.toString());
			return guarNo.toString();
		}catch (HibernateException e){
			logger.error("获取票据池编号失败!",e);
			throw e;
		}finally{
			if(session != null)
				session.close();
		}
	}
	public synchronized String txGetBatchNoBySessionNew(String prefix,int length) throws DAOException{
		GenericHibernateDao dao = (GenericHibernateDao)this.getDao();
		Session session = null;
		String fSeq = "0";
		/* 设置后续位数 */ 
		StringBuffer guarNo = new StringBuffer(prefix);
		try{
			session = dao.getHibernateTemplate().getSessionFactory().openSession();
			//查询核心序列对象
			/* 首先获得当前系统日期 */
			String curDate = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
			Query query = session.createQuery("from BatchSequenceDto as seq where seq.SDate = '" + curDate + "'");
			List sequences = query.list();
			
			//如果还不存在该类型的流水记录,新建
			BatchSequenceDto dto=null;
			//是否重置流水序号表的按纸票日期判断 2011-04-14
			if(sequences == null || sequences.size() == 0){
				dto = new BatchSequenceDto();
				dto.setBatchId(curDate);
				dto.setSDate(curDate);
			}else{
				dto = (BatchSequenceDto)sequences.get(0);
				fSeq = dto.getSSeq();
			}
			fSeq = getNextSeq(fSeq,length);
			//将流水号加1
			dto.setSSeq(fSeq);
			session.save(dto);
			session.flush();
			String code=null;
			for (int i=1;i<999999 ;i++ ){    //for循环
				code=i+"";
				int leng=(code.trim()).length();  //定义长度
				if(leng==1){
				code="00000"+i;
				}else if(leng==2){
				code="0000"+i;
				}else if(leng==3){
				code="000"+i;
				}else if(leng==4){
				code="00"+i;
				}else if(leng==5){
				code="0"+i;
				}
			}
			guarNo.append(curDate.substring(2, 8));
			guarNo.append(fSeq);
			guarNo.append(code);
			logger.info("生成票据池编号为:~~"+guarNo.toString());
			return guarNo.toString();
		}catch (HibernateException e){
			logger.error("获取票据池编号失败!",e);
			throw e;
		}finally{
			if(session != null)
				session.close();
		}
	}

	/**
	 * 电票生成担保品编号
	 * @param prefix
	 * @param length
	 * @return
	 * @throws DAOException
	 */
	public synchronized String txGetCuarNoBySession(String prefix,int length) throws DAOException{
		GenericHibernateDao dao = (GenericHibernateDao)this.getDao();
		Session session = null;
		String fSeq = "0";
		/* 设置后续位数 */
		StringBuffer guarNo = new StringBuffer(prefix);
		try{
			session = dao.getHibernateTemplate().getSessionFactory().openSession();
			//查询核心序列对象
			/* 首先获得当前系统日期 */
			String curDate = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
			Query query = session.createQuery("from BatchSequenceDto as seq where seq.SDate = '" + curDate + "'");
			List sequences = query.list();
			
			//如果还不存在该类型的流水记录,新建
			BatchSequenceDto dto=null;
			//是否重置流水序号表的按纸票日期判断 2011-04-14
			if(sequences == null || sequences.size() == 0){
				dto = new BatchSequenceDto();
				dto.setBatchId(curDate);
				dto.setSDate(curDate);
			}else{
				dto = (BatchSequenceDto)sequences.get(0);
				fSeq = dto.getSSeq();
			}
			fSeq = getNextSeq(fSeq,length);
			//将流水号加1
			dto.setSSeq(fSeq);
			session.save(dto);
			session.flush();
			guarNo.append(curDate);
			guarNo.append("ZE");
			guarNo.append(fSeq);
			logger.info("生成担保品编号为:~~"+guarNo.toString());
			return guarNo.toString();
		}catch (HibernateException e){
			logger.error("获取担保品编号失败!",e);
			throw e;
		}finally{
			if(session != null)
				session.close();
		}
	}

	/**
	 * 纸票生成担保品编号
	 * @param prefix
	 * @param length
	 * @return
	 * @throws DAOException
	 */
	public synchronized String txGetPoperCuarNoBySession(String prefix,int length) throws DAOException{
		GenericHibernateDao dao = (GenericHibernateDao)this.getDao();
		Session session = null;
		String fSeq = "0";
		/* 设置后续位数 */
		StringBuffer guarNo = new StringBuffer(prefix);
		try{
			session = dao.getHibernateTemplate().getSessionFactory().openSession();
			//查询核心序列对象
			/* 首先获得当前系统日期 */
			String curDate = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
			Query query = session.createQuery("from BatchSequenceDto as seq where seq.SDate = '" + curDate + "'");
			List sequences = query.list();

			//如果还不存在该类型的流水记录,新建
			BatchSequenceDto dto=null;
			//是否重置流水序号表的按纸票日期判断 2011-04-14
			if(sequences == null || sequences.size() == 0){
				dto = new BatchSequenceDto();
				dto.setBatchId(curDate);
				dto.setSDate(curDate);
			}else{
				dto = (BatchSequenceDto)sequences.get(0);
				fSeq = dto.getSSeq();
			}
			fSeq = getNextSeq(fSeq,length);
			//将流水号加1
			dto.setSSeq(fSeq);
			session.save(dto);
			session.flush();
			guarNo.append(curDate);
			guarNo.append("ZP");
			guarNo.append(fSeq);
			logger.info("生成担保品编号为:~~"+guarNo.toString());
			return guarNo.toString();
		}catch (HibernateException e){
			logger.error("获取担保品编号失败!",e);
			throw e;
		}finally{
			if(session != null)
				session.close();
		}
	}
	public synchronized String txGetFlowNo() throws DAOException{
		GenericHibernateDao dao = (GenericHibernateDao)this.getDao();
		Session session = null;
		/* 设置前辍 */
		String prefix="";
		String fSeq = "0";
		/* 设置后续位数 */
		StringBuffer flowNo = new StringBuffer(prefix);
		try{
			session = dao.getHibernateTemplate().getSessionFactory().openSession();
			//查询核心序列对象
			/* 首先获得当前系统日期 */
			String curDate = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
			Query query = session.createQuery("from FlowNoDto as seq where seq.SDate = '" + curDate + "'");
			List sequences = query.list();
			
			//如果还不存在该类型的流水记录,新建
			FlowNoDto dto=null;
			//是否重置流水序号表的按纸票日期判断 2011-04-14
			if(sequences == null || sequences.size() == 0){
				dto = new FlowNoDto();
				dto.setFlowId(curDate);
				dto.setSDate(curDate);
			}else{
				dto = (FlowNoDto)sequences.get(0);
				fSeq = dto.getSSeq();
			}
			fSeq = getNextSeq(fSeq, 4);
			//将流水号加1
			dto.setSSeq(fSeq);
			session.save(dto);
			session.flush();
			flowNo.append(curDate.substring(4, 8));
			flowNo.append(fSeq);
			logger.info("PoolBatchNoUtils生成流水号为~~~~~~~~~~~~~~~~~~~"+flowNo.toString());
			return flowNo.toString();
		}catch (HibernateException e){
			logger.error("获取核心序列号失败!",e);
			throw e;
		}finally{
			if(session != null)
				session.close();
		}
	}
	//获取下一位流水号
	public String getNextSeq(String number,int length){
		StringBuffer pattern = new StringBuffer("");
		for (int i = 0; i < length; i++) {
			pattern.append("0");
		}
		DecimalFormat df = new DecimalFormat(pattern.toString());
		int seq = Integer.parseInt(number)+1;
		return df.format(seq);
	}

	/**
	 * @description 借据号
	 * @author wss
	 * @date 2021-5-11
	 * @param prefix
	 * @param length
	 */
	public synchronized String txGetIOUNo(String prefix,int length ) throws Exception{
		synchronized (this) {
			
			String sSeq = "0000000";
			StringBuffer batchNo = new StringBuffer(prefix);
			String curDate = DateUtils.dtuGetCurDatTimStr().substring(2, 8);/* 首先获得当前系统日期 */
			batchNo.append(curDate);
			BatchSequenceDto dto = null;
			try {
				/* 根据系统日期查询序列 */
				dto = (BatchSequenceDto) this.load(prefix+"IOU"+curDate,BatchSequenceDto.class, LockMode.UPGRADE);
				sSeq = dto.getSSeq();
			} catch (Exception e) {
				dto = new BatchSequenceDto();
				dto.setBatchId(prefix+"IOU"+curDate);
			}
			sSeq = Integer.parseInt(sSeq) + 1 + "";
			if (sSeq.length() < length) {
				for (int i = 0; i < length - sSeq.length(); i++) {
					batchNo.append("0");
				}
				batchNo.append(sSeq);
				dto.setSDate(curDate);/* 保存并更新序列对象 */
				dto.setSSeq(sSeq);
				this.txStore(dto);
				logger.info("生成借据号为:~" + batchNo.toString());
				return batchNo.toString();
			} else {
				throw new Exception("错误：序号长度超过规定上限！");
			}
		}
	}

}
