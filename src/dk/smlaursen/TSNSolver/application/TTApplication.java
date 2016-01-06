package dk.smlaursen.TSNSolver.application;

import java.util.Arrays;

import dk.smlaursen.TSNSolver.architecture.EndSystem;

public class TTApplication extends Application{
	private Long aPeriod;
	private Long aDeadline;
	
	public TTApplication(String name, int payloadSize, long period, long deadline, EndSystem src, EndSystem ...dest) {
		super(payloadSize, name, src, dest);
		this.aPeriod = period;
		this.aDeadline = deadline;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("TT ").append(aTitle);
		sb.append(" S : ").append(aMaxFrameSize).append("B");
		sb.append(" P : ").append(aPeriod).append("ms");
		sb.append(" D : ").append(aDeadline).append("ms");
		sb.append(" (").append(aSource).append(" -> ").append(Arrays.toString(aDestinations)).append(")");
		return sb.toString();
	}
}
