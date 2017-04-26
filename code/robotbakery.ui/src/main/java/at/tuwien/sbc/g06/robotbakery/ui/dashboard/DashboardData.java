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
import javafx.scene.AmbientLight;

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

		ProductCount count = storageProductsCounterMap.get(product.getProductName());
		if (count == null) {
			count = new ProductCount(product.getProductName());
			storageProductsCounterMap.put(product.getProductName(), count);
			productsInStorage.add(count);
		}
		count.amount++;
		updateProductCount(count, productsInStorage);
		stateToProductsMap.get(product.getState()).add(product);

	}

	@Override
	public void onProductRemovedFromStorage(Product product) {
		ProductCount count = storageProductsCounterMap.get(product.getProductName());
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				updateProductCount(count, productsInStorage);
			} else {
				productsInStorage.remove(count);
				storageProductsCounterMap.remove(count.productName);
			}
		}

		stateToProductsMap.get(product.getState()).remove(product);

	}

	@Override
	public void onProductsAddedToCounter(Product product) {
		ProductCount count = counterProductsCounterMap.get(product.getProductName());
		if (count == null) {
			count = new ProductCount(product.getProductName());
			counterProductsCounterMap.put(product.getProductName(), count);
			productsInCounter.add(count);
		}
		count.amount++;
		updateProductCount(count, productsInCounter);
		stateToProductsMap.get(product.getState()).add(product);

	}

	@Override
	public void onProductRemovedFromCounter(Product product) {
		ProductCount count = counterProductsCounterMap.get(product.getProductName());
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				updateProductCount(count, productsInCounter);
			} else {
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

	private void updateProductCount(ProductCount productCount, ObservableList<ProductCount> list) {
		int index = list.indexOf(productCount);
		if (index != -1)
			list.set(index, productCount);
	}

	/**
	 * Helper class for representing Product name and amount in the UI
	 * 
	 * @author Tobias Ortmayr (1026279)
	 *
	 */
	public class ProductCount {

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
