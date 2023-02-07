package de.sayk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import de.sayk.logging.Logger;

public class MesTestClient extends Thread {
	private static Logger log = Logger.getLogger(MesTestClient.class.getName());

	public static void main(String[] args) throws Exception {

		MesTestClient mtc = new MesTestClient();
		mtc.start();

	}

	@Override
	public void run() {
		ServerResponse sr;

		log.debug(Thread.interrupted() ? "TestClient beendet." : "TestClient laeuft...");

		int rfidPalette = 1;

		try {
			while (!Thread.interrupted()) {

				Thread.sleep(2000);
				sr = getMesData(400, 1, rfidPalette, 0); // start order
				log.debug(sr.toLog("startOrder", 1, 0, 0));
				if (sr.returnCode > 0) {

					rfidPalette++;
					
					int rfid = sr.outPara2;

					Thread.sleep(2000);
					sr = getMesData(200, 1, rfid, 0); // startProcess
					log.debug(sr.toLog("startProcess", 1, rfid, 0));

					Thread.sleep(2000);
					sr = getMesData(300, 1, rfid, 0); // endProcess
					log.debug(sr.toLog("endProcess", 1, rfid, 0));

					ProductionPart pp = new ProductionPart(rfid);
					pp.start();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void interruptAll() {

		this.interrupt();
		ProductionPart.interruptAll();

	}

	static class ProductionPart extends Thread {

		static ArrayList<ProductionPart> parts = new ArrayList<ProductionPart>();

		int rfid;
		Random rnd = new Random();

		public ProductionPart(int rfid) {
			this.rfid = rfid;
			parts.add(this);
		}

		public static void interruptAll() {
			for (ProductionPart productionPart : parts) {
				productionPart.interrupt();
			}
		}

		@Override
		public void run() {
			ServerResponse sr;

			try {

				for (int clientId = 1; clientId < 6; clientId++) {

					if (clientId == 5)
						clientId = 1;

					sr = getMesData(100, clientId, rfid, 0); // getOpForPartID
					log.debug(sr.toLog("getOpForPartID", clientId, rfid, 0));
					if (sr.returnCode > 0) {

						sr = getMesData(200, clientId, rfid, 0); // startProcess
						log.debug(sr.toLog("startProcess", clientId, rfid, 0));

						int runtime = rnd.nextInt(2000) + 1000;
						Thread.sleep(runtime);
						sr = getMesData(300, clientId, rfid, 0); // endProcess
						log.debug(sr.toLog("endProcess", clientId, rfid, 0));
					}
					Thread.sleep(1000);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static synchronized ServerResponse getMesData(int a, int b, int c, int d) throws Exception {

		ServerResponse sr = new ServerResponse();

		Socket clientSocket = new Socket("localhost", 2018);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

		outToServer.writeShort(a);
		outToServer.writeShort(b);
		outToServer.writeShort(c);
		outToServer.writeShort(d);

		sr.returnCode = inFromServer.readShort();
		sr.outPara1 = inFromServer.readShort();
		sr.outPara2 = inFromServer.readShort();

		clientSocket.close();

		return sr;
	}

}
