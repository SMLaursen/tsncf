package dk.smlaursen.TSNSolver.parser;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.application.SRApplication;
import dk.smlaursen.TSNSolver.application.SRType;
import dk.smlaursen.TSNSolver.architecture.EndSystem;
import dk.smlaursen.TSNSolver.architecture.Node;

public class ApplicationParser {
	private static Logger logger = LoggerFactory.getLogger(ApplicationParser.class.getSimpleName());
	
	/**Parses the */
	public static List<Application> parse(Graph<Node, DefaultEdge> graph){
		List<Application> applications = new LinkedList<Application>();
		
		//FIXME Parse file
		//For each application {:
		String app1name = "APP1";
		int payloadSize = 8;
		int noOfFrames = 10;
		SRType type = SRType.CLASS_A;
		EndSystem app1src = new EndSystem("ES1");
		EndSystem[] app1dests = {new EndSystem("ES2"), new EndSystem("ES4")};
		
		

		//Check that src ES exists
		if(!graph.containsVertex(app1src)){
			logger.error("Aborting : src node "+app1src+" could not be found in graph "+graph);
			return null;
		}
		
		//Then check that all dest ES exists
		for(EndSystem es : app1dests){
			if(!graph.containsVertex(es)){
				logger.error("Aborting : destination node "+es+" could not be found in graph "+graph);
				return null;
			}
		}
		applications.add(new SRApplication(app1name, type, payloadSize, noOfFrames , app1src, app1dests));
		//}

		EndSystem app2src = new EndSystem("ES1");
		EndSystem[] app2dests = {new EndSystem("ES3")};

		applications.add(new SRApplication("APP2", type, payloadSize, noOfFrames , app2src, app2dests));
		
		
		return applications;
	}
}
