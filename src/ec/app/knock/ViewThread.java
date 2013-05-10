/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.knock;

import org.lwjgl.opengl.Display;

import ec.EvolutionState;
import ec.Individual;
import ec.app.tutorial1.MaxOnes;
import ec.vector.FloatVectorIndividual;

/**
 *
 * @author brendan
 */
public class ViewThread extends Thread {

    EvolutionState state;
    FloatVectorIndividual individual;

    public ViewThread(EvolutionState state,Individual individual) {

        this.state = state;
        this.individual = (FloatVectorIndividual) individual;
    }

    @Override
    public void run() {
        super.run();
        MaxOnes p = (MaxOnes) state.evaluator.p_problem.clone();
        //p.model = new Model(LWJGL.getGL(), true);
        int frame = 0;
        while (frame < 10000 && !Display.isCloseRequested()) {
            //p.runFrame(state, individual, 0, 0, frame,p.model);
           frame++;
        }
        //    p.model.getDynamicsWorld().destroy();
        //    p.model.setDynamicsWorld(null);
           Display.destroy();
           p = null;


        //state.output.message(best_of_run[0].printIndividual(state, System.out.));

    }
}
