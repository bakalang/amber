package cc.howareu.bo;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.howareu.api.JsoupConnect;
import cc.howareu.api.TwseConnect;
import cc.howareu.commons.dao.DailyStakeDAO;
import cc.howareu.commons.dto.DailyStake;
import cc.howareu.commons.dto.Securitys;
import cc.howareu.commons.model.database.DBPool;
import cc.howareu.util.BigDecimalUtils;
import cc.howareu.util.DateUtil;
import cc.howareu.util.DbUtils;
import cc.howareu.util.FormatUtils;
import cc.howareu.util.LogUtils;
import cc.howareu.util.ParseUtils;
import cc.howareu.util.StockSecurityUtils;
import cc.howareu.util.Validator;


public class JustwebBO {
	
	public static TwseConnect twseConnect = new TwseConnect();	
	
	public static void grebAllSecurityStake() throws Exception {
		twseConnect.run();
		
		List<Securitys> allList = StockTWBO.queryNewsByID();
		if(allList != null) {
			for(Securitys s : allList) {
				Date d = ParseUtils.parseDate(FormatUtils.DATE_PATTERN_YYYYMMDD, "20171204");
				grebSecurityDailyStake(s, d);
			}
		}
	}
	
	public static void grebSecurityDailyStake(Securitys s, Date date) {
		try {
			String url = StockSecurityUtils.getURLParameters(s.getUrl(), date);
			System.out.println("url : "+url);
			Document doc = JsoupConnect.jsoupRetry(url);
			if(doc == null){
				System.out.println("webpage not found.");
                return;
			}
			 
			Elements mainTable = doc.select("#oMainTable");
			String tmpDate = mainTable.select(".t11").text();
			if(tmpDate.length() > 10) {
				String pageDate = tmpDate.substring(tmpDate.length() - 8, tmpDate.length());
				System.out.println(">> "+pageDate);
				if(!Validator.isPositiveInteger(pageDate)) {
					System.out.println(s+" new data need greb.");
					return;
				}
				
				if(DateUtil.isToday(DateUtil.toDate(pageDate, FormatUtils.DATE_PATTERN_YYYYMMDD))) {
					System.out.println(s+" new data need greb.");
					//return;
				}
				
//				if(!s.getLastModifiedDate().before(DateUtil.toDate(pageDate, FormatUtils.DATE_PATTERN_YYYYMMDD))) {
//
//					System.out.println(s.getLastModifiedDate());
//					System.out.println(DateUtil.toDate(pageDate, FormatUtils.DATE_PATTERN_YYYYMMDD));
//					System.out.println(s+" has up to date.");
//					return;
//				}
	 		}

			System.out.println(s+"do job.");
			Elements mainTRs = mainTable.select("tbody > tr");
			fetchDetailTR(s.getSecurityId(), date, mainTRs.get(2).children().get(0));
			fetchDetailTR(s.getSecurityId(), date, mainTRs.get(2).children().get(1));
			
			// update security last update date
			StockTWBO.updateSecurityLastModifiedDate(s.getSecurityId(), date);
			 
		} catch (Exception e) {
			LogUtils.coral.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void fetchDetailTR(String securityId, Date date, Element datailTR) throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);
		
			Iterator<Element> rr = datailTR.select("tr").iterator();
	        while (rr.hasNext()) {
	        	Iterator<Element> dd = rr.next().select("td").iterator();
	        	DailyStake ds = null;
	    		int t3n1Flag = 0;
	        	while (dd.hasNext()) {
	        		Element element = dd.next();
	        		if(element.hasClass("t4t1")) {
	        			ds = new DailyStake();
	        			Elements aHref = element.select("a[href]");
	        			if(aHref.size() > 0) {
	        				String hrefText = aHref.attr("href");
	        				// <a href="javascript:Link2Stk('00637L');">00637L元大滬深300正2</a>
	        				ds.setStockId(hrefText.substring(hrefText.indexOf("'") + 1, hrefText.lastIndexOf("'")));
	        				ds.setSecurityId(securityId);
	        				ds.setCreatedDate(new Timestamp(date.getTime()));
	        			}else {
	        				// <script language="javascript"> GenLink2stk('AS5871','中租-KY'); </script>
	        				String htmlText = element.html().replace("AS", "");
	        				ds.setStockId(htmlText.substring(htmlText.indexOf("'") + 1, htmlText.indexOf("',")));
	        				ds.setSecurityId(securityId);
	        				ds.setCreatedDate(new Timestamp(date.getTime()));
	        			}
	        		}
	        		if(element.hasClass("t3n1")) {
	        			if(t3n1Flag == 0) {
	        				ds.setBuyStake(BigDecimalUtils.build(Jsoup.parse(element.html()).text(), 0));
	        			} else if (t3n1Flag == 1) {
	        				ds.setSellStake(BigDecimalUtils.build(Jsoup.parse(element.html()).text(), 0));
	        			} else {
	        				continue;
	        			}        			
	        			t3n1Flag++;        			
	        		}
	        	}
	        	
	        	if(ds != null) {
	        		// 取得cache中的收盤價
	        		ds.setClose(twseConnect.getClose(ds.getStockId()));
	        		DailyStakeDAO.save(conn, ds);
	        	}        	
	        }	

			conn.commit();
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}
}
