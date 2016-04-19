package dk.smlaursen.TSNCF.solver.GRASP;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.evaluator.Evaluator;
import dk.smlaursen.TSNCF.solver.Solver;
import dk.smlaursen.TSNCF.solver.GraphPaths;
import dk.smlaursen.TSNCF.solver.Multicast;
import dk.smlaursen.TSNCF.solver.Unicast;
import dk.smlaursen.TSNCF.solver.UnicastCandidates;

public class GraspSolver implements Solver {
	private double globalBestCost;
	private Set<Unicast> bestSolution;
	private final Object costLock = new Object();
	private ExecutorService exec;

	private static final int NO_OF_THREADS = 1;// Runtime.getRuntime().availableProcessors()-2;
	private static final int PROGRESS_PERIOD = 10000;
	private static final int MAX_HOPS = 20;
	private static final int K = 20;

	private static Logger logger = LoggerFactory.getLogger(GraspSolver.class.getSimpleName());

	private List<UnicastCandidates> avbRoutes;
	private List<Unicast> ttRoutes;
	private Graph<Node, GCLEdge> aTopology;
	private Evaluator aEval;

	@Override
	public List<Multicast> solve(Graph<Node, GCLEdge> topology, List<Application> applications, Evaluator eval, Duration dur) {
		///////////////////////////////////////////////////////
		//                  -- Setup --                       //
		///////////////////////////////////////////////////////
		globalBestCost = Double.MAX_VALUE;
		bestSolution = new HashSet<Unicast>();

		aTopology = topology;
		aEval = eval;

		GraphPaths gp = new GraphPaths(topology, applications, MAX_HOPS, K);
		avbRoutes = gp.getAVBRoutingCandidates();
		ttRoutes = gp.getTTRoutes();

		//For monitoring time and reporting of progress (as this may take some time)
		Timer timer = new Timer();
		if(logger.isInfoEnabled()){
			TimerTask progressUpdater = new TimerTask() {
				private int i = 0;
				private DecimalFormat numberFormat = new DecimalFormat(".00");
				@Override
				public void run() {
					//Report progress every 10sec

					float searchProgress = (++i* (float) PROGRESS_PERIOD) / dur.toMillis();
					logger.info("Searching "+numberFormat.format(searchProgress*100)+"% : CurrentBest "+numberFormat.format(globalBestCost));
				}
			};
			//If info is enabled, start a timer-task that reports the progress every PROGRESS_PERIOD
			timer.schedule(progressUpdater, PROGRESS_PERIOD, PROGRESS_PERIOD);
		}

		//Get number of processors
		if(logger.isInfoEnabled()){
			logger.info("Solving problem using "+NO_OF_THREADS+" threads");
		}
		exec = Executors.newFixedThreadPool(NO_OF_THREADS);
		for(int i=0; i < NO_OF_THREADS; i++){
			exec.execute(new GRASPRunnable());
		}
		try{
			exec.awaitTermination(dur.toMillis(),TimeUnit.MILLISECONDS);
			exec.shutdown();
		} catch(InterruptedException e){
			if(logger.isInfoEnabled()){
				logger.info("Executor interrupted");
			}
		} finally {
			//If not terminated, do a hard shutdown
			if(!exec.isTerminated()){
				exec.shutdownNow();
			}
			timer.cancel();
		}
		///////////////////////////////////////////////////////
		//                  -- DONE --                       //
		///////////////////////////////////////////////////////
		return Multicast.generateMulticasts(bestSolution);
	}

	@Override
	public void abort() {
		//Shutdown all threads. 
		exec.shutdown();
	}

	private class GRASPRunnable implements Runnable{
		@Override
		public void run() {
			//Just run until interrupted. 
			while(!Thread.currentThread().isInterrupted()){

				//Construct Greedy Randomized Solution
				List<Unicast> solution = constructSolution();
				if(solution == null){
					continue;
				}
				//Local Search (See if it can be improved)
//				solution = localSearch(solution);	
//				Map<Application, ArrayList<GraphPath<Node, GCLEdge>>> map = new HashMap<Application, ArrayList<GraphPath<Node, GCLEdge>>>();
//				List<Routing> routing = new ArrayList<Routing>();
//				for(int i = 0; i < partialSolution.size(); i++){
//					Application app = partialSolution.get(i).getApplication();
//					//This merges multiple destinations into one route
//					if(!map.containsKey(app)){
//						map.put(app, new ArrayList<GraphPath<Node, GCLEdge>>());
//					}
//					map.get(app).add(partialSolution.get(i).getRoutings().get(0));
//				}
//				//Convert map into Set<Routing>
//				for(Application app : map.keySet()){
//					routing.add(new Routing(app, map.get(app)));
//				}


				double cost = aEval.evaluate(solution, aTopology);

				//pre-check before entering critical-section
				if(cost < globalBestCost){
					synchronized(costLock){
						if(cost < globalBestCost){
							globalBestCost = cost;
							bestSolution.clear();
							bestSolution.addAll(solution);
						}
					}
				}
			}
		}
		
		private List<Unicast> constructSolution(){
			List<Unicast> partialSolution = new ArrayList<Unicast>();

			//Add all TT-Routes
			partialSolution.addAll(ttRoutes);

			//First we randomize the order of considering applications
			List<UnicastCandidates> aTemporaryList = new ArrayList<UnicastCandidates>(avbRoutes);
			List<UnicastCandidates> aRandomizedRoutingCandidateList = new ArrayList<UnicastCandidates>(avbRoutes.size());
			while(!aTemporaryList.isEmpty()){
				int index = ThreadLocalRandom.current().nextInt(aTemporaryList.size());
				aRandomizedRoutingCandidateList.add(aTemporaryList.remove(index));
			}
			aTemporaryList = null;

			//Then within an application, we select the
			for(int i = 0; i < aRandomizedRoutingCandidateList.size();i++){
				UnicastCandidates uc = aRandomizedRoutingCandidateList.get(i);

				double currBestCost = Double.MAX_VALUE;
				Unicast r;
				Unicast currBest = null;
				for(int u = 0; u < Math.max(3, K/3); u++){
					//As the candidates are in ordered by their shortest path first, it may be clever to increase the probability, 
					//Of selecting one of the first elements.
					int index = ThreadLocalRandom.current().nextInt(uc.getCandidates().size());
					//					int index = RandomDistributions.RouletteWheelDistribution(candidates.size());
					r = new Unicast(aRandomizedRoutingCandidateList.get(i).getApplication(), uc.getDestNode(), uc.getCandidates().get(index));
					partialSolution.add(r);
					double cost = aEval.evaluate(partialSolution, aTopology);
					if(cost < currBestCost){
						currBestCost = cost;
						currBest = r;
					}
					partialSolution.remove(r);

				}
				//We're only interested in feasible solutions
				if(currBest == null){
					return null;
				} else {
					partialSolution.add(currBest);
				}
			}
			return partialSolution;
		}


		private List<Unicast> localSearch(List<Unicast> solution){
			return solution;
		}
	}
}
