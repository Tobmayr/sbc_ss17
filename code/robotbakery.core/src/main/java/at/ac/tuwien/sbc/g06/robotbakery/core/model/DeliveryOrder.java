package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthias Höllthaler on 13.05.2017.
 */
@SuppressWarnings("serial")
public class DeliveryOrder extends Order {

    private final List<Product> products;

    private final URI destination;

    public DeliveryOrder(Order order, URI destination) {
        super(order.getId());
        setCustomerId(order.getCustomerId());
        order.getItemsMap().values().forEach(i -> addItem(i.getProductName(), i.getAmount()));
        this.products = new ArrayList<Product>();
        this.destination = destination;
    }

    public void addAll(List<Product> products) {
        this.products.addAll(products);
    }

    public UUID getCustomerID() {
        return getCustomerID();
    }

    public UUID getOrderID() {
        return getId();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public URI getDestination() {
        return destination;
    }
}
