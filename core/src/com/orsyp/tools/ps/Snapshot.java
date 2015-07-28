package com.orsyp.tools.ps;

import java.util.ArrayList;

public class Snapshot {

	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		
		
		
		Connector conn = new Connector(fileName,true,"",true,"",true,"");

		ArrayList<Job> list = new ArrayList<Job>(conn.getConnectionList().get(0).uprocsToJob());	
	
		for(int i=0;i<list.size();i++)
		{
			list.get(i).print(System.out);
		}
		
		
	
	}
	

}
