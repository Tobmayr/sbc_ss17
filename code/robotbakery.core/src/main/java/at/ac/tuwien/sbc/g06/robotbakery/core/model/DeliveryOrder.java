package at.ac.tuwien.sbc.g06.robotbakery.core.model;

@SuppressWarnings("serial")
public class DeliveryOrder extends Order {

	private String customerAddress;

	public DeliveryOrder(String customerAddress) {
		super();
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

}
