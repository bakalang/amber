package cc.howareu.commons.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;

import cc.howareu.commons.dto.StockDailyTransact;
import cc.howareu.commons.model.database.DBQueryRunner;
import cc.howareu.util.JSONUtils;


public class StockDailyTransactDAO {

	public static CachedRowSet getThisMonthStockTransactDate(Connection conn, String stockId) throws SQLException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		String sql = "SELECT DAY(TRANSACT_DATE) AS DAY FROM STOCK_DAILY_TRANSACT WHERE STOCK_ID=? AND MONTH(TRANSACT_DATE)=? ";
		return DBQueryRunner.query(conn, sql, stockId, cal.get(Calendar.MONTH) + 1);
	}

	public static StockDailyTransact getStockDailyTransact(Connection conn, String stockId, Date transactDate) throws SQLException, IOException {
		String sql = "SELECT * FROM STOCK_DAILY_TRANSACT WHERE STOCK_ID=? AND TRANSACT_DATE=? ";
		StockDailyTransact sdt = DBQueryRunner.getBean(conn, StockDailyTransact.class, sql, stockId, new Timestamp(transactDate.getTime()));
		sdt.setObj(JSONUtils.parseJsonToObjectList(sdt.getData(), String.class));
		return sdt;
	}	
	
	public static int save(Connection conn, StockDailyTransact sdt) throws SQLException {
		String sql = "INSERT INTO STOCK_DAILY_TRANSACT (STOCK_ID ,TRANSACT_DATE, DATA) VALUES (?, ?, ?)";		
		return DBQueryRunner.update(conn, sql, sdt.getStockId(), sdt.getTransactDate(), sdt.getData());
	}	
}
