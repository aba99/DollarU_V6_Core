package com.orsyp.client.amex;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.orsyp.tools.ps.Connector;

import au.com.bytecode.opencsv.CSVReader;


public class CreateCabrinUprocs {

	public static HashMap<String,ArrayList<String>> reference = new HashMap<String,ArrayList<String>>();
	public static String cabrin_template = "CABRIN_TEMPLATE";
	
	public static void main(String[] args) throws Exception  
	{		

		String refFile = args[0];
		String configFile = args[1];
		
		readReferenceFile(refFile);
		Connector conn = new Connector(configFile,true,"CABRIN_",false,"",false,"");
		
		for(String key:reference.keySet())
		{
			ArrayList<String> currentListOfJobs = reference.get(key);
			
			for(int j=0;j<currentListOfJobs.size();j++)
			{
				conn.getConnectionList().get(0).duplicateUproc(cabrin_template,
																"CABRIN_"+currentListOfJobs.get(j).toUpperCase());

			}
				
		}//get cabrin_uprocs ready
		
		
		
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
		        	String value=line[1];
		        
		        	
		        	if(!reference.containsKey(key))
		        	{
		        		ArrayList<String> placer = new ArrayList<String>();
		        		placer.add(value);
		        		reference.put(key, placer);
		        	}
		        	else
		        	{
		        		if(!reference.get(key).contains(value))
		        		{
		        			reference.get(key).add(value);
		        		}
		        	}
		        		
		        }
		    }	
	}
}