package dk.smlaursen.TSNCF;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.evaluator.ModifiedAVBEvaluator;
import dk.smlaursen.TSNCF.parser.ApplicationParser;
import dk.smlaursen.TSNCF.parser.TopologyParser;
import dk.smlaursen.TSNCF.solver.Solver;
import dk.smlaursen.TSNCF.solver.VLAN;
import dk.smlaursen.TSNCF.solver.KShortestPath.KShortestPathSolver_SR;
import dk.smlaursen.TSNCF.visualization.Visualizer;

public class Main {
	//Command line options
	private static final String APP_ARG = "app",NET_ARG = "net", DISP_ARG = "display", VERBOSE_ARG = "verbose";

	//FIXME todos
	//////////////////////////////////////////////
	//TODO Create pre-processor and validator  
	//TODO Improve logging and error handling
	//TODO Create GUI in separate project
	//////////////////////////////////////////////
	public static void main(String[] args){
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

			if(line.hasOption(VERBOSE_ARG)){
				System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
			}
			Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());
			boolean display = line.hasOption(DISP_ARG);

			//Parse Topology
			logger.debug("Parsing Topology from "+net.getName());
			Graph<Node, GCLEdge> graph= TopologyParser.parse(net);
			logger.info("Topology parsed!");

			Visualizer vis = new Visualizer(graph);
			
			//Display Application?
			if(display){
				vis.topologyPanel();
			}

			//Parse Applications
			logger.debug("Parsing application set from "+app.getName());
			List<Application> apps = ApplicationParser.parse(app);
			logger.info("Applications parsed! ");

			//Solve problem
			logger.debug("Solving problem");
			Solver s = new KShortestPathSolver_SR();
			Set<VLAN> sol = s.solve(graph, apps, new ModifiedAVBEvaluator());
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
