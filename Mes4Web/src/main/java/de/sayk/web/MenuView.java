package de.sayk.web;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import de.sayk.Dir;
import de.sayk.MesTestClient;
import de.sayk.ServerStartupListener;
import de.sayk.data.BigData;
import de.sayk.data.Database;
import de.sayk.logging.Logger;

@ManagedBean
@ApplicationScoped
public class MenuView implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(MenuView.class.getName());

	private String[] lsen = { "n.n", "n.n", "n.n", "n.n", "n.n" };
	private MesTestClient mtc = null;

	private MenuModel model;

	private static boolean isRunningInTomcat() {
		String environment = System.getProperty("catalina.base");
		return environment.indexOf("eclipse") > -1;
	}

	private static String getHostAddress() {
		String host = "localhost";

		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return host;
	}

	@PostConstruct
	public void init() {

		model = new DefaultMenuModel();

		try {
			String sqlFolder = Dir.getHomePath() + "sql";

//			if (isRunningInTomcat()) {
//				sqlFolder = "C:\\xampp\\tomcat\\webapps\\Mes4Web-1.0\\WEB-INF\\classes\\sql";
//			}
//
//			File folder = new File(sqlFolder);
//			
//			System.out.println(sqlFolder);
//   
//			if (folder.exists() && folder.isDirectory()) {
//				System.out.println("SQL Ordner gefunden");
//			} else {
//				System.out.println("SQL Ordner existiert nicht.");
//			}

			// First submenu
			DefaultSubMenu firstSubmenu = DefaultSubMenu.builder().label("Lernsituationen").build();

			DefaultMenuItem item;

			int j = 0;
			File[] files = new File(sqlFolder).listFiles();
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".sql")) {
					lsen[j++] = file.getName();
					if (j > 4)
						break;
				}
			}

			for (int i = 0; i < lsen.length; i++) {
				item = DefaultMenuItem.builder().value(lsen[i]).icon("pi pi-refresh").command("#{menuView.ls" + i + "}")
						.update("messages").build();
				firstSubmenu.getElements().add(item);
			}

			model.getElements().add(firstSubmenu);

			// Second submenu
			DefaultSubMenu secondSubmenu = DefaultSubMenu.builder().label("Navigations").build();

			item = DefaultMenuItem.builder().value("Neuen Auftrag anlegen...").icon("pi pi-external-link")
					.url("http://" + getHostAddress() + ":8042/neworder").build();
			secondSubmenu.getElements().add(item);

			item = DefaultMenuItem.builder().value("Starte Test SPS").icon("pi pi-server")
					.command("#{menuView.startSPS}").update("messages").build();
			secondSubmenu.getElements().add(item);

			item = DefaultMenuItem.builder().value("Stoppe Test SPS").icon("pi pi-power-off")
					.command("#{menuView.stopSPS}").update("messages").build();
			secondSubmenu.getElements().add(item);

			model.getElements().add(secondSubmenu);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MenuModel getModel() {
		return model;
	}

	public void startSPS() {
		addMessage("startSPS", "SPS Client gestartet");
		if (mtc == null) {
			mtc = new MesTestClient();
			mtc.start();
		}
	}

	public void stopSPS() {
		addMessage("stoppeSPS", "SPS Client gestartet");
		if (mtc != null) {
			mtc.interruptAll();
			mtc = null;
		}
	}

	// blöd, aber in JSF lassen sich keine Parameter in Methodeaufrufen übergeben!
	public void ls0() {
		addMessage("SQL Skript", lsen[0]);
		startScript(0);
	}

	public void ls1() {
		addMessage("SQL Skript", lsen[1]);
		startScript(1);
	}

	public void ls2() {
		addMessage("SQL Skript", lsen[2]);
		startScript(2);
	}

	public void ls3() {
		addMessage("SQL Skript", lsen[3]);
		startScript(3);
	}

	public void ls4() {
		addMessage("SQL Skript", lsen[4]);
		startScript(4);
	}

	private void startScript(int i) {

		Database.setup(Dir.getHomePath() + "sql/" + lsen[i]);

		ServerStartupListener.ms.refreshAllPartsInStore();

		if (lsen[i].equals("Data Intelligence.sql"))
			BigData.addProductionData();

		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			log.debug("Upadte gestartet...");
			PrimeFaces.current().ajax().update("idHeader:lager");
			PrimeFaces.current().ajax().update("idPlanedOrder:table");
		}
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}