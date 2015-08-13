package com.orsyp.client.amex;

import java.util.HashMap;

import com.orsyp.tools.ps.Connector;



public class RunTest {

	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
	
		HashMap<String,String>test=new HashMap<String,String>();
		test.put("AMEX_TEMPLATE", "AMEX_TEMPLATE");

		//for(String uprKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			
			//conn.getConnectionList().get(0).setSessionControlOnUproc(uprKey);
			
			conn.getConnectionList().get(0).removeDepsFromUproc("UPR_3", test);
			
		}
	}	
		
	

}
