package com.orsyp.client.amex;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.session.Session;
import com.orsyp.tools.ps.Connector;



public class OrConditionalDep {

	static HashMap<String,ArrayList<String>> table = new HashMap<String,ArrayList<String>>();
	static HashMap<String,String> uproc_session_mapping = new HashMap<String,String>();
	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		String csvFile  = args[1];
		String csvFile_2 = args[2];
		
		
		Connector conn = new Connector(fileName,true,"",true,"",false,"");
		
		
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
		
		@SuppressWarnings("resource")
		CSVReader reader_2 = new CSVReader(new FileReader(csvFile_2),',', '\"', '\0');

		String [] line2;			

		//parse lines
		while ((line2 = reader_2.readNext()) != null) 
	    {	    	
			
	        if(line2.length>1)
	        {
	        	String key=line2[0];
	        	String value = line2[1];
	        	
	        	uproc_session_mapping.put(key, value);
	        }
	        	
	
	        
	        
    
	    }	
		
		
		for(String uprKey:table.keySet())
		{
			ArrayList<String>deps = table.get(uprKey);
			HashMap<String,String>conditionsToOr = new HashMap<String,String>();
			
			for(int d=0;d<deps.size();d++)
			{
				if(uproc_session_mapping.containsKey(deps.get(d)))
				{
					String sesname = uproc_session_mapping.get(deps.get(d));
					if(conn.getConnectionList().get(0).getSessionsHashMap_from_outside().containsKey(sesname))
					{
						Session ses = conn.getConnectionList().get(0).getSessionsHashMap_from_outside().get(sesname);
						
						conditionsToOr.put(deps.get(d), ses.getHeader());

					}

				}

			}
			conn.getConnectionList().get(0).addConditionalDependency(uprKey, conditionsToOr);
			
		}
		/*for(String uprocKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			if(!table.containsKey(uprocKey))
			{
				conn.getConnectionList().get(0).getUprocHashMap_from_outside().get(uprocKey).delete();
			}
		}*/
		
	
	}

}

	

