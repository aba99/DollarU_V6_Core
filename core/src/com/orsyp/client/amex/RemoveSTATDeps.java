package com.orsyp.client.amex;



import com.orsyp.tools.ps.Connector;
import com.orsyp.tools.ps.InMemoryFile;



public class RemoveSTATDeps {

	static int count = 1;
	
	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		String csvFile = args[1];
		
		InMemoryFile csvF = new InMemoryFile(csvFile);
		csvF.store();
		
		Connector conn = new Connector(fileName,true,"",false,"",false,"");
		for(String uprKey:csvF.getHash_Store().keySet())
		{
			if(conn.getConnectionList().get(0).getUprocHashMap_from_outside().containsKey(uprKey))
			//conn.getConnectionList().get(0).setSessionControlOnUproc(uprKey);
			{
				
					//System.out.println(uprKey+" will have stat dependencies removed "+csvF.getHash_Store().get(uprKey).get(0));
				conn.getConnectionList().get(0).removeDepsFromUproc(uprKey,csvF.getHash_Store().get(uprKey) );
			}
			else
			{
				//System.out.println(uprKey+" does not exist on node");
			}
			
		}
	}	
		

}

	

