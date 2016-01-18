package dk.smlaursen.TSNAF.evaluator;

import java.util.Set;

import org.jgrapht.Graph;

import dk.smlaursen.TSNAF.architecture.GCLEdge;
import dk.smlaursen.TSNAF.architecture.Node;
import dk.smlaursen.TSNAF.solver.VLAN;

public class MonteCarloEvaluator implements Evaluator {

	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, GCLEdge> graph) {
		double cost = 0;
		return cost;
	}
}
