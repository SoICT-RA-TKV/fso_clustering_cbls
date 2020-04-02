package vuongdx.search;

import java.util.HashMap;

import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;
import vuongdx.search.legal.TabuSTMMove;
import vuongdx.search.memory.TabuMemory;
import vuongdx.search.select.RandomSTMSelection;
import vuongdx.search.solutiongenerator.GRandom;

public class TabuSearch extends LocalSearch {
	private TabuMemory mem;
	
	public TabuSearch(ConstraintSystem cs,
			IFunction[] f,
			HashMap<String, VarIntLS[]> dVar,
			IMoveLS moveRule, Integer term) {
		this.mem = new TabuMemory(term);
		this.cs = cs;
		this.f = f;
		this.dVar = dVar;
		this.moveRule = moveRule;
		this.legalMoveRule = new TabuSTMMove(this.mem);
		this.selectMoveRule = new RandomSTMSelection(this.mem);
	}
	
	public TabuSearch(ConstraintSystem cs,
			IFunction[] f,
			HashMap<String, VarIntLS[]> dVar,
			IMoveLS moveRule) {
		this(cs, f, dVar, moveRule, 0);
	}

	public int search(int numIter, int maxStable, ISolutionGeneratorLS g) {
		int it = 0;
		int nic = 0;
		this.mem.rememberBestViolation(cs, cs.violations());
		while (cs.violations() > 0 && it < numIter) {
			System.out.println("Iter " + it + ": " + cs.violations());
			it++;
			this.search();
			if (cs.violations() < this.mem.getBestViolation(cs)) {
				nic = 0;
				this.mem.rememberBestViolation(cs, cs.violations());
			} else {
				nic++;
				if (nic >= maxStable) {
					g.generateSolution(dVar);
				}
			}
		}
		return cs.violations();
	}
}
