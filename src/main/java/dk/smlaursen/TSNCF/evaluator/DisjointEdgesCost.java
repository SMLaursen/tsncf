package dk.smlaursen.TSNCF.evaluator;

import dk.smlaursen.TSNCF.solver.Multicast;

public class DisjointEdgesCost implements Cost {
	private double cost;
	
	public DisjointEdgesCost() {
		reset();
	}
	
	public void addToCost(double val){
		cost+=val;
	}
	
	@Override
	public double getTotalCost() {
		return cost;
	}

	@Override
	public void reset() {
		cost = 0;
	}

	@Override
	public String toDetailedString() {
		return toString();
	}

	@Override
	public double getWCD(Multicast r) {
		// TODO Not implemented
		return 0;
	}
}

