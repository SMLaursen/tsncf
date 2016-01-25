package dk.smlaursen.TSNAF.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dk.smlaursen.TSNAF.application.Application;
import dk.smlaursen.TSNAF.application.SRApplication;
import dk.smlaursen.TSNAF.application.SRType;
import dk.smlaursen.TSNAF.application.TTApplication;
import dk.smlaursen.TSNAF.architecture.Bridge;
import dk.smlaursen.TSNAF.architecture.EndSystem;

public class ApplicationParser {
	
	/**Parses the  */
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
		SRType type;

		//Parse SRType
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
		
		return new SRApplication(name, type, payloadSize, noOfFrames, src, dest);
	}
	
	/**Parses an element into a {@link TTApplication}
	 * @param ttAppEle the TTApplicationElement
	 * @return The corresponding {@link TTApplication}*/
	private static TTApplication getTTApplication(Element ttAppEle){
		String name = ttAppEle.getAttribute("name");
		int payloadSize = parsePayloadSize(ttAppEle);
		int noOfFrames = parseNoOfFrames(ttAppEle);

		EndSystem src = parseSource(ttAppEle);
		List<List<Bridge>> path = parseExplicitPath(ttAppEle);
		
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
	
	private static List<List<Bridge>> parseExplicitPath(Element ele){
		List<List<Bridge>> path = null;
		Element destEl = (Element) ele.getElementsByTagName("Destinations").item(0);
		NodeList destNL = destEl.getElementsByTagName("Dest");
		if(destNL != null && destNL.getLength() > 0){
			path = new ArrayList<List<Bridge>>(destNL.getLength());
			for(int i= 0; i < destNL.getLength(); i++){
				Element routeEL = (Element) ele.getElementsByTagName("Route").item(0);
				if(routeEL == null){
					throw new IllegalArgumentException("Route to "+((Element) destNL.item(i)).getAttribute("name")+" not specified");
				}
				NodeList routeNL = routeEL.getElementsByTagName("Bridge");
				if(routeNL != null && routeNL.getLength() > 0){
					path.add(i, new LinkedList<Bridge>());
					for(int u= 0; u < routeNL.getLength(); u++){
						path.get(i).add(new Bridge(((Element) routeNL.item(u)).getAttribute("name")));
					}
				} 
			}
		} 
		return path;
	}
}
