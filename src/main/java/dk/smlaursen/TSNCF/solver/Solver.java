package dk.smlaursen.TSNCF.solver;

import java.time.Duration;
import java.util.List;

import org.jgrapht.Graph;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.evaluator.Evaluator;

/** Classes implementing the Solver interface are intended to return the {@link Multicast} assignments that given the topology and set of applications solves the problem.
 *  For non-analysis based solvers, the solve implementation will most likely make use of an {@link Evaluator} to score and direct the different guesses.  */
public interface Solver {
	
	/**@param topology the {@link Graph}
	 * @param applications the set of {@link Application}s.
	 * @param eval the {@link Evaluator} to use for scoring.
	 * @param dur the {@link Duration} the solver is allowed to run. 
	 * @return the {@link Solution} containing the solution this solver deemed best*/
	public Solution solve(final Graph<Node, GCLEdge> topology,final List<Application> applications, Evaluator eval, Duration dur);
	
	/** Instructs the solver method to abort and return the currently best solution, if any, at the time of aborting*/
	public void abort();
}
