package scripts.rs07.rangeguilder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.RandomEvents;

import scripts.rs07.magicbuddy.utls.Tools;
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

public class RangeGuilder extends Script implements Painting, RandomEvents {

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
		
		Data.startExp = Skills.getXP(Skills.SKILLS.RANGED);
        Data.startLevel = Skills.SKILLS.RANGED.getCurrentLevel();
        Data.startTime = System.currentTimeMillis();
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

	@Override
	public void onPaint(Graphics g) {
		if(Login.getLoginState().equals(Login.STATE.INGAME)) {
			Data.runTime = System.currentTimeMillis();
			long timeRan = Data.runTime - Data.startTime;

			int expGained = Skills.getXP(Skills.SKILLS.RANGED) - Data.startExp;
			int expPerHour = (int) (expGained * 3600000D / timeRan);
			int levelsGained = Skills.getCurrentLevel(Skills.SKILLS.RANGED) - Data.startLevel;

			g.setFont(new Font("Sans Serif", 0, 12));

			Color lightGreen = new Color(0, 153, 0, 150);
			g.setColor(lightGreen);
			g.fillRect(383, 4, 132, 100);

			// Draws the back panel border
			g.setColor(Color.BLACK);
			g.drawRect(383, 4, 132, 100);

			// Draws a black line under the name branding
			g.drawLine(383, 25, 515, 25);

			// Draws the text shadowing
			g.drawString("Van's RangeGuilder", 396, 21);
			g.drawString("Time: " + Timing.msToString(timeRan), 396, 41);
			g.drawString("Exp: " + expGained + " (" + expPerHour + "/h)", 396, 59);
			g.drawString("Level: " + Skills.getActualLevel(Skills.SKILLS.RANGED) + " (" + levelsGained + ")", 396, 79);
			g.drawString("TTL: " + Tools.timeToLevel(Skills.SKILLS.RANGED, expGained, timeRan), 396, 97);

			// Draws the text
			g.setColor(Color.WHITE);
			g.drawString("Van's RangeGuilder", 395, 20);
			g.drawString("Time: " + Timing.msToString(timeRan), 395, 40);
			g.drawString("Exp: " + expGained + " (" + expPerHour + "/h)", 395, 58);
			g.drawString("Level: " + Skills.getActualLevel(Skills.SKILLS.RANGED) + " (" + levelsGained + ")", 395, 78);
			g.drawString("TTL: " + Tools.timeToLevel(Skills.SKILLS.RANGED, expGained, timeRan), 395, 96);
		}
	}

}
