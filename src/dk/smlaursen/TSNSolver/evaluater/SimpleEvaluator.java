package dk.smlaursen.TSNSolver.evaluater;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

public class SimpleEvaluator implements evaluator {

	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, DefaultEdge> graph) {
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
