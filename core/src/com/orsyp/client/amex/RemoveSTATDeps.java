package com.orsyp.client.amex;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.orsyp.tools.ps.Connector;



public class RemoveSTATDeps {

	static HashMap<String,String> deps = new HashMap<String,String>();
	static int count = 1;
	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		String xmlFile_1 = args[1];
		String xmlFile_2 = args[2];
		
		
		
		readFile(xmlFile_1);
		readFile(xmlFile_2);
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		for(String uprKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			
			//conn.getConnectionList().get(0).setSessionControlOnUproc(uprKey);
			conn.getConnectionList().get(0).removeDepsFromUproc(uprKey, deps);
			
		}
	}	
		
	private static void readFile(String file) throws IOException
	{ 
		
	
		String flag_1="<OUTCOND NAME=";
		String flag_2="<INCOND NAME=";
		String flag_3="ODATE=\"STAT\" />";
		
		String delim_1 = "-";
		String delim_2 = "_";
		 
		try (Scanner sc = new Scanner(new File(file), "UTF-8")) {
		        while (sc.hasNextLine()) {
		            String line = sc.nextLine();
		           // System.out.println(line);
		            if(line.contains(flag_1)||line.contains(flag_2))
		            {
		            	@SuppressWarnings("unused")
						String interline_1 = sc.nextLine();//skip
		            	String interline_2 = sc.nextLine();
		            	if(interline_2.equals(flag_3))
		            	{
		            		String dep = line.substring(line.indexOf(delim_1)+1
		            				,line.indexOf(delim_2));
		            		
		            		if(!deps.containsKey(dep))
		            		{
		            			deps.put(dep,dep);
		            			System.out.println(count+"-DEP "+dep+" added"); 
		            			count++;
		            		}
		            		
		            	}
		            }
		           
		        }
		        // note that Scanner suppresses exceptions
		        if (sc.ioException() != null) {
		            throw sc.ioException();
		        }
	}


	}}

