package de.sayk.data.objects;

import java.io.Serializable;

public class ProductColor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6407308037052455677L;
	private int id;
	private String webColor;
	private String name;

	public ProductColor(int id, String webColor, String name) {
		this.id = id;
		this.webColor = webColor;
		this.name = name;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWebColor() {
		return webColor;
	}

	public void setWebColor(String webColor) {
		this.webColor = webColor;
	}

}
