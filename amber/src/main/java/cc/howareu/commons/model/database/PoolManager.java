package cc.howareu.commons.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import com.google.apphosting.api.ApiProxy;

import cc.howareu.commons.model.Setting;
import cc.howareu.util.LogUtils;

public class PoolManager {

	private Connection conn;
	private String url = null;
	private static final Logger logger = Logger.getLogger(PoolManager.class.getName());

	public PoolManager(){
		try {			
			Class.forName("com.mysql.jdbc.Driver");			 
			ApiProxy.Environment env = ApiProxy.getCurrentEnvironment();
			String hostname = null;
			if(env != null) {
				Map<String,Object> attr = env.getAttributes();
				hostname = (String) attr.get("com.google.appengine.runtime.default_version_hostname");
			}

			this.url = (null == hostname)
					? "jdbc:mysql://35.201.212.218:3306/STOCK_TW?user=quartz&password=quartz"
					: "jdbc:google:mysql://heisenberg-182109:asia-east1:quartz/STOCK_TW?user=quartz&password=quartz" ;
			logger.log(Level.INFO, "connecting to: " + url);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public Connection get() {
		return get(-1);
	}

	public Connection get(int dbIndex) {

		try {			
			conn = DriverManager.getConnection(this.url);
		} catch (SQLException e) {
			logger.log(Level.ALL, "Unable to connect to Cloud SQL", e);
		}
		if (Setting.ENABLE_CONNECTION_DEBUG) {
			ConnectionWrapper connectionWrapper = new ConnectionWrapper(conn);
			ConnectionMonitor.getInstance().addConnection(connectionWrapper);
			return connectionWrapper;
		} else {
			return conn;
		}
	}
	
	private void checkDBTime() {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Timestamp ts = null;

		try {
			conn = get();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT SYSTIMESTAMP FROM DUAL");
			while (rs.next()) {
				ts = rs.getTimestamp(1);
			}
			
			// 跟DB的時間比較，誤差不應該超過一分鐘
			if (Math.abs(ts.getTime() - System.currentTimeMillis()) > 60_000L) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S [z]");
				String localtimeStr = sdf.format(new Date());
				LogUtils.coral.error(String.format("Different DBTime %s with ClientTime %s", ts.toString(), localtimeStr));
			}
			
		} catch (Exception e) {
			LogUtils.coral.error("check db time error ", e);
			e.printStackTrace();
		} finally {
			closeAll(rs, stmt, conn);
		}
	}
	
	private static void closeAll(AutoCloseable... closeables) {
		if (closeables == null) {
			return;
		}
		
		for (AutoCloseable resource : closeables) {
			if (resource == null) {
				continue;
			}
			try {
				resource.close();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
	

}
