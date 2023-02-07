package de.sayk;

import java.rmi.Remote;

import de.sayk.data.objects.Order;

public interface OrderListener extends Remote{

	public void startOrder(Order o) throws java.rmi.RemoteException;

	public void finishOrder(Order o) throws java.rmi.RemoteException;

	public void updateOrder(Order o) throws java.rmi.RemoteException;

	public void startProcess(Order o) throws java.rmi.RemoteException;

	public void endProcess(Order o) throws java.rmi.RemoteException;

}
