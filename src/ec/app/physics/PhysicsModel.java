package ec.app.physics;

import static com.bulletphysics.demos.opengl.IGL.GL_COLOR_BUFFER_BIT;
import static com.bulletphysics.demos.opengl.IGL.GL_DEPTH_BUFFER_BIT;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.IGL;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;

public class PhysicsModel extends DemoApplication {

    public class AveragedInput {

        float[] v;
        int current;
        float min, max;

        public AveragedInput(int size, float min, float max) {
            v = new float[size];
            for (int i = 0; i < v.length; i++) {
                v[i] = 0;
            }
            this.min = min;
            this.max = max;
        }

        public void add(float f) {
            if (current == v.length) {
                current = 0;
            }
            if (f > 1) {
                v[current] = 1;
            } else if (f < -1) {
                v[current] = -1;
            } else {
                v[current] = f;
            }
            v[current] = v[current] * max;
            //TODO: range is fixed at max ; min not used
            current++;
        }

        public float getValue() {
            float ret = 0;
            for (int i = 0; i < v.length; i++) {
                ret += v[i] / v.length;
            }
            return ret;
        }
    }
    public static final float PI = 3.14159265358979323846f;
    public static final float PI_2 = 1.57079632679489661923f;
    public static final float PI_4 = 0.785398163397448309616f;
    public static final float LIFT_EPS = 0.0000001f;
    public List<RagDoll> ragdolls = new ArrayList<RagDoll>();

    public PhysicsModel(IGL gl,boolean visible) {
        super(gl);
        this.visible= visible;
        if (visible) {
            try {
                Display.setDisplayMode(new DisplayMode(800, 600));
                Display.setTitle("Physics");
                Display.create(new PixelFormat(0, 24, 0));
                Keyboard.create();
                Keyboard.enableRepeatEvents(true);
                Mouse.create();
            } catch (LWJGLException ex) {
                Logger.getLogger(PhysicsProblem.class.getName()).log(Level.SEVERE, null, ex);
            }
            //gl.init();
            reshape(800, 600);
            setCameraDistance(10f);
            myinit();
        }
        initPhysics();

    }
    public boolean visible;
    public RigidBody bod;
    public RigidBody bod2;
    public RigidBody bod3;
    public RigidBody bod4;
    public RigidBody bod5;
    public HingeConstraint liftHinge;
    public HingeConstraint liftHinge2;
    public HingeConstraint liftHinge3;
    public HingeConstraint liftHinge4;
    AveragedInput hingeMotor1;
    AveragedInput hingeMotor2;
    AveragedInput hingeMotor3;
    AveragedInput hingeMotor4;

    AveragedInput inputs[];

    public void initPhysics() {

        inputs = new AveragedInput[30];
        for(int i = 0;i < 30;i++)
            inputs[i] = new AveragedInput(1,-1,1);

        // Setup the basic world
        DefaultCollisionConfiguration collision_config = new DefaultCollisionConfiguration();

        CollisionDispatcher dispatcher = new CollisionDispatcher(collision_config);

        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        BroadphaseInterface overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
        //BroadphaseInterface overlappingPairCache = new SimpleBroadphase();

        //#ifdef USE_ODE_QUICKSTEP
        //btConstraintSolver* constraintSolver = new OdeConstraintSolver();
        //#else
        ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
        //#endif

        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver, collision_config);

        dynamicsWorld.setGravity(new Vector3f(0f, -30f, 0f));


        //dynamicsWorld.setDebugDrawer(new GLDebugDrawer(gl));

        // Setup a big ground box
        {
            CollisionShape groundShape = new BoxShape(new Vector3f(1000f, 10f, 1000f));
            Transform groundTransform = new Transform();
            groundTransform.setIdentity();
            groundTransform.origin.set(0f, -15f, 0f);
            localCreateRigidBody(0f, groundTransform, groundShape);
        }

        // Spawn one ragdoll
         spawnRagdoll();

