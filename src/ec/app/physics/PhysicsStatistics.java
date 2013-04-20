/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.physics;
import com.bulletphysics.demos.opengl.LWJGL;
import ec.*;
import ec.gp.koza.*;
import ec.steadystate.SteadyStateStatisticsForm;
import ec.util.*;
import java.io.PrintWriter;
import org.lwjgl.opengl.Display;

/* 
 * EdgeShortStatistics.java
 * 
 * Created: Fri Nov  5 16:03:44 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class PhysicsStatistics extends KozaStatistics {

    @Override
    public void postEvaluationStatistics(final EvolutionState state) {

        super.postEvaluationStatistics(state);
/*
        PhysicsProblem p = (PhysicsProblem) state.evaluator.p_problem.clone();
        p.model = new PhysicsModel(LWJGL.getGL(), true);
        int frame = 0;
        while (frame < 10000 && !Display.isCloseRequested()) {
            p.runFrame(state, best_of_run[0], 0, 0, frame,p.model);
           frame++;
        }
            p.model.getDynamicsWorld().destroy();
            p.model.setDynamicsWorld(null);
           Display.destroy();
           p = null;
*/
            //ViewThread t = new ViewThread(state, best_of_run[0]);
            //t.start();

        //state.output.message(best_of_run[0].printIndividual(state, System.out.));

    }

}

