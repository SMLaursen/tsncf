package dk.smlaursen.TSNCF.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dk.smlaursen.TSNCF.application.Application;
import dk.smlaursen.TSNCF.application.ExplicitPath;
import dk.smlaursen.TSNCF.application.SRApplication;
import dk.smlaursen.TSNCF.application.SRType;
import dk.smlaursen.TSNCF.application.TTApplication;
import dk.smlaursen.TSNCF.architecture.Bridge;
import dk.smlaursen.TSNCF.architecture.EndSystem;
import dk.smlaursen.TSNCF.architecture.GCL;

public class ApplicationParser {
	private static Logger logger = LoggerFactory.getLogger(ApplicationParser.class.getSimpleName());
	/**Parses the applications from an XML file
	 * @param f the XML formatted {@link File}
	 * @return a List of {@link Application}s  */
	public static List<Application> parse(File f){

		List<Application> applications = new LinkedList<Application>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom;

		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(f);
			Element docEle = dom.getDocumentElement();

			//Get nodelist of SRApplicationElements
			NodeList nl = docEle.getElementsByTagName("SRApplication");
			if(nl != null && nl.getLength() > 0){
				for(int i = 0; i < nl.getLength(); i++){
					//Get the SRApplication element
					Element srAppEle = (Element) nl.item(i);
					//Get the SRApplication object
					Application srApp = getSRApplication(srAppEle);
					//Add it to the application list
					applications.add(srApp);
				}
			}

			//Get nodelist of TTApplicationElements
			nl = docEle.getElementsByTagName("TTApplication");
			if(nl != null && nl.getLength() > 0){
				for(int i = 0; i < nl.getLength(); i++){
					//Get the TTApplication element
					Element ttAppEle = (Element) nl.item(i);
					//Get the TTApplication object
					Application ttApp = getTTApplication(ttAppEle);
					//Add it to the application list
					applications.add(ttApp);
				}
			}
		} catch(ParserConfigurationException pce){
			pce.printStackTrace();
		} catch(SAXException se){
			se.printStackTrace();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}

		return applications;
	}

	/**Parses an element into a {@link SRApplication}
	 * @param srAppEle the SRApplicationElement
	 * @throws A
	 * @return The corresponding {@link SRApplication}*/
	private static SRApplication getSRApplication(Element srAppEle){
		String name = srAppEle.getAttribute("name");
		List<String> modes = parseModes(srAppEle);

		//Parse SRType
		SRType type;
		String text =  srAppEle.getElementsByTagName("SRType").item(0).getFirstChild().getNodeValue();
		switch(text){
		case "CLASS_A" : type = SRType.CLASS_A; break;
		case "CLASS_B" : type = SRType.CLASS_B; break;
		default : return null;
		}

		//Parse PayloadSize
		int payloadSize = parsePayloadSize(srAppEle);

		//Parse NoOfFrames
		int noOfFrames = Integer.parseInt(srAppEle.getElementsByTagName("NoOfFrames").item(0).getFirstChild().getNodeValue());

		//Parse Source
		EndSystem src = parseSource(srAppEle);

		//Parse Destinations
		EndSystem[] dest = parseDestinations(srAppEle);

		return new SRApplication(name, modes, type, payloadSize, noOfFrames, src, dest);
	}

	/**Parses an element into a {@link TTApplication}
	 * @param ttAppEle the TTApplicationElement
	 * @return The corresponding {@link TTApplication}*/
	private static TTApplication getTTApplication(Element ttAppEle){
		String name = ttAppEle.getAttribute("name");
		int payloadSize = parsePayloadSize(ttAppEle);
		int noOfFrames = parseNoOfFrames(ttAppEle);

		EndSystem src = parseSource(ttAppEle);
		ExplicitPath path = parseExplicitPath(ttAppEle);

		EndSystem[] dest = parseDestinations(ttAppEle);
		return new TTApplication(name, payloadSize, noOfFrames, path, src, dest);
	}

	private static int parsePayloadSize(Element ele){
		return Integer.parseInt(ele.getElementsByTagName("PayloadSize").item(0).getFirstChild().getNodeValue());
	}

	private static int parseNoOfFrames(Element ele){
		return Integer.parseInt(ele.getElementsByTagName("NoOfFrames").item(0).getFirstChild().getNodeValue());
	}

	private static EndSystem parseSource(Element ele){
		return new EndSystem(((Element) ele.getElementsByTagName("Source").item(0)).getAttribute("name"));
	}

	private static EndSystem[] parseDestinations(Element ele){
		EndSystem[] dest = null;
		Element el = (Element) ele.getElementsByTagName("Destinations").item(0);
		NodeList nl = el.getElementsByTagName("Dest");
		if(nl != null && nl.getLength() > 0){
			dest = new EndSystem[nl.getLength()];
			for(int i= 0; i < nl.getLength(); i++){
				dest[i] = new EndSystem(((Element) nl.item(i)).getAttribute("name"));
			}
		} 
		return dest;
	}

	private static List<String> parseModes(Element ele){
		List<String> modes = new LinkedList<String>();
		NodeList nl = ele.getElementsByTagName("Mode");
		if(nl != null && nl.getLength()>0){
			for(int i = 0; i<nl.getLength(); i++){
				modes.add(nl.item(i).getFirstChild().getNodeValue());
			}
		} else {
			//Default mode is just a white space
			modes.add(" ");
		}
		return modes;
	}

	/**Parses the route to each of the destination. The index of the outer ArrayList matches the EndSystem[] array returned by {@link #parseDestinations(Element)}
	 * Destinations with no explicit routes contains null in the returned List*/
	private static ExplicitPath parseExplicitPath(Element ele){
		List<List<Bridge>> path = null;
		List<GCL> gcl = null;
		Element destEl = (Element) ele.getElementsByTagName("Destinations").item(0);
		NodeList destNL = destEl.getElementsByTagName("Dest");
		NodeList gclNL = destEl.getElementsByTagName("GCL");
		//Parse GCL
		if(gclNL != null && gclNL.getLength() > 0){
			gcl = new LinkedList<GCL>();
			for(int i=0; i < gclNL.getLength(); i++){
				double off = Double.parseDouble(((Element) gclNL.item(i)).getAttribute("offset"));
				double dur = Double.parseDouble(((Element) gclNL.item(i)).getAttribute("duration"));
				double per = Double.parseDouble(((Element) gclNL.item(i)).getAttribute("period"));
				gcl.add(new GCL(off, dur, per));
			}
		}
		//Parse Path
		if(destNL != null && destNL.getLength() > 0){
			path = new ArrayList<List<Bridge>>(destNL.getLength());
			for(int i= 0; i < destNL.getLength(); i++){
				Element routeEL = (Element) ele.getElementsByTagName("Route").item(0);
				if(routeEL == null){
					if(logger.isDebugEnabled()){
						logger.debug("Route to "+((Element) destNL.item(i)).getAttribute("name")+" left unspecified.");
					}
					path.add(i, null);
				} else {
					NodeList routeNL = routeEL.getElementsByTagName("Bridge");
					if(routeNL != null && routeNL.getLength() > 0){
						path.add(i, new LinkedList<Bridge>());
						for(int u= 0; u < routeNL.getLength(); u++){
							path.get(i).add(new Bridge(((Element) routeNL.item(u)).getAttribute("name")));
						}
					} 
				}
			}
		} 
		return new ExplicitPath(gcl, path);
	}
}
