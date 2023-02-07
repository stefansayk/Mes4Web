package de.sayk;

import java.rmi.Remote;

import de.sayk.data.objects.OrderList;
import de.sayk.data.objects.OrderState;
import de.sayk.data.objects.Part;

public interface MesService  extends Remote{

	public abstract OrderList getAllOrdersByState(OrderState os) throws java.rmi.RemoteException;

	public abstract void refreshAllPartsInStore() throws java.rmi.RemoteException;

	public abstract void savePart(Part p) throws Exception;
	
	public abstract void nextProductionStep(int rfid) throws java.rmi.RemoteException;
	
	public abstract void addPartListener(PartListener pl) throws java.rmi.RemoteException;
	
	public abstract void addOrderListener(OrderListener ol) throws java.rmi.RemoteException;

	public abstract void addClientListener(ClientListener cl) throws java.rmi.RemoteException;
}
