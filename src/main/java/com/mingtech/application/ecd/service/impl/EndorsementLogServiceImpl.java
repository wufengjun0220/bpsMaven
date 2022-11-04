package com.mingtech.application.ecd.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecd.domain.EndorsementLog;
import com.mingtech.application.ecd.service.EndorsementLogService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>说明:历史背书、行为信息</p>
 *
 * @author   张永超
 * @Date	 May 19, 2009 3:08:03 PM
 */
@Service("endorsementLogService")
public class EndorsementLogServiceImpl extends GenericServiceImpl implements EndorsementLogService{

	private static final Logger logger = Logger.getLogger(EndorsementLogServiceImpl.class);
	
	public Class getEntityClass() {
		return EndorsementLog.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}

	public List getELogsByEId(String id) throws Exception {
		StringBuffer sb = new StringBuffer();
		List params = new ArrayList();
		sb.append("from EndorsementLog as el where el.ownerEDraftId = ? and el.msgTpId = ? ");
		sb.append(" order by el.count desc");
		params.add(id);
		params.add("201002");
		List list = this.find(sb.toString(),params);
		if(list!=null&&list.size()>0){
			return list;
		}
		return null;
	}

	public List<EndorsementLog> getLastEnLogByEdraftNb(String eDraftNb){
		StringBuffer sb = new StringBuffer();
		List params = new ArrayList();
		sb.append("select el from EndorsementLog as el where el.ownerEDraft =?");
		sb.append(" order by el.count desc");
		params.add(eDraftNb);
		List list = this.find(sb.toString(),params);
		if(list != null && list.size() > 0 ){
			return list;
		}
		return null;
	}

	public List getLogsByEdraftNbMsgTpId(String eDraftNb, String msgTpId) {
		StringBuffer sb = new StringBuffer();
		List params = new ArrayList();
		if(null != msgTpId && msgTpId.trim().length() !=0){//如果msgTpId不为空
			sb.append("select el from EndorsementLog as el where el.ownerEDraft =?"+" and el.msgTpId=?");
			params.add(eDraftNb);
			params.add(msgTpId);
		}else{//如果为空则查询所有交易类型
			sb.append("select el from EndorsementLog as el where el.ownerEDraft = ?");
			params.add(eDraftNb);
		}
		sb.append(" order by el.count desc");
		List list = this.find(sb.toString(),params);
		return list;
	}
	public boolean txSaveEndorsementLog(EndorsementLog el) {
		try {
			el.setCount(this.getNextSeqByEdraftNb(el.getOwnerEDraft()));
			this.txStore(el);
		} catch (RuntimeException e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		return true;

	}

	/**
	 * getNextSeqByEdraftNb:(按票据号码获取下一个背书记录序号)
	 * @param  @param eDraftNb
	 * @param  @return
	 * @return int
	 * @throws
	 */
	private int getNextSeqByEdraftNb(String eDraftNb) {
		StringBuffer sb = new StringBuffer();
		sb.append("select el.count from EndorsementLog as el where el.ownerEDraft =?");
		sb.append(" order by el.count desc");

		List params = new ArrayList();
		params.add(eDraftNb);
		List list = this.find(sb.toString(),params);
		if(list.size()>0){
			int t = ((Integer)list.get(0)).intValue();
			return t+1;
		}else{
			return 0;
		}

	}

}