        CollisionShape colShape = new BoxShape(new Vector3f(2, 1, 1));
        Transform startTransform = new Transform();

/*
        {
            float mass = 1f;
            Vector3f localInertia = new Vector3f(0, 0, 0);
            colShape.calculateLocalInertia(mass, localInertia);
            startTransform.setIdentity();
            startTransform.origin.set(0, 0, 0);
            DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
            RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
            bod = new RigidBody(rbInfo);
            dynamicsWorld.addRigidBody(bod);
        }
        {
            float mass = 1f;
            Vector3f localInertia = new Vector3f(0, 0, 0);
            colShape.calculateLocalInertia(mass, localInertia);
            startTransform.setIdentity();
            startTransform.origin.set(2, 0, 0);
            DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
            RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
            bod2 = new RigidBody(rbInfo);
            dynamicsWorld.addRigidBody(bod2);
        }
        {
            float mass = 1f;
            Vector3f localInertia = new Vector3f(0, 0, 0);
            colShape.calculateLocalInertia(mass, localInertia);
            startTransform.setIdentity();
            startTransform.origin.set(4, 0, 0);
            DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
            RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
            bod3 = new RigidBody(rbInfo);
            dynamicsWorld.addRigidBody(bod3);
        }
        {(float) (
            float mass = 1f;
            Vector3f localInertia = new Vector3f(0, 0, 0);
            colShape.calculateLocalInertia(mass, localInertia);
            startTransform.setIdentity();
            startTransform.origin.set(6f, 0, 0);
            DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
            RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
            bod4 = new RigidBody(rbInfo);
            dynamicsWorld.addRigidBody(bod4);
        }
        {
            float mass = 1f;
            Vector3f localInertia = new Vector3f(0, 0, 0);
            colShape.calculateLocalInertia(mass, localInertia);
            startTransform.setIdentity();
            startTransform.origin.set(8f, 0, 0);
            DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
            RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
            bod5 = new RigidBody(rbImotorsnfo);
            dynamicsWorld.addRigidBody(bod5);
        }

        {
            Transform localA = new Transform(), localB = new Transform();
            localA.setIdentity();
            localB.setIdentity();
            MatrixUtil.setEulerZYX(localA.basis, 0, PI_2, 0);
            localA.origin.set(0.0f, 0.0f, -1.0f);
            MatrixUtil.setEulerZYX(localB.basis, 0, PI_2, 0);
            localB.origin.set(0.0f, 0.0f, 1.0f);
            liftHinge = new HingeConstraint(bod, bod2, localA, localB);
            liftHinge.setLimit(-PI / 2f, PI / 2f);
            dynamicsWorld.addConstraint(liftHinge, true);
        }
        {
            Transform localA = new Transform(), localB = new Transform();
            localA.setIdentity();
            localB.setIdentity();
            MatrixUtil.setEulerZYX(localA.basis, 0, PI_2, 0);
            localA.origin.set(0.0f, 0.0f, -1.0f);
            MatrixUtil.setEulerZYX(localB.basis, 0, PI_2, 0);
            localB.origin.set(0.0f, 0.0f, 1.0f);
            liftHinge2 = new HingeConstraint(bod2, bod3, localA, localB);
            liftHinge2.setLimit(-PI / 2f, PI / 2f);
            dynamicsWorld.addConstraint(liftHinge2, true);
        }
        {
            Transform localA = new Transform(), localB = new Transform();
            localA.setIdentity();
            localB.setIdentity();
            MatrixUtil.setEulerZYX(localA.basis, 0, PI_2, 0);
            localA.origin.set(0.0f, 0.0f, -1.0f);
            MatrixUtil.setEulerZYX(localB.basis, 0, PI_2, 0);
            localB.origin.set(0.0f, 0.0f, 1.0f);
            liftHinge3 = new HingeConstraint(bod3, bod4, localA, localB);
            liftHinge3.setLimit(-PI / 2f, PI / 2f);
            dynamicsWorld.addConstraint(liftHinge3, true);


        }
        {
            Transform localA = new Transform(), localB = new Transform();
            localA.setIdentity();
            localB.setIdentity();
            MatrixUtil.setEulerZYX(localA.basis, 0, PI_2, 0);
            localA.origin.set(0.0f, 0.0f, -1.0f);
            MatrixUtil.setEulerZYX(localB.basis, 0, PI_2, 0);
            localB.origin.set(0.0f, 0.0f, 1.0f);
            liftHinge4 = new HingeConstraint(bod4, bod5, localA, localB);
            liftHinge4.setLimit(-PI / 2f, PI / 2f);
            dynamicsWorld.addConstraint(liftHinge4, true);


        }
 */
        /*
        Transform localA = new Transform(), localB = new Transform();
        localA.setIdentity();
        localB.setIdentity();
        localA.origin.set(2f, 0f, 0f);
        localB.origin.set(4f, 0f, 0f);
        ConeTwistConstraint c = new ConeTwistConstraint(bod,bod2,localA,localB);
        c.setLimit(.5f,.5f, .5f);
        dynamicsWorld.addConstraint(c,true);
         */
        /*
        {
        Point2PointConstraint p = new Point2PointConstraint(bod,bod2,new Vector3f(2.5f,0,0),new Vector3f(-2,0,0));
        dynamicsWorld.addConstraint(p, false);
        }
         */
        /*
        {
        Point2PointConstraint p = new Point2PointConstraint(bod2,bod3,new Vector3f(0,0,0),new Vector3f(2,0,0));
        dynamicsWorld.addConstraint(p, true);
        }
         */

        /*
        Transform localA = new Transform(), localB = new Transform();
        boolean useLinearReferenceFrameA = true;
        localA.setIdentity();
        localB.setIdentity();
        localA.origin.set(2f, 0f, 0f);
        localB.origin.set(4f, 0f, 0f);

        Generic6DofConstraint joint6DOF;
        joint6DOF = new Generic6DofConstraint(bod2, bod3, localA, localB, useLinearReferenceFrameA);
        Vector3f tmp = new Vector3f(0,0,0);
        //tmp.set(-BulletGlobals.SIMD_PI * 0.3f, -BulletGlobals.FLT_EPSILON, -BulletGlobals.SIMD_PI * 0.3f);
        joint6DOF.setAngularLowerLimit(tmp);
        //tmp.set(BulletGlobals.SIMD_PI * 0.5f, BulletGlobals.FLT_EPSILON, BulletGlobals.SIMD_PI * 0.3f);
        joint6DOF.setAngularUpperLimit(tmp);

        dynamicsWorld.addConstraint(joint6DOF, true);
         */

