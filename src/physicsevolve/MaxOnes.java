package physicsevolve;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import ec.vector.FloatVectorIndividual;

public class MaxOnes extends Problem implements SimpleProblemForm {
	public final static int frames = 500;
	public static boolean paused;
    public Individual monitorInd;
    public Parameter base;
    public transient Model[] models;
    
    @Override
    public void setup(EvolutionState state, Parameter base) {
    	this.base = base;
    }
    
    @Override
    public void prepareToEvaluate(EvolutionState state, int threadNum) {
    	models = new Model[state.evalthreads];
		models[threadNum] = (Model) state.parameters.getInstanceForParameterEq(
				base.push("model"), null, Model.class);
		models[threadNum].setup(state, base);
    }
    

	public void evaluate(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum) {
		if (ind.evaluated)
			return;
		models[threadnum].reset();
		if(monitorInd == null) {
            this.monitorInd = ind;
        }
        DoubleVectorIndividual ind2 = (DoubleVectorIndividual) ind;

		float sum = 0;
		int frame = 0;
		long t = System.currentTimeMillis();
		while (frame < frames) {
			while (paused) {
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}

			try {
				runFrame(state, ind2, subpopulation, threadnum, frame, models[threadnum]);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				sum = 0;
				break;
			}
			frame++;
		}
		Transform tr = new Transform();
		for(int i = 0; i < 10; i++) {
			models[threadnum].bodies.get(i).getWorldTransform(tr);
			sum -= Math.abs(0 - tr.origin.z) + Math.abs((i - 5) * 2 - tr.origin.x);
		}

		((SimpleFitness) ind2.fitness).setFitness(state, sum ,false);
		ind2.evaluated = true;
	}
    public void runFrame(final EvolutionState state,
            final DoubleVectorIndividual ind,
            final int subpopulation,
            final int threadnum, int frame, Model model) {
    	if(frame == 0) {
    		for(int i = 0; i < 10; i++) {
				Vector3f v = new Vector3f();
				model.bodies.get(i).getLinearVelocity(v);
				v.x += ind.genome[i*3];
				v.y += ind.genome[i*3+1];
				v.z += ind.genome[i*3+2];
				model.bodies.get(i).setLinearVelocity(v);
    		}
    	}
        model.move();
    }
}
