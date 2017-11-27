package cc.howareu.commons.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import cc.howareu.commons.dto.Securitys;
import cc.howareu.commons.model.database.DBQueryRunner;



public class SecuritysDAO {
	
	public static List<Securitys> findAllSecuritys(final Connection conn) throws Exception {
		String sql = "SELECT SECURITY_ID ,URL ,LAST_MODIFIED_DATE from SECURITYS ";
		return DBQueryRunner.getBeanList(conn, Securitys.class, sql);
	}
	
	public static int save(final Connection conn, String securityId, String url) throws SQLException {
		String sql = "INSERT INTO SECURITYS (SECURITY_ID ,URL ,LAST_MODIFIED_DATE) VALUES (?, ?, CURRENT_TIMESTAMP)";
		return DBQueryRunner.update(conn, sql, securityId, url);
	}	
	
	public static int updateLastModifiedDate(final Connection conn, String securityId, Date date) throws SQLException {
		String sql = "UPDATE SECURITYS SET LAST_MODIFIED_DATE = ? WHERE SECURITY_ID = ?";
		return DBQueryRunner.update(conn, sql, date, securityId);
	}	
}