package com.orsyp.tools.ps;

import static java.lang.System.out;

import java.io.File;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.SerializationUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.orsyp.Area;
import com.orsyp.Environment;
import com.orsyp.Identity;
import com.orsyp.SyntaxException;
import com.orsyp.UniverseException;
import com.orsyp.api.Client;
import com.orsyp.api.Context;
import com.orsyp.api.FunctionalPeriod;
import com.orsyp.api.ItemList;
import com.orsyp.api.ObjectNotFoundException;
import com.orsyp.api.Product;
import com.orsyp.api.Variable;
import com.orsyp.api.application.Application;
import com.orsyp.api.application.ApplicationFilter;
import com.orsyp.api.application.ApplicationList;
import com.orsyp.api.central.UniCentral;
import com.orsyp.api.domain.Domain;
import com.orsyp.api.domain.DomainFilter;
import com.orsyp.api.domain.DomainList;
import com.orsyp.api.dqm.DqmQueue;
import com.orsyp.api.dqm.DqmQueueFilter;
import com.orsyp.api.dqm.DqmQueueId;
import com.orsyp.api.dqm.DqmQueueList;
import com.orsyp.api.dqm.DqmQueueType;
import com.orsyp.api.event.JobEvent;
import com.orsyp.api.event.JobEventFilter;
import com.orsyp.api.event.JobEventItem;
import com.orsyp.api.event.JobEventList;
import com.orsyp.api.execution.Execution;
import com.orsyp.api.execution.ExecutionFilter;
import com.orsyp.api.execution.ExecutionItem;
import com.orsyp.api.execution.ExecutionList;
import com.orsyp.api.execution.ExecutionLog;
import com.orsyp.api.execution.ExecutionStatus;
import com.orsyp.api.launch.Launch;
import com.orsyp.api.launch.LaunchFilter;
import com.orsyp.api.launch.LaunchId;
import com.orsyp.api.launch.LaunchItem;
import com.orsyp.api.launch.LaunchList;
import com.orsyp.api.mu.Mu;
import com.orsyp.api.mu.MuFilter;
import com.orsyp.api.mu.MuList;
import com.orsyp.api.rule.KDayAuthorization;
import com.orsyp.api.rule.KDayAuthorization.KDayAuthorizationType;
import com.orsyp.api.rule.KmeleonPattern.KDayType;
import com.orsyp.api.rule.MonthAuthorization;
import com.orsyp.api.rule.MonthAuthorization.Direction;
import com.orsyp.api.rule.PositionsInPeriod;
import com.orsyp.api.rule.PositionsInPeriod.SubPeriodType;
import com.orsyp.api.rule.Rule;
import com.orsyp.api.rule.Rule.PeriodTypeEnum;
import com.orsyp.api.rule.RuleFilter;
import com.orsyp.api.rule.RuleId;
import com.orsyp.api.rule.RuleItem;
import com.orsyp.api.rule.RuleList;
import com.orsyp.api.rule.UniPattern;
import com.orsyp.api.rule.UniPattern.RunOverEnum;
import com.orsyp.api.rule.WeekAuthorization;
import com.orsyp.api.rule.YearAuthorization;
import com.orsyp.api.security.Operation;
import com.orsyp.api.session.ExecutionContext;
import com.orsyp.api.session.Session;
import com.orsyp.api.session.SessionAtom;
import com.orsyp.api.session.SessionData;
import com.orsyp.api.session.SessionFilter;
import com.orsyp.api.session.SessionId;
import com.orsyp.api.session.SessionItem;
import com.orsyp.api.session.SessionList;
import com.orsyp.api.session.SessionTree;
import com.orsyp.api.session.SessionTree.AtomVisitor;
/*import com.orsyp.api.syntaxerules.ClassicSyntaxRules;
*/import com.orsyp.api.syntaxerules.OwlsSyntaxRules;


import com.orsyp.api.task.DayType;
import com.orsyp.api.task.LaunchHourPattern;
import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskFilter;
import com.orsyp.api.task.TaskId;
import com.orsyp.api.task.TaskImplicitData;
import com.orsyp.api.task.TaskItem;
import com.orsyp.api.task.TaskList;
import com.orsyp.api.task.TaskPlanifiedData;
import com.orsyp.api.task.TaskProvokedData;
import com.orsyp.api.task.TaskType;
import com.orsyp.api.uproc.Memorization;
import com.orsyp.api.uproc.MuControl;
import com.orsyp.api.uproc.DependencyCondition.Status;
import com.orsyp.api.uproc.MuControl.Type;
import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.LaunchFormula;
import com.orsyp.api.uproc.NonSimultaneityCondition;
import com.orsyp.api.uproc.ProcessingDateControl;
import com.orsyp.api.uproc.ResourceCondition;
import com.orsyp.api.uproc.SessionControl;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.uproc.UprocFilter;
import com.orsyp.api.uproc.UprocId;
import com.orsyp.api.uproc.UprocList;
import com.orsyp.api.uproc.UserControl;
import com.orsyp.api.uproc.cl.InternalScript;
import com.orsyp.api.user.UserFilter;
import com.orsyp.api.user.UserList;
import com.orsyp.central.jpa.jpo.NodeInfoEntity;
import com.orsyp.comm.Connection;
import com.orsyp.comm.client.ClientServiceLocator;
import com.orsyp.owls.impl.application.OwlsApplicationImpl;
import com.orsyp.owls.impl.application.OwlsApplicationListImpl;
import com.orsyp.owls.impl.domain.OwlsDomainImpl;
import com.orsyp.owls.impl.domain.OwlsDomainListImpl;
import com.orsyp.owls.impl.dqm.OwlsDqmQueueImpl;
import com.orsyp.owls.impl.dqm.OwlsDqmQueueListImpl;
import com.orsyp.owls.impl.event.OwlsJobEventImpl;
import com.orsyp.owls.impl.event.OwlsJobEventListImpl;
import com.orsyp.owls.impl.execution.OwlsExecutionImpl;
import com.orsyp.owls.impl.execution.OwlsExecutionListImpl;
import com.orsyp.owls.impl.launch.OwlsLaunchImpl;
import com.orsyp.owls.impl.launch.OwlsLaunchListImpl;
import com.orsyp.owls.impl.mu.OwlsMuImpl;
import com.orsyp.owls.impl.mu.OwlsMuListImpl;
import com.orsyp.owls.impl.nfile.NodeFile;
import com.orsyp.owls.impl.rule.OwlsRuleImpl;
import com.orsyp.owls.impl.rule.OwlsRuleListImpl;
import com.orsyp.owls.impl.session.OwlsSessionImpl;
import com.orsyp.owls.impl.session.OwlsSessionListImpl;
import com.orsyp.owls.impl.task.OwlsTaskImpl;
import com.orsyp.owls.impl.task.OwlsTaskListImpl;
import com.orsyp.owls.impl.uproc.OwlsUprocImpl;
import com.orsyp.owls.impl.uproc.OwlsUprocListImpl;
import com.orsyp.owls.impl.user.OwlsUserListImpl;
import com.orsyp.owls.msg.TaskMsg.EOwlsTaskStatus;
import com.orsyp.owls.msg.TaskMsg.EOwlsTaskType;
import com.orsyp.std.ClientConnectionManager;
import com.orsyp.std.ConnectionFactory;
import com.orsyp.std.MultiCentralConnectionFactory;


import com.orsyp.std.central.UniCentralStdImpl;
import com.orsyp.std.nfile.LocalBinaryFile;
import com.orsyp.util.DateTools;


public class DuApiConnection {
	
	private String name;
	private Context context;
	
	static String fileName = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
    private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm:ss");

    
	private String node;
	private String area;
	private String host;
	private int port ;
	private String user;
	private String password;

	private static String defaultVersion = "000";
	@SuppressWarnings("unused")
	private static String defaultSubmissionUser="user";
	private static String defaultSubmissionAccount ="Operations";

	public boolean isReference = false;
	
	private HashMap<String, Session> sess = new HashMap<String, Session>();
	private HashMap<String, Uproc> uprs = new HashMap<String, Uproc>();
	private HashMap<String,Task>tsks = new HashMap<String,Task>();
	private Multimap<String, Task> tsks_multimap = ArrayListMultimap.create();
	private HashMap<String,Rule> rules= new HashMap<String,Rule>();
	

	final static int TSKNAME_LIMIT = 63;
	final static String TASKNAMESPLITTER ="\\-";
	

