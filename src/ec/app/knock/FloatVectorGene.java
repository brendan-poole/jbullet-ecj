package ec.app.knock;

import ec.EvolutionState;
import ec.vector.Gene;

public class FloatVectorGene extends Gene {

	public float x;
	public float y;
	public float z;
	
	@Override
	public int hashCode() {
		return (int) (x+y+z);
	}

	@Override
	public boolean equals(Object other) {
		FloatVectorGene v = (FloatVectorGene) other;
		return v.x == x && v.y == y && v.z == z;
	}

	@Override
	public void reset(EvolutionState state, int thread) {
		x = state.random[thread].nextFloat() - .5f;
		y = state.random[thread].nextFloat() - .5f;
		z = state.random[thread].nextFloat() - .5f;
	}

}
