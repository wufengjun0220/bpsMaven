package com.mingtech.application.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

public class ExpInsertSql {
	public void getInsert(int beginYear, int endYear, String fileUrl) {

		PrintStream ps = null;

		try {
			ps = new PrintStream(new FileOutputStream(fileUrl));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// int beginYear = 2009;
		// int endYear = 2015;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, beginYear);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		System.out.println("begin");
		while (calendar.get(Calendar.YEAR) < endYear) {
			String sql = "";
			String pk_ix_bo_holiday_id = calendar.getTimeInMillis() + "";
			int s_if_holiday = calendar.get(Calendar.DAY_OF_WEEK) == 1
					|| calendar.get(Calendar.DAY_OF_WEEK) == 7 ? 1 : 0;
			String d_date = "to_date('" + calendar.get(Calendar.YEAR) + "-"
					+ (calendar.get(Calendar.MONTH) + 1) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "','yyyy-mm-dd'";
			if (s_if_holiday == 1) {
				sql += "insert into bo_holiday (pk_ix_bo_holiday_id,s_if_holiday,s_day,d_date) values ('"
						+ pk_ix_bo_holiday_id
						+ "',"
						+ s_if_holiday
						+ ",'',"
						+ d_date + "));";
				
				System.out.println(sql);
				ps.print(sql + "\n");
			}
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		System.out.println("end");

	}

	public void inputFile(String data, String fileUrl) {

		try {

		} catch (Exception e) {
			// TODO Auto-generated catch block
//			logger.error(e.getMessage(),e);
		}
	}

	public static void main(String[] args) {
		ExpInsertSql test = new ExpInsertSql();
		test.getInsert(2009, 2016, "d:/test.sql");
	}
}