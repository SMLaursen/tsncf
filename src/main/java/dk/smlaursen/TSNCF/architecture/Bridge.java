package dk.smlaursen.TSNCF.architecture;

public class Bridge extends Node {
	private int aRateMBPS;
	private double aMacDelay_us;
	
	public Bridge(String id){
		aId = id;
		aRateMBPS = 100;
		aMacDelay_us = 5.12;
	}
	
	public Bridge(String id, int rateMBPS, double macDelay_us){
		aId = id;
		aRateMBPS = rateMBPS;
		aMacDelay_us = macDelay_us;
	}
	
	public int getRateMBPS(){
		return aRateMBPS;
	}
	
	public double getMacDelay(){
		return aMacDelay_us;
	}
}

