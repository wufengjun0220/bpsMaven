package com.mingtech.application.autotask.taskService;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.mingtech.framework.common.jdbcHelper.DBUtil;
import com.mingtech.framework.common.jdbcHelper.ISqlHandler;
import com.mingtech.framework.common.jdbcHelper.SqlHandler;
import com.mingtech.framework.common.util.ConnectionUtils;
/**
 * 
 * 日终余额 数据抽取  服务
 * 可以持续扩展：承兑余额、月报表  等等
 *
 */
public class BalanceOlapDataInphase {
	//定义日志处理
	private static final Logger log = Logger.getLogger(BalanceOlapDataInphase.class);
	//数据库连接
	private Connection conn;
	//定义实例
	private static BalanceOlapDataInphase odi = null;
	//保证数据抽取是唯一的实例
	public static BalanceOlapDataInphase getInstance(){
		if(odi == null) {
			odi = new BalanceOlapDataInphase();
		}
		return odi;
	}

	/**
	 * 抽取贴现和转贴余额数据
	 * 
	 * */
	public void isolateBalanceListInphase()throws Exception{
		log.info("************贴现转贴现余额明细抽取开始**************");
		/**
		 * balance_flag 
		 * 余额标记：1贴现余额 2同业间回购式卖出余额
		 * 3卖出回购式再贴现余额 4卖断销账
		 * 5返售到期销账 6托收在途余额 7托收销账  
		 * billBuyMode
	 	 * 买入类型：01直贴02买断转贴03买入返售转贴04双买05系统内买断
		 */
		
		//删除当日提取余额明细数据
		String delSql = "delete from BO_BUSI_BALANCE_RECORD where BALANCE_DT = (select WORK_DATE from cd_runstate) ";
		this.updateData(delSql);
		//插入贴现余额明细
		String insertDiscListSql ="insert into BO_BUSI_BALANCE_RECORD (id,BRCH_ID,BRCH_NAME,ACCT_BRCH_NAME,S_BILL_NO,S_BILL_TYPE,D_ISSUE_DT" +
				",D_DUE_DT,F_BILL_AMOUNT,PRODUCT_ID,S_ACCEPTOR,S_ISSUER_NAME,S_ISSUER_BANK_CODE, " +
				"CUST_NAME,BUSI_DT,GALE_DT,F_INT_DAYS,F_RATE,S_RATE_TYPE,F_PAYMENT,D_Interest" +
				",BUSI_ID,BILLINFO_ID,BALANCE_DT,BALANCE_FLAG,BILL_BUY_MODE) " ;
		insertDiscListSql+=" select BALANCE_RECORD_SEQ.nextval,bill.BALANCE_BRCH_ID,dept.d_name,dept.d_name,info.S_BILL_NO,info.S_BILL_TYPE,info.D_ISSUE_DT" +
		",info.D_DUE_DT,info.F_BILL_AMOUNT,info.PRODUCT_ID,info.S_ACCEPTOR,bill.ed_drwrNm,bill.ed_accptrsvcr " +
		",info.S_OUT_NAME,info.DISC_BATCH_DT,info.GALE_DT,info.F_INT_DAYS,info.F_RATE,info.S_RATE_TYPE,info.F_BUY_PAYMENT,info.F_INT" +
		",info.DISC_BILL_ID,info.BILLINFO_ID,(select WORK_DATE from cd_runstate),bill.balance_flag,bill.billBuyMode" +
		" from BT_DISCOUNT info,BT_DISCOUNT_BATCH batch,cd_edraft bill,t_department dept " +
		" where info.DISC_BATCH_ID=batch.DISC_BATCH_ID and bill.LAST_SOURCE_ID=info.DISC_BILL_ID " +
		" and bill.BALANCE_BRCH_ID=dept.id " +
		" and bill.balance_flag in (1,2,3,6) and bill.billBuyMode in ('01') ";
		
		this.updateData(insertDiscListSql);
		
		String insertRebuyListSql="insert into BO_BUSI_BALANCE_RECORD (id,BRCH_ID,BRCH_NAME,ACCT_BRCH_NAME,S_BILL_NO,S_BILL_TYPE,D_ISSUE_DT" +
		",D_DUE_DT,F_BILL_AMOUNT,PRODUCT_ID,S_ACCEPTOR,S_ISSUER_NAME,S_ISSUER_BANK_CODE, " +
		"CUST_NAME,BUSI_DT,GALE_DT,F_INT_DAYS,F_RATE,S_RATE_TYPE,F_PAYMENT,D_Interest" +
		",BUSI_ID,BILLINFO_ID,BALANCE_DT,BALANCE_FLAG,BILL_BUY_MODE) " ;
		insertRebuyListSql+=" select BALANCE_RECORD_SEQ.nextval,bill.BALANCE_BRCH_ID,dept.d_name,dept.d_name,info.S_BILL_NO,info.S_BILL_TYPE,info.D_ISSUE_DT" +
		",info.D_DUE_DT,info.F_BILL_AMOUNT,info.PRODUCT_ID,info.S_ACCEPTOR,bill.ed_drwrNm,bill.ed_accptrsvcr " +
		",info.REDISCOUNTOUTNAME,info.D_BUYIN_DT,info.GALE_DT,info.F_INT_DAYS,info.F_RATE,info.S_RATE_TYPE,info.F_PAYMENT,info.D_Interest" +
		",info.REDISC_BUY_BILL_ID,info.BILLINFO_ID,(select WORK_DATE from cd_runstate),bill.balance_flag,bill.billBuyMode" +
		" from BT_REDISCOUNT_BUYIN info,BT_REDISCOUNT_BUYIN_BATCH batch,cd_edraft bill,t_department dept " +
		" where info.REDISC_BUY_BATCH_ID=batch.REDISC_BUY_BATCH_ID and bill.LAST_SOURCE_ID=info.REDISC_BUY_BILL_ID " +
		" and bill.BALANCE_BRCH_ID=dept.id " +
		" and bill.balance_flag in (1,2,3,6) and bill.billBuyMode in ('02','03','05') ";
		
		this.updateData(insertRebuyListSql);
		
		log.info("************贴现转贴现余额明细抽取结束**************");
	}
	
	
	
