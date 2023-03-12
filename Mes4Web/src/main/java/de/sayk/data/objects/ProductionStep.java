package de.sayk.data.objects;

import java.io.Serializable;
import java.io.StringWriter;

public class ProductionStep implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8282263110099501504L;
	private int id;
	private Machine machine;
	int para1;
	int para2;
	boolean isActive;

	public ProductionStep(int id, Machine machine, int para1, int para2) {
		super();
		this.id = id;
		this.machine = machine;
		this.para1 = para1;
		this.para2 = para2;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public int getPara1() {
		return para1;
	}

	public void setPara1(int para1) {
		this.para1 = para1;
	}

	public int getPara2() {
		return para2;
	}

	public void setPara2(int para2) {
		this.para2 = para2;
	}

	public String getDesc() {
		
		String p1 = this.getPara1()==-1 ? "OID" : this.getPara1() + "";
		String p2 = this.getPara2()==-1 ? "OID" : this.getPara2() + "";
		
		StringWriter sw = new StringWriter();
//		sw.append(machine.getName());
		sw.append(" (");
		if(this.getPara1()!=0) {
			sw.append(machine.getNamePara1());
			sw.append("=");
		sw.append(p1);	
		}
		if(this.getPara2()!=0) {
			sw.append(", ");
			sw.append(machine.getNamePara2());
			sw.append("=");
		sw.append(p2);	
		}
		sw.append(")");

		return sw.toString();
	}

	@Override
	public String toString() {
		return "ProductionStep [id=" + id + ", machine=" + machine + ", para1=" + para1 + ", para2=" + para2 + "]";
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	
	
}
