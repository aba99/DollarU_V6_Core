package com.orsyp.client.stateofde;


import java.util.ArrayList;

import com.orsyp.tools.ps.Connector;
import com.orsyp.tools.ps.DuApiConnection;


public class RemoveFatherUprocFromDepListOnUprocs {

	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		
		
		
		Connector conn = new Connector(fileName,true,"",true,"",false,"");
		DuApiConnection duapi = conn.getConnectionList().get(0);
		
		for(String sesKey:duapi.getSessionsHashMap_from_outside().keySet())
		{
			String [] uprocs_in_session= duapi.getSessionsHashMap_from_outside().get(sesKey).getUprocs();
			
			for(int u=0;u<uprocs_in_session.length;u++)
			{
				ArrayList<String>clist = duapi.getChildrenUproc(sesKey, uprocs_in_session[u]);
				
				for(int c=0;c<clist.size();c++)
				{
					ArrayList<String> toBeRemoved = new ArrayList<String>();
					toBeRemoved.add(uprocs_in_session[u]);
					
					duapi.removeDepsFromUproc(clist.get(c), toBeRemoved);
				}
				
			}
		}

				
	
	}

}

	

