package com.orsyp.client.amex;


import com.orsyp.tools.ps.Connector;
import com.orsyp.tools.ps.InMemoryFile;


public class AddResCondition {

	public static void main(String[] args) throws Exception {
		String fileName = args[0];
		String csvFile = args[1];
		String csvFile2= args[2];
		
		InMemoryFile csvF = new InMemoryFile(csvFile);
		csvF.store();
		InMemoryFile csvF2 = new InMemoryFile(csvFile2);
		csvF2.store();
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		
		
		
	for(String uprKey:csvF.getHash_Store().keySet())
		{
			if(conn.getConnectionList().get(0).getUprocHashMap_from_outside().containsKey(uprKey))
			{
				if(csvF2.getHash_Store().containsKey(csvF.getHash_Store().get(uprKey).get(0)))
				{
					conn.getConnectionList().get(0).addResourceConditionToUproc
					(uprKey, 
							csvF2.getHash_Store().get(csvF.getHash_Store().get(uprKey).get(0)).get(0));			
				}
			}
			
			
		}
	for(String upr:conn.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
	{
		if(!csvF.getHash_Store().keySet().contains(upr))
		{
			conn.getConnectionList().get(0).getUprocHashMap_from_outside().get(upr).delete();
		}
	}
}

}