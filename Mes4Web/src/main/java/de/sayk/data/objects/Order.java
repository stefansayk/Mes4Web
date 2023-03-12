package de.sayk.data.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import de.sayk.data.ObjectWorld;
import de.sayk.logging.Logger;

public class Order implements Serializable {
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(Order.class.getName());

	private static final long serialVersionUID = 1896837290135696786L;
	private int id;
	private Product product;
	private String webColor;
	private Variant variant;
	private Customer costumer;
	private OrderState orderState = OrderState.PLANED;
	private Part part = null;

	private int actStep = 0;
	private boolean isActive;
	private long startStep;
	private long startProcess;

	public Order(int id, Product product, String webColor, Variant variant, Customer costumer, OrderState orderState,
			Part part) {
		super();
		this.id = id;
		this.product = product;
		this.webColor = webColor;
		this.variant = variant;
		this.costumer = costumer;
		this.orderState = orderState;
		this.part = part;
	}

	public int getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public String getWebColor() {
		return webColor;
	}

	public Variant getVariant() {
		return variant;
	}

	public Customer getCostumer() {
		return costumer;
	}

	public OrderState getOrderState() {
		return orderState;
	}

	public Part getPart() {
		return part;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	public void setCostumer(Customer costumer) {
		this.costumer = costumer;
	}

	public void setOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public void setWebColor(String webColor) {
		this.webColor = webColor;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", product=" + product + ", color=" + webColor + ", variant=" + variant
				+ ", costumer=" + costumer.getName() + ", orderState=" + orderState + ", part=" + part + "]";
	}

	public class Prop {
		public String name;
		public String value;

		public Prop(String n, String v) {
			this.name = n;
			this.value = v;
		}
	}

	public ArrayList<Prop> getOrderInfo() {
		ArrayList<Prop> p = new ArrayList<Prop>();
		p.add(new Prop("id", "" + getId()));
		p.add(new Prop("Product.id", "" + getProduct().getId()));
		p.add(new Prop("Product.name", "" + getProduct().getName()));
		p.add(new Prop("color", "" + getWebColor()));
		p.add(new Prop("Variant.name", "" + getVariant().getName()));
		for (ProductionStep ps : getVariant().getWorkflow()) {
			p.add(new Prop("ProductionStep", "" + ps.getDesc()));
		}
		p.add(new Prop("Costumer.id", "" + getCostumer().getId()));
		p.add(new Prop("Costumer.name", "" + getCostumer().getName()));
		p.add(new Prop("Costumer.contact", "" + getCostumer().getContact()));
		p.add(new Prop("Costumer.street", "" + getCostumer().getStreet()));
		p.add(new Prop("Costumer.zip", "" + getCostumer().getZip()));
		p.add(new Prop("Costumer.city", "" + getCostumer().getCity()));
		p.add(new Prop("oderState", "" + getOrderState()));
		p.add(new Prop("Part.id", "" + getPart().getId()));
		p.add(new Prop("Part.rfid", "" + getPart().getRfid()));
		p.add(new Prop("Part.color", "" + getPart().getWebColor()));
		p.add(new Prop("Part.state", "" + getPart().getPartState()));

		return p;
	}

	public int getActStep() {
		return actStep;
	}

	public void startProcess() {
		isActive = true;
		startStep = (new Date()).getTime();
		if (actStep == 0)
			startProcess = (new Date()).getTime();

		getVariant().getWorkflow().get(actStep).setActive(true);

	
	}

	public void endProcess(int nextStep) {
		long now = (new Date()).getTime();
		if (isActive) {
			isActive = false;
			
			log.debug("RFID: "+ this.getPart().getRfid());
			log.debug("actStep: " + this.actStep);
			log.debug("max Step:" + (this.getVariant().getWorkflow().size()-1));

			ObjectWorld.logMachineApi(part.getRfid(), variant.getWorkflow().get(actStep).getMachine().getId(),
					now - startStep, "step complete: " + variant.getWorkflow().get(actStep).getDesc());

			if (actStep == getVariant().getWorkflow().size() - 1) {
				setOrderState(OrderState.FINISH);
				getPart().setPartState(PartState.FINISH);

				ObjectWorld.logProcessApi(part.getRfid(), variant.getWorkflow().get(actStep).getMachine().getId(),
						now - startProcess, "production complete varaiant: " + variant.getName());
			}
			
			getVariant().getWorkflow().get(actStep).setActive(false);
			

			if (nextStep > 0)
				actStep = nextStep; // ggf. Produktionsschritte ueberspringen
			else if (actStep < getVariant().getWorkflow().size() - 1) {
				actStep++;
			}
		}
	}

	public int getClientForActiveStep() {
		return getVariant().getWorkflow().get(actStep).getMachine().getId();
	}

	public ProductionStep getActiveProductionStep() {
		return getVariant().getWorkflow().get(actStep);
	}

	public boolean isFirstStepForStore() {
		return getVariant().getWorkflow().get(actStep).getMachine().getId() == 1 && 0 == actStep;
	}

	public boolean isLastStepForStore() {
		return getVariant().getWorkflow().get(actStep).getMachine().getId() == 1
				&& getVariant().getWorkflow().size() - 1 == actStep;
	}

	public boolean isFinish() {
		return getOrderState() == OrderState.FINISH;
	}

	public static void main(String[] args) {
		try {

			Order o = ObjectWorld.getOrderById(4711);

			System.out.println(o.getWebColor());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
