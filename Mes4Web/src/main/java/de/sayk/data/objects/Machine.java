package de.sayk.data.objects;

import java.io.Serializable;

public class Machine implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2828340494781476744L;
	private int id;
	private String name;
	private String namePara1;
	private String namePara2;

	public Machine(int id, String name, String namePara1, String namePara2) {
		super();
		this.id = id;
		this.name = name;
		this.namePara1 = namePara1;
		this.namePara2 = namePara2;
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


	public String getNamePara1() {
		return namePara1;
	}

	public void setNamePara1(String namePara1) {
		this.namePara1 = namePara1;
	}

	public String getNamePara2() {
		return namePara2;
	}

	public void setNamePara2(String namePara2) {
		this.namePara2 = namePara2;
	}

	@Override
	public String toString() {
		return "Machine [id=" + id + ", name=" + name + ", namePara1=" + namePara1 + ", namePara2=" + namePara2 + "]";
	}

}
