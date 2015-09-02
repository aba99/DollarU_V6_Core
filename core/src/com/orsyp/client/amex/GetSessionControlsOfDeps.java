package com.orsyp.client.amex;

//this code reads through all uprocs and identifies the 3 types of session controls on DepCons. It outputs everything into
// a hash map that will contain UPR|DEPUPR in an arrayList. 3 types of arraylists (ANY,SAME,SAME_EXEC) .
// Good luck. Trust yourself, you thought it out properly.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.SessionControl;
import com.orsyp.tools.ps.Connector;



public class GetSessionControlsOfDeps {

	public static void main(String[] args) throws Exception {

		String configFile = args[0];
		
		
		Connector conn = new Connector(configFile,true,"",false,"",false,"");
	
		HashMap<String,ArrayList<String>> output= new HashMap<String,ArrayList<String>>();
		ArrayList<String> any = new ArrayList<String>();
		ArrayList<String> same = new ArrayList<String>();
		ArrayList<String> same_exec = new ArrayList<String>();
		
		output.put("ANY", any);
		output.put("SAME",same);
		output.put("SAME_EXEC", same_exec);
		
		for(String uprKey:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			
			Vector<DependencyCondition> curDeps = conn.getConnectionList().get(0).getUprocHashMap_from_outside().get(uprKey).getDependencyConditions();
			
			for(int d=0;d<curDeps.size();d++)
			{
				if(curDeps.get(d).getSessionControl().getType().equals(SessionControl.Type.SAME_SESSION))
				{
					output.get("SAME").add(uprKey+"|"+curDeps.get(d).getUproc());
				}
				else if(curDeps.get(d).getSessionControl().getType().equals(SessionControl.Type.ANY_SESSION))
				{
					output.get("ANY").add(uprKey+"|"+curDeps.get(d).getUproc());

				}
				else if(curDeps.get(d).getSessionControl().getType().equals(SessionControl.Type.SAME_SESSION_AND_EXECUTION))
				{
					output.get("SAME_EXEC").add(uprKey+"|"+curDeps.get(d).getUproc());

				}
				


			}
			
			
			
		}
		
		for(String key:output.keySet())
		{
			ArrayList<String> curList = output.get(key);
			
			System.out.println(key);
			System.out.println("---------");
			for(int j=0;j<curList.size();j++)
			{
				System.out.println("-"+curList.get(j));
				
			}
			System.out.println();
		}
	}	
		
	

}