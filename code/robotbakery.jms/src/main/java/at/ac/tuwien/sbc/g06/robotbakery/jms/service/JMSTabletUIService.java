package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JMSTabletUIService extends AbstractJMSService implements ITabletUIService {
	private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);
	private Queue orderQueue;
	private MessageProducer orderProducer;
	private Queue terminalQueue;
	private Queue counterQueue;
	private MessageConsumer terminalConsumer;
	private QueueBrowser counterQueueBrowser;

	public JMSTabletUIService() {
		super(false, Session.AUTO_ACKNOWLEDGE);
		try {
			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			orderQueue = session.createQueue(JMSConstants.Queue.ORDER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);
			orderProducer = session.createProducer(orderQueue);
			// We keep the order in the counter as an history after payment, so

			counterQueueBrowser = session.createBrowser(counterQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public boolean addOrderToCounter(Order order) {
		return send(orderProducer, order);

	}

	@Override
	public PackedOrder getOrderPackage(Order order) {
		try {
			terminalConsumer = session.createConsumer(terminalQueue,
					String.format("%s= '%s' AND %s='%s'", JMSConstants.Property.CUSTOMER_ID,
							order.getCustomerId().toString(), JMSConstants.Property.ORDER_ID,
							order.getId().toString()));
			return receive(terminalConsumer);
		} catch (JMSException e) {
			e.printStackTrace();
			return null;

		}

	}

	@Override
	public boolean payOrder(PackedOrder order) {
		return send(orderProducer, order);

	}

	@Override
	public Map<String, Integer> getInitialCounterProducts() {
		return JMSUtil.getBrowserContentSizeByValues(counterQueueBrowser, SBCConstants.PRODUCTS_NAMES,
				JMSConstants.Property.TYPE);
	}

	@Override
	public Prepackage getPrepackage(UUID packageId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Prepackage> getInitialPrepackages() {
		// TODO Auto-generated method stub
		return null;
	}

}
