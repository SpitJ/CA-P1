import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;



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
		
		for (String busbarconnectivitynode : BusBartoNode.values())
		{
			// find terminals of busbarconnectivitynode
			System.out.println("---------------- Starting from new BusBar at origin");
			Set<String> terminals = getKeysByValue(grid.column("cim:Terminal.ConnectivityNode_rdf:resource"), busbarconnectivitynode);
			for(String terminal : terminals)
			{
				System.out.println("*********Starting from Terminal at origin");
				analyseNextElement(terminal, busbarconnectivitynode, grid, BusBartoNode);
//				System.out.println("Node: busbarconnectivitynode " + busbarconnectivitynode + " has Terminal " + terminal);				
			}
		}
//		System.out.println("result of mulitplication = " + multiply_recursive(5,5));
		
		//find Terminals of Busbar
//		System.out.println(getKeysByValue(grid.column("cim:Terminal.ConductingEquipment_rdf:resource"), "_fd649fe1-bdf5-4062-98ea-bbb66f50402d"));
		
		
		
		return grid;
	}
	
	public static void analyseNextElement(String current, String previous, Table<String, String, String> grid, BiMap<String, String> busbartonode)
	{
		System.out.println("Analyzing current");
		 // check if current element is a stop
		 if(busbartonode.containsKey(current))
		 {
			 // current node is a busbar
			 System.out.println("current is a busbar: " + current);
			 return;
		 }
		 else if(busbartonode.containsValue(current))
		 {
			// current node is a connectivity busbar node
			 System.out.println("current is a connectivity busbar node: " + current);
			 return;
		 }
		 // TODO: add other stop applicable
		 
		 // find out next element and store information of current element
		 Set<String> possibleNexts = new HashSet<String>();
		 String TagNamecurrent = grid.get(current, "TagName");
		 switch(TagNamecurrent)
		 {
		 	case "cim:Terminal":
		 		possibleNexts.add(grid.get(current, "cim:Terminal.ConductingEquipment_rdf:resource"));
		 		possibleNexts.add(grid.get(current, "cim:Terminal.ConnectivityNode_rdf:resource"));
		 		System.out.println("current is a Terminal. Found nexts ");
		 	break;
		 	case "cim:ConnectivityNode":
		 		possibleNexts = getKeysByValue(grid.column("cim:Terminal.ConnectivityNode_rdf:resource"), current);
		 		System.out.println("current is a ConnectivityNode. Found nexts ");
		 	break;
		 	default:
		 		System.out.println("current is a unkown node. Stopping");
		 		return;		 		
		 
		 }
		 
		 // only analyze not previous connected
		 for(String possibleNext : possibleNexts)
		 {
			 if(possibleNext != previous)
			 {
				 analyseNextElement(possibleNext, current, grid, busbartonode);
			 }
		 }
		 return;
	}
	
	
	
//	public static int multiply_recursive(int multi1, int mulit2) 
//	{
//		System.out.println("Call of function, multi2 = " + mulit2);
//		if(mulit2 == 0 )
//		{
//			
//			return 0;
//		}
//		else
//		{
//			
//			return multiply_recursive(multi1, mulit2-1)+multi1; 
//		}
//	}
	
	
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
