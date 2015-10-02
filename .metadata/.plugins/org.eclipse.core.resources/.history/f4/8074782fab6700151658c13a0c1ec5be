package com.orsyp.client.amex;


import java.util.ArrayList;
import java.util.HashMap;

import com.orsyp.tools.ps.Connector;



public class OrConditionalDep {

	static HashMap<String,ArrayList<String>> table = new HashMap<String,ArrayList<String>>();
	
	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];

		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		
		conn.getConnectionList().get(0).orDepWithThisDep("SD99M802", "SD99M020", "TID071");
	
	}

}

	

