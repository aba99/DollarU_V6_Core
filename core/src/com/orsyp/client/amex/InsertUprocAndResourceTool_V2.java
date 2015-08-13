import com.orsyp.migration.extensions.*;

import com.orsyp.api.session.Session;
import com.orsyp.api.session.SessionAtom;
import com.orsyp.api.session.SessionData;
import com.orsyp.api.uproc.LaunchFormula;
import com.orsyp.api.uproc.*;
import com.orsyp.api.uproc.ResourceCondition;
import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.Variable;
import com.orsyp.api.VariableText;
import com.orsyp.api.FunctionalPeriod;
import com.orsyp.api.uproc.DependencyCondition.Status;


import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Collection;

import javax.swing.JFileChooser;
import com.orsyp.migration.gui.forms.ConnectionDialog;
import com.orsyp.migration.univAPI.UVMSConnection;
import com.orsyp.migration.datamodel.du.Resource;

public class InsertUprocAndResourceTool_V2 extends GenericViewExtension {

	public void run() {
				
		//Template uproc name
		String templateUproc = "CABRIN";
		//input csv file
		String fileName = "C:\\Users\\administrator\\Desktop\\Projects\\Amex\\input_files\\uconverter_input_files\\ex.txt";

		//search for file, if not found show file chooser window
		File f = new File (fileName);				
		if (!f.exists()) {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			  f = fileChooser.getSelectedFile();					 
			}
			if (!f.exists()) {
				log ("File not found: " + f.getPath());
				return;
			}
			else
				fileName = f.getAbsolutePath();
		}
		
		//read csv 
		Collection<String[]> lines = readCsvFile(fileName);
		
		//connect to uvms
		//show connection window
		ConnectionDialog dlg = new ConnectionDialog(project.projectData.host, String.valueOf(project.projectData.port), project.projectData.uvmsUser, 
				project.projectData.uvmsPassword, project.projectData.referenceNode,
				project.projectData.company, project.projectData.area);
		dlg.setVisible(true);
		if (!dlg.isAccepted())
			return;
			
		project.projectData.host = dlg.getHost();
		project.projectData.port = dlg.getPort();
		project.projectData.uvmsUser = dlg.getUser();
		project.projectData.uvmsPassword = dlg.getPassword();
		project.projectData.referenceNode = dlg.getRefNode();

		//connect
		UVMSConnection conn;
		try {
			conn = UVMSConnection.createConnection(project.projectData.host, project.projectData.port, 
			project.projectData.uvmsUser, project.projectData.uvmsPassword, 
									project.projectData.company, project.projectData.area, 
									project.projectData.referenceNode,
									project.projectData.systemUser, project.projectData.systemPassword, 
									project.projectData.systemOs);
			
			conn.testReadObject(); 			
		} catch (Exception e) {								
			log("UVMS Server connection failed.\nCheck user name, password and $Universe node name.");
			return;
		}
		
