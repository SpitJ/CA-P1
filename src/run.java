import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public class run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ReadXML readxml = new ReadXML();
//		readxml.ReadXMLinTable();
		
		Table<String, String, String> grid = HashBasedTable.create();
		
		grid.put("Peter", "Lohn","12");
		grid.put("Hans", "Lohn","13");
		grid.put("Fred", "Monatslohn","1500");
		System.out.println(grid.toString());
	}

}
