package physicsevolve;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.linearmath.Transform;

import ec.EvolutionState;
import ec.util.Parameter;

public class DefaultModel extends Model {
	
	public void setup(EvolutionState evolutionState, Parameter base) {
        super.setup(evolutionState, base);
		BoxShape cs = new BoxShape(new Vector3f(1f, 1f, 1f));
		Transform tr = new Transform();
		tr.setIdentity();
        tr.origin.set(0f, 200f, 0f);
        createRigidBody("box", 1f, tr, cs);
	}
}
