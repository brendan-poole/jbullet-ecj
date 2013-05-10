package physicsevolve;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.Transform;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class Controller {

	static Clock clock = new Clock();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File paramFile = new File(args[0]);
		EvolutionState state = new EvolutionState();
		state.parameters = new ParameterDatabase(paramFile,new String[] {});
		Parameter base = new Parameter("eval.problem");
		Model model1 = (Model) state.parameters.getInstanceForParameterEq(
				base.push("model"), null, Model.class);
		Model model2 = (Model) state.parameters.getInstanceForParameterEq(
				base.push("model"), null, Model.class);
		WorldView view = (WorldView) state.parameters.getInstanceForParameterEq(
				base.push("view"), null, WorldView.class);

		model1.setup(state, base);
		model2.setup(state, base);

		view.model = model1;
		view.setup(state, base);

		int frame = 0;
        String data1 = "";
        while (frame < 1000  && !view.isCloseRequested()) {
            //view.render();
            model1.move();
            model2.move();
            frame++;
    		//while(clock.getTimeMicroseconds()<1000000f/60f) {  }
    		//clock.reset();
            
    		Iterator it = model1.bodies.values().iterator();
			Transform tr = new Transform();
    		while(it.hasNext()) {
    			RigidBody b = (RigidBody) it.next();
    			b.getWorldTransform(tr);
    			String out = tr.origin.x+","+tr.origin.y+","+tr.origin.z+"\t";
    			data1 += out;
    		}
    		data1 += "\n";

        }
		model1.reset(true);
		model2.reset(true);
		
		frame = 0;
        String data2 = "";
        while (frame < 1000  && !view.isCloseRequested()) {
            //view.render();
            model1.move();
            model2.move();
            frame++;
    		//while(clock.getTimeMicroseconds()<1000000f/60f) {  }
    		//clock.reset();
            
    		Iterator it = model1.bodies.values().iterator();
			Transform tr = new Transform();
    		while(it.hasNext()) {
    			RigidBody b = (RigidBody) it.next();
    			b.getWorldTransform(tr);
    			String out = tr.origin.x+","+tr.origin.y+","+tr.origin.z+"\t";
    			data2 += out;
    		}
    		data2 += "\n";
        }
        System.out.println(data1.equals(data2));
        model1.dynamicsWorld.destroy();
        model1.dynamicsWorld = null;
        model2.dynamicsWorld.destroy();
        model2.dynamicsWorld = null;
        view.destroy();
	}
}
