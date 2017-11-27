package cc.howareu.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.CachedRowSet;

import com.google.common.base.Stopwatch;

import cc.howareu.commons.dao.DailyStakeDAO;
import cc.howareu.commons.dao.SecuritysDAO;
import cc.howareu.commons.dao.StockDailyTransactDAO;
import cc.howareu.commons.dto.DailyStake;
import cc.howareu.commons.dto.Securitys;
import cc.howareu.commons.dto.StockDailyTransact;
import cc.howareu.commons.model.database.DBPool;
import cc.howareu.util.DbUtils;



public class StockTWBO {
	
	public static int addSecurity(String securityId, String url) throws SQLException {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);

			int result = SecuritysDAO.save(conn, securityId, url);
			conn.commit();
			return result;
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}
	
	public static List<Securitys> queryNewsByID() throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getReadConnection();
			return SecuritysDAO.findAllSecuritys(conn);
		} finally {
			DbUtils.close(conn);
		}
	}
	
	public static List<String> getTopDifferSecurityTrade(String securityId, String type, int top) throws SQLException {
		Connection conn = null;
		CachedRowSet crs = null;
		List<String> rsList = new ArrayList<String>();
		try {

			Stopwatch stopwatch = Stopwatch.createStarted();
			conn = DBPool.getInstance().getReadConnection();
			if(type.equals("b")) {
				crs = DailyStakeDAO.getTopDifferBuySecurityTrade(conn, securityId, top);
			} else if(type.equals("s")) {
				crs = DailyStakeDAO.getTopDifferSellSecurityTrade(conn, securityId, top);
			}

		   	stopwatch.stop();
			System.out.println("getTopSecurityTrade: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
			
			while (crs.next()) {
				rsList.add(crs.getString("STOCK_ID"));
			}
			return rsList;
		}finally {
			DbUtils.close(crs);
			DbUtils.close(conn);
		}		
	}
	
	public static List<String> getTopSecurityTrade(String securityId, String type, int top) throws SQLException {
		Connection conn = null;
		CachedRowSet crs = null;
		List<String> rsList = new ArrayList<String>();
		try {

			Stopwatch stopwatch = Stopwatch.createStarted();
			conn = DBPool.getInstance().getReadConnection();
			if(type.equals("b")) {
				crs = DailyStakeDAO.getTopBuySecurityTrade(conn, securityId, top);
			} else if(type.equals("s")) {
				crs = DailyStakeDAO.getTopSellSecurityTrade(conn, securityId, top);
			}

		   	stopwatch.stop();
			System.out.println("getTopSecurityTrade: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
			
			while (crs.next()) {
				rsList.add(crs.getString("STOCK_ID"));
			}
			return rsList;
		}finally {
			DbUtils.close(crs);
			DbUtils.close(conn);
		}		
	}
	
	public static List<Integer> getThisMonthStockTransactDate(String stockId) throws SQLException {
		Connection conn = null;
		CachedRowSet crs = null;
		List<Integer> rsList = new ArrayList<Integer>();
		try {
			conn = DBPool.getInstance().getReadConnection();
			crs = StockDailyTransactDAO.getThisMonthStockTransactDate(conn, stockId);
			
			while (crs.next()) {
				rsList.add(crs.getInt("DAY"));
			}
			return rsList;
		}finally {
			DbUtils.close(crs);
			DbUtils.close(conn);
		}		
	}

	public static List<DailyStake> getSecurityTradeByStockIdAndSecurityId(String stockId, String securityId) throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getReadConnection();
			return DailyStakeDAO.getSecurityTradeByStockIdAndSecurityId(conn, stockId, securityId);
		} finally {
			DbUtils.close(conn);
		}
	}
	
	public static void updateSecurityLastModifiedDate(String securityId, Date date) throws Exception {					
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);
			SecuritysDAO.updateLastModifiedDate(conn, securityId, date);
			conn.commit();
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}
	
	public static void saveStockDailyTransact(StockDailyTransact sdt) throws Exception {					
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);
			StockDailyTransactDAO.save(conn, sdt);
			conn.commit();
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}
}
