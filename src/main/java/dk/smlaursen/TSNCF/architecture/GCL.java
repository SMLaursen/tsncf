package dk.smlaursen.TSNCF.architecture;

public class GCL {
	private double aOffset, aDuration, aPeriod;
	
	public GCL(double offset, double duration, double period){
		aOffset = offset;
		aDuration = duration;
		aPeriod = period;
	}
	public double getOffset(){
		return aOffset;
	}
	
	public double getDuration(){
		return aDuration;
	}
	
	public double getPeriod(){
		return aPeriod;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(aOffset).append(",").append(aDuration).append(",").append(aPeriod).append(">");
		return sb.toString();
	}
}
