package dk.smlaursen.TSNCF.evaluator;


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
}

