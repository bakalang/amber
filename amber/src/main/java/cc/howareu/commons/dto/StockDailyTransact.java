package cc.howareu.commons.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import cc.howareu.commons.annotation.Column;
import cc.howareu.util.BigDecimalUtils;
import cc.howareu.util.FormatUtils;
import cc.howareu.util.JSONUtils;
import cc.howareu.util.ParseUtils;


public class StockDailyTransact {

	@Column(name = "STOCK_ID")
	private String stockId;
	
	@Column(name = "TRANSACT_DATE")
	private Timestamp transactDate;
	
	@Column(name = "DATA")
	private String data;
	
	List<String> obj;
	
	public StockDailyTransact(String date, List<String> obj) {
		this.transactDate = ParseUtils.parseTimestamp(FormatUtils.DATE_PATTERN_YYYYMMDD, date);
		this.stockId = ((String) obj.get(0)).trim();
		obj.remove(1);
		this.data = JSONUtils.toJsonString(obj);
		this.obj = obj;
	}
	
	public String getStockId() {
		return stockId;
	}
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}
	public Timestamp getTransactDate() {
		return transactDate;
	}
	public void setTransactDate(Timestamp transactDate) {
		this.transactDate = transactDate;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.stockId = data;
	}	
	public List<String> getObj() {
		return obj;
	}
	public void setObj(List<String> obj) {
		this.obj = obj;
	}

	// 成交股數
	public BigDecimal getTransactVolume() {
		return BigDecimalUtils.build(this.obj.get(0), 0);
	}

	// 成交金額
	public BigDecimal getTurnover() {
		return BigDecimalUtils.build(this.obj.get(1), 0);
	}
	
	// 開盤價
	public BigDecimal getOpen() {
		return BigDecimalUtils.build(this.obj.get(2), 2);
	}

	// 最高價
	public BigDecimal getHigh() {
		return BigDecimalUtils.build(this.obj.get(3), 2);
	}

	// 最低價
	public BigDecimal getLow() {
		return BigDecimalUtils.build(this.obj.get(4), 2);
	}
	
	// 收盤價
	public BigDecimal getClose() {
		return BigDecimalUtils.build(this.obj.get(5), 2);
	}
	
	// 漲跌價差
	public BigDecimal getGrossBalance() {
		return BigDecimalUtils.build(this.obj.get(6), 2);
	}
	
	// 成交筆數
	public BigDecimal getTransactTotal() {
		return BigDecimalUtils.build(this.obj.get(7), 0);
	}
	
	@Override
	public String toString() {
		return JSONUtils.toJsonString(this);
	}
}
