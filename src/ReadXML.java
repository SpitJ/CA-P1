import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class ReadXML {
	public void ReadXMLinTable()
	{
		try {
			
			File XmlFile = new File("./xml/Assignment_EQ_reduced.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlFile);
					 
			doc.getDocumentElement().normalize();
			NodeList subList = doc.getElementsByTagName("cim:ACLineSegment");
			String rdfID;
			for (int i = 0; i < subList.getLength(); i++) { // … use extractNode method 
				Element element = (Element) subList.item(1);
				rdfID = element.getAttribute("rdf:ID");
			   }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