	/**
	 * 按日按机构汇总余额
	 * 
	 * */
	public void isolateBalanceSumByDay()throws Exception{
		log.info("************贴现转贴现余额按日汇总开始**************");
		/**
		 * balance_flag 
		 * 余额标记：1贴现余额 2同业间回购式卖出余额
		 * 3卖出回购式再贴现余额 4卖断销账
		 * 5返售到期销账 6托收在途余额 7托收销账  
		 * billBuyMode
	 	 * 买入类型：01直贴02买断转贴03买入返售转贴04双买05系统内买断
		 */
		
		//删除当日提取余额明细数据
		String delSql = "delete from BO_BUSI_BALANCE_SUM_DAY where BALANCE_DT = (select WORK_DATE from cd_runstate) ";
		this.updateData(delSql);
		//插入贴现余额明细
		String insertDiscListSql ="insert into BO_BUSI_BALANCE_SUM_DAY (id,BALANCE_FLAG,BILL_BUY_MODE,BALANCE_DT,BRCH_ID,BRCH_NAME,F_BILL_AMOUNT) " ;
		insertDiscListSql+=" (select BALANCE_RECORD_SEQ.nextval, a.* " +
				"from " +
				"(select bb.balance_flag, bb.bill_buy_mode,(select WORK_DATE from cd_runstate), bb.brch_id,bb.BRCH_NAME, sum (bb.f_bill_amount) " +
				"from BO_BUSI_BALANCE_RECORD bb " +
				"where bb.balance_dt = (select WORK_DATE from cd_runstate) group by (bb.balance_flag,bb.bill_buy_mode,bb.brch_id,BRCH_NAME) ) a) ";
		
		this.updateData(insertDiscListSql);
		log.info("************贴现转贴现余额按日汇总结束**************");
	}
	
	/**
	 * 按日按机构汇总余额
	 * 
	 * */
	public void isolateBalanceSumByMonth()throws Exception{
		log.info("************贴现转贴现余额按月统计开始**************");
		/**
		 * balance_flag 
		 * 余额标记：1贴现余额 2同业间回购式卖出余额
		 * 3卖出回购式再贴现余额 4卖断销账
		 * 5返售到期销账 6托收在途余额 7托收销账  
		 * billBuyMode
	 	 * 买入类型：01直贴02买断转贴03买入返售转贴04双买05系统内买断
		 */
		
		//删除当日提取余额明细数据
		String delSql = "delete from BO_BUSI_BALANCE_SUM_MONTH where BALANCE_MONTH = (select to_char(WORK_DATE,'yyyy-MM')from cd_runstate) ";
		this.updateData(delSql);
		//插入贴现余额明细
		String insertDiscListSql ="insert into BO_BUSI_BALANCE_SUM_MONTH (id,BALANCE_FLAG,BILL_BUY_MODE,BALANCE_MONTH,BRCH_ID,BRCH_NAME,F_BILL_AMOUNT) " ;
		insertDiscListSql+=" (select BALANCE_RECORD_SEQ.nextval, a.* " +
				"from " +
				"(select bb.balance_flag, bb.bill_buy_mode,(select to_char(WORK_DATE,'yyyy-MM')from cd_runstate), bb.brch_id,bb.BRCH_NAME, sum (bb.f_bill_amount) " +
				"from BO_BUSI_BALANCE_RECORD bb " +
				"where bb.balance_dt = (select WORK_DATE from cd_runstate) group by (bb.balance_flag,bb.bill_buy_mode,bb.brch_id,BRCH_NAME) ) a) ";
		
		this.updateData(insertDiscListSql);
		log.info("************贴现转贴现余额按月统计结束**************");
	}
	
	/***
	 * 执行传入的sql语句
	 * @param sql
	 * @return
	 */
	public boolean updateData(String sql)throws Exception{
		Connection conn=null;
		boolean returnValue=false;
		try{
			conn=this.getConn();
			ISqlHandler ish=new SqlHandler(conn);
			returnValue= ish.executeUpdate(sql);
			DBUtil.commitConn(conn);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			DBUtil.rollbackConn(conn);
			throw new Exception(e.getMessage());
		}finally{
			DBUtil.closeConn(conn);
		}
		return returnValue;
	}

	/*****
	 * 获取数据库连接，利用jdbc的方式
	 * @return
	 * @throws Exception
	 */
	public Connection getConn() throws Exception{
		if(conn==null || this.conn.isClosed()){			
			conn=ConnectionUtils.getConn();			
		}
		return conn;
	}
	
}
