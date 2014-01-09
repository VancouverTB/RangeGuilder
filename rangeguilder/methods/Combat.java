package scripts.rs07.rangeguilder.methods;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.rs07.rangeguilder.utls.Tools;

public class Combat {
	
	/*
	 * Returns the guild door in an RSObject type
	 */
	private static RSObject getDoor() {
		RSObject[] objects = Objects.findNearest(10, "Guild door");
		return (objects.length > 0) ? objects[0] : null;
	}
	
	/*
	 * param out = Going out the door, false for going in to the range guild. 
	 * Decides the X position we have to check for until we're in/out of guild
	 */
	private static boolean goThroughDoor(boolean out) {
		final int x = (out == true) ? 2657 : 2659;
		
		RSObject object = getDoor();
		General.println(object == null);
		if(object != null) {
			Mouse.click(Tools.randomizePoint(object.getModel().getCentrePoint(), 2), 3);
			if(Timing.waitChooseOption("Open Guild", General.random(800, 1000))) {
				if(!Timing.waitCondition(new Condition() {

					@Override
					public boolean active() {
						return Player.getPosition().getX() == x;
					}
					
				}, General.random(5000, 6000))) {
					General.println("Timed-out waiting to go through the door.");
				}
			}
		}
		
		return Player.getPosition().getX() == x;
	}
	
	public static void runFromCombat() {
		if(Player.getPosition().distanceTo(new RSTile(2659, 3437)) > 3) {
			if(WebWalking.walkTo(new RSTile(2659, 3437))) {
				General.sleep(250, 350);
			}
		}
		
		if(Player.getPosition().distanceTo(new RSTile(2659, 3437)) <= 3) {
			Camera.setCameraAngle(General.random(50, 75));
			if(goThroughDoor(true)) {
				General.sleep(750, 1000);
				if(goThroughDoor(false)) {
					General.sleep(250, 350);
				}
			}
		}
	}
	
}
