package dk.smlaursen.TSNSolver.solver.KShortestPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.evaluator.SimpleEvaluator;
import dk.smlaursen.TSNSolver.solver.Solver;
import dk.smlaursen.TSNSolver.solver.VLAN;

/**The KShortestPathSolver relies on the {@link KShortestPaths} algorithm in the jgrapht library to calculate the K shortest paths
 * for each src-dest nodes. Naturally, the greater K the better solution can be found, but as the shortest paths are in sorted order, the simples routes (Often yielding the best results) are evaluated first. 
 * So increase K with care, as it can quickly lead to excessive memory and computation time use. */
public class KShortestPathSolver implements Solver {
	private static final int K = 1;
	private static final int MAX_HOPS = 2;
	private static final int PROGRESS_PERIOD = 10000;
	
	private static Logger logger = LoggerFactory.getLogger(KShortestPathSolver.class.getSimpleName());

	//For storing the so far best solution
	private Set<VLAN> bestVlan = new HashSet<VLAN>();
	private boolean abortFlag;


	@Override
	public Set<VLAN> solve(final Graph<Node, DefaultEdge> topology,final List<Application> applications) {
		abortFlag = false;

		///////////////////////////////////////////////////////
		//-- First we retrieve all individual graphPaths  -- //
		///////////////////////////////////////////////////////
		logger.debug("Retrieving all individual graphPaths");

		// 2*app.size due to some of them having multiple destination. The size can increase and decrease accordingly
		//Notice we here use the VLAN structure somewhat different than intended
		ArrayList<VLAN> graphPaths = new ArrayList<VLAN>(2*applications.size());

		// Loop through each application and add it's K shortest paths to above arraylist
		for(Application app : applications){
			KShortestPaths<Node, DefaultEdge> shortestPaths = new KShortestPaths<Node, DefaultEdge>(topology, app.getSource(), K, MAX_HOPS);

			//For each destinations
			int noOfDests = app.getDestinations().length;
			for(int d = 0; d < noOfDests; d++){
				//Up to K paths to the destination exists
				ArrayList<GraphPath<Node,DefaultEdge>> appPaths = new ArrayList<GraphPath<Node, DefaultEdge>>(K);	
				//Retrieve the K shortest paths to the destination
				List<GraphPath<Node, DefaultEdge>> sp = shortestPaths.getPaths(app.getDestinations()[d]);
				//Abort If no such exists as the problem cannot be solved
				if(sp == null){
					logger.warn("Aborting, could not find a path from "+app.getSource()+" to "+app.getDestinations()[d]+" within "+MAX_HOPS+" hops");
					return bestVlan;
				}
				appPaths.addAll(sp);
				//Remove excess reserved space
				appPaths.trimToSize();
				//Add paths to global VLAN list
				graphPaths.add(new VLAN(app, appPaths));
			}
		}

		//Remove excess reserved space
		graphPaths.trimToSize();

		///////////////////////////////////////////////////////
		//-- Then we calculate each permutation of these  -- //
		///////////////////////////////////////////////////////
		logger.debug("Calculating permutations");

		CombinatoricTable table = new CombinatoricTable(graphPaths);
		logger.info("Found "+table.getNoOfCombinations() +" different permutations using K="+K);

		///////////////////////////////////////////////////////
		//-- Then we evaluate each of these permutation   -- //
		///////////////////////////////////////////////////////
		logger.debug("Evaluating each permutation");

		//For reporting of progress (as this may take some time)
		Timer timer = new Timer();
		TimerTask progressUpdater = new TimerTask() {
			@Override
			public void run() {
				//Report progress every 10sec
				if(logger.isInfoEnabled()){
					logger.info("Progress = "+table.getCurrCombination()+"/"+table.getNoOfCombinations());
				}
			}
		};

		//If info is enabled, start a task that reports the progress every PROGRESS_PERIOD
		if(logger.isInfoEnabled()){
			timer.schedule(progressUpdater, PROGRESS_PERIOD, PROGRESS_PERIOD);
		}

		//Start evaluating
		double bestCost = Double.MAX_VALUE;
		SimpleEvaluator eval = new SimpleEvaluator();

		while(!abortFlag && table.hasNext()){

			//Retrieve the set of VLANs
			Set<VLAN> curr = table.getSet();

			//Evaluate and store the so far best solution
			double currCost = eval.evaluate(curr, topology); 
			if(currCost < bestCost){
				bestVlan.clear();
				bestVlan.addAll(curr);
				bestCost = currCost;
				if(logger.isInfoEnabled()){
					logger.info("Found new best solution "+bestCost +" : "+bestVlan);
				}
			}
			table.next();

		}
		timer.cancel();
		//return best;
		return bestVlan;
	}


	@Override
	public void configure(Object param) {
		// TODO Auto-generated method stub
	}

	@Override
	public void abort() {
		abortFlag = true;
	}
}
