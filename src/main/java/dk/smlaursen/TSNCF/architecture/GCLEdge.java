package dk.smlaursen.TSNCF.architecture;

import org.jgrapht.graph.DefaultEdge;

public class GCLEdge extends DefaultEdge {
	private static final long serialVersionUID = 3927841355223720495L;
	private int aCapacity;
	
	public GCLEdge(int capacityMbps){
		aCapacity = capacityMbps;
	}
	
	public int getCapacityMbps(){
		return aCapacity;
	}
}
