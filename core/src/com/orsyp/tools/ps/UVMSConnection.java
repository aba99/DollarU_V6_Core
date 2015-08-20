package com.orsyp.tools.ps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.orsyp.Environment;
import com.orsyp.Identity;
import com.orsyp.SyntaxException;
import com.orsyp.api.Client;
import com.orsyp.api.Context;
import com.orsyp.api.central.UniCentral;
import com.orsyp.api.central.services.INetworkNodeService;
import com.orsyp.api.central.services.INodeInfoService;
import com.orsyp.central.jpa.NetworkNodeTypeEnum;
import com.orsyp.central.jpa.jpo.NetworkNodeEntity;
import com.orsyp.central.jpa.jpo.NodeInfoEntity;
import com.orsyp.comm.client.ClientServiceLocator;
import com.orsyp.std.ClientConnectionManager;
import com.orsyp.std.MultiCentralConnectionFactory;
import com.orsyp.std.central.UniCentralStdImpl;

public class UVMSConnection {
	
	private UniCentral central = null;
	private String hostname;
	private int port;
	private String user;
	private String password;
	
	
	public UVMSConnection(String hostname, int port, String user, String password) {
		this.hostname = hostname;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public ArrayList<DuNode> getNodeList() throws Exception{
		ArrayList<DuNode> nodeList = new ArrayList<DuNode>();
		try {
			central = getUVMSConnection();		
			
			INetworkNodeService nns = ClientServiceLocator.getNetworkNodeService();
			List<NetworkNodeEntity> duNodes = nns.getAllNodes((long) 0);
			
			HashMap<String,NodeInfoEntity> nodeInfo = new HashMap<String,NodeInfoEntity>(); 
			INodeInfoService nis =  ClientServiceLocator.getNodeInfoService();
			NodeInfoEntity[] nie = nis.getAllNodeInfoFromCache(-1, null);
			for (NodeInfoEntity ie : nie)
				nodeInfo.put(ie.getNodeName(), ie);
			
			for (NetworkNodeEntity nne : duNodes) 
				if (nne.getProductType().equals(NetworkNodeTypeEnum.DU) || nne.getProductType().equals(NetworkNodeTypeEnum.DU_OWLS)){
					DuNode n = new DuNode();
					n.name = nne.getName();
					n.company = nne.getCompany();
					n.host = nne.firstHost();
					n.otherData = nne.firstHost().getName();
					n.uvmsUser = user;
					n.V5 = nne.getProductType().equals(NetworkNodeTypeEnum.DU);
					NodeInfoEntity ie = nodeInfo.get(n.name);
					if (ie!=null) 
						if (!ie.getNodeOs().equalsIgnoreCase("undefined"))
							n.os=ie.getNodeOs();
					nodeList.add(n);
				}
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		}
		
		Collections.sort(nodeList, new Comparator<DuNode>() {
			@Override
			public int compare(DuNode n1, DuNode n2) {
				return n1.name.compareToIgnoreCase(n2.name);
			}
		});		
		return nodeList;
	}
	
	private UniCentral getUVMSConnection() throws SyntaxException {
		String host =hostname;
		
		UniCentral cent = new UniCentral(host, port);
		cent.setImplementation(new UniCentralStdImpl(cent));
		
		Context ctx = new Context(new Environment("UJCENT",host), new Client(new Identity(user, password, host, "W32")));
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

	public UniCentral getUniCentral() throws SyntaxException {
		if (central==null)
			return getUVMSConnection();
		else
			return central;
	}
	
}