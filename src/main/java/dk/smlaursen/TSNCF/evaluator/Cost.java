package dk.smlaursen.TSNCF.evaluator;

public interface Cost {

	public void reset();
	
	public double getTotalCost();
	
	public String toDetailedString();
	
}
