package com.orsyp.client.stateofde;


import com.orsyp.tools.ps.Connector;


public class RemoveDuplicatedConFromUpr {

	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		
		
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		
		
		for(String uprocKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			conn.getConnectionList().get(0).removeDuplicatedDepCons(uprocKey);
		}

				
	
	}

}

	

