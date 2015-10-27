package com.orsyp.client.amex;
import java.util.ArrayList;

import com.orsyp.tools.ps.DuApiConnection;
import com.orsyp.tools.ps.Connector;
public class FindUprWithVariable {

		public static void main(String[] args) throws Exception {

			String fileName = args[0];
						
			Connector conn = new Connector(fileName,true,"",false,"",false,"");
			DuApiConnection duapi = conn.getConnectionList().get(0);
			
			ArrayList<String>iter = duapi.getUprocsVariables("M9OSDDD");
			
			for(int q=0;q<iter.size();q++)
			{
				System.out.println(iter.get(q));
			}
			
		}

	}

