package physicsevolve;

import ec.Setup;


public abstract class WorldView implements Setup {

	public Model model;

	public abstract void render();

	public abstract void destroy();

	public abstract boolean isCloseRequested();

	public void updateCamera() {System.out.println("sdf");}
}