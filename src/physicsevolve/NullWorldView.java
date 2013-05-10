package physicsevolve;

import ec.EvolutionState;
import ec.util.Parameter;

public class NullWorldView extends WorldView {

	public void setup(EvolutionState evolutionState, Parameter base) {
	}
	@Override
	public void render() {

	}

	@Override
	public void destroy() {
	}

	@Override
	public boolean isCloseRequested() {
		return false;
	}

}
