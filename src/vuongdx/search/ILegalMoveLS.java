package vuongdx.search;

import java.util.HashMap;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public interface ILegalMoveLS {
	
	public IMoveLS[] listLegal(IConstraint cs,
			IFunction[] f,
			HashMap<String, VarIntLS[]> dVar,
			IMoveLS[] moveList);
	
}