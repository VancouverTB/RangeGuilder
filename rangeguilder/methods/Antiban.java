package scripts.rs07.rangeguilder.methods;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.GameTab;

import scripts.rs07.rangeguilder.config.Data;

public class Antiban {

	public static void doAntiban() {
		if(!Target.targetScreenUp()) {
			int i = General.random(1, 500);
			switch(i) {
			case 1 : VCamera.adjustCamera(); break;
			case 2 : Mouse.setSpeed(Data.mouseSpeed + General.random(-10, 10)); break;
			case 3 : 
				if(GameTab.open(GameTab.TABS.STATS)) {
					General.sleep(750, 1250);
					if(GameTab.open(GameTab.TABS.INVENTORY)) {
						General.sleep(250, 350);
					}
				}
				
				break;
			default : break;
			}
		}
	}
	
}
