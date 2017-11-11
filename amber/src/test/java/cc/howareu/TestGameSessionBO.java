package cc.howareu;

import org.junit.Test;

import cc.howareu.bo.JustwebBO;



public class TestGameSessionBO {
	
	@Test
	public void openJustwebBO() throws Exception {
		JustwebBO.grebAllSecurityStake();
	}
}
