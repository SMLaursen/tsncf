package dk.smlaursen.TSNAF.application;

import dk.smlaursen.TSNAF.architecture.EndSystem;

/** Model of a application */
public abstract class Application {
	protected String aTitle;
	
	protected int aNoOfFramesPerInterval,aMaxFrameSize;
	
	protected EndSystem aSource;
	protected EndSystem[] aDestinations;
	
	/** Returns the period for this class of traffic in microseconds / us*/
	public abstract int getInterval();
	public abstract int getDeadline();
	
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
