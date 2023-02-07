package de.sayk;

import java.rmi.Remote;

import de.sayk.data.objects.Part;

public interface PartListener extends Remote {

	public void updatePart(Part p, int no) throws java.rmi.RemoteException;

}
