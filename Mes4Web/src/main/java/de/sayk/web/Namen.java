package de.sayk.web;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bubble.BubbleChartDataSet;
import org.primefaces.model.charts.bubble.BubbleChartModel;
import org.primefaces.model.charts.data.BubblePoint;

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
	
	
    public void createBubbleModel() {
    	BubbleChartModel bubbleModel = new BubbleChartModel();
        ChartData data = new ChartData();

        BubblePoint bp = new BubblePoint(20, 30, 15);
       
        
        BubbleChartDataSet dataSet = new BubbleChartDataSet();
        List<BubblePoint> values = new ArrayList<>();
        values.add(new BubblePoint(20, 30, 15));
        values.add(new BubblePoint(40, 10, 10));
        dataSet.setData(values);
        dataSet.setBackgroundColor("rgb(255, 99, 132)");
        dataSet.setLabel("First Dataset");
        data.addChartDataSet(dataSet);
        bubbleModel.setData(data);
    }
	
}