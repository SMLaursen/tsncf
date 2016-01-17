package dk.smlaursen.TSNSolver.evaluator;

import java.util.Set;

import org.jgrapht.Graph;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.application.SRApplication;
import dk.smlaursen.TSNSolver.application.TTApplication;
import dk.smlaursen.TSNSolver.architecture.GCLEdge;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

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
