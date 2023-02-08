package de.sayk.web;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.sayk.data.ObjectWorld;
import de.sayk.data.objects.Order;
import de.sayk.data.objects.OrderState;

@ManagedBean
@ApplicationScoped
public class Orders {


	public List<Order> getPlaned() {
		return ObjectWorld.getAllOrdersByState(OrderState.PLANED);
	}
	
	public List<Order> getFinished() {
		return ObjectWorld.getAllOrdersByState(OrderState.FINISH);
	}
	
	
}