        clientResetScene();
    }

    public void spawnRagdoll() {
        spawnRagdoll(false);
    }

    public void spawnRagdoll(boolean random) {
        RagDoll ragDoll = new RagDoll(dynamicsWorld, new Vector3f(0f, 0f, 10f), 5f);

        ragdolls.add(ragDoll);
    }

    @Override
    public void moveAndDisplay() {
        if(visible) {
            this.keyboardMouse();


        Display.update();
}
        clientMoveAndDisplay();
    }

    @Override
    public void clientMoveAndDisplay() {
        if (visible) {
            gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    		while(clock.getTimeMicroseconds()<1000000f/60f) {  }
        }
        for(int i = 0;i < 30;i++)
            this.ragdolls.get(0).motors[i].targetVelocity = inputs[i].getValue();



        for(int b = 0; b < this.ragdolls.get(0).bodies.length; b++) {
                    this.ragdolls.get(0).bodyCollisions[b] = true;
        }
        int n = dynamicsWorld.getDispatcher().getNumManifolds();
        for(int i=0;i<n;i++) {
            PersistentManifold cm = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);


                for(int b = 0; b < this.ragdolls.get(0).bodies.length; b++) {
                if(cm.getBody0() == this.ragdolls.get(0).bodies[b]
                        || cm.getBody1() == this.ragdolls.get(0).bodies[b]) {
                    this.ragdolls.get(0).bodyCollisions[b] = true;
                }
            }


        }

/*
        liftHinge.setLimit(-PI / 3f, PI / 3f);
        liftHinge.enableAngularMotor(true, hingeMotor1.getValue(), 1f);

        liftHinge2.setLimit(-PI / 3f, PI / 3f);
        liftHinge2.enableAngularMotor(true, hingeMotor2.getValue(), 1f);

        liftHinge3.setLimit(-PI / 3f, PI / 3f);
        liftHinge3.enableAngularMotor(true, hingeMotor3.getValue(), 1f);

        liftHinge4.setLimit(-PI / 3f, PI / 3f);
        liftHinge4.enableAngularMotor(true, hingeMotor4.getValue(), 1f);
*/

        float ms = getDeltaTimeMicroseconds();
        float minFPS = 1000000f / 60f;
        if (ms > minFPS) {
            ms = minFPS;
        }

        if (dynamicsWorld != null) {
            //dynamicsWorld.stepSimulation(ms / 1000000.f);
            dynamicsWorld.stepSimulation(1/30f,1);
            // optional but useful: debug drawing
            //dynamicsWorld.debugDrawWorld();
        }

        if (visible) {
            renderme();
        }

    }

    @Override
    public void keyboardCallback(char key, int x, int y, int modifiers) {
        switch (key) {
            case 'e':
                spawnRagdoll(true);
                break;
            default:
                super.keyboardCallback(key, x, y, modifiers);
        }
    }

    public void keyboardMouse() {
        int modifiers = 0;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            modifiers |= KeyEvent.SHIFT_DOWN_MASK;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            modifiers |= KeyEvent.CTRL_DOWN_MASK;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA)) {
            modifiers |= KeyEvent.ALT_DOWN_MASK;
        }

        while (Keyboard.next()) {
            if (Keyboard.getEventCharacter() != '\0') {
                keyboardCallback(Keyboard.getEventCharacter(), Mouse.getX(), Mouse.getY(), modifiers);
            }

            if (Keyboard.getEventKeyState()) {
                specialKeyboard(Keyboard.getEventKey(), Mouse.getX(), Mouse.getY(), modifiers);
            } else {
                specialKeyboardUp(Keyboard.getEventKey(), Mouse.getX(), Mouse.getY(), modifiers);
            }

            if (Keyboard.getEventKey() == Keyboard.KEY_1) {
                visible = true;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_2) {
                visible = false;
            }
        }

        while (Mouse.next()) {
            if (Mouse.getEventButton() != -1) {
                int btn = Mouse.getEventButton();
                if (btn == 1) {
                    btn = 2;
                } else if (btn == 2) {
                    btn = 1;
                }
                mouseFunc(btn, Mouse.getEventButtonState() ? 0 : 1, Mouse.getEventX(), Display.getDisplayMode().getHeight() - 1 - Mouse.getEventY());
            }
            mouseMotionFunc(Mouse.getEventX(), Display.getDisplayMode().getHeight() - 1 - Mouse.getEventY());
        }

    }
    public void setDynamicsWorld(DynamicsWorld d) {
        this.dynamicsWorld = d;
    }
}
