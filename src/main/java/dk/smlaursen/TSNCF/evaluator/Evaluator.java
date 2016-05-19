package dk.smlaursen.TSNCF.evaluator;

import java.util.Collection;

import org.jgrapht.Graph;

import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.solver.Unicast;

/**Classes implementing the Evaluator interface are used to score a given {@link Unicast} assignment based on the actual topology. 
 * The score denotes an associated cost of doing that assignment and can be used to direct a {@link Solver} towards a less costly assignment.*/
public interface Evaluator {
	
	/** Evaluates the set of route and returns a score based on how well it fits.
	 * @param route the Collection of {@link Unicast}s containing the SR-Apps to be evaluated
	 * @param graph the {@link Graph} containing the topology.
	 * @return the {@Cost}*/
	public Cost evaluate(final Collection<Unicast> route, final Graph<Node, GCLEdge> graph);
}

