package dk.smlaursen.TSNCF.solver;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.GraphPathImpl;
import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.application.TTApplication;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;

public class GraphPaths {
	
	//For storing the AVB- and TT-Applications 
	private List<Unicast> ttRoutes;
	private List<UnicastCandidates> avbRoutes;

	public GraphPaths(final Graph<Node, GCLEdge> topology,final List<Application> applications, int MAX_HOPS, int K){
		///////////////////////////////////////////////////////
		//-- First we retrieve all individual graphPaths  -- //
		///////////////////////////////////////////////////////

		avbRoutes = new ArrayList<UnicastCandidates>();
		ttRoutes  = new ArrayList<Unicast>();
		// Loop through each application and add it's K shortest paths to above arraylist
		for(Application app : applications){
			//If TT-Application (explicitlyRouted) parse to VLAN and continue
			if(app instanceof TTApplication){
				ttRoutes.addAll(convertTTApplicationToRouting((TTApplication) app, topology));
				continue;
			}

			//Else SR-Application
			KShortestPaths<Node, GCLEdge> shortestPaths = new KShortestPaths<Node, GCLEdge>(topology, app.getSource(), K, MAX_HOPS);

			//For each destinations
			int noOfDests = app.getDestinations().length;
			for(int d = 0; d < noOfDests; d++){
				//Up to K paths to the destination exists
				ArrayList<GraphPath<Node,GCLEdge>> appPaths = new ArrayList<GraphPath<Node, GCLEdge>>(K);	
				//Retrieve the K shortest paths to the destination
				List<GraphPath<Node, GCLEdge>> sp = shortestPaths.getPaths(app.getDestinations()[d]);
				//Abort If no such exists as the problem cannot be solved
				if(sp == null){
					throw new InputMismatchException("Aborting, could not find a path from "+app.getSource()+" to "+app.getDestinations()[d]+" within "+MAX_HOPS+" hops");
				} else {
					appPaths.addAll(sp);
					//Remove excess reserved space
					appPaths.trimToSize();
					//Add paths to global VLAN list
					avbRoutes.add(new UnicastCandidates(app, app.getDestinations()[d], appPaths));
				}
			}
		}
	}
	
	public List<UnicastCandidates> getAVBRoutingCandidates(){
		return avbRoutes;
	}
	
	public List<Unicast> getTTRoutes(){
		return ttRoutes;
	}
	
	/**Method which converts the routing of a {@link TTApplication} to a {@link Unicast}*/
	private List<Unicast> convertTTApplicationToRouting(TTApplication ttApp, Graph<Node, GCLEdge> graph){
		ArrayList<Unicast> aRouting = new ArrayList<Unicast>(ttApp.getDestinations().length);
		try{
			for(int i=0; i<ttApp.getDestinations().length; i++){
				List<GCLEdge> edgeList = new LinkedList<GCLEdge>();
				
				Node prev = ttApp.getSource();
				for(Node curr : ttApp.getExplicitPath().getPath().get(i)){
					edgeList.add(graph.getEdge(prev, curr));
					prev = curr;
				}
				edgeList.add(graph.getEdge(prev, ttApp.getDestinations()[i]));
				
				for(GCLEdge edge : edgeList){
					//Put the GCL on all the GCLEdges in the edgeList
					edge.addGCL(ttApp.getExplicitPath().getGCL());
				}
				
				GraphPath<Node, GCLEdge> gp = new GraphPathImpl<Node, GCLEdge>(graph, ttApp.getSource(), ttApp.getDestinations()[i], edgeList, 1.0);
				aRouting.add(new Unicast(ttApp,ttApp.getDestinations()[i], gp));
			}
			
			return aRouting;
		} catch(IllegalArgumentException e){
			throw new IllegalArgumentException("The specified vertice-Route for "+ttApp.getTitle()+" do not form a path.");
		}
	}
}
