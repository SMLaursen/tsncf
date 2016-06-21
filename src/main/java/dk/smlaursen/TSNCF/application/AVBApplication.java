package dk.smlaursen.TSNCF.application;

import java.util.Arrays;
import java.util.List;

import dk.smlaursen.TSNCF.architecture.EndSystem;

public class AVBApplication extends Application {
	private AVBClass aAVBClass;
	private List<String> aModes;
	private int aNoOfFramesPerInterval,aMaxFrameSize;
	
	public AVBApplication (String name, List<String> modes, AVBClass type, int payloadSize, int noOfFrames, EndSystem src, EndSystem ...dest){
		super(name, src, dest);
		aAVBClass = type;
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
	public AVBClass getType(){
		return aAVBClass;
	}
	
	public List<String> getModes(){
		return aModes;
	}
	
	@Override
	public int getInterval() {
		return aAVBClass.getIntervalMicroSec();
	}
	
	@Override
	public int getDeadline(){
		return aAVBClass.getMaxEndToEndDelayMicroSec();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("AVB ").append(aTitle).append(" ").append(aModes);
		sb.append(" : ").append(aAVBClass);
		sb.append(" (").append(aNoOfFramesPerInterval).append("x").append(aMaxFrameSize).append("B / ").append(getInterval()).append("us)");
		sb.append(" (").append(aSource).append(" -> ").append(Arrays.toString(aDestinations)).append(")");
		return sb.toString();
	}
}
