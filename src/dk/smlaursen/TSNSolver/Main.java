package dk.smlaursen.TSNSolver;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNSolver.application.Application;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.evaluator.DisjointEdgesEvaluator;
import dk.smlaursen.TSNSolver.parser.ApplicationParser;
import dk.smlaursen.TSNSolver.parser.TopologyParser;
import dk.smlaursen.TSNSolver.solver.Solver;
import dk.smlaursen.TSNSolver.solver.VLAN;
import dk.smlaursen.TSNSolver.solver.KShortestPath.KShortestPathSolver_SR;
import dk.smlaursen.TSNSolver.visualization.Visualizer;

public class Main {
	//Command line options
	private static final String APP_ARG = "app",NET_ARG = "net", DISP_ARG = "display", VERBOSE_ARG = "v";
	
	//FIXME todos
	//////////////////////////////////////////////
	//TODO Create pre-processor and validator  
	//TODO Improve logging and error handling
	//TODO Add real Evaluator
	//TODO Create GUI in separate project
	//////////////////////////////////////////////
	
	public static void main(String[] args){
		Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());
		
		Option architectureFile = Option.builder(NET_ARG).required().argName("file").hasArg().desc("Use given file as network").build();
		Option applicationFile = Option.builder(APP_ARG).required().argName("file").hasArg().desc("Use given file as application").build();
		
		Options options = new Options();
		options.addOption(applicationFile);
		options.addOption(architectureFile);
		options.addOption(VERBOSE_ARG, false, "Verbose output");
		options.addOption(DISP_ARG, false, "Display output");
		
		CommandLineParser parser = new DefaultParser();
		
		try {
			//Parse command line arguments
			CommandLine line = parser.parse(options, args);
			//Required, so cannot be null
			File net = new File(line.getOptionValue(NET_ARG));
			File app = new File(line.getOptionValue(APP_ARG));
			
			boolean verbose = line.hasOption(VERBOSE_ARG);
			boolean display = line.hasOption(DISP_ARG);
			
			//Parse Topology
			logger.debug("Parsing Topology");
			Graph<Node, DefaultEdge> graph= TopologyParser.parse(net);
			logger.info("Parsed topology ");
			
			Visualizer vis = new Visualizer(graph);
			//Display Application?
			if(display){
				vis.topologyPanel();
			}
			
			//Parse Applications
			logger.debug("Parsing application set");
			List<Application> apps = ApplicationParser.parse(app);
			logger.info("Parsed applications  ");
			
			//Solve problem
			logger.debug("Solving problem");
			Solver s = new KShortestPathSolver_SR();
			Set<VLAN> sol = s.solve(graph, apps, new DisjointEdgesEvaluator());
			logger.info("Found solution ");
			
			if(display){
				vis.addSolutions(sol);
			}
		} catch (ParseException e) {
			System.err.println(e);
			
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ant", options );
		}
	}
}
