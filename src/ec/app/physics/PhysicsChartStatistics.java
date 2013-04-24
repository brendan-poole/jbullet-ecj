package ec.app.physics;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.Display;

import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.RigidBody;

import ec.EvolutionState;
import ec.Individual;
import ec.app.physics.RagDoll.BodyPart;
import ec.display.chart.XYSeriesChartStatistics;
import ec.util.Parameter;

public class PhysicsChartStatistics extends XYSeriesChartStatistics {

	private int[] seriesID;

	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		int numSubPops = state.parameters.getInt(new Parameter("pop.subpops"), null);
		seriesID = new int[numSubPops];

		for (int i = 0; i < numSubPops; ++i) {
			seriesID[i] = addSeries("SubPop " + i);
		}
	}

	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);
		
		if (((PhysicsStatistics) state.statistics).best_of_run[0] != null) {
			this.seriesCollection.getSeries(0).clear();
			Individual best = ((PhysicsStatistics) state.statistics).best_of_run[0];

			PhysicsProblem.paused = true;
			PhysicsProblem p = (PhysicsProblem) state.evaluator.p_problem.clone();
			p.model = new PhysicsModel(LWJGL.getGL(), false);
			int frame = 0;
			while (frame < 1000) {
				p.runFrame(state, best, 0, 0, frame, p.model);
				Vector3f v = new Vector3f();
				p.model.ragdolls.get(0).bodies[BodyPart.BODYPART_HEAD.ordinal()]
						.getCenterOfMassPosition(v);
				addDataPoint(seriesID[0], frame, v.y);
				frame++;
			}
			p = null;
			PhysicsProblem.paused = false;
		}
	}
}
