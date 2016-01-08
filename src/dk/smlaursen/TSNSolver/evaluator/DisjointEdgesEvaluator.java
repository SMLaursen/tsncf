package dk.smlaursen.TSNSolver.evaluator;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

/** SimpleEvaluator is an evaluator that counts the number of disjoint edges used.
 *  The fewer the better. */
public class DisjointEdgesEvaluator implements Evaluator {

	@Override
	public double evaluate(final Set<VLAN> vlans,final Graph<Node, DefaultEdge> graph) {
		double cost = 0;
		
		for(VLAN vl : vlans){
			Set<DefaultEdge> edges = new HashSet<DefaultEdge>();
			for(GraphPath<Node, DefaultEdge> gp : vl.getRoutings()){
				//Hashset so only unique edges will be stored for this route
				edges.addAll(gp.getEdgeList());
			}
			//Cost is then simply just the sum of all the edges
			cost += edges.size();
		}
		return cost;
	}
}
