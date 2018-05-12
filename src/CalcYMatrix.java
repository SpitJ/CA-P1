import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;




public class CalcYMatrix {
	public Table CalcYMatrixFromGrid(Table<String, String, String> grid)
	{
		// read out all available bus bars and Link Nodes to Busbars
		
		Set<String> BusBars = getKeysByValue(grid.column("TagName"), "cim:BusbarSection");
		
		BiMap<String, String> BusBartoNode = HashBiMap.create();
		
		System.out.println(" Found Busbars: " + BusBars.toString());
		
		for (String bus : BusBars)
		{
			System.out.println(" Busbar: " + bus.toString());
			
			Set<String> terminals = getKeysByValue(grid.column("cim:Terminal.ConductingEquipment_rdf:resource"), bus);
			System.out.println(" Terminal of busbar: " + terminals.toString());
			for (String terminal : terminals)
			{
				String nodeofterminal = grid.get(terminal, "cim:Terminal.ConnectivityNode_rdf:resource");
				BusBartoNode.put(bus, nodeofterminal);
				System.out.println("Node of Terminal: " + nodeofterminal);
				System.out.println("Node of Terminal: " + grid.get(nodeofterminal, "cim:IdentifiedObject.name"));				
			}
		}
		System.out.println(BusBartoNode.toString());
		
		// Loop through all Terminals of each bus bar and build up table
		Table<String, String, String> ImpedanceMatrix = TreeBasedTable.create();
		
		for (String busbarconnectivitynode : BusBartoNode.values())
		{
			// find terminals of busbarconnectivitynode
			System.out.println("---------------- Starting from new BusBar at origin");
			Set<String> terminals = getKeysByValue(grid.column("cim:Terminal.ConnectivityNode_rdf:resource"), busbarconnectivitynode);
			for(String terminal : terminals)
			{
				Map<String, String> row = new HashMap<String, String>();
				row.put("From", busbarconnectivitynode);
				System.out.println("*********Starting from Terminal at origin " + grid.get(terminal, "cim:IdentifiedObject.name"));
				row = analyseNextElement(terminal, busbarconnectivitynode, row, grid, BusBartoNode);
				String rowIdentifier = grid.get(busbarconnectivitynode, "cim:IdentifiedObject.name") + " to " +  grid.get(row.get("To"), "cim:IdentifiedObject.name");
				ImpedanceMatrix.row(rowIdentifier).putAll(row);
				System.out.println(ImpedanceMatrix.toString());
//				System.out.println("Node: busbarconnectivitynode " + busbarconnectivitynode + " has Terminal " + terminal);				
			}
		}
//		System.out.println("result of mulitplication = " + multiply_recursive(5,5));
		
		//find Terminals of Busbar
//		System.out.println(getKeysByValue(grid.column("cim:Terminal.ConductingEquipment_rdf:resource"), "_fd649fe1-bdf5-4062-98ea-bbb66f50402d"));
		
		
		//Calculate Y Matrix from intermediate Table
		
		
		return grid;
	}
	
	public static Map<String, String> analyseNextElement(String current, String previous, Map<String, String> row, Table<String, String, String> grid, BiMap<String, String> busbartonode)
	{
	
		System.out.println("Analyzing current " + grid.get(current, "cim:IdentifiedObject.name"));
		
		String TagNamecurrent = grid.get(current, "TagName");
		Set<String> possibleNexts = new HashSet<String>();
		
		
		// check if current element is a stop
		 if(busbartonode.containsKey(current))
		 {
			 // current node is a busbar
			 System.out.println("current is a busbar: " + grid.get(current, "cim:IdentifiedObject.name"));
			 row.put("To", current);
			 return row;
		 }
		 if(busbartonode.containsValue(current))
		 {
			// current node is a connectivity busbar node
			 System.out.println("current is a connectivity busbar node: " + grid.get(current, "cim:IdentifiedObject.name"));
			 row.put("To", current);
			 return row;
		 }
		 switch(TagNamecurrent)
		 {
		 	// check if current element is a stop
		 	case "cim:EnergyConsumer":
		 		System.out.println("current is a cim:EnergyConsumer: " + grid.get(current, "cim:IdentifiedObject.name"));
		 		row.put("To", current);
				 return row;
		 	
		 	case "cim:GeneratingUnit":
		 		System.out.println("current is a cim:GeneratingUnit: " + grid.get(current, "cim:IdentifiedObject.name"));
		 		row.put("To", current);
				 return row;
		 	
		 	case "cim:LinearShuntCompensator":
		 		System.out.println("current is a cim:LinearShuntCompensator: " + grid.get(current, "cim:IdentifiedObject.name"));
		 		row.put("To", current);
				 return row;
				 
		 	case "cim:Breaker":
		 		if(Objects.equals(grid.get(current, "cim:Switch.open"),"true"))
		 		{
		 			System.out.println("current is a cim:Breaker and open: " + grid.get(current, "cim:IdentifiedObject.name"));
		 			row.put("To", current);
					return row;
		 		}
		 }
		 // TODO: add other stop applicable
		 
		 // find out next element and store information of current element
		 
		 
		 
		 // Extract necessary information
		 switch(TagNamecurrent)
		 {
		 	case "cim:ACLineSegment":
		 		
		 		System.out.println("current is a cim:ACLineSegment. Extracting info");
		 	break;
		 	case "cim:PowerTransformer":
		 		
		 		System.out.println("current is a cim:PowerTransformer. Extracting info");
		 	break;
		 	
		 }
		 
		 //Find possible Nexts:
		 switch(TagNamecurrent)
		 {
		 	case "cim:Terminal":
		 		possibleNexts.add(grid.get(current, "cim:Terminal.ConductingEquipment_rdf:resource"));
		 		possibleNexts.add(grid.get(current, "cim:Terminal.ConnectivityNode_rdf:resource"));
		 		System.out.println("current is a Terminal. Found nexts");
		 	break;
		 	case "cim:ConnectivityNode":
		 		possibleNexts = getKeysByValue(grid.column("cim:Terminal.ConnectivityNode_rdf:resource"), current);
		 		System.out.println("current is a ConnectivityNode. Found nexts");
		 	break;
		 	default: // element
		 		possibleNexts = getKeysByValue(grid.column("cim:Terminal.ConductingEquipment_rdf:resource"), current);
		 		System.out.println("current is a Element. Found nexts");
		 	break;
		 		
		 
		 }
		 
		 // only analyze not previous connected
		 for(String possibleNext : possibleNexts)
		 {
//			 System.out.println("Checking if " +  possibleNext + " with " + previous);
			 if(!(Objects.equals(possibleNext, previous)))
			 {
				 System.out.println("Calling possibl next: " + grid.get(possibleNext, "cim:IdentifiedObject.name"));
				 row = analyseNextElement(possibleNext, current, row, grid, busbartonode);
			 }
		 }
		 return row;
	}
	
	// helper function to obtain keys from a value
	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) 
	{
	    Set<T> keys = new HashSet<T>();
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            keys.add(entry.getKey());
	        }
	    }
	    return keys;
	}
}
