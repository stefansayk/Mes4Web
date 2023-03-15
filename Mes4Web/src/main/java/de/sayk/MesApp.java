package de.sayk;

import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;

import de.sayk.data.Database;
import de.sayk.logging.Logger;

public class MesApp  {
    
	private static Logger log = Logger.getLogger(MesApp.class.getName());

	public static String title = "sfmMES";
	public static String version = "1.0.BETA";
	public static String vendor = "all rights reserved by Stefan Sayk, ${TODAY}";

	private static MesServer msrv;
	private static Thread serverThread;
	public static MesService ms;

	
	public static void main(String[] args) {

		log.debug("MES gestartet...");

		try {
			URL url = MesApp.class.getResource("");
			URI uri = new URI(url.getPath().split("!")[0]);
			JarFile myJar = new JarFile(uri.getPath());
			title = myJar.getManifest().getMainAttributes().getValue("Implementation-Title");
			version = myJar.getManifest().getMainAttributes().getValue("Implementation-Version");
			vendor = myJar.getManifest().getMainAttributes().getValue("Implementation-Vendor");

			log.debug("Title: " + title);
			log.debug("Version: " + version);
			log.debug("Vendor: " + vendor);
		} catch (Exception ex) {
		}

		log.debug("setup database if not exists...");

		Database.initDb();


		log.debug("MES Server starten...");
		try {

			msrv = new MesServer();

			msrv.setup();
			serverThread = new Thread(msrv);
			serverThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ms = msrv;
		} catch (Exception e) {
			e.printStackTrace();
		}


		log.debug("stoppe services...");
		msrv.shutdown();

		log.debug("bye, bye.");

		System.exit(0);

	}

}