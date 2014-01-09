package scripts.rs07.rangeguilder.methods;

import org.tribot.api.General;
import org.tribot.api2007.Camera;

public class VCamera {

	
	// Returns true if the camera is in the right position to fire at the targets
	public static boolean atFiringPosition() {
		return Camera.getCameraAngle() >= 33 && Camera.getCameraAngle() <= 40 && 
				Camera.getCameraRotation() >= 300 && Camera.getCameraRotation() <= 330;
	}
	
	// Adjusts the camera to the proper firing position
	public static void adjustCamera() {
		General.println("Adjusting the camera to the proper firing position.");
		Camera.setCameraAngle(General.random(33, 40));
		Camera.setCameraRotation(General.random(300, 330));
	}
	
}
