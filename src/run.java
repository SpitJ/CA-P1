import java.io.File;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public class run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Table<String, String, String> grid = HashBasedTable.create();
		
		File EQ_File = new File("./xml/Assignment_EQ_reduced.xml");
		File SSH_File = new File("./xml/Assignment_SSH_reduced.xml");
		
		ReadXML readxml = new ReadXML();
		grid = readxml.ReadXMLinTable(EQ_File,grid);
		grid = readxml.ReadXMLinTable(SSH_File, grid);
		
		System.out.println(grid.toString());
//		Table<String, String, String> grid = HashBasedTable.create();
//		
//		grid.put("Peter", "Lohn","12");
//		grid.put("Hans", "Lohn","13");
//		grid.put("Fred", "Monatslohn","1500");
//		System.out.println( grid.column("Lohn").toString());
		
		
	}

}
