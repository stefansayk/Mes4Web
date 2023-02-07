package de.sayk;

import java.rmi.RemoteException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.sayk.logging.Logger;

public class ServerStartupListener implements ServletContextListener {
	
	private static Logger log = Logger.getLogger(MesServer.class.getName());

	private static MesServer msrv;
	private static Thread serverThread;

	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			log.debug("init servlet...");
			msrv = new MesServer();

			log.debug("starte MES...");
			msrv.setup();
			serverThread = new Thread(msrv);
			serverThread.start();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		msrv.shutdown();
	}

}
