package dk.smlaursen.TSNCF.architecture;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.graph.DefaultEdge;

public class GCLEdge extends DefaultEdge {
	private static final long serialVersionUID = 3927841355223720495L;
	
	private List<GCL> aGCLList = new LinkedList<GCL>();
	private int aCapacity;
	
	public GCLEdge(int capacityMbps){
		aCapacity = capacityMbps;
	}
	
	public void addGCL(List<GCL> gcl){
		aGCLList.addAll(gcl);
	}
	
	public int getCapacityMbps(){
		return aCapacity;
	}
	
	/** Calculates the worst-case duration it will require  */
	public double calculateWorstCaseInterference(double duration){
		double interference = duration;
		for(GCL gcl : aGCLList){
			System.out.println(gcl);
		}
		return interference;
	}
}
