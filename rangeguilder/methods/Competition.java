package scripts.rs07.rangeguilder.methods;

import java.awt.Point;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

import scripts.rs07.rangeguilder.utls.Tools;

public class Competition {

	/*
	 * Uses settings to tell when we're done with a competition or need a new 
	 * one. 
	 * 
	 * 0 = No competition
	 * 11 = Need to turn in the current competition as it's over
	 * 1~10 = Shots currently fired so far
	 */
	public static boolean inCompetition() {
		return Game.getSetting(156) != 0;
	}
	
	public static boolean competitionOver() {
		return Game.getSetting(156) == 11;
	}
	
	public static boolean atCompetitionArea() {
		return Player.getPosition().distanceTo(new RSTile(2672, 3418)) < 5;
	}
	
	public static boolean atRangeGuild() {
		return Player.getPosition().distanceTo(new RSTile(2666, 3430)) < 100;
	}
	
	private static boolean talkToJudge() {
		RSNPC[] npcs = NPCs.findNearest("Competition Judge");
		if(npcs.length > 0) {
			if(npcs[0].isOnScreen()) {
				Mouse.click(Tools.randomizePoint(npcs[0].getModel().getCentrePoint(), 5), 3);
				if(Timing.waitChooseOption("Talk-to Comp", General.random(800, 1000))) {
					if(!Timing.waitCondition(new Condition() {

						@Override
						public boolean active() {
							try { 
								return NPCChat.getMessage() != null && 
										(NPCChat.getMessage().contains("Hello there,") || NPCChat.getMessage().contains("Hello again,"));
							
							} catch(NullPointerException e) {
								e.printStackTrace();
								return false;
							}
						}
						
					}, General.random(2500, 3500))) {
						General.println("Timed-out waiting to talk to the Judge.");
					}
				} else { // Attempts to get rid of the right click menu screen
					Mouse.move(new Point(Mouse.getPos().x + General.random(-10, 10), 
										 Mouse.getPos().y + General.random(50, 60)));
				}
			} else {
				if(WebWalking.walkTo(npcs[0])) {
					General.sleep(250, 350);
				}
			}
		}
		
		return NPCChat.getMessage() != null && (NPCChat.getMessage().contains("Hello there,") || NPCChat.getMessage().contains("Hello again,"));
	}
	
	/*
	 * Purchases more arrows through the Judge.
	 */
	public static void getMoreArrows() {
		if(talkToJudge()) {
			if(Timing.waitCondition(new Condition() {

				@Override
				public boolean active() {
					if(NPCChat.clickContinue(true)) {
						General.sleep(100, 150);
					}
					
					if(Interfaces.get(228) != null && Interfaces.get(228, 1).click()) {
						General.sleep(100, 150);
					}
					
					return Inventory.getCount(new String[] { "Bronze arrow" }) > 0;
				}
				
			}, General.random(35000, 45000)));
		}
	}
	
	/*
	 * Enters a new range guild competition through the judge by clicking 
	 * through the chats.
	 */
	private static boolean enterCompetition() {
		if(NPCChat.getMessage() != null) {
			if(!Timing.waitCondition(new Condition() {

				@Override
				public boolean active() {
					if(NPCChat.clickContinue(true)) {
						General.sleep(100, 150);
					}
					
					// Clicks the "Sure, I'll give it a go." option
					if(Interfaces.get(230) != null && Interfaces.get(230, 1).click()) {
						General.sleep(100, 150);
					}
					
					return inCompetition();
				}
				
			}, General.random(45000, 60000)));
		}
		
		return inCompetition();
	}
	
	public static void getNewCompetition() {
		if(!inCompetition() || competitionOver()) {
			if(talkToJudge()) {
				if(enterCompetition()) {
					General.sleep(100, 150);
				}
			}
		}
	}
	
}
