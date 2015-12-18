package dk.smlaursen.TSNSolver.application;

import dk.smlaursen.TSNSolver.architecture.EndSystem;

/** Model of a application */
public abstract class Application {
	//Many layers, see http://www.satnac.org.za/proceedings/2012/papers/5.Coverged_Services/72.pdf
	//Investigate whether this is the same for all traffic classes 
	private static final int MESSAGE_OVERHEAD_BYTES = 74;
	
	protected int aMaxFrameSize;
	protected String aTitle;
	
	protected EndSystem aSource;
	protected EndSystem[] aDestinations;
	
	public Application(int maxPayloadSize, String title, EndSystem src, EndSystem ... dest) {
		this.aSource = src;
		this.aDestinations = dest;
		this.aMaxFrameSize = maxPayloadSize + MESSAGE_OVERHEAD_BYTES;
		this.aTitle = title;
	}
	
	public EndSystem getSource(){
		return aSource;
	}
	
	public EndSystem[] getDestinations(){
		return aDestinations;
	}
	
	public int getMaxFrameSize(){
		return aMaxFrameSize;
	}
	
	public String getTitle(){
		return aTitle;
	}
}
