package dk.smlaursen.TSNCF.application;

import java.util.List;

import dk.smlaursen.TSNCF.architecture.Bridge;
import dk.smlaursen.TSNCF.architecture.GCL;

public class ExplicitPath {
	private List<GCL> aGCL;
	private List<List<Bridge>> aPath;
	
	public ExplicitPath(List<GCL> gcl, List<List<Bridge>> path){
		aGCL = gcl;
		aPath = path;
	}
	
	public List<GCL> getGCL(){
		return aGCL;
	}
	
	public List<List<Bridge>> getPath(){
		return aPath;
	}
	
	public String toString(){
		return aPath.toString();
	}
}
