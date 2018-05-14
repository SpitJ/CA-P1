import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.math3.complex.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class CalcYMatrix {
	// Calculates a Y Matrix from given Grid information
	public Table<String, String, String> CalcYMatrixFromGrid(Table<String, String, String> grid)
	{
		// read out all available bus bars and save relaation Busbar and BusBar connectivitynode for later usage
		Set<String> BusBars = getKeysByValue(grid.column("TagName"), "cim:BusbarSection");
		BiMap<String, String> BusBartoNode = HashBiMap.create();
		for (String bus : BusBars)
		{
			System.out.println(" Busbar: " + bus.toString());
			
			Set<String> terminals = getKeysByValue(grid.column("cim:Terminal.ConductingEquipment_rdf:resource"), bus);
			for (String terminal : terminals)
			{
				String nodeofterminal = grid.get(terminal, "cim:Terminal.ConnectivityNode_rdf:resource");
				BusBartoNode.put(bus, nodeofterminal);
				System.out.println("Node of Terminal: " + nodeofterminal);
				System.out.println("Node of Terminal: " + grid.get(nodeofterminal, "cim:IdentifiedObject.name"));				
			}
		}
		System.out.println(BusBartoNode.toString());
		
		// Loop through all Terminals of each bus bar connectivity node and build up Impedance Table
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
				row.put("Yab_r", "0");
				row.put("Yab_i", "0");
				row.put("Ya_r", "0");
				row.put("Ya_i", "0");
				System.out.println("*********Starting from Terminal at origin " + grid.get(terminal, "cim:IdentifiedObject.name"));
				row = analyseNextElement(terminal, busbarconnectivitynode, row, grid, BusBartoNode);
				String rowIdentifier = grid.get(busbarconnectivitynode, "cim:IdentifiedObject.name") + " to " +  grid.get(row.get("To"), "cim:IdentifiedObject.name");
				ImpedanceMatrix.row(rowIdentifier).putAll(row);
				System.out.println(ImpedanceMatrix.toString());
//				System.out.println("Node: busbarconnectivitynode " + busbarconnectivitynode + " has Terminal " + terminal);				
			}
		}
		TableToCSV tabletocsv = new TableToCSV();
		tabletocsv.write(ImpedanceMatrix, "./csv/ImpedanceMatrix.csv");
		
		//Convert Impedance Table to Y Matrix
		Table<String, String, String> YMatrix = TreeBasedTable.create();
		for (String busbar_a : BusBars)
		{
			String busbarconnectivitynode_a = BusBartoNode.get(busbar_a);
			for (String busbar_b : BusBars)
			{
				String busbarconnectivitynode_b = BusBartoNode.get(busbar_b);
				Set<String> ImpedanceMatrixRows = getKeysByValue(ImpedanceMatrix.column("From"), busbarconnectivitynode_a);
				Double Y_r = 0.0;
				Double Y_i = 0.0;
				if(!(Objects.equals(busbar_a, busbar_b)))
				{
					// Calculate Y for non-diagonal Values:
					Set<String> MatchingTos = new HashSet<String>();
					for(String ImpedanceMatrixRow : ImpedanceMatrixRows)
					{
						// Filter out only ImpedanceMatrixRows, where From = busbar_b and To = busbar_a
						if(Objects.equals(ImpedanceMatrix.get(ImpedanceMatrixRow, "To"),busbarconnectivitynode_b))
						{
							MatchingTos.add(ImpedanceMatrixRow);
						}
					}
					if(MatchingTos.isEmpty())
					{
						//Analyzed busbar_b has no connection to busbar_a -> Y = 0
					}
					else
					{
						//Analyzed busbar_b has a connection to busbar_a -> Y = - Yab
						Y_r = -Double.parseDouble(ImpedanceMatrix.get(MatchingTos.iterator().next(), "Yab_r"));
						Y_i = -Double.parseDouble(ImpedanceMatrix.get(MatchingTos.iterator().next(), "Yab_i"));
					}	
				}
				else
				{
					// Calculate Y for Diagonal values -> Y = Yab + Ya
					for(String ImpedanceMatrixRow : ImpedanceMatrixRows)
					{
						Y_r += Double.parseDouble(ImpedanceMatrix.get(ImpedanceMatrixRow, "Yab_r"));
						Y_i += Double.parseDouble(ImpedanceMatrix.get(ImpedanceMatrixRow, "Yab_i"));
						Y_r += Double.parseDouble(ImpedanceMatrix.get(ImpedanceMatrixRow, "Ya_r"));
						Y_i += Double.parseDouble(ImpedanceMatrix.get(ImpedanceMatrixRow, "Ya_i"));
					}
				}
				// add calculated Y to YMatrix
				Complex Y = new Complex(Y_r, Y_i);
				YMatrix.put(grid.get(busbar_a, "cim:IdentifiedObject.name"), grid.get(busbar_b, "cim:IdentifiedObject.name"), Y.toString());
			}
		}
		return YMatrix;
	}
	
	// recursive function determining if a node is a stop condition (reached another busbar), extracting possible info, determining next nodes connected to it and calling itself with determined next node  
	public static Map<String, String> analyseNextElement(String current, String previous, Map<String, String> row, Table<String, String, String> grid, BiMap<String, String> busbartonode)
	{
		System.out.println("Analyzing current " + grid.get(current, "cim:IdentifiedObject.name"));
		
		String TagNamecurrent = grid.get(current, "TagName");
		Set<String> possibleNexts = new HashSet<String>();
		Complex Yab = Complex.ZERO;
		Complex Ya = Complex.ZERO;
		
		
		// check if recursive loop should stop at current element
		if(busbartonode.containsKey(current))
		{
			// current node is a busbar
			System.out.println("current is a busbar: " + grid.get(current, "cim:IdentifiedObject.name"));
			row.put("To", current);
			return row;
		}
		if(busbartonode.containsValue(current))
		{
			// current node is a connectivity busbar connectivity node
			System.out.println("current is a connectivity busbar node: " + grid.get(current, "cim:IdentifiedObject.name"));
			row.put("To", current);
			return row;
		}
		if(Objects.equals(TagNamecurrent, "cim:EnergyConsumer"))
		{	 		
			double P = Double.parseDouble(grid.get(current, "cim:EnergyConsumer.p"));
			double Q = Double.parseDouble(grid.get(current, "cim:EnergyConsumer.q"));
	 		double Vlevel = Double.parseDouble(grid.get(grid.get(current, "cim:Equipment.EquipmentContainer_rdf:resource"),"cim:IdentifiedObject.name"));
	 		Complex V = new Complex(Vlevel, 0);
	 		Complex S = new Complex(P, Q);	 		
	 		Complex Z = V.pow(2).divide(S);
	 		Ya = Complex.ONE.divide(Z);
	 		
	 		System.out.println("current is a cim:EnergyConsumer: " + grid.get(current, "cim:IdentifiedObject.name"));
	 		System.out.println("Extracted Info: P = " + P + " Q = " + Q + " Vlevel = " + Vlevel + " Vlevel = " + V + " S = " + S.toString() + "  Z = " + Z.toString() + "  Ya = " + Ya.toString());
	 		row.put("To", current);
	 		row = addImpedancetoRow(row, Complex.ZERO, Ya);
	 		System.out.println(row);
	 		return row;
		}
		if(Objects.equals(TagNamecurrent, "cim:GeneratingUnit"))
		{ 
			System.out.println("current is a cim:GeneratingUnit: " + grid.get(current, "cim:IdentifiedObject.name"));
			row.put("To", current);
			return row;
		}
		if(Objects.equals(TagNamecurrent, "cim:LinearShuntCompensator"))
		{
			double B = Double.parseDouble(grid.get(current, "cim:LinearShuntCompensator.bPerSection"));
	 		double G = Double.parseDouble(grid.get(current, "cim:LinearShuntCompensator.gPerSection"));
	 		Ya = new Complex(G, B);
	 		
	 		System.out.println("current is a cim:LinearShuntCompensator: " + grid.get(current, "cim:IdentifiedObject.name"));
	 		System.out.println("Extracted Info: B = " + B + " G = " + G + " Ya = " + Ya );
	 		row.put("To", current);
	 		row = addImpedancetoRow(row, Complex.ZERO, Ya);
	 		System.out.println(row);
			return row; 
		}
		if(Objects.equals(TagNamecurrent, "cim:Breaker"))
		{
			if(Objects.equals(grid.get(current, "cim:Switch.open"),"true"))
			{
				System.out.println("current is a cim:Breaker and open: " + grid.get(current, "cim:IdentifiedObject.name"));
				row.put("To", current);
				return row;
			}
		}
		 
		 
		// Extract necessary Element Information:
		if(Objects.equals(TagNamecurrent, "cim:PowerTransformer"))
		{
			Set<String> PowertransformerEnds = getKeysByValue(grid.column("cim:PowerTransformerEnd.PowerTransformer_rdf:resource"), current);
			double R = 0;	
			double X = 0;	
			double B = 0;	
			double G = 0;	
			for(String PowertransformerEnd : PowertransformerEnds)
	 		{
				R = Double.parseDouble(grid.get(PowertransformerEnd, "cim:PowerTransformerEnd.r"));
		 		X = Double.parseDouble(grid.get(PowertransformerEnd, "cim:PowerTransformerEnd.x"));
		 		B = Double.parseDouble(grid.get(PowertransformerEnd, "cim:PowerTransformerEnd.b"));
		 		G = Double.parseDouble(grid.get(PowertransformerEnd, "cim:PowerTransformerEnd.g"));
	 		}
			Ya = new Complex(G, B);
	 		Complex Z = new Complex(R, X);
	 		if(R == 0 && X == 0)
	 		{
	 			Yab = Complex.ZERO;
	 		}
	 		else
	 		{
	 			Yab = Complex.ONE.divide(Z);
	 		}
	 		System.out.println("current is a cim:PowerTransformer. Extracting info");
	 		System.out.println("Extracted Info: B = " + B + " G = " + G + " Ya = " + Ya + " R = " + R + " X = " + X + " Yab = " + Yab);	 		
		}
		if(Objects.equals(TagNamecurrent, "cim:ACLineSegment"))
		{
			System.out.println("current is a cim:ACLineSegment. Extracting info");
			double B = Double.parseDouble(grid.get(current, "cim:ACLineSegment.bch"));
	 		double G = Double.parseDouble(grid.get(current, "cim:ACLineSegment.gch"));
	 		Ya = new Complex(G, B);
	 		
	 		double R = Double.parseDouble(grid.get(current, "cim:ACLineSegment.r"));
	 		double X = Double.parseDouble(grid.get(current, "cim:ACLineSegment.x"));
	 		Complex Z = new Complex(R, X);
	 		Yab = Complex.ONE.divide(Z);
	 		
	 		System.out.println("Extracted Info: B = " + B + " G = " + G + " Ya = " + Ya + " R = " + R + " X = " + X + " R = " + Yab   );	 		
		}	 
		row = addImpedancetoRow(row, Yab, Ya);
		System.out.println(row);
		 
		 
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
	
	// helper function to add complex values to row
	static Map<String, String> addImpedancetoRow(Map<String, String> row, Complex Yab, Complex Ya)
	{
		row.put("Yab_r", String.valueOf((Double.parseDouble(row.get("Yab_r")) + Yab.getReal())));
		row.put("Yab_i", String.valueOf((Double.parseDouble(row.get("Yab_i")) + Yab.getImaginary())));
		row.put("Ya_r", String.valueOf((Double.parseDouble(row.get("Ya_r")) + Ya.getReal())));
		row.put("Ya_i", String.valueOf((Double.parseDouble(row.get("Ya_i")) + Ya.getImaginary())));
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
