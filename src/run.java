import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;




import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;


public class run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Table<String, String, String> grid = TreeBasedTable.create();
		
		File EQ_File = new File("./xml/Assignment_EQ_reduced.xml");
		File SSH_File = new File("./xml/Assignment_SSH_reduced.xml");
		
		ReadXML readxml = new ReadXML();
		grid = readxml.ReadXMLinTable(EQ_File,grid);
		grid = readxml.ReadXMLinTable(SSH_File, grid);
		
		CalcYMatrix calcYmatrix = new CalcYMatrix();
		calcYmatrix.CalcYMatrixFromGrid(grid);
		//System.out.println(grid.toString());
		
		
//		Table<String, String, String> testTable = TreeBasedTable.create();
//		Map<String, String> testMap = new HashMap<String, String>();
//		testMap.put("TagName", "geraet1");
//		testMap.put("Wert", "10");
//		testTable.row("1").putAll(testMap);
//		System.out.println(testTable.row("1"));
//		for (int i= 0; i < grid.column("TagName").size(); j++)
//		{
//			grid.column
//		}
		
		
		
//		System.out.println(grid.column()
	//	System.out.println(getKeysByValue(grid.column("TagName"), "cim:BusbarSection"));
		
//		Table<String, String, String> grid = HashBasedTable.create();
//		
//		grid.put("Peter", "Lohn","12");
//		grid.put("Hans", "Lohn","13");
//		grid.put("Fred", "Monatslohn","1500");
//		System.out.println( grid.column("Lohn").toString());
	

	}


}
