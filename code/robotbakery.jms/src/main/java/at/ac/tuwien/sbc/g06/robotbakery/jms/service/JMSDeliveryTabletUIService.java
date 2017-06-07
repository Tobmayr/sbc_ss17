package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;

/**
 * Created by Matthias Höllthaler on 07.06.2017.
 */
public class JMSDeliveryTabletUIService extends JMSTabletUIService implements IDeliveryTabletUIService {

	private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);
	private String address;

	public JMSDeliveryTabletUIService(String address) {
		super();
		this.address=address;
	
		

	}

	@Override
	public URI getDeliveryURI() {
		return URI.create(address);
	}
}
