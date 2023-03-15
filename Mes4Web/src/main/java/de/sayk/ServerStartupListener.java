package de.sayk;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.ProcessingException;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import de.sayk.logging.Logger;

public class ServerStartupListener implements ServletContextListener {
	
	private static Logger log = Logger.getLogger(MesServer.class.getName());

	private static MesServer msrv;
	private static Thread serverThread;
	public static MesService ms;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		// MES Server starten
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
		
		try {
			ms = msrv;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//REST Server starten
		ResourceConfig config = new ResourceConfig().packages("de.sayk.rest");
        config.property(ServerProperties.TRACING, "ALL");
        config.property(ServerProperties.TRACING_THRESHOLD, "VERBOSE");

        try {
			JettyHttpContainerFactory.createServer(
			    new URI("http://localhost:8042/"),
			    config
			).start();
		} catch (ProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		msrv.shutdown();
	}

}
