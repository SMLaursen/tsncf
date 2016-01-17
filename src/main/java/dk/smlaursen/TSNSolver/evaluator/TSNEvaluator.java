package dk.smlaursen.TSNSolver.evaluator;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.application.SRApplication;
import dk.smlaursen.TSNSolver.application.TTApplication;
import dk.smlaursen.TSNSolver.architecture.GCLEdge;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

/** This {@link Evaluator} is used for calculating the cost of the given {@link VLAN} assignment taking SR- and TT-timings into account.
 *  The evaluation is based on the following penalties :
 *  <li> {@value #QUEUE_LENGTH_PENALTY} times the maximum number of queued frames for each queue. This essentially performs load-balancing and tries to avoid that our solution relies on a lot of bursts.
 *  <li> {@value #EDGE_PENALTY} for each edge. This favors shorter paths and penalizes disjoint multi-cast routes.
 *  <li> {@value #UNSCHEDULABLE_PENALTY} per Traffic Class not being schedulable. This ensures that we disregard unschedulable setups. 
 *  */
public class TSNEvaluator implements Evaluator {
	// For ensuring a reasonably short path
	public static final double QUEUE_LENGTH_PENALTY = 0.5;
	// For avoiding very tight / fully utilized schedules
	public static final double EDGE_PENALTY = 0.5;
	public static final double UNSCHEDULABLE_PENALTY = 10000.0;

	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, GCLEdge> graph) {
		double cost = 0;
		for(VLAN vl : vlans){
			//////////////////////////////////////////////////
			// FIRST WE CALCULATE THE EDGE PENALITY			//
			//////////////////////////////////////////////////
			Set<DefaultEdge> edges = new HashSet<DefaultEdge>();
			for(GraphPath<Node, GCLEdge> gp : vl.getRoutings()){
				//Hashset so only unique edges will be stored for this route
				edges.addAll(gp.getEdgeList());
			}
			//Cost is then simply just the sum of the edges times the edge-penalty
			cost += edges.size() * EDGE_PENALTY;

			//////////////////////////////////////////////////
			// THEN WE CHECK THE TIMINGS					//
			//////////////////////////////////////////////////
			Application app = vl.getApplication();
			if(app instanceof SRApplication){
				SRApplication srApp = (SRApplication) app;

			} else if(app instanceof TTApplication){
				TTApplication ttApp = (TTApplication) app;
			} else {

			}
		}
		return cost;
	}
}
