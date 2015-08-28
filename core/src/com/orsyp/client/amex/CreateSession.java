package com.orsyp.client.amex;


import java.util.ArrayList;
import java.util.HashMap;

import com.orsyp.tools.ps.Connector;
import com.orsyp.tools.ps.DuApiConnection;
import com.orsyp.tools.ps.InMemoryFile;

public class CreateSession {

	
	
	public static void main(String[] args) throws Exception {
		
		String configFile = args[0];
		String csvFile = args[1];
		
		Connector conn = new Connector(configFile,true,"",true,"",false,"");
		DuApiConnection duapi = conn.getConnectionList().get(0);
		InMemoryFile csv = new InMemoryFile(csvFile);
		csv.store();
		
		
		for(String sesNameKey:csv.getHash_Store().keySet())
		{
			ArrayList<String>upr1_upr2 = csv.getHash_Store().get(sesNameKey);
			String father = upr1_upr2.get(0);
			upr1_upr2.remove(0);
			HashMap<String,ArrayList<String>> input = new HashMap<String,ArrayList<String>>();
			input.put(father, upr1_upr2);
			duapi.createSession(sesNameKey,input);

			
		}

	}

}
