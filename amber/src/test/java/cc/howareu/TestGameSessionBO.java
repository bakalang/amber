package cc.howareu;

import org.junit.Test;

import cc.howareu.api.TwseConnect;
import cc.howareu.bo.JustwebBO;



public class TestGameSessionBO {
	
	@Test
	public void openJustwebBO() throws Exception {
		JustwebBO.grebAllSecurityStake();
	}
	
	@Test
	public void testTwseConnect() throws Exception {
		TwseConnect twseConnect = new TwseConnect();
		twseConnect.run();
		//System.out.println(twseConnect.getRespList().size());
	}
}
