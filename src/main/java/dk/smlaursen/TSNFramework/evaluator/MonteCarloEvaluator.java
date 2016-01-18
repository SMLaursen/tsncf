package dk.smlaursen.TSNFramework.evaluator;

import java.util.Set;

import org.jgrapht.Graph;

import dk.smlaursen.TSNFramework.application.Application;
import dk.smlaursen.TSNFramework.application.SRApplication;
import dk.smlaursen.TSNFramework.application.TTApplication;
import dk.smlaursen.TSNFramework.architecture.GCLEdge;
import dk.smlaursen.TSNFramework.architecture.Node;
import dk.smlaursen.TSNFramework.solver.VLAN;

public class MonteCarloEvaluator implements Evaluator {

	@Override
	public double evaluate(Set<VLAN> vlans, Graph<Node, GCLEdge> graph) {
		double cost = 0;
		for(VLAN vl : vlans){
			Application app = vl.getApplication();
			if(app instanceof SRApplication){
				SRApplication srApp = (SRApplication) app;

			} else if(app instanceof TTApplication){
				TTApplication ttApp = (TTApplication) app;
			} else {

			}
		}
		for(int i = 0; i < 10; i++){
			
		}
	
		return cost;
	}
}
