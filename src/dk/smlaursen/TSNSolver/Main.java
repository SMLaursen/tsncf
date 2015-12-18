package dk.smlaursen.TSNSolver;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.parser.ApplicationParser;
import dk.smlaursen.TSNSolver.parser.TopologyParser;
import dk.smlaursen.TSNSolver.solver.BruteForceSolver;
import dk.smlaursen.TSNSolver.solver.Solver;
import dk.smlaursen.TSNSolver.visualization.TopologyVisualizer;

public class Main {
	public static final boolean display = true;
	
	public static void main(String[] args){
		Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());
		
		
		//Parse Topology
		logger.info("Parsing Topology");
		Graph<Node, DefaultEdge> graph= TopologyParser.parse();
		
		if(display){
			TopologyVisualizer.display(graph);
		}
		
		//Parse Applications
		logger.info("Parsing application set");
		Set<Application> apps = ApplicationParser.parse(graph);
//		System.out.println(apps);
		
		logger.info("Solving problem");
		Solver s = new BruteForceSolver();
		s.solve(graph, apps);
	}
}
