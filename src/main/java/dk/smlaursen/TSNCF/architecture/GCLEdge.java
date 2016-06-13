package dk.smlaursen.TSNCF.architecture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;

public class GCLEdge extends DefaultEdge {
	private static final long serialVersionUID = 3927841355223720495L;
	private static final double MAX_ALLOC = 0.75;
	
	//As the calculated interferences are application specific and will remain constant 
	//we cache them to avoid re-calculating the same value
	private Map<Double, Double> cachedInterferenceValues = new HashMap<Double,Double>();
	
	private List<GCL> aGCLList = new LinkedList<GCL>();
	private int aRate;
	private double aLatency;
	
	/**Constructor
	 * @param rateMbps the transmission speed of the link*/
	public GCLEdge(int rateMbps, double latency){
		aRate = rateMbps;
		aLatency = latency;
	}
	
	public void addGCL(List<GCL> gcl){
		aGCLList.addAll(gcl);
	}

	public int getRateMbps(){
		return aRate;
	}
	
	public double getAllocationCapacity(){
		return MAX_ALLOC;
	}
	
	public double getLatency(){
		return aLatency;
	}
	
	public double calculateReservedForTTTraffic(double duration){
		return (calculateWorstCaseInterference(duration)/duration - 1)*aRate;
	}
	
	/** Calculates the worst-case duration it will require  */
	public double calculateWorstCaseInterference(double duration){
		double interference = duration;
		//Use cached value if available
		if(cachedInterferenceValues.containsKey(duration)){
			interference = cachedInterferenceValues.get(duration); 
		} else {
			if(aGCLList.size() > 0){
				//Slow approach (See algorithm 1 in paper)
				List<GCE> l = convertGCLToGCEs(aGCLList);
				double i_max = 0;
				for(int i = 0; i < l.size(); i++){
					double i_curr = 0, rem = duration;
					int index = i;
					while(rem > 0){
						i_curr += l.get(index).getDuration();
						rem    -= getSlack(l.get((index+1)%l.size()), l.get(index));
						index   = (index+1)%l.size();
					}
					if(i_curr > i_max){
						i_max = i_curr;
					}
				}
				interference = duration + i_max;
			} else {
				//0 or 1 GCL pr. edge can be calculated more efficiently
				for(GCL gcl : aGCLList){
					interference = duration + (
							Math.ceil(duration / ((500.0 / gcl.getFrequency())-gcl.getDuration()))
							) * gcl.getDuration();
				}
			}
			//Cache calculation, so it becomes faster to calculate next-time (TT-streams never changes routing)
			cachedInterferenceValues.put(duration, interference);
		}
		return interference;
	}
	
	//Returns the slack between end of ''next'' and beginning of ''curr''
	private double getSlack(GCE next, GCE curr){
		if(next.aStart < curr.aEnd){
			//Add 500 to compensate for modulus effect
			return (next.aStart+500.0 - curr.aEnd);
		} else {
			return next.aStart - curr.aEnd;
		}
	}
	
	private List<GCE> convertGCLToGCEs(List<GCL> gcls){
		List<GCE> gces = new ArrayList<GCE>();
		//Convert GCL to GCE and add to gces list
		for(GCL gcl : gcls){
			double period = 500.0 / gcl.getFrequency();
			for(int i = 0; i < gcl.getFrequency(); i ++){
				double start = gcl.getOffset()+i*period;
				gces.add(new GCE(start, start+gcl.getDuration()));
			}
		}
		//Sort GCEs on their start-times
		Collections.sort(gces, new Comparator<GCE>() {
			@Override
			public int compare(GCE o1, GCE o2) {
				Double g1 = new Double(o1.getStart());
				Double g2 = new Double(o1.getStart());
				return g1.compareTo(g2);
			}
		});
		//Return the sorted list
		return gces;
	}
	//// Helper class for
	private class GCE{
		private double aStart, aEnd;
		
		GCE(double start, double end){
			aStart = start;
			aEnd = end;
		}
		private double getStart(){
			return aStart;
		}
		private double getDuration(){
			return aEnd-aStart;
		}
		@Override
		public String toString(){
			return "["+aStart+"-"+aEnd+"]"; 
		}
		@Override
		public int hashCode(){
			return Double.hashCode(aStart)+Double.hashCode(aEnd);
		}
	}
}
