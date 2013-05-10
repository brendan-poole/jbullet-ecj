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

public class MaxOnes extends Problem implements SimpleProblemForm {
	public final static int frames = 1000;
	public Parameter base;
	public transient Model model;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		this.base = base;
	}

	public Object clone() {
		MaxOnes myobj = (MaxOnes) (super.clone());
		return myobj;
	}

	private void initModel(EvolutionState state) {
		if (model == null) {
			model = (Model) state.parameters.getInstanceForParameterEq(base.push("model"), null,
					Model.class);
			model.setup(state, base);
		} else {
			model.reset(true);
		}
	}

	public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation,
			final int threadnum) {
		if (ind.evaluated)
			return;
		initModel(state);
		DoubleVectorIndividual ind2 = (DoubleVectorIndividual) ind;

		float sum = 0;
		int frame = 0;
		while (frame < frames) {
			try {
				runFrame(state, ind2, subpopulation, threadnum, frame, model);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				break;
			}
			frame++;
		}
		Transform tr = new Transform();
		for (int i = 0; i < 10; i++) {
			model.bodies.get(i).getWorldTransform(tr);
			sum -= Math.abs(tr.origin.x) + Math.abs(tr.origin.z);
		}
		((SimpleFitness) ind2.fitness).setFitness(state, sum, false);
		ind2.evaluated = true;
	}

	public void runFrame(final EvolutionState state, final DoubleVectorIndividual ind,
			final int subpopulation, final int threadnum, int frame, Model model) {
		for (int i = 0; i < 10; i++) {
			if (frame == i * 10) {
				Vector3f v = new Vector3f();
				model.bodies.get(i).getLinearVelocity(v);
				v.x += ind.genome[i * 3];
				v.y += ind.genome[i * 3 + 1];
				v.z += ind.genome[i * 3 + 2];
				model.bodies.get(i).setLinearVelocity(v);
			}
		}
		model.move();
	}

	public void describe(final EvolutionState state, final Individual ind, final int subpopulation,
			final int threadnum, final int log) {
		int frame = 0;
		initModel(state);
		while (frame < frames) {
			try {
				runFrame(state, (DoubleVectorIndividual) ind, subpopulation, threadnum, frame,
						model);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				break;
			}
			frame++;
		}
		state.output.println(" Max Y: " + calcMaxY(), log);
	}

	public float calcMaxY() {
		Transform tr = new Transform();
		float maxy = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < 10; i++) {
			model.bodies.get(i).getWorldTransform(tr);
			if (tr.origin.y > maxy) {
				maxy = tr.origin.y;
			}
		}
		return maxy;

	}
}
