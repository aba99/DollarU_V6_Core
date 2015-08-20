package com.orsyp.tools.ps;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.orsyp.Area;
import com.orsyp.Environment;
import com.orsyp.Identity;
import com.orsyp.SyntaxException;
import com.orsyp.api.Client;
import com.orsyp.api.Context;
import com.orsyp.api.FunctionalPeriod;
import com.orsyp.api.ObjectNotFoundException;
import com.orsyp.api.Product;
import com.orsyp.api.application.Application;
import com.orsyp.api.application.ApplicationFilter;
import com.orsyp.api.application.ApplicationList;
import com.orsyp.api.central.UniCentral;
import com.orsyp.api.domain.Domain;
import com.orsyp.api.domain.DomainFilter;
import com.orsyp.api.domain.DomainList;
import com.orsyp.api.execution.Execution;
import com.orsyp.api.execution.ExecutionFilter;
import com.orsyp.api.execution.ExecutionItem;
import com.orsyp.api.execution.ExecutionList;
import com.orsyp.api.execution.ExecutionLog;
import com.orsyp.api.launch.Launch;
import com.orsyp.api.launch.LaunchFilter;
import com.orsyp.api.launch.LaunchId;
import com.orsyp.api.launch.LaunchItem;
import com.orsyp.api.launch.LaunchList;
import com.orsyp.api.mu.MuFilter;
import com.orsyp.api.mu.MuList;
import com.orsyp.api.security.Operation;
import com.orsyp.api.syntaxerules.ClassicSyntaxRules;
import com.orsyp.api.syntaxerules.OwlsSyntaxRules;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.uproc.UprocId;
import com.orsyp.api.uproc.cl.InternalScript;
import com.orsyp.comm.Connection;
import com.orsyp.api.uproc.Memorization;
import com.orsyp.api.user.UserFilter;
import com.orsyp.api.user.UserList;
import com.orsyp.owls.impl.application.OwlsApplicationImpl;
import com.orsyp.owls.impl.application.OwlsApplicationListImpl;
import com.orsyp.owls.impl.domain.OwlsDomainImpl;
import com.orsyp.owls.impl.domain.OwlsDomainListImpl;
import com.orsyp.owls.impl.execution.OwlsExecutionImpl;
import com.orsyp.owls.impl.execution.OwlsExecutionListImpl;
import com.orsyp.owls.impl.launch.OwlsLaunchImpl;
import com.orsyp.owls.impl.launch.OwlsLaunchListImpl;
import com.orsyp.owls.impl.mu.OwlsMuListImpl;
import com.orsyp.owls.impl.nfile.NodeFile;
import com.orsyp.owls.impl.uproc.OwlsUprocImpl;
import com.orsyp.owls.impl.user.OwlsUserListImpl;
import com.orsyp.std.ApplicationListStdImpl;
import com.orsyp.std.ApplicationStdImpl;
import com.orsyp.std.ClientConnectionManager;
import com.orsyp.std.ConnectionFactory;
import com.orsyp.std.DomainListStdImpl;
import com.orsyp.std.DomainStdImpl;
import com.orsyp.std.ExecutionListStdImpl;
import com.orsyp.std.ExecutionStdImpl;
import com.orsyp.std.LaunchListStdImpl;
import com.orsyp.std.LaunchStdImpl;
import com.orsyp.std.MuListStdImpl;
import com.orsyp.std.UprocStdImpl;
import com.orsyp.std.UserListStdImpl;
import com.orsyp.std.nfile.AnyFile;
import com.orsyp.std.nfile.LocalBinaryFile;

public class NodeConnection {
	
	private DuNode node;
	private Context ctx;
	
	public NodeConnection(DuNode node, UniCentral central)  throws SyntaxException {
		super();
		this.node = node;
		
		Client client = new Client(new Identity(node.uvmsUser, "", node.name, ""));
		ctx = new Context(new Environment(node.company, node.name, Area.Exploitation), client, central);
		if (node.V5)
    		ctx.setProduct(Product.DOLLAR_UNIVERSE);
    	else
    		ctx.setProduct(Product.OWLS);
	}
	
	private List<String> getApplications() throws Exception{
		ArrayList<String> list = new ArrayList<String>();
		ApplicationList l = new ApplicationList(ctx, new ApplicationFilter());
		if (node.V5) 
			l.setImpl(new ApplicationListStdImpl());
		else 
    		l.setImpl(new OwlsApplicationListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++)
			list.add(l.get(i).getName());		
		return list;

	}
	
