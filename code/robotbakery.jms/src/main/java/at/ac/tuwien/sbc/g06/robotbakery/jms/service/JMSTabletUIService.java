package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSTabletUIService extends AbstractJMSService implements ITabletUIService {
	private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);
	private Queue orderQueue;
	private MessageProducer orderProducer;
	private Queue terminalQueue;
	private MessageConsumer terminalConsumer;
	private MessageConsumer orderConsumer;

	public JMSTabletUIService() {

	}

	public void initialize(UUID customerID, UUID orderID) {
		try {
			orderQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);
			orderProducer = session.createProducer(orderQueue);
			// We keep the order in the counter as an history after payment, so
			// we need this consumer to retrieve the old order message and
			// update the state
			orderConsumer = session.createConsumer(orderQueue,
					String.format("%s= '%s'", JMSConstants.Property.ORDER_ID, orderID.toString()));
			terminalConsumer = session.createConsumer(terminalQueue,
					String.format("%s= '%s' AND %s='%s'", JMSConstants.Property.CUSTOMER_ID, customerID.toString(),
							JMSConstants.Property.ORDER_ID, orderID.toString()));

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public boolean addOrderToCounter(Order order) {
		return send(orderProducer, order);

	}

	@Override
	public PackedOrder getOrderPackage() {
		return receive(terminalConsumer);
	}

	@Override
	public boolean payOrder(Order order) {
		Order oldOrder = receive(orderConsumer);
		oldOrder.setState(OrderState.PAID);
		return send(orderProducer, oldOrder);

	}

	@Override
	public Map<String, Integer> getInitialCounterProducts() {
		// TODO Auto-generated method stub
		return null;
	}

}
