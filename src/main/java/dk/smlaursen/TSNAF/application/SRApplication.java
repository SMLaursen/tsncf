package dk.smlaursen.TSNAF.application;

import java.util.Arrays;
import java.util.List;

import dk.smlaursen.TSNAF.architecture.EndSystem;

public class SRApplication extends Application {
	private SRType aSRType;
	private List<String> aModes;
	
	public SRApplication (String name, List<String> modes, SRType type, int payloadSize, int noOfFrames, EndSystem src, EndSystem ...dest){
		super(name, payloadSize, noOfFrames, src, dest);
		aSRType = type;
		aModes = modes;
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
