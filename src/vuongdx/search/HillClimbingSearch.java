package vuongdx.search;

import vuongdx.search.legal.BestMove;
import vuongdx.search.select.RandomSelection;

import java.util.HashMap;

import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;
import vuongdx.search.IMoveLS;;

public final class HillClimbingSearch extends LocalSearch {
	public HillClimbingSearch(ConstraintSystem cs,
			IFunction[] f,
			HashMap<String, VarIntLS[]> dVar,
			IMoveLS moveRule) {
		super(cs, f, dVar, moveRule, new BestMove(), new RandomSelection());
	}
}
