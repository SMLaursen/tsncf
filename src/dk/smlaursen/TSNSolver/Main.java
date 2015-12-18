package dk.smlaursen.TSNSolver;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

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
		
		//Parse Topology
		Graph<Node, DefaultEdge> graph= TopologyParser.parse();
		
		if(display){
			TopologyVisualizer.display(graph);
		}
		
		//Parse Applications
		Set<Application> apps = ApplicationParser.parse(graph);
//		System.out.println(apps);
		
		Solver s = new BruteForceSolver();
		s.solve(graph, apps);
	}
}
