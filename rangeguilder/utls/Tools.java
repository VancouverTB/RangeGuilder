package scripts.rs07.rangeguilder.utls;

import java.awt.Point;

import org.tribot.api.General;

public class Tools {

	public static Point randomizePoint(Point p, int range) {
		return new Point(p.x + General.random(-range, range), 
						 p.y + General.random(-range, range));
		
	}
	
}
