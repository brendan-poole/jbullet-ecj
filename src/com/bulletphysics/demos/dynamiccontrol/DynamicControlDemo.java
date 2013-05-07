package com.bulletphysics.demos.dynamiccontrol;

import static com.bulletphysics.demos.opengl.IGL.GL_COLOR_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_DEPTH_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_LINES;

import javax.vecmath.Vector3f;

import org.lwjgl.LWJGLException;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConeTwistConstraint;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

public class DynamicControlDemo extends DemoApplication {

	private float time;
	private float cyclePeriod; // in milliseconds
	private float muscleStrength;
	
	HingeConstraint[] j = new HingeConstraint[10];
	
	private ObjectArrayList<TestRig> rigs = new ObjectArrayList<TestRig>();

	RigidBody[] bodies = new RigidBody[10];
	public DynamicControlDemo(IGL gl) {
		super(gl);
	}

	public void initPhysics() {
		// Setup the basic world
		time = 0.0f;
		cyclePeriod = 1000.0f; // in milliseconds
		muscleStrength = 0.05f;

		setCameraDistance(5.0f);

		DefaultCollisionConfiguration collision_config = new DefaultCollisionConfiguration();

		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collision_config);

		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		// BroadphaseInterface overlappingPairCache = new
		// AxisSweep3(worldAabbMin, worldAabbMax);
		// BroadphaseInterface overlappingPairCache = new SimpleBroadphase();
		BroadphaseInterface overlappingPairCache = new DbvtBroadphase();

		ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,
				overlappingPairCache, constraintSolver, collision_config);

		// Setup a big ground box
		{
			CollisionShape groundShape = new BoxShape(new Vector3f(5f, 10f, 5f));
			// TODO
			// m_collisionShapes.push_back(groundShape);
			Transform groundTransform = new Transform();
			groundTransform.setIdentity();
			groundTransform.origin.set(0f, -10f, 0f);
			localCreateRigidBody(0f, groundTransform, groundShape);
		}

		for (int i = 0; i < 2; i++) {
			CollisionShape node = new BoxShape(new Vector3f(.5f, .5f, .5f));
			Transform tr = new Transform();
			tr.setIdentity();
			tr.origin.set(i * 1f, .5f, 2f);
			bodies[i] = localCreateRigidBody(1f, tr, node);
			//bodies[i].setDamping(0.1f, .1f);
			if (i > 0) {
				Vector3f v = new Vector3f(-.5f,0,0);
				Vector3f v1 = new Vector3f(.5f,0,0);
				Point2PointConstraint joint = new Point2PointConstraint(
						bodies[i], bodies[i - 1], v, v1);
				dynamicsWorld.addConstraint(joint, false);
			}
		}
		
		for (int i = 0; i < 2; i++) {
			CollisionShape node = new BoxShape(new Vector3f(.5f, .5f, .5f));
			Transform tr = new Transform();
			tr.setIdentity();
			tr.origin.set(i * 1f, .5f, 0f);
			bodies[i + 2] = localCreateRigidBody(1f, tr, node);
			//bodies[i].setDamping(0.1f, .1f);
			if (i > 0) {
				Transform tr1 = new Transform();
				tr.origin.set(-.5f,0,0);
				tr1.origin.set(.5f,0,0);
				ConeTwistConstraint j = new ConeTwistConstraint(bodies[i + 2], bodies[i + 2 - 1], tr, tr1);
				j.setLimit(-.1f,-.1f,-.1f);
				dynamicsWorld.addConstraint(j, true);
			}
		}
		for (int i = 0; i < 2; i++) {
			CollisionShape node = new BoxShape(new Vector3f(.5f, .5f, .5f));
			Transform tr = new Transform();
			tr.setIdentity();
			tr.origin.set(i * 1f, .5f, -2f);
			bodies[i + 2] = localCreateRigidBody(.1f, tr, node);
			//bodies[i].setDamping(0.1f, .1f);
			if (i > 0) {
				Transform tr1 = new Transform();
				tr.origin.set(-.5f,0,0);
				tr1.origin.set(.5f,0,0);
				j[i + 2 - 1] = new HingeConstraint(bodies[i + 2], bodies[i + 2 - 1], tr, tr1);
				j[i + 2 - 1].setLimit(- BulletGlobals.SIMD_2_PI * 0.625f, 0.2f);
				dynamicsWorld.addConstraint(j[i + 2 - 1], true);
				j[i + 2 - 1].enableAngularMotor(true, .1f, .1f);

			}
		}
		clientResetScene();
	}

	@Override
	public void clientMoveAndDisplay() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// BDP added the following. clock is reset within getdelta below
		while (clock.getTimeMicroseconds() < 1000000f / 60f) {
		}

		// simple dynamics world doesn't handle fixed-time-stepping
		float ms = getDeltaTimeMicroseconds();
		float minFPS = 1000000f / 60f;
		if (ms > minFPS) {
			ms = minFPS;
		}

		time += ms;

		//
		// set per-frame sinusoidal position targets using angular motor
		// (hacky?)
		//
		for (int r = 0; r < rigs.size(); r++) {
			for (int i = 0; i < 2 * TestRig.NUM_LEGS; i++) {
				HingeConstraint hingeC = (HingeConstraint) (rigs.getQuick(r)
						.getJoints()[i]);
				float curAngle = hingeC.getHingeAngle();

				float targetPercent = ((int) (time / 1000) % (int) (cyclePeriod))
						/ cyclePeriod;
				float targetAngle = 0.5f * (1.0f + (float) Math.sin(i + r
						+ BulletGlobals.SIMD_2_PI * targetPercent));
				float targetLimitAngle = hingeC.getLowerLimit() + targetAngle
						* (hingeC.getUpperLimit() - hingeC.getLowerLimit());
				float angleError = targetLimitAngle - curAngle;
				float desiredAngularVel = 1000000.f * angleError / ms;
				hingeC.enableAngularMotor(true, desiredAngularVel,
						muscleStrength);
			}
		}
		
		
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(ms / 1000000.f);
			// optional but useful: debug drawing
			// dynamicsWorld.debugDrawWorld();
		}

		// This draws the x y z lines
		for (int i = dynamicsWorld.getCollisionObjectArray().size() - 1; i >= 0; i--) {
			CollisionObject obj = dynamicsWorld.getCollisionObjectArray()
					.getQuick(i);
			RigidBody body = RigidBody.upcast(obj);
			 drawFrame(body.getWorldTransform(new Transform()));
		}

		renderme();

		// glFlush();
		// glutSwapBuffers();
	}

	@Override
	public void displayCallback() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (dynamicsWorld != null) {
			// dynamicsWorld.debugDrawWorld();
		}

		// renderme();

		// glFlush();
		// glutSwapBuffers();
	}

	@Override
	public void keyboardCallback(char key, int x, int y, int modifiers) {
		switch (key) {
		case '+':
		case '=':
			cyclePeriod /= 1.1f;
			if (cyclePeriod < 1.f) {
				cyclePeriod = 1.f;
			}
			break;
		case '-':
		case '_':
			cyclePeriod *= 1.1f;
			break;
		case '[':
			muscleStrength /= 1.1f;
			break;
		case ']':
			muscleStrength *= 1.1f;
			break;
		default:
			super.keyboardCallback(key, x, y, modifiers);
		}
	}

	private void vertex(Vector3f v) {
		gl.glVertex3f(v.x, v.y, v.z);
	}

	private void drawFrame(Transform tr) {
		final float size = 1.0f;

		gl.glBegin(GL_LINES);

		// x
		gl.glColor3f(255.f, 0, 0);
		Vector3f vX = new Vector3f();
		vX.set(size, 0, 0);
		tr.transform(vX);
		vertex(tr.origin);
		vertex(vX);

		// y
		gl.glColor3f(0, 255.f, 0);
		Vector3f vY = new Vector3f();
		vY.set(0, size, 0);
		tr.transform(vY);
		vertex(tr.origin);
		vertex(vY);

		// z
		gl.glColor3f(0, 0, 255.f);
		Vector3f vZ = new Vector3f();
		vZ.set(0, 0, size);
		tr.transform(vZ);
		vertex(tr.origin);
		vertex(vZ);

		gl.glEnd();
	}

	public static void main(String[] args) throws LWJGLException {
		DynamicControlDemo demoApp = new DynamicControlDemo(LWJGL.getGL());
		demoApp.initPhysics();

		LWJGL.main(args, 1024, 768,
				"Bullet Physics Demo. http://bullet.sf.net", demoApp);
	}

}
