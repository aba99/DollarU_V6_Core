package com.orsyp.client.stateofde;

import com.orsyp.tools.ps.Connector;
import com.orsyp.tools.ps.DuApiConnection;


public class UpdateLaunchWindowOnOptTask {

	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		String newLw = args[1];
		
		
		Connector conn = new Connector(fileName,false,"",false,"",true,"");
		DuApiConnection duapi = conn.getConnectionList().get(0);
		
		for(String tskKey:duapi.getTaskHashMap_from_outside().keySet())
		{
			
			duapi.updateLWStartTimeOnOptTask(tskKey, newLw);
		}

				
	
	}

}

	

