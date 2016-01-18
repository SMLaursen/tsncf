package dk.smlaursen.TSNFramework.evaluator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import dk.smlaursen.TSNFramework.application.Application;
import dk.smlaursen.TSNFramework.application.SRType;
import dk.smlaursen.TSNFramework.architecture.GCLEdge;
import dk.smlaursen.TSNFramework.architecture.Node;
import dk.smlaursen.TSNFramework.solver.VLAN;

public class ModifiedLegacyAVBEvaluator implements Evaluator{
	private final static double RATE_MBPS = 100;
	
	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, GCLEdge> graph) {
		System.out.println(calculateMaxLatency(16, 230));
		
		Map<GCLEdge, Double> allocMap = new HashMap<GCLEdge, Double>(); 
		
		double cost = 0;
		for(VLAN vl : vlans){
			Set<GCLEdge> edges = new HashSet<GCLEdge>();
			Application app = vl.getApplication();
			for(GraphPath<Node, GCLEdge> gp : vl.getRoutings()){
				//Hashset so only unique edges will be stored for this route
				for(GCLEdge edge : gp.getGraph().edgeSet()){
					edges.add(edge);
					//If not already there, put it there
					if(!allocMap.containsKey(edge)){
						allocMap.put(edge, 0.0);
					}
					double allocMbps = (app.getMaxFrameSize() * 8) * app.getNoOfFramesPerInterval() / app.getInterval();
					double totalAllocMbps = allocMap.get(edge) + allocMbps;
					//Abort if edge exceeded
					if(totalAllocMbps > edge.getCapacityMbps()){
						return Double.MAX_VALUE;
					}
					allocMap.put(edge, totalAllocMbps);
				}
			}
			//Cost also includes the the sum of all disjoint edges
			cost += edges.size();
		}
		return cost;
	}
	
	private void setup(Set<Application> apps){
		//How much is allocated in total on a 125us interval
		int allFrameAllocBytes = 0;
		
		for(Application app : apps){
			allFrameAllocBytes += app.getMaxFrameSize();
		}
	}
	
	//TODO Add UnitTests
	/** This method has been based on the formulas in 802.1BA Draft 2.5 
	 * http://www.ieee802.org/1/files/private/ba-drafts/d2/802-1ba-d2-5.pdf
	 * Note the WCRT has been shown to be optimistic and are therefore not guaranteed*/
	private double calculateMaxLatency(double alloc_mbps, int frame_size_bytes){
		double tDevice = 5.12;
		double totalAllocMbps = alloc_mbps;
		//Time to transmit max interfering packet. Here it's assumed to be 1522 
		double tMaxPacket = (1522 + 8)*8 / RATE_MBPS;
		//Time to transmit 
		double tStreamPacket = (frame_size_bytes + 8)*8 / RATE_MBPS;
		//Inter-Frame-Gap
		double tIFG = 12*8 / RATE_MBPS;
		//Sum of transmission times of all Class A stream frames in a 125ms interval (Here just assumed to be max)
		//TODO this is only for one stream. Set it to maxAlloc or calculate real value
		double tAllStreams = totalAllocMbps * SRType.CLASS_A.getIntervalMicroSec() / RATE_MBPS;
		
		//How much can the TT traffic interfere = given the TT schedule of the port, how much delay can this add?
		double tTTInterference = 0;
		
		double maxLatency = tDevice + tMaxPacket+tIFG +
				(tAllStreams - (tStreamPacket+tIFG)) * (RATE_MBPS/alloc_mbps) + 
				tStreamPacket + 
				tTTInterference;
		
		return maxLatency;
	}
}

