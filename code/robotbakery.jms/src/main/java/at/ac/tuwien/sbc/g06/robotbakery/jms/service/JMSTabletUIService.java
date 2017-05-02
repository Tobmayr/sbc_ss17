package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

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
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSTabletUIService extends AbstractJMSService implements ITabletUIService {
	private Queue orderQueue;
	private MessageProducer orderProducer;
	private Queue terminalQueue;
	private MessageConsumer terminalConsumer;

	public JMSTabletUIService() {

	}

	public void initialize(UUID customerID, UUID orderID) {
		try {
			orderQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);
			orderProducer = session.createProducer(orderQueue);
			terminalConsumer = session.createConsumer(orderQueue,
					String.format("%s= '%s' AND %s='%s'", JMSConstants.Property.CUSTOMER_ID, customerID.toString(),
							JMSConstants.Property.ORDER_ID, orderID.toString()));

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		order.setState(OrderState.PAID);
		return send(orderProducer, order);

	}

}
