package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.ITableUIChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TabletData implements ITableUIChangeListener {

	private final ObservableList<CounterInformation> counterInformationData = FXCollections.observableArrayList();
	private final Map<String, CounterInformation> counterProductsCounterMap = new HashMap<>();
	private TabletController delegateController;

	public ObservableList<CounterInformation> getCounterInformationData() {
		return counterInformationData;
	}

	public Map<String, CounterInformation> getCounterProductsCounterMap() {
		return counterProductsCounterMap;
	}

	public TabletData() {

	}

	public void delegateController(TabletController controller) {
		this.delegateController = controller;
		// Get the initial state of the counter from the bakery. Every update
		// after this will be invoked via observer
		List<Product> products = delegateController.getService().getInitialCounterProducts();
		products.forEach(product -> onProductsAddedToCounter(product));
	}

	@Override
	public void onProductsAddedToCounter(Product product) {
		CounterInformation info = counterProductsCounterMap.get(product.getProductName());
		if (info == null) {
			info = new CounterInformation(product.getProductName(), 0, product.getRecipe().getPricePerUnit());
			counterProductsCounterMap.put(product.getProductName(), info);
			counterInformationData.add(info);
		}
		info.stock++;
		int index = counterInformationData.indexOf(info);
		if (index != -1)
			counterInformationData.set(index, info);

	}

	@Override
	public void onProductRemovedFromCounter(Product product) {
		CounterInformation info = counterProductsCounterMap.get(product.getProductName());
		if (info != null) {
			if (info.stock > 0) {
				info.stock--;
				int index = counterInformationData.indexOf(info);
				if (index != -1)
					counterInformationData.set(index, info);

			} else {
				counterInformationData.remove(info);
				counterProductsCounterMap.remove(info.type);
			}
		}

	}

	@Override
	public void onOrderUpdated(Order order) {
		delegateController.onOrderUpdated(order);

	}

	public class CounterInformation {
		private final String type;
		private int stock;
		private final double pricePerPiece;

		public CounterInformation(String type, int stock, double pricePerPiece) {
			super();
			this.type = type;
			this.stock = stock;
			this.pricePerPiece = pricePerPiece;
		}

		public String getType() {
			return type;
		}

		public Integer getStock() {
			return stock;
		}

		public double getPricePerPiece() {
			return pricePerPiece;
		}

		@Override
		public int hashCode() {
			return type.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CounterInformation) {
				CounterInformation that = (CounterInformation) obj;
				return this.type.equals(that.type);
			}
			return super.equals(obj);
		}

	}

}
