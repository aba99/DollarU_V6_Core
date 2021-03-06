package com.orsyp.client.amex;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.tools.ps.Connector;



public class AddDepConToUproc {

	static HashMap<String,ArrayList<String>> table = new HashMap<String,ArrayList<String>>();
	
	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		String csvFile = args[1];
		String cabrintemplate =args[2];
		cabrintemplate=cabrintemplate.toUpperCase();


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
	        		
	        		
	        		arrayList.add(line[l].trim());
	        		
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
		
		
		
		
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		
		
		for(String uprKey:table.keySet())
		{
			//conn.getConnectionList().get(0).duplicateUproc("TEMPLATE", uprKey);
				
				for(int s=0;s<table.get(uprKey).size();s++)
				{
					if(!conn.getConnectionList().get(0).doesUprocExist(table.get(uprKey).get(s)))
				
					{
							conn.getConnectionList().get(0).duplicateUproc(cabrintemplate, table.get(uprKey).get(s));
						
					}
				}
				
		}
		
		
		for(String uprKey:table.keySet())
		{
			
			try{
				
					conn.getConnectionList().get(0).addDepToUproc(uprKey,table.get(uprKey));
					
					
				}
				catch(Exception e)
				{
					System.out.println(uprKey+" failed");
					e.printStackTrace();
					continue;
				}
				
		}
		
	/*	for(String key:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			
				conn.getConnectionList().get(0).removeDuplicatedDepCons(key);			
		}
		
		for(String uproc:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			if(!table.keySet().contains(uproc))
			{
				conn.getConnectionList().get(0).getUprocHashMap_from_outside().get(uproc).delete();
			}
		}*/
				
	
	}

}

	

