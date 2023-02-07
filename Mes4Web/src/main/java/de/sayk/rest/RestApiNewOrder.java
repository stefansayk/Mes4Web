package de.sayk.rest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.sayk.data.ObjectWorld;
import de.sayk.data.objects.Customer;
import de.sayk.data.objects.Order;
import de.sayk.data.objects.OrderState;
import de.sayk.data.objects.Product;
import de.sayk.data.objects.ProductColor;
import de.sayk.data.objects.Variant;



@Path("/neworder")
public class RestApiNewOrder {

	
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String info() {

		RestApiMesSite os = new RestApiMesSite();

		os.add("        <form>");
		os.add("            <h2>neuen Auftrag anlegen</h2>");
		os.add("            <p>");
		os.add("                <label>Produkt</label>");
		os.add("                <select id=\"product\">");
		ArrayList<Product> prods = ObjectWorld.getAllProducts();
		for (Product product : prods) {
			os.add("                    <option value=\"" + product.getId() + "\">" + product.getName() + "</option>");
		}
		os.add("                </select>");
		os.add("            </p>");
		os.add("            <p>");
		os.add("                <label>Farbe</label>");
		os.add("                <select id=\"color\">");
		ArrayList<ProductColor> colors = ObjectWorld.getAllColors();
		for (ProductColor pColor : colors) {
			os.add("                    <option value=\"" + pColor.getId() + "\" style=\"background:"
					+ pColor.getWebColor() + ";\">" + pColor.getName() + "</option>");
		}
		os.add("                </select>");
		os.add("            </p>");
		os.add("            <p>");
		os.add("                <label>Variante</label>");
		os.add("                <select id=\"variant\">");
		ArrayList<Variant> vars = ObjectWorld.getAllVariants();
		for (Variant variant : vars) {
			os.add("                    <option value=\"" + variant.getId() + "\">" + variant.getName() + "</option>");
		}
		os.add("                </select>");
		os.add("            </p>");
		os.add("            <p>");
		os.add("                <label>Kunde</label>");
		os.add("                <select id=\"customer\">");
		ArrayList<Customer> custs = ObjectWorld.getAllCustomer();
		for (Customer c : custs) {
			os.add("                    <option value=\"" + c.getId() + "\">" + c.getName() + ", "
					+ c.getName() + "</option>");
		}
		os.add("                </select>");
		os.add("            </p>");
		os.add("        </form>");
		os.add("");
		os.add("        <a href=\"javascript:newOrder()\" class=\"myButton\">anlegen</a>");

		return os.getSiteHTML();

	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("product/{product}/color/{color}/variant/{variant}/customer/{customer}")
	public String message(@PathParam("product") int prodId, @PathParam("color") int colorId,
			@PathParam("variant") int varId, @PathParam("customer") int custId) {

		try {
			// do some validation
			Product prod = ObjectWorld.getProductById(prodId);
			ProductColor pc = ObjectWorld.getColorById(colorId);
			Customer cust = ObjectWorld.getCustomerById(custId);
			Variant v = ObjectWorld.getVariantById(varId);

			// get new unique id
			int id = ObjectWorld.getId("ORDER");

			// save new Order
			Order o = new Order(id, prod, pc.getWebColor(), v, cust, OrderState.PLANED, null);

			ObjectWorld.createOrder(o);

			//MesMainView.addNewOrder(o);

			return "New order sucsessfull created, id: " + id;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "ERROR: No new order created: " + e.getMessage();
		}
	}


}