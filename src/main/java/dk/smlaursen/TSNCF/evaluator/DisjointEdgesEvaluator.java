package dk.smlaursen.TSNCF.evaluator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.solver.Unicast;

/** DisjointEdgesEvaluator is a simple evaluator that just counts the number of disjoint edges used.
 *  The fewer the better. */
public class DisjointEdgesEvaluator implements Evaluator {

	@Override
	public double evaluate(final Collection<Unicast> routes,final Graph<Node, GCLEdge> graph) {
		double cost = 0;
		HashMap<Application, HashSet<DefaultEdge>> map = new HashMap<Application, HashSet<DefaultEdge>>(); 
		//First we put all unqiue edges in the map
		for(Unicast r : routes){
			if(!map.containsKey(r.getApplication())){
				map.put(r.getApplication(), new HashSet<DefaultEdge>());
			}
			map.get(r.getApplication()).addAll(r.getRoute().getEdgeList());
		}
		
		//Then we run through it, and report the number
		for(HashSet<DefaultEdge> m : map.values()){
			cost +=m.size();
		}
		
		return cost;
	}
}
