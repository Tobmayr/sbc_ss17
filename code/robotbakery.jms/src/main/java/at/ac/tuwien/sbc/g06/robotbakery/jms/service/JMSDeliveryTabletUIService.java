package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;

/**
 * Created by Matthias Höllthaler on 07.06.2017.
 */
public class JMSDeliveryTabletUIService implements IDeliveryTabletUIService {

	private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);
	private String address;
	private JMSTabletUIService service;

	public JMSDeliveryTabletUIService(JMSTabletUIService service, String address) {
		super();
		this.service = service;
		this.address = address;

	}

	@Override
	public URI getDeliveryURI() {
		return URI.create(address);
	}

	@Override
	public boolean addOrderToCounter(Order order) {
		return service.addOrderToCounter(order);
	}

	@Override
	public PackedOrder getPackedOrder(Order order) {
		return service.getPackedOrder(order);
	}

	@Override
	public boolean payOrder(PackedOrder order) {
		return service.payOrder(order);
	}

	@Override
	public Map<String, Integer> getInitialCounterProducts() {
		return service.getInitialCounterProducts();
	}

	@Override
	public List<Prepackage> getInitialPrepackages() {
		return service.getInitialPrepackages();
	}

	@Override
	public Prepackage getPrepackage(UUID packageId) {
		return service.getPrepackage(packageId);
	}

	@Override
	public boolean updatePrepackage(Prepackage prepackage) {
		return service.updatePrepackage(prepackage);
	}
}
