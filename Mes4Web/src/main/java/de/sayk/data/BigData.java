package de.sayk.data;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import de.sayk.data.objects.Customer;
import de.sayk.data.objects.Order;
import de.sayk.data.objects.OrderState;
import de.sayk.data.objects.Part;
import de.sayk.data.objects.PartState;
import de.sayk.data.objects.Product;
import de.sayk.data.objects.ProductColor;
import de.sayk.data.objects.Variant;
import de.sayk.logging.Logger;

public class BigData {
	private static Logger log = Logger.getLogger(BigData.class.getName());

	/*
	 * Klasse ergänzt in der ProdData Tabelle jede Menge Logeinträge zur Analyse der
	 * Datenbasis z.B. zur Vorbeugenden Instandhaltung 03.10.2018 10:24 2 52 1 2004
	 * step complete: Lager () 03.10.2018 10:24 1 52 1 0 getOpForPartID => nothing
	 * to do 03.10.2018 10:24 1 52 2 0 getOpForPartID => nothing to do 03.10.2018
	 * 10:24 1 52 3 0 getOpForPartID =>1, 1, 0 03.10.2018 10:24 2 52 3 2005 step
	 * complete: Drucker (Layout=1) 03.10.2018 10:24 1 52 4 0 getOpForPartID =>1,
	 * 35, 10 03.10.2018 10:24 2 52 4 2003 step complete: Ofen (Temperatur=35,
	 * Trockenzeit=10) 03.10.2018 10:24 1 52 1 0 getOpForPartID =>1, 2, 0 03.10.2018
	 * 10:24 2 52 1 2005 step complete: Lager () 03.10.2018 10:24 3 52 1 32069
	 * production complete varaiant: bedrucken mit Motiv 1
	 */

