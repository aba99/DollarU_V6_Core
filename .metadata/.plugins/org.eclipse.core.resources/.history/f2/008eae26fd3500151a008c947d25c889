package com.orsyp.tools.ps;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) throws Exception {

		String fileName = args[0];
		String file_esp_uproc=args[1];
		String file_espschedule_rulename=args[2];
		String file_espListOfFather=args[3];
		String file_espJob_schedules=args[4];
		String file_uproc_rules = args[5];
		
		
		Connector conn = new Connector(fileName,true,"",true,"",true,"");
	
		
		InMemoryFile espJob_uprocName = new InMemoryFile(file_esp_uproc);
		espJob_uprocName.store();
		//espJob_uprocName.printOut(System.out);

		InMemoryFile espSchedule_ruleName = new InMemoryFile(file_espschedule_rulename);
		espSchedule_ruleName.store();
		//espSchedule_ruleName.printOut(System.out);

		InMemoryFile espListOfFathers = new InMemoryFile(file_espListOfFather);
		espListOfFathers.store();
		//espListOfFathers.printOut(System.out);

		InMemoryFile espListOfSchedules = new InMemoryFile(file_espJob_schedules);
		espListOfSchedules.store();
		//espListOfSchedules.printOut(System.out);
		
		InMemoryFile uproc_rule = new InMemoryFile(file_uproc_rules);
		uproc_rule.store();
		
		//ArrayList<String> rulesToApply = new ArrayList<String>();
		ArrayList<String> dependenciesToApply = new ArrayList<String>();
		
		for(String uprKey:uproc_rule.getHash_Store().keySet())
		{
			conn.getConnectionList().get(0).createOptionalTaskOnUproc(uprKey, uproc_rule.getHash_Store().get(uprKey));

		}
 	
		for(String espKJobKey:espJob_uprocName.getHash_Store().keySet())
		{
			
			
			/*Part to apply rules*/

			///part where we just apply the rules 
			
			
			///part where we need to cross reference everything
			
/*			if(espListOfSchedules.getHash_Store().containsKey(espKJobKey))// if I can find an entry for the current job in my espListOfSchedules
			{
				ArrayList<String>espschedules= new ArrayList<String>(espListOfSchedules.getHash_Store().get(espKJobKey));

				for(int s=0;s<espschedules.size();s++)
				{
					
					if(!espSchedule_ruleName.getHash_Store().containsKey(espschedules.get(s)))
					{
						ArrayList<String> ruleNamesIn$U = new ArrayList<String>();
						ruleNamesIn$U.add(espschedules.get(s));
						espSchedule_ruleName.getHash_Store().put(espschedules.get(s), ruleNamesIn$U);
						//just add the rule with its ESP name if its not foudn in the espschedule_mapping
						
					}
					
					rulesToApply.addAll(espSchedule_ruleName.getHash_Store().get(espschedules.get(s)));
					
				}
			}
			
			conn.getConnectionList().get(0).createOptionalTaskOnUproc(espJob_uprocName.getHash_Store().get(espKJobKey).get(0), rulesToApply);
			rulesToApply.clear();*/
			
			/*Part to apply dependencies*/
			if(espListOfFathers.getHash_Store().containsKey(espKJobKey))
			{
				ArrayList<String>espfathers= new ArrayList<String>(espListOfFathers.getHash_Store().get(espKJobKey));
				for(int s=0;s<espfathers.size();s++)
				{
					if(espJob_uprocName.getHash_Store().containsKey(espfathers.get(s)))//if I can find the equivalent $U uprocname
					{
						dependenciesToApply.addAll(espJob_uprocName.getHash_Store().get(espfathers.get(s)));
					}
				}
			}
			
			if(espJob_uprocName.getHash_Store().containsKey(espKJobKey))//if I can find the equivalent $U uprocname
			{
				conn.getConnectionList().get(0).setDepConOnUproc(espJob_uprocName.getHash_Store().get(espKJobKey).get(0), dependenciesToApply);
			}
			dependenciesToApply.clear();
			
		}
	
			
		
/*		
		ArrayList<Job> list = new ArrayList<Job>(conn.getConnectionList().get(0).uprocsToJob());	
	
		for(int i=0;i<list.size();i++)
		{
			list.get(i).print(System.out);
		}*/
		
		
		
	/*		InMemoryFile myFile = new InMemoryFile(fileName,3);
		myFile.store();
		
		myFile.printOut(System.out);
		
		Job a = new Job("myJob");
		a.print(System.out);
		a.addChild("Baby");
		a.addFather("Sobhi");
		a.addRule("EveryDay");
		a.print(System.out);
	
		Job b = new Job ("myOtherJob");
		b.addChild("Baby");
		b.addFather("Sobhi");
		b.addFather("Ahmad");
		b.addRule("EveryDay");
		
		System.out.println(a.isEqual(b));
		b.print(System.out);*/
	}
	

}
