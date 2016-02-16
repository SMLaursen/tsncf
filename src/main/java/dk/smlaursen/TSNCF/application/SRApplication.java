package dk.smlaursen.TSNCF.application;

import java.util.Arrays;
import java.util.List;

import dk.smlaursen.TSNCF.architecture.EndSystem;

public class SRApplication extends Application {
	private SRType aSRType;
	private List<String> aModes;
	private int aNoOfFramesPerInterval,aMaxFrameSize;
	
	public SRApplication (String name, List<String> modes, SRType type, int payloadSize, int noOfFrames, EndSystem src, EndSystem ...dest){
		super(name, src, dest);
		aSRType = type;
		aModes = modes;
		aNoOfFramesPerInterval = noOfFrames;
		aMaxFrameSize = payloadSize;
	}
	
	public int getNoOfFramesPerInterval(){
		return aNoOfFramesPerInterval;
	}
	
	public int getMaxFrameSize(){
		return aMaxFrameSize;
	}
	public SRType getType(){
		return aSRType;
	}
	
	public List<String> getModes(){
		return aModes;
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
		sb.append("SR ").append(aTitle).append(" ").append(aModes);
		sb.append(" : ").append(aSRType);
		sb.append(" (").append(aNoOfFramesPerInterval).append("x").append(aMaxFrameSize).append("B / ").append(getInterval()).append("us)");
		sb.append(" (").append(aSource).append(" -> ").append(Arrays.toString(aDestinations)).append(")");
		return sb.toString();
	}
}
