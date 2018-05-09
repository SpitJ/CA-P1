import java.io.File;
import java.util.Objects;

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
			
			// Convert xml interface document to NodeList containing everything 
			NodeList xmlNodeList = doc.getElementsByTagName("*");
		
		
			// Loop through nodelist
			for (int i = 0; i < xmlNodeList.getLength(); i++) {  
				Element element = (Element) xmlNodeList.item(i);
				
				//check if element has rdf:ID
				if(Objects.equals(element.getAttribute("rdf:ID"), "") == false)
				{
					System.out.println("------------------------");
					System.out.println(element.getTagName());
					System.out.println(element.getAttribute("rdf:ID"));
					
					NodeList NodesOfTagList =  element.getElementsByTagName("*");
					for (int j= 0; j < NodesOfTagList.getLength(); j++) { // … use extractNode method 
						Element ElementOfElement = (Element) NodesOfTagList.item(j);
						
						if(ElementOfElement.getAttributes().getLength() == 0)
						{
							System.out.println(ElementOfElement.getTagName());
							System.out.println(ElementOfElement.getTextContent());
						}
						else
						{
							Node attr = ElementOfElement.getAttributes().item(0);
							System.out.println(ElementOfElement.getTagName() + "_" + attr.getNodeName());
							System.out.println(attr.getNodeValue().substring(1));
						}
						
						System.out.println("");
					}
					
				}
			   }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
