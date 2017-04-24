package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class DashboardData implements IBakeryChangeListener {

	private final ObservableList<Order> orders = FXCollections.observableArrayList();
	private final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
	private final ObservableList<Product> productsInStorage = FXCollections.observableArrayList();
	private final ObservableList<Product> productsData = FXCollections.observableArrayList();
	private final ObservableMap<String, Integer> productAmountInCounterMap = FXCollections.observableHashMap();

	public ObservableList<Order> getOrders() {
		return orders;
	}

	public ObservableList<Ingredient> getIngredients() {
		return ingredients;
	}

	public ObservableList<Product> getProductsInStorage() {
		return productsInStorage;
	}

	public ObservableList<Product> getProductsData() {
		return productsData;
	}

	public ObservableMap<String, Integer> getProductAmountInCounterMap() {
		return productAmountInCounterMap;
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
		productsInStorage.add(product);
		productsData.add(product);

	}

	@Override
	public void onProductRemovedFromStorage(Product product) {
		productsInStorage.remove(product);
		productsData.remove(product);

	}

	@Override
	public void onProductsAddedToCounter(Product product) {
		productsData.add(product);
		Integer amount = productAmountInCounterMap.get(product.getName());
		if (amount == null)
			amount = 0;
		productAmountInCounterMap.put(product.getName(), (amount+1));

	}

	@Override
	public void onProductRemovedFromCounter(Product product) {
		productsData.remove(product);
		Integer amount = productAmountInCounterMap.get(product.getName());
		if (amount != null)
			productAmountInCounterMap.put(product.getName(), amount--);
	}

	@Override
	public void onIngredientAddedToStorage(Ingredient ingredient) {
		ingredients.add(ingredient);
	}

	@Override
	public void onIngredientRemovedFromStorage(Ingredient ingredient) {
		ingredients.remove(ingredient);

	}

}
