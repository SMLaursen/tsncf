package dk.smlaursen.TSNCF.evaluator;

import dk.smlaursen.TSNCF.solver.Multicast;

public interface Cost {

	public void reset();
	
	public double getTotalCost();
	
	public String toDetailedString();
	
	/** Returns the WCD in us (Microseconds)*/
	public double getWCD(Multicast r);
	
}
