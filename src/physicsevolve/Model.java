package physicsevolve;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Model {
	
	public BiMap<String, RigidBody> bodies = HashBiMap.create();
	public DynamicsWorld dynamicsWorld = null;

	public Model() {
        DefaultCollisionConfiguration collision_config = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collision_config);
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        BroadphaseInterface overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
        ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver, collision_config);
        dynamicsWorld.setGravity(new Vector3f(0f, -30f, 0f));

        CollisionShape cs = new BoxShape(new Vector3f(1000f, 10f, 1000f));
        Transform tr = new Transform();
        tr.setIdentity();
        tr.origin.set(0f, -15f, 0f);
        createRigidBody("ground", 0f, tr, cs);
        
        cs = new BoxShape(new Vector3f(1f, 1f, 1f));
        tr = new Transform();
        tr.setIdentity();
        tr.origin.set(0f, 200f, 0f);
        createRigidBody("box", 1f, tr, cs);
	}

	public void move() {
		dynamicsWorld.stepSimulation(1/30f,1);
	}

	public RigidBody createRigidBody(String name, float mass, Transform startTransform, CollisionShape shape) {
		
		// rigidbody is dynamic if and only if mass is non zero, otherwise static
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(cInfo);
		dynamicsWorld.addRigidBody(body);
		bodies.put(name, body);
		return body;
	}


}
