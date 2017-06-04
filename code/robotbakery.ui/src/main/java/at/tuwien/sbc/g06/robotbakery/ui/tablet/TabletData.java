package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.RecipeRegistry;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TabletData implements IChangeListener {

	private final ObservableList<CounterInformation> counterInformationData = FXCollections.observableArrayList();
	private final ObservableList<Prepackage> prepackages = FXCollections.observableArrayList();
	private final Map<String, CounterInformation> counterProductsCounterMap = new HashMap<>();
	private AbstractTabletController delegateController;

	public ObservableList<CounterInformation> getCounterInformationData() {
		return counterInformationData;
	}

	public Map<String, CounterInformation> getCounterProductsCounterMap() {
		return counterProductsCounterMap;
	}

	public ObservableList<Prepackage> getPrepackagesList() {
		return prepackages;
	}

	public TabletData() {

	}

	public void delegateController(AbstractTabletController controller) {
		this.delegateController = controller;
		// Get the initial state of the counter from the bakery. Every update
		// after this will be invoked via observer
		Map<String, Integer> initialCounterStock = delegateController.getService().getInitialCounterProducts();
		initialCounterStock.forEach((s, i) -> {
			CounterInformation info = new CounterInformation(s, i,
					RecipeRegistry.getInstance().getRecipeByName(s).getPricePerUnit());
			counterProductsCounterMap.put(s, info);
			counterInformationData.add(info);
			FXCollections.sort(counterInformationData);

		});
		prepackages.addAll(delegateController.getService().getInitialPrepackages());
		
	}

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (object instanceof Product) {
			Product product = (Product) object;
			if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_COUNTER)) {
				if (added)
					onProductsAddedToCounter(product);
				else
					onProductRemovedFromCounter(product);
			}
		} else if (object instanceof Order) {
			if (added)
				onOrderUpdated((Order) object);
		} else if (object instanceof Prepackage) {
			Prepackage prepackage = (Prepackage) object;
			if (added)
				onPrepackageAdded(prepackage);
			else
				onPrepackageRemoved(prepackage);
		}

	}

	private void onPrepackageRemoved(Prepackage prepackage) {
		prepackages.remove(prepackage);
	}

	private void onPrepackageAdded(Prepackage prepackage) {
		int index = prepackages.indexOf(prepackage);
		if (index == -1)
			prepackages.add(prepackage);
		else
			prepackages.set(index, prepackage);

	}

	private void onProductsAddedToCounter(Product product) {
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

	private void onProductRemovedFromCounter(Product product) {
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

	private void onOrderUpdated(Order order) {
		delegateController.onOrderUpdated(order);

	}

	public class CounterInformation implements Comparable<CounterInformation> {
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

		@Override
		public int compareTo(CounterInformation o) {
			return this.getType().compareTo(o.getType());
		}

	}

}
