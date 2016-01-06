package dk.smlaursen.TSNSolver;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.parser.ApplicationParser;
import dk.smlaursen.TSNSolver.parser.TopologyParser;
import dk.smlaursen.TSNSolver.solver.Solver;
import dk.smlaursen.TSNSolver.solver.VLAN;
import dk.smlaursen.TSNSolver.solver.KShortestPath.KShortestPathSolver;
import dk.smlaursen.TSNSolver.visualization.Visualizer;

public class Main {
	public static final boolean display = true;
	
	//TODO Add parameter handling
	//TODO Extend JGraphT to include GraphML parser 
	//TODO Add application parser
	//TODO Add TTApplication and Create simple TTApplication layout pre-processor and validator
	//TODO Add real Evaluator
	//TODO Create GUI in separate project
	//TODO Put this on GitHub
	
	public static void main(String[] args){
		Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());
//		
		//Parse Architecture
		logger.debug("Parsing Topology");
		Graph<Node, DefaultEdge> graph= TopologyParser.parse();
		logger.info("Parsed topology ");
		
		Visualizer vis = new Visualizer(graph);
		//Display Application?
		if(display){
			vis.topologyPanel();
		}
		//Parse Applications
		logger.debug("Parsing application set");
		List<Application> apps = ApplicationParser.parse();
		System.out.println(apps);
		logger.info("Parsed applications  ");
		
		//Solve problem
		logger.debug("Solving problem");
		Solver s = new KShortestPathSolver();
		Set<VLAN> sol = s.solve(graph, apps);
		logger.info("Found solution ");
		
		if(display){
			vis.addSolutions(sol);
		}
	}
}
