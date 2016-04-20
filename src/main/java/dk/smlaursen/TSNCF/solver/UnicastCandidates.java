package dk.smlaursen.TSNCF.solver;

import java.util.ArrayList;
import org.jgrapht.GraphPath;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;

/** Class representing a routingCandidate, i.e. all considered routes for a given application streams*/
public class UnicastCandidates extends Route{
	//The ArrayList of GraphPaths making up all the
	private ArrayList<GraphPath<Node, GCLEdge>> aRouting;
	
	public UnicastCandidates(Application app, Node destNode, ArrayList<GraphPath<Node, GCLEdge>> paths){
		aApp = app;
		aDestNode = destNode;
		aRouting = paths;
	}
	
	public ArrayList<GraphPath<Node, GCLEdge>> getCandidates(){
		return aRouting;
	}
}

