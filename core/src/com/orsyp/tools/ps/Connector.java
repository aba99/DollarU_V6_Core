package com.orsyp.tools.ps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import com.orsyp.tools.ps.DuApiConnection;


public class Connector {


	private static HashMap<String,String> config_params = new HashMap<String,String>();
	private static HashMap<String,DuApiConnection> DUAPIConnections = new HashMap<String,DuApiConnection>();
	private static String logfile ="";
	
	FileOutputStream fout=null;	
    PrintStream prtstm=null;
	
public Connector(String connectFile,boolean withUpr,String uprfilter,boolean withSes,String sesfilter,boolean withTsk,String tskfilter) throws FileNotFoundException
{
	
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmm");
		String formattedDate = sdf.format(date);
		

	
		logfile = "conn_"+formattedDate+".log";
    	fout = new FileOutputStream (logfile);
    	prtstm = new PrintStream(fout);	
	
    	connect(connectFile,withUpr,uprfilter,withSes,sesfilter,withTsk,tskfilter);
}
public Connector(PrintStream prtstm_in,String connectFile,boolean withUpr,String uprfilter,boolean withSes,String sesfilter,boolean withTsk,String tskfilter) throws FileNotFoundException
{
	   	prtstm = prtstm_in;	
	
    	connect(connectFile,withUpr,uprfilter,withSes,sesfilter,withTsk,tskfilter);
}
public ArrayList<DuApiConnection> getConnectionList()
{
	ArrayList<DuApiConnection> result = new ArrayList<DuApiConnection>(DUAPIConnections.values());
	return result;
}
private void connect(String connectFile,boolean withUpr,String uprfilter,boolean withSes,String sesfilter,boolean withTsk,String tskfilter)
{
	/// Reading connectFile info
	Scanner sc = null;
	try {
		sc = new Scanner(new File(connectFile));
		while (sc.hasNext()) {
			String line=sc.nextLine().trim();
			if (line.length()>0)
				if (!line.startsWith("#")) 
					if (line.contains("="))
						config_params.put(line.split("=")[0].trim().toUpperCase(), line.substring(line.indexOf("=")+1).trim());
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		e.printStackTrace(prtstm);
		
	} finally {
		sc.close();
	}
	
	
	if(!config_params.containsKey("NODE") || !config_params.containsKey("PORT")
			||!config_params.containsKey("AREA")||!config_params.containsKey("HOST")
			|| !config_params.containsKey("USER")|| !config_params.containsKey("PASSWORD"))
	{
		System.out.println("Error ! Missing fields in UVMS Config   :\""+connectFile+"\"");
		prtstm.println("Error ! Missing fields in UVMS Config   :\""+connectFile+"\"");

		System.exit(-1);
	}
	
	String nodes[] = config_params.get("NODE").split(",");
	
	System.out.println("Connecting to UVMS : \""+config_params.get("HOST")+"\"");
	prtstm.println("Connecting to UVMS : \""+config_params.get("HOST")+"\"");
	
	for(int i=0;i<nodes.length;i++)
	{
		
		
		DuApiConnection conn= new DuApiConnection(prtstm,nodes[i], config_params.get("AREA"),config_params.get("HOST"),Integer.parseInt(config_params.get("PORT")),config_params.get("USER"),config_params.get("PASSWORD"),withUpr,uprfilter,withSes,sesfilter,withTsk,tskfilter);
		 
		
		if(!DUAPIConnections.containsKey(nodes[i]+"/"+config_params.get("AREA")))
	       {
	    	   DUAPIConnections.put(nodes[i]+"/"+config_params.get("AREA"),conn);
	    	   
	    	
	       }
		 continue;
	}
}
	
	  
	    

	

	

	
	}
	
	
	
	
	
	
	
	
	
	
	

