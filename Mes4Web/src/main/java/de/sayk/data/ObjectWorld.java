package de.sayk.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import de.sayk.data.objects.Customer;
import de.sayk.data.objects.Machine;
import de.sayk.data.objects.Order;
import de.sayk.data.objects.OrderList;
import de.sayk.data.objects.OrderState;
import de.sayk.data.objects.Part;
import de.sayk.data.objects.PartState;
import de.sayk.data.objects.Product;
import de.sayk.data.objects.ProductColor;
import de.sayk.data.objects.ProductionStep;
import de.sayk.data.objects.Variant;
import de.sayk.logging.Logger;

public class ObjectWorld {

	private static Logger log = Logger.getLogger(ObjectWorld.class.getName());

	static Database db = new Database();
	static Connection con = Database.getCon();

	public static void main(String[] args) throws Exception {

		Part p = getPartById(1);
		Machine m = getMachinesById(2);

		Order o = getOrderById(4711);

		log.debug(p);
	}

	// Zur Dokumentation aller Anfragen �ber das SPS API auch der Anfragen, die
	// keine Aktion ausl�sen.
	public static void logSpsApi(int rfid, int machine, String desc) {
		log(1, rfid, machine, 0, desc);
	}

	// Dokumentation eines Arbeitsschrittes einer Maschine
	public static void logMachineApi(int rfid, int machine, long duration, String desc) {
		log(2, rfid, machine, duration, desc);
	}

	// Dokumentation eines kompletten Produktionsvorgangs
	public static void logProcessApi(int rfid, int machine, long duration, String desc) {
		log(3, rfid, machine, duration, desc);
	}

	public static void log(int logType, int rfid, int machine, long duration, String desc) {
		java.util.Date now = new java.util.Date();
		log(now, logType, rfid, machine, duration, desc);
	}

	public static void log(java.util.Date timeStamp, int logType, int rfid, int machine, long duration, String desc) {

		try {

			Timestamp t = new Timestamp(timeStamp.getTime());

			String insertTableSQL = "INSERT INTO MES.PROD_DATA"
					+ "(LOG_TIME, LOG_TYPE, RFID, MACHINE, DURATION, MESSAGE) VALUES" + "(?,?,?,?,?,?)";

			PreparedStatement preparedStatement = con.prepareStatement(insertTableSQL);
			preparedStatement.setTimestamp(1, t);
			preparedStatement.setInt(2, logType);
			preparedStatement.setInt(3, rfid);
			preparedStatement.setInt(4, machine);
			preparedStatement.setLong(5, duration);
			preparedStatement.setString(6, desc);
			// execute insert SQL statement
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			log.error("problem insert log entry", e);
		}

	}

	public static ArrayList<String> getAllLogEntriesForRfid(int rfid) {

		ArrayList<String> temp = new ArrayList<String>();
		Statement stmt = null;
		String query = "SELECT * FROM MES.PROD_DATA WHERE RFID=" + rfid + "ORDER BY LOG_TIME";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String row = "time:" + rs.getString("LOG_TIME") + " / type:" + rs.getString("LOG_TYPE") + " / machine:"
						+ rs.getString("MACHINE") + " / duration:" + rs.getString("DURATION") + " / message:"
						+ rs.getString("MESSAGE");
				temp.add(row);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static void deleteAllLogEntries() {

		try {

			String insertTableSQL = "DELETE FROM MES.PROD_DATA";

			PreparedStatement preparedStatement = con.prepareStatement(insertTableSQL);
			// execute insert SQL statement
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			log.error("problem deleting logentries from PROD_DATA", e);
		}

	}

	public static int getId(String tableName) throws Exception {

		Statement stmt = null;
		String query = "SELECT MAX(" + tableName + "_ID) FROM MES." + tableName + "S";

		int id = 0;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				id = rs.getInt(1);
			} else
				throw new Exception("problem generating unique id for:" + tableName);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return ++id;

	}

