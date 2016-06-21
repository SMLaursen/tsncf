package dk.smlaursen.TSNCF.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dk.smlaursen.TSNCF.architecture.Bridge;
import dk.smlaursen.TSNCF.architecture.EndSystem;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;

public class TopologyParser {

	private static final int RATE = 100;
	private static final double DEVICE_DELAY = 5.12;
	
	public static AbstractBaseGraph<Node, GCLEdge> parse(File f){
		AbstractBaseGraph<Node, GCLEdge> graph = new SimpleDirectedGraph<Node, GCLEdge>(GCLEdge.class);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom;

		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(f);
			Element docEle = dom.getDocumentElement();

			Element graphEle = (Element) docEle.getElementsByTagName("graph").item(0);
			Map<String, Node> nodeMap = new HashMap<String, Node>();
			
			String edgeDefault = graphEle.getAttribute("edgedefault");
			boolean isDirected;
			switch(edgeDefault){
			case "directed" : isDirected = true; break;
			case "undirected" : isDirected = false; break;
			default : throw new InputMismatchException("edgeDefault "+edgeDefault+" is not supported");
			}
			
			//Parse nodes and create graph-vertices accordingly
			NodeList nl = graphEle.getElementsByTagName("node");
			if(nl != null && nl.getLength() > 0){
				for(int i = 0; i < nl.getLength(); i++){
					String nodeName = ((Element) nl.item(i)).getAttribute("id");
					if(nodeName == null){
						throw new InputMismatchException("Aborting : nodes don't contain 'id' attribute");
					} 
					nodeName = nodeName.toUpperCase();
					Node n;
					if(nodeName.startsWith("ES")){
						n = new EndSystem(nodeName);
					} else if(nodeName.startsWith("B") || nodeName.startsWith("SW")){
						n = new Bridge(nodeName);
					} else {
						throw new InputMismatchException("Aborting : Node type of "+nodeName+" unrecognized.");
					}
					nodeMap.put(nodeName, n);
					graph.addVertex(n);
				}
			}

			//Parse edges and create graph-edges accordingly
			nl = graphEle.getElementsByTagName("edge");
			if(nl != null && nl.getLength() > 0){
				for(int i = 0; i < nl.getLength(); i++){
					String source = ((Element) nl.item(i)).getAttribute("source");
					if(source == null){
						throw new InputMismatchException("Aborting : edge didn't contain any source");
					} 
					source = source.toUpperCase();
					
					String target = ((Element) nl.item(i)).getAttribute("target");
					if(target == null){
						throw new InputMismatchException("Aborting : edge didn't contain any target");
					} 
					target = target.toUpperCase();
					graph.addEdge(nodeMap.get(source), nodeMap.get(target), new GCLEdge(RATE, DEVICE_DELAY));
					if(!isDirected){
						graph.addEdge(nodeMap.get(target), nodeMap.get(source), new GCLEdge(RATE, DEVICE_DELAY));
					}
				}
			}
			nodeMap.clear();
		} catch(ParserConfigurationException pce){
			pce.printStackTrace();
		} catch(SAXException se){
			se.printStackTrace();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
		return graph;
	}
}
