package dk.smlaursen.TSNCF.application;

public enum SRType {
	CLASS_A(125,  2000), 
	CLASS_B(250, 50000);

	private int aInterval;
	private int aMaxEndToEndDelay;
	
	private SRType(int interval, int maxDelay){
		this.aInterval = interval;
		this.aMaxEndToEndDelay = maxDelay;
	}
	
	public int getIntervalMicroSec(){
		return aInterval;
	}
	
	public int getMaxEndToEndDelayMicroSec(){
		return aMaxEndToEndDelay;
	}
}
