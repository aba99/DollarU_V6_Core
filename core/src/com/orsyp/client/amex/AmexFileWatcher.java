package com.orsyp.client.amex;
//This filewatcher accounts for the 3 scenarios AMEX runs when a file gets dropped at a specified location
// Scenario 1 : If file shows up, create a CABRIN_<MAINJOB> uproc and launch CABRIN_<MAINJOB>. If successful, trigger <MAINJOB> uproc
// Scenario 2 : If file shows up, create a CARBIN_<MAINJOB> uproc and launch CABRIN_<MAINJOB>. If successful, trigger provoked task on the session <MAINJOB> uproc belongs to
// Scenario 3 : If file shows up, create a CABRIN_<MAINJOB> uproc and launch CABRIN_<MAINJOB>. Do nothing afterwards.
// The condition of CABRIN_<JOB> being triggered uniquely still holds for all 3 scenarios.

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
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

import java.sql.Timestamp;

public class AmexFileWatcher {

	private static HashMap<String,ArrayList<String>> reference = new HashMap<String,ArrayList<String>>();
	//this will have FileName,MainJob,Cabrin_Template_toUse,InWhatSessionIsMyMainUproc
	
	private static ArrayList<ExecutionStatus> array  = new ArrayList<ExecutionStatus>();
	

