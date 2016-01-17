package dk.smlaursen.TSNSolver.application;

import java.util.Arrays;

import dk.smlaursen.TSNSolver.architecture.EndSystem;

public class TTApplication extends Application{
	private int ttInterval = 500;
	
	/** Assumes that all TTApplications are periodic so payloadSize and NoOfFrames is enough */
	public TTApplication(String name, int payloadSize, int noOfFrames, EndSystem src, EndSystem ...dest) {
		super(name, payloadSize, noOfFrames, src, dest);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("TT ").append(aTitle);
		sb.append(" (").append(aNoOfFramesPerInterval).append(" x ").append(aMaxFrameSize).append("B / ").append(ttInterval).append("us)");
		sb.append(" (").append(aSource).append(" -> ").append(Arrays.toString(aDestinations)).append(")");
		return sb.toString();
	}
}
