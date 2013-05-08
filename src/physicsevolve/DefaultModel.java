package physicsevolve;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.linearmath.Transform;

import ec.EvolutionState;
import ec.util.Parameter;

public class DefaultModel extends Model {
	
	public void setup(EvolutionState evolutionState, Parameter base) {
        super.setup(evolutionState, base);
        this.init();
	}
	
	@Override
	public void init() {
        for (int i = 0; i < 10; i++) {
			BoxShape cs = new BoxShape(new Vector3f(1f, 1f, 1f));
			Transform tr = new Transform();
			tr.setIdentity();
			tr.origin.set(0, -2.5f, 50 - i * 5);
			createRigidBody(i, 1f, tr, cs);
		}
    
        
		
	}
	

}
