import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class Namen {

	private ArrayList<String> meineNamen = new ArrayList<String>();

	public Namen() {
		meineNamen.add("Silke");
		meineNamen.add("Marvin");
		meineNamen.add("Pumuckl");
	}

	public int getAnzahl() {
		return meineNamen.size();
	}

	public List<String> getListe() {
		return meineNamen;
	}
}