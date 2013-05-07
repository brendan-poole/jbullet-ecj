package physicsevolve;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Clock;

public class Controller {

	static Clock clock = new Clock();

	public static void main(String[] args) {
		Model model1 = new Model();
		Model model2 = new Model();
		View view = new View(model1);
		
        int frame = 0;
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
        while (frame < 1000  && !view.isCloseRequested()) {
    		while(clock.getTimeMicroseconds()<1000000f/60f) {  }
    		clock.reset();
			model1.bodies.get("box").getCenterOfMassPosition(v1);
			model2.bodies.get("box").getCenterOfMassPosition(v2);
			System.out.println(v1.y +" "+v2.y);
            model1.move();
            model2.move();
            view.render();
            frame++;
        }
        model1.dynamicsWorld.destroy();
        model1.dynamicsWorld = null;
        model2.dynamicsWorld.destroy();
        model2.dynamicsWorld = null;
        view.destroy();
	}
}
