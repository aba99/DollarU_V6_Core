package com.orsyp.client.amex;



public class CopyOfTest {

	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		
		
		Connector conn = new Connector(fileName,true,"",true,"",true,"");
	

		for(String uprKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			
			conn.getConnectionList().get(0).setSessionControlOnUproc(uprKey);
			
			
			
		}
	}	
		
	

}
