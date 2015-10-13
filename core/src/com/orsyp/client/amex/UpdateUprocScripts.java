package com.orsyp.client.amex;

import com.orsyp.tools.ps.Connector;
import com.orsyp.tools.ps.DuApiConnection;

public class UpdateUprocScripts {

	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		//String stringa = args[1];
		//String stringb = args[2];
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		DuApiConnection duapi = conn.getConnectionList().get(0);
		
		for(String uKey:duapi.getUprocHashMap_from_outside().keySet())
		{
			try{
			duapi.updateScript(duapi.getUprocHashMap_from_outside().get(uKey)
					,"\"-2m\"" 
					,"\"-2d\"" 

					);
			}catch(Exception e)
			{
				System.out.println("Error updating script on "+uKey);
				continue;
			}
			
			/*try{
			duapi.updateScript(duapi.getUprocHashMap_from_outside().get(uKey)
					,"P1YYYY=`$UNI_DIR_EXEC/uxdat \"yyyymmdd\" $S_DATRAIT \"yyyy\" \"+1y\"`"
					,"P1YYYY=`$UNI_DIR_EXEC/uxdat \"yyyymmdd\" $S_DATRAIT \"yyyy\" \"+1d\"`");
			}catch (Exception e)
			{
				System.out.println("Error updating script on"+uKey);
				continue;
			}*/
		}
		

	}

}
