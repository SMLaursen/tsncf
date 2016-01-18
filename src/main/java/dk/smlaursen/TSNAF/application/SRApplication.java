package dk.smlaursen.TSNAF.application;

import java.util.Arrays;

import dk.smlaursen.TSNAF.architecture.EndSystem;

public class SRApplication extends Application {
	private SRType aSRType;
	
	public SRApplication (String name, SRType type, int payloadSize, int noOfFrames, EndSystem src, EndSystem ...dest){
		super(name, payloadSize, noOfFrames, src, dest);
		this.aSRType = type;
		this.aNoOfFramesPerInterval = noOfFrames;
		this.aMaxFrameSize = payloadSize;
	}
	
	public SRType getType(){
		return aSRType;
	}
	
	@Override
	public int getInterval() {
		return aSRType.getIntervalMicroSec();
	}
	
	@Override
	public int getDeadline(){
		return aSRType.getMaxEndToEndDelayMicroSec();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("SR ").append(aTitle);
		sb.append(" : ").append(aSRType);
		sb.append(" (").append(aNoOfFramesPerInterval).append("x").append(aMaxFrameSize).append("B / ").append(getInterval()).append("us)");
		sb.append(" (").append(aSource).append(" -> ").append(Arrays.toString(aDestinations)).append(")");
		return sb.toString();
	}
}
