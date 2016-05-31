package dk.smlaursen.TSNCF.solver.KShortestPath;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.jgrapht.Graph;
import org.jgrapht.alg.KShortestPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.evaluator.Cost;
import dk.smlaursen.TSNCF.evaluator.Evaluator;
import dk.smlaursen.TSNCF.evaluator.ModifiedAVBEvaluatorCost;
import dk.smlaursen.TSNCF.solver.GraphPaths;
import dk.smlaursen.TSNCF.solver.Multicast;
import dk.smlaursen.TSNCF.solver.Solver;
import dk.smlaursen.TSNCF.solver.Unicast;
import dk.smlaursen.TSNCF.solver.UnicastCandidates;

/**The KShortestPathSolver_SR relies on the {@link KShortestPaths} algorithm in the jgrapht library to calculate the K shortest paths
 * for each src-dest nodes of an SRApplication. Naturally, the greater K the better solution can be found, but as the shortest paths are in sorted order, the simples routes (Often yielding the best results) are evaluated first. 
 * So increase K with care, as it can quickly lead to excessive computation time use. */
public class KShortestPathSolver_SR implements Solver {
	private int K = 7;
	private static final int MAX_HOPS = 10;
	private static final int PROGRESS_PERIOD = 10000;

	private static Logger logger = LoggerFactory.getLogger(KShortestPathSolver_SR.class.getSimpleName());

	//For storing the so far best solution
	private Set<Unicast> bestRoute = new HashSet<Unicast>();

	//For storing the TT-Applications 
	private List<Unicast> ttRoutes;
	private List<UnicastCandidates> avbRoutes;

	private boolean abortFlag;
	
	public KShortestPathSolver_SR(int K) {
		this.K = K;
	}

	@Override
	public List<Multicast> solve(final Graph<Node, GCLEdge> topology,final List<Application> applications, Evaluator eval, Duration dur) {
		abortFlag = false;
		
		///////////////////////////////////////////////////////
		//-- First we retrieve all individual graphPaths  -- //
		///////////////////////////////////////////////////////
		logger.debug("Retrieving all individual graphPaths");
		GraphPaths gp = new GraphPaths(topology, applications, MAX_HOPS, K);
		ttRoutes = gp.getTTRoutes();
		avbRoutes = gp.getAVBRoutingCandidates();

		///////////////////////////////////////////////////////
		//-- Then we calculate each permutation of these  -- //
		///////////////////////////////////////////////////////
		logger.debug("Calculating permutations");

		CombinatoricTable table = new CombinatoricTable(avbRoutes);
		logger.info("Found "+table.getNoOfCombinations() +" different permutations using K="+K);

		///////////////////////////////////////////////////////
		//-- Then we evaluate each of these permutation   -- //
		///////////////////////////////////////////////////////
		logger.debug("Evaluating each permutation");

		//For monitoring time and reporting of progress (as this may take some time)
		Timer timer = new Timer();
		TimerTask progressUpdater = new TimerTask() {
			@Override
			public void run() {
				//Report progress every 10sec
				if(logger.isInfoEnabled()){
					logger.info("Progress = "+(float) table.getCurrCombination()/table.getNoOfCombinations() * 100+"%");
				}
			}
		};
		//If info is enabled, start a timer-task that reports the progress every PROGRESS_PERIOD
		long currTime = System.currentTimeMillis();
		timer.schedule(progressUpdater, PROGRESS_PERIOD, PROGRESS_PERIOD);

		//Start evaluating
		Cost bestCost = new ModifiedAVBEvaluatorCost();
		//TODO Parallelize
		while(!abortFlag && table.hasNext()){

			//Retrieve the set of Routes
			List<Unicast> curr = table.getCandidateSolution();
			curr.addAll(ttRoutes);

			//Evaluate and store the so far best solution
			Cost currCost = eval.evaluate(curr, topology); 
			if(currCost.getTotalCost() < bestCost.getTotalCost()){
				bestRoute.clear();
				bestRoute.addAll(curr);
				bestCost = currCost;
				if(logger.isInfoEnabled()){
					logger.info("Found new best solution "+bestCost +" : "+bestRoute);
				}
			}
			table.next();

		}
		if(logger.isInfoEnabled()){
			logger.info("Finished in "+(System.currentTimeMillis()-currTime)/1000.0+"s");
		}
		timer.cancel();
		
		//return best route as multicasts;
		return Multicast.generateMulticasts(bestRoute);
	}

	@Override
	public void abort() {
		abortFlag = true;
	}
}
