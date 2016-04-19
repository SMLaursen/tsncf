package dk.smlaursen.TSNCF.application;

import dk.smlaursen.TSNCF.architecture.EndSystem;

/** Model of a application */
public abstract class Application {
	protected String aTitle;
	
	protected EndSystem aSource;
	protected EndSystem[] aDestinations;
	
	/** Returns the period for this class of traffic in microseconds / us*/
	public abstract int getInterval();
	public abstract int getDeadline();
	
	public Application(String title, EndSystem src, EndSystem ... dest) {
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
	
	public String getTitle(){
		return aTitle;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Application){
			Application a = (Application) obj;
			if(aTitle.equals(a.aTitle)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return aTitle.hashCode();
	}
}
