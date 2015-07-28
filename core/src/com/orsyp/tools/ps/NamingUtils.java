package com.orsyp.tools.ps;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.orsyp.api.HDP;
import com.orsyp.api.InvalidMuTypeException;
import com.orsyp.api.mu.Mu;
import com.orsyp.api.task.Task;
import com.orsyp.api.uproc.Uproc;

public class NamingUtils {

	final static String TASKNAMESPLITTER ="\\-";
	final static String MUNAMESPLITTER ="\\_";
	final static int TOKENS_IN_MU=4;
	final static String UPR_NAME_SEPERATOR="-";
	final static int UPRNAME_LIMIT=63;
	
	public static String getSA_XX(String name) {		
		Matcher matcher = Pattern.compile("[^_]+_[^_]+_").matcher(name);
		if ( matcher.find() )                 
			return matcher.group();
		return "";
	}
	
	public static boolean existsInLookUp(HashMap<String,String>lookup,String objectName)
	{
		for(String index:lookup.keySet())
		{
			if(objectName.contains(index))
			{
				return true;
			}
		}
		
		return false;
	}
	public static boolean doesTaskGoOnNode(Task t,DuApiConnection conn)
	{
		HashMap<String,Mu> muList = conn.getMUsHashMap();
		
		if(muList.containsKey(t.getMuName()))
		{
			if(muList.get(t.getMuName()).getNodeName().equalsIgnoreCase(conn.getNode()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	public static boolean doesTaskGoOnNode(String mu,DuApiConnection conn)
	{
		HashMap<String,Mu> muList = conn.getMUsHashMap();
		
		if(muList.containsKey(mu))
		{
			if(muList.get(mu).getNodeName().equalsIgnoreCase(conn.getNode()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	public static boolean isMKTUproc(String uprName)
	{
		return (uprName.toUpperCase().contains("_MKT_"));
	}
	public static boolean isMKTNode(String conn)
	{
		return (conn.toUpperCase().contains("_MKT"));
	}
	public static String getKeyThatContainsSubstring(HashMap<String,Uproc>lookup,String substring)
	{
		for(String index:lookup.keySet())
		{
			if(index.contains(substring))
			{
				return index;
			}
		}
		
		return null;
	}
	
	
	public static String getTruncatedUprName(String UprName)
	{
  		if(UprName.length()>=UPRNAME_LIMIT)
		{
			if(UprName.contains(UPR_NAME_SEPERATOR))
			{
				String temp = UprName.substring(0,UprName.indexOf(UPR_NAME_SEPERATOR));
				temp=temp.substring(0,(temp.length()-(UprName.length()-UPRNAME_LIMIT)));
				UprName=UprName.replace(UprName.substring(0,UprName.indexOf(UPR_NAME_SEPERATOR)), temp);
				return UprName;
			}
			else
			{
				UprName=UprName.substring(0,UPRNAME_LIMIT);
				return UprName;
			}
		}
  		else
  		{
  			return UprName;
  		}
	}
	
	public static String getJOB(String name) {
		String tks[] = name.split("-");
		String newName = tks[0];
		newName = newName.replace(getSA_XX(name), "");
		if (newName.startsWith("_"))
			newName = newName.substring(1);
		
		return newName;
	}
	public static String getJOBID(String uprocname)
	{
		
			String tks[] = uprocname.split("_");
			
			if(tks.length >=3)
			{
				
				return tks[2];
			}
			
			return null;
		
	}
	
	public static String getMainTaskName_From_OPTTSK(String optionaltaskName)
	{
		String[] tks = optionaltaskName.split(TASKNAMESPLITTER);
		
		if (tks.length==3)//optional task case
        {
	       	   return getSA_XX(tks[0])+"H_"+tks[1]+"-"+tks[2].replace("MKT", "CIS");//needed because MKT specific tasks are linked to CIS main tasks
        }
        else 
        {
     	   	  return null;
	        	
        }
	}
	public static String getSessionName_From_OPTTSK(String optionaltaskName)
	{
		String[] tks = optionaltaskName.split(TASKNAMESPLITTER);
		
		if (tks.length==3)//optional task case
        {
	       	   return tks[1];
        }
        else 
        {
     	   	  return null;
	        	
        }
	}
	public static String getSessionName_From_MAINTSK(String mainTasks)
	{
		String[] tks = mainTasks.split("\\-");
		
		if(tks.length == 2)
		{
			return tks[0].substring(8);
		}
		else
			return null;
	}
	public static String getUprocName_From_OPTTSK(String optionaltaskName)
	{
		String[] tks = optionaltaskName.split(TASKNAMESPLITTER);
		
		if (tks.length==3)//optional task case
        {
	       	   return tks[0]+"-"+tks[1];
        }
        else 
        {
     	   	  return null;
	        	
        }
		
	}
	public static String getMUName_From_OPTTSK(String optionaTaskName)
	{
		String[] tks = optionaTaskName.split(TASKNAMESPLITTER);
	
		if (tks.length==3)//optional task case
		{
			return tks[2];
		}
		else 
		{
			return null;
        	
		}
		
	}
	public static String getMUName_From_MAINTSK(String mainTaskName)
	{
		String[] tks = mainTaskName.split(TASKNAMESPLITTER);
	
		if (tks.length==2)//main task case
		{
			return tks[1];
		}
		else 
		{
			return null;
        	
		}
		
	}
	public static String getHeaderName_From_Uproc(String uproc)
	{
		String[] tks = uproc.split(TASKNAMESPLITTER);
		
		if(tks.length == 2)
		{
			return getSA_XX(uproc)+"H_"+tks[1];
		}
		else
		{
			return null;
		}
		
	}
	public static String getToken_2_and_4_of_MU(String taskName)
	{
		String[] tks = taskName.split(TASKNAMESPLITTER);
		 String[]mutks;
		 
		 if(tks.length==2 )// so it's a maintask since ATCO has only maintasks on headers that are <HEADER>-<MU> as opposed to <UPROC>-<SESSION>-<MU> for optional tasks. 
          {
	       	   mutks = tks[1].split(MUNAMESPLITTER);
	       	   
	       	   if(mutks.length==TOKENS_IN_MU)
	       	   {
	       		   return mutks[1]+"_"+mutks[3];
	       	   }
	       	   else
	       	   {
	       		   return null;
	       	   }
       	
          }
          else if (tks.length==3)//optional task case
          {
	       	   mutks=tks[2].split(MUNAMESPLITTER);
	     	   
	       	   if(mutks.length==TOKENS_IN_MU)
	     	   {
	     		   return mutks[1]+"_"+mutks[3];
	     	   }
	     	   else
	     	   {
	     		   return null;
	     	   }	

          }
          else 
          {
       	   	  return null;
	        	
          }
	}
	
	public static String getToken_1_and_3_of_NODENAME(String nodeName)
	{
		String[] tks = nodeName.split(MUNAMESPLITTER);
		
		 if(tks.length==3 )//C2_IST_MKT for instance you need to get C2_MKT 
         {
	       	return tks[0]+"_"+tks[2];
	     }
		 else
	     {
			 return null;
	     }
	}
	public static String getSessionFromUpr(String UprocName)
	{
		String[] tks = UprocName.split(TASKNAMESPLITTER);
		
		if(tks.length==2)
		{
			return tks[1];
		}
		else if(NamingUtils.isNCTrailer(UprocName))
		{
			return UprocName.substring(UprocName.indexOf("T_NC_")+5);
		}
		else if(NamingUtils.isTrailer(UprocName) && !NamingUtils.isNCTrailer(UprocName))
		{
			return UprocName.substring(UprocName.indexOf("T_")+2);

		}
		else if (NamingUtils.isHeader(UprocName))
		{
			return UprocName.substring(UprocName.indexOf("_H_")+3);
		}
		else if (NamingUtils.isExitPoint(UprocName))
		{
					return UprocName.substring(UprocName.indexOf("_X_")+3);
		}
		else return null;

	}
	
	public static boolean isExitPoint(String upr)
	{
		if (upr.toUpperCase().contains("_X_"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean isHeaderOrTrailer(String name) {
		if(name.contains("_H_") || name.contains("_T_"))
		{
			return true;
		}
		String s = name.replace(getSA_XX(name), "");
		return s.startsWith("T_") || s.startsWith("H_");
	}
	public static boolean isHeader(String name)
	{
		if(name.contains("_H_") )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static boolean isTrailer(String name)
	{
		if(name.contains("_T_") )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static boolean isNCTrailer(String name)
	{
		if(name.contains("_T_NC_") )
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	public static String getUprocHDP_fromCSV(String csvinput)
	{
		if(csvinput.contains("(;C)"))
        {
        	return " C";
        }
        else if (csvinput.contains("(C;)"))
        {
        	return "C ";
        }
        else if (csvinput.contains("(;L)"))
        {
        	return " L";
        		
        }
        else if (csvinput.contains("(L;)"))
        {
        	return "L ";
        }
        else if (csvinput.contains("(W;)"))
        {
        	return "W ";
        }
        else if (csvinput.contains("(;W)"))
        {
        	return " W";
        }
		 return "";
	}
	
	public static HDP getSessionHDP_fromCSV(String csvinput)
	{
		HDP result = new HDP();
		if(csvinput.contains("(;C)"))
		{
			try {
				result.setChild("");
				result.setParent("C");
			} catch (InvalidMuTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (csvinput.contains("(C;)"))
		{
			try {
				result.setChild("C");
				result.setParent("");
			} catch (InvalidMuTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (csvinput.contains("(L;)"))
		{
			try {
				result.setChild("L");
				result.setParent("");
			} catch (InvalidMuTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (csvinput.contains("(;L)"))
		{
			try {
				result.setChild("");
				result.setParent("L");
			} catch (InvalidMuTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (csvinput.contains("(W;)"))
		{
			try {
				result.setChild("W");
				result.setParent("");
			} catch (InvalidMuTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (csvinput.contains("(;W)"))
		{
			try {
				result.setChild("");
				result.setParent("W");
			} catch (InvalidMuTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else 
		{
			
			try {
				result.setChild("");
				result.setParent("");
			} catch (InvalidMuTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static String getCSV_fromSessionHDP(HDP hdp)
	{
		if (hdp!=null){
		if(hdp.getChild()==null|| hdp.getChild().trim().isEmpty())
		{
			if(hdp.getParent()==null || hdp.getParent().trim().isEmpty())
			{
				return "";
			}
			else
			{
				return "(;"+hdp.getParent()+")";
			}
		}
		else
		{
			if(hdp.getParent()==null || hdp.getParent().trim().isEmpty())
			{
				return "("+hdp.getChild()+";)";
			}
			else
			{
				return "("+hdp.getChild()+hdp.getParent()+")";
			}
			
		}
		}
		return "";
	}
		
		
	
}
