package de.sayk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.sayk.data.ObjectWorld;
import de.sayk.data.objects.Machine;
import de.sayk.data.objects.Order;
import de.sayk.data.objects.OrderList;
import de.sayk.data.objects.OrderState;
import de.sayk.data.objects.Part;
import de.sayk.data.objects.PartState;
import de.sayk.logging.Logger;

public class MesServer implements Runnable, MesService {

	private static Logger log = Logger.getLogger(MesServer.class.getName());

	private static ArrayList<PartListener> partListeners = new ArrayList<PartListener>();
	private static ArrayList<OrderListener> orderListeners = new ArrayList<OrderListener>();
	private static ArrayList<ClientListener> clientListeners = new ArrayList<ClientListener>();
	private static Part[] parts = new Part[6];
	private static HashMap<Integer, Order> orders = new HashMap<Integer, Order>();
	private static int lastPlaceNo;

	private boolean interrupt = false;
	ServerSocket serverSocket = null;

	protected MesServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setup() {
		parts = ObjectWorld.getAllPartsInStore();
	}

	public void shutdown() {
		try {
			interrupt = true;
			serverSocket.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {

		int i = 0;

		Socket s = null;

		try {
			serverSocket = new ServerSocket(2018);
			log.info("Server listening on port:2018");

		} catch (IOException e) {
			log.error("problem starting TCI/IP Server", e);
			e.printStackTrace();
		}

		while (!interrupt) {
			try {
				s = serverSocket.accept();
			} catch (IOException e) {
				log.error("problem accepting socket", e);
				e.printStackTrace();
			}
			// new thread for a client
			new MessageThread(s).start();
			log.info("new client accepted socket on port:2018");
		}
	}

	public class MessageThread extends Thread {
		protected Socket socket;

		public MessageThread(Socket clientSocket) {
			this.socket = clientSocket;
		}

		public void run() {

			DataInputStream dataInputStream = null;
			DataOutputStream dataOutputStream = null;

			try {
				String desc = "";
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				InetAddress caller = socket.getInetAddress();
				int methodId = dataInputStream.readShort();
				int clientId = dataInputStream.readShort();
				int inPara1 = dataInputStream.readShort();
				int inPara2 = dataInputStream.readShort();

				ServerResponse sr = new ServerResponse();
				long start = new Date().getTime();
				log.info("new request: methodId:" + methodId + ", clientId:" + clientId + ", inPara1:" + inPara1
						+ ", inPara2:" + inPara2);
				switch (methodId) {
				case 100: // getOpForPartID(clientId, rfid)
					sr = getOpForPartID(clientId, inPara1);
					if (sr.returnCode == 0) {
						desc = "getOpForPartID => nothing to do";
					} else {
						desc = sr.toLog("getOpForPartID", clientId, inPara1, inPara2);
					}
					break;
				case 200: // startProcess(rfid)
					sr = startProcess(inPara1);
					desc = sr.toLog("startProcess", clientId, inPara1, inPara2);
					break;
				case 300: // endProcess(rfid, <<stepNo>>)
					sr = endProcess(inPara1, inPara2);
					desc = sr.toLog("endProcess", clientId, inPara1, inPara2);
					break;
				case 400: // startOrder
					if (clientId == 1) { // only store
						sr = startOrder(inPara1);
					}
					desc = sr.toLog("startOrder", clientId, inPara1, inPara2);
					break;

				default:
					break;
				}

				dataOutputStream.writeShort(sr.returnCode);
				dataOutputStream.writeShort(sr.outPara1);
				dataOutputStream.writeShort(sr.outPara2);

				for (ClientListener cl : clientListeners) {

					cl.newClientRequest(clientId, clientNamelookupp(clientId), caller.getHostName(),
							">" + methodId + ", " + clientId + ", " + inPara1 + ", " + inPara2 + "=>" + sr.returnCode
									+ ", " + sr.outPara1 + ", " + sr.outPara2);

				}

				ObjectWorld.logSpsApi(clientId, inPara1, desc);

				log.info("request complete: returnCode:" + sr.returnCode + ", outPara1:" + sr.outPara1 + ", outPara2:"
						+ sr.outPara2);
				long runningTime = new Date().getTime() - start;
				log.info("request complete in [ms]:" + runningTime);

			} catch (Exception e) {
				log.error("problem TCI/IP communication", e);
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						log.error("problem closing socket", e);
						e.printStackTrace();
					}
				}

				if (dataInputStream != null) {
					try {
						dataInputStream.close();
					} catch (IOException e) {
						log.error("problem closing input stream", e);
						e.printStackTrace();
					}
				}

				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						log.error("problem closing output stream", e);
						e.printStackTrace();
					}
				}
			}

		}
	}

	private static String clientNamelookupp(int cid) {
		Machine m = null;
		try {
			m = ObjectWorld.getMachinesById(cid);
		} catch (Exception e) {
		}

		if (m == null)
			return "??? id " + cid + " not found";
		else
			return m.getName();
	}

	private static int getFreePlaceNo() {
		int temp = 0;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] == null) {
				temp = i + 1;
				break;
			}
		}
		return temp;
	}

	private static synchronized ServerResponse startOrder(int rfid) {
		ServerResponse sr = new ServerResponse();

		// check if raw part with right color in store
		boolean orderFound = false;
		for (Order o : ObjectWorld.getAllOrdersByState(OrderState.PLANED)) {
			for (int i = 0; i < parts.length; i++) {
				Part part = parts[i];
				if (part != null && o.getWebColor().equalsIgnoreCase(part.getWebColor())
						&& part.getPartState() == PartState.RAW) {

					if (rfid > 0) {
						// RFID wird von der SPS �bergeben
						part.setRfid(rfid);
						try {
							ObjectWorld.savePart(part);
						} catch (Exception e) {
							log.error(e);
						}
					}

					o.setPart(part);
					o.setOrderState(OrderState.IN_PRODUCTION);

					lastPlaceNo = i + 1;
					sr.outPara1 = i + 1;
					sr.outPara2 = part.getRfid();
					sr.returnCode = o.getId();
					orderFound = true;

					// Update in Datenbank
					try {
						ObjectWorld.saveOrder(o);
					} catch (Exception e) {
						log.error(e);
					}

					for (OrderListener ol : orderListeners) {
						// alle informieren Auftrag wird aktiv angezeigt
						try {
							ol.startOrder(o);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					// zur Liste der Auftraege hinzufuegen
					orders.put(o.getPart().getRfid(), o);

					break;
				}
			}
			if (orderFound)
				break;
		}

		return sr;
	}

	private static synchronized ServerResponse getOpForPartID(int clientId, int rfidNo) {
		ServerResponse sr = new ServerResponse();
		// if order is active
		if (orders.containsKey(rfidNo)) {
			Order activeOrder = orders.get(rfidNo);

			// check if there is something to do
			if (activeOrder.getClientForActiveStep() == clientId && !activeOrder.isFinish()) {
				if (activeOrder.getPart().getRfid() == rfidNo) {
					if (activeOrder.isLastStepForStore()) {
						int placeNo = getFreePlaceNo();
						if (placeNo != 0) {
							sr.returnCode = activeOrder.getId();
							sr.outPara1 = placeNo;
						}
					} else {
						sr.returnCode = 1;
						sr.outPara1 = activeOrder.getActiveProductionStep().getPara1();
						sr.outPara2 = activeOrder.getActiveProductionStep().getPara2();

						// negative Parameter stehen f�r Produktionsvariablen
						// -1 => Auftragsnummer
						// -2 => ...
						sr.outPara1 = sr.outPara1 == -1 ? activeOrder.getId() : sr.outPara1;
						sr.outPara2 = sr.outPara2 == -1 ? activeOrder.getId() : sr.outPara2;

					}
				}
			}
		}

		return sr;
	}

	private static synchronized ServerResponse startProcess(int rfidNo) {
		ServerResponse sr = new ServerResponse();
		if (orders.containsKey(rfidNo)) {
			Order activeOrder = orders.get(rfidNo);

			if (!activeOrder.isFinish()) {

				activeOrder.startProcess();
				sr.returnCode = 1;

				for (OrderListener ol : orderListeners) {
					// alle informieren Arbeitsschritt wird farbig angezeigt
					try {
						ol.startProcess(activeOrder);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// wenn es der erste oder letzte Schritt ist das Lager informieren
				if (activeOrder.isFirstStepForStore()) {
					if (lastPlaceNo != 0) {
						parts[lastPlaceNo - 1] = null;

						
						//in Datenbank löschen
						try {
							ObjectWorld.removePartFromStore(lastPlaceNo);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						
						for (PartListener pl : partListeners) {
							try {
								pl.updatePart(null, lastPlaceNo);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				if (activeOrder.isLastStepForStore()) {
					int placeNo = getFreePlaceNo();
					if (placeNo != 0) {
						parts[placeNo - 1] = activeOrder.getPart();

						
						//In Datenbank eintragen
						try {
							ObjectWorld.addPartToStore(placeNo, activeOrder.getPart().getId());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						
						
						for (PartListener pl : partListeners) {
							try {
								pl.updatePart(activeOrder.getPart(), placeNo);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			}
		}

		return sr;
	}

	public static synchronized ServerResponse endProcess(int rfidNo, int nextStep) {
		ServerResponse sr = new ServerResponse();
		if (orders.containsKey(rfidNo)) {
			Order activeOrder = orders.get(rfidNo);

			if (!activeOrder.isFinish()) {

				activeOrder.endProcess(nextStep);

				sr.returnCode = 1;
				
				
				// Update in Datenbank
				try {
					ObjectWorld.saveOrder(activeOrder);
					ObjectWorld.savePart(activeOrder.getPart());
				} catch (Exception e) {
					log.error(e);
				}


				for (OrderListener ol : orderListeners) {
					// alle informieren Arbeitsschritt wird fertig angezeigt
					try {
						ol.endProcess(activeOrder);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (activeOrder.isFinish()) {
					for (OrderListener ol : orderListeners) {
						try {
							ol.finishOrder(activeOrder);
							orders.remove(rfidNo);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return sr;
	}

	@Override
	public OrderList getAllOrdersByState(OrderState os) {
		return ObjectWorld.getAllOrdersByState(os);
	}

	@Override
	public void refreshAllPartsInStore() {

		log.debug("refreshAllParts...");

		// load fresh form database
		parts = ObjectWorld.getAllPartsInStore();
		orders = new HashMap<Integer, Order>();
		
		log.debug("benachitige " + partListeners.size() + " Listener");
		for (PartListener pl : partListeners) {
			log.debug("benachitige " + parts.length + " Teile");
			for (int i = 0; i < parts.length; i++) {
				try {
					log.debug("server an client: update...");
					pl.updatePart(parts[i], i + 1);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void savePart(Part p) throws Exception {
		ObjectWorld.savePart(p);
	}

	@Override
	public void nextProductionStep(int rfid) {
		if (orders.containsKey(rfid)) {
			Order activeOrder = orders.get(rfid);
			// next production step
			startProcess(activeOrder.getPart().getRfid());
			endProcess(activeOrder.getPart().getRfid(), 0);
		} else {
			// start new Order if possible
			ServerResponse sr = startOrder(0);
		}
	}

	@Override
	public void addPartListener(PartListener pl) {
		partListeners.add(pl);
	}

	@Override
	public void addOrderListener(OrderListener ol) {
		orderListeners.add(ol);
	}

	@Override
	public void addClientListener(ClientListener cl) {
		clientListeners.add(cl);
	}

	
	
}
