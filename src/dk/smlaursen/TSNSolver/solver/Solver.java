package dk.smlaursen.TSNSolver.solver;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.architecture.Node;

public interface Solver {

	public Set<VLAN> solve(Graph<Node, DefaultEdge> topology, Set<Application> applications);
	
}