	private List<String> getDomains() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		DomainList l = new DomainList(ctx, new DomainFilter());
		if (node.V5) 
			l.setImpl(new DomainListStdImpl());
		else 
    		l.setImpl(new OwlsDomainListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++)
			list.add(l.get(i).getName());		
		return list;

	}
	
	private void createApp(String name) throws Exception {
		Application app = new Application(name);
		if (node.V5) 
			app.setImpl(new ApplicationStdImpl());
		else 
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
		if (node.V5) 
			dom.setImpl(new DomainStdImpl());
		else 
    		dom.setImpl(new OwlsDomainImpl());
		dom.create();
	}
	
	public void createUProc(String uprName, String[] scriptLines)  throws Exception {		
    	UprocId uprocId = new UprocId(uprName, "000");
		Uproc obj = new Uproc(ctx, uprocId);
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
		obj.setLabel("Audit tool remote collector");
		Memorization memo = new Memorization(Memorization.Type.ONE);
		obj.setMemorization(memo);
		
		if (node.V5) {
			obj.setImpl(new UprocStdImpl());
    		obj.getIdentifier().setSyntaxRules(ClassicSyntaxRules.getInstance());
		}
		else {
    		obj.setImpl(new OwlsUprocImpl());
    		obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());    		
		}
		
		InternalScript script = new InternalScript(obj);
        script.setLines(scriptLines);
		
        if (node.V5)
        	script.save();
        
		obj.create();
		
		if (!node.V5)
			script.save();	
	} 
		
	public LaunchId createLaunch(String uprocName, Date launchDateTime) throws Exception {
		List<String> mus = getMus(); 
		List<String> users = getUsers();
		if (mus.size()==0)
			throw new Exception("No MU found");
		if (users.size()==0)
			throw new Exception("No user found");
		String aMu = mus.get(0);
		String aUser = users.get(0);
		if (users.contains("univ56a"))
			aUser = "univ56a";
		else
		if (users.contains("univ60a"))
			aUser = "univ60a";			
		Launch l = new Launch(ctx,LaunchId.createWithName("", "", uprocName, "000", aMu, null));
		if (node.V5) {
			l.setImpl(new LaunchStdImpl());
    		l.getIdentifier().setSyntaxRules(ClassicSyntaxRules.getInstance());
		}
		else {
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
        l.setPriority("001");
        l.setPrinter("prin");
        l.create();		
        return l.getIdentifier();
	}
	
	public List<String> getMus() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		MuList l = new MuList(ctx, new MuFilter());
		if (node.V5) 
			l.setImpl(new MuListStdImpl());
		else 
    		l.setImpl(new OwlsMuListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++)
			list.add(l.get(i).getName());		
		return list;
	}
	
	public List<String> getUsers() throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		UserList l = new UserList(ctx, new UserFilter());
		if (node.V5) 
			l.setImpl(new UserListStdImpl());
		else 
    		l.setImpl(new OwlsUserListImpl());
		l.extract();
		
		for (int i = 0; i < l.getCount(); i++) 			
			//select only admin users
			//in V5 select user with code 001
			if(l.get(i).getProfile().equalsIgnoreCase("PROFADM") && 
				(!node.V5 || l.get(i).getAuthorCode().equals("001")))
				list.add(l.get(i).getName());
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
		
		LaunchList ll = new LaunchList(ctx, lf);
		if (node.V5) 
			ll.setImpl(new LaunchListStdImpl());
		else 
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
		if (node.V5) {
			ef.setApplication(null);
			ef.setTaskName(null);
			ef.setTaskVersion(null);
		}
		
		ExecutionList list = new ExecutionList(ctx, ef);
		if (node.V5) 
			list.setImpl(new ExecutionListStdImpl());
		else 
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
		if (node.V5) {
			ef.setApplication(null);
			ef.setTaskName(null);
			ef.setTaskVersion(null);
		}
		
		ExecutionList list = new ExecutionList(ctx, ef);
		if (node.V5) 
			list.setImpl(new ExecutionListStdImpl());
		else 
			list.setImpl(new OwlsExecutionListImpl());
		list.extract(Operation.DISPLAY);
		
		if (list.getCount()==1) {
			ExecutionItem it = list.get(0);
			Execution ex = new Execution(ctx,it.getIdentifier());
			if (node.V5) 
				ex.setImpl(new ExecutionStdImpl());
			else 
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
        	connection = ClientConnectionManager.getConnection(ctx, ConnectionFactory.Service.IO);
        	
        	NodeFile nf = new NodeFile(connection, ctx, "*");
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
        	connection = ClientConnectionManager.getConnection(ctx, ConnectionFactory.Service.IO);
        	LocalBinaryFile locFile = new LocalBinaryFile(localFile);
    		if (node.V5) { 
    			AnyFile f = new AnyFile(locFile, connection, ctx, remotePath, remoteFileName, true, true);
    			f.getBinary();
    		}
    		else {
	    		NodeFile nf = new NodeFile(locFile, connection, ctx, remoteFileName);
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
			Uproc obj = new Uproc(ctx, new UprocId(uprName, "000"));
			if (node.V5) {
				obj.setImpl(new UprocStdImpl());
	    		obj.getIdentifier().setSyntaxRules(ClassicSyntaxRules.getInstance());
			}
			else {
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
	
}