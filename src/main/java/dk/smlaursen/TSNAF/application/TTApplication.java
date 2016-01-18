package dk.smlaursen.TSNAF.application;

import java.util.Arrays;

import dk.smlaursen.TSNAF.architecture.EndSystem;

public class TTApplication extends Application{
	private int ttInterval = 500;
	
	/** Assumes that all TTApplications are periodic so payloadSize and NoOfFrames is enough */
	public TTApplication(String name, int payloadSize, int noOfFrames, EndSystem src, EndSystem ...dest) {
		super(name, payloadSize, noOfFrames, src, dest);
	}
	
	@Override
	public int getInterval() {
		return ttInterval;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("TT ").append(aTitle);
		sb.append(" (").append(aNoOfFramesPerInterval).append(" x ").append(aMaxFrameSize).append("B / ").append(getInterval()).append("us)");
		sb.append(" (").append(aSource).append(" -> ").append(Arrays.toString(aDestinations)).append(")");
		return sb.toString();
	}

	@Override
	public int getDeadline() {
		return 0;
	}
}
