package dk.smlaursen.TSNSolver.solver;

import java.util.ArrayList;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.architecture.Node;

/** Class representing a VLAN which tells the routing*/
public class VLAN {
	private Application aApp;
	private ArrayList<GraphPath<Node, DefaultEdge>> aRouting;
	
	public VLAN(Application app, ArrayList<GraphPath<Node, DefaultEdge>> paths){
		aApp = app;
		aRouting = paths;
	}
	
	public ArrayList<GraphPath<Node, DefaultEdge>> getRoutings(){
		return aRouting;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(aApp.getTitle()).append(" | ").append(aRouting);
		return sb.toString();
	}
}

