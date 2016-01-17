package dk.smlaursen.TSNSolver.architecture;

import org.jgrapht.graph.DefaultEdge;

public class GCLEdge extends DefaultEdge {
	private static final long serialVersionUID = 3927841355223720495L;
	
	private int[] aGCL = null;
	public void setGCL(int[] GCL){
		aGCL = GCL;
	}
	
	public int[] getGCL(){
		return aGCL;
	}
}
