package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ProductState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class DashboardData implements IBakeryChangeListener {

	private final ObservableList<Order> orders = FXCollections.observableArrayList();
	private final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
	private final ObservableList<ProductCount> productsInStorage = FXCollections.observableArrayList();
	private final Map<String, ProductCount> storageProductsCounterMap = new HashMap<>();
	private final ObservableList<ProductCount> productsInCounter = FXCollections.observableArrayList();
	private final Map<String, ProductCount> counterProductsCounterMap = new HashMap<>();

	private final Map<ProductState, ObservableList<Product>> stateToProductsMap = new HashMap<ProductState, ObservableList<Product>>();

	public DashboardData() {
		Arrays.asList(ProductState.values())
				.forEach(state -> stateToProductsMap.put(state, FXCollections.observableArrayList()));

	}

	public ObservableList<Order> getOrders() {
		return orders;
	}

	public ObservableList<Ingredient> getIngredients() {
		return ingredients;
	}

	public ObservableList<ProductCount> getProductsInStorage() {
		return productsInStorage;
	}

	public ObservableList<ProductCount> getProductsInCounter() {
		return productsInCounter;
	}

	public Map<ProductState, ObservableList<Product>> getStateToProductsMap() {
		return stateToProductsMap;
	}

	@Override
	public void onOrderAddedOrUpdated(Order order) {
		int index = orders.indexOf(order);
		if (index == -1)
			orders.add(order);
		else
			orders.set(index, order);
	}

	@Override
	public void onProductAddedToStorage(Product product) {

		ProductCount count = storageProductsCounterMap.get(product.getName());
		if (count == null) {
			count = new ProductCount(product.getName());
			storageProductsCounterMap.put(product.getName(), count);
			productsInStorage.add(count);
		}
		count.amount++;
		stateToProductsMap.get(product.getState()).add(product);

	}

	@Override
	public void onProductRemovedFromStorage(Product product) {
		ProductCount count = storageProductsCounterMap.get(product.getName());
		if (count != null) {
			if (count.amount > 0)
				count.amount--;
			else {
				productsInStorage.remove(count);
				storageProductsCounterMap.remove(count.productName);
			}
		}

		stateToProductsMap.get(product.getState()).remove(product);

	}

	@Override
	public void onProductsAddedToCounter(Product product) {
		ProductCount count = counterProductsCounterMap.get(product.getName());
		if (count == null) {
			count = new ProductCount(product.getName());
			counterProductsCounterMap.put(product.getName(), count);
			productsInCounter.add(count);
		}
		count.amount++;
		stateToProductsMap.get(product.getState()).add(product);

	}

	@Override
	public void onProductRemovedFromCounter(Product product) {
		ProductCount count = counterProductsCounterMap.get(product.getName());
		if (count != null) {
			if (count.amount > 0)
				count.amount--;
			else {
				productsInCounter.remove(count);
				counterProductsCounterMap.remove(count.productName);
			}
		}

		stateToProductsMap.get(product.getState()).remove(product);
	}

	@Override
	public void onIngredientAddedToStorage(Ingredient ingredient) {
		ingredients.add(ingredient);
	}

	@Override
	public void onIngredientRemovedFromStorage(Ingredient ingredient) {
		ingredients.remove(ingredient);

	}

	/**
	 * Helper class for representing Product name and amount in the UI
	 * 
	 * @author Tobias Ortmayr (1026279)
	 *
	 */
	class ProductCount {

		private final String productName;
		private int amount;

		public ProductCount(String productName) {
			this.productName = productName;
		}

		public String getProductName() {
			return productName;
		}

		public int getAmount() {
			return amount;
		}

	}

}
