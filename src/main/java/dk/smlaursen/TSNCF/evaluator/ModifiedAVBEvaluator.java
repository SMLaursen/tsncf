package dk.smlaursen.TSNCF.evaluator;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.application.SRApplication;
import dk.smlaursen.TSNCF.application.TTApplication;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.solver.VLAN;

/** This {@link Evaluator} is used for calculating the cost of the given {@link VLAN} assignment taking SR- and TT-timings into account.
 *  The cost is calculated using the following penalties :
 *  <li> {@value #HOP_PENALITY} for each edge. This favors shorter paths and penalizes disjoint multi-cast routes.
 *  <li> {@value #PENALITY_THRESHOLD} The threshold of when to start increasing cost due to high-utilization.   
 *  <li> {@value #THRESHOLD_EXCEEDED_PENALITY} The penality applied to every percent of WCRT / DEADLINE exceeds {@link #PENALITY_THRESHOLD}  */
public class ModifiedAVBEvaluator implements Evaluator{
	//----------- PENALTIES ----------------------
	/** The threshold of WCRT / DEADLINE for when starting to increase with {@value #THRESHOLD_EXCEEDED_PENALITY} per. percent*/
	private final static double PENALITY_THRESHOLD = 0.8;
	/** The penality applied to every percent of WCRT / DEADLINE exceeds {@link #PENALITY_THRESHOLD}*/
	private final static double THRESHOLD_EXCEEDED_PENALITY = 0.2;
	/** How much each hop increases the cost*/
	private final static double HOP_PENALITY = 1.0;
	//----------- CONFIGURATION ------------------
	/** The maximum BE frame-size*/
	private final static int MAX_BE_FRAME_BYTES = 1522;

	private static Logger logger = LoggerFactory.getLogger(ModifiedAVBEvaluator.class.getSimpleName());

	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, GCLEdge> graph) {
		Map<GCLEdge, Double> allocMap = new HashMap<GCLEdge, Double>(); 
		double cost = 0;

		// First we identify all the different modes 
		Map<String, Set<VLAN>> modeMap = new HashMap<String, Set<VLAN>>();
		Set<VLAN> ttSet = new HashSet<VLAN>();
		for(VLAN vl : vlans){
			if(vl.getApplication() instanceof SRApplication){
				SRApplication app = (SRApplication) vl.getApplication();
				for(String mode : app.getModes()){
					if(!modeMap.containsKey(mode)){
						modeMap.put(mode, new HashSet<VLAN>());
					}
					modeMap.get(mode).add(vl);
				}
			} else if(vl.getApplication() instanceof TTApplication){
				ttSet.add(vl);
			} else {
				throw new IllegalArgumentException("Unsupported application class '"+vl.getApplication().getClass().getSimpleName()+"' of "+vl.getApplication().getTitle());
			}
		}
		//Then we run through each mode
		for(String mode : modeMap.keySet()){
			Set<VLAN> vls = new HashSet<VLAN>();
			vls.addAll(modeMap.get(mode));
			vls.addAll(ttSet);

			//First we calculate the accumulated bwthRequirement and the cost due to disjointEdges
			for(VLAN vl : vls){
				Set<GCLEdge> edges = new HashSet<GCLEdge>();
				Application app = vl.getApplication();
				//Retrieve all unique edges for that app
				for(GraphPath<Node, GCLEdge> gp : vl.getRoutings()){
					//Hashset so only unique edges will be stored for this route
					for(GCLEdge edge : gp.getEdgeList()){
						edges.add(edge);
					}
				}
				//Run over all the unique edges
				for(GCLEdge edge : edges){
					//If not already there, put it there
					if(!allocMap.containsKey(edge)){
						allocMap.put(edge, 0.0);
					}
					double allocMbps = (app.getMaxFrameSize() * 8) * app.getNoOfFramesPerInterval() / app.getInterval();
					double totalAllocMbps = allocMap.get(edge) + allocMbps;
					
					//Abort if edge-capacity exceeded (remember not 100% of the edge is allowed to be reserved)
					if(totalAllocMbps > edge.getRateMbps() * edge.getAllocationCapacity()){
						if(logger.isDebugEnabled()){
							logger.debug("VLANS invalid : edge "+edge+"'s capacity exceeded. VLANS : "+vlans);
						}
						return cost = Double.MAX_VALUE;
					}
					allocMap.put(edge, totalAllocMbps);
					//Cost also includes the the sum of all disjoint edges
					cost += edges.size() * HOP_PENALITY;
				}
			}

			//Now calculate timings 
			for(VLAN vl : vls){
				Application app = vl.getApplication();
				//Below calculation is only valid for AVB-traffic
				if(app instanceof TTApplication){
					continue;
				}
				double maxLatency = 0;
				for(GraphPath<Node, GCLEdge> gp : vl.getRoutings()){
					double latency = 0;
					for(GCLEdge edge : gp.getEdgeList()){
						double capacity = edge.getAllocationCapacity() - (edge.calculateWorstCaseInterference(app.getInterval()) /app.getInterval()-1);
						latency += calculateMaxLatency(edge, allocMap.get(edge), app, capacity);
					}
					//For multicast routing, were only interested in the worst route
					if(maxLatency < latency){
						maxLatency = latency;
					}
				}
				if(maxLatency > app.getDeadline()){
					if(logger.isDebugEnabled()){
						logger.debug(vlans+" set non-schedulable : "+app.getTitle()+"'s maxLatency exceeds deadline");
					}
					cost = Double.MAX_VALUE;
					break;
				} else if (maxLatency / app.getDeadline() > PENALITY_THRESHOLD){
					cost += (maxLatency/app.getDeadline() - PENALITY_THRESHOLD) * 100 * THRESHOLD_EXCEEDED_PENALITY;
				}
			}
			//Clear allocationMap before evaluating next mode
			allocMap.clear();
		}
		return cost;
	}
//	//TODO Add UnitTests
//	/** This method has been based on the formulas in 802.1BA Draft 2.5 
//	 * http://www.ieee802.org/1/files/private/ba-drafts/d2/802-1ba-d2-5.pdf*/
	private double calculateMaxLatency(GCLEdge edge, double totalAlloc_mbps, Application app, double capacity){
		//Time to transmit max interfering BE packet.  
		double tMaxPacket = (MAX_BE_FRAME_BYTES + 8)*8 / edge.getRateMbps();
		//Time to transmit frame_size
		double tStreamPacket = (app.getMaxFrameSize() + 8)*8 / edge.getRateMbps();
		//Inter-Frame-Gap
		double tIFG = 12*8 / edge.getRateMbps();
		//Sum of transmission times of all Class A stream frames in a 125us interval
		double tAllStreams = totalAlloc_mbps * app.getInterval() / edge.getRateMbps();
		double maxLatency = edge.getLatency() + edge.calculateWorstCaseInterference((tMaxPacket+tIFG +
				tAllStreams - (tStreamPacket+tIFG)) * (edge.getRateMbps()/capacity)/100 + 
				tStreamPacket); 
		return maxLatency; 
	}
}

