package vuongdx.search;

import java.util.HashMap;

import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class LocalSearch {
	public ConstraintSystem cs;
	public IFunction[] f;
	public HashMap<String, VarIntLS[]> dVar;
	public IMoveLS moveRule;
	public ILegalMoveLS legalMoveRule;
	public ISelectMoveLS selectMoveRule;
	
	public LocalSearch() {
		
	}
	
	public LocalSearch(ConstraintSystem cs,
			IFunction[] f,
			HashMap<String, VarIntLS[]> dVar,
			IMoveLS moveRule,
			ILegalMoveLS legalMoveRule,
			ISelectMoveLS selectMoveRule) {
		this.cs = cs;
		this.f = f;
		this.dVar = dVar;
		this.moveRule = moveRule;
		this.legalMoveRule = legalMoveRule;
		this.selectMoveRule = selectMoveRule;
	}
	
	public int search() {
		IMoveLS[] moveList = this.moveRule.listMove(cs, f, dVar);
		IMoveLS[] legalMoveList = this.legalMoveRule.listLegal(cs, f, dVar, moveList);
		IMoveLS selectedMove = this.selectMoveRule.select(cs, f, dVar, moveList, legalMoveList);
		selectedMove.movePropagate(dVar);
		return cs.violations();
	}
	
	public int search(int numIter) {
		int it = 0;
		while (cs.violations() > 0 && it < numIter) {
			it++;
			this.search();
		}
		return cs.violations();
	}
}