		//process lines
		for (String[] line : lines)
			if (line.length>=4)
			{				
				String targetUprocName = line[0];						
				String resName = line[1];		
				String sessionName = line[2]; 
				String file = line[3];

				log("Processing line: " + arrayToStr(line));
				
				//naming convention
				String newUprocName=templateUproc+"_"+resName+"_"+targetUprocName; 
				
				//create resource
				Resource r = new Resource();
				r.name=resName;
				r.resourceType = Resource.Type.FILE;
				r.file = file;
				
				try {
					conn.createResource(r);
				} catch (Exception e) {							
					log("Error creating resource " + resName);
					continue;
				}
				
				//duplicate uproc
				try {
					conn.duplicateUproc(templateUproc,newUprocName);
					//set var
					addVar(conn, newUprocName,file);
				} catch (Exception e) {
					log("Error duplicating uproc " + templateUproc + " -> " + newUprocName);
					continue;
				}
				
				//create dep res->uproc
				try {
					createResDependency(conn, resName, newUprocName);
				} catch (Exception e) {
					log("Error creating resource dependency");
					continue;
				}
				
				
				//insert uproc in session
				/*try {
					addToSessionBeforeUproc(conn, sessionName, newUprocName,targetUprocName);
				} catch (Exception e) {
					log("Error inserting uproc " + newUprocName + " into session " + sessionName + " before uproc " + targetUprocName );
					continue;
				}*/
				

				try {
					addAfterHeader(conn, sessionName, newUprocName);
				} catch (Exception e) {
					log("Error inserting uproc " + newUprocName + " into session " + sessionName + " after header");
					continue;
				}

				try {
					createJobDependency(conn, newUprocName, targetUprocName);
				} catch (Exception e) {
					e.printStackTrace();
					log("Error creating dependency " + newUprocName + " -> " + targetUprocName);
					
					continue;
				}
				
			}
			else
				log("Skipping line: " + arrayToStr(line));
	}

	//helper methods ------------------------------------------

	private void addAfterHeader(UVMSConnection conn, String sessionName, String newUprocName) throws Exception {
		//get session
		Session s = conn.getSession(sessionName);		
		//find header
		SessionAtom root = s.getTree().getRoot();
		//create new session node
		SessionAtom newNode = new SessionAtom(new SessionData(newUprocName));

		//put it under header node
		newNode.setParent(root);
		SessionAtom sibl = root.getChildOk();		
		SessionAtom last = sibl;
		if(sibl==null)
		{
			root.setChildOk(newNode);
			s.update();
			return;
		}
		while (sibl!=null) {
			last=sibl;
			sibl=sibl.getNextSibling();
		}
		
		newNode.setPreviousSibling(last);
		last.setNextSibling(newNode);
		s.update();
	}
			
	private void addToSessionBeforeUproc(UVMSConnection conn, String sessionName, String newUprocName, String existingUprocName) throws Exception {				
		//get session
		Session s = conn.getSession(sessionName);
		
		//find existing node in tree
		SessionAtom root = s.getTree().getRoot();			
		SessionAtom nodes[] = findNode(root,existingUprocName);								
		
		if (nodes!=null) {
			SessionAtom newNode = new SessionAtom(nodes[1].getData());
			newNode.setChildOk(nodes[1].getChildOk());
			
			nodes[1].setData(new SessionData(newUprocName));
			nodes[1].setChildOk(newNode);
			
			SessionAtom sibl = newNode.getChildOk().getNextSibling();
			while (sibl!=null) {
				sibl.setParent(newNode);
				sibl = sibl.getNextSibling();
			}
		}
		
		s.update();
	}
	
	private SessionAtom[] findNode(SessionAtom node, String uprocName) {
		ArrayList<SessionAtom> list = new ArrayList<SessionAtom>(); 
		SessionAtom child = node.getChildOk();
		while (child!=null) {
			list.add(child);
			if (child.getData().getUprocName().equals(uprocName))
				return new SessionAtom[] {node,child};
			child = child.getNextSibling();				
		}
		
		for (SessionAtom at : list) {
			SessionAtom[] subChild = findNode(at,uprocName);
			if (subChild!=null)
				return subChild;
		}
		
		return null;
	}
	

			
	private void createResDependency(UVMSConnection conn, String resName, String uprocName) throws Exception {
		Vector<ResourceCondition> resList = new Vector<ResourceCondition>();
		ResourceCondition rescond = new ResourceCondition();
							
		rescond.setResource(resName);
		rescond.setType(ResourceCondition.Type.FILE);
		rescond.setAttribute("EXIST");					
		rescond.setChecked(true);
						
		//Mu control		
		MuControl muControl = new MuControl();
		muControl.setType(MuControl.Type.SAME_MU);
		rescond.setMuControl(muControl);

		// common
		rescond.setExpected(true);
		rescond.setFatal(false);
		rescond.setNum(1);		
		
		resList.add(rescond);
		
		Uproc upr = conn.getUProc(uprocName);
		upr.setResourceConditions(resList);
		LaunchFormula f = new LaunchFormula();
		f.appendText("=C01");
		upr.setFormula(f);
		upr.update();			
	}	

	private void createJobDependency(UVMSConnection conn, String newJobName, String uprocName) throws Exception {		
		DependencyCondition cond = new DependencyCondition();							
		cond.setUproc(newJobName);
						
		//Mu control		
		MuControl muControl = new MuControl();
		muControl.setType(MuControl.Type.SAME_MU);
		
		cond.setMuControl(muControl);

		//Session control
		SessionControl sessionControl = new SessionControl();
		sessionControl.setType(SessionControl.Type.SAME_SESSION_AND_EXECUTION);

		cond.setSessionControl(sessionControl);

		// common
		cond.setExpected(true);
		cond.setFatal(false);
		cond.setUserControl(UserControl.ANY);//user is any
		cond.setFunctionalPeriod(FunctionalPeriod.Day);
		cond.setStatus(Status.COMPLETED);

		Uproc upr = conn.getUProc(uprocName);
		Vector<DependencyCondition> depList = upr.getDependencyConditions();
		
				
		if(depList.size()>= 0)
		{
			cond.setNum(depList.size()+1);
		}
		else
		{
			cond.setNum(1);
		}
		
		depList.add(cond);

		LaunchFormula lf = new LaunchFormula();
		String text;
		int depNum;
		   		
		   		for(int d=0;d<depList.size();d++)
    				{
    			
					   depNum=depList.get(d).getNum();	
				        
				        if(d != (depList.size()-1))
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
		
		//log(lf.getFormulaText());
		
		upr.setDependencyConditions(depList);
		upr.setFormula(lf);
		upr.update();			
	}	


	private String arrayToStr(String[] arr) {
		String s = "";
		for (String item : arr) {
			if (s.length()>0)
				s+=",";
			s+=item;
		}
		return s;	
	}

	private void addVar(UVMSConnection conn, String uprName, String value) throws Exception {
		Uproc upr = conn.getUProc(uprName);

		Vector<Variable> varVector = upr.getVariables();
				
		VariableText var = new VariableText();
		var.setName("FILENAME");		
		//remove .TRIGGER from file name
		var.setValue(value.replace(".TRIGGER",""));
		var.setLength(255);
		var.setOrigin(Variable.ORIGIN_UPROC);
		varVector.add(var);

		upr.setVariables(varVector);
		upr.update();
	}
}