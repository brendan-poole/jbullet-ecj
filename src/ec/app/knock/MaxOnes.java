package ec.app.knock;

import javax.vecmath.Vector3f;

import com.bulletphysics.demos.opengl.LWJGL;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.FloatVectorIndividual;

public class MaxOnes extends Problem implements SimpleProblemForm {
	public final static int frames = 500;
	public static boolean paused;
	public transient PhysicsModel model;
    public Individual monitorInd;
	public void evaluate(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum) {
		if (ind.evaluated)
			return;

        if(monitorInd == null) {
            this.monitorInd = ind;
        }
        model = new PhysicsModel(LWJGL.getGL(), false);
        
        
        FloatVectorIndividual ind2 = (FloatVectorIndividual) ind;

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
				runFrame(state, ind2, subpopulation, threadnum, frame, model);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				sum = 0;
				break;
			}
			frame++;
		}
		Vector3f v = new Vector3f();
		//model.ragdolls.get(0).bodies[RagDoll.BodyPart.BODYPART_HEAD.ordinal()].getCenterOfMassPosition(v);
		for(int i = 0; i < 10; i++) {
			model.bod[i].getCenterOfMassPosition(v);
			sum -= Math.abs(0 - v.z) + Math.abs((i - 5) * 2 - v.x);
		}

		((SimpleFitness) ind2.fitness).setFitness(state, sum ,false);
		ind2.evaluated = true;
	}
    public void runFrame(final EvolutionState state,
            final FloatVectorIndividual ind,
            final int subpopulation,
            final int threadnum, int frame, PhysicsModel model) {
    	if(frame == 0) {
    		for(int i = 0; i < 10; i++) {
				Vector3f v = new Vector3f();
				model.bod[i].getLinearVelocity(v);
				v.x += ind.genome[i*3];
				v.y += ind.genome[i*3+1];
				v.z += ind.genome[i*3+2];
				model.bod[i].setLinearVelocity(v);
    		}
    	}
        model.moveAndDisplay();
    }
}
