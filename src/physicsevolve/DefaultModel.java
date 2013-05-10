package physicsevolve;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.Transform;

import ec.EvolutionState;
import ec.util.Parameter;

public class DefaultModel extends Model {
	
	@Override
	public void reset(boolean resetSeed) {
		super.reset(resetSeed);
        for (int i = 0; i < 10; i++) {
			BoxShape bs = new BoxShape(new Vector3f(1f, 1f, 1f));
			Transform tr = new Transform();
			tr.setIdentity();
			tr.origin.set(0, -2.5f, 50 - i * 5);
			createRigidBody(i, 1f, tr, bs);
		}
	}
}
