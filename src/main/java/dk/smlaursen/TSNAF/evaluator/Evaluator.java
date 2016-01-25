package dk.smlaursen.TSNAF.evaluator;

import java.util.Set;

import org.jgrapht.Graph;

import dk.smlaursen.TSNAF.architecture.GCLEdge;
import dk.smlaursen.TSNAF.architecture.Node;
import dk.smlaursen.TSNAF.solver.VLAN;

/**Classes implementing the Evaluator interface are used to score a given {@link VLAN} assignment based on the actual topology. 
 * The score denotes an associated cost of doing that assignment and can be used to direct a {@link Solver} towards a less costly assignment.*/
public interface Evaluator {
	
	/** Evaluates the set of vlans and returns a score based on how well it fits.
	 * @param vlans the set of {@link VLAN}s containing the SR-Apps to be evaluated
	 * @param graph the {@link Graph} containing the topology.
	 * @return the score*/
	public double evaluate(final Set<VLAN> vlans, final Graph<Node, GCLEdge> graph);
}
