package scripts.rs07.rangeguilder.types;

import org.tribot.api.General;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;

import scripts.rs07.rangeguilder.config.Data;
import scripts.rs07.rangeguilder.methods.Competition;
import scripts.rs07.rangeguilder.methods.VCamera;

public enum State {

	STOP, WAIT, CAN_FIRE, NEED_COMPETITION, NEED_TO_REPOSITION, 
	NEED_TO_ADJUST_CAMERA, NEED_TO_EQUIP_ARROWS, NEED_MORE_ARROWS, IN_COMBAT;
	
	public static State state;
	
	public static State getState() {
		return state;
	}
	
	public static void setState(State s) {
		state = s;
	}
	
	public static State findCurrentState() {
		if(Player.isMoving() || !Login.getLoginState().equals(Login.STATE.INGAME)) {
			return WAIT;
		}
		
		if(Inventory.getCount(new String[] { "Coins" }) < 1000) {
			General.println("Less than 1,000 coins, stopping just incase");
			return STOP;
		}
		
		if(!VCamera.atFiringPosition()) {
			return NEED_TO_ADJUST_CAMERA;
		}
		
		if(!Competition.atCompetitionArea()) {
			return NEED_TO_REPOSITION;
		}
		
		if(Player.getRSPlayer().isInCombat()) {
			return IN_COMBAT;
		}
		
		if(!Competition.atRangeGuild()) {
			General.println("We are not at the range guild, stopping.");
			return STOP;
		}
		
		if(Data.equipArrows && (Inventory.getCount(new String[] { "Bronze arrow" }) > 0)) {
			return NEED_TO_EQUIP_ARROWS;
		}
		
		if(Inventory.getCount(new String[] { "Bronze arrow" }) == 0 
				&& Equipment.getCount(new String[] { "Bronze arrow" }) == 0) {
			General.println("No more arrows equipped or in our inventory. Buying more.");
			return NEED_MORE_ARROWS;
		}
		
		if(Competition.inCompetition() && !Competition.competitionOver()) {
			return CAN_FIRE;
		} else {
			return NEED_COMPETITION;
		}
	}
	
}
