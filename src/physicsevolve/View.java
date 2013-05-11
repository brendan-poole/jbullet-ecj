package physicsevolve;

import static com.bulletphysics.demos.opengl.IGL.GL_AMBIENT;
import static com.bulletphysics.demos.opengl.IGL.GL_COLOR_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_DEPTH_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_DEPTH_TEST;
import static com.bulletphysics.demos.opengl.IGL.GL_DIFFUSE;
import static com.bulletphysics.demos.opengl.IGL.GL_LESS;
import static com.bulletphysics.demos.opengl.IGL.GL_LIGHT0;
import static com.bulletphysics.demos.opengl.IGL.GL_LIGHT1;
import static com.bulletphysics.demos.opengl.IGL.GL_LIGHTING;
import static com.bulletphysics.demos.opengl.IGL.GL_MODELVIEW;
import static com.bulletphysics.demos.opengl.IGL.GL_POSITION;
import static com.bulletphysics.demos.opengl.IGL.GL_PROJECTION;
import static com.bulletphysics.demos.opengl.IGL.GL_SMOOTH;
import static com.bulletphysics.demos.opengl.IGL.GL_SPECULAR;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.demos.opengl.GLShapeDrawer;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.demos.opengl.LWJGL;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

import ec.app.physics.PhysicsProblem;

public class View {
	protected float cameraDistance = 15f;
	protected IGL gl;
	protected int glutScreenWidth = 0;
	protected int glutScreenHeight = 0;
	protected float ele = 20f;
	protected float azi = 0f;
	protected final Vector3f cameraPosition = new Vector3f(0f, 0f, 0f);
	protected final Vector3f cameraTargetPosition = new Vector3f(0f, 0f, 0f);
	protected final Vector3f cameraUp = new Vector3f(0f, 1f, 0f);
	protected int forwardAxis = 2;
	private Vector3f wireColor = new Vector3f();
	private final Transform tr = new Transform();
	Model model;

	public void render() {
		Display.update();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		int numObjects = model.dynamicsWorld.getNumCollisionObjects();
		wireColor.set(1f, 0f, 0f);
		for (int i = 0; i < numObjects; i++) {
			CollisionObject colObj = model.dynamicsWorld
					.getCollisionObjectArray().getQuick(i);
			RigidBody body = RigidBody.upcast(colObj);

			if (body != null && body.getMotionState() != null) {
				DefaultMotionState myMotionState = (DefaultMotionState) body
						.getMotionState();
				tr.set(myMotionState.graphicsWorldTrans);
			} else {
				colObj.getWorldTransform(tr);
			}

			wireColor.set(1f, 1f, 0.5f); // wants deactivation
			if ((i & 1) != 0) {
				wireColor.set(0f, 0f, 1f);
			}

			// color differently for active, sleeping, wantsdeactivation states
			if (colObj.getActivationState() == 1) // active
			{
				if ((i & 1) != 0) {
					// wireColor.add(new Vector3f(1f, 0f, 0f));
					wireColor.x += 1f;
				} else {
					// wireColor.add(new Vector3f(0.5f, 0f, 0f));
					wireColor.x += 0.5f;
				}
			}
			if (colObj.getActivationState() == 2) // ISLAND_SLEEPING
			{
				if ((i & 1) != 0) {
					// wireColor.add(new Vector3f(0f, 1f, 0f));
					wireColor.y += 1f;
				} else {
					// wireColor.add(new Vector3f(0f, 0.5f, 0f));
					wireColor.y += 0.5f;
				}
			}

			GLShapeDrawer.drawOpenGL(gl, tr, colObj.getCollisionShape(),
					wireColor, 0);
		}

		// gl.glDisable(GL_LIGHTING);
		// gl.glColor3f(0f, 0f, 0f);
		// gl.glEnable(GL_LIGHTING);
		updateCamera();
	}

	public View(Model model) {
		this.model = model;
		try {
			Display.setDisplayMode(new DisplayMode(1024, 768));
			Display.setTitle("Physics");
			Display.create(new PixelFormat(0, 24, 0));
			Keyboard.create();
			Keyboard.enableRepeatEvents(true);
			Mouse.create();
		} catch (LWJGLException ex) {
			Logger.getLogger(PhysicsProblem.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		gl = LWJGL.getGL();
		reshape(1024, 768);
		setCameraDistance(10f);

		float[] light_ambient = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
		float[] light_diffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] light_specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		/* light_position is NOT default value */
		float[] light_position0 = new float[] { 1.0f, 10.0f, 1.0f, 0.0f };
		float[] light_position1 = new float[] { -1.0f, -10.0f, -1.0f, 0.0f };

		gl.glLight(GL_LIGHT0, GL_AMBIENT, light_ambient);
		gl.glLight(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
		gl.glLight(GL_LIGHT0, GL_SPECULAR, light_specular);
		gl.glLight(GL_LIGHT0, GL_POSITION, light_position0);

		gl.glLight(GL_LIGHT1, GL_AMBIENT, light_ambient);
		gl.glLight(GL_LIGHT1, GL_DIFFUSE, light_diffuse);
		gl.glLight(GL_LIGHT1, GL_SPECULAR, light_specular);
		gl.glLight(GL_LIGHT1, GL_POSITION, light_position1);

		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);
		gl.glEnable(GL_LIGHT1);

		gl.glShadeModel(GL_SMOOTH);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LESS);

		gl.glClearColor(0.7f, 0.7f, 0.7f, 0f);
	}

	public void reshape(int w, int h) {
		glutScreenWidth = w;
		glutScreenHeight = h;

		gl.glViewport(0, 0, w, h);
		updateCamera();
	}

	public void setCameraDistance(float dist) {
		cameraDistance = dist;
	}

	public void updateCamera() {
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		float rele = ele * 0.01745329251994329547f; // rads per deg
		float razi = azi * 0.01745329251994329547f; // rads per deg

		Quat4f rot = new Quat4f();
		QuaternionUtil.setRotation(rot, cameraUp, razi);

		Vector3f eyePos = new Vector3f();
		eyePos.set(0f, 0f, 0f);
		VectorUtil.setCoord(eyePos, forwardAxis, -cameraDistance);

		Vector3f forward = new Vector3f();
		forward.set(eyePos.x, eyePos.y, eyePos.z);
		if (forward.lengthSquared() < BulletGlobals.FLT_EPSILON) {
			forward.set(1f, 0f, 0f);
		}
		Vector3f right = new Vector3f();
		right.cross(cameraUp, forward);
		Quat4f roll = new Quat4f();
		QuaternionUtil.setRotation(roll, right, -rele);

		Matrix3f tmpMat1 = new Matrix3f();
		Matrix3f tmpMat2 = new Matrix3f();
		tmpMat1.set(rot);
		tmpMat2.set(roll);
		tmpMat1.mul(tmpMat2);
		tmpMat1.transform(eyePos);

		cameraPosition.set(eyePos);

		if (glutScreenWidth > glutScreenHeight) {
			float aspect = glutScreenWidth / (float) glutScreenHeight;
			gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 10000.0);
		} else {
			float aspect = glutScreenHeight / (float) glutScreenWidth;
			gl.glFrustum(-1.0, 1.0, -aspect, aspect, 1.0, 10000.0);
		}

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.gluLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z,
				cameraTargetPosition.x, cameraTargetPosition.y,
				cameraTargetPosition.z, cameraUp.x, cameraUp.y, cameraUp.z);
	}

	public void destroy() {
		Display.destroy();
	}

	public boolean isCloseRequested() {
		return Display.isCloseRequested();
	}
}
