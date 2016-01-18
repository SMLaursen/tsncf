package dk.smlaursen.TSNAF.solver.KShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;

import dk.smlaursen.TSNAF.application.Application;
import dk.smlaursen.TSNAF.architecture.GCLEdge;
import dk.smlaursen.TSNAF.architecture.Node;
import dk.smlaursen.TSNAF.solver.VLAN;

public class CombinatoricTable {
	//FIXME ensure no overflow
	private long noOfCombinations = 1;
	private long currCombination = 1;
	
	private boolean hasNext;
	private byte[] indexTable;
	private byte[] sizes;
	private ArrayList<VLAN> graphPaths;
	
	public CombinatoricTable(ArrayList<VLAN> paths){
		hasNext = true;
		graphPaths = paths;
		
		indexTable = new byte[graphPaths.size()];
		sizes = new byte[graphPaths.size()];
		
		//Store all the sizes
		for(int i = 0; i < sizes.length; i++){
			sizes[i] = (byte) graphPaths.get(i).getRoutings().size();
			noOfCombinations *= sizes[i];
		}
	}
	
	public void next(){
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
	
	public Set<VLAN> getSet(){
		Map<Application, ArrayList<GraphPath<Node, GCLEdge>>> map = new HashMap<Application, ArrayList<GraphPath<Node, GCLEdge>>>();
		Set<VLAN> routing = new HashSet<VLAN>();
		
		//TODO performance can be improved by an incremental "find and replace" instead of this redo everything naive approach
		for(int i = 0; i < graphPaths.size(); i++){
			Application app = graphPaths.get(i).getApplication();
			//This merges multiple destinations into one vlan
			if(!map.containsKey(app)){
				map.put(app, new ArrayList<GraphPath<Node, GCLEdge>>());
			}
			map.get(app).add(graphPaths.get(i).getRoutings().get(indexTable[i]));
		}

		//Convert map into HashSet<VLAN>
		for(Application app : map.keySet()){
			routing.add(new VLAN(app, map.get(app)));
		}
		return routing;
	}
	
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
