package de.sayk.data.objects;

import java.io.Serializable;

public class Part implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2746545445000580707L;
	private int id;
	private int rfid;
	private String webColor;
	private PartState partState = PartState.RAW;

	public Part(int id, int rfid, String webColor) {
		super();
		this.id = id;
		this.rfid = rfid;
		this.webColor = webColor;
	}

	public PartState getPartState() {
		return partState;
	}

	public void setPartState(PartState partState) {
		this.partState = partState;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRfid() {
		return rfid;
	}

	public void setRfid(int rfid) {
		this.rfid = rfid;
	}

	public String getWebColor() {
		return webColor;
	}

	public void setWebColor(String webColor) {
		this.webColor = webColor;
	}

	@Override
	public String toString() {
		return "Part [id=" + id + ", rfid=" + rfid + ", color=" + webColor + ", partState=" + partState + "]";
	}

}
