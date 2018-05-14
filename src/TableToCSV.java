import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
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



public class TableToCSV {
	// Calculates a Y Matrix from given Grid information
	public void write(Table<String, String, String> table, String outputFilePath)
	{   
		// Print Y matrix as csv
		
		try 
		{
			Writer out = new BufferedWriter(new FileWriter(outputFilePath));
			List<String> values = new ArrayList();
			
			values.add("");
//			System.out.println(table.columnKeySet().toString());
			for (String columnKey : table.columnKeySet())
			{
				values.add(columnKey);
			}
			writeLine(out, values);
			values.clear();
			for (String rowKey : table.rowKeySet())
			{
				values.add(rowKey);
				for (String columnKey : table.columnKeySet())
				{
					values.add(table.get(rowKey, columnKey));
				}
				writeLine(out, values);
				values.clear();
			}
			out.close();
		}
		catch (final Exception e) 
		{
		    e.printStackTrace();
		}
	}
	
	public void writeLine(Writer w, List<String> values) 
	        throws Exception
    {
        boolean firstVal = true;
        for (String val : values)  {
            if (!firstVal) {
                w.write(",");
            }
            w.write("\"");
            if(val != null && !val.isEmpty())
            {
            	for (int i=0; i<val.length(); i++) {
        		char ch = val.charAt(i);
    			if (ch=='\"') 
    			{
    				w.write("\"");  //extra quote
    			}
    			w.write(ch);
          }
            }
//            
            w.write("\"");
            firstVal = false;
        }
        w.write("\n");
    }
}
