package cc.howareu.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.howareu.bo.StockTWBO;
import cc.howareu.commons.dto.StockDailyTransact;
import cc.howareu.util.JSONUtils;
import cc.howareu.util.LogUtils;


	
public class TwseConnect implements Runnable {

	private final String url = "http://www.twse.com.tw/exchangeReport/STOCK_DAY_ALL?response=json";
	private Map<String, BigDecimal> closeMap = new HashMap<String, BigDecimal>();		
	
//	public TwseConnect() {
//		super();
//	}
	
	@Override
	public void run(){
		try {			
			String rsMessage = ApiCaller.post(url, null, null, 1000 * 60);
			Map<String, Object> map = JSONUtils.jsonToMap(rsMessage, String.class, Object.class);
			List<List<String>> aa = (List<List<String>>) map.get("data");
			StockDailyTransact sdt = null;
			for(List<String> obj : aa) {
				sdt = new StockDailyTransact((String) map.get("date"), obj);
				this.closeMap.put(sdt.getStockId(), sdt.getClose());
				
				StockTWBO.saveStockDailyTransact(sdt);
			}	
		} catch (Exception e) {
			System.out.println("url : "+url);
			LogUtils.coral.error(e.getMessage(), e);
		}		
	}

	public BigDecimal getClose(String stockId) {
		if(closeMap.get(stockId) == null) {
			return BigDecimal.ZERO;
		}
		return closeMap.get(stockId);
	}
}
