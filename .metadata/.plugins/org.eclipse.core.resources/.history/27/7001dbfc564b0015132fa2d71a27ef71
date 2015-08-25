package amex.tools;
//reads a list of  bracket named uprocs such as "UPROC (1)"from a list
//checks for "UPROC" on the node
//if found duplicates "UPROC" into "UPROC (1)"
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


import com.orsyp.UniverseException;
import com.orsyp.owls.impl.Connector;

public class BracketUprocs {

	 private static HashMap<String,String> All_UPR = new HashMap<String,String>();


	public static void main(String argv[]) throws IOException, UniverseException {
		
		String configFile=argv[0];
		String inputFile = argv[1];

		final File workPathFolder = new File(inputFile);
		
		readInputFile(workPathFolder);
		
		Connector myNode = new Connector(configFile,true,"",false,"",false,"");

		
		for(String key:All_UPR.keySet())
		{
			String ori_name=key;
			String lineWithoutSpaces = key.replaceAll("\\s+","");			
	
			lineWithoutSpaces = lineWithoutSpaces.replaceAll("\\(.*\\)", "");
				
				if(myNode.getConnectionList().get(0).uprocAlreadyExists(lineWithoutSpaces))
				{
					try {
						myNode.getConnectionList().get(0).duplicateUproc(lineWithoutSpaces,ori_name);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				else
				{
					System.out.println("Uproc <"+lineWithoutSpaces+"> does not exist on "+myNode.getConnectionList().get(0).getConnName());
				}
			}
		
	
	}
	
	
	
	private static void readInputFile(File jclFile){
		
		System.out.println();
		System.out.println("Scanning file \""+jclFile.getPath()+"\" :");
		 
		
		try (BufferedReader br = new BufferedReader(new FileReader(jclFile.getPath()))) {
		    
			String line;
		    
		    while ((line = br.readLine()) != null) {
		    	
		    	All_UPR.put(line.trim(),line.trim());
		    }	  
	     	
	
	    
	    System.out.println("Uprocs found \""+All_UPR.size()+"\"");
		System.out.println();

	    
	}
	catch (Exception e)
	{
		System.out.println("Reading Input file error !");
		System.exit(1);
	}
}



}
