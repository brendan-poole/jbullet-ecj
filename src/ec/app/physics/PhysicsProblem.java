package ec.app.physics;

import com.bulletphysics.demos.opengl.LWJGL;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;
import org.lwjgl.opengl.Display;

public class PhysicsProblem extends GPProblem implements SimpleProblemForm {

    public static final float PI = 3.14159265358979323846f;
    public static final String P_SIZE = "size";
    public double currentValue;
    public int trainingSetSize;
    // we'll need to deep clone this one though.
    public PhysicsData input;
    public PhysicsModel model;
    public static boolean paused;
    public Individual monitorInd;

    public Object clone() {
        // don't bother copying the inputs and outputs; they're read-only :-)
        // don't bother copying the currentValue; it's transitory
        // but we need to copy our regression data
        PhysicsProblem myobj = (PhysicsProblem) (super.clone());

        myobj.input = (PhysicsData) (input.clone());
        return myobj;
    }

    public void setup(final EvolutionState state, final Parameter base) {
        // very important, remember this
        super.setup(state, base);

        // set up our input -- don't want to use the default base, it's unsafe
        input = (PhysicsData) state.parameters.getInstanceForParameterEq(
                base.push(P_DATA), null, PhysicsData.class);
        input.setup(state, base.push(P_DATA));

        trainingSetSize = state.parameters.getInt(base.push(P_SIZE), null, 1);
        if (trainingSetSize < 1) {
            state.output.fatal("Training Set Size must be an integer greater than 0", base.push(P_SIZE));
        }
    }

    public void evaluate(final EvolutionState state,
            final Individual ind,
            final int subpopulation,
            final int threadnum) {

        if(monitorInd == null) {
            this.monitorInd = ind;
        }
        model = new PhysicsModel(LWJGL.getGL(), false);

        //visualise();
        if (!ind.evaluated) // don't bother reevaluating
        {
            int hits = 0;
            double sum = 0.0;
            double max = 0.0;
            double result;

            int frame = 0;
            long t = System.currentTimeMillis();
            while (frame < 1000) {
                while (paused) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ex) {
                        System.out.println("Oh shit!");
                    }
                }

                this.runFrame(state, ind, subpopulation, threadnum, frame, model);

                Vector3f v = new Vector3f();
                model.ragdolls.get(0).bodies[2].getCenterOfMassPosition(v);

                sum += v.y / 2000f;

                // ragdoll starts in the air so let land before evaluating
                if(frame > 100 && v.y > max)
                    max = v.y;

                if(v.y < 0)
                    break;
                else
                    hits++;

                frame++;
            }
            System.out.print("time: "+(System.currentTimeMillis() - t));

            Vector3f v = new Vector3f();
            model.ragdolls.get(0).bodies[2].getCenterOfMassPosition(v);
            //result = Math.abs(1100 - (Math.abs(v.x) + Math.abs(v.y)));
            //result = 100 - sum;
            //System.out.println(sum);
            //if(sum < -2) result = 99999999;
            //result = 100 - max;
            result = 1000 - hits;

            KozaFitness f = ((KozaFitness) ind.fitness);
            f.setStandardizedFitness(state, (float) result);
            ind.evaluated = true;
            System.out.println(" fitness: " + f.standardizedFitness());

            if (model.visible) {
                Display.destroy();
            }
        }
        model = null;
        //System.out.println();
    }

    public void runFrame(final EvolutionState state,
            final Individual ind,
            final int subpopulation,
            final int threadnum, int frame, PhysicsModel model) {
        if (model.ragdolls.size() == 1) {
            for (int i = 0; i < 30; i++) {
                model.ragdolls.get(0).motors[i].enableMotor = true;
                model.ragdolls.get(0).motors[i].maxMotorForce = 1000;
                currentValue = frame;

                ((GPIndividual) ind).trees[i].child.eval(
                        state, threadnum, input, stack, ((GPIndividual) ind), this);

                model.inputs[i].add(((float) input.x));
            }
        }
        model.moveAndDisplay();

    }
}
