package com.orsyp.tools.ps;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Job {

	private static int count=0;
	private String name;
	private ArrayList<String> listOfFathers ;
	private ArrayList<String> listOfChildren;
 	private ArrayList<String> listOfRules;
	
	public Job(String id)
	{
		name=id;
		listOfFathers = new ArrayList<String>();
		listOfRules = new ArrayList<String>();
		listOfChildren = new ArrayList<String>();
		count++;
	}
	public void addChild(String childName)
	{
		listOfChildren.add(childName);
	}
	public void addFather(String fatherName)
	{
		listOfFathers.add(fatherName);
	}
	public void addRule(String ruleName)
	{
		listOfRules.add(ruleName);
	}
	public String getID()
	{
		return name;
	}
	public ArrayList<String> getFathers()
	{
		return listOfFathers;
	}
	public ArrayList<String> getRules()
	{
		return listOfRules;
	}
	public ArrayList<String> getChildren()
	{
		return listOfChildren;
	}
	public int getCount()
	{
		return count;
	}
	public HashMap<String,List<String>> compareChildren(Job a)
	{
		return getDiff(this.listOfChildren,a.getChildren());
		
	}
	public HashMap<String,List<String>> compareFathers(Job a)
	{
		return getDiff(this.listOfFathers,a.getFathers());
		
	}
	public HashMap<String,List<String>> compareRules(Job a)
	{
		return getDiff(this.listOfRules,a.getRules());
		
	}
	public boolean isEqual(Job a)
	{
		if(this.listOfChildren.containsAll(a.getChildren())
			&& this.listOfFathers.containsAll(a.getFathers())
			&&this.listOfRules.containsAll(a.getRules())
			&&a.getFathers().containsAll(this.listOfFathers)
			&&a.getChildren().containsAll(this.listOfChildren)
			&&a.getRules().containsAll(this.listOfRules))
			{
				return true;
				
			}
		else
		{
			return false;
		}
	}
	private static HashMap<String,List<String>> getDiff(ArrayList<String> a,ArrayList<String>b)
	{
		HashMap<String,List<String>> result = new HashMap<String,List<String>>();
		
	    List<String> sourceList = new ArrayList<String>(a);
	    List<String> destinationList = new ArrayList<String>(b);


	    sourceList.removeAll( b );
	    destinationList.removeAll( a );
	    
	    result.put("a",sourceList );
	    result.put("b",destinationList);
	    
	    return result;
	}
	public void print(PrintStream prtstm)
	{
		prtstm.println();

		prtstm.println("JOB <"+this.name+"> :");
		for(int i=0;i<("JOB <"+this.name+"> :").length();i++)
		{
			prtstm.print("-");
		}
		prtstm.println();
		prtstm.println("-ListofFathers");
		for(int j=0;j<this.listOfFathers.size();j++)
		{
			prtstm.println("--"+this.listOfFathers.get(j));
		}
		
		prtstm.println();
		prtstm.println("-ListofChildren");
		for(int j=0;j<this.listOfChildren.size();j++)
		{
			prtstm.println("--"+this.listOfChildren.get(j));
		}
		
		prtstm.println();
		prtstm.println("-ListofRules");
		for(int j=0;j<this.listOfRules.size();j++)
		{
			prtstm.println("--"+this.listOfRules.get(j));
		}
		
		prtstm.println();
		
	}
	
}
