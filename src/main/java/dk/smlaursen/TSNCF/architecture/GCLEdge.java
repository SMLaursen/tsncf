package dk.smlaursen.TSNCF.architecture;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCLEdge extends DefaultEdge {
	private static final long serialVersionUID = 3927841355223720495L;
	private static final Logger logger = LoggerFactory.getLogger(GCLEdge.class.getSimpleName());
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
	
	/** Calculates the worst-case duration it will require  */
	public double calculateWorstCaseInterference(double duration){
		double interference = duration;
		//Use cached value if available
		if(cachedInterferenceValues.containsKey(duration)){
			interference = cachedInterferenceValues.get(duration); 
		} else {
			if(aGCLList.size() > 1){
				logger.warn("Currently only one GCL pr. edge is supported. Ignoring all but the first.");
			}
			//FIXME : correct this calculation for #gcl > 1
			for(GCL gcl : aGCLList){
				interference = duration + (
						Math.ceil(duration / ((500.0 / gcl.getFrequency())-gcl.getDuration()))
						) * gcl.getDuration();
				cachedInterferenceValues.put(duration, interference);
			}
		}
		return interference;
	}
}
