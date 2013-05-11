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
import org.lwjgl.opengl.GL11;
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

import ec.EvolutionState;
import ec.app.physics.PhysicsProblem;
import ec.util.Parameter;

public class DefaultWorldView extends WorldView {
	protected IGL gl;
	protected int glutScreenWidth;
	protected int glutScreenHeight;
	private Vector3f wireColor = new Vector3f();
	private final Transform tr = new Transform();

	@Override
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

		// set the modelview matrix back to the identity
		GL11.glLoadIdentity();
	}

	public void setup(EvolutionState evolutionState, Parameter base) {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
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
		reshape(800, 600);

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

		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();

		if (glutScreenWidth > glutScreenHeight) {
			float aspect = glutScreenWidth / (float) glutScreenHeight;
			gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 10000.0);
		} else {
			float aspect = glutScreenHeight / (float) glutScreenWidth;
			gl.glFrustum(-1.0, 1.0, -aspect, aspect, 1.0, 10000.0);
		}

		gl.glMatrixMode(GL_MODELVIEW);
		
	}

	@Override
	public void destroy() {
		Display.destroy();
	}

	@Override
	public boolean isCloseRequested() {
		return !Display.isCloseRequested()
				&& !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
	}
}
