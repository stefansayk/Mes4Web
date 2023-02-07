package de.sayk.web;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import de.sayk.MesTestClient;

@ManagedBean
@ApplicationScoped
public class MenuView implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] lsen = { "n.n", "n.n", "n.n", "n.n", "n.n" };
	private MesTestClient mtc = null;

	private MenuModel model;

	@PostConstruct
	public void init() {
		model = new DefaultMenuModel();

		// First submenu
		DefaultSubMenu firstSubmenu = DefaultSubMenu.builder().label("Lernsituationen").build();

		DefaultMenuItem item;

		int j = 0;
		File[] files = new File("sql").listFiles();
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
				.url("http://www.primefaces.org").build();
		secondSubmenu.getElements().add(item);

		item = DefaultMenuItem.builder().value("Starte Test SPS").icon("pi pi-server").command("#{menuView.startSPS}")
				.update("messages").build();
		secondSubmenu.getElements().add(item);

		item = DefaultMenuItem.builder().value("Stoppe Test SPS").icon("pi pi-power-off").command("#{menuView.stopSPS}")
				.update("messages").build();
		secondSubmenu.getElements().add(item);

		model.getElements().add(secondSubmenu);
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
	}

	public void ls1() {
		addMessage("SQL Skript", lsen[1]);
	}

	public void ls2() {
		addMessage("SQL Skript", lsen[2]);
	}

	public void ls3() {
		addMessage("SQL Skript", lsen[3]);
	}

	public void ls4() {
		addMessage("SQL Skript", lsen[4]);
	}

	public void redirect() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.redirect(ec.getRequestContextPath());
	}

	public void save() {
		addMessage("Save", "Data saved");
	}

	public void update() {
		addMessage("Update", "Data updated");
	}

	public void delete() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Delete", "Data deleted");
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void sleepAndSave() throws InterruptedException {
		TimeUnit.SECONDS.sleep(1);
		save();
	}

	public void sleepAndUpdate() throws InterruptedException {
		TimeUnit.SECONDS.sleep(1);
		update();
	}

	public void sleepAndDelete() throws InterruptedException {
		TimeUnit.SECONDS.sleep(1);
		delete();
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}