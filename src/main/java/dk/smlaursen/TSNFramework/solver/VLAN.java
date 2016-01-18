package dk.smlaursen.TSNFramework.solver;

import java.util.ArrayList;
import org.jgrapht.GraphPath;

import dk.smlaursen.TSNFramework.application.Application;
import dk.smlaursen.TSNFramework.architecture.GCLEdge;
import dk.smlaursen.TSNFramework.architecture.Node;

/** Class representing a VLAN which tells the routing*/
public class VLAN {
	private Application aApp;
	private ArrayList<GraphPath<Node, GCLEdge>> aRouting;
	
	public VLAN(Application app, ArrayList<GraphPath<Node, GCLEdge>> paths){
		aApp = app;
		aRouting = paths;
	}
	
	public ArrayList<GraphPath<Node, GCLEdge>> getRoutings(){
		return aRouting;
	}
	
	public Application getApplication(){
		return aApp;
	}
	
	@Override
	public int hashCode(){
		return aApp.hashCode() + aRouting.hashCode();
	}
	
	@Override
	public String toString(){
		return aApp.toString();
	}
}

