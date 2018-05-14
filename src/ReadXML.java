import java.io.File;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.google.common.collect.Table;

public class ReadXML {
	// Reads grid information from XML file and stores it in Table
	public Table<String, String, String> ReadXMLinTable(File XmlFile, Table<String, String, String> grid)
	{	
		try 
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlFile);	 
			doc.getDocumentElement().normalize();
			
			// Convert xml interface document to NodeList containing everything 
			NodeList xmlNodeList = doc.getElementsByTagName("*");
		
			// Loop through nodelist
			for (int i = 0; i < xmlNodeList.getLength(); i++) {  
				Element element = (Element) xmlNodeList.item(i);
				
				
				String row = "";
				String column = "";
				// depending if it is a EQ or SSH file the rdf name will be different 
				if(Objects.equals(element.getAttribute("rdf:ID"), "") == false)
				{
					row = element.getAttribute("rdf:ID");
				}
				else if(Objects.equals(element.getAttribute("rdf:about"), "") == false)
				{
					row = element.getAttribute("rdf:about").substring(1);
				}
				
				//check if element is of interest (has valid rdf:ID), then extract and store information 
				if(row != "")
				{
					column = "TagName";
					grid.put(row, column, element.getTagName());
					
					// Iterate through sub nodes of node of interest 
					NodeList NodesOfTagList =  element.getElementsByTagName("*");
					for (int j= 0; j < NodesOfTagList.getLength(); j++) 
					{
						Element ElementOfElement = (Element) NodesOfTagList.item(j);
						
						// store all element of element information in grid table, also rdf_resource
						if(ElementOfElement.getAttributes().getLength() == 0)
						{
							column = ElementOfElement.getTagName();
							grid.put(row, column, ElementOfElement.getTextContent());
						}
						else
						{
							Node attr = ElementOfElement.getAttributes().item(0);
							column = ElementOfElement.getTagName() + "_" + attr.getNodeName();
							grid.put(row, column, attr.getNodeValue().substring(1));
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return grid;
	}
}
