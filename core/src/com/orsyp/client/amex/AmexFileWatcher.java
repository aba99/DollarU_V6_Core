package com.orsyp.client.amex;
//This filewatcher accounts for the 3 scenarios AMEX runs when a file gets dropped at a specified location
// Scenario 1 : If file shows up, create a CABRIN_<MAINJOB> uproc and launch CABRIN_<MAINJOB>. If successful, trigger <MAINJOB> uproc
// Scenario 2 : If file shows up, create a CARBIN_<MAINJOB> uproc and launch CABRIN_<MAINJOB>. If successful, trigger provoked task on the session <MAINJOB> uproc belongs to
// Scenario 3 : If file shows up, create a CABRIN_<MAINJOB> uproc and launch CABRIN_<MAINJOB>. Do nothing afterwards.
// The condition of CABRIN_<JOB> being triggered uniquely still holds for all 3 scenarios.
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.Variable;
import com.orsyp.api.execution.ExecutionStatus;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.tools.ps.Connector;
import com.orsyp.tools.ps.DuApiConnection;


public class AmexFileWatcher {

	private static HashMap<String,ArrayList<String>> reference = new HashMap<String,ArrayList<String>>();
	//this will have FileName,MainJob,Cabrin_Template_toUse,InWhatSessionIsMyMainUproc
	private static ArrayList<ExecutionStatus> array  = new ArrayList<ExecutionStatus>();
	
	public static String cabrin_template_scenario_1 = "CABRIN_TEMPLATE";
	public static String cabrin_template_scenario_2 = "CABRIN_TEMPLATE";
	public static String cabrin_template_scenario_3 = "CABRIN_TEMPLATE";

	public static String currentMu = "AMEX-E0";
	public static String currentUser = "casm_dellc";
	
