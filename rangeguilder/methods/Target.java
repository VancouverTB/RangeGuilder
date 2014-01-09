package scripts.rs07.rangeguilder.methods;

import java.awt.Point;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Objects;
import org.tribot.api2007.types.RSObject;

import scripts.rs07.rangeguilder.config.Data;
import scripts.rs07.rangeguilder.utls.Tools;

public class Target {

	public static boolean targetScreenUp() {
		return Interfaces.get(325) != null;
	}
	
	public static void closeTargetScreen() {
		if(targetScreenUp()) {
			if(Interfaces.get(325, 89).click()) {
				if(!Timing.waitCondition(new Condition() {

					@Override
					public boolean active() {
						return !targetScreenUp();
					}
					
				}, General.random(1500, 2000))) {
					General.println("Timed-out waiting to close the target screen.");
				}
			}
			
			// Required wait so that the script doesn't go off the range
			General.sleep(750, 1000);
		}
	}
	
	public static boolean mouseTarget() {
		if(!Game.getUptext().equals("Fire-at Target")) {
			RSObject[] objects = Objects.findNearest(15, "Target");
			if(objects.length > 0) {
				for(RSObject object : objects) {
					if(object != null) {
						Mouse.move(Tools.randomizePoint(object.getModel().getCentrePoint(), 3));
						if(Timing.waitUptext("Fire-at", General.random(800, 1000))) {
							return true;
						}
					}
				}
			}
		}
		
		return Game.getUptext().contains("Fire-at Target");
	}
	
	/*
	 * Fires at the target until the player is no longer in the competition
	 */
	public static void fireAtTarget() {
		if(Competition.inCompetition() && Competition.atCompetitionArea() && mouseTarget()) {
			if(!Timing.waitCondition(new Condition() {

				@Override
				public boolean active() {
					if(!Competition.atCompetitionArea()) {
						return true;
					}
					
					// Stops firing at the target if we have no arrows at all
					if(Equipment.find(new String[] { "Bronze arrow" }).length == 0) {
						return true;
					}
					
					if(!Game.getUptext().contains("Fire-at Target")) {
						if(mouseTarget()) {
							General.sleep(50, 75);
						}

						return false;
					}
					
					// If the target screen is up for more than 800ms~1000ms, we close it
					if(targetScreenUp()) {
						if(!Timing.waitCondition(new Condition() {

							@Override
							public boolean active() {
								return !targetScreenUp();
							}
							
						}, General.random(1000, 1250))) {
							closeTargetScreen();
							return false;
						}
					}
					
					int i = General.random(1, 7);
					if(i == 1) {
						Mouse.move(new Point(Mouse.getPos().x + General.random(-1, 1), 
											 Mouse.getPos().y + General.random(-1, 1)));
					}
					
					Mouse.click(1); // Fires at the target
					
					if(Data.rightClick) {
						General.sleep(50, 75);
						Mouse.click(3);
						if(Timing.waitCondition(new Condition() {

							@Override
							public boolean active() {
								return targetScreenUp();
							}
							
						}, General.random(3500, 4500))) {
							if(Timing.waitChooseOption("Fire-at", General.random(800, 1000))) {
								if(mouseTarget()) {
									General.sleep(Data.clickTime + General.random(-10, 10));
								}
							}
						}
					}
					
					General.sleep(Data.clickTime + General.random(-10, 10));
					
					return Competition.competitionOver() || !Competition.inCompetition();
				}
				
			}, General.random(65000, 75000))) {
				General.println("Timed-out while playing the competition.");
			}
		}
	}
	
}
