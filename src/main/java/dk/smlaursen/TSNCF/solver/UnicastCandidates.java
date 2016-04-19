package dk.smlaursen.TSNCF.solver;

import java.util.ArrayList;
import org.jgrapht.GraphPath;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;

/** Class representing a routingCandidate, i.e. all considered routes for a given application streams*/
public class UnicastCandidates {
	//The Application
	private Application aApp;
	//The destination ID - Each multicast stream is split into multiple RoutingCandidates
	private Node aDestNode;
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
	
	public Application getApplication(){
		return aApp;
	}
	
	public Node getDestNode(){
		return aDestNode;
	}
	
	@Override
	public int hashCode(){
		return aApp.hashCode() + aDestNode.hashCode() + aRouting.hashCode();
	}
	
	@Override
	public String toString(){
		return aApp.toString();
	}
}