	public static void main(String[] args) throws Exception  
	{		

		String refFile = args[0];
		String configFile = args[1];
		String path = args[2];
		
		
		readReferenceFile(refFile);
		array.add(ExecutionStatus.Running);
		array.add(ExecutionStatus.Aborted);
		array.add(ExecutionStatus.Pending);
		array.add(ExecutionStatus.EventWait);
		
		Connector conn = new Connector(configFile,true,"CABRIN_",true,"",true,"PROVOKED_");
		DuApiConnection duapi = conn.getConnectionList().get(0);
		
		System.out.println("Scanning following path '"+path+"'");
		final File folder = new File(path);
		int count =1;
		
		while(true)
		{
			HashMap<String,String> currentFiles = getFilesToProcess(folder);//CABRINUPROC_FILENAMETOPROCESS
		
			
			
			for(String cabUprocKey : currentFiles.keySet())
			{
				
				String cabrinTemplate_toUse=cabrin_template_scenario_1;

				if(duapi.getExecutionList(cabUprocKey, array).size()==0)
				{
					System.out.println(count+" - Processing CABRIN_UPROC <"+cabUprocKey+"> with file \""+currentFiles.get(cabUprocKey)+"\"");
					
					int scenario=1;
					String fileEntryKeyForRef = getGenericKeyFileName(currentFiles.get(cabUprocKey));
					ArrayList<String> entries = new ArrayList<String>();
					
					if(fileEntryKeyForRef!=null)
					{
						entries = reference.get(fileEntryKeyForRef);
						//entries here should look like : "CABRIN_<MAINJOB>","CABRIN_TEMPLATE_SCENARIO1","SESSIONNAME"
						//or "CABRIN_<MAINJOB>","CABRIN_TEMPLATE_SCENARIO1"
						if(entries.size()==3)
						{
							scenario=2;
						}
						
						cabrinTemplate_toUse=entries.get(1);
						
					}
					
					
					count++;
					
					if(!duapi.doesUprocExist(cabUprocKey))
					{
						System.out.println(cabrinTemplate_toUse);
						duapi.duplicateUproc(cabrinTemplate_toUse, cabUprocKey, "CABRIN");
						duapi.setNonSimulOnUproc(cabUprocKey);
					}
					
						Uproc currentCabUpr = duapi.getUproc(cabUprocKey);
						Vector<Variable> varia = currentCabUpr.getVariables();
						
					if(scenario==1 && varia.size()==3 
								&& varia.get(1).getName().equals("COMMAND_PART2") 
						   		&& varia.get(2).getName().equals("MAIN_JOB_TRIGGER"))
					{	
								
			
								String command =varia.get(1).getValue();
								String spart = command.substring(command.indexOf("-S")+2, command.indexOf("-E\"")).trim();
								String fpart = command.substring(command.indexOf("\"XFPATH=/IPland/")+16,command.length()).trim();
								
								command=command.replace("-S"+spart, "-S"+getNumberFromFileName(currentFiles.get(cabUprocKey)));
								command=command.replace(fpart, currentFiles.get(cabUprocKey));
								varia.get(1).setValue(command);	
							
								
								String main_job_trigger = varia.get(2).getValue();								
								String jpart=main_job_trigger.substring(main_job_trigger.indexOf("upr=")+4, main_job_trigger.indexOf("user=")).trim();
								
								main_job_trigger=main_job_trigger.replace(jpart, cabUprocKey.replace("CABRIN_",""));
								varia.get(2).setValue(main_job_trigger);
								//System.out.println(" main_job_trigger "+main_job_trigger);
					}
					else if(scenario==2 && varia.size()==3 
								&& varia.get(1).getName().equals("COMMAND_PART2") 
						   		&& varia.get(2).getName().equals("PROVOKED_TASK_NAME"))
					{
						
						String command =varia.get(1).getValue();
						String spart = command.substring(command.indexOf("-S")+2, command.indexOf("-E\"")).trim();
						String fpart = command.substring(command.indexOf("\"XFPATH=/IPland/")+16,command.length()).trim();
						
						command=command.replace("-S"+spart, "-S"+getNumberFromFileName(currentFiles.get(cabUprocKey)));
						command=command.replace(fpart, currentFiles.get(cabUprocKey));
						varia.get(1).setValue(command);	
						
						varia.get(2).setValue("PROVOKED_"+entries.get(2));
						
						if(!duapi.taskAlreadyExists("PROVOKED_"+entries.get(2)))
						{
							duapi.createProvokedTask("PROVOKED_"+entries.get(2), entries.get(2),currentMu,currentUser);
						}
					}
					else if(varia.size()==2
								&& varia.get(1).getName().equals("COMMAND_PART2") )
					{

						String command =varia.get(1).getValue();
						String spart = command.substring(command.indexOf("-S")+2, command.indexOf("-E\"")).trim();
						String fpart = command.substring(command.indexOf("\"XFPATH=/IPland/")+16,command.length()).trim();
						
						command=command.replace("-S"+spart, "-S"+getNumberFromFileName(currentFiles.get(cabUprocKey)));
						command=command.replace(fpart, currentFiles.get(cabUprocKey));
						varia.get(1).setValue(command);	
							
					}
					
						
						
						currentCabUpr.setVariables(varia);
						currentCabUpr.update();
						
						//System.out.println("yoooo-"+currentCabUpr.getVariables().get(2).getValue());
					
					
					
					duapi.createLaunch(cabUprocKey, currentUser, currentMu);
					
					/*File f = new File(path+"\\"+currentFiles.get(cabUprocKey));
					f.delete();*/
				}
					
				
			}

			
			 try {
			        Thread.sleep(1000l);
			    } catch(InterruptedException e){}
			 	
	}
		
			
		
}
		
		
	public static HashMap<String,String> getFilesToProcess(final File folder) {
		
		HashMap<String,String> currentFilesToProcess= new HashMap<String,String>();
		
		
	    for (final File fileEntry : folder.listFiles()) {
	        
	    	if (fileEntry.isDirectory()) 
	        {
	            getFilesToProcess(fileEntry);
	        }
	        else 
	        {
	        	
	        	
	        	if(EndsWithGxxxxVxxDotTrigger(fileEntry.getName()))
	            {
	            
		           
		            	String gnum = getGoogleNumber(fileEntry.getName());
		            	String cabrinjob = getCabrinUprocName(fileEntry.getName());
		            	
		            	if(cabrinjob!=null)
		            	{
		            	
				            if(currentFilesToProcess.containsKey(cabrinjob))
				            {
				            	int new_gnum =Integer.parseInt(gnum);
				            	int existing_gnum = Integer.parseInt(getGoogleNumber(currentFilesToProcess.get(cabrinjob)));
				            	
				            	if(new_gnum<existing_gnum)
				            	{
				            		currentFilesToProcess.put(cabrinjob,fileEntry.getName());
				            	}//get the file with the lowest googoo number
				            }
				            else
				            {
				            	currentFilesToProcess.put(cabrinjob, fileEntry.getName());
				            }
				            
		            	}
	
		        }
	        	
	        	else if(EndsWithJDxxxDotTrigger(fileEntry.getName()))
	        	{
		        		String gnum = getJDxxxNumber(fileEntry.getName());
		        		String cabrinjob = getCabrinUprocName(fileEntry.getName());
		        		
		        		if(cabrinjob!=null)
		        		{

				            if(currentFilesToProcess.containsKey(cabrinjob))
				            {
				            	int new_gnum =Integer.parseInt(gnum);
				            	int existing_gnum = Integer.parseInt(getJDxxxNumber(currentFilesToProcess.get(cabrinjob)));
				            	
				            	if(new_gnum<existing_gnum)
				            	{
				            		currentFilesToProcess.put(cabrinjob,fileEntry.getName());
				            	}//get the file with the lowest googoo number
				            }
				            else
				            {
				            	currentFilesToProcess.put(cabrinjob, fileEntry.getName());
				            }
		        		}
	        		
	        	}
	        	
	        	else if(EndsWithJYYYYDDDDotTrigger(fileEntry.getName()))
	        	{
		        		String gnum = getJxxxxxxxNumber(fileEntry.getName());
		        		String cabrinjob = getCabrinUprocName(fileEntry.getName());
		        		
		        		if(cabrinjob!=null)
		        		{

				            if(currentFilesToProcess.containsKey(cabrinjob))
				            {
				            	int new_gnum =Integer.parseInt(gnum);
				            	int existing_gnum = Integer.parseInt(getJxxxxxxxNumber(currentFilesToProcess.get(cabrinjob)));
				            	
				            	if(new_gnum<existing_gnum)
				            	{
				            		currentFilesToProcess.put(cabrinjob,fileEntry.getName());
				            	}//get the file with the lowest googoo number
				            }
				            else
				            {
				            	currentFilesToProcess.put(cabrinjob, fileEntry.getName());
				            }
		        		}
	        		
	        	}
	        	else if (fileEntry.getName().endsWith(".TRIGGER"))
	        	{
	        		String cabrinjob = getCabrinUprocName(fileEntry.getName());
	        		
	        		if(cabrinjob!=null)
	        		{

			          
			            	currentFilesToProcess.put(cabrinjob, fileEntry.getName());
			            
	        		}
	        		
	        	}
	        	
	        	
	        }
	    }
	    
	    return currentFilesToProcess;
	}
	public static boolean EndsWithGxxxxVxxDotTrigger(String value) {
		// Test start and end characters.
		return Pattern.matches(".*G\\d\\d\\d\\dV\\d\\d.TRIGGER$", value);
	    }
	public static boolean EndsWithJDxxxDotTrigger(String value)
	{
		return Pattern.matches(".*JD\\d\\d\\d.TRIGGER$", value);
	}
	public static boolean EndsWithJYYYYDDDDotTrigger(String value)
	{
		return Pattern.matches(".*J\\d\\d\\d\\d\\d\\d\\d.TRIGGER$", value);
	}
	