	public static String currentMu = "AMEX-E0";
	public static String currentUser = "casm_dellc";
	
	
	
	
	public static void main(String[] args) throws Exception  
	{		

		String refFile = args[0];
		String configFile = args[1];
		String path = args[2];
		String sleeptime = args[3];
		
		long sleep = Long.valueOf(Integer.toString((Integer.parseInt(sleeptime)*1000)));
		
		
		System.out.println("Version 30/09/2015");
		array.add(ExecutionStatus.Running);
		array.add(ExecutionStatus.Aborted);
		array.add(ExecutionStatus.Pending);
		array.add(ExecutionStatus.EventWait);
		array.add(ExecutionStatus.Launching);
		
		
		Connector conn = new Connector(configFile,true,"CABRIN_",true,"",true,"PROVOKED_");
		DuApiConnection duapi = conn.getConnectionList().get(0);
		
		
		final File folder = new File(path);

		int loop = 1;
		int spartNumber = 0;
		
		while(true)
		{
			System.out.println();
			System.out.println();
			System.out.println(" LOOP #"+loop);
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			
			reference.clear();
			readReferenceFile(refFile);
			
			HashMap<String,String> currentFiles = getFilesToProcess(folder);//CABRINUPROC_FILENAMETOPROCESS
		
			if(currentFiles!=null && !currentFiles.isEmpty() && currentFiles.keySet()!=null)
			{
			
					for(String cabUprocKey : currentFiles.keySet())
					{
						String cabrinTemplate_toUse="";
		
						if(duapi.getExecutionList(cabUprocKey, array).size()==0)
						{
							if(spartNumber>99999)
							{
								spartNumber=0;
							}
							java.util.Date date3= new java.util.Date();
							Timestamp ts3 = new Timestamp(date3.getTime());
							
							System.out.println(ts3+":  Processing CABRIN_UPROC <"+cabUprocKey+"> with file \""+currentFiles.get(cabUprocKey)+"\"");
							
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
							else
							{
								continue;
							}
							
							
							
							if(!duapi.doesUprocExist(cabUprocKey))
							{
								//System.out.println(cabrinTemplate_toUse);
								try{
								duapi.duplicateUproc(cabrinTemplate_toUse, cabUprocKey, "CABRIN");
								}
								catch (Exception e)
								{
									e.printStackTrace();
									continue;
								}
								duapi.setNonSimulOnUproc(cabUprocKey);
							}
							else
							{	
								java.util.Date date7= new java.util.Date();
								Timestamp ts7 = new Timestamp(date7.getTime());
							
								System.out.println(ts7+": Using an already existing version of CABRIN_UPROC <"+cabUprocKey+">");
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
										
										command=command.replace("-S"+spart, "-S"+spartNumber);//getNumberFromFileName(currentFiles.get(cabUprocKey)));
										spartNumber++;
										
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
								
								command=command.replace("-S"+spart, "-S"+spartNumber);//getNumberFromFileName(currentFiles.get(cabUprocKey)));
								spartNumber++;
								command=command.replace(fpart, currentFiles.get(cabUprocKey));
								varia.get(1).setValue(command);	
								
								varia.get(2).setValue("PROVOKED_"+entries.get(2));
								
								if(!duapi.taskAlreadyExists("PROVOKED_"+entries.get(2)))
								{
									
									duapi.createProvokedTask("PROVOKED_"+entries.get(2), entries.get(2),currentMu,currentUser);
								
									 java.util.Date date4= new java.util.Date();
										Timestamp ts4 = new Timestamp(date4.getTime());
										
										System.out.println(ts4+": Task <"+"PROVOKED_"+entries.get(2)+"> created");
		
								
								}
							}
							else if(varia.size()==2
										&& varia.get(1).getName().equals("COMMAND_PART2") )
							{
		
								String command =varia.get(1).getValue();
								String spart = command.substring(command.indexOf("-S")+2, command.indexOf("-E\"")).trim();
								String fpart = command.substring(command.indexOf("\"XFPATH=/IPland/")+16,command.length()).trim();
								
								command=command.replace("-S"+spart, "-S"+spartNumber);//getNumberFromFileName(currentFiles.get(cabUprocKey)));
								spartNumber++;
								command=command.replace(fpart, currentFiles.get(cabUprocKey));
								varia.get(1).setValue(command);	
									
							}
							
								
								
								currentCabUpr.setVariables(varia);
								currentCabUpr.update();
								java.util.Date date5= new java.util.Date();
								Timestamp ts5 = new Timestamp(date5.getTime());
								
								System.out.println(ts5+" : Updated variables on <"+currentCabUpr.getName()+">");
								System.out.println(ts5+" : Scenario "+scenario+" will be run for this CABRIN_UPROC <"+currentCabUpr.getName()+">");
		
							
							
							duapi.createLaunch(cabUprocKey, currentUser, currentMu);
							
							
						}
						else
						{
							java.util.Date date5= new java.util.Date();
							Timestamp ts5 = new Timestamp(date5.getTime());
							
							/*System.out.println("");
							System.out.println("");
							System.out.println("Total execution list : ");
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
							
							for(int ex=0;ex<duapi.getExecutionList().size();ex++)
							{
								System.out.println(duapi.getExecutionList().get(ex).getUprocName()
										+" -- Numlanc "+duapi.getExecutionList().get(ex).getNumlanc() 
										+" -- Numproc "+duapi.getExecutionList().get(ex).getNumproc());
							}
							
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
							System.out.println("");
							System.out.println("");*/
							
							
							System.out.println(ts5+": CABRIN_UPROC <"+cabUprocKey+"> will not be triggered because of an ABORTED entry");
							
							for(int exec=0;exec<duapi.getExecutionList(cabUprocKey, array).size();exec++)
							{
								
								System.out.println(ts5+": "+duapi.getExecutionList(cabUprocKey, array).get(exec).getUprocName()
										+"-- NumLanc "+duapi.getExecutionList(cabUprocKey, array).get(exec).getNumlanc()
										+" -- NumUproc "+duapi.getExecutionList(cabUprocKey,array).get(exec).getNumproc()
										+" -- "+duapi.getExecutionList(cabUprocKey, array).get(exec).getStatus());
										//+" --"+duapi.getExecution);
							}
							
						}
							
						
					}
			
			}
			
			 try {
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					loop++;
			        Thread.sleep(sleep);
			    } catch(InterruptedException e){
			    	e.printStackTrace();
			    	continue;
			    }
			 	
	}
		
			
		
}
		
		
	public static HashMap<String,String> getFilesToProcess(final File folder) {
		
		java.util.Date date8= new java.util.Date();
    	Timestamp ts8 = new Timestamp(date8.getTime());
		System.out.println();
		System.out.println(ts8+": Checking for trigger files in \""+folder.getPath()+"\"");
		
		
		
		HashMap<String,String> currentFilesToProcess= new HashMap<String,String>();
		
		File testDirectory = new File(folder.getPath());
		File[] files = testDirectory.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".trigger");
		    }
		});
		
	    for (int f=0;f<files.length;f++) {
	       
	        	
	        	
	        	if(EndsWithGxxxxVxxDotTrigger(files[f].getName()))
	            {
	            
		           
		            	String gnum = getGoogleNumber(files[f].getName());
		            	String cabrinjob = getCabrinUprocName(files[f].getName());
		            	
		            	if(cabrinjob!=null)
		            	{
		            	
				            if(currentFilesToProcess.containsKey(cabrinjob))
				            {
				            	int new_gnum =parseWithDefault(gnum,99);
				            	int existing_gnum = parseWithDefault(getGoogleNumber(currentFilesToProcess.get(cabrinjob)),99);
				            	
				            	if(new_gnum<existing_gnum)
				            	{
				            		currentFilesToProcess.put(cabrinjob,files[f].getName());
				            	}//get the file with the lowest googoo number
				            }
				            else
				            {
				            	currentFilesToProcess.put(cabrinjob, files[f].getName());
				            }
				            
		            	}
		            	else
		            	{

		        			java.util.Date date10= new java.util.Date();
			            	Timestamp ts10 = new Timestamp(date10.getTime());
			        		System.out.println(ts10+": No mapping found for \""+files[f].getName()+"\"");
		        		
		            	}
	
		        }
	        	
	        	else if(EndsWithJDxxxDotTrigger(files[f].getName()))
	        	{
		        		String gnum = getJDxxxNumber(files[f].getName());
		        		String cabrinjob = getCabrinUprocName(files[f].getName());
		        		
		        		if(cabrinjob!=null)
		        		{

				            if(currentFilesToProcess.containsKey(cabrinjob))
				            {
				            	int new_gnum =parseWithDefault(gnum,99);
				            	int existing_gnum = parseWithDefault(getJDxxxNumber(currentFilesToProcess.get(cabrinjob)),99);
				            	
				            	if(new_gnum<existing_gnum)
				            	{
				            		currentFilesToProcess.put(cabrinjob,files[f].getName());
				            	}//get the file with the lowest googoo number
				            }
				            else
				            {
				            	currentFilesToProcess.put(cabrinjob, files[f].getName());
				            }
		        		}
		        		else
		        		{
		        			java.util.Date date10= new java.util.Date();
			            	Timestamp ts10 = new Timestamp(date10.getTime());
			        		System.out.println(ts10+": No mapping found for \""+files[f].getName()+"\"");
		        		}
	        		
	        	}
	        	
	        	else if(EndsWithJYYYYDDDDotTrigger(files[f].getName()))
	        	{
		        		String gnum = getJxxxxxxxNumber(files[f].getName());
		        		String cabrinjob = getCabrinUprocName(files[f].getName());
		        		
		        		if(cabrinjob!=null)
		        		{

				            if(currentFilesToProcess.containsKey(cabrinjob))
				            {
				            	int new_gnum =parseWithDefault(gnum,99);
				            	int existing_gnum = parseWithDefault(getJxxxxxxxNumber(currentFilesToProcess.get(cabrinjob)),99);
				            	
				            	if(new_gnum<existing_gnum)
				            	{
				            		currentFilesToProcess.put(cabrinjob,files[f].getName());
				            	}//get the file with the lowest googoo number
				            }
				            else
				            {
				            	currentFilesToProcess.put(cabrinjob, files[f].getName());
				            }
		        		}
		        		else
		        		{

		        			java.util.Date date10= new java.util.Date();
			            	Timestamp ts10 = new Timestamp(date10.getTime());
			        		System.out.println(ts10+": No mapping found for \""+files[f].getName()+"\"");
		        		
		        		}
	        		
	        	}
	        	else if (files[f].getName().endsWith(".TRIGGER"))
	        	{
	        		String cabrinjob = getCabrinUprocName(files[f].getName());
	        		
	        		if(cabrinjob!=null)
	        		{

			          
			            	currentFilesToProcess.put(cabrinjob, files[f].getName());
			            
	        		}
	        		else
	        		{
	        			java.util.Date date10= new java.util.Date();
		            	Timestamp ts10 = new Timestamp(date10.getTime());
		        		System.out.println(ts10+": No mapping found for \""+files[f].getName()+"\"");
	        		}
	        		
	        	}
	        	else
	        	{

        			java.util.Date date10= new java.util.Date();
	            	Timestamp ts10 = new Timestamp(date10.getTime());
	        		System.out.println(ts10+": No mapping found for \""+files[f].getName()+"\"");
        		
	        	}
	        	
	        	
	        }
	    
	    
	    if(currentFilesToProcess.size()!=0)
	    {
	    	java.util.Date date2= new java.util.Date();
	    	Timestamp ts2 = new Timestamp(date2.getTime());
		
	    	System.out.println(ts2+": Files to process listed below :");
			String line=null;
			for(String cabKey:currentFilesToProcess.keySet())
			{
				java.util.Date date6= new java.util.Date();
				Timestamp ts6 = new Timestamp(date6.getTime());
				
				System.out.println(ts6+": CABRIN <"+ cabKey +"> will be processed with file "+currentFilesToProcess.get(cabKey));
				line =(ts6+": CABRIN <"+ cabKey +"> will be processed with file "+currentFilesToProcess.get(cabKey));

			}

			for(int l=0;l<line.length();l++)
			{
				System.out.print("-");
			}
			System.out.println();
			
	    }
	    else
	    {
	    	java.util.Date date2= new java.util.Date();
	    	Timestamp ts2 = new Timestamp(date2.getTime());
	    	System.out.println(ts2+": No files found for this iteration...Looping");
	    }
	    
	    return currentFilesToProcess;
	}
	public static boolean EndsWithGxxxxVxxDotTrigger(String value) {
		// Test start and end characters.
		return Pattern.matches(".*G\\d\\d\\d\\dV\\d\\d.TRIGGER$", value);
	    }
	public static boolean EndsWithGxxxxVDotTrigger(String value) {
		// Test start and end characters.
		return Pattern.matches(".*G\\d\\d\\d\\dV.TRIGGER$", value);
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
		        	
		        	if(line.length>3)
		        	{
		        		String session = line[3].trim();
		        		
		        		if(!session.isEmpty())
		        		{
		        			entries.add(session);
		        		}
		        	}
		        		
		        	reference.put(key, entries);
		        }
		        	
		        		
		        
		    }	
			
			java.util.Date date5= new java.util.Date();
			Timestamp ts5 = new Timestamp(date5.getTime());
			
			System.out.println(ts5+" Reference File \""+fileName+"\" has been read");
			System.out.println(ts5+" Number of entries found in Reference File : "+reference.size());
	}

	public static String getGenericKeyFileName(String filename)
	{//get filename without googlenumber, that is stored as a key in the reference table
		
		//System.out.println("File name "+filename);
		String keyToCheck=null;
		if(EndsWithGxxxxVxxDotTrigger(filename))
		{
			keyToCheck=filename.replaceAll(".G\\d\\d\\d\\dV\\d\\d.TRIGGER$", "");
		}
		else if (EndsWithJDxxxDotTrigger(filename))
		{
			keyToCheck=filename.replaceAll(".JD\\d\\d\\d.TRIGGER$", "");

		}
		else if (EndsWithJYYYYDDDDotTrigger(filename))
		{
			keyToCheck=filename.replaceAll(".J\\d\\d\\d\\d\\d\\d\\d.TRIGGER$", "");

		}
		else if(filename.endsWith(".TRIGGER"))
		{
			keyToCheck=filename.replaceAll(".TRIGGER", "");
		}
			
		
		if(reference.containsKey(keyToCheck))
		{
			return keyToCheck;
		}
		else
		{
			return null;
		}

	}
	
	static int parseWithDefault(String s, int def) {
	    try {
	        return Integer.parseInt(s);
	    }
	    catch (NumberFormatException e) {
	        // It's OK to ignore "e" here because returning a default value is the documented behaviour on invalid input.
	        return def;
	    }
	}
	
}
