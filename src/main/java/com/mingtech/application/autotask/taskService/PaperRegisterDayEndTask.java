package com.mingtech.application.autotask.taskService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.CollectionDto;
import com.mingtech.application.ecds.draftcollection.domain.CollectionQueryBean;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 日终纸票登记服务
 * @author Administrator
 *
 */
public class PaperRegisterDayEndTask extends AbstractAutoTask{

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		//当前系统工作日
		Date wkDate = DateUtils.getWorkDayDate();
		//承兑出票登记
		this.registerForAcpt(wkDate);
		//贴现登记
		this.registerForDisc(wkDate);
		//转贴现买入
		this.registerForRebuyin(wkDate);
		//发出托收登记
		this.registerForCollection(wkDate);
		
		return new BooleanAutoTaskResult(true);
	}
	/**
	 * 承兑出票 纸票登记
	 * @param wkDate
	 */
	private void registerForAcpt(Date wkDate){
		/*PcdsAcceptanceService pcdsAcptService =PcdsAcceptanceServiceFactory.getPcdsAcceptanceService();
		List status = new ArrayList();
		status.add(PublicStaticDefineTab.CD108);//记账完毕
		AcceptionQueryBean acctQueryBean = new AcceptionQueryBean();
		acctQueryBean.setAcctDate(wkDate);
		try {
			List list = pcdsAcptService.queryAcceptBillListByStatus(null,status, acctQueryBean, null);
			for(int i=0;i<list.size();i++){
				AcceptionDto dto = (AcceptionDto)list.get(i);
				pcdsAcptService.txAcceptanceRegisterPcds(dto, wkDate);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}*/
	}
	/**
	 * 托收  登记
	 * @param wkDate
	 */
	private void registerForCollection(Date wkDate){
		/*PdcsDraftCollectionService pdcsDraftCollectionService = PdcsDraftCollectionServiceFactory.getPdcsDraftCollectionService();
		List status = new ArrayList();
		status.add(PublicStaticDefineTab.PTS002);//发出托收
		CollectionQueryBean collQueyBean = new CollectionQueryBean();
		collQueyBean.setDCollectDt(wkDate);//托收日期
		try{
			List list = pdcsDraftCollectionService.queryPdcsDraftCollectionInfoList(collQueyBean, status,null, null);
			for(int i=0;i<list.size();i++){
				CollectionDto info  = (CollectionDto)list.get(i);
				pdcsDraftCollectionService.txStorePaperDarft(info, wkDate);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}*/
		
		
	}
	/**
	 * 转贴现买入 纸票登记
	 * @param wkDate
	 */
	private void registerForRebuyin(Date wkDate){
		/*PcdsReDiscountBuyService pcdsReDiscountBuyService = PcdsReDiscountBuyServiceFactory.getPcdsReDiscountBuyService();
		//201612以清单状态为准查询
		List status = new ArrayList();
		status.add(PublicStaticDefineTab.PTX012);//记账完毕
		RediscountBuyInQueryBean	buyinQueryBean = new RediscountBuyInQueryBean();
		buyinQueryBean.setIfSystemInner("02");//同业
		buyinQueryBean.setAcctDate(wkDate);//只能查当日记账的业务
		try {
			List result = pcdsReDiscountBuyService.queryBillListByBillStatus(status, buyinQueryBean, null, null);
			for(int i=0;i<result.size();i++){
				RediscountBuyInDto info = (RediscountBuyInDto)result.get(i);
				pcdsReDiscountBuyService.txRedisountRegister(info, wkDate);
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}*/
	}
	/**
	 * 贴现数据 纸票登记
	 * @param wkDate
	 */
	private void registerForDisc(Date wkDate){
		/*DiscountBatchDtoPcdsService service = DiscountPcdsServiceFactory.getDiscountBatchDtoPcdsService();
		List stats = new ArrayList();
		stats.add(PublicStaticDefineTab.PTX012);// 记账完毕
		DiscountQueryBean discQueryBean = new  DiscountQueryBean();
		discQueryBean.setAcctDate(wkDate);//设置查询记账日期为当前系统工作日
		try {
			//查询 当日 贴现记账的数据
			List discList = service.queryDiscBillList(stats, null, discQueryBean, null);
			for(int i=0;i<discList.size();i++){
				DiscountDto info = (DiscountDto)discList.get(i);
				//纸票登记
				service.txDisountRegisterPcds(info,wkDate);
			}
		} catch (Exception e) {
			
			logger.error(e.getMessage(),e);
		}*/
	}
	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

}