	public static String getGoogleNumber(String fullFilename)
	{// *.G0000V00.TRIGGER needs to be parsed
		fullFilename=fullFilename.replace(".TRIGGER","");
		int length= fullFilename.length();
		
		
		fullFilename=fullFilename.substring(length-8, length);
		return fullFilename.substring(3, 5);
		
	}
	public static String getJDxxxNumber(String fullFilename)
	{
		fullFilename=fullFilename.replace(".TRIGGER","");
		int length= fullFilename.length();
		
		
		fullFilename=fullFilename.substring(length-3, length);
		
		return fullFilename;
	}
	public static String getJxxxxxxxNumber(String fullFilename)
	{
		fullFilename=fullFilename.replace(".TRIGGER","");
		int length= fullFilename.length();
		fullFilename=fullFilename.substring(length-7, length);
		return fullFilename;
	}
	public static String getCabrinUprocName(String fullFilename)
	{
		if(getGenericKeyFileName(fullFilename) != null)
		{
			return reference.get(getGenericKeyFileName(fullFilename)).get(0);
		}
		
		return null;
	}
	public static String getNumberFromFileName(String fileName)
	{
		if(EndsWithGxxxxVxxDotTrigger(fileName))
		{
			return getGoogleNumber(fileName);
		}
		
		fileName=fileName.replace(".TRIGGER", "");
		int l = fileName.length();
		
		if(l>2 )
		{
			if(Pattern.matches("\\d\\d",fileName.substring(l-2, l)))
			{
				return fileName.substring(l-2, l);
			}
			else
				return "99";
		}
		else
		{
			return "99";
		}
		
	}
	
	public static void readReferenceFile(String fileName) throws IOException
	{
		 @SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(fileName),',', '\"', '\0');
			
			String [] line;			

			//parse lines
			while ((line = reader.readNext()) != null) 
		    {	    	
				
		        if(line.length>2)
		        {
		        	String key=line[0].trim();
		        	String value=line[1].trim();
		        	String template=line[2].trim();
		        	ArrayList<String> entries = new ArrayList<String>();
		        	entries.add("CABRIN_"+value.toUpperCase());
		        	entries.add(template);
		        	//System.out.println(template);
		        	
		        	if(line.length>3)
		        	{
		        		String session = line[3].trim();
		        		entries.add(session);
		        	}
		        		
		        	reference.put(key, entries);
		        }
		        	
		        		
		        
		    }	
	}

	public static String getGenericKeyFileName(String filename)
	{//get filename without googlenumber, that is stored as a key in the reference table
		for(String key:reference.keySet())
		{
			if(filename.contains(key))
			{
				return key;
			}
		}
		
		return null;
	}
	
}
