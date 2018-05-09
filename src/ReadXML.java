import java.io.File;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

public class ReadXML {
	public Table ReadXMLinTable(File XmlFile, Table<String, String, String> grid)
	{
		
				
		try {
			
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlFile);
					 
			doc.getDocumentElement().normalize();
			
			// Convert xml interface document to NodeList containing everything 
			NodeList xmlNodeList = doc.getElementsByTagName("*");
		
		
			// Loop through nodelist
			for (int i = 0; i < xmlNodeList.getLength(); i++) {  
				Element element = (Element) xmlNodeList.item(i);
				
				//check if element has 
				String row = "";
				String column = "";
				if(Objects.equals(element.getAttribute("rdf:ID"), "") == false)
				{
					row = element.getAttribute("rdf:ID");
				}
				else if(Objects.equals(element.getAttribute("rdf:about"), "") == false)
				{
					row = element.getAttribute("rdf:about").substring(1);
				}
				
				if(row != "")
				{
					column = "TagName";
					grid.put(row, column, element.getTagName());
					
//					System.out.println("------------------------");
//					System.out.println(element.getTagName());
//					System.out.println(element.getAttribute("rdf:ID"));
//					
//									
					NodeList NodesOfTagList =  element.getElementsByTagName("*");
					for (int j= 0; j < NodesOfTagList.getLength(); j++) { // … use extractNode method 
						Element ElementOfElement = (Element) NodesOfTagList.item(j);
						
						if(ElementOfElement.getAttributes().getLength() == 0)
						{
							column = ElementOfElement.getTagName();
							grid.put(row, column, ElementOfElement.getTextContent());
//							System.out.println(ElementOfElement.getTagName());
//							System.out.println(ElementOfElement.getTextContent());
						}
						else
						{
							Node attr = ElementOfElement.getAttributes().item(0);
							column = ElementOfElement.getTagName() + "_" + attr.getNodeName();
							grid.put(row, column, attr.getNodeValue().substring(1));
//							System.out.println(ElementOfElement.getTagName() + "_" + attr.getNodeName());
//							System.out.println(attr.getNodeValue().substring(1));
						}
						
//						System.out.println("");
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
//		System.out.println(grid.toString());
//		String testrdf = grid.get("_b58bf21a-096a-4dae-9a01-3f03b60c24c7", "cim:Equipment.EquipmentContainer_rdf:resource");
//		System.out.println(grid.row(testrdf).toString());
//		System.out.println(grid.get("_b58bf21a-096a-4dae-9a01-3f03b60c24c7", "cim:Equipment.EquipmentContainer_rdf:resource"));
		//System.out.println(grid.column("TagName").toString());
		return grid;
	}
}
