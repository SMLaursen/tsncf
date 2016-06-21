package dk.smlaursen.TSNCF.solver;

import java.util.List;

import dk.smlaursen.TSNCF.evaluator.Cost;

/** Return type wrapper */
public class Solution {
	private Cost aCost;
	private List<Multicast> aRouting;

	public Solution(Cost c, List<Multicast> m){
		aCost = c;
		aRouting = m;
	}
	
	public List<Multicast> getRouting(){
		return aRouting;
	}
	
	public Cost getCost(){
		return aCost;
	}
}
