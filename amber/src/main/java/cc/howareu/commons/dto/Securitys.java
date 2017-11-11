package cc.howareu.commons.dto;

import java.sql.Timestamp;

import cc.howareu.commons.annotation.Column;
import cc.howareu.util.JSONUtils;

public class Securitys {
	
	@Column(name = "SECURITY_ID")
	private String securityId;
	@Column(name = "URL")
	private String url;
	@Column(name = "LAST_MODIFIED_DATE")
	private Timestamp lastModifiedDate;
		
	public String getSecurityId() {
		return securityId;
	}
	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@Override
	public String toString() {
		return JSONUtils.toJsonString(this);
	}
}
