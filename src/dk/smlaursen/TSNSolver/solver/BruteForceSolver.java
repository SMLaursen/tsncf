package dk.smlaursen.TSNSolver.solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.evaluater.SimpleEvaluator;

public class BruteForceSolver implements Solver {
	
	@Override
	public Set<VLAN> solve(Graph<Node, DefaultEdge> topology, Set<Application> applications) {
		
		Application app1 = applications.iterator().next();
		
		KShortestPaths<Node, DefaultEdge> sp = new KShortestPaths<Node, DefaultEdge>(topology, app1.getSource(), 1, 7);
		
		int noOfDests = app1.getDestinations().length;
		ArrayList<GraphPath<Node, DefaultEdge>> gps = new ArrayList<GraphPath<Node, DefaultEdge>>(noOfDests);
		for(int i = 0; i < noOfDests; i++){
			gps.add(i, sp.getPaths(app1.getDestinations()[i]).get(0));
		}

		//Combinatoric, try all possibilities
		Set<VLAN> vlans = new HashSet<VLAN>();
		vlans.add(new VLAN(app1, gps));
		
		SimpleEvaluator eval = new SimpleEvaluator();
		return vlans;
	}
}
