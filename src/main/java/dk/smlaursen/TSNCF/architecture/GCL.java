package dk.smlaursen.TSNCF.architecture;

public class GCL {
	private int afrequency;
	private double aOffset, aDuration;
	
	public GCL(double offset, double duration, int frequency){
		aOffset = offset;
		aDuration = duration;
		afrequency = frequency;
	}
	public double getOffset(){
		return aOffset;
	}
	
	public double getDuration(){
		return aDuration;
	}
	
	public double getFrequency(){
		return afrequency;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(aOffset).append(",").append(aDuration).append(",").append(afrequency).append(">");
		return sb.toString();
	}
}
