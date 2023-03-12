package de.sayk.web;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.sayk.data.ObjectWorld;
import de.sayk.data.objects.Part;
import de.sayk.data.objects.PartState;

@ManagedBean
@ApplicationScoped
public class Parts {

	private List<StoreLine> storeLines;

	public List<StoreLine> getAllPartsInStore() {

		storeLines = new ArrayList<>();

		Part[] parts = ObjectWorld.getAllPartsInStore();

		storeLines.add(new StoreLine(parts[0], parts[2], parts[4], 1, 3, 5));
		storeLines.add(new StoreLine(parts[1], parts[3], parts[5], 2, 4, 6));

		return storeLines;
	}

	public static class StoreLine {
		int no1;
		int no2;
		int no3;
		private Part p1;
		private Part p2;
		private Part p3;

		public StoreLine(Part part1, Part part2, Part part3, int no1,int no2,int no3) {
			this.p1 = part1;
			this.p2 = part2;
			this.p3 = part3;
			this.no1 = no1;
			this.no2 = no2;
			this.no3 = no3;
		}

		public String getRfid1() {
			if (p1 == null)
				return "";
			return p1.getRfid() + "";
		}

		public String getState1() {
			if (p1 == null)
				return "";
			return (p1.getPartState() == PartState.RAW ? "ROH" : "FERTIG");
		}

		public String getColor1() {
			if (p1 == null)
				return "";
			return p1.getWebColor();
		}

		public String getRfid2() {
			if (p2 == null)
				return "";
			return p2.getRfid() + "";
		}

		public String getState2() {
			if (p2 == null)
				return "";
			return (p2.getPartState() == PartState.RAW ? "ROH" : "FERTIG");
		}

		public String getColor2() {
			if (p2 == null)
				return "";
			return p2.getWebColor();
		}

		public String getRfid3() {
			if (p3 == null)
				return "";
			return p3.getRfid() + "";
		}

		public String getState3() {
			if (p3 == null)
				return "";
			return (p3.getPartState() == PartState.RAW ? "ROH" : "FERTIG");
		}

		public String getColor3() {
			if (p3 == null)
				return "";
			return p3.getWebColor();
		}

		public int getNo1() {
			return no1;
		}

		public int getNo2() {
			return no2;
		}

		public int getNo3() {
			return no3;
		}

	}

}