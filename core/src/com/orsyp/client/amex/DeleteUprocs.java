package com.orsyp.client.amex;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.orsyp.tools.ps.Connector;

import au.com.bytecode.opencsv.CSVReader;

public class DeleteUprocs {

	static ArrayList<String> notToDelete = new ArrayList<String>();
	public static void main(String[] args) throws Exception {
		String logList = args[0];
		String configFile = args[1];
		
		readReferenceFile(logList);
		Connector conn = new Connector(configFile,true,"",false,"",false,"");
		for(String uprKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			if(notToDelete.contains(uprKey))
			{
				continue;
			}
			else
			{
				conn.getConnectionList().get(0).getUprocHashMap_from_outside().get(uprKey).delete();
			}
		}

	}

	public static void readReferenceFile(String fileName) throws IOException
	{
		 @SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(fileName),',', '\"', '\0');
			
			String [] line;			

			//parse lines
			while ((line = reader.readNext()) != null) 
		    {	    	
				
		        if(line.length>1)
		        {
		        	String key=line[0];
		        	
		        	notToDelete.add(key);
		        		
		        }
		    }	
	}
	
}
