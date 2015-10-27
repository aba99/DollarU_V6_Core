package com.orsyp.client.amex;




import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.tools.ps.Connector;



public class RemoveDepConFromUpr {

	private static HashMap<String,ArrayList<String>>table = new HashMap<String,ArrayList<String>>();
	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		String csvFile = args[1];
		
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		
		@SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(csvFile),',', '\"', '\0');
		ArrayList<String> arrayList = new ArrayList<String>();

		String [] line;			

		//parse lines
		while ((line = reader.readNext()) != null) 
	    {	    	
			
	        if(line.length>1)
	        {
	        	String key=line[0];
	        	
	        	
	        	for(int l=1;l<line.length;l++)
	        	{
	        		if(!line[l].trim().isEmpty())
	        			{
	        				arrayList.add(line[l].trim().toUpperCase());
	        			}
	        		
	        		
	        		
	        	}
	        	
	        	if(!table.containsKey(key))
	        	{
	        		ArrayList<String> placer = new ArrayList<String>();
	        		placer.addAll(arrayList);
	        		table.put(key, placer);
	        	}
	        	else
	        	{
	        		for(int e=0;e<arrayList.size();e++)
	        		{
	        			table.get(key).add(arrayList.get(e));
	        		}
	        	}
	
	        }
	        
    		arrayList.clear();

	    }	
		
		for(String uprocKey:table.keySet())
		{
			conn.getConnectionList().get(0).removeDepsFromUprocClean(uprocKey, table.get(uprocKey));
		}

				
	
	}

}

	
