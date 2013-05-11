package physicsevolve;

import javax.vecmath.Vector3f;

import ec.Setup;


public abstract class WorldView implements Setup {

	public Model model;
	protected float cameraDistance = 15f;

	protected float ele = 20f;
	protected float azi = 0f;
	private static final float STEPSIZE = 5;
	protected final Vector3f cameraPosition = new Vector3f(0f, 0f, 0f);
	protected final Vector3f cameraTargetPosition = new Vector3f(0f, 0f, 0f);
	protected final Vector3f cameraUp = new Vector3f(0f, 1f, 0f);
	public float yaw = 0;

	
	public abstract void render();

	public abstract void destroy();

	public abstract boolean isCloseRequested();

	public void stepLeft() {
		azi -= STEPSIZE;
		if (azi < 0) {
			azi += 360;
		}
		updateCamera();
	}

	public void stepRight() {
		azi += STEPSIZE;
		if (azi >= 360) {
			azi -= 360;
		}
		updateCamera();
	}

	public void stepFront() {
		cameraPosition.x -= 100 * (float)Math.sin(Math.toRadians(yaw));
		cameraPosition.z += 100 * (float)Math.cos(Math.toRadians(yaw));
		updateCamera();
	}

	public void stepBack() {
		ele -= STEPSIZE;
		if (ele < 0) {
			ele += 360;
		}
		updateCamera();
	}

	public void zoomIn() {
		cameraDistance -= 0.4f;
		updateCamera();
		if (cameraDistance < 0.1f) {
			cameraDistance = 0.1f;
		}
	}

	public void zoomOut() {
		cameraDistance += 0.4f;
		updateCamera();
	}
	public void updateCamera() {System.out.println("sdf");}
}