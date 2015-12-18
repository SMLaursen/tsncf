package dk.smlaursen.TSNSolver.evaluater;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

public interface evaluator {
	/** Evaluates the set of vlans and returns a score based on how well it fits*/
	public double evaluate(Set<VLAN> vlans, Graph<Node, DefaultEdge> graph);
}
