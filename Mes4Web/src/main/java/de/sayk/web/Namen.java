package de.sayk.web;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.sayk.data.ObjectWorld;
import de.sayk.data.objects.Customer;

@ManagedBean
@ApplicationScoped
public class Namen {

  private ArrayList<String> meineNamen = new ArrayList<String>();
  private ArrayList<Customer> kunden = new ArrayList<Customer>();
	
	
	public Namen() {
		meineNamen.add("Silke");
		meineNamen.add("Marvin");
		meineNamen.add("Pumuckl");
		
		
		kunden = ObjectWorld.getAllCustomer();
	}

	public int getAnzahl() {
		return meineNamen.size();
	}

	public List<String> getListe() {
		return meineNamen;
	}
	
	public List<Customer> getKundenliste() {
		return kunden;
	}
	
	
}