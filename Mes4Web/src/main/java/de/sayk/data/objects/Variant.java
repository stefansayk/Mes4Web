package de.sayk.data.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Variant implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6688710650780204611L;
	/**
	 * 
	 */
	private int id;
	private String name;
	private ArrayList<ProductionStep> workflow = new ArrayList<ProductionStep>();

	public Variant(int id, String name) {
		super();
		this.id = id;
		this.name = name;
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

	public ArrayList<ProductionStep> getWorkflow() {
		return workflow;
	}

	public void setWorkflow(ArrayList<ProductionStep> workflow) {
		this.workflow = workflow;
	}

	public void addProductionStep(ProductionStep ps) {
		workflow.add(ps);
	}

	@Override
	public String toString() {
		return "Variant [id=" + id + ", name=" + name + ", workflow=" + workflow + "]";
	}

}
