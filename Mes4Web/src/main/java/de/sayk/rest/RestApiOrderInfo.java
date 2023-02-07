package de.sayk.rest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.sayk.data.ObjectWorld;
import de.sayk.data.objects.Order;
import de.sayk.data.objects.Part;

@Path("orderinfo")
public class RestApiOrderInfo {

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("id/{id}")
	public String info(@PathParam("id") int rfid) {

		RestApiMesSite os = new RestApiMesSite();

		os.add("        <form>");

		os.add("            <h2>Informationen zum Auftrag</h2>");

		Part p;
		try {
			p = ObjectWorld.getPartByRfid(rfid);

			try {
				Order o = ObjectWorld.getOrderForPart(p);
				ArrayList<Order.Prop> infos = o.getOrderInfo();

				for (Order.Prop prop : infos) {
					os.add("            <p>");
					os.add("                <label>" + prop.name + "</label>");
					os.add("                <text>" + prop.value + "</text>");
					os.add("            </p>");
				}
			} catch (Exception e) {
				os.add("            <p>");
				os.add("                <text>Das Produkt mit der RFID:" + p.getRfid()
						+ " wurde noch keinem Auftrag zugeordnet! Es ist im Zustand " + p.getPartState() + "</text>");
				os.add("            </p>");
			}

		} catch (Exception e) {
			os.add("            <p>");
			os.add("                <text>Kann kein Produkt mit der RFID:" + rfid + " finden!</text>");
			os.add("            </p>");
		}

		os.add("        </form>");
		
		
		//SAY TODO: Produktiondata einfügen

		return os.getSiteHTML();

	}

}