	public static void addProductionData() {

		boolean ovenIsActive = false;
		boolean storeIsActive = false;
		boolean store2IsActive = false;
		boolean turningIsActive = false;
		boolean printerIsActive = false;
		Date ovenIsReady = new Date();
		Date storeIsReady = new Date();
		Date store2IsReady = new Date();
		Date turningIsReady = new Date();
		Date printerIsReady = new Date();
		long ovenStartedAt = 0;
		long storeStartedAt = 0;
		long store2StartedAt = 0;
		long turningStartedAt = 0;
		long printerStartedAt = 0;
		long productionStartedAt = 0;

		int activeVar = 0;
		int layout = 1;

		int activeMaschine = 1;
		int rfid = 4242;
		int storeNo = 0;
		int dirtyOven = 0;
		int products = 1;
		Date now = new Date();
		Random rnd = new Random(now.getTime());

		String problemColor = "";
		int problemVariantId = 0;

		Order o = null;

		ObjectWorld.deleteAllLogEntries();

		for (int i = 0; i < 20000; i++) {

			// log(java.util.Date timeStamp, int logType, int rfid, int machine, long
			// duration, String desc) {

			// Prozess
			now = addSeconds(now, 1);

			// unnuetze Anfragen stellen => MAschinenauslasung
			if (i % 30 == 0 && !storeIsActive)
				ObjectWorld.log(now, 1, rfid, 1, 0, "getOpForPartID => nothing to do");
			if (i % 10 == 0 && !turningIsActive)
				ObjectWorld.log(now, 1, rfid, 2, 0, "getOpForPartID => nothing to do");
			if (i % 15 == 0 && !printerIsActive)
				ObjectWorld.log(now, 1, rfid, 3, 0, "getOpForPartID => nothing to do");
			if (i % 100 == 0 && !ovenIsActive)
				ObjectWorld.log(now, 1, rfid, 4, 0, "getOpForPartID => nothing to do");

			// Lager fragt an ob neuer Auftrag vorhanden wenn Lager nicht aktiv und
			// Wartezeit verstrichen
			if (activeMaschine == 1 && !storeIsActive) {
				storeNo = (storeNo++ % 6);
				rfid++;
				ObjectWorld.log(now, 1, 0, 1, 0, "startOrder =>1, " + (storeNo + 1) + ", " + rfid);
				storeIsActive = true;
				activeVar = rnd.nextInt(6) + 1;

				switch (activeVar) {
				case 1:
				case 4:
					layout = 1;
					break;
				case 2:
				case 5:
					layout = 2;
					break;
				case 3:
				case 6:
					layout = 3;
					break;
				}

				try {
					ProductColor pc = ObjectWorld.getColorById(rnd.nextInt(3) + 1);

					Part pa = new Part(products, rfid, pc.getWebColor());
					pa.setPartState(PartState.FINISH);
					ObjectWorld.createPart(pa);

					Product p = ObjectWorld.getProductById(1);
					Variant v = ObjectWorld.getVariantById(activeVar);
					Customer c = ObjectWorld.getCustomerById(rnd.nextInt(15) + 1);

					o = new Order(products, p, pc.getWebColor(), v, c, OrderState.FINISH, pa);
					ObjectWorld.createOrder(o);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.debug("neuer Auftrag(" + rfid + ") mit Varianate: " + o.getVariant().getName());

				if (problemVariantId == 0) {
					problemColor = o.getWebColor();
					problemVariantId = activeVar;
				}

				productionStartedAt = now.getTime();
				storeStartedAt = now.getTime();
				ObjectWorld.log(now, 1, 0, 1, 0, "startProcess");
				storeIsReady = addSeconds(now, rnd.nextInt(2) + 5);

			}
			// wenn ja: startProcess und ca. 5s später endProcess
			if (now.after(storeIsReady) && storeIsActive) {
				storeIsActive = false;
				ObjectWorld.log(now, 1, 0, 1, 0, "endProcess");
				ObjectWorld.log(now, 2, rfid, 1, now.getTime() - storeStartedAt + rnd.nextInt(999),
						"step complete: Lager (auslagern)");
				activeMaschine++;
				if (activeVar < 4)
					activeMaschine++;
			}

			// Wenden fragt an ob etwas zu drehen ist
			if (activeMaschine == 2 && !turningIsActive) {
				ObjectWorld.log(now, 1, rfid, 2, 0, "getOpForPartID =>1, 0, 0");
				turningIsActive = true;
				turningStartedAt = now.getTime();
				ObjectWorld.log(now, 1, 0, 2, 0, "startProcess");
				turningIsReady = addSeconds(now, rnd.nextInt(5) + 12);
			}
			// wenn ja: startProcess und ca. 5s später endProcess
			if (now.after(turningIsReady) && turningIsActive) {
				turningIsActive = false;
				ObjectWorld.log(now, 1, 0, 2, 0, "endProcess");
				ObjectWorld.log(now, 2, rfid, 2, now.getTime() - turningStartedAt + rnd.nextInt(999),
						"step complete: Wenden ()");
				activeMaschine++;
			}

			// Drucken fragt an ob etwas zu drucken
			if (activeMaschine == 3 && !printerIsActive) {
				ObjectWorld.log(now, 1, rfid, 3, 0, "getOpForPartID =>1, " + layout + ", 0");
				printerIsActive = true;
				printerStartedAt = now.getTime();
				ObjectWorld.log(now, 1, 0, 3, 0, "startProcess");
				if (problemVariantId == activeVar && problemColor.equals(o.getWebColor()))
					printerIsReady = addSeconds(now, rnd.nextInt(7) + 52);
				else
					printerIsReady = addSeconds(now, rnd.nextInt(7) + 22);
			}
			// wenn ja: startProcess und ca. 5s später endProcess
			if (now.after(printerIsReady) && printerIsActive) {
				printerIsActive = false;
				ObjectWorld.log(now, 1, 0, 3, 0, "endProcess");
				ObjectWorld.log(now, 2, rfid, 3, now.getTime() - printerStartedAt + rnd.nextInt(999),
						"step complete: Drucker (Layout=" + layout + ")");
				activeMaschine++;
			}

			// Ofen fragt ob etwas zu trocknen ist
			if (activeMaschine == 4 && !ovenIsActive) {
				ObjectWorld.log(now, 1, rfid, 4, 0, "getOpForPartID =>1, " + layout + ", 0");
				ovenIsActive = true;
				ovenStartedAt = now.getTime();
				ObjectWorld.log(now, 1, 0, 4, 0, "startProcess");
				// Ofen verrusst => Heizung wird immer schwaecher => Zeit verlaengert sich
				log.debug("Ruäzeit: " + dirtyOven);
				ovenIsReady = addSeconds(now, 18 + dirtyOven);
			}
			// wenn ja: startProcess und ca. 5s später endProcess
			if (now.after(ovenIsReady) && ovenIsActive) {
				ovenIsActive = false;
				ObjectWorld.log(now, 1, 0, 4, 0, "endProcess");
				ObjectWorld.log(now, 2, rfid, 4, now.getTime() - ovenStartedAt + rnd.nextInt(999),
						"step complete: Ofen (Temperatur=35, Trockenzeit=10) (Layout=" + layout + ")");
				activeMaschine = 5;
			}

			// wieder einlagern
			if (activeMaschine == 5 && !store2IsActive) {
				ObjectWorld.log(now, 1, rfid, 1, 0, "getOpForPartID =>1, 4, 0");
				store2IsActive = true;
				store2StartedAt = now.getTime();
				ObjectWorld.log(now, 1, 0, 1, 0, "startProcess");
				store2IsReady = addSeconds(now, rnd.nextInt(2) + 5);
			}
			// wenn ja: startProcess und ca. 5s später endProcess
			if (now.after(store2IsReady) && store2IsActive) {
				store2IsActive = false;
				ObjectWorld.log(now, 1, 0, 1, 0, "endProcess");
				ObjectWorld.log(now, 2, rfid, 1, now.getTime() - store2StartedAt + rnd.nextInt(999),
						"step complete: Lager (einlagern)");
				activeMaschine = 1;

				// production complete varaiant: bedrucken mit Motiv 1
				ObjectWorld.log(now, 3, rfid, 1, now.getTime() - productionStartedAt + rnd.nextInt(999),
						"production complete varaiant: " + o.getVariant().getName());

				// offset Trockenzeit Ofen
				if (products <= 20)
					dirtyOven = 0;
				else if (products > 20 && products <= 50)
					dirtyOven = (int) (0.0003 * Math.exp((products - 20) / 3));
				else
					dirtyOven = (int) (20 * (1 - Math.exp(-1.0 * (products - 50) / 4)) + 3);
				// etwas verrauschen
				dirtyOven = dirtyOven + 1 - rnd.nextInt(3);

				log.debug(products++ + "Produkte simuliert...");

			}

		}

		log.debug("all data loged!");
	}

	public static Date addSeconds(Date date, Integer seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}

}
