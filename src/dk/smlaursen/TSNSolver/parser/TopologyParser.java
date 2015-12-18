package dk.smlaursen.TSNSolver.parser;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import dk.smlaursen.TSNSolver.architecture.Bridge;
import dk.smlaursen.TSNSolver.architecture.EndSystem;
import dk.smlaursen.TSNSolver.architecture.Node;

public class TopologyParser {
	
	public static SimpleGraph<Node, DefaultEdge> parse(){
		SimpleGraph<Node, DefaultEdge> graph = new SimpleGraph<Node, DefaultEdge>(DefaultEdge.class);
		
		EndSystem es1 = new EndSystem("ES1");
		graph.addVertex(es1);
		
		EndSystem es2 = new EndSystem("ES2");
		graph.addVertex(es2);
		
		EndSystem es3 = new EndSystem("ES3");
		graph.addVertex(es3);
		
		EndSystem es4 = new EndSystem("ES4");
		graph.addVertex(es4);
		
		Bridge b1 = new Bridge("B1");
		graph.addVertex(b1);
		
		Bridge b2 = new Bridge("B2");
		graph.addVertex(b2);
		
		Bridge b3 = new Bridge("B3");
		graph.addVertex(b3);
		
		Bridge b4 = new Bridge("B4");
		graph.addVertex(b4);
		
		graph.addEdge(es1, b1);
		graph.addEdge(es3, b3);
		graph.addEdge(b1, b4);
		graph.addEdge(b4, b2);
		graph.addEdge(b1, b3);
		graph.addEdge(b3, b2);
		graph.addEdge(b2, es4);
		graph.addEdge(b2, es2);
		
		return graph;
	}
}
