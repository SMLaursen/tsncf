package dk.smlaursen.TSNAF.evaluator;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import dk.smlaursen.TSNAF.application.Application;
import dk.smlaursen.TSNAF.application.SRType;
import dk.smlaursen.TSNAF.architecture.GCLEdge;
import dk.smlaursen.TSNAF.architecture.Node;
import dk.smlaursen.TSNAF.solver.VLAN;

/** This {@link Evaluator} is used for calculating the cost of the given {@link VLAN} assignment taking SR- and TT-timings into account.
 *  The cost is calculated using the following penalties :
 *  <li> {@value #HOP_PENALITY} for each edge. This favors shorter paths and penalizes disjoint multi-cast routes.
 *  <li> {@value #PENALITY_THRESHOLD} The threshold of when to start increasing cost due to high-utilization.   
 *  <li> {@value #THRESHOLD_EXCEEDED_PENALITY} The penality applied to every percent of WCRT / DEADLINE exceeds {@link #PENALITY_THRESHOLD}  */
public class ModifiedLegacyAVBEvaluator implements Evaluator{
	//----------- PENALTIES ----------------------
	/** The threshold of WCRT / DEADLINE for when starting to increase with {@value #THRESHOLD_EXCEEDED_PENALITY} per. percent*/
	private final static double PENALITY_THRESHOLD = 0.8;
	/** The penality applied to every percent of WCRT / DEADLINE exceeds {@link #PENALITY_THRESHOLD}*/
	private final static double THRESHOLD_EXCEEDED_PENALITY = 0.1;
	/** How much each hop increases the cost*/
	private final static double HOP_PENALITY = 1.0;
	//----------- CONFIGURATION ------------------
	/** The global rate of the network*/
	private final static double RATE_MBPS = 100;
	/** The ratio of allocatable bwth / BE traffic*/
	private final static double MAX_ALLOC = 0.75;
	/** The maximum BE frame-size*/
	private final static int MAX_BE_FRAME_BYTES = 1522;
	
	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, GCLEdge> graph) {
		Map<GCLEdge, Double> allocMap = new HashMap<GCLEdge, Double>(); 
		double cost = 0;
		
		//First we calculate the accumulated bwthRequirement and the cost due to disjointEdges
		for(VLAN vl : vlans){
			Set<GCLEdge> edges = new HashSet<GCLEdge>();
			Application app = vl.getApplication();
			for(GraphPath<Node, GCLEdge> gp : vl.getRoutings()){
				//Hashset so only unique edges will be stored for this route
				for(GCLEdge edge : gp.getEdgeList()){
					edges.add(edge);
					//If not already there, put it there
					if(!allocMap.containsKey(edge)){
						allocMap.put(edge, 0.0);
					}
					double allocMbps = (app.getMaxFrameSize() * 8) * app.getNoOfFramesPerInterval() / app.getInterval();
					double totalAllocMbps = allocMap.get(edge) + allocMbps;
					
					//Abort if edge-capacity exceeded (remember not 100% of the edge is allowed to be reserved)
					if(totalAllocMbps > edge.getCapacityMbps() * MAX_ALLOC){
						//TODO LOG
						return cost = Double.MAX_VALUE;
					}
					allocMap.put(edge, totalAllocMbps);
				}
			}
			//Cost also includes the the sum of all disjoint edges
			cost += edges.size() * HOP_PENALITY;
		}
		
		//Now calculate timings 
		for(VLAN vl : vlans){
			Application app = vl.getApplication();
			double maxLatency = 0;
			for(GraphPath<Node, GCLEdge> gp : vl.getRoutings()){
				double latency = 0;
				for(GCLEdge edge : gp.getEdgeList()){
					double alloc_mbps = (app.getMaxFrameSize() * 8) * app.getNoOfFramesPerInterval() / app.getInterval();
					latency += calculateMaxLatency(alloc_mbps, allocMap.get(edge)-alloc_mbps , app.getMaxFrameSize());
				}
				//For multicast routing, were only interested in the worst route
				if(maxLatency < latency){
					maxLatency = latency;
				}
			}
			if(maxLatency > app.getDeadline()){
				cost = Double.MAX_VALUE;
				break;
			} else if (maxLatency / app.getDeadline() > PENALITY_THRESHOLD){
				cost += (maxLatency/app.getDeadline() - PENALITY_THRESHOLD) * 100 * THRESHOLD_EXCEEDED_PENALITY;
			}
		}
		return cost;
	}
	
	//TODO Add UnitTests
	/** This method has been based on the formulas in 802.1BA Draft 2.5 
	 * http://www.ieee802.org/1/files/private/ba-drafts/d2/802-1ba-d2-5.pdf
	 * Note the WCRT has been shown to be optimistic and are therefore not guaranteed*/
	private double calculateMaxLatency(double alloc_mbps, double totalAlloc_mbps, int frame_size_bytes){
		double tDevice = 5.12;
		//Time to transmit max interfering packet.  
		double tMaxPacket = (MAX_BE_FRAME_BYTES + 8)*8 / RATE_MBPS;
		//Time to transmit 
		double tStreamPacket = (frame_size_bytes + 8)*8 / RATE_MBPS;
		//Inter-Frame-Gap
		double tIFG = 12*8 / RATE_MBPS;
		//Sum of transmission times of all Class A stream frames in a 125ms interval (Here just assumed to be max)
		//TODO this is only for one stream. Set it to maxAlloc or calculate real value
		double tAllStreams = totalAlloc_mbps * SRType.CLASS_A.getIntervalMicroSec() / RATE_MBPS;
		//How much can the TT traffic interfere = given the TT schedule of the port, how much delay can this add?
		double tTTInterference = 0;
		double maxLatency = tDevice + tMaxPacket+tIFG +
				(tAllStreams - (tStreamPacket+tIFG)) * (RATE_MBPS/alloc_mbps) + 
				tStreamPacket + 
				tTTInterference;
		return maxLatency;
	}
}

