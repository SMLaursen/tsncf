package dk.smlaursen.TSNCF.solver.KShortestPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleGraphPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.application.TTApplication;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.evaluator.Evaluator;
import dk.smlaursen.TSNCF.solver.Solver;
import dk.smlaursen.TSNCF.solver.VLAN;

/**The KShortestPathSolver_SR relies on the {@link KShortestPaths} algorithm in the jgrapht library to calculate the K shortest paths
 * for each src-dest nodes of an SRApplication. Naturally, the greater K the better solution can be found, but as the shortest paths are in sorted order, the simples routes (Often yielding the best results) are evaluated first. 
 * So increase K with care, as it can quickly lead to excessive memory and computation time use. */
public class KShortestPathSolver_SR implements Solver {
	private static final int K = 7;
	private static final int MAX_HOPS = 10;
	private static final int PROGRESS_PERIOD = 10000;

	private static Logger logger = LoggerFactory.getLogger(KShortestPathSolver_SR.class.getSimpleName());

	//For storing the so far best solution
	private Set<VLAN> bestVlan = new HashSet<VLAN>();

	//For storing the TT-Applications 
	private Set<VLAN> ttVlan  = new HashSet<VLAN>();

	private boolean abortFlag;

	@Override
	public Set<VLAN> solve(final Graph<Node, GCLEdge> topology,final List<Application> applications, Evaluator eval) {
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

			//If TT-Application (explicitlyRouted) parse to VLAN and continue
			if(app instanceof TTApplication){
				ttVlan.add(convertToVLAN((TTApplication) app, topology));
				continue;
			}

			//Else SR-Application
			KShortestPaths<Node, GCLEdge> shortestPaths = new KShortestPaths<Node, GCLEdge>(topology, app.getSource(), K, MAX_HOPS);

			//For each destinations
			int noOfDests = app.getDestinations().length;
			for(int d = 0; d < noOfDests; d++){
				//Up to K paths to the destination exists
				ArrayList<GraphPath<Node,GCLEdge>> appPaths = new ArrayList<GraphPath<Node, GCLEdge>>(K);	
				//Retrieve the K shortest paths to the destination
				List<GraphPath<Node, GCLEdge>> sp = shortestPaths.getPaths(app.getDestinations()[d]);
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
					logger.info("Progress = "+(float) table.getCurrCombination()/table.getNoOfCombinations() * 100+"%");
				}
			}
		};

		//If info is enabled, start a task that reports the progress every PROGRESS_PERIOD
		if(logger.isInfoEnabled()){
			timer.schedule(progressUpdater, PROGRESS_PERIOD, PROGRESS_PERIOD);
		}

		//Start evaluating
		double bestCost = Double.MAX_VALUE;

		//TODO Parallelize
		while(!abortFlag && table.hasNext()){

			//Retrieve the set of VLANs
			Set<VLAN> curr = table.getSet();
			curr.addAll(ttVlan);

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

	/**Method which converts the routing of a {@link TTApplication} to a {@link VLAN}*/
	private VLAN convertToVLAN(TTApplication ttApp, Graph<Node, GCLEdge> graph){
		ArrayList<GraphPath<Node, GCLEdge>> aRouting = new ArrayList<GraphPath<Node, GCLEdge>>(ttApp.getDestinations().length);
		try{
			for(int i=0; i<ttApp.getDestinations().length; i++){
				List<Node> path = new LinkedList<Node>();
				path.add(ttApp.getSource());
				path.addAll(ttApp.getPath().get(i));
				path.add(ttApp.getDestinations()[i]);
				
				SimpleGraphPath<Node, GCLEdge> p = new SimpleGraphPath<Node, GCLEdge>((SimpleGraph<Node, GCLEdge>) graph, path , 1.0);
				aRouting.add(p);
			}
			return new VLAN(ttApp, aRouting);
		} catch(IllegalArgumentException e){
			throw new IllegalArgumentException("The specified vertice-Route for "+ttApp.getTitle()+" do not form a path.");
		}
	}

	@Override
	public void abort() {
		abortFlag = true;
	}
}
