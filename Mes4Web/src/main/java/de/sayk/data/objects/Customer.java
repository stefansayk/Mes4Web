package de.sayk.data.objects;

import java.io.Serializable;

public class Customer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2264931129953985863L;
	private int id;
	private String name;
	private String contact;
	private String street;
	private String zip;
	private String city;

	
	
	public Customer(int id, String name, String contact, String street, String zip, String city) {
		super();
		this.id = id;
		this.name = name;
		this.contact = contact;
		this.street = street;
		this.zip = zip;
		this.city = city;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
