package dk.smlaursen.TSNCF.solver.KShortestPath;

import java.util.ArrayList;
import java.util.List;
import org.jgrapht.GraphPath;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.solver.Unicast;
import dk.smlaursen.TSNCF.solver.UnicastCandidates;

public class CombinatoricTable {
	//FIXME ensure no overflow
	private long noOfCombinations = 1;
	private long currCombination = 1;
	
	private boolean hasNext;
	private byte[] indexTable;
	private byte[] sizes;
	private List<UnicastCandidates> graphPaths;
	
	public CombinatoricTable(List<UnicastCandidates> paths){
		hasNext = true;
		
		graphPaths = paths;
		indexTable = new byte[graphPaths.size()];
		sizes = new byte[graphPaths.size()];
		
		//Store all the sizes
		for(int i = 0; i < sizes.length; i++){
			sizes[i] = (byte) graphPaths.get(i).getCandidates().size();
			noOfCombinations *= sizes[i];
		}
	}
	
	public void next(){
		if(indexTable.length == 0){
			hasNext = false;
			return;
		}
		currCombination++;
		byte index = 0;
		while(hasNext && ++indexTable[index] == sizes[index]){
			indexTable[index] = 0;
			//When overflowing
			if(++index == indexTable.length){
				hasNext = false; 
				break;
			}
		}
	}
	
	public List<Unicast> getCandidateSolution(){
		List<Unicast> routing = new ArrayList<Unicast>();
		for(int i = 0; i< graphPaths.size(); i++){
			Application app = graphPaths.get(i).getApplication();
			Node destNode = graphPaths.get(i).getDestNode();
			GraphPath<Node, GCLEdge> gp = graphPaths.get(i).getCandidates().get(indexTable[i]);
			routing.add(new Unicast(app, destNode, gp));
		}
		return routing;
	}
	
//	public Set<Routing> getSet(){
//		Map<Application, ArrayList<GraphPath<Node, GCLEdge>>> map = new HashMap<Application, ArrayList<GraphPath<Node, GCLEdge>>>();
//		Set<Routing> routing = new HashSet<Routing>();
//		
//		//TODO performance can be improved by an incremental "find and replace" instead of this redo everything naive approach
//		for(int i = 0; i < graphPaths.size(); i++){
//			Application app = graphPaths.get(i).getApplication();
//			//This merges multiple destinations into one route
//			if(!map.containsKey(app)){
//				map.put(app, new ArrayList<GraphPath<Node, GCLEdge>>());
//			}
//			map.get(app).add(graphPaths.get(i).getCandidates().get(indexTable[i]));
//		}
//
//		//Convert map into HashSet<VLAN>
//		for(Application app : map.keySet()){
//			routing.add(new Routing(app, map.get(app)));
//		}
//		return routing;
//	}
	
	public boolean hasNext(){
		return hasNext;
	}
	
	public long getCurrCombination(){
		return currCombination;
	}
	
	public long getNoOfCombinations(){
		return noOfCombinations;
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append("[");
		for(int i = 0; i < indexTable.length; i ++){
			b.append(indexTable[i]);
			b.append(" / ");
			b.append(sizes[i]);
		}
		b.append("]");
		return b.toString();
	}
}
