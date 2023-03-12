package de.sayk.web;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeFacesContext;

import de.sayk.DatabaseListener;
import de.sayk.OrderListener;
import de.sayk.ServerStartupListener;
import de.sayk.data.Database;
import de.sayk.data.objects.Order;
import de.sayk.data.objects.OrderState;
import de.sayk.logging.Logger;

@ManagedBean
@ApplicationScoped
public class ChronolineView implements OrderListener, DatabaseListener, Serializable {
	
	private static final long serialVersionUID = 2L;

	private static Logger log = Logger.getLogger(ChronolineView.class.getName());

	private final static String COL_PLANED = "#E6E6E6";// grau
	private final static String COL_FINISHED = "#088A4B";// gruen
	private final static String COL_ACTUAL = "#B40404";// rot
	private final static String COL_ACTIVE = "#ebdb34";// gelb

	private final static String ICO_ACTIVE = "pi pi-cog";// Zahnrad
	private final static String ICO_PLANED = "pi pi-circle";// Kreis
	private final static String ICO_FINISHED = "pi pi-check";// Haken

	private List<Product> products = new ArrayList<>();

	@PostConstruct
	public void init() {
		log.debug("init...");
		try {
			ServerStartupListener.ms.addOrderListener(this);
			Database.addDataListener(this);
			log.debug("OrderListener geadded");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			log.error("problems adding action listener...", e);
			;
		}
	}
	

	public List<Product> getProducts() {
		return products;
	}

	private void addProduct(Product p) {
		products.add(p);
	}

	public static class Product {
		private String rfid;
		private String name;
		private String variant;
		private String customer;
		private List<ProdStep> prodSteps;

		public Product(Order o) {
			this.rfid = o.getPart().getRfid() + "";
			this.name = o.getProduct().getName();
			this.variant = o.getVariant().getName();
			this.customer = o.getCostumer().getName() + " (" + o.getCostumer().getContact() + ")";
			prodSteps = new ArrayList<>();

			for (int i = 0; i < o.getVariant().getWorkflow().size(); i++) {
				prodSteps.add(new ProdStep(o, i));
			}

		}

		public List<ProdStep> getProdSteps() {
			return prodSteps;
		}

		public String getRfid() {
			return rfid;
		}

		public String getName() {
			return name;
		}

		public String getVariant() {
			return variant;
		}

		public String getCustomer() {
			return customer;
		}

		public String getDescription() {
			return name + " rfid(" + rfid + "), " + variant + ", " + customer;
		}

	}

	public static class ProdStep {
		private Order order;
		private int no;
		private String machine;
		private String details;
		private String icon;
		private String color;

		public ProdStep() {
		}

		public ProdStep(Order o, int no) {
			this.order = o;
			this.no = no;
			this.machine = o.getVariant().getWorkflow().get(no).getMachine().getName();
			this.details = o.getVariant().getWorkflow().get(no).getDesc();
		}

		public String getMachine() {
			return machine;
		}

		public String getDetails() {
			return details;
		}

		public String getIcon() {

			if (order.getActStep() == no) {
				icon = order.getOrderState() == OrderState.FINISH ? ICO_FINISHED : ICO_ACTIVE;
			} else if (order.getActStep() > no) {
				icon = ICO_FINISHED;
			} else if (order.getActStep() < no) {
				icon = ICO_PLANED;
			}

			return icon;
		}

		public String getColor() {

			if (order.getActStep() == no) {
				color = order.getVariant().getWorkflow().get(no).isActive() ? COL_ACTIVE : COL_ACTUAL;
				if (order.getOrderState() == OrderState.FINISH) {
					color = COL_FINISHED;
				}
			} else if (order.getActStep() > no) {
				color = COL_FINISHED;
			} else if (order.getActStep() < no) {
				color = COL_PLANED;
			}
			return color;
		}

	}

	@Override
	public void startOrder(Order o) throws RemoteException {

		log.debug("Produktion angestossen >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		addProduct(new Product(o));

		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			PrimeFaces.current().ajax().update("idHeader:lager");
			PrimeFaces.current().ajax().update("idPlanedOrder:table");
			PrimeFaces.current().ajax().update("idActiveOrder:grid");
		}

	}

	@Override
	public void finishOrder(Order o) throws RemoteException {

		log.debug("Produktion beendet >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
// TODO Auto-generated method stub
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			PrimeFaces.current().ajax().update("idHeader:lager");
			PrimeFaces.current().ajax().update("idPlanedOrder:table");
			PrimeFaces.current().ajax().update("idActiveOrder:grid");
		}

	}

	@Override
	public void updateOrder(Order o) throws RemoteException {
		log.debug("Produktion update >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		// TODO Auto-generated method stub
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			PrimeFaces.current().ajax().update("idHeader:lager");
			PrimeFaces.current().ajax().update("idPlanedOrder:table");
			PrimeFaces.current().ajax().update("idActiveOrder:grid");
		}

	}

	@Override
	public void startProcess(Order o) throws RemoteException {
		log.debug("Produktion start prozess >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		// TODO Auto-generated method stub
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			PrimeFaces.current().ajax().update("idHeader:lager");
			PrimeFaces.current().ajax().update("idPlanedOrder:table");
			PrimeFaces.current().ajax().update("idActiveOrder:grid");
		}

	}

	@Override
	public void endProcess(Order o) throws RemoteException {

		log.debug("Produktion stop prozess >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		// TODO Auto-generated method stub
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			PrimeFaces.current().ajax().update("idHeader:lager");
			PrimeFaces.current().ajax().update("idPlanedOrder:table");
			PrimeFaces.current().ajax().update("idActiveOrder:grid");
		}

	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		 products = new ArrayList<>();
	}

}