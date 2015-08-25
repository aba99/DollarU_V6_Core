package com.orsyp.client.amex;

import java.util.Vector;

import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.SessionControl;
import com.orsyp.tools.ps.Connector;



public class CopyOfTest {

	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
	
		

		for(String uprKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			
			Vector<DependencyCondition> curDeps = conn.getConnectionList().get(0).getUprocHashMap_from_outside().get(uprKey).getDependencyConditions();
			
			for(int d=0;d<curDeps.size();d++)
			{
			}
			
			
			
		}
	}	
		
	

}
