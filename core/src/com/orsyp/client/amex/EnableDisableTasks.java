package com.orsyp.client.amex;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.orsyp.tools.ps.Connector;

import au.com.bytecode.opencsv.CSVReader;

public class EnableDisableTasks {

	static ArrayList<String> toEnableOrDisable = new ArrayList<String>();
	static String flag = "";
	public static void main(String[] args) throws Exception {
		
		String configFile = args[0];
		String logList = args[1];
		String enableOrDisable = args[2];
		flag=enableOrDisable;
		

		
		readListOfTaskNames(logList);
		System.out.println();
		
		Connector conn = new Connector(configFile,false,"",false,"",true,"");
		
		for(String tskKey:conn.getConnectionList().get(0).getTaskHashMap_from_outside().keySet())
		{
			if(toEnableOrDisable.contains(tskKey))
			{
				if(enableOrDisable.equalsIgnoreCase("enable"))
				{
					if(!conn.getConnectionList().get(0).getTaskHashMap_from_outside().get(tskKey).isActive())
					{
						conn.getConnectionList().get(0).getTaskHashMap_from_outside().get(tskKey).enable();
						System.out.println(tskKey +" is now enabled");
					}
				}
				else if(enableOrDisable.equalsIgnoreCase("disable"))
				{
					if(conn.getConnectionList().get(0).getTaskHashMap_from_outside().get(tskKey).isActive())
					{
						conn.getConnectionList().get(0).getTaskHashMap_from_outside().get(tskKey).disable();
						System.out.println(tskKey +" is now disabled");

					}
				}
				else
				{
					continue;
				}
			}
		}

	}

	public static void readListOfTaskNames(String fileName) throws IOException
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
		        	System.out.println("Task found "+key+" to be "+flag);

		        	toEnableOrDisable.add(key);
		        		
		        }
		    }	
	}
	
}
