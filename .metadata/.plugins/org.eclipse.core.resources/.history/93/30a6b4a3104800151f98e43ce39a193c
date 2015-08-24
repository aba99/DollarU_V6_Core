package com.orsyp.client.amex;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.orsyp.api.Variable;
import com.orsyp.api.execution.ExecutionStatus;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.tools.ps.Connector;

import au.com.bytecode.opencsv.CSVReader;


public class FileWatcherDispatcher {

	public static HashMap<String,ArrayList<String>> reference = new HashMap<String,ArrayList<String>>();
	public static HashMap<String,String> board = new HashMap<String,String>();
	public static String cabrin_template = "CABRIN_TEMPLATE";
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception  
	{		

		String refFile = args[0];
		String configFile = args[1];
		String path = args[2];
		
		readReferenceFile(refFile);
		Connector conn = new Connector(configFile,true,"CABRIN_",false,"",false,"");
		System.out.println("Watching following path '"+path+"'");
		
		Path faxFolder = Paths.get(path);
		
			
		WatchService watchService = FileSystems.getDefault().newWatchService();
			
		faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		int count=0;
			
		boolean valid = true;
			
		do {
				WatchKey watchKey = watchService.take();

				for (WatchEvent event : watchKey.pollEvents()) 
				{
					@SuppressWarnings("unused")
					WatchEvent.Kind kind = event.kind();
					
					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) 
					{
						String fileName = event.context().toString();
						System.out.println((count+1)+"- File Read:" + fileName);
						count++;

						if(hasEntry(fileName) && fileName.endsWith(".TRIGGER"))
						{	// if name.googleNumber where name is a key in the reference table
						
							String fileNameEntry = getGenericFileName(fileName);//to get the key from the reference table
							
							if(fileNameEntry!=null)
							{

								ArrayList<String>listOfJobs = reference.get(fileNameEntry);
								//this is where we fetch the list of jobs that
								//need to be triggered by this file
								
								for(int j=0;j<listOfJobs.size();j++)
								{
									String targetName = "CABRIN_"+listOfJobs.get(j).toUpperCase();
									

									if(!conn.getConnectionList().get(0).uprocAlreadyExists(targetName))
									{
										conn.getConnectionList().get(0).duplicateUproc(cabrin_template, targetName,"CABRIN");
										System.out.println("Create uproc <"+targetName+">");										
									}
									if(checkPreviousInstanceExecution(targetName,conn))
									{
										Uproc cabrinFromList = conn.getConnectionList().get(0).getUproc(targetName);
									
										modifyVariables(cabrinFromList,fileName,listOfJobs.get(j).toUpperCase(),getGoogleNumber(fileName));
									
									
										conn.getConnectionList().get(0).createLaunch(targetName);
									}
								}
								
							}
							
							
						}
					}
				}
				valid = watchKey.reset();

			} while (valid);

	}

	public static void readReferenceFile(String fileName) throws IOException
	{
		 @SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(fileName),',', '\"', '\0');
			
			String [] line;			

			//parse lines
			while ((line = reader.readNext()) != null) 
		    {	    	
				
		        if(line.length>1)
		        {
		        	String key=line[0];
		        	String value=line[1];
		        
		        	
		        	if(!reference.containsKey(key))
		        	{
		        		ArrayList<String> placer = new ArrayList<String>();
		        		placer.add(value);
		        		reference.put(key, placer);
		        	}
		        	else
		        	{
		        		if(!reference.get(key).contains(value))
		        		{
		        			reference.get(key).add(value);
		        		}
		        	}
		        		
		        }
		    }	
	}

	public static boolean hasEntry(String filename)
	{
		for(String key:reference.keySet())
		{
			if(filename.contains(key))
			{
				return true;
			}
		}
		
		return false;
	}
	public static String getGoogleNumber(String string)
	{// *.G0000V00.TRIGGER needs to be parsed
		string=string.replace(".TRIGGER","");
		int length= string.length();
		
		
		string=string.substring(length-8, length);
		return string.substring(3, 5);
		
	}
	public static String getGenericFileName(String filename)
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

	public  static void modifyVariables(Uproc upr,String file,String mainjob,String googleNumber) throws Exception 
	{
		
		Vector<Variable> varia=upr.getVariables();
		
		if(varia.size()==3 && varia.get(1).getName().equals("COMMAND_PART2") 
						   && varia.get(2).getName().equals("MAIN_JOB_TRIGGER")
						   && !board.containsKey(upr.getName()) )
		{	

			System.out.println("Updating \"COMMAND_PART2\" on UPR <"+upr.getName()+"> with #G ="+googleNumber);
			System.out.println("Updating \"MAIN_JOB_TRIGGER\" on UPR <"+upr.getName()+"> with \""+mainjob+"\"");

				String command =varia.get(1).getValue();
				command=command.replace("-S00", "-S"+googleNumber);
				command=command.replace("<FILE>", file);
				varia.get(1).setValue(command);	
			
				
				String main_job_trigger = varia.get(2).getValue();
				main_job_trigger=main_job_trigger.replace("<JOB>", mainjob);
				varia.get(2).setValue(main_job_trigger);
				
				
				upr.setVariables(varia);	
				upr.update();
		
		}
		if(varia.size()==3 && varia.get(1).getName().equals("COMMAND_PART2") && board.containsKey(upr.getName()) )
		{
			String command =varia.get(1).getValue();
			System.out.println("Updating \"COMMAND_PART2\" on UPR <"+upr.getName()+"> with #G ="+googleNumber);
			command=command.replace("-S"+board.get(upr.getName()), "-S"+googleNumber);
			command=command.replace("G00"+board.get(upr.getName()), "G00"+googleNumber);
			varia.get(1).setValue(command);	
			upr.setVariables(varia);	
			upr.update();
			
		}
			
		board.put(upr.getName(),googleNumber);
			
		
	
	}


	public  static boolean checkPreviousInstanceExecution(String upr,Connector node) throws Exception
	{
		ArrayList<ExecutionStatus> array  = new ArrayList<ExecutionStatus>();
		array.add(ExecutionStatus.Running);
		array.add(ExecutionStatus.Aborted);
		array.add(ExecutionStatus.Pending);
		//,ExecutionStatus.TimeOverrun};

		if(node.getConnectionList().get(0).getExecutionList(upr,array).size()==0)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}

}
