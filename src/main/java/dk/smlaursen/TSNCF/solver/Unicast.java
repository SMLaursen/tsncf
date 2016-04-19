package dk.smlaursen.TSNCF.solver;

import org.jgrapht.GraphPath;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;

/** Class representing the routing*/
public class Unicast {
	//The Application
	private Application aApp;
	//The Destination Node 
	private Node aDestNode;
	//The ArrayList of GraphPaths (One for each destination)
	private GraphPath<Node, GCLEdge> aRoute;
	
	public Unicast(Application app, Node destNode,  GraphPath<Node, GCLEdge> route){
		aApp = app;
		aDestNode = destNode;
		aRoute = route;
	}
	
	public GraphPath<Node, GCLEdge> getRoute(){
		return aRoute;
	}
	
	public Application getApplication(){
		return aApp;
	}
	
	public Node getDestNode(){
		return aDestNode;
	}
	
	@Override
	public int hashCode(){
		return aApp.hashCode() + aDestNode.hashCode() + aRoute.hashCode();
	}
	
	@Override
	public String toString(){
		return aApp.toString() + "->" + aDestNode;
	}
}

