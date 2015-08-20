package amex.tools;
//reads the list of JCLs , identifies the variables and the commandtranslation of these variables
//creates uprocs with update script and variables
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.orsyp.UniverseException;
import com.orsyp.api.Variable;
import com.orsyp.api.uproc.Uproc;







import com.orsyp.owls.impl.Connector;

import au.com.bytecode.opencsv.CSVReader;




public class Amex_Converter {

	 private static HashMap<String,String> globalVariablesRepertoire = new HashMap<String,String>();
	 private static List<String> globalVariablesRepertoire_Aa=new ArrayList<String>();

	 private static HashMap<String,String> JCL_listOfVariables = new HashMap<String,String>();//contains all JCL with variables
	 private static HashMap<String,ArrayList<String>> JCL_arrayListOfVariables = new HashMap<String,ArrayList<String>>();
	 private static HashMap<String,ArrayList<String>> variable_ListOfCommands  = new HashMap<String,ArrayList<String>>();
	 
	 private static HashMap<String,String> all_JCL = new HashMap<String,String>();//all Jcls (with and without variables)
	 private static String template_Uproc = "AMEX_TEMPLATE";
	 
	 
		
	public static void main(String argv[]) throws IOException {
			
			String configFile = argv[0];
			String glbVariablesFile = argv[1];
			String variableCmdFile=argv[2];
			String workPath = argv[3];
			

			Connector myNode = new Connector(configFile,true,"",false,"",false,"");

			final File workPathFolder = new File(workPath);
			
			readGlobalVariablesFile(glbVariablesFile);
			readJCLFiles(workPathFolder);
			readVariableCommandFile(variableCmdFile);
			
			
			for(String key:all_JCL.keySet())
			{
				try {
					myNode.getConnectionList().get(0).duplicateUproc(template_Uproc,key);
					
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//get the uprocs ready
			
	
			
			
			try {
				
				//Connector myNode_2 = new Connector(configFile,true,"",false,"",false,"");

				HashMap<String,Uproc> uprocs = myNode.getConnectionList().get(0).getUprocHashMap_from_outside();

				for(String uprKey:all_JCL.keySet())
				{
					//System.out.println(uprKey);
					modifyVariables(uprocs.get(uprKey));
					updateUprocScript(uprocs.get(uprKey),myNode);
				}
			
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
			
		
			
	}
		
			
	private static void readGlobalVariablesFile(String csvFile){
		try{
		@SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(csvFile),',', '\"', '\0');
		
		 
		String [] line;
		int counter=0;
		//parse lines
	    while ((line = reader.readNext()) != null) 
	    {
	    	
	    	
	     	if (line[0].contains("%%SET")&& line[0].contains("="))
	    	{
	    		String index = line[0].substring(line[0].indexOf("%%SET")+5, line[0].indexOf("="));
	    		index=index.trim();
	    		
	    		if(!globalVariablesRepertoire.containsKey(index))
	    		{
	    			globalVariablesRepertoire.put(index, index);
	    			counter++;
	    		}
	    		
	    	}
	    }

	    globalVariablesRepertoire_Aa.addAll(globalVariablesRepertoire.keySet());
	    
	    
        Collections.sort(globalVariablesRepertoire_Aa, new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                if(s1.length() < s2.length()) {
                    return 1;
                }
                else if(s1.length() > s2.length()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });

        
		System.out.println("Total of "+counter+" global variables have been stored from \""+csvFile+"\"");
		   
	
	}
	catch (Exception e)
	{
		e.printStackTrace(System.out);
		System.out.println("Reading Global Variables file error !");
		System.exit(1);
	}
}

	private static void readJCLFile(File jclFile){
		
		System.out.println();
		System.out.println("Scanning file \""+jclFile.getPath()+"\" :");
		 
		String var="";
		
		try (BufferedReader br = new BufferedReader(new FileReader(jclFile.getPath()))) {
		    
			String line;
		    
		    while ((line = br.readLine()) != null) {
		    	
		    	String temp = pickupVariableFromLine(line,jclFile);
		    	if(!var.contains(temp))//make sure the variables are not duplicated
		    	{
		    		var+=temp;
		    	
		    	}
		    }	  
	     	
	    if(!var.isEmpty())
	    {
	    	if(!JCL_listOfVariables.containsKey(jclFile.getName()))
	    	{
	    		JCL_listOfVariables.put(jclFile.getName(), var.substring(0, var.length()-1));
	    	}
	    }
	    
	    System.out.println("Variables found \""+var+"\"");
		System.out.println();

	    
	}
	catch (Exception e)
	{
		System.out.println("Reading JCL file error !");
		System.exit(1);
	}
}
	
			
	public static void readJCLFiles(final File folder) {
			    for (final File fileEntry : folder.listFiles()) {
			        if (fileEntry.isDirectory()) {
			            readJCLFiles(fileEntry);
			        } else {
			            if(!all_JCL.containsKey(fileEntry.getName()))
			            {
			            	all_JCL.put(fileEntry.getName(), fileEntry.getName());
			            }
			            readJCLFile(fileEntry);
			        }
			    }
			}
	
	public  static void modifyVariables(Uproc upr) throws UniverseException 
	{
		System.out.println();
		System.out.println("Modifying Variables List on Uproc "+upr.getName());
		Vector<Variable> varia=upr.getVariables();
		
		if(varia.size()==2 && varia.get(1).getName().equals("COMMAND_PART2") )
		{	

			
				String command =varia.get(1).getValue();
				
				if(JCL_listOfVariables.containsKey(upr.getName()))
				{
					command=command
							+" "+upr.getName()+" -S 00 -E\""
							+JCL_listOfVariables.get(upr.getName())
							+"\" -H 9999";
				}
				else
				{
					if(all_JCL.containsKey(upr.getName()))
					{
						command=command
								+" "+upr.getName()+" -H 9999";
					}
							
				}
			
			varia.get(1).setValue(command);	
			upr.setVariables(varia);	
			upr.update();
		
		}
	
	}

	public static String pickupVariableFromLine(String line,File jclFile)
	{
		String var="";

	 	for(String key:globalVariablesRepertoire_Aa)
		{
			if(line.contains(key))
			{
				String holder=key.replace("%%","");
				
				if(JCL_arrayListOfVariables.containsKey(jclFile.getName()))
				{
					if(!JCL_arrayListOfVariables.get(jclFile.getName()).contains(holder))//to make sure no duplicated variables
					{
						JCL_arrayListOfVariables.get(jclFile.getName()).add(holder);
					}
				}
				else
				{
					ArrayList<String> list = new ArrayList<String>();
					list.add(holder);
					JCL_arrayListOfVariables.put(jclFile.getName(),list );
				}
				
				var+=holder+"=\\$"+holder+",";
				line=line.replace(key, "");
			}
			
		}
	 	
		
	 	return var;
	}

	public static void updateUprocScript(Uproc upr,Connector myNode) throws UniverseException
	{
		System.out.println("Updating Script on Uproc "+upr.getName());
		System.out.println();
		
		if(myNode.getConnectionList().get(0).uprocAlreadyExists(upr.getName())
				&&upr.getType().equalsIgnoreCase("CL_INT"))
				//&&JCL_arrayListOfVariables.containsKey(upr.getName()))
		{
			String[] currentScriptLines =myNode.getConnectionList().get(0).extractInternalScript(upr);
			
			for(int j=0;j<currentScriptLines.length;j++)
			{
				if(currentScriptLines[j].contains("CA2JDK4B"))
				{
					currentScriptLines[j]=currentScriptLines[j].replace("CA2JDK4B", upr.getName());
					
				}
								
				if(currentScriptLines[j].contains("eval ${COMMAND_PART1}${COMMAND_PART2}"))
				{
					if(JCL_arrayListOfVariables.containsKey(upr.getName()))
					{
							
						ArrayList<String> listOfVariablesForThisJCL = JCL_arrayListOfVariables.get(upr.getName());
						String toInclude = "";
						
						for(int y=0;y<listOfVariablesForThisJCL.size();y++)
						{
							if(variable_ListOfCommands.containsKey(listOfVariablesForThisJCL.get(y)))
							{
								for(int z=0;z<variable_ListOfCommands.get(listOfVariablesForThisJCL.get(y)).size();z++)
								{
									toInclude+="\n"+variable_ListOfCommands.get(listOfVariablesForThisJCL.get(y)).get(z);
								}
							}
						}
						currentScriptLines[j]=currentScriptLines[j].replace("eval ${COMMAND_PART1}${COMMAND_PART2}", toInclude+"\neval ${COMMAND_PART1}${COMMAND_PART2}");
					}	
				
				}
				
			}
			
				myNode.getConnectionList().get(0).createInternalScript(upr,currentScriptLines);
			
			
			
		}
		
	}
	
	private static void readVariableCommandFile(String variableCommandFile){
		
		System.out.println();
		System.out.println("Scanning file \""+variableCommandFile+"\" :");
		 
		
		try (BufferedReader br = new BufferedReader(new FileReader(variableCommandFile))) {
		    
			String line;
		    String variable="";
		    ArrayList<String>cmd_lines=new ArrayList<String>();
		    
		    
		    while ((line = br.readLine()) != null) {
		    	if(line.startsWith("#"))
		    	{
		    		variable=line.replace("#", "").trim();
		    		line=br.readLine();
		    	}
		    	
		    	while(((line)!=null) && !line.startsWith("#") && !line.trim().isEmpty())
		    	{
		    		cmd_lines.add(line);
		    		line=br.readLine();
		    	}
		    	
		    	if(!variable_ListOfCommands.containsKey(variable))
		    	{
		    		variable_ListOfCommands.put(variable, new ArrayList<String>(cmd_lines));
		    		
		    	}
		    	cmd_lines.clear();
		    	
		    }	  
	     	
	  
	    
	}
	catch (Exception e)
	{
		System.out.println("Reading Command Variable file error !");
		System.exit(1);
	}
			

	}
}
			
			