package dk.smlaursen.TSNSolver.evaluator;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.application.SRApplication;
import dk.smlaursen.TSNSolver.application.SRType;
import dk.smlaursen.TSNSolver.application.TTApplication;
import dk.smlaursen.TSNSolver.architecture.GCLEdge;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

public class ModifiedLegacyAVBEvaluator implements Evaluator{
	private final static double RATE_MBPS = 100;
	
	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, GCLEdge> graph) {
		
		double cost = 0;
		for(VLAN vl : vlans){
			Set<GCLEdge> edges = new HashSet<GCLEdge>();
			for(GraphPath<Node, GCLEdge> gp : vl.getRoutings()){
				//Hashset so only unique edges will be stored for this route
				edges.addAll(gp.getGraph().edgeSet());
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
	private double calculateMaxLatency(double alloc_mbps, int frame_size_bytes, int allFrameAllocBytes){
		double tDevice = 5.12;
		//Time to transmit max interfering packet. Here it's assumed to be 1522 
		double tMaxPacket = (1522 + 8)*8 / RATE_MBPS;
		//Time to transmit 
		double tStreamPacket = (frame_size_bytes + 8)*8 / RATE_MBPS;
		//Inter-Frame-Gap
		double tIFG = 12*8 / RATE_MBPS;
		//Sum of transmission times of all Class A stream frames in a 125ms interval (Here just assumed to be max)
		//TODO this is only for one stream. Set it to maxAlloc or calculate real value
		double tAllStreams = frame_size_bytes * SRType.CLASS_A.getIntervalMicroSec() / RATE_MBPS;
		
		//How much can the TT traffic interfere = given the TT schedule of the port, how much delay can this add?
		double tTTInterference = 0;
		
		double maxLatency = tDevice + tMaxPacket+tIFG +
				(tAllStreams - (tStreamPacket+tIFG)) * (RATE_MBPS/alloc_mbps) + 
				tStreamPacket + 
				tTTInterference;
		return maxLatency;
	}
}

