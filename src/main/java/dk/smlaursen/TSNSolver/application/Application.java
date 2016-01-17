package dk.smlaursen.TSNSolver.application;

import dk.smlaursen.TSNSolver.architecture.EndSystem;

/** Model of a application */
public abstract class Application {
	protected String aTitle;
	
	protected int aNoOfFramesPerInterval,aMaxFrameSize;
	
	protected EndSystem aSource;
	protected EndSystem[] aDestinations;
	
	public Application(String title,int aNoOfFramesPerInterval, int aMaxFrameSize, EndSystem src, EndSystem ... dest) {
		this.aSource = src;
		this.aDestinations = dest;
		this.aTitle = title;
	}
	
	public EndSystem getSource(){
		return aSource;
	}
	
	public EndSystem[] getDestinations(){
		return aDestinations;
	}
	
	public int getNoOfFramesPerInterval(){
		return aNoOfFramesPerInterval;
	}
	
	public int getMaxFrameSize(){
		return aMaxFrameSize;
	}
	
	public String getTitle(){
		return aTitle;
	}
	
	@Override
	public int hashCode(){
		return aTitle.hashCode();
	}
}
