import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;


public class run {

	public static void main(String[] args) {

		//Table with all containing information
		Table<String, String, String> grid = TreeBasedTable.create();
		
		File EQ_File = new File("./xml/Assignment_EQ_reduced.xml");
		File SSH_File = new File("./xml/Assignment_SSH_reduced.xml");
		
//		File EQ_File = new File("./xml/MicroGridTestConfiguration_T1_BE_EQ_V2.xml");
//		File SSH_File = new File("./xml/MicroGridTestConfiguration_T1_BE_SSH_V2.xml");
		
		// read XML grid info into grid table
		ReadXML readxml = new ReadXML();
		grid = readxml.ReadXMLinTable(EQ_File,grid);
		grid = readxml.ReadXMLinTable(SSH_File, grid);
		
		// calculate Y matrix with the grid table information
		CalcYMatrix calcYmatrix = new CalcYMatrix();
		Table<String, String, String> YMatrix = calcYmatrix.CalcYMatrixFromGrid(grid);
		
		System.out.println("Final Y Matrix:");
		System.out.println(YMatrix.toString());
		
		// Write determined matrices as a csv
		TableToCSV tabletocsv = new TableToCSV();
		tabletocsv.write(grid, "./csv/grid.csv");
		tabletocsv.write(YMatrix, "./csv/YMatrix.csv");
	}
	

}
