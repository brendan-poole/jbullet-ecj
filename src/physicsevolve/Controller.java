package physicsevolve;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.Transform;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class Controller {

	static Clock clock = new Clock();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File paramFile = new File(args[0]);
		EvolutionState es = new EvolutionState();
		es.parameters = new ParameterDatabase(paramFile,new String[] {});
		Parameter base = new Parameter("physics");
		Model model1 = (Model) es.parameters.getInstanceForParameterEq(
				base.push("model"), null, Model.class);
		Model model2 = (Model) es.parameters.getInstanceForParameterEq(
				base.push("model"), null, Model.class);
		WorldView view = (WorldView) es.parameters.getInstanceForParameterEq(
				base.push("view"), null, WorldView.class);

		model1.setup(es, base);
		model2.setup(es, base);
		
		view.model = model1;
		view.setup(es, base);
		
        int frame = 0;
        while (frame < 1000  && !view.isCloseRequested()) {
            view.render();
            model1.move();
            model2.move();
            frame++;
    		while(clock.getTimeMicroseconds()<1000000f/60f) {  }
    		clock.reset();
        }
        model1.dynamicsWorld.destroy();
        model1.dynamicsWorld = null;
        model2.dynamicsWorld.destroy();
        model2.dynamicsWorld = null;
        view.destroy();
	}
}
