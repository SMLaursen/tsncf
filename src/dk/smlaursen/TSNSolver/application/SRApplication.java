package dk.smlaursen.TSNSolver.application;

import java.util.Arrays;

import dk.smlaursen.TSNSolver.architecture.EndSystem;

public class SRApplication extends Application {
	private SRType aSRType;
	private int aNoOfFramesPerInterval;
	
	public SRApplication (String name, SRType type, int payloadSize, int noOfFrames, EndSystem src, EndSystem ...dest){
		super(payloadSize, name, src, dest);
		this.aSRType = type;
		this.aNoOfFramesPerInterval = noOfFrames;
	}
	
	public SRType getType(){
		return aSRType;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("SR ").append(aTitle);
		sb.append(" : ").append(aSRType);
		sb.append(" (").append(aNoOfFramesPerInterval).append(" x ").append(aMaxFrameSize).append("B),");
		sb.append(" (").append(aSource).append(" -> ").append(Arrays.toString(aDestinations)).append(")");
		return sb.toString();
	}
}
