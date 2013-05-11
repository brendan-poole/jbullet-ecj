package physicsevolve;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class FppController {
	public Camera camera;
	float dx = 0.0f;
	float dy = 0.0f;
	float dt = 0.0f; // length of frame
	float lastTime = 0.0f; // when the last frame was
	float time = 0.0f;

	float mouseSensitivity = 0.05f;
	float movementSpeed = 10.0f; // move 10 units per second

	public FppController(Camera camera) {
		// hide the mouse
		Mouse.setGrabbed(true);
		this.camera = camera;
		
	}

	public void moveCamera() {
        time = Sys.getTime();
        dt = (time - lastTime)/1000.0f;
        lastTime = time;
 
        //distance in mouse movement from the last getDX() call.
        dx = Mouse.getDX();
        //distance in mouse movement from the last getDY() call.
        dy = -Mouse.getDY();
 
        //controll camera yaw from x movement fromt the mouse
        camera.yaw(dx * mouseSensitivity);
        //controll camera pitch from y movement fromt the mouse
        camera.pitch(dy * mouseSensitivity);
 
        //when passing in the distance to move
        //we times the movementSpeed with dt this is a time scale
        //so if its a slow frame u move more then a fast frame
        //so on a slow computer you move just as fast as on a fast computer
        if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
        {
            camera.walkForward(movementSpeed*dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
        {
            camera.walkBackwards(movementSpeed*dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left
        {
            camera.strafeLeft(movementSpeed*dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right
        {
            camera.strafeRight(movementSpeed*dt);
        }
	}
}
