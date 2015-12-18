package dk.smlaursen.TSNSolver.parser;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.application.SRApplication;
import dk.smlaursen.TSNSolver.application.SRType;
import dk.smlaursen.TSNSolver.architecture.EndSystem;
import dk.smlaursen.TSNSolver.architecture.Node;

public class ApplicationParser {
	
	/**Parses the */
	public static Set<Application> parse(Graph<Node, DefaultEdge> graph){
		Set<Application> applications = new HashSet<Application>();
		
		//For each application {:
		String name = "Test1";
		int payloadSize = 8;
		int noOfFrames = 10;
		SRType type = SRType.CLASS_A;
		EndSystem src = new EndSystem("ES1");
		EndSystem[] dests = {new EndSystem("ES2"),new EndSystem("ES4")};

		//Check that src ES exists
		if(!graph.containsVertex(src)){

		}
		
		//Then check that all dest ES exists
		for(EndSystem es : dests){
			if(!graph.containsVertex(es)){
				//CONTINUE
			}
		}
		applications.add(new SRApplication(name, type, payloadSize, noOfFrames , src, dests));
		//}
		
		return applications;
	}
}
