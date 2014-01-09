package scripts.rs07.rangeguilder;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.RandomEvents;

import scripts.rs07.rangeguilder.config.Data;
import scripts.rs07.rangeguilder.config.GUI;
import scripts.rs07.rangeguilder.methods.Antiban;
import scripts.rs07.rangeguilder.methods.Combat;
import scripts.rs07.rangeguilder.methods.Competition;
import scripts.rs07.rangeguilder.methods.Target;
import scripts.rs07.rangeguilder.methods.VCamera;
import scripts.rs07.rangeguilder.types.State;

@ScriptManifest(
		authors = { "Vancouver" }, 
		category = "Ranged", 
		name = "Van's RangeGuilder")

public class RangeGuilder extends Script implements RandomEvents {

	@Override
	public void run() {
		onStart();
		
		State.setState(State.WAIT);
		while(!State.getState().equals(State.STOP)) {
			Mouse.setSpeed(Data.mouseSpeed + General.random(-10, 10));
			
			switch(State.getState()) {
			case WAIT :
				General.sleep(250, 350);
				Antiban.doAntiban();
				break;
			
			case CAN_FIRE : Target.fireAtTarget(); break;
			case NEED_COMPETITION : Competition.getNewCompetition(); break;
			case NEED_TO_ADJUST_CAMERA : VCamera.adjustCamera(); break;
			case NEED_MORE_ARROWS : Competition.getMoreArrows(); break;
			case IN_COMBAT : Combat.runFromCombat(); break;
			
			case NEED_TO_EQUIP_ARROWS :
				RSItem[] items = Inventory.find(new String[] { "Bronze arrow" });
				if(items.length > 0) {
					if(Inventory.open() && items[0].click("Wield")) {
						General.sleep(75, 100);
					}
				}
				
				break;
			
			case NEED_TO_REPOSITION :
				if(PathFinding.aStarWalk(new RSTile(2671, 3417, 0))) {
					General.sleep(250, 350);
				}
		
				break;
				
			default : break;
			}
			
			// We handle script breaking things here
			if(ChooseOption.isOpen()) {
				ChooseOption.close();
				General.sleep(150, 250);
			}
			
			if(Target.targetScreenUp()) {
				General.sleep(250, 350);
				Target.closeTargetScreen();
			}
			
			if(!Data.equipArrows && (Inventory.getCount(new String[] { "Bronze arrow" }) > 0 && 
					Equipment.getCount(new String[] { "Bronze arrow" }) == 0)) {
				General.println("No arrows equipped and you're set to not equip arrows, equipping them anyway.");
				Data.equipArrows = true;
			}
			
			if(!State.getState().equals(State.STOP)) {
				State.setState(State.findCurrentState());
				General.sleep(150, 250);
			}
		}
	}
	
	private void onStart() {
		final GUI gui = new GUI();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					gui.setVisible(true);
					gui.setLocationRelativeTo(null);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		} 

		while(!GUI.started || !Login.getLoginState().equals(Login.STATE.INGAME)) {
			General.sleep(50);
		}
	}

	@Override
	public void onRandom(RANDOM_SOLVERS e) {
		if(Target.targetScreenUp()) {
			Target.closeTargetScreen();
		}
		
		if(e.equals(RANDOM_SOLVERS.COMBATRANDOM)) {
			General.println("Combat random detected: Running through the guild"
								+ " door.");
			Combat.runFromCombat();
		}
	}

	@Override
	public boolean randomFailed(RANDOM_SOLVERS arg0) {
		return false;
	}

	@Override
	public void randomSolved(RANDOM_SOLVERS arg0) {
	}

}
