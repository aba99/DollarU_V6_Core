package com.orsyp.client.amex;

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

	private static HashMap<String,String> reference = new HashMap<String,String>();
	private static ArrayList<ExecutionStatus> array  = new ArrayList<ExecutionStatus>();
	
	public static String cabrin_template = "CABRIN_TEMPLATE";
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
		
		Connector conn = new Connector(configFile,true,"CABRIN_",false,"",false,"");
		DuApiConnection duapi = conn.getConnectionList().get(0);
		
		System.out.println("Scanning following path '"+path+"'");
		final File folder = new File(path);
		int count =1;
		
		while(true)
		{
			HashMap<String,String> currentFiles = getFilesToProcess(folder);//CABRINUPROC_FILENAMETOPROCESS
		
			
			
			for(String cabUprocKey : currentFiles.keySet())
			{
				
				
				if(duapi.getExecutionList(cabUprocKey, array).size()==0)
				{
					System.out.println(count+" - Processing CABRIN_UPROC <"+cabUprocKey+"> with file \""+currentFiles.get(cabUprocKey)+"\"");
					
					count++;
					
					if(!duapi.doesUprocExist(cabUprocKey))
					{
						duapi.duplicateUproc(cabrin_template, cabUprocKey, "CABRIN");
						duapi.setNonSimulOnUproc(cabUprocKey);
					}
					
						Uproc currentCabUpr = duapi.getUproc(cabUprocKey);
						Vector<Variable> varia = currentCabUpr.getVariables();
						
						if(varia.size()==3 
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
						}
						
						currentCabUpr.setVariables(varia);
						currentCabUpr.update();
					
					
					
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
			return reference.get(getGenericKeyFileName(fullFilename));
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
				
		        if(line.length>1)
		        {
		        	String key=line[0];
		        	String value=line[1];
		        
		        	
		        		reference.put(key, "CABRIN_"+value.toUpperCase());
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
