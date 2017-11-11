package cc.howareu.commons.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Stopwatch;

import cc.howareu.bo.StockTWBO;
import cc.howareu.commons.dto.DailyStake;
import cc.howareu.util.LogUtils;
import cc.howareu.util.RequestParser;
import cc.howareu.util.RequestUtils;
import cc.howareu.util.ResponseUtils;

@SuppressWarnings("serial")
@WebServlet("/service/nvd3_chart/*")
public class Nvd3ChartController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException {
		doProcess(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException {
		doProcess(request, response);
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		try {
			String pathInfo = RequestUtils.getPathInfo(request);

			if ("/topAbsolute".equals(pathInfo)) {
				topAbsolute(request, response, session);
			} else if ("/topDiffer".equals(pathInfo)) {
				topDiffer(request, response, session);
			} else {
				System.err.println("incorrect pathInfo of " + pathInfo);
				ResponseUtils.sendResponse(response, pathInfo + " is not supported");
			}

		} catch (Exception e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
	}

	private void topDiffer(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

		try {
			String securityId = RequestParser.getStringParameter(request, 2000, "securityId", null);
//			String days = RequestParser.getStringParameter(request, 2000, "days", null);
			String type = RequestParser.getStringParameter(request, 1, "type", null);

			Map<String, List<DailyStake>> rtnMap = new HashMap<String, List<DailyStake>>();

			Stopwatch stopwatch = Stopwatch.createStarted();
			for(String stockId : StockTWBO.getTopDifferSecurityTrade(securityId, type, 15)) {
				 List<DailyStake> stList = StockTWBO.getSecurityTradeByStockIdAndSecurityId(stockId, securityId);
				 for(DailyStake st : stList){
					 st.setTradeDateMinSec(st.getCreatedDate().getTime());
				 }
				 rtnMap.put(stockId, stList);
			}stopwatch.stop();
			System.out.println("topDiffer getSecurityTradeByStockIdAndSecurityId: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
			
			ResponseUtils.sendJsonResponse(response, rtnMap);
		} catch (Exception e) {
			LogUtils.coral.error("[MobileAppService][queryWeatherHistory] " + e.getMessage(), e);
			ResponseUtils.sendJsonErrorResponse(response, e.getMessage());
			return;
		} 
		
	}
	
	private void topAbsolute(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

		try {
			String securityId = RequestParser.getStringParameter(request, 2000, "securityId", null);
//			String days = RequestParser.getStringParameter(request, 2000, "days", null);
			String type = RequestParser.getStringParameter(request, 1, "type", null);

			Map<String, List<DailyStake>> rtnMap = new HashMap<String, List<DailyStake>>();

			Stopwatch stopwatch = Stopwatch.createStarted();
			for(String stockId : StockTWBO.getTopSecurityTrade(securityId, type, 10)){
				 List<DailyStake> stList = StockTWBO.getSecurityTradeByStockIdAndSecurityId(stockId, securityId);
				 for(DailyStake st : stList){
					 st.setTradeDateMinSec(st.getCreatedDate().getTime());
				 }
				 rtnMap.put(stockId, stList);
			}stopwatch.stop();
			System.out.println("top getSecurityTradeByStockIdAndSecurityId: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
			
			ResponseUtils.sendJsonResponse(response, rtnMap);
		} catch (Exception e) {
			LogUtils.coral.error("[MobileAppService][queryWeatherHistory] " + e.getMessage(), e);
			ResponseUtils.sendJsonErrorResponse(response, e.getMessage());
			return;
		} 
		
	}
}
