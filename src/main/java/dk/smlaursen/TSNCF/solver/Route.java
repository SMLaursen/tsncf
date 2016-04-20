package dk.smlaursen.TSNCF.solver;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.Node;

/** Abstract class which both {@link Unicast} and {@link UnicastCandidates} extends, so that they easily can be mapped to each other
 *  using their hashCode*/
public abstract class Route {
	//The Application
	protected Application aApp;
	//The Destination Node 
	protected Node aDestNode;

	@Override
    public boolean equals(Object obj) {
		
		if(obj instanceof Route){
			Route r = (Route) obj;
			if(aApp.equals(r.aApp) && aDestNode.equals(r.aDestNode)){
				return true;
			}
		}
		return false;
    }
	
	@Override
	public int hashCode(){
		return aApp.hashCode() + aDestNode.hashCode();
	}
	
	@Override
	public String toString(){
		return aApp.toString() + "->" + aDestNode;
	}
	
	public Application getApplication(){
		return aApp;
	}
	
	public Node getDestNode(){
		return aDestNode;
	}

	

    
    
}
