package de.sayk;

import java.rmi.Remote;

public interface ClientListener extends Remote{
	public void newClientRequest(int mid, String name, String ip, String request) throws java.rmi.RemoteException;
}
