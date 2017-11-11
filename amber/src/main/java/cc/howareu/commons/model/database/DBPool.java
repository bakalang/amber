package cc.howareu.commons.model.database;

import java.sql.Connection;
import java.sql.SQLException;

import cc.howareu.util.LogUtils;

public class DBPool {

	private PoolManager poolManager; 
	private static DBPool theInstance = new DBPool();

	private DBPool() {
		try {
			poolManager = new PoolManager();
		} catch (Exception ex) {
			LogUtils.coral.error(ex.getMessage(), ex);
		}
	}

	public static DBPool getInstance() {		
		return theInstance;
	}
	
	public Connection getReadConnection() throws SQLException {
		return poolManager.get(-1);
	}

	public Connection getWriteConnection() throws SQLException {
		return poolManager.get(-1);
	}
}