	public static ArrayList<Machine> getAllMachines() {

		ArrayList<Machine> temp = new ArrayList<Machine>();
		Statement stmt = null;
		String query = "SELECT * FROM MES.MACHINES ORDER BY MACHINE_ID";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Machine m = new Machine(rs.getInt("MACHINE_ID"), rs.getString("NAME"), rs.getString("NAME_PARA1"),
						rs.getString("NAME_PARA2"));
				temp.add(m);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static Machine getMachinesById(int id) throws Exception {

		ArrayList<Machine> temp = new ArrayList<Machine>();
		Statement stmt = null;
		String query = "SELECT * FROM MES.MACHINES WHERE MACHINE_ID=" + id;

		Machine m = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				m = new Machine(rs.getInt("MACHINE_ID"), rs.getString("NAME"), rs.getString("NAME_PARA1"),
						rs.getString("NAME_PARA2"));
				temp.add(m);
			} else
				throw new Exception("getMachinesById machine not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return m;

	}

	public static ArrayList<Part> getAllParts() {

		ArrayList<Part> temp = new ArrayList<Part>();
		Statement stmt = null;
		String query = "SELECT * FROM MES.PARTS ORDER BY PART_ID";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Part p = new Part(rs.getInt("PART_ID"), rs.getInt("RFID"), rs.getString("WEB_COLOR"));
				switch (rs.getInt("PART_STATE")) {
				case 0:
					p.setPartState(PartState.RAW);
					break;
				case 1:
					p.setPartState(PartState.FINISH);
					break;
				}
				temp.add(p);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static Part getPartById(int id) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.PARTS WHERE PART_ID=" + id;

		Part p = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				ProductColor pc = getColorById(rs.getInt("COLOR_ID"));

				p = new Part(rs.getInt("PART_ID"), rs.getInt("RFID"), pc.getWebColor());
				switch (rs.getInt("PART_STATE")) {
				case 0:
					p.setPartState(PartState.RAW);
					break;
				case 1:
					p.setPartState(PartState.FINISH);
					break;
				}
			} else
				throw new Exception("getPartById part not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return p;

	}

	public static Part getPartByRfid(int rfid) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.PARTS WHERE RFID=" + rfid;

		Part p = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				ProductColor pc = getColorById(rs.getInt("COLOR_ID"));

				p = new Part(rs.getInt("PART_ID"), rs.getInt("RFID"), pc.getWebColor());
				switch (rs.getInt("PART_STATE")) {
				case 0:
					p.setPartState(PartState.RAW);
					break;
				case 1:
					p.setPartState(PartState.FINISH);
					break;
				}
			} else
				throw new Exception("getPartByRfid part not found with rfid:" + rfid);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return p;

	}

	public static void createPart(Part p) throws Exception {

		PreparedStatement stmt = null;
		StringBuffer insert = new StringBuffer();
		insert.append("INSERT INTO MES.PARTS (PART_ID, RFID, COLOR_ID, PART_STATE) VALUES (");

		insert.append(p.getId() + ", ");
		insert.append(p.getRfid() + ", ");
		insert.append((getColorByWebCode(p.getWebColor())).getId() + ", ");
		switch (p.getPartState()) {
		case FINISH:
			insert.append("1);");
			break;

		default:
			insert.append("0);");
			break;
		}

		try {
			stmt = con.prepareStatement(insert.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("problem with query: " + insert.toString(), e);
			throw new Exception("can't create part in database:" + p);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void saveOrder(Order o) throws Exception {

		PreparedStatement stmt = null;
		StringBuffer update = new StringBuffer();
		update.append("UPDATE MES.ORDERS SET ");

		update.append("PRODUCT_ID=" + o.getProduct().getId() + ", ");
		update.append("COLOR_ID=" + (getColorByWebCode(o.getWebColor())).getId() + ", ");
		update.append("CUSTOMER_ID=" + o.getCostumer().getId() + ", ");
		update.append("VARIANT_ID=" + o.getVariant().getId() + ", ");
		update.append("PART_ID=" + o.getPart().getId() + ", ");

		switch (o.getOrderState()) {
		case PLANED:
			update.append("ORDER_STATE=0 ");
			break;
		case IN_PRODUCTION:
			update.append("ORDER_STATE=1 ");
			break;
		case FINISH:
			update.append("ORDER_STATE=2 ");
			break;
		}

		update.append("WHERE ORDER_ID=" + o.getId());

		try {
			stmt = con.prepareStatement(update.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("problem with query: " + update.toString(), e);
			throw new Exception("can't update order in database:" + o);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void savePart(Part p) throws Exception {

		PreparedStatement stmt = null;
		StringBuffer update = new StringBuffer();
		update.append("UPDATE MES.PARTS SET ");

		update.append("RFID=" + p.getRfid() + ", ");
		update.append("COLOR_ID=" + (getColorByWebCode(p.getWebColor())).getId() + ", ");
		switch (p.getPartState()) {
		case FINISH:
			update.append("PART_STATE=1 ");
			break;

		default:
			update.append("PART_STATE=0 ");
			break;
		}
		update.append("WHERE PART_ID=" + p.getId());

		try {
			stmt = con.prepareStatement(update.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("problem with query: " + update.toString(), e);
			throw new Exception("can't update part in database:" + p);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static ArrayList<Product> getAllProducts() {

		ArrayList<Product> temp = new ArrayList<Product>();
		Statement stmt = null;
		String query = "SELECT * FROM MES.PRODUCTS ORDER BY PRODUCT_ID";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Product p = new Product(rs.getInt("PRODUCT_ID"), rs.getString("NAME"));
				temp.add(p);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static ArrayList<ProductColor> getAllColors() {

		ArrayList<ProductColor> temp = new ArrayList<ProductColor>();
		Statement stmt = null;
		String query = "SELECT * FROM MES.COLORS ORDER BY COLOR_ID";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				ProductColor pc = new ProductColor(rs.getInt("COLOR_ID"), rs.getString("WEB_COLOR"),
						rs.getString("NAME"));
				temp.add(pc);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static ArrayList<Variant> getAllVariants() {

		ArrayList<Variant> temp = new ArrayList<Variant>();
		Statement stmtSteps = null;
		Statement stmt = null;
		String query = "SELECT * FROM MES.VARIANTS ORDER BY VARIANT_ID";

		try {
			stmt = con.createStatement();
			stmtSteps = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Variant v = new Variant(rs.getInt("VARIANT_ID"), rs.getString("NAME"));

				String querySteps = "SELECT * FROM MES.PRODUCTION_STEPS WHERE VARIANT_ID=" + rs.getInt("VARIANT_ID")
						+ " ORDER BY STEP_NO";
				ResultSet rsSteps = stmtSteps.executeQuery(querySteps);
				while (rsSteps.next()) {
					Machine m = getMachinesById(rsSteps.getInt("MACHINE_ID"));
					ProductionStep step = new ProductionStep(rsSteps.getInt("MACHINE_ID"), m, rsSteps.getInt("PARA1"),
							rsSteps.getInt("PARA2"));
					v.addProductionStep(step);
				}

				temp.add(v);
				rsSteps.close();
			}
			rs.close();
		} catch (Exception e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmtSteps != null) {
				try {
					stmtSteps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static ArrayList<Customer> getAllCustomer() {

		ArrayList<Customer> temp = new ArrayList<Customer>();
		Statement stmt = null;
		String query = "SELECT * FROM MES.CUSTOMERS ORDER BY CUSTOMER_ID";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Customer c = new Customer(rs.getInt("CUSTOMER_ID"), rs.getString("NAME"), rs.getString("CONTACT"),
						rs.getString("STREET"), rs.getString("ZIP"), rs.getString("CITY"));
				temp.add(c);
			}
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static Product getProductById(int id) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.PRODUCTS WHERE PRODUCT_ID=" + id;

		Product p = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				p = new Product(rs.getInt("PRODUCT_ID"), rs.getString("NAME"));
			} else
				throw new Exception("getProductById product not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return p;

	}

	public static Customer getCustomerById(int id) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.CUSTOMERS WHERE CUSTOMER_ID=" + id;

		Customer c = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				c = new Customer(rs.getInt("CUSTOMER_ID"), rs.getString("NAME"), rs.getString("CONTACT"),
						rs.getString("STREET"), rs.getString("ZIP"), rs.getString("CITY"));
			} else
				throw new Exception("getCustomerById customer not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return c;

	}

	public static ProductColor getColorById(int id) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.COLORS WHERE COLOR_ID=" + id;

		ProductColor pc = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				pc = new ProductColor(rs.getInt("COLOR_ID"), rs.getString("WEB_COLOR"), rs.getString("NAME"));
			} else
				throw new Exception("getColorById color not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return pc;

	}

	public static ProductColor getColorByWebCode(String webCode) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.COLORS WHERE WEB_COLOR='" + webCode + "'";

		ProductColor pc = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				pc = new ProductColor(rs.getInt("COLOR_ID"), rs.getString("WEB_COLOR"), rs.getString("NAME"));
			} else
				throw new Exception("getColorById color not found with web code:" + webCode);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return pc;

	}

	public static ProductionStep getProductionStepById(int id) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.PRODUCTION_STEPS WHERE PRODUCTION_STEP_ID=" + id;

		ProductionStep p = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				Machine m = getMachinesById(rs.getInt("MACHINE_ID"));
				p = new ProductionStep(rs.getInt("PRODUCTION_STEP_ID"), m, rs.getInt("PARAM1"), rs.getInt("PARAM1"));
			} else
				throw new Exception("getProductionStepById productionstep not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return p;

	}

	public static Variant getVariantById(int id) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.VARIANTS WHERE VARIANT_ID=" + id;

		Variant v = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {

				v = new Variant(rs.getInt("VARIANT_ID"), rs.getString("NAME"));

				String querySteps = "SELECT * FROM MES.PRODUCTION_STEPS WHERE VARIANT_ID=" + rs.getInt("VARIANT_ID")
						+ " ORDER BY STEP_NO";
				ResultSet rsSteps = stmt.executeQuery(querySteps);
				while (rsSteps.next()) {
					Machine m = getMachinesById(rsSteps.getInt("MACHINE_ID"));
					ProductionStep step = new ProductionStep(rsSteps.getInt("MACHINE_ID"), m, rsSteps.getInt("PARA1"),
							rsSteps.getInt("PARA2"));
					v.addProductionStep(step);
				}
				rsSteps.close();
			} else
				throw new Exception("getProductionStepById productionstep not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return v;

	}

	public static void createOrder(Order o) throws Exception {

		PreparedStatement stmt = null;
		StringBuffer insert = new StringBuffer();
		insert.append(
				"INSERT INTO MES.ORDERS (ORDER_ID, PRODUCT_ID, COLOR_ID, VARIANT_ID, CUSTOMER_ID, ORDER_STATE, PART_ID) VALUES (");

		insert.append(o.getId() + ", ");
		insert.append(o.getProduct().getId() + ", ");
		insert.append((getColorByWebCode(o.getWebColor())).getId() + ", ");
		insert.append(o.getVariant().getId() + ", ");
		insert.append(o.getCostumer().getId() + ", ");
		switch (o.getOrderState()) {
		case IN_PRODUCTION:
			insert.append("1, ");
			break;

		case FINISH:
			insert.append("2, ");
			break;

		default:
			insert.append("0, ");
			break;
		}
		if (o.getPart() != null)
			insert.append(o.getPart().getId() + ");");
		else
			insert.append("NULL);");

		try {
			stmt = con.prepareStatement(insert.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("problem with query: " + insert.toString(), e);
			throw new Exception("can't create oder in database:" + o);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Order getOrderById(int id) throws Exception {

		Statement stmt = null;
		String query = "SELECT * FROM MES.ORDERS WHERE ORDER_ID=" + id;

		Order o = null;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {

				Product prod = getProductById(rs.getInt("PRODUCT_ID"));
				ProductColor pc = getColorById(rs.getInt("COLOR_ID"));
				Customer cust = getCustomerById(rs.getInt("CUSTOMER_ID"));
				Variant v = getVariantById(rs.getInt("VARIANT_ID"));
				Part part = null;
				if (rs.getInt("PART_ID") != 0)
					part = getPartById(rs.getInt("PART_ID"));
				;

				o = new Order(rs.getInt("ORDER_ID"), prod, pc.getWebColor(), v, cust, OrderState.PLANED, part);

				switch (rs.getInt("ORDER_STATE")) {
				case 0:
					o.setOrderState(OrderState.PLANED);
					break;
				case 1:
					o.setOrderState(OrderState.IN_PRODUCTION);
					break;
				case 2:
					o.setOrderState(OrderState.FINISH);
					break;
				}
			} else
				throw new Exception("getOrderById order not found with id:" + id);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return o;

	}

	public static Order getOrderForPart(Part part) throws Exception {
		return getOrderForRfid(part.getRfid());
	}

	public static Order getOrderForRfid(int rfid) throws Exception {

		Statement stmt = null;
		String query = "SELECT ORDERS.ORDER_ID FROM MES.ORDERS INNER JOIN MES.PARTS ON ORDERS.PART_ID=PARTS.PART_ID WHERE PARTS.RFID="
				+ rfid;

		int orderId = 0;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				orderId = rs.getInt("ORDER_ID");
			} else
				throw new Exception("getOrderForPart order not found with rfid:" + rfid);
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return getOrderById(orderId);

	}

	public static OrderList getAllOrdersByState(OrderState os) {

		OrderList temp = new OrderList();
		Statement stmt = null;
		String query = "SELECT * FROM MES.ORDERS WHERE ORDER_STATE=" + os.ordinal() + " ORDER BY ORDER_ID";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {

				Product prod = getProductById(rs.getInt("PRODUCT_ID"));
				ProductColor pc = getColorById(rs.getInt("COLOR_ID"));
				Customer cust = getCustomerById(rs.getInt("CUSTOMER_ID"));
				Variant v = getVariantById(rs.getInt("VARIANT_ID"));
				Part part = null;
				if (rs.getInt("PART_ID") != 0)
					part = getPartById(rs.getInt("PART_ID"));
				;

				Order o = new Order(rs.getInt("ORDER_ID"), prod, pc.getWebColor(), v, cust, os, part);

				temp.add(o);
			}
			rs.close();
		} catch (Exception e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static Part[] getAllPartsInStore() {

		Part[] temp = new Part[6];
		Statement stmt = null;
		String query = "SELECT * FROM MES.STOCKYARDS ORDER BY STOCKYARD_ID";
		int pid = 0;

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				pid = rs.getInt("PART_ID");
				if (pid != 0) {
					temp[rs.getInt("STOCKYARD_ID") - 1] = getPartById(pid);
				}
			}
			rs.close();
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} catch (Exception e) {
			log.error("problem getPartById: " + pid, e);
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return temp;

	}

	public static void removePartFromStore(int stockNo) throws Exception {

		PreparedStatement stmt = null;
		StringBuffer update = new StringBuffer();
		update.append("UPDATE MES.STOCKYARDS SET ");

		update.append("PART_ID=0 ");
		update.append("WHERE STOCKYARD_ID=" + stockNo);

		try {
			stmt = con.prepareStatement(update.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("problem with query: " + update.toString(), e);
			throw new Exception("can't remove part in stock:" + stockNo);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void addPartToStore(int stockNo, int partId) throws Exception {

		PreparedStatement stmt = null;
		StringBuffer update = new StringBuffer();
		update.append("UPDATE MES.STOCKYARDS SET ");

		update.append("PART_ID=" + partId + " ");
		update.append("WHERE STOCKYARD_ID=" + stockNo);

		try {
			stmt = con.prepareStatement(update.toString());
			stmt.execute();
		} catch (SQLException e) {
			log.error("problem with query: " + update.toString(), e);
			throw new Exception("can't add part " + partId + " in stock:" + stockNo);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
