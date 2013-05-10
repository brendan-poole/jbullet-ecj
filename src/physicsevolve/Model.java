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

import ec.EvolutionState;
import ec.Setup;
import ec.util.Parameter;

public abstract class Model implements Setup, Cloneable {

	public BiMap<Integer, RigidBody> bodies;
	public DynamicsWorld dynamicsWorld;
	DefaultCollisionConfiguration collision_config;
	CollisionDispatcher dispatcher;
	BroadphaseInterface overlappingPairCache;
	ConstraintSolver constraintSolver;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		collision_config = new DefaultCollisionConfiguration();
		constraintSolver = new SequentialImpulseConstraintSolver();
		dispatcher = new CollisionDispatcher(collision_config);
		reset(false);
	}

	public void move() {
		dynamicsWorld.stepSimulation(1 / 30f, 1);
	}

	protected RigidBody createRigidBody(int name, float mass, Transform startTransform,
			CollisionShape shape) {

		// rigidbody is dynamic if and only if mass is non zero, otherwise
		// static
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape,
				localInertia);
		RigidBody body = new RigidBody(cInfo);
		dynamicsWorld.addRigidBody(body);
		bodies.put(name, body);
		return body;
	}

	public void reset(boolean resetSeed) {
		bodies = HashBiMap.create();
		
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);

		dispatcher = new CollisionDispatcher(collision_config);

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache,
				constraintSolver, collision_config);
		dynamicsWorld.setGravity(new Vector3f(0f, -30f, 0f));

		if(resetSeed) constraintSolver.reset();

		CollisionShape cs = new BoxShape(new Vector3f(1000f, 10f, 1000f));
		Transform tr = new Transform();
		tr.setIdentity();
		tr.origin.set(0f, -15f, 0f);
		createRigidBody(-1, 0f, tr, cs);
	}
}