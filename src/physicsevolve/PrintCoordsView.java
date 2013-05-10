package physicsevolve;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import ec.EvolutionState;
import ec.util.Parameter;

public class PrintCoordsView extends WorldView {

	public void setup(EvolutionState evolutionState, Parameter base) {
	}
	@Override
	public void render() {
		int numObjects = model.dynamicsWorld.getNumCollisionObjects();
		for (int i = 0; i < numObjects; i++) {
			CollisionObject colObj = model.dynamicsWorld
					.getCollisionObjectArray().getQuick(i);
			Transform tr = new Transform();
			colObj.getWorldTransform(tr);
			RigidBody body = RigidBody.upcast(colObj);
			body.getWorldTransform(tr);
			System.out.print(i+" : "+tr.origin.x+","+tr.origin.y+","+tr.origin.z+"\t");
		}
		System.out.println();
	}

	@Override
	public void destroy() {
	}

	@Override
	public boolean isCloseRequested() {
		return false;
	}

}
