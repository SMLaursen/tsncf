package dk.smlaursen.TSNSolver.evaluator;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

/**Classes implementing the Evaluator interface are used to score a given {@link VLAN} assignment based on the actual topology. 
 * The score denotes an associated cost of doing that assignment and can be used to direct a {@link Solver} towards a less costly assignment.*/
public interface Evaluator {
	
	/** Evaluates the set of vlans and returns a score based on how well it fits*/
	public double evaluate(final Set<VLAN> vlans,final Graph<Node, DefaultEdge> graph);
}
