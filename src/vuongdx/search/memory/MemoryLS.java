package vuongdx.search.memory;

import java.util.HashMap;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import vuongdx.search.IMemoryLS;
import vuongdx.search.IMoveLS;
import vuongdx.search.ISolutionLS;

public class MemoryLS implements IMemoryLS {
	protected HashMap<IMoveLS, Integer> move;
	protected HashMap<ISolutionLS, Integer> solution;
	protected HashMap<IConstraint, Integer> violation;
	protected HashMap<IFunction, Integer> value;
	protected Integer term;
	protected Integer it;

	@Override
	public void rememberMove(IMoveLS m) {

	}

	@Override
	public void rememberSolution(ISolutionLS s) {

	}

	@Override
	public boolean inMemory(IMoveLS m) {
		return false;
	}

	@Override
	public boolean inMemory(ISolutionLS s) {
		return false;
	}

	@Override
	public Integer getBestViolation(IConstraint cs) {
		return null;
	}

	@Override
	public void rememberBestValue(IFunction f) {

	}

	@Override
	public Integer getBestValue(IFunction f) {
		return null;
	}

	@Override
	public void rememberBestViolation(IConstraint cs, Integer v) {

	}
}
