package physicsevolve;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class DefaultModel extends Model {
	private static final long serialVersionUID = 1L;

	@Override
	public void reset(boolean resetSeed) {
		super.reset(resetSeed);
        for (int i = 0; i < 10; i++) {
			BoxShape bs = new BoxShape(new Vector3f(1f, 1f, 1f));
			Transform tr = new Transform();
			tr.setIdentity();
			tr.origin.set((float)Math.cos(Math.PI * 2 / 10 * i) * 5, -2f, (float)Math.sin(Math.PI * 2 / 10 * i) * 5);
			RigidBody b = createRigidBody(i, 1f, tr, bs);
			b.setSleepingThresholds(0, 0);
			
		}
	}
}