	public DuApiConnection(String node, String area, String host, int port, String user, String password) {
		try {	
			
			
			getNodeConnection(node, area, host, port, user, password);
			this.node=node;
			this.area=area;
			this.host=host;
			this.port=port;
			this.user=user;
			this.password=password;
			
			this.name=node+"/"+area;
			
			sess = getSessionsHashMap("");
			uprs = getUprocHashMap("");
			tsks_multimap = getTaskMultiMap("");
			tsks = getTaskHashMap();
			rules=getRulesHashMap();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public DuApiConnection(PrintStream prtstm,String node, String area, String host, int port, String user, String password,boolean withUpr,String uprfilter,boolean withSes,String sesfilter,boolean withTsk,String tskfilter) {
		try {	
			
			
			getNodeConnection(node, area, host, port, user, password);
			this.node=node;
			this.area=area;
			this.host=host;
			this.port=port;
			this.user=user;
			this.password=password;
			
			this.name=node+"/"+area;
			
			System.out.println("Connecting to \""+node+"/"+area+"\" ...");
			prtstm.println("Connecting to \""+node+"/"+area+"\" ...");
			
			if(withUpr)
			{
				System.out.println("Extracting uprocs ...");
				prtstm.println("Extracting uprocs ...");
				uprs = getUprocHashMap(uprfilter);
			}
			
			if(withSes)
			{
				System.out.println("Extracting sessions ...");
				prtstm.println("Extracting sessions ...");
				sess = getSessionsHashMap(sesfilter);
			}
			
		
			
			if(withTsk)
			{
				System.out.println("Extracting tasks ...");
				prtstm.println("Extracting tasks ...");


				tsks_multimap= getTaskMultiMap(tskfilter);
				tsks = getTaskHashMap();
				rules=getRulesHashMap();

			}
			
			System.out.println("--> Connected !");
			prtstm.println("--> Connected !");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public DuApiConnection(String node, String area, String host, int port, String user, String password,boolean withUpr,boolean withSes,boolean withTsk) {
		try {	
			
			
			getNodeConnection(node, area, host, port, user, password);
			this.node=node;
			this.area=area;
			this.host=host;
			this.port=port;
			this.user=user;
			this.password=password;
			
			this.name=node+"/"+area;
			
			System.out.println("Connecting to \""+node+"/"+area+"\" :");
			
			if(withSes)
			{
				System.out.println("Extracting sessions ...");
				sess = getSessionsHashMap("");
			}
			
			if(withUpr)
			{
				System.out.println("Extracting uprocs ...");
				uprs = getUprocHashMap("");
			}
			
			if(withTsk)
			{
				System.out.println("Extracting tasks ...");

				tsks_multimap = getTaskMultiMap("");
				tsks = getTaskHashMap();
				rules=getRulesHashMap();

			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getNode()
	{
		return node;
	}
	public String getUser()
	{
		return user;
	}
	public String getPassword()
	{
		return password;
	}
	public int getPort()
	{
		return port;
	}
	public String getArea()
	{
		return area;
	}
	public String getHost()
	{
		return host;
	}
	public String getConnName()
	{
		return this.name;
	}

	private void getNodeConnection(String node, String area, String host, int port, String user, String password) throws Exception{
		
		UniCentral central = getUVMSConnection(host, port, user, password);			
		NodeInfoEntity[] nnes = ClientServiceLocator.getNodeInfoService().getAllNodeInfoFromCache(-1, null);
		String company = null;
		
		for (NodeInfoEntity nne : nnes)
			if (nne.getProductCode().equals("DUN"))
				if (nne.getNodeName().equalsIgnoreCase(node)) {
					company = nne.getCompany();
					break;
				}
		
		if (company==null)
			throw new Exception("Node not found");
				
		if (Arrays.asList("A", "APP", "I", "INT").contains(area.toUpperCase())) 
			defaultVersion = "001";//001
		else
			defaultVersion = "000";
		
		Area a = Area.Exploitation;
		if (Arrays.asList("A", "APP").contains(area.toUpperCase()))
			a = Area.Application;
		else
		if (Arrays.asList("I", "INT").contains(area.toUpperCase()))
			a = Area.Integration;
		else
		if (Arrays.asList("S", "SIM").contains(area.toUpperCase()))
			a = Area.Simulation;
		
		context = makeContext(node, company, central, user, a);
	}
	
	private UniCentral getUVMSConnection(String host, int port, String user, String password) throws SyntaxException {		
		UniCentral cent = new UniCentral(host, port);
		cent.setImplementation(new UniCentralStdImpl(cent));
		
		Context ctx = new Context(new Environment("UJCENT",host), new Client(new Identity(user, password, host, "")));
		ctx.setProduct(com.orsyp.api.Product.UNICENTRAL);
		ctx.setUnijobCentral(cent);
		ClientServiceLocator.setContext(ctx);

		try {
			cent.login(user, password);
			if (ClientConnectionManager.getDefaultFactory() == null) {
	            ClientConnectionManager.setDefaultFactory(MultiCentralConnectionFactory.getInstance());
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cent;
	}
	
	private Context makeContext(String node, String company, UniCentral central, String user, Area area) throws SyntaxException {
		Context ctx = null;
		Client client = new Client(new Identity(user, "", node, ""));
		ctx = new Context(new Environment(company, node, area), client, central);
		ctx.setProduct(Product.OWLS);
		return ctx;
	}
	


	
	//-------------------------------------------
		
	public Context getContext() {
		return context;
	}
	
	//-----------------------------------------
	
	public void duplicateUproc (String sourceUproc, String targetName,String uprlbl) throws Exception{
		Uproc uproc = getUproc(sourceUproc);

		//uproc.setDefaultSeverity(uprseverity);
		uproc.setLabel(uprlbl);
		uproc.update();
		uproc.extract();
		
		if(doesUprocExist(targetName))
		{
			return;
		}
		
        UprocId newDuplicatedUprocId = new UprocId(targetName, defaultVersion);
		newDuplicatedUprocId.setId(targetName);  
        
        uproc.setImpl(new OwlsUprocImpl());
        uproc.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        uproc.duplicate(newDuplicatedUprocId, uproc.getLabel());
        
        java.util.Date date3= new java.util.Date();
		Timestamp ts3 = new Timestamp(date3.getTime());
		
		System.out.println(ts3+": Uproc <"+targetName+"> created off of <"+sourceUproc+">");
		

        
        Uproc obj = new Uproc(getContext(), newDuplicatedUprocId);
        obj.setImpl(new OwlsUprocImpl());
        obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        //obj.create(); 
        
        uprs.put(targetName, obj);
	}
	
	public void duplicateUproc (String sourceUproc, String targetName) throws Exception{
		
		if(!uprs.containsKey(sourceUproc))
		{
			return;
		}
		
		if(uprs.containsKey(targetName))
		{
			uprs.get(targetName).delete();
			uprs.remove(targetName);
		}
		
	
		Uproc uproc = uprs.get(sourceUproc);
	
		
        UprocId newDuplicatedUprocId = new UprocId(targetName, defaultVersion);
        newDuplicatedUprocId.setName(targetName);
        newDuplicatedUprocId.setId(targetName);
        
        uproc.setImpl(new OwlsUprocImpl());
        uproc.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        uproc.duplicate(newDuplicatedUprocId, uproc.getLabel());
        

        System.out.println("Uproc <"+sourceUproc+"> duplicated into Uproc <"+targetName+">");
      
        Uproc obj = new Uproc(getContext(), newDuplicatedUprocId);
        obj.setImpl(new OwlsUprocImpl());
        obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        //obj.create(); 
        
        uprs.put(targetName, obj);
        
        
	
	}
	
	public Uproc getUproc(String name) throws Exception{
	
		if(uprs.containsKey(name))
		{
			return uprs.get(name);
		}
		
		UprocId uprocId = new UprocId(name, defaultVersion);
		Uproc obj = new Uproc(getContext(), uprocId);
        
        obj.setImpl(new OwlsUprocImpl());
        obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());

        obj.extract();

        if(obj!=null)
        {
        	uprs.put(obj.getName(), obj);
        }
        
		return obj;
	}
	
	public Uproc getUprocWithContext(String name) throws Exception{
		
		if(uprs.containsKey(name))
		{
			return uprs.get(name);
		}
		
		UprocId uprocId = new UprocId(name, defaultVersion);
		Uproc obj = new Uproc(getContext(), uprocId);
        
        obj.setImpl(new OwlsUprocImpl());
        obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());

        obj.extract();
		System.out.println("UPR ---- "+obj.getName());

		return obj;
	}
	
	
	public Session getSession(String name) throws Exception{
		if(sess.containsKey(name))
		{
			return sess.get(name);
		}
		
	
		    	SessionId sessionId = new SessionId(name, defaultVersion);
	            Session obj = new Session(getContext(), sessionId);
	            obj.setImpl(new OwlsSessionImpl());
	            obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
	            obj.extract();

		return obj;
	}
	
	public boolean ruleAlreadyExists(String rulename) throws UniverseException
	{
		
	   return rules.containsKey(rulename);
	    
	}

	public void dumpSessions_to_CSV(HashMap<String,String>sesList, PrintStream prtstm)
	{
		prtstm.println("Session,Uproc,Rule,LW,MU");
		for(String sesKey:sesList.keySet())
		{
			if(sess.containsKey(sesKey))
			{
				for(int u=0;u<sess.get(sesKey).getUprocs().length;u++)
				{
					prtstm.println(sesKey+","+sess.get(sesKey).getUprocs()[u]+","+getRULE_LW_MU_for_CSV(sess.get(sesKey).getUprocs()[u]));
				}
			}
		}
	}
	public void deleteTasksForSession(String sesName) throws UniverseException
	{
		for(String tskKey:tsks.keySet())
		{
			
		/*	if(tsks.get(tskKey).getSessionName().equalsIgnoreCase(sesName))
			{
				tsks.get(tskKey).delete();
			}*/
			if(tsks.get(tskKey).getSessionName().equalsIgnoreCase(sesName))
			{
				tsks.get(tskKey).delete();
			}
		}
	}
	
	
	
    int countChild(SessionAtom atom) {
        int childCount = 0; // le nb de child au level+1
        if (atom.getChildOk() != null) {
            childCount++;
            SessionAtom a = atom.getChildOk();
            while (a.getNextSibling() != null) {
                childCount++;
                a = a.getNextSibling();
            }
        }
        if (atom.getChildKo() != null) {
            childCount++;
            SessionAtom a = atom.getChildKo();
            while (a.getNextSibling() != null) {
                childCount++;
                a = a.getNextSibling();
            }
        }
        return childCount;
    }


  

    public boolean uprocAlreadyExists(String uprname) throws UniverseException
	{
		
			return uprs.containsKey(uprname);
					
	}

	public boolean doesUprocExist(String upr) throws UniverseException
	{
		 UprocFilter filter = new UprocFilter(upr);
	        UprocList list = new UprocList(getContext(), filter);
	        
	        list.setSyntaxRules(OwlsSyntaxRules.getInstance());
	        OwlsUprocListImpl impl = new OwlsUprocListImpl();
	        impl.init(list, Operation.DISPLAYLIST);
	        list.setImpl(impl);
	       // list.setImpl(new OwlsUprocListImpl());
	        list.extract();
	        //System.out.println("lissssssst"+list.getCount());
	       if(list.getCount()!=0)
	       {
	    	   return true;
	       }
	       else
	       {
	    	   return false;
	       }
	}
	
	
	public ItemList<LaunchItem> getLaunchList() throws UniverseException {
	        /* pattern for task name  */
	        LaunchFilter filter = new LaunchFilter();
	        filter.setSessionId("*");
	        filter.setUprocId("*");
	        filter.setMuId("*");
	        filter.setSessionName("*");
	        filter.setUprocName("*");
	        filter.setMuName("*");
	        filter.selectAllStatus();
	        filter.setUserName("*");
	        filter.setUserId("*");
	        filter.setBeginDate("*");  /**@todo use Date ?? */
	        filter.setBeginHour("*");  /**@todo use Date ?? */
	        filter.setEndDate("*");  /**@todo use Date ?? */
	        filter.setEndHour("*");  /**@todo use Date ?? */
	        filter.setProcessingDate("*");
	        filter.setNumlancMin("0000000");
	        filter.setNumlancMax("9999999");
	        filter.setNumsessMin("0000000");
	        filter.setNumsessMax("9999999");
	        filter.setNumprocMin("0000000");
	        filter.setNumprocMax("9999999");
	        
	        LaunchList list = new LaunchList(getContext(), filter);
	        list.setImpl(new OwlsLaunchListImpl());
	        list.extract();
	        return list;
	    }
	public ItemList<LaunchItem> getLaunchList(String uproc) throws UniverseException {
        /* pattern for task name  */
        LaunchFilter filter = new LaunchFilter();
        filter.setSessionId("*");
        filter.setUprocId("*"+uproc+"*");
        filter.setMuId("*");
        filter.setSessionName("*");
        filter.setUprocName("*");
        filter.setMuName("*");
        filter.selectAllStatus();
        filter.setUserName("*");
        filter.setUserId("*");
        filter.setBeginDate("*");  /**@todo use Date ?? */
        filter.setBeginHour("*");  /**@todo use Date ?? */
        filter.setEndDate("*");  /**@todo use Date ?? */
        filter.setEndHour("*");  /**@todo use Date ?? */
        filter.setProcessingDate("*");
        filter.setNumlancMin("0000000");
        filter.setNumlancMax("9999999");
        filter.setNumsessMin("0000000");
        filter.setNumsessMax("9999999");
        filter.setNumprocMin("0000000");
        filter.setNumprocMax("9999999");
        
        LaunchList list = new LaunchList(getContext(), filter);
        list.setImpl(new OwlsLaunchListImpl());
        list.extract();
        return list;
    }
	
	public void createDQM(String dqmname,int joblimit) {

        try {
            String queueName = dqmname;
            DqmQueue obj = new DqmQueue(getContext(),DqmQueueId.create(queueName));
            
            String connectionName = this.getConnName();
            String nodeName = connectionName.substring(0,connectionName.indexOf("/"));
            
            obj.setType(DqmQueueType.PHYSICAL);
            obj.setNodeName(nodeName);

            /* we set default values */
            obj.setJobLimit(joblimit);
            obj.setPrioDefaultSub(1);
            obj.setPrioMax(999);

            obj.setImpl(new OwlsDqmQueueImpl());
            obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
            obj.create();
            obj.start();
           obj.update();
           // printf("Dqm queue [%s] created.\n", obj.getIdentifier().getName());
        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
	
	
	public HashMap<String,DqmQueue> getDqmsHashMap() throws UniverseException
	{
		
			HashMap<String,DqmQueue> result = new HashMap<String,DqmQueue>();
	        DqmQueueFilter filter = new DqmQueueFilter();

	        filter.setQueueName("*");
	        filter.setQueueStatus('*');
	        filter.setQueueType('*');
	                
	        /**
	         * DEPRECATED IN OWLS => to extract the related physical queues, run either
	         * {@link com.orsyp.api.dqm.DqmQueue#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueue#relatedPhysicalQueueList}
	         * or run
	         * {@link com.orsyp.api.dqm.DqmQueueList#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueueItem#relatedPhysicalQueueList}
	         */
	        final boolean  isRelatedQueueList = false;

	        /**
	         * DEPRECATED IN OWLS => to extract the related physical queues, run either
	         * {@link com.orsyp.api.dqm.DqmQueue#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueue#relatedPhysicalQueueList}
	         * or run
	         * {@link com.orsyp.api.dqm.DqmQueueList#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueueItem#relatedPhysicalQueueList}
	         */
	        final String queueNameRelated = null;
	        
	        DqmQueueList list = new DqmQueueList(getContext(), filter, isRelatedQueueList, queueNameRelated);
	        list.setImpl(new OwlsDqmQueueListImpl());
	        list.extract();
	        
	        for(int d=0;d<list.getCount();d++)
	        {
	        	 DqmQueue obj = new DqmQueue(getContext(),list.get(d).getIdentifier());
	             
	             obj.setImpl(new OwlsDqmQueueImpl());
	             obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
	             obj.extract();
	             result.put(obj.getIdentifier().getName(),obj );
	        }
	    return result;
	}
	
	
	public boolean tskAlreadyExists(String tskname) throws Exception
	{
		Task foundTask_template = this.getTaskByName(tskname,true);
	    Task foundTask_nontemplate=this.getTaskByName(tskname,false);
	        
			if(foundTask_template==null && foundTask_nontemplate==null)
			{
				return false;
			}
			else
			{
				return true;
			}
			
	}
	
	public boolean taskAlreadyExists(String tskname) throws Exception
	{
		
			
				return tsks.containsKey(tskname);
			
			
	}
	public boolean sesAlreadyExists(String sesname) throws Exception
	{
		
				return sess.containsKey(sesname);
		
			
	}
	

	public void createTempUproc(String upr_temp_name,int upr_temp_severity)
	{

        try {
            String uprocName = upr_temp_name;
            String uprocVersion = "000";

            UprocId uprocId = new UprocId(uprocName, uprocVersion);
            Uproc obj = new Uproc(getContext(), uprocId);
            obj.setLabel("Template");
            obj.setApplication("U_");
            obj.setDomain("I");
            obj.setType("CL_INT");
            obj.setDefaultInformation("");
            obj.setDefaultSeverity(upr_temp_severity);
            obj.setFunctionalPeriod(FunctionalPeriod.Day);
           
            Memorization memo = new Memorization(Memorization.Type.ONE);
            memo.setNumber(1);
            obj.setMemorization(memo);
         
            obj.setImpl(new OwlsUprocImpl());
            obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
            obj.create();
           

            if ("CL_INT".equals(obj.getType())) {
                /* once Uproc is created, we save the associated specific data  */
                createInternalScript(obj);   
            }
           /* if ("CMD".equals(obj.getType())) {

                CmdData cmdData = new CmdData();
                cmdData.setCommandLine("%uxexe%\\uxsleep 6600");//put your command here
                obj.setSpecificData(cmdData);
            }*/

        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
    private void createInternalScript(Uproc obj) throws UniverseException {
        InternalScript data = new InternalScript(obj);
        
        if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_ACISSUB")){
        data.setLines(new String[] {"ACISsub"});}// put your script here
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_HEADER")){
            data.setLines(new String[] {"set resexe=0"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_TRAILER")){
            data.setLines(new String[] {"set resexe=0"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_NC_TRAILER")){
            data.setLines(new String[] {"set resexe=0"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_SPC")){
        	data.setLines(new String[] {"REM call e:\\data\\%c2_ENV%\\scripts\\CallMKTGet.bat","REM set resexe=%errorlevel%"
        			,"set resexe=1"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_SENDFILES")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallSendFiles.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_PRETXN")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallPreTXN.bat","set resexe=%errorlevel%"});}

        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_PUT")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMKTPut.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_GET")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMKTGet.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_MOVEFILES")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMoveFiles.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_DELCSV")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMKTDelCSV.bat","set resexe=%errorlevel%"});
            }
        
        else 
        {
            data.setLines(new String[] {"set resexe=0"});
        }
        
        obj.setInternalScript(data);
        obj.setSpecificData(data);
        data.save();
       // obj.update();
       
    }
    
    

	public void deleteSession(String sessionName) {				
		try {
				if(this.sess.containsKey(sessionName))
				{
					this.sess.get(sessionName).delete();
					this.sess.remove(sessionName);
					System.out.println("SESSION : "+sessionName+" deleted");

					return;
				}
				
				SessionId sessionId = new SessionId(sessionName, defaultVersion);
				Session sess = new Session(getContext(), sessionId);
	
				sess.setImpl(new OwlsSessionImpl());
				sess.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
				sess.delete();
				System.out.println("SESSION : "+sess.getName()+" deleted");
				
				
		} catch (ObjectNotFoundException e) {
			//ignore
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteUproc(String uprname)
	{
		try {
			if(uprs.containsKey(uprname))
			{
				uprs.get(uprname).delete();
				uprs.remove(uprname);
				System.out.println("UPROC : "+uprname+" deleted");

				return;
			}
			
			UprocId uprId = new UprocId(uprname, defaultVersion);
			Uproc upr = new Uproc(getContext(), uprId);

			upr.setImpl(new OwlsUprocImpl());
			upr.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			upr.delete();
			
			System.out.println("UPROC : "+uprname+" deleted");
			if(uprs.containsKey(uprname))
			{
				uprs.remove(uprname);
			}
			
		} catch (ObjectNotFoundException e) {
			//ignore
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////	
   @SuppressWarnings("unused")
private MuControl getMuControl_branch (ArrayList<SessionAtom> trailer_to_header_branch)
   {//this method returns the last execution context in a branch
	   
	   MuControl default_MuControl = new MuControl();// default 
	   default_MuControl.setType (Type.SAME_MU);
	   default_MuControl.setOneEnough (Boolean.TRUE);
	   
	   for(int y=0;y<trailer_to_header_branch.size();y++)
		{
		   if(!trailer_to_header_branch.get(y).getData().getExecutionContext().getType().equals(ExecutionContext.Type.SAME))
		   {
			   if(trailer_to_header_branch.get(y).getData().getExecutionContext().getType().equals(ExecutionContext.Type.HDP))
			   {
				   
				   if(!trailer_to_header_branch.get(y).getData().getExecutionContext().getHDP().toString().equals("{ C}"))
				   {
					   
					   default_MuControl.setType(Type.HDP);
				   default_MuControl.setHdp(trailer_to_header_branch.get(y).getData().getExecutionContext().getHDP().toString().replace("{", "").replace("}", ""));
				   default_MuControl.setOneEnough (Boolean.TRUE);
				   
				   return default_MuControl;
				   }
				   else
				   {
					     return default_MuControl;
					   
				   }
				   
				   
			   }
			   
			   if(trailer_to_header_branch.get(y).getData().getExecutionContext().getType().equals(ExecutionContext.Type.MU))
			   {
				   default_MuControl.setType(Type.SPECIFIC_MU);
				   default_MuControl.setMu(trailer_to_header_branch.get(y).getData().getExecutionContext().getMuName());
				   default_MuControl.setOneEnough (Boolean.TRUE);

				   return default_MuControl;
			   }
		   }
		   else continue;
		}
	   
	   return default_MuControl;
   }
   //////////////////////////////////////////////////////////////////////////////////////
   
   public void updateSessionAtom(String sessname,String currentUprName , String newUprName)
   {
   	try {					    		   		
   	            Session obj = this.getSession(sessname);

   	    		SessionTree tree = obj.getTree();
   	    		
   	    		final String curUpr = currentUprName;
   	    		final String newUpr = newUprName;
   	    			 
   	    		tree.scan(new AtomVisitor() 
   	    			{
   	    				public void handle(SessionAtom atom) 
   	    				{
   	    					updateName(atom,curUpr,newUpr);
   	    				}
   	    			});
   	    			
   	    		if(this.uprocAlreadyExists(currentUprName))
   	    		{
   	    			if(!this.uprocAlreadyExists(newUprName))
   	    			{
   	    				this.duplicateUproc(currentUprName, newUprName);
   	    			}
   	    			
   	   	    		this.deleteUproc(currentUprName);
   	    		}
   	    
   	    		obj.update();
   	    	   	    		
   	    		System.out.println("RENAMING FROM ["+currentUprName+"] to ["+newUprName+"] in ["+sessname+"] DONE --> OK");     

   	} catch (Exception e) {
   		e.printStackTrace();
   	}	
   	
   }
   
   public void createOptionalTaskOnUproc (String uprocName,ArrayList<String> listOfRules) throws Exception {
       try {
       	
    	   uprocName=uprocName.toUpperCase();
    	   
    	   if(!uprs.containsKey(uprocName))
    	   {
    		   System.out.println("**** Uproc "+uprocName+" does not exist");
    		   return;
    	   }
    	   
    	  ArrayList<Task> mainTasks = new ArrayList<Task>(getMainTasksUprBelongsTo(tsks,uprocName));
    	  ArrayList<Task> optTasks  = new ArrayList<Task>(getOptionalTasksUprBelongsTo(tsks,uprocName));
    	  
    	  for(int o=0;o<optTasks.size();o++)
    	  {
    		  optTasks.get(o).delete();
    		  if(tsks.containsKey(optTasks.get(o).getIdentifier().getName()))
    		  {
    			  
    			tsks.remove(optTasks.get(o).getIdentifier().getName())	 ; 
    		  }
    	  }//clean up what already exists there 
    	  
    	  

		  String oTskName = ("OPT_"+uprocName).replace("DECSS_","").toUpperCase();
		  

		  if (oTskName.length() > TSKNAME_LIMIT) 
		  {

			  oTskName=oTskName.substring(TSKNAME_LIMIT);
			  
		  }
    	  
    	  if(mainTasks.size()==1) 
       	  {
    		 

    		  
       	
    		  if(!NamingUtils.doesTaskGoOnNode(mainTasks.get(0).getMuName(), this))
    		  {
    			  return;
    		  }
	
    		  
    		  if(mainTasks.get(0).getTaskType().equals(TaskType.Provoked))
    		  {
					System.out.println("[MAIN TSK] " + mainTasks.get(0).getIdentifier().getName()
							+ " is PROVOKED. Skipping [OPT TSK] "
							+ oTskName + " on " + this.getConnName());
							

					return;
			  }

				Task obj = new Task(getContext(), TaskId.createWithName(oTskName, mainTasks.get(0).getIdentifier().getVersion(),
						mainTasks.get(0).getMuName(), mainTasks.get(0).isTemplate()));
				
				obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());

				/* optional task */
				TaskPlanifiedData tpd = new TaskPlanifiedData();
				tpd.setGenerateEvent(true);
				tpd.setOptional(true);

				TaskPlanifiedData main_tpd = mainTasks.get(0).getPlanifiedData();

				if (main_tpd == null) {
					System.out.println("[MAIN TSK] " + mainTasks.get(0).getIdentifier().getName()
							+ " has WRONG scheduling data. Skipping [OPT TSK] "
							+ oTskName + " on " + this.getConnName());

					return;
				}

				tpd.setLaunchHourPatterns(main_tpd.getLaunchHourPatterns());

				

				  for(int r=0;r<listOfRules.size();r++)
				    {
				    	if(!this.ruleAlreadyExists(listOfRules.get(r)))
				    	{
				    		
				    		createRule(listOfRules.get(r));
				    	}
				    }
				   
				    ArrayList<TaskImplicitData> listOfImplicitData =new ArrayList<TaskImplicitData>();    
				    
				    for(int r=0;r<listOfRules.size();r++)
				    {	
				        Rule rule1=getRule(listOfRules.get(r));
				   
				        TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
				        
				        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
				        tid1.setLabel(rule1.getLabel());
				        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
				        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
				        
				        tid1.setPeriodType (rule1.getPeriodType ());
				        tid1.setPeriodNumber(rule1.getPeriodNumber());
				        tid1.setPattern (rule1.getPattern ());
				        tid1.setAuthorized (true);
				        final Date date1 = DateTools.toDate ("20140101");
				        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
				        Calendar calendar1 = DateTools.getCalendarInstance();
				        calendar1.setTime(date1);
				        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
				        tid1.setApplicationWeek(weekNumber1.toString());
				        
				        tid1.setLabel(rule1.getLabel());
				        tid1.setInternal(true);
				      
				        listOfImplicitData.add(tid1);
					}
				  
				    TaskImplicitData[] implicitDataArray = new TaskImplicitData[listOfImplicitData.size()];
				    listOfImplicitData.toArray(implicitDataArray);
				    tpd.setImplicitData (implicitDataArray);
				        
				    obj.setSpecificData(tpd);
			

				
				Uproc currentUpr= uprs.get(uprocName);				
				
				// uproc info
				obj.setUprocName(uprocName);
				obj.getIdentifier().setUprocVersion(currentUpr.getIdentifier().getVersion());

				// MU info
				obj.setMuName(mainTasks.get(0).getMuName());

				// Session info
				// obj.setSessionId(currentSession.getId());
				obj.setSessionName(mainTasks.get(0).getSessionName());
				obj.getIdentifier().setSessionVersion(mainTasks.get(0).getSessionVersion());

				// Task info
				obj.setTaskType(TaskType.Optional);
				obj.setAutoRestart(false);
				obj.setEndExecutionForced(false);
				obj.setCentral(mainTasks.get(0).isCentral());
				obj.setActive(mainTasks.get(0).isActive());
				obj.setUserName(mainTasks.get(0).getUserName());
				obj.setLabel("Optional task");
				obj.setPriority(mainTasks.get(0).getPriority());
				obj.setQueue(mainTasks.get(0).getQueue());
				obj.setFunctionalPeriod(mainTasks.get(0).getFunctionalPeriod());
				// obj.setUserId(currentMainTask.getUserId());
				obj.setParallelLaunch(false);

				obj.setTypeDayOffset(mainTasks.get(0).getTypeDayOffset());
				obj.setPrinter(mainTasks.get(0).getPrinter());

				obj.setVariables(mainTasks.get(0).getVariables());
				obj.setPrinter(mainTasks.get(0).getPrinter());

				obj.setDayOffset(mainTasks.get(0).getDayOffset());
				obj.setUnitOffset(mainTasks.get(0).getUnitOffset());
				obj.setSimulated(mainTasks.get(0).isSimulated());
				obj.setValidFrom(mainTasks.get(0).getValidFrom());
				obj.setValidTo(mainTasks.get(0).getValidTo());

				obj.setDeploy(false);
				obj.setUpdate(false);
				obj.setInteractiveFlag(mainTasks.get(0).getInteractive());
				obj.setDeployDate(mainTasks.get(0).getDeployDate());
				obj.setDuration(mainTasks.get(0).getDuration());
				obj.setStatInfo(mainTasks.get(0).getStatInfo());
				obj.setAutoPurgeLevels(mainTasks.get(0).getAutoPurgeLevels());
				obj.setLastRun(mainTasks.get(0).getLastRun());

				obj.setUprocHeader(false);
				// obj.setOriginNode(currentMainTask.getOriginNode());
				obj.setFlagAdvance(mainTasks.get(0).isFlagAdvance());
				obj.setAdvanceDays(mainTasks.get(0).getAdvanceDays());
				obj.setAdvanceHours(mainTasks.get(0).getAdvanceHours());
				obj.setAdvanceMinutes(mainTasks.get(0).getAdvanceMinutes());
				obj.setMuTZOffset(mainTasks.get(0).getMuTZOffset());

				obj.setParentTaskMu(mainTasks.get(0).getMuName());
				obj.setParentTaskName(mainTasks.get(0).getIdentifier().getName());
				obj.setParentTaskVersion(mainTasks.get(0).getParentTaskVersion());
				obj.setParentTaskMuNode(mainTasks.get(0).getParentTaskMuNode());
				obj.setTimeLimit(mainTasks.get(0).getTimeLimit());

				obj.setImpl(new OwlsTaskImpl());
				
				obj.create();
				System.out.println("TASK  [" + oTskName+ "] ON NODE=[" + this.getConnName() + "] ---> OK");
				
				tsks.put(obj.getIdentifier().getName(),obj);
				
			} else {

				System.out.println("Skipping [OPT TSK] "
								+ oTskName
								+ " on TARGET ["
								+ this.getConnName()
								+ "] : Missing [MAIN TSK] and/or Naming Convention not respected");

			}
       	 
       	
       } catch (SyntaxException e) {
           e.printStackTrace (System.out);
       } catch (UniverseException e) {
           e.printStackTrace (System.out);
       }
   }
   
   
   
   
   
   
   public void createMissingOptionalTask (String mtskName,String uprocName,String rule) throws Exception {
       try {
       	
       	String musnippet;
    	
   		String oTskName = uprocName+"_OPT_TASK";

		if (oTskName.length() > TSKNAME_LIMIT) 
		{

			oTskName=oTskName.substring(TSKNAME_LIMIT);
		}

       	if(!tsks.containsKey(mtskName))
       	{
				System.out.println("Skipping [OPT TSK] "
								+ oTskName
								+ " on TARGET ["
								+ this.getConnName()
								+ "] : [MAIN TSK]"+mtskName+" does not exist");
				return;
       	}
       	else
       	{
       		
				
       		musnippet=tsks.get(mtskName).getMuName();
       	
			
       	}// limit the label length to 64 characters

		if (this.taskAlreadyExists(oTskName)) {
				return;
			}

			if (this.taskAlreadyExists(mtskName)
					&& NamingUtils.doesTaskGoOnNode(musnippet, this)) {
				
				Task main = tsks.get(mtskName);

							
				if(main.getTaskType().equals(TaskType.Provoked))
				{
					System.out.println("[MAIN TSK] " + mtskName
							+ " is PROVOKED. Skipping [OPT TSK] "
							+ oTskName + " on " + this.getConnName()
							+". [MAIN TSK] "+mtskName+" needs to be SCHEDULED with RULE ["+rule+"]****");

					return;
				}

				Task obj = new Task(getContext(), TaskId.createWithName(
						oTskName, main.getIdentifier().getVersion(),
						musnippet, main.isTemplate()));
				obj.getIdentifier().setSyntaxRules(
						OwlsSyntaxRules.getInstance());

				/* optional task */
				TaskPlanifiedData tpd = new TaskPlanifiedData();
				tpd.setGenerateEvent(true);
				tpd.setOptional(true);

				TaskPlanifiedData main_tpd = main.getPlanifiedData();

				if (main_tpd == null) {
					System.out.println("[MAIN TSK] " + mtskName
							+ " has WRONG scheduling data. Skipping [OPT TSK] "
							+ oTskName + " on " + this.getConnName());

					return;
				}

				tpd.setLaunchHourPatterns(main_tpd.getLaunchHourPatterns());

				Rule rule1;

				if (!this.ruleAlreadyExists(rule)) {
					this.createRule(rule);
				}

				
				
				
				
				rule1 = this.getRule(rule);


				//////
				
				TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
		        
		        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
		        tid1.setLabel(rule1.getLabel());
		        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
		        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
		        
		        tid1.setPeriodType (rule1.getPeriodType ());
		        tid1.setPeriodNumber(rule1.getPeriodNumber());
		        tid1.setPattern (rule1.getPattern ());
		        tid1.setAuthorized (true);
		        final Date date1 = DateTools.toDate ("20140101");
		        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
		        Calendar calendar1 = DateTools.getCalendarInstance();
		        calendar1.setTime(date1);
		        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
		        tid1.setApplicationWeek(weekNumber1.toString());
		        
		        tid1.setLabel(rule1.getLabel());
		        tid1.setInternal(true);
		      
		        TaskImplicitData[] implicitDataArray = new TaskImplicitData[] {tid1};
		        tpd.setImplicitData (implicitDataArray);
		        
		        obj.setSpecificData(tpd);
				//////

				Uproc currentUpr=null;
				
				if(uprs.containsKey(uprocName)){

					currentUpr = uprs.get(uprocName);
					
				}
				else
				{
					System.out.println("Error : [OPT TSK] can not be created for "+uprocName);
					return;
				}
				
				
				
				// uproc info
				//obj.setUprocId(currentUpr.getIdentifier().toString());
				obj.setUprocName(currentUpr.getIdentifier().getName());
				obj.getIdentifier().setUprocVersion(currentUpr.getIdentifier().getVersion());

				// MU info
				// obj.setMuId(currentMainTask.getMuId());
				obj.setMuName(musnippet);

				// Session info
				// obj.setSessionId(currentSession.getId());
				obj.setSessionName(main.getSessionName());
				obj.getIdentifier().setSessionVersion(main.getSessionVersion());

				// Task info
				obj.setTaskType(TaskType.Optional);
				obj.setAutoRestart(false);
				obj.setEndExecutionForced(false);
				obj.setCentral(main.isCentral());
				obj.setActive(main.isActive());
				obj.setUserName(main.getUserName());
				obj.setLabel("Optional task");
				obj.setPriority(main.getPriority());
				obj.setQueue(main.getQueue());
				obj.setFunctionalPeriod(main.getFunctionalPeriod());
				// obj.setUserId(currentMainTask.getUserId());
				obj.setParallelLaunch(false);

				obj.setTypeDayOffset(main.getTypeDayOffset());
				obj.setPrinter(main.getPrinter());

				obj.setVariables(main.getVariables());
				obj.setPrinter(main.getPrinter());

				obj.setDayOffset(main.getDayOffset());
				obj.setUnitOffset(main.getUnitOffset());
				obj.setSimulated(main.isSimulated());
				obj.setValidFrom(main.getValidFrom());
				obj.setValidTo(main.getValidTo());

				obj.setDeploy(false);
				obj.setUpdate(false);
				obj.setInteractiveFlag(main.getInteractive());
				obj.setDeployDate(main.getDeployDate());
				obj.setDuration(main.getDuration());
				obj.setStatInfo(main.getStatInfo());
				obj.setAutoPurgeLevels(main.getAutoPurgeLevels());
				obj.setLastRun(main.getLastRun());

				obj.setUprocHeader(false);
				// obj.setOriginNode(currentMainTask.getOriginNode());
				obj.setFlagAdvance(main.isFlagAdvance());
				obj.setAdvanceDays(main.getAdvanceDays());
				obj.setAdvanceHours(main.getAdvanceHours());
				obj.setAdvanceMinutes(main.getAdvanceMinutes());
				obj.setMuTZOffset(main.getMuTZOffset());

				obj.setParentTaskMu(main.getMuName());
				obj.setParentTaskName(main.getIdentifier().getName());
				obj.setParentTaskVersion(main.getParentTaskVersion());
				obj.setParentTaskMuNode(main.getParentTaskMuNode());
				obj.setTimeLimit(main.getTimeLimit());

				obj.setImpl(new OwlsTaskImpl());
				System.out.print("[OPT TSK] " + obj.getIdentifier().getName()
						+ " about to be created on [" + this.getConnName()
						+ "] : ");
				obj.create();
				System.out.println("[OPT TSK]  [" + oTskName
						+ "] ON NODE=[" + this.getConnName() + "] ---> OK");

			} else {

				System.out.println();
				System.out
						.println("Skipping [OPT TSK] "
								+ oTskName
								+ " on TARGET ["
								+ this.getConnName()
								+ "] : Missing [MAIN TSK] and/or Naming Convention not respected");

			}
       	
       	
       } catch (SyntaxException e) {
           e.printStackTrace (System.out);
       } catch (UniverseException e) {
           e.printStackTrace (System.out);
       }
   }
   
   
   protected  void insertTechUproc(SessionAtom atom,String currentUprName,String nameOfTechUproc)
   {
	   	if (atom.getData()!=null ) 
	   	{
	           
	   				if (atom.getData().getUprocName().equalsIgnoreCase(currentUprName))
	   				{
	   					try {
	   						 
	   							SessionAtom techUproc = new SessionAtom(new SessionData(nameOfTechUproc));
	   							SessionAtom currentParent = atom.getParent();
	   							SessionAtom currentNextSibling = atom.getNextSibling();
	   							SessionAtom currentPreviousSibling=atom.getPreviousSibling();
	   							
	   								atom.setParent(techUproc);
	   							
	   								if(currentParent!=null)
	   								{		
	   									currentParent.setChildOk(techUproc);
	   								}
	   							techUproc.setChildOk(atom);
	   							techUproc.setNextSibling(atom.getNextSibling());//techUproc.setNextSibling(atom.getNextSibling());
	   							techUproc.setPreviousSibling(atom.getPreviousSibling());
	   							if(currentPreviousSibling !=null)
	   							{
	   								currentPreviousSibling.setNextSibling(techUproc);
	   							}
	   							if(currentNextSibling!=null)
	   							{
		   							currentNextSibling.setPreviousSibling(techUproc);

	   							}
	   							atom.setNextSibling(null);
	   							atom.setPreviousSibling(null);
	   							
	   						}
	   					catch (Exception e) {
	   						e.printStackTrace();
	   					}
	   				}
	   	}
   }
   protected  void updateName(SessionAtom atom,String currentUprName,String newUprName)
   {
	   	if (atom.getData()!=null && !currentUprName.trim().isEmpty() && !newUprName.trim().isEmpty()) 
	   	{
	           
	   				if (atom.getData().getUprocName().equalsIgnoreCase(currentUprName))
	   				{
	   					try {
	   						 
	   							
	   							atom.getData().setUprocName(newUprName);
	   							
	   						}
	   					catch (Exception e) {
	   						e.printStackTrace();
	   					}
	   				}
	   	}
   }
   protected  void updateSessionAtomName(SessionAtom atom,HashMap<String,String>old_new)
   {//developed for StateOfDE
	   	if (atom.getData()!=null)
	   	{
	           
	   				if (old_new.containsKey(atom.getData().getUprocName()))
	   				{
	   					try {
	   						 
	   							
	   							atom.getData().setUprocName(old_new.get(atom.getData().getUprocName()));
	   							System.out.println("SessionAtom <"+atom.getData().getUprocName()+"> renamed to <"+old_new.get(atom.getData().getUprocName()+">"));

	   						}
	   					catch (Exception e) {
	   						e.printStackTrace();
	   					}
	   				}
	   				
	   				
	   	}
   }
   
	////////////////////////////////	
	
	/*private void addSessionChildren(String sessionName, String parentName, SessionAtom parentAtom, SessionModel sm, ArrayList<String> processed) throws Exception {
		SessionAtom lastAtomOk = null;
		
		Collection<String> list = sm.getChildren(parentName);
		if (list!=null)
			for (String child : list) {			
				if (processed.contains(child))
					continue;
				else
					processed.add(child);
				SessionAtom atom = null;
				if (!sm.getUprs().contains(child)) {
					System.out.println("WARNING: skipping - uproc " + parentName + " child " + child + " not found in session");
					continue;
				}

				
				atom = new SessionAtom(new SessionData(child));
	
				if (lastAtomOk == null)
					parentAtom.setChildOk(atom);
				else
					lastAtomOk.setNextSibling(atom);
				lastAtomOk = atom;
	
				addSessionChildren(sessionName, child, atom, sm, processed);
			}
	}*/
	
	
	
    protected ItemList<TaskItem> initList(String TaskNameFilter1,String SessionNameFilter,String muNameFilter) {
        /* pattern for task name  */

        String name =TaskNameFilter1;
        String muName =muNameFilter;
        String userName = "*";
        String label = "*";
        String uprocName = "*"; 
        String sessionName =SessionNameFilter;
        String queue = "*";
        String version = "*";
        Boolean template = null;
        TaskType type = null;
        
        TaskFilter filter = new TaskFilter(name, muName, userName, label,uprocName, sessionName, queue);
        //System.out.println("Pulling list of TASKS with NAME="+name+" MU="+muName+" USERNAME="+userName+" LABEL="+label+" UPROCNAME="+uprocName+" SESSION="+sessionName+" QUEUE="+queue);
        //System.out.println();
        filter.setVersion(version);
        filter.setTemplate(template);
        filter.setType(type);
        
        filter.setParentTaskMu("*");
        filter.setParentTask("*");
        TaskList list;
        try {
        list = new TaskList(getContext(), filter);
        list.setContext(getContext());
        list.setImpl(new OwlsTaskListImpl());
        list.extract();
        return list;
        
    } catch (SyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UniverseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    return null;
    }

    public static EOwlsTaskType convertToMsgType(TaskType type) {
        EOwlsTaskType returnEOwlsTaskType = null;

        if (type != null) {
            if (TaskType.Scheduled.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_SCHEDULED;
            } else if (TaskType.Provoked.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_PROVOKED;
            } else if (TaskType.Optional.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_OPTIONAL;
            } else if (TaskType.Cyclic.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_CYCLICAL;
            }
        }

        return returnEOwlsTaskType;
    }
    public static EOwlsTaskStatus retrieveTaskStatus(TaskItem obj) {
        EOwlsTaskStatus returnEOwlsTaskType = null;

        if (obj != null) {
            if (obj.isSimulated()) {
                returnEOwlsTaskType = EOwlsTaskStatus.TASK_STATUS_SIMULATED;
            } else if (obj.isActive()) {
                returnEOwlsTaskType = EOwlsTaskStatus.TASK_STATUS_ACTIVE;
            } else {
                returnEOwlsTaskType = EOwlsTaskStatus.TASK_STATUS_DISABLED;
            }
        }

        return returnEOwlsTaskType;
    }
    
    public void deploy_to_MU(Task obj) throws UniverseException, Exception
    {
    	if(obj.isTemplate() && (this.getTaskByName(obj.getIdentifier().getName(),true) != null))
    	{
		    	if(this.getTaskByName(obj.getIdentifier().getName(),false)==null)
		    	{// if non template version does not exists , create it
			        Task obj_todeploy = new Task (getContext (), obj.getIdentifier());
			        obj_todeploy.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
			        obj_todeploy.populate(obj);
			        obj_todeploy.setActive(true);
			        obj_todeploy.getIdentifier().setTemplate(false);
			        obj_todeploy.create();
	    			System.out.println("TASK = ["+obj.getIdentifier().getName()+"] has been deployed to MU = ["+obj.getIdentifier().getMuName()+" on [TARGET] "+this.name+" ---> OK");
		    	} 
		    	else
		    	{
		    		
		    			System.out.println("TASK = ["+obj.getIdentifier().getName()+"] already exists on TARGET= ["+this.name+"] in NON-TEMPLATE format ...Skipping");
		
		    		
		    	}
    	}
    	else
    	{
    		System.out.println("[TASK] "+obj.getIdentifier().getName()+" does not exist on TARGET ["+this.name+"] ...Skipping");
    		
    				
    	}
    }
    public void createTask(Task obj) throws Exception
    {
    	if(!this.tskAlreadyExists(obj.getIdentifier().getName()))
    	{
        Task obj_tocreate = new Task (getContext (), obj.getIdentifier());
        obj_tocreate.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
        obj_tocreate.populate(obj);
        obj_tocreate.create();
    	}
    	else
    	{
    		System.out.println("[TASK] "+obj.getIdentifier().getName()+" already exists on TARGET  ["+this.name+"]...Skipping");
    	}
    	
    }
    

    
    public TaskList getTaskList() throws Exception {
		return getTaskListWithFilter(new TaskFilter());
	}
    public ArrayList<Task> getTaskArrayList() throws Exception {
  		TaskList list = getTaskListWithFilter(new TaskFilter());
  		ArrayList<Task>listTasks= new ArrayList<Task>();
  		
  		for(int i=0;i<list.getCount();i++)
  		{
  			TaskId id = list.get(i).getIdentifier();
    		Task obj = new Task(getContext(),id);
    		obj.setImpl(new OwlsTaskImpl());
			obj.extract();
			listTasks.add(obj);
  		}
  		return listTasks;
  	}
    public HashMap<String,Task> getTaskHashMap() throws Exception {
  		TaskList list = getTaskListWithFilter(new TaskFilter());
  		HashMap<String,Task>listTasks= new HashMap<String,Task>();
  		
  		for(int i=0;i<list.getCount();i++)
  		{
  			TaskId id = list.get(i).getIdentifier();
    		Task obj = new Task(getContext(),id);
    		obj.setImpl(new OwlsTaskImpl());
			obj.extract();
			listTasks.put(obj.getIdentifier().getName(),obj);
  		}
  		return listTasks;
  	}
    public Multimap<String, Task> getTaskMultiMap(String tskFilter) throws Exception {
  		
    	
    	 String name = "*";
        
    	 if(!tskFilter.isEmpty())
    	 {
    		 name="*"+tskFilter+"*";
    	 }
    	 String muName = "*";
         String userName = "*";
         String label = "*";
         String uprocName = "*"; 
         String sessionName = "*";
         String queue = "*";
         String version = "*";
         Boolean template = null;
         TaskType type = null;
         
         TaskFilter filter = new TaskFilter(name, muName, userName, label,
                 uprocName, sessionName, queue);
         filter.setVersion(version);
         filter.setTemplate(template);
         filter.setType(type);
         
         filter.setParentTaskMu("*");
         filter.setParentTask("*");
    	
    	
    	TaskList list = getTaskListWithFilter(filter);

  		Multimap<String, Task> listTasks = ArrayListMultimap.create();
  		
  		for(int i=0;i<list.getCount();i++)
  		{
  			TaskId id = list.get(i).getIdentifier();
    		Task obj = new Task(getContext(),id);
    		obj.setImpl(new OwlsTaskImpl());
			obj.extract();
			
			
			listTasks.put(obj.getIdentifier().getName(),obj);
  		}
  		return listTasks;
  	}
    
    public Multimap<String,Task> getTaskMultiMap_from_outside() throws Exception {
  		return tsks_multimap;
  	}
    public HashMap<String,Task> getTaskHashMap_from_outside() throws Exception {
  		return tsks;
  	}
    public TaskList getTaskListWithFilter(TaskFilter tf) throws Exception {
		TaskList list = new TaskList(getContext(), tf);
		list.setImpl(new OwlsTaskListImpl());
		list.extract();
		return list;
	}
    
      public Task getTaskByName(String name,boolean isTemplate) throws Exception {
	
    	TaskList list = getTaskList();
		for(int i=0;i<list.getCount();i++){
			TaskItem ent = list.get(i);
			ent.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			if(isTemplate){
				if(ent.getIdentifier().getName().equalsIgnoreCase(name) && ent.isTemplate()){
					TaskId id = ent.getIdentifier();
					Task u = new Task(getContext(), id);
					u.setImpl(new OwlsTaskImpl());
					u.extract();
					return u;
				}
			}else{
				if(ent.getIdentifier().getName().equalsIgnoreCase(name) && !ent.isTemplate()){
					TaskId id = ent.getIdentifier();
					Task u = new Task(getContext(), id);
					u.setImpl(new OwlsTaskImpl());
					u.extract();
					return u;
			}
		}
		}
		return null;
	}
    
    public void deleteTask(String taskname,String taskversion,String taskmu,boolean isTemplate) {

        try {
            String name = taskname;
            String version = taskversion;
            String muName = taskmu;

            
            Task obj = new Task(getContext(), TaskId.createWithName(name, version, muName, isTemplate));
            obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
            
            obj.setImpl(new OwlsTaskImpl());
            obj.delete();
            
        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
    
    
   
    
    
    public void transferTask(Task obj,Area destArea) {

        try {

            obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
            obj.setImpl(new OwlsTaskImpl());
            obj.transfer(destArea);
            System.out.println("[TASK] "+obj.getIdentifier().getName()+" has been transferred to "+destArea.toString());
            
        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
    
    
    public ItemList<RuleItem> getRuleList(String rulename) throws UniverseException
    {
    	String name ;
        String label = "*";
    	 
    	        /* pattern for rule name  */
    	        if(rulename==null || rulename.trim().isEmpty() || rulename.trim().equals("") || rulename.trim().equals("*"))
    	        {
    	        	name = "*";
    	        	
    	        }
    	        else
    	        {
    	        	name="*"+rulename+"*";
    	        }
    	        
    	        RuleFilter filter = new RuleFilter(name,label);

    	        RuleList list = new RuleList(getContext(), filter);
    	        list.setImpl(new OwlsRuleListImpl());
    	        list.extract();
    	        return list;
    	    
    }
    public Rule getRule(String rulename) throws UniverseException
    {
    	
    	
    	if(!rules.containsKey(rulename))
    	{
    		createRule(rulename);
    	}
    	
    	return rules.get(rulename);
    }
    
    public  HashMap<String,Rule> getRulesHashMap() throws UniverseException
    {
    	HashMap<String,Rule> result = new HashMap<String,Rule>();
    	
    	RuleFilter filter = new RuleFilter("*","*");

        RuleList list = new RuleList(getContext(), filter);
        list.setImpl(new OwlsRuleListImpl());
        list.extract();

        for(int r=0;r<list.getCount();r++)
        {
        	Rule obj = new Rule(getContext(),list.get(r).getIdentifier());
            obj.setImpl(new OwlsRuleImpl());
            obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
            obj.extract();
            result.put(obj.getName(), obj);
        }
        
        return result;
        
    }

    public void updateRuleOnOptionalTask(Task obj,String rule) throws UniverseException{
    	
    	String oldRule="";
    	Task currentTaskObj = new Task(getContext(), obj.getIdentifier());
    	currentTaskObj.setImpl(new OwlsTaskImpl());
        
    	currentTaskObj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
    	currentTaskObj.extract();

    	if(this.getRuleList(rule).getCount() == 0)
    	{
    		System.out.println("RULE = ["+rule+"] does not exists on "+this.getConnName()+" ... Creating it first...");
    		this.createRule(rule);
    	}
    	
    	Rule currentRuleObj= this.getRule(rule);

    	TaskPlanifiedData taskPlanifiedData =(TaskPlanifiedData) currentTaskObj.getSpecificData();
    	taskPlanifiedData.setGenerateEvent(true);
    	
        if (taskPlanifiedData.getImplicitData() != null && currentTaskObj.getTaskType().equals(TaskType.Optional)) 
        {
            
            for (int i = 0; i < taskPlanifiedData.getImplicitData().length; i++) 
            {
            	if(i==0)
            	{
            		oldRule=taskPlanifiedData.getImplicitData()[i].getName();
            		
            		if(!oldRule.equals(currentRuleObj.getName()))
            		{
            			taskPlanifiedData.getImplicitData()[i].setName(currentRuleObj.getName());
            			System.out.println("RULE updated on TASK =["+currentTaskObj.getIdentifier().getName()+"] from OLD VALUE ["+oldRule+"] to NEW VALUE ["+rule+"]");
            		}
            		else
            		{
            			continue;
            		}
	            	
	            	//taskPlanifiedData.getImplicitData()[i].update();
	                
            	}
            	else
            	{
            		taskPlanifiedData.getImplicitData()[i].delete();
            	}
              
            
            }
            currentTaskObj.setLabel("Optional task");
            currentTaskObj.setSpecificData (taskPlanifiedData);
            currentTaskObj.setImpl (new OwlsTaskImpl ());
            currentTaskObj.update ();
			System.out.println("TASK =["+currentTaskObj.getIdentifier().getName()+"] has been updated ---> [OK]");

        }
        else
        {
        	System.out.println("TASK = ["+currentTaskObj.getIdentifier().getName()+"] is being skipped : No rule or non-optional task...");
        }

    }

    
    public  void createRule(String rulename) throws UniverseException
    {
        
        String name = rulename.toUpperCase().trim();
        
        if(!rules.containsKey(name))
        {
        
        
        Rule obj = new Rule(getContext(), RuleId.create(name));
        
        String label = "THIS RULE NEEDS TO BE REVISED";
        obj.setLabel(label);

        obj.setPeriodNumber(1);
        obj.setPeriodType(PeriodTypeEnum.DAY);
        
        UniPattern pattern = new UniPattern();
        
        PositionsInPeriod positionsInPeriod = new PositionsInPeriod();
        positionsInPeriod.setPositionsPattern("1");
        positionsInPeriod.setForward(true);
        positionsInPeriod.setType(SubPeriodType.DAY);
        
        pattern.setPositionsInPeriod(positionsInPeriod);
        pattern.setRunOver(RunOverEnum.NO);//flag exit 'N'
        
        
        Hashtable<KDayType, KDayAuthorization> dayAuthorizations = new Hashtable<KDayType, KDayAuthorization>();

        KDayAuthorization openDay = new KDayAuthorization(KDayAuthorizationType.AUTHORIZED);
        KDayAuthorization closeDay = new KDayAuthorization(KDayAuthorizationType.AUTHORIZED);
        KDayAuthorization holiday = new KDayAuthorization(KDayAuthorizationType.AUTHORIZED);
        
        dayAuthorizations.put(KDayType.OPEN, openDay);
        dayAuthorizations.put(KDayType.CLOSED, closeDay);
        dayAuthorizations.put(KDayType.HOLIDAY, holiday);
        
        pattern.setDayAuthorizations(dayAuthorizations);
        
        MonthAuthorization monthAuthorization = new MonthAuthorization();
        monthAuthorization.setDirection(Direction.FROM_BEGINNING);
        for (int i = 0; i < 31; i++) {
            monthAuthorization.setAuthorization(i, true);
        }
        
        obj.setMonthAuthorization(monthAuthorization);
        
        YearAuthorization yearAuthorization = new YearAuthorization();
        for (int i = 0; i < 12; i++) {
            yearAuthorization.setAuthorization(i, true);
        }
        obj.setYearAuthorization(yearAuthorization);
        
        WeekAuthorization weekAuthorization = new WeekAuthorization();
        for (int i = 0; i < 7; i++) {
            weekAuthorization.setBlankDay(i, true);
        }
        for (int i = 0; i < 7; i++) {
            weekAuthorization.setClosedDay(i, true);
        }
        for (int i = 0; i < 7; i++) {
            weekAuthorization.setWorkedDay(i, true);
        }
        
        obj.setWeekAuthorization(weekAuthorization);
        
        com.orsyp.api.rule.Offset offset = new com.orsyp.api.rule.Offset();
        pattern.setOffset(offset);
       
        
        obj.setPattern(pattern);

        obj.setImpl(new OwlsRuleImpl());
        obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());

        obj.create();
        rules.put(obj.getName(), obj);
        System.out.println("Rule <"+obj.getName()+"> created");
        }
    }
    
    public ArrayList<String> getRuleListFromTask(Task obj)
    {
    	ArrayList<String> result = new ArrayList<String>();
    	
    	if(!obj.getTaskType().equals(TaskType.Provoked))
    	{
    	
             TaskPlanifiedData taskPlanifiedData =(TaskPlanifiedData) obj.getSpecificData();
    
                
            if (taskPlanifiedData.getImplicitData() != null && taskPlanifiedData.getImplicitData().length>=1) 
            {
            	for(int r=0;r<taskPlanifiedData.getImplicitData().length;r++)
            	{
            		TaskImplicitData taskImplicitData = taskPlanifiedData.getImplicitData()[r];
            		result.add(taskImplicitData.getName().toUpperCase());
            	}
                            
            }
    	

         
            
            
    	}
    	else
    	{
    		result.add("PROVOKED");
    	}
    	
    	return result;
    	
    }
    
    public String getLWFromTask(Task obj) throws UniverseException
    {
    	
                 TaskPlanifiedData taskPlanifiedData =(TaskPlanifiedData) obj.getSpecificData();
                
                 if (taskPlanifiedData.getLaunchHourPatterns() != null && taskPlanifiedData.getLaunchHourPatterns().length>=1) 
                 {
                	 	LaunchHourPattern curLp = taskPlanifiedData.getLaunchHourPatterns()[0];

                	 	String freq=Integer.toString(curLp.getFrequency());
                	 	String durH=Integer.toString(curLp.getDurationHour());
                	 	String durM=Integer.toString(curLp.getDurationMinute());
                	 	
                	 	String endTime=curLp.getEndTime();
                	 	String startTime= curLp.getStartTime().trim().substring(0,(curLp.getStartTime().trim().length()-2));
                	 	
                	 	if(curLp.getFrequency()<=99 && curLp.getFrequency()>=10 )
                	 	{
                	 		freq="0"+freq;
                	 	}
                	 	if(curLp.getFrequency()<10)
                	 	{
                	 		freq="00"+freq;
                	 	}
                	 	
                	 	if(curLp.getDurationHour()<=99 && curLp.getDurationHour()>=10)
                	 	{
                	 		durH="0"+durH;
                	 		
                	 	}
                	 	if(curLp.getDurationHour()<10)
                	 	{
                	 		durH="00"+durH;
                	 	}
                	 	
                	 
                	 	if(curLp.getDurationMinute()<10)
                	 	{
                	 		durM="0"+durM;
                	 	}
                	 	
                	 	
                	 	if(endTime==null)
                	 	{
                	 		endTime="0000";
                	 	}
                	 	if(startTime==null)
                	 	{
                	 		startTime="0000";
                	 	}
                	 	
                	 	return startTime+";"+endTime.substring(0,4)+";"+freq+";"+durH+";"+durM;
          
                 } 
                 else return null;
    	}
    	
    public ItemList<JobEventItem> getJobEventList() throws UniverseException {

        JobEventFilter filter = new JobEventFilter();

        JobEventList list = new JobEventList(getContext(), filter);
        list.setImpl(new OwlsJobEventListImpl());
        list.extract();
        return list;
    }
    public ArrayList<JobEvent> getJobEventsArrayList() throws UniverseException {

    	ArrayList<JobEvent> result = new ArrayList<JobEvent>();
        JobEventFilter filter = new JobEventFilter();

        JobEventList list = new JobEventList(getContext(), filter);
        list.setImpl(new OwlsJobEventListImpl());
        list.extract();
    	
        for(int i=0;i<list.getCount();i++)
    	{
        	JobEventItem obj_fromList = list.get(i);
        	JobEvent obj = new JobEvent(getContext(),obj_fromList.getIdentifier());

        	obj.setImpl(new OwlsJobEventImpl());
        	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());

        	obj.extract();

        	result.add(obj);
    	}
    	
    	return result;
    }
    public void createJobEvents_fromList(ItemList<JobEventItem> jobeventList) throws UniverseException
    {
    	for(int i=0;i<jobeventList.getCount();i++)
    	{
    		JobEvent obj_fromList = new JobEvent(getContext(),jobeventList.get(i).getIdentifier());
    		obj_fromList.extract();
    		
    		
                
    		obj_fromList.setImpl(new OwlsJobEventImpl());
    		obj_fromList.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
    		obj_fromList.create();
    	}
    	
    }
    
public UprocList getUprocList() throws Exception {  
		
        UprocFilter filter = new UprocFilter("*");
        UprocList list = new UprocList(getContext(), filter);
        
        list.setSyntaxRules(OwlsSyntaxRules.getInstance());
        OwlsUprocListImpl impl = new OwlsUprocListImpl();
        impl.init(list, Operation.DISPLAYLIST);
        list.setImpl(impl);
       // list.setImpl(new OwlsUprocListImpl());
        list.extract();
        return list;
	}

public ArrayList<Uproc> getUprocArrayList() throws Exception {  
	
    UprocFilter filter = new UprocFilter("*");
    UprocList list = new UprocList(getContext(), filter);
    
    list.setSyntaxRules(OwlsSyntaxRules.getInstance());
    OwlsUprocListImpl impl = new OwlsUprocListImpl();
    impl.init(list, Operation.DISPLAYLIST);
    list.setImpl(impl);
   // list.setImpl(new OwlsUprocListImpl());
    list.extract();
  
    
 ArrayList<Uproc> uprocs= new ArrayList<Uproc>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	UprocId id = list.get(i).getIdentifier();
    	Uproc obj = new Uproc(getContext(),id);
    	obj.setImpl(new OwlsUprocImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        uprocs.add(obj);
    	
    }
    return uprocs;
}
public HashMap<String,Uproc> getUprocHashMap(String uprfilter) throws Exception {  
	
    UprocFilter filter ;
    if(!uprfilter.isEmpty())
    {
    	filter = new UprocFilter("*"+uprfilter+"*");
    }
    else
    {
    	filter = new UprocFilter("*");
    }
    UprocList list = new UprocList(getContext(), filter);
    
    list.setSyntaxRules(OwlsSyntaxRules.getInstance());
    OwlsUprocListImpl impl = new OwlsUprocListImpl();
    impl.init(list, Operation.DISPLAYLIST);
    list.setImpl(impl);
   // list.setImpl(new OwlsUprocListImpl());
    list.extract();
  
    
 HashMap<String,Uproc> uprocs= new HashMap<String,Uproc>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	UprocId id = list.get(i).getIdentifier();//list.get(i).getId()
    	Uproc obj = new Uproc(getContext(),id);
    	obj.setImpl(new OwlsUprocImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        if(!uprocs.containsKey(obj.getName())){
        uprocs.put(obj.getName(),obj);}
    	
    }
    return uprocs;
}

public HashMap<String,Uproc> getUprocHashMap_from_outside() throws Exception {  
	
   
    return uprs;
}


public static void createInternalScript(Uproc obj,String[]lines) throws UniverseException {
    InternalScript data = new InternalScript(obj);
    
    data.setLines(lines);// put your script here

    obj.setInternalScript(data);
    obj.setSpecificData(data);
    //obj.update();
    data.save();
    //printf("Uproc [%s] => specific data created.\n", obj.getIdentifier()
     //       .getName());
}
public static String[] extractInternalScript(Uproc u) throws UniverseException {
    
    InternalScript script = new InternalScript(u);
    
    script.extractContent();
    //printf("Internal script extraction for Uproc [%s] :\n", u.getName());

    String[] lines = script.getLines();
    if (lines != null) {
        return lines;
        }
    else
    {
    	return new String[]{"Error copying script"};
    }
}
public ArrayList<String> getUprocsVariables(String variableStrFilter)
{
	ArrayList<String> result = new ArrayList<String>();
	
	for(String upKey:uprs.keySet())
	{
		Vector<Variable> curVec = uprs.get(upKey).getVariables();
		for(int v=0;v<curVec.size();v++)
		{
			if(curVec.get(v).getValue().contains(variableStrFilter))
			{
				result.add(upKey);
				break;
				
			}
		}
	}
	
	return result;
}

public void fixUprocVariablesAndScript(Uproc upr) throws UniverseException
{
	
	
	if(upr.getType().equalsIgnoreCase("CL_INT"))
	{
		String[] currentScriptLines =extractInternalScript(upr);
		
		for(int j=0;j<currentScriptLines.length;j++)
		{
			if(currentScriptLines[j].startsWith("M") 
					||currentScriptLines[j].startsWith("P"))
			{
				System.out.print("Fixing Uproc "+upr.getName()+" -- Replacing \""+currentScriptLines[j]+"\" with ");
				
				if(currentScriptLines[j].endsWith("y\"`"))
				{
					currentScriptLines[j]=currentScriptLines[j].replaceAll("y\"`", "d\"`");
					upr.setLabel("Fixed");

				}
				
				if(currentScriptLines[j].endsWith("m\"`"))
				{
					currentScriptLines[j]=currentScriptLines[j].replaceAll("m\"`", "d\"`");
					upr.setLabel("Fixed");
				}
				
				System.out.println(currentScriptLines[j]);
				System.out.println();

			}
		}
		
		Vector<Variable> variables = upr.getVariables();
		boolean containsVariable = false;
		String toInclude ="";
		
		if(variables.size()>1)
		{
			if(variables.get(1).getName().equals("COMMAND_PART2"))
			{
				
				
				if(variables.get(1).getValue().contains("M9OSJYYY"))
				{
					containsVariable = true;
					toInclude = "M9OSJYYY=`$UNI_DIR_EXEC/uxdat \"yyyymmdd\" $S_DATRAIT \"yy\" \"-9d\"`"; 
				}
				else if (variables.get(1).getValue().contains("M9OSDDD"))
				{
					containsVariable= true;
					toInclude = "M9OSDDD=`$UNI_DIR_EXEC/uxdat \"yyyymmdd\" $S_DATRAIT \"ddd\" \"-9d\"`";
				}
				else
				{
					toInclude="";
				}
				
			}
		}
		
		for(int j=0;j<currentScriptLines.length;j++)
		{
			if(currentScriptLines[j].contains("eval ${COMMAND_PART1}${COMMAND_PART2}"))
			{
				if(containsVariable)
				{
						
					currentScriptLines[j]=currentScriptLines[j].replace("eval ${COMMAND_PART1}${COMMAND_PART2}", toInclude+"\neval ${COMMAND_PART1}${COMMAND_PART2}");
				
					System.out.println("Fixing Uproc "+upr.getName()+" -- Adding \""+toInclude+"\"");
					System.out.println();
				}	
			
			}
			
		}
		
		createInternalScript(upr,currentScriptLines);
		upr.update();
		
		
	}
	
}




public void updateScript(Uproc obj,String a,String b) throws Exception
{
	if(getUprocHashMap_from_outside().containsKey(obj.getName()))
	{
		Uproc uproc = getUprocHashMap_from_outside().get(obj.getName());
		
		if(uproc.getType().equalsIgnoreCase("CL_INT")){
		
	
		String[] currentScriptLines =extractInternalScript(uproc);
	
		for(int j=0;j<currentScriptLines.length;j++)
	
		{
		
			//System.out.println( (j+1)+") Before : \""+currentScriptLines[j]+"\"");
			
		if(currentScriptLines[j].contains(a))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHH");
			String label = uproc.getLabel();
			uproc.setLabel(label+"_"+sdf.format(new Date()));
			uproc.update();
			currentScriptLines[j]=currentScriptLines[j].replace(a, b);
			System.out.println("UPR ["+uproc.getName()+"] : Replacing "+"\""+a+"\" with \""+b+"\"");

		}
		
		//System.out.println((j+1)+") After : \""+currentScriptLines[j]+"\"");

			
		
	}
	createInternalScript(uproc, currentScriptLines);
}
	}
}

public void renameTskNames(Task obj,String a,String b) throws Exception
{
	if(obj.getIdentifier().getName().contains(a))
	{
		
	String newname= (obj.getIdentifier().getName().replace(a, b)+"_"+obj.getMuName()).replace("(", "").replace(")","");
	System.out.println(" Renaming TASK ["+obj.getIdentifier().getName()+"] to ["+newname+"]");

	if(!tskAlreadyExists(newname))
	{
		
	Task newobj  = new Task (getContext (), TaskId.createWithName (newname, obj.getIdentifier().getVersion(), obj.getMuName(), obj.isTemplate()));
	newobj.populate(obj);
	
	newobj.setImpl(new OwlsTaskImpl());
	newobj.create();
	//tsks.put(newobj.getIdentifier().getName(),newobj);
	obj.delete();
	}
	}
	
}

public UprocList getHeaderUprocList() throws Exception {  
//example of how you can filter by "_H_" 	
    UprocFilter filter = new UprocFilter("*_H_*");
    UprocList list = new UprocList(getContext(), filter);
    
    list.setSyntaxRules(OwlsSyntaxRules.getInstance());
    OwlsUprocListImpl impl = new OwlsUprocListImpl();
    impl.init(list, Operation.DISPLAYLIST);
    list.setImpl(impl);
    list.extract();
    return list;
}

public ArrayList<Session> getSessionsArrayList() throws Exception {  
    SessionFilter filter = new SessionFilter("*","*");
    SessionList list = new SessionList(getContext(), filter);
    list.setImpl(new OwlsSessionListImpl());
    list.extract();
    
    ArrayList<Session> sessions= new ArrayList<Session>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	SessionId id = list.get(i).getIdentifier();
    	Session obj = new Session(getContext(),id);
    	obj.setImpl(new OwlsSessionImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        sessions.add(obj);
    	
    }
    return sessions;
}
public HashMap<String,Session> getSessionsHashMap(String sesfilter) throws Exception {  
    SessionFilter filter ;
    if(!sesfilter.isEmpty())
    {
    	filter =  new SessionFilter("*","*"+sesfilter.toUpperCase()+"*");
    }
    else
    {
    	filter = new SessionFilter("*","*");
    }
    SessionList list = new SessionList(getContext(), filter);
    list.setImpl(new OwlsSessionListImpl());
    list.extract();
    
    HashMap<String,Session> sessions= new HashMap<String,Session>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	SessionId id = list.get(i).getIdentifier();
    	Session obj = new Session(getContext(),id);
    	obj.setImpl(new OwlsSessionImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        if(!sessions.containsKey(obj.getName())){
        sessions.put(obj.getName(),obj);}
    	
    }
    return sessions;
}

public HashMap<String,Session> getSessionsHashMap_from_outside() throws Exception {  
    
    return sess;
}



public Session getSession(SessionItem item) throws Exception{  
	SessionId id = item.getIdentifier();
	Session obj = new Session(getContext(),id);
	obj.setImpl(new OwlsSessionImpl());
	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
    obj.extract();
    return obj;
}

public HashMap<String,Mu> getMUsHashMap()
{
	HashMap<String,Mu> result = new HashMap<String,Mu>();
	 try {
	 MuFilter filter = new MuFilter("*", "*");  // filter on ID useless
     filter.setRequestOffset(false);
     MuList list = new MuList(getContext(), filter);
     OwlsMuListImpl impl = new OwlsMuListImpl();
     list.setImpl(impl);
    
		list.extract();
	
	for(int i=0;i<list.getCount();i++)
	{

         Mu obj = new Mu(getContext(), list.get(i).getIdentifier());
         obj.setImpl(new OwlsMuImpl());
         obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
         obj.extract();
         
         if(!result.containsKey(obj.getName()))
         {
        	 result.put(obj.getName(), obj);
         }
         
     } 

	 }catch (SyntaxException e) {
         e.printStackTrace(System.out);
     } catch (UniverseException e) {
         e.printStackTrace(System.out);
     }
	 
		return result;
	
}

public void delExistingOpt(String upr,String mu) throws UniverseException
{
	Set<String> taskKeys = new HashSet<String>(tsks.keySet());
	
	for(String tskKey:taskKeys)
	{
		if(tsks.get(tskKey).getTaskType().equals(TaskType.Optional) 
				&& tsks.get(tskKey).getUprocName().equalsIgnoreCase(upr)
				&& tsks.get(tskKey).getMuName().equalsIgnoreCase(mu))
		{
			tsks.get(tskKey).delete();
			tsks.remove(tskKey);
		}
	}
}

public ArrayList<String> getSessionsUprBelongsTo(String uprName)
{
	ArrayList<String> result = new ArrayList<String>();

	if(!uprs.containsKey(uprName))
	{
		result.add("ERROR_UPR_NOT_FOUND");
		return result;
	}
	
	
	for(String sesKey:sess.keySet())
	{
		for(int u=0;u<sess.get(sesKey).getUprocs().length;u++)
		{
			if(sess.get(sesKey).getUprocs()[u].equalsIgnoreCase(uprName))
			{
				result.add(sesKey);
			}
		}
	}
	
	return result;
	
}

public void globalRuleRefreshOnTask() throws UniverseException
{
	for(String tskKey:tsks.keySet())
	{
		updateRule(tsks.get(tskKey),getRule(tsks.get(tskKey)));

	}
}
public  void normalizeSchedules(String uprocname,ArrayList<String> listOfNewRules) throws UniverseException
{
	ArrayList<Task> table = new ArrayList<Task>(getOptionalTasksUprBelongsTo(tsks,uprocname));
	
	
	 for(int rule=0;rule<listOfNewRules.size();rule++)
	    {
	    	if(!this.ruleAlreadyExists(listOfNewRules.get(rule)))
	    	{
	    		
	    		createRule(listOfNewRules.get(rule).toUpperCase());
	    	}
	    }
	
	for(int t=0;t<table.size();t++)
	{
		
		if(table.get(t).getTaskType().equals(TaskType.Provoked))
		{
			System.out.println("Skipping rule update on [PROVOKED TSK] "+table.get(t).getIdentifier().getName());
			continue;
		}
		
	
	TaskPlanifiedData tpd  =(TaskPlanifiedData) table.get(t).getSpecificData();
   
   
    ArrayList<TaskImplicitData> listOfImplicitData =new ArrayList<TaskImplicitData>();    
    
    for(int r=0;r<listOfNewRules.size();r++)
    {	
        Rule rule1=getRule(listOfNewRules.get(r));
   
        TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
        
        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
        tid1.setLabel(rule1.getLabel());
        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
        
        tid1.setPeriodType (rule1.getPeriodType ());
        tid1.setPeriodNumber(rule1.getPeriodNumber());
        tid1.setPattern (rule1.getPattern ());
        tid1.setAuthorized (true);
        final Date date1 = DateTools.toDate ("20140101");
        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
        Calendar calendar1 = DateTools.getCalendarInstance();
        calendar1.setTime(date1);
        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
        tid1.setApplicationWeek(weekNumber1.toString());
        
        tid1.setLabel(rule1.getLabel());
        tid1.setInternal(true);
      
        listOfImplicitData.add(tid1);
	}
  
    TaskImplicitData[] implicitDataArray = new TaskImplicitData[listOfImplicitData.size()];
    listOfImplicitData.toArray(implicitDataArray);
    tpd.setImplicitData (implicitDataArray);
        
        table.get(t).setSpecificData(tpd);
        table.get(t).update();
        System.out.println("Task <"+table.get(t).getIdentifier().getName()+"> has been updated with rules "+listOfNewRules);
        
	}
}
public  void updateAllRulesOnTask(Task t,ArrayList<String> rule) throws UniverseException
{
	
	if(t.getTaskType().equals(TaskType.Provoked))
	{
		System.out.println("Skipping rule update on [PROVOKED TSK] "+t.getIdentifier().getName());
		return;
	}
		
	TaskPlanifiedData tpd  =(TaskPlanifiedData) t.getSpecificData();
	

	  for(int r=0;r<rule.size();r++)
	    {
	    	if(!this.ruleAlreadyExists(rule.get(r)))
	    	{
	    		
	    		createRule(rule.get(r));
	    	}
	    }
	   
	    ArrayList<TaskImplicitData> listOfImplicitData =new ArrayList<TaskImplicitData>();    
	    
	    for(int r=0;r<rule.size();r++)
	    {	
	        Rule rule1=getRule(rule.get(r));
	   
	        TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
	        
	        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
	        tid1.setLabel(rule1.getLabel());
	        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
	        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
	        
	        tid1.setPeriodType (rule1.getPeriodType ());
	        tid1.setPeriodNumber(rule1.getPeriodNumber());
	        tid1.setPattern (rule1.getPattern ());
	        tid1.setAuthorized (true);
	        final Date date1 = DateTools.toDate ("20140101");
	        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
	        Calendar calendar1 = DateTools.getCalendarInstance();
	        calendar1.setTime(date1);
	        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
	        tid1.setApplicationWeek(weekNumber1.toString());
	        
	        tid1.setLabel(rule1.getLabel());
	        tid1.setInternal(true);
	      
	        listOfImplicitData.add(tid1);
		}
	  
	    TaskImplicitData[] implicitDataArray = new TaskImplicitData[listOfImplicitData.size()];
	    listOfImplicitData.toArray(implicitDataArray);
	    tpd.setImplicitData (implicitDataArray);
	        
	    t.setSpecificData(tpd);
	    t.update();
	    
	    tsks.put(t.getIdentifier().getName(), t);
}
public  void updateRule(Task t,String rule) throws UniverseException
{
	
	if(t.getTaskType().equals(TaskType.Provoked))
	{
		System.out.println("Skipping rule update on [PROVOKED TSK] "+t.getIdentifier().getName());
		return;
	}
	
	String oldRule = getRule(t);
	
	TaskPlanifiedData tpd  =(TaskPlanifiedData) t.getSpecificData();
	

    Rule rule1;
    
    if(!this.ruleAlreadyExists(rule))
    { 
    	this.createRule(rule);
    }
   
    
    rule1=this.getRule(rule);

   
        TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
        
        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
        tid1.setLabel(rule1.getLabel());
        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
        
        tid1.setPeriodType (rule1.getPeriodType ());
        tid1.setPeriodNumber(rule1.getPeriodNumber());
        tid1.setPattern (rule1.getPattern ());
        tid1.setAuthorized (true);
        final Date date1 = DateTools.toDate ("20140101");
        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
        Calendar calendar1 = DateTools.getCalendarInstance();
        calendar1.setTime(date1);
        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
        tid1.setApplicationWeek(weekNumber1.toString());
        
        tid1.setLabel(rule1.getLabel());
        tid1.setInternal(true);
      
        TaskImplicitData[] implicitDataArray = new TaskImplicitData[] {tid1};
        tpd.setImplicitData (implicitDataArray);
        
        t.setSpecificData(tpd);
        t.update();
        
	System.out.println("Updated Rule [OPT TSK] "+t.getIdentifier().getName()+": "+oldRule+" --> "+getRule(t));

}

private  String getRule(Task tsk) {
	if (tsk.getTaskType().equals(TaskType.Provoked)) {
		return "Provoked";
	} else {
		TaskPlanifiedData taskPlanifiedData = (TaskPlanifiedData) tsk
				.getSpecificData();

		if (taskPlanifiedData.getImplicitData() != null) {
			if (taskPlanifiedData.getImplicitData().length > 0) {
				TaskImplicitData taskImplicitData = taskPlanifiedData
						.getImplicitData()[0];
				return taskImplicitData.getName();
			} else {
				return "RULE_READ_ERROR";

			}

		} else {

			return "RULE_READ_ERROR";

		}
	}
}
private  String getLaunchInfo(Task tsk) {
	if (tsk.getTaskType().equals(TaskType.Provoked)) {
		return "Provoked";
	} else {
		
		
       TaskPlanifiedData tpd = new TaskPlanifiedData ();
       
       
       tpd=(TaskPlanifiedData)tsk.getSpecificData();
       
        LaunchHourPattern[] launchHourPatterns =tpd.getLaunchHourPatterns() ;         
        //Start time;end time;every mmm;launch window in hours;launch window in minutes
    	//Hhmm;hhmm;mmm;hhh;mm
        //0715;1515;060;000;59
	       if(launchHourPatterns.length>0)
	       {
	    	   String start;
	    	   String end;
	    	   String freq;
	    	   String LW_hour;
	    	   String LW_min;
	    	   
	    	   start = launchHourPatterns[0].getStartTime().substring(0,4);
	    	   
	    	   if(launchHourPatterns[0].getEndTime()==null)
	    	   {
	    		   end="0000";
	    	   }
	    	   else
	    	   {
	    		   end=launchHourPatterns[0].getEndTime();
	    	   }
	    	   
	    	   if(launchHourPatterns[0].getFrequency()>99)
	    	   {
	    		   freq=Integer.toString(launchHourPatterns[0].getFrequency());
	    	   }
	    	   else if(launchHourPatterns[0].getFrequency()>9)
	    	   {
	    		   freq="0"+Integer.toString(launchHourPatterns[0].getFrequency());
	    	   }
	    	   else
	    	   {
	    		   freq="00"+Integer.toString(launchHourPatterns[0].getFrequency());
	    	   }
	    	   
	    	   if(launchHourPatterns[0].getDurationHour()>99)
	    	   {
	    		   LW_hour=Integer.toString(launchHourPatterns[0].getDurationHour());
	    	   }
	    	   else if(launchHourPatterns[0].getDurationHour()>9)
	    	   {
	    		   LW_hour="0"+Integer.toString(launchHourPatterns[0].getDurationHour());
	    	   }
	    	   else
	    	   {
	    		   LW_hour="00"+Integer.toString(launchHourPatterns[0].getDurationHour());
	    	   }
	    	   
	    	   
	    	   
	    	   
	    	   if(launchHourPatterns[0].getDurationMinute()>9)
	    	   {
	    		   LW_min=Integer.toString(launchHourPatterns[0].getDurationMinute());
	    	   }
	    	   else
	    	   {
	    		   LW_min="0"+Integer.toString(launchHourPatterns[0].getDurationMinute());
	    	   }
	    	  
	    	   
	    		  return  start
	    				  +";"+end
	    				  +";"+freq
	    				  +";"+LW_hour
	    				  +";"+LW_min;
	  
	       }
	       else
	       {
	    	   return "";
	       }
	}
}

public String getRULE_LW_MU_for_CSV(String upr)
{
	String rule="";
	String lw="";
	String mu="";
	
	for(String tskKey:tsks.keySet())
	{
		if(tsks.get(tskKey).getUprocName().equalsIgnoreCase(upr))
		{
			rule=getRule(tsks.get(tskKey));
			lw = getLaunchInfo(tsks.get(tskKey));
			mu+=tsks.get(tskKey).getMuName()+"|";
			
		}
	}
	
	return rule+","+lw+","+mu;
}

public ArrayList<ExecutionItem> getExecutionList() throws Exception{        
        ExecutionFilter filter = new ExecutionFilter();
        
        ExecutionList list = new ExecutionList(getContext(), filter);
        list.setImpl(new OwlsExecutionListImpl());
        list.extract();
        ArrayList<ExecutionItem> arrayList = new ArrayList<ExecutionItem>();
        for(int i=0;i<list.getCount();i++)
        {
        	ExecutionItem item=list.get(i);
        	arrayList.add(item);
        }
        
        return arrayList;
    }
public ArrayList<ExecutionItem> getExecutionList(String upr,ArrayList<ExecutionStatus> status_array) throws Exception{        
    
	HashMap <String,ExecutionItem> table = new HashMap<String,ExecutionItem>();
	
	ExecutionFilter filter = new ExecutionFilter();
  
    //filter.setStatuses(null);
    filter.setUprocName(upr);
    ExecutionList list = new ExecutionList(getContext(), filter);
    list.setImpl(new OwlsExecutionListImpl());
    list.extract();
    
    ArrayList<ExecutionItem> aborted_timeoverrun = new ArrayList<ExecutionItem>();

    
    for(int i=0;i<list.getCount();i++)
    {
    	ExecutionItem item=list.get(i);
    	
    	if(table.containsKey(item.getUprocName()))
    	{
    		if(item.getEndDate()!=null && table.get(item.getUprocName())!=null)
    		{
    			if(item.getEndDate().after(table.get(item.getUprocName()).getEndDate()))
    			{
        			        				
    					table.remove(item.getUprocName());
        				table.put(item.getUprocName(),item);
    			}//if the current execution is newer than the one we already have in the table, replace the old with the new 
    				
        			
    			
    		}
    	}
    	else
    	{
    		table.put(item.getUprocName(), item);
    	}
    	
    	
    }
    
    for(String key:table.keySet())
    {
    	//arrayList.add(table.get(key));
    	if(status_array.contains(table.get(key).getStatus()))
    		//||table.get(key).getStatus().equals(ExecutionStatus.TimeOverrun))
    	{
    		aborted_timeoverrun.add(table.get(key));
    	}
    }
   //System.out.println(aborted_timeoverrun.get(0).getNumlanc());
        return aborted_timeoverrun;
}

@SuppressWarnings("unused")
private void printExecution(ExecutionItem execution,PrintStream prtstm) {

    out.println();
    out.println(" ExecutionId");
    out.println("   Session : " + execution.getSessionName());
    out.println("   Uproc   : " + execution.getUprocName());
    out.println("   MU      : " + execution.getMuName());
    out.println("   Numsess : " + execution.getNumsess());
    out.println("   Numproc : " + execution.getNumproc());
    out.println();
    out.println(" Data");
    out.println("   Numlanc : " + execution.getNumlanc());
    out.println("   Status     : " +
                getStatus(execution.getStatus()));
    out.println("   Step       : " + execution.getStep());
    out.println("   Relaunched : " + execution.isRelaunched());
    out.println("   Begin date : " +
            sdfDate.format(execution.getBeginDate()));
    out.println("   Begin hour : " +
            sdfHour.format(execution.getBeginDate()));
    out.println("   End date   : " +
    	(execution.getEndDate() == null? ""
    		: sdfDate.format(execution.getEndDate())));
    out.println("   End hour   : " +
    	(execution.getEndDate() == null? ""
    		: sdfHour.format(execution.getEndDate())));
    out.println("   Begin date : " +
            sdfDate.format(execution.getBeginDate()));
    if (execution.getProcessingDate() != null 
            && !execution.getProcessingDate().equals("00000000")
            && !execution.getProcessingDate().equals("")) {
        out.println("   Proc. date : "
                + execution.getProcessingDate());
    } else {
        out.println("   NO Proc. date .");
    }

    out.println("   User       : " + execution.getUserName());
    out.println("   Author     : " + execution.getAuthorCode());

    out.println("   Queue      : " + execution.getQueue());
    out.println("   Priority   : " + execution.getPriority());
    out.println("   Num Entry  : " + execution.getEntry());
    out.println("   Uproc ver  : " + execution.getUprocVersion());
    out.println("   Sess. ver  : " + execution.getSessionVersion());
    out.println("   Info.      : " + execution.getInfo());
    out.println("   Severity   : " + execution.getSeverity());
    out.println("   Appli.     : " + execution.getApplication());

    out.println("   Sess. rank : " + execution.getRankInSession());
    out.println("   from task  : " + execution.isTaskOrigin());
    out.println("   task       : " + execution.getTaskName());
    out.println("   Task vers. : " + execution.getTaskVersion());
    out.println("   Domain   . : " + execution.getDomain());
    
    out.println("-------------------------------------------------------");
}

public static ArrayList<Session> getSessionsUprBelongsTo(HashMap<String, Session> sessions, String upr) {
	ArrayList<Session> matches = new ArrayList<Session>();

	for (String s : sessions.keySet()) {
		for (int u = 0; u < sessions.get(s).getUprocs().length; u++) {
			if (sessions.get(s).getUprocs()[u].equalsIgnoreCase(upr)) {
				matches.add(sessions.get(s));
			}
		}
	}

	return matches;
}

public  ArrayList<Task> getOptionalTasksUprBelongsTo(HashMap<String, Task> tasks, String upr) {
	ArrayList<Task> matches = new ArrayList<Task>();

	for (String t : tasks.keySet()) {
		{
			//if(tasks.get(t).getTaskType().equals(TaskType.Optional))
			{
		
				if (tasks.get(t).getUprocName().equalsIgnoreCase(upr))
				{
					matches.add(tasks.get(t));
				}
			}
		
		}
		
	}
	
	return matches;
}
public ArrayList<Task> getMainTasksUprBelongsTo(HashMap<String,Task>tasks,String upr)
{
	ArrayList<Task> matches = new ArrayList<Task>();

	
		ArrayList<String> sessionsUprBelongsTo = getSessionsUprBelongsTo(upr);
		
		for(int s=0;s<sessionsUprBelongsTo.size();s++)
		//if(sessionsUprBelongsTo.size()==1)
		{
			String headerUprocOnWhichMainTask=sess.get(sessionsUprBelongsTo.get(s)).getHeader();

			for(String t:tasks.keySet())
			{
				if (tasks.get(t).getSessionName().equalsIgnoreCase(sessionsUprBelongsTo.get(s)) && tasks.get(t).getUprocName().equalsIgnoreCase(headerUprocOnWhichMainTask))
					{
						matches.add(tasks.get(t));
	
					}
			}
		}
	
	
	return matches;
}

public  ArrayList<Task> getTasksUprBelongsTo(HashMap<String, Task> tasks, String upr) {
	ArrayList<Task> matches = new ArrayList<Task>();

	for (String t : tasks.keySet()) {
		{
			//if(tasks.get(t).getTaskType().equals(TaskType.Optional))
			{
		
				if (tasks.get(t).getUprocName().equalsIgnoreCase(upr))
				{
					matches.add(tasks.get(t));
				}
			}
		
		}
		
	}
	
	if(matches.size()==0)
	{
		ArrayList<String> sessionsUprBelongsTo = getSessionsUprBelongsTo(upr);
		
		for(int s=0;s<sessionsUprBelongsTo.size();s++)
		//if(sessionsUprBelongsTo.size()==1)
		{
			String headerUprocOnWhichMainTask=sess.get(sessionsUprBelongsTo.get(s)).getHeader();

			for(String t:tasks.keySet())
			{
				if (tasks.get(t).getSessionName().equalsIgnoreCase(sessionsUprBelongsTo.get(s)) && tasks.get(t).getUprocName().equalsIgnoreCase(headerUprocOnWhichMainTask))
					{
						matches.add(tasks.get(t));
	
					}
			}
		}
	}
	
	return matches;
}

public static String getStatus(ExecutionStatus status) {

    if (status == ExecutionStatus.Pending) {
        return "Pending";
    } else if (status == ExecutionStatus.Started) {
        return "Started";
    } else if (status == ExecutionStatus.Running) {
        return "Running";
    } else if (status == ExecutionStatus.CompletionInProgress) {
        return "Completion in progress";
    } else if (status == ExecutionStatus.Aborted) {
        return "Aborted";
    } else if (status == ExecutionStatus.TimeOverrun) {
        return "Time overrun";
    } else if (status == ExecutionStatus.Refused) {
        return "Refused";
    } else if (status == ExecutionStatus.Completed) {
        return "Completed";
    } else if (status == ExecutionStatus.EventWait) {
        return "Event wait";
    } else if (status == ExecutionStatus.Launching) {
        return "Launching";
    } else if (status == ExecutionStatus.Held) {
        return "Held";
    } else {
        return "???";
    }
}



    public void insertSessionAtom(Session sessname,String currentUprName,final String nameOfTechUproc)
    {
    	try {					    		   		
    	            Session obj = sessname;

    	    		SessionTree tree = obj.getTree();
    	    		
    	    		final String curUpr = currentUprName;
    	    			 
    	    		tree.scan(new AtomVisitor() 
    	    			{
    	    				public void handle(SessionAtom atom) 
    	    				{
    	    					insertTechUproc(atom,curUpr,nameOfTechUproc);
    	    				}
    	    			});
    	    			
    	    
    	    		obj.update();
    	    		
    	    		System.out.println(nameOfTechUproc+" has been inserted as a PARENT to \""+currentUprName+"\" in SESSION \""+sessname.getName()+"\"");
    	    	   	    		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}	
    	
    }
    
    public String getFatherUproc(String sessname,String currentUprName)
    {
    					
    			if(sess.containsKey(sessname)){
    				try {	
    				
    				 final ArrayList<String> result= new ArrayList<String>();
    	            Session obj = sess.get(sessname);

    	    		SessionTree tree = obj.getTree();
    	    		
    	    		final String curUpr = currentUprName;
    	    			 
    	    		tree.scan(new AtomVisitor() 
    	    			{
    	    				public void handle(SessionAtom atom) 
    	    				{
    	    					if(atom.getData()!=null)
    	    					{
    	    						if(atom.getData().getUprocName().equalsIgnoreCase(curUpr))
    	    						{
    	    							if(atom.getParent()!=null)
    	    							{
    	    								result.add(atom.getParent().getData().getUprocName());
    	    								
    	    							}
    	    						}
    	    					}
    	    				}
    	    			});
    	    			
    	    
    	    		if(result.size()>0)
    	    		{//System.out.println(currentUprName+" has father uproc  \""+result.get(0)+"\" in SESSION \""+sessname+"\"");
    	    		return result.get(0);
    	    		}
    	    		else
    	    		{
    	    			return "isHeader";
    	    		}
		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    			}
		return "N/A";
    	
    }
    
    public String getUprocDepsAndVariablesToString(String uproc,HashMap<String,String>dollaru_esp_uprocname_map)
   	{//this was developed for the state of DE
       	if(uprs.containsKey(uproc))
       	{
       		Uproc obj = uprs.get(uproc);
       		
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
   	
   	
   			String depcons = "";
   			
   			for(int i=0;i<deps.size();i++)
   			{
   				String mappedESPName="TBD";
   				if(dollaru_esp_uprocname_map.containsKey(deps.get(i).getUproc()))
   				{
   					mappedESPName=dollaru_esp_uprocname_map.get(deps.get(i).getUproc());
   				}
   				depcons+=deps.get(i).getNum()+":"+deps.get(i).getUproc()+"/"+mappedESPName+"|";
   				
   			}
   		
   			Vector<Variable> varia=obj.getVariables();
   			String variables = "";
   			
   			for(int v=0;v<varia.size();v++)
   			{
   				if(varia.get(v).getName().equalsIgnoreCase("COMMAND"))
   				{
   					variables+=varia.get(v).getName()+"="+varia.get(v).getValue()+"|";
   				}
   			}
   		    
   			return ","+depcons+","+variables;
   			
   		 }
       	
       	else
       	{
       		return ",UPR "+uproc+" not found on"+this.getConnName()+
       				",UPR "+uproc+" not found on"+this.getConnName();
       	}
   	
   	}
    
    
    
    private void updateDepConsName(String curUpr,HashMap<String,String> old_new) throws UniverseException
    {//developed for state of DE , takes into a mapping of old_new uproc names,
    	//checks if depcon is part of that hashmap and replaces it with its value (the new name)
    	if(uprs.containsKey(curUpr))
    	{
    		Uproc atUpr = uprs.get(curUpr);
    		
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(atUpr.getDependencyConditions());

       		for(int i=0;i<deps.size();i++)
   			{
   				if(old_new.containsKey(deps.get(i).getUproc()))
   				{
   		       		System.out.println(" DEPCON <"+deps.get(i).getUproc()+"> renamed to <"+old_new.get(deps.get(i).getUproc())+">");

   					deps.get(i).setUproc(old_new.get(deps.get(i).getUproc()));

   				}
   			}
       		
       		atUpr.setDependencyConditions(deps);
       		atUpr.update();
       		
       		
    	}
    }
    
    private void updateTaskWithNewUprocName(String taskName,HashMap<String,String> old_new) throws UniverseException
    {//developed for state of DE , takes into a mapping of old_new uproc names,
    	//checks if tasks is defined on one of the old uproc names, if found changes that uproc setting to
    	//new uproc at the task level . 
    	
    	if(tsks.containsKey(taskName))
    	{
    		Task atTask = tsks.get(taskName);
    		String atUpr=atTask.getUprocName();
       	

    	
    		if(old_new.containsKey(atUpr))
       		{
       			atTask.setUprocName(old_new.get(atUpr));
        		System.out.println("Task <"+atTask.getIdentifier().getName()+"> has been updated with uproc <"+old_new.get(atUpr)+">");
       		/*	String optTaskName= "OPT_TSK_"+old_new.get(atUpr).toUpperCase();
        		if(optTaskName.length()>64)
        		{
        			optTaskName=optTaskName.substring(0, 63);
        		}
        		
           		atTask.getIdentifier().setName(optTaskName);*/


       			
       		}
    	
       		atTask.update();
       		
       		
    	}
    }
    public void updateLWStartTimeOnOptTask(String tsk,String lwStart) throws UniverseException
    {//lwStart should have HHmm format (4 digits)
    	
    	if(tsks.containsKey(tsk))
    	{
    		if(tsks.get(tsk).getTaskType().equals(TaskType.Optional))
    		{
    			TaskPlanifiedData tpd = new TaskPlanifiedData ();
    		    
    			tpd=(TaskPlanifiedData)tsks.get(tsk).getSpecificData();
        
    			LaunchHourPattern[] launchHourPatterns =tpd.getLaunchHourPatterns() ;         

    		       if(launchHourPatterns.length>0)
    		       {
       	            launchHourPatterns[0].setStartTime(lwStart+"00");

    		       }
    	          
    		 
    	            tpd.setLaunchHourPatterns (launchHourPatterns);
    	            tsks.get(tsk).setSpecificData (tpd);
    	            tsks.get(tsk).update();
    	            
    			
    		};
    	}
    }
    public void updateOPT_TaskNameAdhoc() throws Exception
    {//developed for state of DE , makes sure the opt Tasks follow <OPT_TSK><UPROCNAME>. 
    	
    	for(String tskKey:tsks.keySet())
    	{
    		Task atTask = tsks.get(tskKey);
    		
    		if(atTask.getTaskType().equals(TaskType.Optional))
    		{
        		String atUpr = atTask.getUprocName();
        		String optTaskName= ("OPT_TSK_"+atUpr).replace("DECSS_", "").toUpperCase();
        		if(optTaskName.length()>64)
        		{
        			optTaskName=optTaskName.substring(0, 63);
        		}
        		        	  			
    	
    			
    		String newname= optTaskName;
    		System.out.println(" Renaming TASK ["+atTask.getIdentifier().getName()+"] to ["+newname+"]");

    		copyTask(atTask,newname);
    		
    		}	


       			
       	}
    	
       	
    }
    
    public void copyTask(Task task,String newName)  throws Exception{
    	System.out.println(newName);
    	Object O = SerializationUtils.clone(task);
    	if (O instanceof Task)
    	{
    	Task t = (Task)O;
        t.setContext(getContext());
        t.setImpl(new OwlsTaskImpl());           
        TaskId tId = TaskId.createWithName(newName, task.getIdentifier().getVersion(), task.getIdentifier().getMuName(), task.getIdentifier().isTemplate());
        tId.setSyntaxRules(OwlsSyntaxRules.getInstance());
        tId.setSessionName(task.getSessionName());
        tId.setSessionVersion(task.getSessionVersion());
        tId.setUprocName(task.getUprocName());
        tId.setUprocVersion(task.getUprocVersion());
        task.delete();
        t.setIdentifier(tId);
        t.create();
        }
  }


    
    private void updateUprocNameInSession(String sessname,HashMap<String,String>old_new)
    {//developed for state of DE, needs to update the uprocname in a session
    	try {					    		   		
    	            Session obj = this.getSession(sessname);

    	    		SessionTree tree = obj.getTree();
    	    		
    	    		final HashMap<String,String> oldUprocs_newUprocs = old_new;
    	    			 
    	    		tree.scan(new AtomVisitor() 
    	    			{
    	    				public void handle(SessionAtom atom) 
    	    				{
    	    					updateSessionAtomName(atom,oldUprocs_newUprocs);
    	    				}
    	    			});
    	    			
    	    
    	    
    	    		obj.update();
    	    	   	    		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}	
    	
    }
    public ArrayList<String> getListOfFathersForUproc(String uproc)
   	{//this was developed for the state of DE
    	
    	ArrayList<String> resultList = new ArrayList<String>();
    	
       	if(uprs.containsKey(uproc))
       	{
       		Uproc obj = uprs.get(uproc);
       		
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
   	
   			
   			for(int i=0;i<deps.size();i++)
   			{
   				resultList.add(deps.get(i).getUproc());
   			}
   		
       		ArrayList<String> sessions = new ArrayList<String> (getSessionsUprBelongsTo(uproc));

   			for(int j=0;j<sessions.size();j++)
   			{
   				resultList.add(getFatherUproc(sessions.get(j), uproc));
   			}
       	}   
   		
       	return resultList;
   			
   		
    
    
   	}
    public static ArrayList<String> getAllRulesFromTask(Task tsk) {
    	ArrayList<String> result=new ArrayList<String>();
    	if (tsk.getTaskType().equals(TaskType.Provoked)||tsk.getTaskType().equals(TaskType.Cyclic)) {
    		result.add("Provoked/Cyclic");
    	} else {
    		TaskPlanifiedData taskPlanifiedData = (TaskPlanifiedData) tsk
    				.getSpecificData();

    		if (taskPlanifiedData.getImplicitData() != null) {
    			if (taskPlanifiedData.getImplicitData().length > 0) {
    				
    				for(int r=0;r<taskPlanifiedData.getImplicitData().length;r++)
    				{
    				TaskImplicitData taskImplicitData = taskPlanifiedData
    						.getImplicitData()[r];
    				result.add(taskImplicitData.getName());
    				}
    				
    			} else {
    				result.add("RULE_READ_ERROR");

    			}

    		} else {

    			result.add("RULE_READ_ERROR");


    		}
    	}
    	
    	return result;
    }
    public ArrayList<String> getListOfRulesForUproc(String uproc)
    {//this was developed for the state of DE
    	ArrayList<String> resultList = new ArrayList<String>();
    	
    	ArrayList<Task>matchedTasksToUpr = getTasksUprBelongsTo(tsks,uproc);

    	for(int m=0;m<matchedTasksToUpr.size();m++)
    	{
    		for(int r=0;r<getAllRulesFromTask(matchedTasksToUpr.get(m)).size();r++)
    		{
    			resultList.add(getAllRulesFromTask(matchedTasksToUpr.get(m)).get(r));
    		}
    	}
    	
    	return resultList;
    	
    }
    public ArrayList<String> getListOfChildrenForUproc(String uproc)
   	{//this was developed for the state of DE
    	
    	ArrayList<String> resultList = new ArrayList<String>();
    	
       	if(uprs.containsKey(uproc))
       	{
       		for(String uprKey : uprs.keySet())
       		{
       			if (!uproc.equals(uprKey))
       			{
       				Uproc obj = uprs.get(uproc);
       	       		
       	       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
       	   	
       	   			
       	   			for(int i=0;i<deps.size();i++)
       	   			{
       	   				if(deps.get(i).getUproc().equals(uproc))
       	   				{
       	   					if(!resultList.contains(deps.get(i).getUproc()))
       	   					{
       	   						resultList.add(uprKey);
       	   					}
       	   				}
       	   			}

       	   		}
       			
       		}
       		
       		ArrayList<String> sessions = new ArrayList<String> (getSessionsUprBelongsTo(uproc));
   			
   			for(int j=0;j<sessions.size();j++)
   			{
   				resultList.addAll(getChildrenUproc(sessions.get(j), uproc));
   			}
       	}
       		return resultList;
       	
       		
       	
    
   	}
    public ArrayList<String> getChildrenUproc(String sessname,String currentUprName)
    { final ArrayList<String> result= new ArrayList<String>();
    					
    			if(sess.containsKey(sessname)){
    				try {	
    				
    	            Session obj = sess.get(sessname);

    	    		SessionTree tree = obj.getTree();
    	    		
    	    		final String curUpr = currentUprName;
    	    			 
    	    		tree.scan(new AtomVisitor() 
    	    			{
    	    				public void handle(SessionAtom atom) 
    	    				{
    	    					if(atom.getData()!=null)
    	    					{
    	    						if(atom.getData().getUprocName().equalsIgnoreCase(curUpr))
    	    						{
    	    							if(atom.getChildOk()!=null)
    	    							{
    	    								result.add(atom.getChildOk().getData().getUprocName());
    	    								SessionAtom currentAtom = atom.getChildOk().getNextSibling();
    	    								SessionAtom otherCurrentAtom = atom.getChildOk().getPreviousSibling();
    	    								
    	    								while(currentAtom!=null)
    	    								{
    	    									result.add(currentAtom.getData().getUprocName());
    	    									currentAtom=currentAtom.getNextSibling();
    	    								}
    	    								while(otherCurrentAtom!=null)
    	    								{
    	    									result.add(otherCurrentAtom.getData().getUprocName());
    	    									otherCurrentAtom=otherCurrentAtom.getPreviousSibling();
    	    								}
    	    							}
    	    							if(atom.getChildKo()!=null)
    	    							{
    	    								result.add(atom.getChildKo().getData().getUprocName());
    	    								SessionAtom currentAtom = atom.getChildKo().getNextSibling();
    	    								SessionAtom otherCurrentAtom = atom.getChildKo().getPreviousSibling();

    	    								while(currentAtom!=null)
    	    								{
    	    									result.add(currentAtom.getData().getUprocName());
    	    									currentAtom=currentAtom.getNextSibling();
    	    								}
    	    								while(otherCurrentAtom!=null)
    	    								{
    	    									result.add(otherCurrentAtom.getData().getUprocName());
    	    									otherCurrentAtom=otherCurrentAtom.getPreviousSibling();
    	    								}
    	    							}
    	    							
    	    							
    	    							
    	    						}
    	    					}
    	    				}
    	    			});
    	    			
    	    
		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    			}
    			
    			return result;
    	
    }
    
    public ArrayList<Job> uprocsToJob()
    {
    	ArrayList<Job> result = new ArrayList<Job>();
    	for(String uprKey:uprs.keySet())
    	{
    		result.add(getJob(uprKey));
    	}
    	return result;
    }
    public  Job getJob(String uprocName)
    {
    	Job result = new Job (uprocName);
    	if(uprs.containsKey(uprocName))
    	{
        	ArrayList<String> fathers = new ArrayList<String>(getListOfFathersForUproc(uprocName));
        	result.getFathers().addAll(fathers);
        	ArrayList<String> rules = new ArrayList<String>(getListOfRulesForUproc(uprocName));
        	result.getRules().addAll(rules);
        	ArrayList<String>children = new ArrayList<String>(getListOfChildrenForUproc(uprocName));
        	result.getChildren().addAll(children);
    	}	
        	return result;
    	
    }
    
    public void setDepConOnUproc(String upr,ArrayList<String> depConsList) throws Exception
	{
		
    	if(uprs.containsKey(upr))
    	{
    		SessionControl sessionControl = new SessionControl();
			sessionControl.setType(SessionControl.Type.SAME_SESSION_AND_EXECUTION);

			MuControl muControl= new MuControl();			
			muControl.setType (Type.SAME_MU);//constants for the dependency condition
    		
    		Uproc currentUproc = uprs.get(upr);
			Vector<DependencyCondition> dependencies = new Vector<DependencyCondition>();

			LaunchFormula lf = new LaunchFormula();
			String text;
    		
    		for(int d=0;d<depConsList.size();d++)
    		{
    			if(uprs.containsKey(depConsList.get(d)))
    			{
    				DependencyCondition dc = new DependencyCondition();
				    dc.setExpected(true);//expected is chosen
				    dc.setFatal(false);//fatal box is NOT checked
					dc.setUserControl(UserControl.ANY);//user is any
					dc.setFunctionalPeriod(FunctionalPeriod.Day);
					dc.setMuControl (muControl);
					dc.setSessionControl(sessionControl);
					
					dc.setNum(d+1);
					dc.setUproc(depConsList.get(d));
					dc.setStatus(Status.COMPLETED);
			        
					dependencies.add(dc);

			        if(d != (depConsList.size()-1))
					{						
			        	if((d+1)<10)
						{
							text = " =C0"+(d+1)+" AND";// OK
						}
						else 
						{
							text = " =C"+(d+1)+" AND";
						}
					}
					else
					{
						if((d+1)<10)
						{
							text = " =C0"+(d+1);// OK
						}
						else 
						{
							text = " =C"+(d+1);
						}
				       
					
			        }
					lf.appendText(text);

    			
    			}

			
    		}

				
    		currentUproc.setFormula(lf);
			currentUproc.setDependencyConditions(dependencies);
			currentUproc.update();	
					
			System.out.println("DEP added to UPROC : "+currentUproc.getName()+" ---> OK");

				
		}
		
			
			
		
	}
    public void setNonSimulOnUproc(String upr) throws Exception
	{
    	Uproc currentUproc;
    	if(uprs.containsKey(upr))
    	{
    		currentUproc=uprs.get(upr);
    	}
    	else
    	{
    		currentUproc=getUproc(upr);
    	}
    	LaunchFormula formula = new LaunchFormula ();
		Vector<NonSimultaneityCondition> nonSimCondVect = new Vector<NonSimultaneityCondition>();			
		NonSimultaneityCondition nonSimultaneityCondition = new NonSimultaneityCondition();
		nonSimultaneityCondition.setExpected(true);
		nonSimultaneityCondition.setFatal(false);
		nonSimultaneityCondition.setNum(1);
		nonSimultaneityCondition.setSameProcessingDate(true);
		nonSimultaneityCondition.setUproc(upr);
		MuControl mu2Control = new MuControl();
		mu2Control.setType( MuControl.Type.SAME_MU );
		mu2Control.setOneEnough(true);
		nonSimultaneityCondition.setMuControl( mu2Control );
		SessionControl sessionControl2 = new SessionControl();
		sessionControl2.setType(SessionControl.Type.ANY_SESSION);
		nonSimultaneityCondition.setSessionControl(sessionControl2);
		nonSimCondVect.add(nonSimultaneityCondition);
		currentUproc.setNonSimultaneityConditions(nonSimCondVect);
		
		formula.appendText("=C01");	
		currentUproc.setFormula(formula);
		currentUproc.update();
		uprs.put(upr, currentUproc);
	}
    protected  void updateExecutionContextOfSessionAtom(SessionAtom atom,String csvinput){
    	if (atom.getData()!=null && csvinput.contains("(")) {
            String trimmed = csvinput.substring(0,csvinput.indexOf("("));
    				if (atom.getData().getUprocName().equalsIgnoreCase(trimmed)) {
    					try {
    						 
    						atom.getData().getExecutionContext().setHDP(NamingUtils.getSessionHDP_fromCSV(csvinput));
    						//set the HDP on session atom depending on the content of the (\\)
    					}
    					catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    	}
    }
    
    public void createMainTask (String taskName,String rule,String lwsFromCSV) throws Exception {

        try {
        	
        	taskName=taskName.trim();
        	rule=rule.trim();
        	lwsFromCSV=lwsFromCSV.trim();
        	
        	String [] lwstks = lwsFromCSV.split("\\;");
        	
        	/*Start time;end time;every mmm;launch window in hours;launch window in minutes
        	Hhmm;hhmm;mmm;hhh;mm

        	Example:

        	Single launch window: run job at 21h15 with a launch window of 1h30 minutes:

        	2115;0000;000;001;30

        	Multiple launch windows: run job between 7h15 and 15h15 every hour and each run has a launch window of 59 minutes:

        	0715;1515;060;000;59*/

        	if(this.getTaskByName(taskName, false) != null || this.getTaskByName(taskName,true) != null)
        	{
        		System.out.println("TASK : "+taskName+" already exists on "+this.getConnName()+"...Skipping");
        		
        		return;
        		
        	}
        	
        	
        	if(lwstks.length!=5 || NamingUtils.getMUName_From_MAINTSK(taskName)==null ||NamingUtils.getSessionName_From_MAINTSK(taskName)==null)
        	{//input check , hhmm;hhmm;mmm;hhh;mm 
        		return;
        	}
        	
        	if (taskName.length()> TSKNAME_LIMIT)
	    	{
        		String [] tks = taskName.split("\\-");
        		
        		if(tks.length != 2)
        		{
        			taskName=taskName.substring(0,TSKNAME_LIMIT).trim();
            	}
        		else
        		{
        			
        			String truncated =tks[0].substring(0,tks[0].length()-(taskName.length()-TSKNAME_LIMIT));
        			taskName = taskName.replace(tks[0], truncated);
        		}
	    	}//limit the task name to 64 characters
        	
                	
        	
        	
        	String name =taskName;
            String version = defaultVersion;
            String muName = NamingUtils.getMUName_From_MAINTSK(taskName);
            boolean isTemplate = false;
            
            if(!this.tskAlreadyExists(taskName) && NamingUtils.doesTaskGoOnNode(muName, this))
            {
            
            Task obj = new Task (getContext (), TaskId.createWithName (name, version, muName, isTemplate));
            obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
            
           
            
            /* scheduled task */
            TaskPlanifiedData tpd = new TaskPlanifiedData ();
            tpd.setOptional(false);
            
            LaunchHourPattern[] launchHourPatterns = new LaunchHourPattern[] {         
                    
                    new LaunchHourPattern (lwstks[0]+"00", lwstks[1]+"00", Integer.parseInt(lwstks[4]), Integer.parseInt(lwstks[3]), Integer.parseInt(lwstks[2])), };
                    // 0715;1515;060;000;59 ; this means at 7:15  till 15:15 run every 60 minutes , and lw = 0h 59min ;
                    
            
            launchHourPatterns[0].setStartTime(lwstks[0]+"00");
            launchHourPatterns[0].setFrequency(Integer.parseInt(lwstks[2].substring(1)));
            launchHourPatterns[0].setEndTime(lwstks[1]+"00");
            launchHourPatterns[0].setDurationHour((Integer.parseInt(lwstks[3])));
            launchHourPatterns[0].setDurationMinute(Integer.parseInt(lwstks[4]));
            launchHourPatterns[0].setDurationSecond(0);
	            
            tpd.setLaunchHourPatterns (launchHourPatterns);
           
           
            Rule rule1;
            
            if(!this.ruleAlreadyExists(rule))
            { 
            	this.createRule(rule);
            }
           
            
            rule1=this.getRule(rule);
            

      
            	
 
            TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
            
            tid1.setFunctionalVersion(rule1.getFunctionalVersion());
            tid1.setLabel(rule1.getLabel());
            tid1.setMonthAuthorization(rule1.getMonthAuthorization());
            tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
            
            tid1.setPeriodType (rule1.getPeriodType ());
            tid1.setPeriodNumber(rule1.getPeriodNumber());
            tid1.setPattern (rule1.getPattern ());
            tid1.setAuthorized (true);
            final Date date1 = DateTools.toDate ("20140101");
            tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
            Calendar calendar1 = DateTools.getCalendarInstance();
            calendar1.setTime(date1);
            Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
            tid1.setApplicationWeek(weekNumber1.toString());
            
            tid1.setLabel(rule1.getLabel());
            tid1.setInternal(true);

                                
                TaskImplicitData[] implicitDataArray = new TaskImplicitData[] {tid1};
               
                                                    

                tpd.setImplicitData (implicitDataArray);
               
	            obj.setSpecificData (tpd);
	           
                     
                Uproc currentUpr = this.getUproc(taskName.substring(0,taskName.indexOf("-")));
                Session currentSession = this.getSession(NamingUtils.getSessionName_From_MAINTSK(taskName));
               
	            obj.getIdentifier().setSessionName(currentSession.getName());
	            obj.getIdentifier().setSessionVersion(currentSession.getVersion());
	            obj.getIdentifier().setUprocName(currentUpr.getName());
	            obj.getIdentifier ().setUprocVersion (currentUpr.getVersion());

	            obj.setLabel ("Main Task");
	            obj.setActive (false);
	            String subAccount = defaultSubmissionAccount;
	            obj.setUserName (subAccount);
	           
	            obj.setFunctionalPeriod (currentUpr.getFunctionalPeriod());
	                 obj.setTypeDayOffset (DayType.WORKING);
	            obj.setTaskType (TaskType.Scheduled);
	            obj.setPriority ("100");
	            obj.setQueue ("SYS_BATCH");
	            obj.setPrinter ("IMPR");

	            obj.setParallelLaunch(false);

	            obj.setImpl (new OwlsTaskImpl ());
	            obj.create ();
				System.out.println("MAIN TASK  ["+taskName+"] CREATED ON NODE=["+this.getConnName()+"] ---> OK");
				
            }
            else
            {
            	System.out.println();
           		System.out.println("MAIN TASK "+taskName+" on TARGET ["+this.getConnName()+"] : Already exists ...");
           		System.out.println();            }
        
        } catch (SyntaxException e) {
            e.printStackTrace (System.out);
        } catch (UniverseException e) {
            e.printStackTrace (System.out);
        }
    }
    public void createProvokedTask(String taskName) throws Exception
    {
    	 try {
         	
         	taskName=taskName.trim().toUpperCase();
         	


         	if(this.getTaskByName(taskName, false) != null || this.getTaskByName(taskName,true) != null)
         	{
         		System.out.println("TASK : "+taskName+" already exists on "+this.getConnName()+"...Skipping");
         		
         		return;
         		
         	}
         	
         	if(!this.sesAlreadyExists((NamingUtils.getSessionName_From_MAINTSK(taskName))))
         	{
         		System.out.println("Session "+NamingUtils.getSessionName_From_MAINTSK(taskName)+" does not exist.Can't create "+taskName);
         		
         		return;
         			
         	}
         	
         	
         	if( NamingUtils.getMUName_From_MAINTSK(taskName)==null ||NamingUtils.getSessionName_From_MAINTSK(taskName)==null)
         	{//input check , hhmm;hhmm;mmm;hhh;mm 
         		return;
         	}
         	
         	if (taskName.length()> TSKNAME_LIMIT)
 	    	{
         		String [] tks = taskName.split("\\-");
         		
         		if(tks.length != 2)
         		{
         			taskName=taskName.substring(0,TSKNAME_LIMIT).trim();
             	}
         		else
         		{
         			
         			String truncated =tks[0].substring(0,tks[0].length()-(taskName.length()-TSKNAME_LIMIT));
         			taskName = taskName.replace(tks[0], truncated);
         		}
 	    	}//limit the task name to 64 characters
         	
                 	
         	
         	
         		String name =taskName;
             String version = defaultVersion;
             String muName = NamingUtils.getMUName_From_MAINTSK(taskName);
             String sessionName=NamingUtils.getSessionName_From_MAINTSK(taskName);
             String headerName=this.getSessionsHashMap("").get(sessionName).getHeader();
             boolean isTemplate = false;
             
             
             Task obj = new Task (getContext (), TaskId.createWithName (name, version, muName, isTemplate));
             obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
             
            
             
           

            if(!this.tskAlreadyExists(taskName) )//&& NamingUtils.getToken_1_and_3_of_NODENAME(this.getConnName().substring(0,this.getConnName().indexOf("/"))).equals(NamingUtils.getToken_2_and_4_of_MU(taskName)))
            {
             	
                 obj.getIdentifier().setSessionName(sessionName);
                 obj.getIdentifier().setSessionVersion(version);
                 obj.getIdentifier().setUprocName(headerName);
                 obj.getIdentifier ().setUprocVersion (version);

                 obj.setLabel ("Provoked Task");
                 obj.setActive (true);
                 String subAccount = defaultSubmissionAccount;
                 obj.setUserName (subAccount);
                 FunctionalPeriod functionalPeriod = FunctionalPeriod.Day;
                 obj.setFunctionalPeriod (functionalPeriod);
                 obj.setTypeDayOffset (DayType.WORKING);
                 obj.setTaskType (TaskType.Provoked);
                 obj.setPriority ("001");
                 obj.setQueue ("SYS_BATCH");
                 obj.setPrinter ("IMPR");
                 
           /*      final VariableNumeric variableNumeric = new VariableNumeric ();
                 variableNumeric.setName ("test");
                 variableNumeric.setMin (1);
                 variableNumeric.setMax (10);
                 variableNumeric.setValue ("7");
                 variableNumeric.setOrigin ("P");
                 obj.setVariables (new ArrayList<Variable> (Arrays.asList (variableNumeric)));*/
                 
                 /* provoked task */
                 TaskProvokedData tpd = new TaskProvokedData ();
                 tpd.setStartLaunchTime (null);
                 obj.setSpecificData (tpd);

                 obj.setImpl (new OwlsTaskImpl ());
                 obj.create ();
         	
         	   

 				System.out.println("PROVOKED TASK  ["+taskName+"] CREATED ON NODE=["+this.getConnName()+"] ---> OK");
 				
             }
             else
             {
             	System.out.println();
            		System.out.println("PROVOKED TASK "+taskName+" on TARGET ["+this.getConnName()+"] : Already exists ...");
            		System.out.println();            }
         
         } catch (SyntaxException e) {
             e.printStackTrace (System.out);
         } catch (UniverseException e) {
             e.printStackTrace (System.out);
         }
    	
    }
    public void createProvokedTask(String taskName,String session,String mu,String user) throws Exception
    {
    	 try {
         	
         	taskName=taskName.trim().toUpperCase();
         	


         	if(this.getTaskByName(taskName, false) != null || this.getTaskByName(taskName,true) != null)
         	{
         		System.out.println("TASK : "+taskName+" already exists on "+this.getConnName()+"...Skipping");
         		
         		return;
         		
         	}
         	
         	if(!this.sesAlreadyExists(session))
         	{
         		System.out.println("Session "+session+" does not exist.Can't create "+taskName);
         		
         		return;
         			
         	}
         	
         	
         	List<String> mus = getMus(); 
    		List<String> users = getUsers();
    		if (mus.size()==0)
    			throw new Exception("No MU found");
    		if (users.size()==0)
    			throw new Exception("No user found");
    		String aMu = mus.get(0);
    		String aUser = users.get(0);
    		if (users.contains(user))
    			aUser = user;
    		
    		if(mus.contains(mu))
    			aMu=mu;
         	        	
                 	
         	
         	
         	 String name =taskName;
             String version = defaultVersion;
             String muName = aMu;
             String sessionName=session;
             String headerName=sess.get(session).getHeader();
             boolean isTemplate = false;
             
             
             Task obj = new Task (getContext (), TaskId.createWithName (name, version, muName, isTemplate));
             obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
             
            
             
           

            if(!this.tskAlreadyExists(taskName) )//&& NamingUtils.getToken_1_and_3_of_NODENAME(this.getConnName().substring(0,this.getConnName().indexOf("/"))).equals(NamingUtils.getToken_2_and_4_of_MU(taskName)))
            {
             	
                 obj.getIdentifier().setSessionName(sessionName);
                 obj.getIdentifier().setSessionVersion(version);
                 obj.getIdentifier().setUprocName(headerName);
                 obj.getIdentifier ().setUprocVersion (version);

                 obj.setLabel ("Provoked Task");
                 obj.setActive (true);
                 obj.setUserName (aUser);
                 FunctionalPeriod functionalPeriod = FunctionalPeriod.Day;
                 obj.setFunctionalPeriod (functionalPeriod);
                 obj.setTypeDayOffset (DayType.WORKING);
                 obj.setTaskType (TaskType.Provoked);
                 obj.setPriority ("001");
                 obj.setQueue ("SYS_BATCH");
                 obj.setPrinter ("IMPR");
                 
           /*      final VariableNumeric variableNumeric = new VariableNumeric ();
                 variableNumeric.setName ("test");
                 variableNumeric.setMin (1);
                 variableNumeric.setMax (10);
                 variableNumeric.setValue ("7");
                 variableNumeric.setOrigin ("P");
                 obj.setVariables (new ArrayList<Variable> (Arrays.asList (variableNumeric)));*/
                 
                 /* provoked task */
                 TaskProvokedData tpd = new TaskProvokedData ();
                 tpd.setStartLaunchTime (null);
                 obj.setSpecificData (tpd);

                 obj.setImpl (new OwlsTaskImpl ());
                 obj.create ();
         	
         	   

 				System.out.println("PROVOKED TASK  ["+taskName+"] CREATED ON NODE=["+this.getConnName()+"] ---> OK");
 				
             }
             else
             {
             	System.out.println();
            		System.out.println("PROVOKED TASK "+taskName+" on TARGET ["+this.getConnName()+"] : Already exists ...");
            		System.out.println();            }
         
         } catch (SyntaxException e) {
             e.printStackTrace (System.out);
         } catch (UniverseException e) {
             e.printStackTrace (System.out);
         }
    	
    }

    public void c (String taskName,String rule,String lwsFromCSV) throws Exception {

        try {
        	
        	String fullTaskName = taskName;
        	String [] lwstks = lwsFromCSV.split("\\;");
        	
        	String name=fullTaskName;
        	
        	/*Start time;end time;every mmm;launch window in hours;launch window in minutes
        	Hhmm;hhmm;mmm;hhh;mm

        	Example:

        	Single launch window: run job at 21h15 with a launch window of 1h30 minutes:

        	2115;0000;000;001;30

        	Multiple launch windows: run job between 7h15 and 15h15 every hour and each run has a launch window of 59 minutes:

        	0715;1515;060;000;59*/

        	if(this.getTaskByName(fullTaskName, false) != null || this.getTaskByName(fullTaskName,true) != null || this.tskAlreadyExists(fullTaskName))
        	{
        		System.out.println("TASK : "+fullTaskName+" already exists on "+this.getConnName()+"...Skipping");
        		
        		return;
        		
        	}
        	
        	
        	if(lwstks.length!=5 || NamingUtils.getUprocName_From_OPTTSK(fullTaskName)==null ||NamingUtils.getSessionName_From_OPTTSK(fullTaskName)==null || NamingUtils.getMUName_From_OPTTSK(fullTaskName)==null)
        	{//input check , hhmm;hhmm;mmm;hhh;mm 
        		return;
        	}
        	

        	if (fullTaskName.length()> TSKNAME_LIMIT)
	    	{
        		String [] tks = fullTaskName.split("\\-");
        		
        		if(tks.length != 3)
        		{
        			name=fullTaskName.substring(0,TSKNAME_LIMIT).trim();
            	}
        		else
        		{
        			
        			String truncated =tks[0].substring(0,tks[0].length()-(fullTaskName.length()-TSKNAME_LIMIT));
        			name = fullTaskName.replace(tks[0], truncated);
        		}
	    	}//limit the label length to 64 characters

        	if(this.tskAlreadyExists(fullTaskName))
        	{
        		System.out.println("OPT TASK : "+fullTaskName+" already exists on "+this.getConnName()+"...Skipping");
        		return;
        	}

        	
            String version = defaultVersion;
            String muName = NamingUtils.getMUName_From_OPTTSK(fullTaskName);
            boolean isTemplate = false;
            
            String mainTaskDerived = this.getMainTaskNameFromOptTsk(fullTaskName);

            if(mainTaskDerived!=null && this.tskAlreadyExists(mainTaskDerived) && NamingUtils.doesTaskGoOnNode(muName, this))
            {
            
            Task obj = new Task (getContext (), TaskId.createWithName (name, version, muName, isTemplate));
            obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
            
      
            
            /* optional task */
            TaskPlanifiedData tpd = new TaskPlanifiedData ();
            tpd.setGenerateEvent(true);
            tpd.setOptional(true);
            
            LaunchHourPattern[] launchHourPatterns = new LaunchHourPattern[] {         
                    
                    new LaunchHourPattern (lwstks[0]+"00", lwstks[1]+"00", Integer.parseInt(lwstks[4]), Integer.parseInt(lwstks[3]), Integer.parseInt(lwstks[2])), };
                    // 0715;1515;060;000;59 ; this means at 7:15  till 15:15 run every 60 minutes , and lw = 0h 59min ;
                    
            
            launchHourPatterns[0].setStartTime(lwstks[0]+"00");
            launchHourPatterns[0].setFrequency(Integer.parseInt(lwstks[2].substring(1)));
            launchHourPatterns[0].setEndTime(lwstks[1]+"00");
            launchHourPatterns[0].setDurationHour((Integer.parseInt(lwstks[3])));
            launchHourPatterns[0].setDurationMinute(Integer.parseInt(lwstks[4]));
            launchHourPatterns[0].setDurationSecond(0);
	            
            tpd.setLaunchHourPatterns (launchHourPatterns);
           
           
            Rule rule1;
            
            if(!this.ruleAlreadyExists(rule))
            { 
            	this.createRule(rule);
            }
           
            
            rule1=this.getRule(rule);

           
                TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
                
                tid1.setFunctionalVersion(rule1.getFunctionalVersion());
                tid1.setLabel(rule1.getLabel());
                tid1.setMonthAuthorization(rule1.getMonthAuthorization());
                tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
                
                tid1.setAuthorized (true);
                tid1.setPeriodType (rule1.getPeriodType ());
                tid1.setPattern (rule1.getPattern ());
                
                
                final Date date1 = DateTools.toDate ("20140707");
                
                tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
                Calendar calendar1 = DateTools.getCalendarInstance();
                calendar1.setTime(date1);
                Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
                tid1.setApplicationWeek(weekNumber1.toString());
                
                tid1.setLabel(rule1.getName());
                tid1.setPeriodNumber(1);
                tid1.setName(rule.trim());
            
                
                
                TaskImplicitData[] implicitDataArray = new TaskImplicitData[] {tid1,};
                tpd.setImplicitData (implicitDataArray);
                
                obj.setSpecificData(tpd);
                
               
                Uproc currentUpr = this.getUproc(NamingUtils.getUprocName_From_OPTTSK(fullTaskName));
                Task currentMainTask = this.getTaskByName(mainTaskDerived, false);
                Session currentSession = this.getSession(NamingUtils.getSessionName_From_OPTTSK(fullTaskName));
                
                //uproc info
                obj.setUprocId(currentUpr.getIdentifier().toString());
                obj.setUprocName(currentUpr.getIdentifier().getName());
                obj.getIdentifier().setUprocVersion(currentUpr.getIdentifier().getVersion());
                
          
                //MU info
                obj.setMuName(muName);

               //Session info
                //obj.setSessionId(currentSession.getId());
                obj.setSessionName(currentSession.getName());
                obj.getIdentifier().setSessionVersion(currentSession.getVersion());
                
                //Task info    
                obj.setTaskType(TaskType.Optional);
                obj.setAutoRestart(false);
                obj.setEndExecutionForced(false);
                obj.setCentral(currentMainTask.isCentral());
                obj.setActive(currentMainTask.isActive());
                obj.setUserName(defaultSubmissionAccount);
                obj.setLabel ("Optional task");
                obj.setPriority (currentMainTask.getPriority());
                obj.setQueue (currentMainTask.getQueue());
                obj.setFunctionalPeriod (currentMainTask.getFunctionalPeriod());
                //obj.setUserId(currentMainTask.getUserId());
                obj.setParallelLaunch(false);
              
                obj.setTypeDayOffset(currentMainTask.getTypeDayOffset());
                obj.setPrinter(currentMainTask.getPrinter());

                obj.setVariables(currentMainTask.getVariables());
                obj.setPrinter(currentMainTask.getPrinter());
                
                obj.setDayOffset(currentMainTask.getDayOffset());
                obj.setUnitOffset(currentMainTask.getUnitOffset());
                obj.setSimulated(currentMainTask.isSimulated());
                obj.setValidFrom(currentMainTask.getValidFrom());
                obj.setValidTo(currentMainTask.getValidTo());
               
                
                obj.setDeploy(false);
                obj.setUpdate(false);
                obj.setInteractiveFlag(currentMainTask.getInteractive());
                obj.setDeployDate(currentMainTask.getDeployDate());
                obj.setDuration(currentMainTask.getDuration());
                obj.setStatInfo(currentMainTask.getStatInfo());
                obj.setAutoPurgeLevels(currentMainTask.getAutoPurgeLevels());   
                obj.setLastRun(currentMainTask.getLastRun());   
          

                obj.setUprocHeader(false);
                //obj.setOriginNode(currentMainTask.getOriginNode());
                obj.setFlagAdvance(currentMainTask.isFlagAdvance());
               obj.setAdvanceDays(currentMainTask.getAdvanceDays());
               obj.setAdvanceHours(currentMainTask.getAdvanceHours());
                obj.setAdvanceMinutes(currentMainTask.getAdvanceMinutes());
                obj.setMuTZOffset(currentMainTask.getMuTZOffset());
                
                obj.setParentTaskMu(currentMainTask.getMuName());
                obj.setParentTaskName(currentMainTask.getIdentifier().getName());
                obj.setParentTaskVersion(currentMainTask.getParentTaskVersion());
                obj.setParentTaskMuNode(currentMainTask.getParentTaskMuNode());
                obj.setTimeLimit (currentMainTask.getTimeLimit ());
                
            	obj.setImpl (new OwlsTaskImpl ());
            	System.out.print("OPT TSK "+obj.getIdentifier().getName()+" about to be created on ["+this.getConnName()+"] ...");
            	obj.create ();
				System.out.println("---> OPT TSK  ["+fullTaskName+"] ON NODE=["+this.getConnName()+"] ---> OK");

            }
            else
            {
            	if(!this.tskAlreadyExists(mainTaskDerived))
            	{
            		System.out.println();
            		System.out.println("Skipping OPT TSK "+fullTaskName+" on TARGET ["+this.getConnName()+"] : Non-existent MainTask ["+mainTaskDerived+"]");
            		System.out.println();
            		
            	}
            	if(mainTaskDerived==null || !NamingUtils.getToken_1_and_3_of_NODENAME(this.getConnName().substring(0,this.getConnName().indexOf("/"))).equals(NamingUtils.getToken_2_and_4_of_MU(fullTaskName)))
            	{
            		System.out.println();
            		System.out.println("Skipping OPT TSK "+fullTaskName+" on TARGET ["+this.getConnName()+"] : Naming Convention not respected");
            		System.out.println();
            	}
            	
            }
        } catch (SyntaxException e) {
            e.printStackTrace (System.out);
        } catch (UniverseException e) {
            e.printStackTrace (System.out);
        }
    }

    public String getMainTaskNameFromOptTsk(String optionaltaskName) throws Exception
    {
    	return "aba";
    }
    public void createSession(String sessionName,HashMap<String,ArrayList<String>> $U_CHILDREN) throws Exception
    {
    	sessionName=sessionName.toUpperCase();
    	
    	if(sess.containsKey(sessionName))
    	{
    		sess.get(sessionName).delete();
    		sess.remove(sessionName);
    	}
    	SessionId sessionId = new SessionId(sessionName, defaultVersion);
		Session sess = new Session(getContext(), sessionId);		
		sess.setImpl(new OwlsSessionImpl());
		sess.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
		
		String header=sessionName+"_H";
		if(!this.uprocAlreadyExists(header))
		{
			this.createUProc(header, new String[]{"set resexe=0"});
		}
		
		SessionAtom root = new SessionAtom(new SessionData(header));
		
		HashMap<String,SessionAtom> allSessionAtoms = new HashMap<String,SessionAtom>();
		
		for(String keys:$U_CHILDREN.keySet())
		{
			keys=keys.toUpperCase();
			
			SessionAtom father_upr ;
			
			if(!allSessionAtoms.containsKey(keys))
			{
				father_upr= new SessionAtom(new SessionData(keys));
				allSessionAtoms.put(keys, father_upr);
				
			}

			father_upr=allSessionAtoms.get(keys);//the father is prepped up
			
			
			for(int c=0;c<$U_CHILDREN.get(keys).size();c++)
			{
				
				SessionAtom current_child ;
				String current_child_uproc = $U_CHILDREN.get(keys).get(c).toUpperCase();
				
				if(!allSessionAtoms.containsKey(current_child_uproc))
				{
					current_child= new SessionAtom(new SessionData(current_child_uproc));
					current_child.setParent(father_upr);
					allSessionAtoms.put(current_child_uproc, current_child);
					current_child=allSessionAtoms.get(current_child_uproc);
					
					if(father_upr.getChildOk()==null)
					{
						father_upr.setChildOk(current_child);
						
					}
					
				}

				current_child=allSessionAtoms.get(current_child_uproc);
				current_child.setParent(father_upr);
				
				if(c<$U_CHILDREN.get(keys).size()-1){
				SessionAtom nextSibling;
				
				String next_sibling_uproc = $U_CHILDREN.get(keys).get(c+1).toUpperCase();

				if(!allSessionAtoms.containsKey(next_sibling_uproc))
				{
					nextSibling= new SessionAtom(new SessionData(next_sibling_uproc));
					nextSibling.setParent(father_upr);
					nextSibling.setPreviousSibling(current_child);
					allSessionAtoms.put(next_sibling_uproc, nextSibling);
				}
				
				nextSibling = allSessionAtoms.get(next_sibling_uproc);
				
				current_child.setNextSibling(nextSibling);
				}
				
			}
			
			
			
		}
		
		String last_uprc = null ;

		for(String sesAtom:allSessionAtoms.keySet())
		{
			if(allSessionAtoms.get(sesAtom).getParent()==null)
			{
				allSessionAtoms.get(sesAtom).setParent(root);
				
				if(root.getChildOk()==null)
				{
					root.setChildOk(allSessionAtoms.get(sesAtom));
					last_uprc = sesAtom;
				}
				else
				{
					allSessionAtoms.get(last_uprc).setNextSibling(allSessionAtoms.get(sesAtom));
					allSessionAtoms.get(sesAtom).setPreviousSibling(allSessionAtoms.get(last_uprc));
					
				}
			}
		}
		
		
		sess.setTree(new SessionTree(root));
		sess.create();
		sess.extract();
		System.out.println("Session <"+sess.getName()+"> created");
		
		this.sess.put(sess.getName(),sess);
    }
  public void renameUprocEverywhere(HashMap<String,String> oldUpr_newUpr ) throws Exception  
    {   

			for(String uprocKey:uprs.keySet())
			{
				updateDepConsName(uprocKey, oldUpr_newUpr);
			}//update the depcons on all uprocs first
			
			for(String curUpr:oldUpr_newUpr.keySet())
			{
				if(uprs.containsKey(curUpr))
				{
					
					duplicateUproc(curUpr,oldUpr_newUpr.get(curUpr));
					/*uprs.get(curUpr).delete();
					uprs.remove(curUpr);*/
					
				}//duplicate old upr into new upr and delete the old one.
			}
			
			for(String sesKey:sess.keySet())
			{
				updateUprocNameInSession(sesKey, oldUpr_newUpr);
			}
			
			for(String tskKey : tsks.keySet())
			{
				updateTaskWithNewUprocName(tskKey, oldUpr_newUpr);
			}
			
	
	
}
  
    public void setMemorizationOnUproc(String upr) throws UniverseException
    {
    	if(uprs.containsKey(upr))
    	{
    		Uproc currentUproc=uprs.get(upr);
    		currentUproc.setFunctionalPeriod(FunctionalPeriod.Day);
            
            Memorization memo = new Memorization(Memorization.Type.ONE);
            memo.setNumber(1);
            currentUproc.setMemorization(memo);
            currentUproc.update();
            System.out.println("Memorization set on UPR "+currentUproc.getName()+" --> OK");
            
    	}
    }
    public void removeDepsFromUprocClean(String upr,ArrayList<String> depconHashToRemove) throws Exception
	{
		
    	if(uprs.containsKey(upr))
    	{
    		Uproc currentUproc = uprs.get(upr);
			Vector<DependencyCondition> curDependencies = currentUproc.getDependencyConditions();
			Vector<DependencyCondition> newDependencies = new Vector<DependencyCondition>();
			
			String currentLF = currentUproc.getFormula().getFormulaText();
			
			HashMap<String,String> nameDep_Cnumber = new HashMap<String,String>();
    		
			
			for(int i=0;i<curDependencies.size();i++)
			{			
				String cnum = null;
				
				if(curDependencies.get(i).getNum()<10)
				{
					cnum="=C0"+curDependencies.get(i).getNum();
				}
				else
				{
					cnum="=C"+curDependencies.get(i).getNum();
				}
				nameDep_Cnumber.put(curDependencies.get(i).getUproc(), cnum);
				
			}
					
			for(String depKey:depconHashToRemove)
			{
				if(nameDep_Cnumber.containsKey(depKey))
				{
					String toRemove=nameDep_Cnumber.get(depKey);
					
					if(currentLF.contains(" AND "+toRemove))
					{
						currentLF=currentLF.replaceAll(" AND "+toRemove, "");

					}
					else if (currentLF.contains(toRemove+" AND "))
					{
						currentLF=currentLF.replace(toRemove+" AND ","");
					}
					else
					{
						continue;
					}
			
				}
			}
			
			for(int c=0;c<curDependencies.size();c++)
			{
				if(!depconHashToRemove.contains(curDependencies.get(c).getUproc()))
				{
						newDependencies.add(curDependencies.get(c));
				}
						
			}
		
    		currentUproc.setDependencyConditions(newDependencies);
    		
    		LaunchFormula newFormula = new LaunchFormula();
    		     		
    		newFormula.appendText(currentLF.trim());
    		currentUproc.setFormula(newFormula);
    		currentUproc.update();

			
    	}
			
		
	}

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void removeDepsFromUproc(String upr,ArrayList<String> depconHashToRemove) throws Exception
	{
		
    	if(uprs.containsKey(upr))
    	{
    		Uproc currentUproc = uprs.get(upr);
			Vector<DependencyCondition> curDependencies = currentUproc.getDependencyConditions();
    		Vector<DependencyCondition> newDependencies = new Vector<DependencyCondition>();
			
    		ArrayList<String> curEntries = new ArrayList<String>();
    		ArrayList<String> newEntries = new ArrayList<String>();
			
			for(int i=0;i<curDependencies.size();i++)
			{
				String curDepName=curDependencies.get(i).getUproc();
				curEntries.add(curDepName);
				
				if(!depconHashToRemove.contains(curDepName))
				{
					newDependencies.add(curDependencies.get(i));//if not part of the dep-to-be-removed list, add it to the new depcons
					newEntries.add(curDepName);
				}
			}
			
		    Set<String> set1 = new HashSet<String>();
		    set1.addAll(curEntries);

		    Set<String> set2 = new HashSet<String>();
		    set2.addAll(newEntries);

		    set1.removeAll(set2);
			
			if(!set1.isEmpty())//if set1 is not Empty it means that we have found a booboo, action needs to be taken
			{
				//System.out.println("UPR <"+upr+"> has a new list of deps");
			
			
			LaunchFormula lf = new LaunchFormula();
			String text;
			int depNum;
    		
    		for(int d=0;d<newDependencies.size();d++)
    		{
    			
    			newDependencies.get(d).setNum(d+1);
    		}   
    	    			
   
    		for(int q=0;q<newDependencies.size();q++)
    		{
    			depNum=newDependencies.get(q).getNum();	
    					        
    					        if(q != (newDependencies.size()-1))
    							{					
    						        	if(depNum<10)
    									{
    										text = " =C0"+depNum+" AND";// OK
    									}
    									else 
    									{
    										text = " =C"+depNum+" AND";
    									}
    							}
    							else
    							{
    								if(depNum<10)
    								{
    									text = " =C0"+depNum;// OK
    								}
    								else 
    								{
    									text = " =C"+depNum;
    								}
    					       
    						
    				        		}
    					
    					lf.appendText(text);

    	    			
    	    }
    			
    			
    		currentUproc.setDependencyConditions(newDependencies);
    		currentUproc.setFormula(lf);
    		System.out.println("- "+currentUproc.getName()+" has "+set1+" removed");
    		currentUproc.update();			
			}
			else
			{//if no booboo found, set1 is empty. then just delete the uproc to only keep the affected list on the node for easier extraction/identification
				//currentUproc.delete();
			}
		}
		
			
			
		
	}

    public void addDepToUprocAdhoc(String upr,ArrayList<String>depconHashToAdd) throws UniverseException
    {//only adds the parts of deoconHashToAdd that upr does not have
    	Uproc uproc = null;
    	ArrayList<String> actualDepsToAdd = new ArrayList<String>();
    	
    	if(!doesUprocExist(upr))
    	{
    		System.out.println("@ Uproc "+upr+" does not exist on node");

    		return;
    	}
    	//System.out.println("About to set new dep on "+upr+" with "+depconHashToAdd);
    	if(uprs.containsKey(upr))
    	{
    		uproc = uprs.get(upr);
    	}
    	else
    	{
    		UprocId uprocId = new UprocId(upr, defaultVersion);
    		uproc = new Uproc(getContext(), uprocId);
            
    		uproc.setImpl(new OwlsUprocImpl());
    		uproc.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());

    		uproc.extract();
    	}
    	if(uproc!=null )
    	{
        	Vector<DependencyCondition> curDeps = uproc.getDependencyConditions();

        	ArrayList<String> curNames = new ArrayList<String>();
        	
        	for(int cdeps=0;cdeps<curDeps.size();cdeps++)
        	{
        		curNames.add(curDeps.get(cdeps).getUproc());
        	}
    
        	for(int newdeps=0;newdeps<depconHashToAdd.size();newdeps++)
        	{
        		if(!curNames.contains(depconHashToAdd.get(newdeps)) )//&& doesUprocExist(depconHashToAdd.get(newdeps)))
        		{
        			if(doesUprocExist(depconHashToAdd.get(newdeps)))
        			{
        				actualDepsToAdd.add(depconHashToAdd.get(newdeps));	
        			}
        			else
        			{
        				System.out.println("# Dep Uproc "+depconHashToAdd.get(newdeps)+" does not exist on node");
        			}
        		}
        	}
        	
        	SessionControl sessionControl = new SessionControl();
			sessionControl.setType(SessionControl.Type.ANY_SESSION);

			MuControl muControl= new MuControl();			
			muControl.setType (Type.SAME_MU);//constants for the dependency condition
        	
			for(String depUpr:actualDepsToAdd)
			{
				DependencyCondition dc = new DependencyCondition();
			
			    dc.setExpected(true);//expected is chosen
			    dc.setFatal(false);//fatal box is NOT checked
				dc.setUserControl(UserControl.ANY);//user is any
				dc.setFunctionalPeriod(FunctionalPeriod.Day);
				dc.setMuControl (muControl);
				dc.setSessionControl(sessionControl);
				
				dc.setNum(1);
				dc.setUproc(depUpr);
				dc.setStatus(Status.COMPLETED);
		        
				curDeps.add(dc);
			}
			
			LaunchFormula lf = new LaunchFormula();
			String text;
			int depNum;
    		
    		for(int d=0;d<curDeps.size();d++)
    		{
    			
    			curDeps.get(d).setNum(d+1);
    		}   
    	    			
   
    		for(int q=0;q<curDeps.size();q++)
    		{
    			depNum=curDeps.get(q).getNum();	
    					        
    					        if(q != (curDeps.size()-1))
    							{					
    						        	if(depNum<10)
    									{
    										text = " =C0"+depNum+" AND";// OK
    									}
    									else 
    									{
    										text = " =C"+depNum+" AND";
    									}
    							}
    							else
    							{
    								if(depNum<10)
    								{
    									text = " =C0"+depNum;// OK
    								}
    								else 
    								{
    									text = " =C"+depNum;
    								}
    					       
    						
    				        		}
    					
    					lf.appendText(text);

    	    			
    	    }
    			
    			
    		uproc.setDependencyConditions(curDeps);
    		uproc.setFormula(lf);
    		uproc.update();		
    		System.out.println("UPROC <"+uproc.getName()+"> has "+depconHashToAdd+" added");

			
    	}
    	else
    	{
    		System.out.println("@ Uproc "+upr+" does not exist on node");
    	}
			
    	
    }

   public void addConditionalDependency(String upr,HashMap<String,String> depCon_oredDep) throws UniverseException
    {
	   System.out.println();
    	if(uprs.containsKey(upr))
    	{
        	Vector<DependencyCondition> curDeps = uprs.get(upr).getDependencyConditions();
        	HashSet<Integer> listOfCNumbers = new HashSet<Integer>();
        	
        	
        	LaunchFormula lf = new LaunchFormula();
        	String lfText = uprs.get(upr).getFormula().getFormulaText();
        	System.out.println(upr+" Before "+lfText);
        	HashMap<String,Integer> deps_cnumber = new HashMap<String,Integer>();
        	
        	for(int cdeps=0;cdeps<curDeps.size();cdeps++)
        	{
        		deps_cnumber.put(curDeps.get(cdeps).getUproc(),curDeps.get(cdeps).getNum());
        		listOfCNumbers.add(curDeps.get(cdeps).getNum());
        	}

        	HashMap<String,String> temp = new HashMap<String,String>();
        	for(String k : depCon_oredDep.keySet())
        	{
        		temp.put(k, depCon_oredDep.get(k));
        	}
        	for(String key : temp.keySet())
        	{
        		if(!deps_cnumber.containsKey(key))
        		{
        			depCon_oredDep.remove(key);
        		}
        		
        		if(deps_cnumber.containsKey(temp.get(key)))
        		{
        			depCon_oredDep.remove(key);
        		}
        	}

        	SessionControl sessionControl = new SessionControl();
			sessionControl.setType(SessionControl.Type.ANY_SESSION);

			MuControl muControl= new MuControl();			
			muControl.setType (Type.SAME_MU);//constants for the dependency condition
        	
			HashMap<String,Integer> conditionalDependenciesToBeOred = new HashMap<String,Integer>();
			
			for(String key:depCon_oredDep.keySet())
			{
				if(!uprs.containsKey(depCon_oredDep.get(key)))
				{ 
					continue;
				}
				
				if(deps_cnumber.containsKey(key))
				{
					
								DependencyCondition dc = new DependencyCondition();
								dc.setExpected(true);//expected is chosen
							    dc.setFatal(false);//fatal box is NOT checked
								dc.setUserControl(UserControl.ANY);//user is any
								dc.setFunctionalPeriod(FunctionalPeriod.Day);
								dc.setMuControl (muControl);
								dc.setSessionControl(sessionControl);
								dc.setUproc(depCon_oredDep.get(key));
								dc.setStatus(Status.ABSENT);
						        
								if(curDeps.size()<99)
								{		
									
									Integer cnumber=99;
									boolean found = false;
									for(int i=1;i<=99;i++)
									{
										if(!listOfCNumbers.contains(i))
										{
											cnumber=i;
											listOfCNumbers.add(cnumber);
											found=true;
											break;
										}
										
									}
									if(!found)
									{
										continue;
									}
									dc.setNum(cnumber);
									conditionalDependenciesToBeOred.put(depCon_oredDep.get(key),cnumber);//adds the oringDepCon with number

									curDeps.add(dc);
								}
				}
				else
				{
					System.out.println(upr+" does not have "+key+" in LF on"+getNode()+"-"+getArea());
				}
			}
			
			for(String key:depCon_oredDep.keySet())
			{
				if(deps_cnumber.containsKey(key)
						&&conditionalDependenciesToBeOred.containsKey(depCon_oredDep.get(key)) )
				{
					String cNumber;
					String cNumber_ored;
					
					if(deps_cnumber.get(key)<10)
					{
						cNumber="=C0"+deps_cnumber.get(key);
					}
					else
					{
						cNumber="=C"+deps_cnumber.get(key);
					}		
					
					if(conditionalDependenciesToBeOred.get(depCon_oredDep.get(key))<10)
					{
						cNumber_ored="=C0"+conditionalDependenciesToBeOred.get(depCon_oredDep.get(key));
					}
					else
					{
						cNumber_ored="=C"+conditionalDependenciesToBeOred.get(depCon_oredDep.get(key));
					}
					
					lfText=lfText.replace(cNumber, "("+cNumber+" OR "+cNumber_ored+")");
								
				}
			}
			
			
    					
    		lf.appendText(lfText);

    	    
    		
    		
    		
    		System.out.println(upr+" After  "+lf.getFormulaText());
    		uprs.get(upr).setDependencyConditions(curDeps);
    		uprs.get(upr).setFormula(lf);
    		try{
    		uprs.get(upr).update();			
    		}
    		catch(UniverseException e)
    		{
    			System.out.println("Error "+ lfText+" on "+upr);
    		}
			
        	System.out.println("------------------------");
    	}
    	else
    	{
    		System.out.println("Uproc "+upr+" not found ");
    		return;
    		
    	}
    }
    
    
    public void orDepWithThisDep(String upr,String depCon,String oredDep) throws UniverseException
    {
    	if(uprs.containsKey(upr))
    	{
        	Vector<DependencyCondition> curDeps = uprs.get(upr).getDependencyConditions();
        	ArrayList<String> curNames = new ArrayList<String>();

        	for(int cdeps=0;cdeps<curDeps.size();cdeps++)
        	{
        		curNames.add(curDeps.get(cdeps).getUproc());
        	}
    
        	if(!curNames.contains(depCon))
        	{
        		System.out.println("Uproc <"+upr+"> does not have Dep <"+depCon+">");
        		return;
        	}

        	SessionControl sessionControl = new SessionControl();
			sessionControl.setType(SessionControl.Type.ANY_SESSION);

			MuControl muControl= new MuControl();			
			muControl.setType (Type.SAME_MU);//constants for the dependency condition
        	
			
			DependencyCondition dc = new DependencyCondition();
								dc.setExpected(true);//expected is chosen
							    dc.setFatal(false);//fatal box is NOT checked
								dc.setUserControl(UserControl.ANY);//user is any
								dc.setFunctionalPeriod(FunctionalPeriod.Day);
								dc.setMuControl (muControl);
								dc.setSessionControl(sessionControl);
								dc.setNum(1);
								dc.setUproc(oredDep);
								dc.setStatus(Status.ABSENT);
						        
								curDeps.add(dc);
			
			
			LaunchFormula lf = new LaunchFormula();
			String text;
			int depNum;
    		int relevantDepNum =0;
    		int relevantDepNumOred=0;
    		
    		for(int d=0;d<curDeps.size();d++)
    		{
    			
    			curDeps.get(d).setNum(d+1);
    			
    			if(curDeps.get(d).getUproc().equalsIgnoreCase(depCon))
    			{
    				relevantDepNum = curDeps.get(d).getNum();
    			}
    			if(curDeps.get(d).getUproc().equalsIgnoreCase(oredDep))
    			{
    				relevantDepNumOred =curDeps.get(d).getNum(); 
    			}
    		}   
    	    			
   
    		for(int q=0;q<curDeps.size();q++)
    		{
    			depNum=curDeps.get(q).getNum();	
    			
    			if(depNum==relevantDepNumOred)
    			{
    				continue;
    			}

    					        if(q != (curDeps.size()-1))
    							{	
    					        
    						        	if(depNum<10)
    									{
    						        		if(depNum == relevantDepNum)
    						        		{
    						        			if(relevantDepNumOred<10)
    						        			{
    						        				text = " (=C0"+depNum+" OR =C0"+relevantDepNumOred+") AND";
    						        			}
    						        			else
    						        			{
    						        				text = " (=C0"+depNum+" OR =C"+relevantDepNumOred+") AND";

    						        			}
    						        		}
    						        		else
    						        		{
    						        			text = " =C0"+depNum+" AND";// OK
    						        		}
    										
    									}
    									else 
    									{
    										if(depNum == relevantDepNum)
    						        		{
    						        			if(relevantDepNumOred<10)
    						        			{
    						        				text = " (=C"+depNum+" OR =C0"+relevantDepNumOred+") AND";
    						        			}
    						        			else
    						        			{
    						        				text = " (=C"+depNum+" OR =C"+relevantDepNumOred+") AND";
    						        			}
    						        		}
    										else
    										{
        										text = " =C"+depNum+" AND";

    										}
    									}
    							}
    							else
    							{
    							
    								if(depNum<10)
									{
						        		if(depNum == relevantDepNum)
						        		{
						        			if(relevantDepNumOred<10)
						        			{
						        				text = " (=C0"+depNum+" OR =C0"+relevantDepNumOred+")";
						        			}
						        			else
						        			{
						        				text = " (=C0"+depNum+" OR =C"+relevantDepNumOred+")";

						        			}
						        		}
						        		else
						        		{
						        			text = " =C0"+depNum;// OK
						        		}
										
									}
									else 
									{
										if(depNum == relevantDepNum)
						        		{
						        			if(relevantDepNumOred<10)
						        			{
						        				text = " (=C"+depNum+" OR =C0"+relevantDepNumOred+")";
						        			}
						        			else
						        			{
						        				text = " (=C"+depNum+" OR =C"+relevantDepNumOred+")";
						        			}
						        		}
										else
										{
    										text = " =C"+depNum;

										}
									}
    					       
    						
    				        	}
    					
    					lf.appendText(text);

    			}
    	    
    		
    		
    		
    		//System.out.println(lf.getFormulaText());
    		uprs.get(upr).setDependencyConditions(curDeps);
    		uprs.get(upr).setFormula(lf);
    		uprs.get(upr).update();			
			
			
			
    	}
    	else
    	{
    		System.out.println("Uproc "+upr+" not found ");
    		return;
    		
    	}
    }
    
    public void addDepToUproc(String upr,ArrayList<String>depconHashToAdd) throws UniverseException
    {
    	if(uprs.containsKey(upr))
    	{
        	Vector<DependencyCondition> curDeps = uprs.get(upr).getDependencyConditions();	
        	
        	String currentlfText="";
        	
        	if(uprs.get(upr).getFormula()!=null)
        	{
        		
        		if(uprs.get(upr).getFormula().getFormulaText()!=null)
        		{
        		
        			currentlfText= uprs.get(upr).getFormula().getFormulaText();
        	
        		}
        	}
        	
        	
        	
        	HashSet<Integer> listOfCNumbers = new HashSet<Integer>();
        	ArrayList<String> actualDepsToAdd = new ArrayList<String>();
        	ArrayList<String> curNames = new ArrayList<String>();
        	
        	
        	for(int cdeps=0;cdeps<curDeps.size();cdeps++)
        	{
        		curNames.add(curDeps.get(cdeps).getUproc());
        		listOfCNumbers.add(curDeps.get(cdeps).getNum());
        	}
    
        	for(int newdeps=0;newdeps<depconHashToAdd.size();newdeps++)
        	{
        		if(!curNames.contains(depconHashToAdd.get(newdeps)) )//&& doesUprocExist(depconHashToAdd.get(newdeps)))
        		{
        			
        			if(doesUprocExist(depconHashToAdd.get(newdeps)))
        			{
        				actualDepsToAdd.add(depconHashToAdd.get(newdeps));	
        			}
        			
        		}
        	}
        	
        	
        	
        	HashMap<String,Integer> newDepsToAddWithTheirNumber = new HashMap<String,Integer>();
        	
        	
        	SessionControl sessionControl = new SessionControl();
			sessionControl.setType(SessionControl.Type.ANY_SESSION);

			MuControl muControl= new MuControl();			
			muControl.setType (Type.SAME_MU);//constants for the dependency condition
        	
			if(actualDepsToAdd.size()==0)
			{
				return;
			}
			
			for(String depUpr:actualDepsToAdd)
			{
				DependencyCondition dc = new DependencyCondition();
			
			    dc.setExpected(true);//expected is chosen
			    dc.setFatal(false);//fatal box is NOT checked
				dc.setUserControl(UserControl.ANY);//user is any
				dc.setFunctionalPeriod(FunctionalPeriod.Day);
				dc.setMuControl (muControl);
				dc.setSessionControl(sessionControl);
				
				Integer cnumber=99;
				boolean found = false;
				for(int i=1;i<=99;i++)
				{
					if(!listOfCNumbers.contains(i))
					{
						cnumber=i;
						listOfCNumbers.add(cnumber);
						found=true;
						break;
					}
					
				}
				if(!found)
				{
					continue;
				}
				dc.setNum(cnumber);
				dc.setUproc(depUpr);
				dc.setStatus(Status.COMPLETED);
		        
				if(curDeps.size()<99)
				{
		        	curDeps.add(dc);
					newDepsToAddWithTheirNumber.put(depUpr, cnumber);

				}
			}
			
		boolean first = true;
		
			for(String depKeyToAdd:newDepsToAddWithTheirNumber.keySet())
			{
				String cpart;
				
				if(newDepsToAddWithTheirNumber.get(depKeyToAdd)<10)
				{
					cpart="=C0"+newDepsToAddWithTheirNumber.get(depKeyToAdd);
				}
				else
				{
					cpart="=C"+newDepsToAddWithTheirNumber.get(depKeyToAdd);
				}
				
					if(!first)
					{
						currentlfText+=" AND "+cpart;
					}
					else
					{
						currentlfText+=cpart;
						first=false;
					}
			}
			
			LaunchFormula lf = new LaunchFormula();
			lf.appendText(currentlfText);

    		uprs.get(upr).setDependencyConditions(curDeps);
    		uprs.get(upr).setFormula(lf);
    		System.out.println("- "+uprs.get(upr).getName()+" has "+actualDepsToAdd+" added");
    		uprs.get(upr).update();			
			
			
			
    	}
    	else
    	{
    		System.out.println("Uproc "+upr+" not found ");
    		return;
    		
    	}
    }
    public void addResourceConditionToUproc(String upr,String logicalRes) throws UniverseException
    {
    	if(uprs.containsKey(upr))
    	{
        	Vector<DependencyCondition> curDeps = uprs.get(upr).getDependencyConditions();

        	
			MuControl muControl= new MuControl();			
			muControl.setType (Type.SAME_MU);//constants for the dependency condition
        	
			
			ResourceCondition rc = new ResourceCondition();
			rc.setExpected(true);//expected is chosen
			rc.setFatal(false);//fatal box is NOT checked
			rc.setMuControl (muControl);
			rc.setType(ResourceCondition.Type.FILE);
			rc.setAttribute("EXIST");					
			rc.setResource(logicalRes);
			/*rc.setQuota1(2);
			rc.setQuota2(3);   */ 
			
			
			LaunchFormula lf = new LaunchFormula();
			lf = uprs.get(upr).getFormula();
			String text;
			
    		
    		
    	    
    		rc.setNum(curDeps.size()+1);
    		

    		if(curDeps.size()+1<10)
    		{
    				text = "AND =C0"+(curDeps.size()+1);// OK
    		}
    		else 
    		{
    				text = "AND =C"+(curDeps.size()+1);
    		}
    					       
    						
    				        				
    		lf.appendText(text);

    	    			
    	   
    			
    		Vector<ResourceCondition> curResCond = new Vector<ResourceCondition>();
    		curResCond.add(rc);
    			
    		uprs.get(upr).setResourceConditions(curResCond);
    		uprs.get(upr).setFormula(lf);
    		System.out.println("- "+uprs.get(upr).getName()+" has "+logicalRes+" added");
    		uprs.get(upr).update();			
			
			
			
    	}
    }
    
    
    public void setSessionControlOnUproc(String upr) throws UniverseException
    {//this was developed for AMEX to fix the offset vs SAME_SESSION_AND_EXECUTION issue
    	
    	if(uprs.containsKey(upr))
    	{
    		Uproc uproc = uprs.get(upr);

			Vector<DependencyCondition> deps = new Vector<DependencyCondition>(uproc.getDependencyConditions());
			for(int d=0;d<deps.size();d++)
			{
				 ProcessingDateControl pdc=deps.get(d).getProcessingDateControl();
				 ProcessingDateControl.CalendarUnit unit_day = pdc.getCalendarUnitDay();
				 
				 ProcessingDateControl.CalendarUnit unit_month = pdc.getCalendarUnitMonth();
				 ProcessingDateControl.CalendarUnit unit_year = pdc.getCalendarUnitYear();

				 
				 
				if(deps.get(d).getSessionControl().getType().equals(SessionControl.Type.SAME_SESSION_AND_EXECUTION)
						&&(unit_day!=null)&& (unit_month!=null) && (unit_year!=null)
						&&(!unit_day.getOperator().equals(ProcessingDateControl.Operator.SAME)||
								!unit_month.getOperator().equals(ProcessingDateControl.Operator.SAME)||
								!unit_year.getOperator().equals(ProcessingDateControl.Operator.SAME)
								))
								
				{
					SessionControl sessionControl = new SessionControl();
					sessionControl.setType(SessionControl.Type.SAME_SESSION);
					deps.get(d).setSessionControl(sessionControl);
					System.out.println("-Uproc <"+upr+"> : Dep <"+deps.get(d).getUproc()+"> has been updated");
					
				}
			}
			uproc.setDependencyConditions(deps);
			uproc.update();
    	}
    }
    private List<String> getApplications() throws Exception{
		ArrayList<String> list = new ArrayList<String>();
		ApplicationList l = new ApplicationList(getContext(), new ApplicationFilter());
//		if (node.V5) 
//			l.setImpl(new ApplicationListStdImpl());
//		else 
    		l.setImpl(new OwlsApplicationListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++)
			list.add(l.get(i).getName());		
		return list;

	}
	
	private List<String> getDomains() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		DomainList l = new DomainList(getContext(), new DomainFilter());
	/*	if (node.V5) 
			l.setImpl(new DomainListStdImpl());
		else*/ 
    		l.setImpl(new OwlsDomainListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++)
			list.add(l.get(i).getName());		
		return list;

	}
	
	private void createApp(String name) throws Exception {
		Application app = new Application(name);
	/*	if (node.V5) 
			app.setImpl(new ApplicationStdImpl());
		else */
    		app.setImpl(new OwlsApplicationImpl());
		List<String> doms = getDomains();
		String d="I";
		if (!doms.contains(d)) {
			if (doms.size()==0) 
				createDom("I");
			else
				d=doms.get(0);
		}
		app.setDomain(d);
		app.create();
	}
	
	private void createDom(String name) throws Exception {
		Domain dom = new Domain(name);
//		if (node.V5) 
//			dom.setImpl(new DomainStdImpl());
//		else 
    		dom.setImpl(new OwlsDomainImpl());
		dom.create();
	}
	
	public void createUProc(String uprName, String[] scriptLines)  throws Exception {		
    	UprocId uprocId = new UprocId(uprName, "000");
		Uproc obj = new Uproc(getContext(), uprocId);
		List<String> apps = getApplications();
		String app = "U_";
		if (!apps.contains(app)) {
			if (apps.size()==0) 
				createApp("U_");
			else
				app=apps.get(0);
		}
		obj.setApplication(app);
		obj.setType("CL_INT");
		obj.setFunctionalPeriod(FunctionalPeriod.Day);
		obj.setLabel("Header");
		Memorization memo = new Memorization(Memorization.Type.ONE);
		obj.setMemorization(memo);
		
	/*	if (node.V5) {
			obj.setImpl(new UprocStdImpl());
    		obj.getIdentifier().setSyntaxRules(ClassicSyntaxRules.getInstance());
		}
		else*/ {
    		obj.setImpl(new OwlsUprocImpl());
    		obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());    		
		}
		
		InternalScript script = new InternalScript(obj);
        script.setLines(scriptLines);
		
      
        
		obj.create();
		obj.extract();
		System.out.println("Header Uproc <"+obj.getName()+"> created");
		uprs.put(obj.getName(), obj);
		
		
			script.save();	
	} 
		
	public void createLaunch(String uprocName,String submissionUser,String muName) throws Exception {
		
		
	    Date launchDateTime = new Date();
	    
		
		List<String> mus = getMus(); 
		List<String> users = getUsers();
		if (mus.size()==0)
			throw new Exception("No MU found");
		if (users.size()==0)
			throw new Exception("No user found");
		String aMu = mus.get(0);
		String aUser = users.get(0);
		if (users.contains(submissionUser))
			aUser = submissionUser;
		
		if(mus.contains(muName))
			aMu=muName;
		
				
		Launch l = new Launch(getContext(),LaunchId.createWithName("", "", uprocName, "000", aMu, null));
		{
    		l.setImpl(new OwlsLaunchImpl());
    		l.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
		}
        l.setBasedOnTask(false);
        l.setBeginDate(launchDateTime);
        Date endDate = new Date();
        endDate.setTime(launchDateTime.getTime() + 100000000);
        l.setEndDate(endDate);
        l.setProcessingDate((new SimpleDateFormat("yyyyMMdd")).format(launchDateTime));
        l.setUserName(aUser);
        l.setQueue("SYS_BATCH");
        l.setPriority("100");
        l.setPrinter("IMPR");
       
        
        l.create();	
     
        java.util.Date date5= new java.util.Date();
		Timestamp ts5 = new Timestamp(date5.getTime());
		
		System.out.println(ts5+" LOG : Launch created for <"+uprocName+"> with nmLanc ="+l.getNumlanc());


        //return l.getIdentifier();
	}

	
	public List<String> getMus() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		MuList l = new MuList(getContext(), new MuFilter());
		/*if (node.V5) 
			l.setImpl(new MuListStdImpl());
		else */
    		l.setImpl(new OwlsMuListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++)
			list.add(l.get(i).getName());		
		return list;
	}
	
	public List<String> getUsers() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		UserList l = new UserList(getContext(), new UserFilter());
		/*if (node.V5) 
			l.setImpl(new UserListStdImpl());
		else */
    		l.setImpl(new OwlsUserListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++) 			
			//select only admin users
			//in V5 select user with code 001
			if(l.get(i).getProfile().equalsIgnoreCase("PROFADM") && 
//				(!node.V5 || l.get(i).getAuthorCode().equals("001")))
				list.add(l.get(i).getName()));
		return list;
	}
	
	public String getLaunchStatus(String uprName, String launch) throws Exception {
		LaunchFilter lf = new LaunchFilter();
		lf.setNumlancMin(launch);
		lf.setNumlancMax(launch);
		lf.setUprocName(uprName);
		lf.setBeginDate((new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime()));
		//lf.setEndDate("20300101");
		//lf.setBeginHour("000000");
		//lf.setEndHour("000000");
		
		LaunchList ll = new LaunchList(getContext(), lf);
		/*if (node.V5) 
			ll.setImpl(new LaunchListStdImpl());
		else*/ 
			ll.setImpl(new OwlsLaunchListImpl());
				
		ll.extract(Operation.DISPLAY);
		
		if (ll.getCount()==1) {		
			LaunchItem it = ll.get(0);
			return ""+it.getStatus().getCode();
		}
				
		ExecutionFilter ef = new ExecutionFilter();
		ef.setNumlancMin(launch);
		ef.setNumlancMax(launch);
		ef.setUprocName(uprName);
		ef.setBeginDate((new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime()));
	/*	if (node.V5) {
			ef.setApplication(null);
			ef.setTaskName(null);
			ef.setTaskVersion(null);
		}*/
		
		ExecutionList list = new ExecutionList(getContext(), ef);
	
			list.setImpl(new OwlsExecutionListImpl());
		list.extract(Operation.DISPLAYLIST);
		
		if (list.getCount()==1) {
			ExecutionItem it = list.get(0);
			return it.getStatus().toString();
		}
        
        return "?";
	}
	
	public String[] getExecutionLog (String uprName, String launch) throws Exception {
		ExecutionFilter ef = new ExecutionFilter();
		ef.setUprocName(uprName);
		ef.setNumlancMin(launch);
		ef.setNumlancMax(launch);
		ef.setBeginDate((new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime()));
	/*	if (node.V5) {
			ef.setApplication(null);
			ef.setTaskName(null);
			ef.setTaskVersion(null);
		}*/
		
		ExecutionList list = new ExecutionList(getContext(), ef);
		
			list.setImpl(new OwlsExecutionListImpl());
		list.extract(Operation.DISPLAY);
		
		if (list.getCount()==1) {
			ExecutionItem it = list.get(0);
			Execution ex = new Execution(getContext(),it.getIdentifier());
	 
				ex.setImpl(new OwlsExecutionImpl());
			try {
				ExecutionLog log = ex.getExecutionLog();			
	            return log.getLines();			
			} catch (Exception exc){}
		}			
		
		return null;
	}
	
	public List<String> getNodeFileList() throws Exception{
		Connection connection = null;
		List<String> files = new ArrayList<String>();
        try {
        	connection = ClientConnectionManager.getConnection(getContext(), ConnectionFactory.Service.IO);
        	
        	NodeFile nf = new NodeFile(connection, getContext(), "*");
	    	files = nf.getNodeFileList();
    		    		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			connection.close();
		}
        return files;		
	}
	
	public String getRemoteFile(String string, String file) throws Exception {
		File f = new File(file);
		String path = f.getParent();
		String filename = f.getName();
		return getRemoteFile(path, filename);
	}
	
	public String getRemoteFile(String destinationPath, String remotePath, String remoteFileName) throws Exception{
		Connection connection = null;
		String localFile = destinationPath + File.separator + remoteFileName;
        try {
        	connection = ClientConnectionManager.getConnection(getContext(), ConnectionFactory.Service.IO);
        	LocalBinaryFile locFile = new LocalBinaryFile(localFile);
    	 {
	    		NodeFile nf = new NodeFile(locFile, connection, getContext(), remoteFileName);
	    		nf.get();
    		}    		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			connection.close();
		}
        return localFile;
	}

	public void deleteUProc(String uprName) {
		try {
			Uproc obj = new Uproc(getContext(), new UprocId(uprName, "000"));
		
			 {
	    		obj.setImpl(new OwlsUprocImpl());
	    		obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			}
			obj.delete();
		} catch (ObjectNotFoundException oe) {
			//ignore
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void removeDuplicatedDepCons(String upr) throws UniverseException
    {
    	if(uprs.containsKey(upr))
    	{
        	Vector<DependencyCondition> curDeps = uprs.get(upr).getDependencyConditions();
        	Vector<DependencyCondition> curDeps_noDup = new Vector<DependencyCondition>();
        	
              	HashMap<String,DependencyCondition> deps = new HashMap<String,DependencyCondition>();
        	
        	for(int cdeps=0;cdeps<curDeps.size();cdeps++)
        	{
        		deps.put(curDeps.get(cdeps).getUproc(),curDeps.get(cdeps));
        	}
    
        	
        
        	
        	
        	
        	
        	
        	SessionControl sessionControl = new SessionControl();
			sessionControl.setType(SessionControl.Type.SAME_SESSION);

			MuControl muControl= new MuControl();			
			muControl.setType (Type.SAME_MU);//constants for the dependency condition
        	
		
			
			for(String depUpr:deps.keySet())
			{
			
				curDeps_noDup.add(deps.get(depUpr));
			}
			
			LaunchFormula lf = new LaunchFormula();
			String text;
			int depNum;
    		
    		for(int d=0;d<curDeps_noDup.size();d++)
    		{
    			
    			curDeps_noDup.get(d).setNum(d+1);
    		}   
    	    			
   
    		for(int q=0;q<curDeps_noDup.size();q++)
    		{
    			depNum=curDeps_noDup.get(q).getNum();	
    					        
    					        if(q != (curDeps_noDup.size()-1))
    							{					
    						        	if(depNum<10)
    									{
    										text = " =C0"+depNum+" AND";// OK
    									}
    									else 
    									{
    										text = " =C"+depNum+" AND";
    									}
    							}
    							else
    							{
    								if(depNum<10)
    								{
    									text = " =C0"+depNum;// OK
    								}
    								else 
    								{
    									text = " =C"+depNum;
    								}
    					       
    						
    				        		}
    					
    					lf.appendText(text);

    	    			
    	    }
    			
    			
    		uprs.get(upr).setDependencyConditions(curDeps_noDup);
    		uprs.get(upr).setFormula(lf);
    		uprs.get(upr).update();			
			
			
			
    	}
    	else
    	{
    		System.out.println("Uproc "+upr+" not found ");
    		return;
    		
    	}
    }
    
}



















	
	

