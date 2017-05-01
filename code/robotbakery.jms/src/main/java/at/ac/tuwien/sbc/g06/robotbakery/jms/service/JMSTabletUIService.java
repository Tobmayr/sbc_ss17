package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSTabletUIService extends AbstractJMSService implements ITabletUIService {
	private static Logger logger = LoggerFactory.getLogger(JMSTabletUIService.class);

	private Queue orderQueue;
	private MessageProducer orderProducer;
	private Queue terminalQueue;
	private MessageConsumer terminalConsumer;
	private UUID customerID;
	private UUID orderID;

	public JMSTabletUIService() {

	}

	public void initialize(UUID customerID, UUID orderID) {
		try {
			this.customerID = customerID;
			this.orderID = orderID;
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
		try {
			ObjectMessage msg = session.createObjectMessage(order);
			msg.setStringProperty(JMSConstants.Property.ORDER_STATE, order.getState().toString());
			orderProducer.send(msg);
			notifiyObserver(msg, false);
			return true;
		} catch (JMSException e) {
			return false;
		}
	}

	@Override
	public PackedOrder getOrderPackage() {
		try {
			Message msg = terminalConsumer.receive(2000);
			if (msg instanceof ObjectMessage) {
				PackedOrder packedOrder = (PackedOrder) ((ObjectMessage) msg).getObject();
				return packedOrder;
			}

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public boolean payOrder(Order order) {
		try {
			order.setState(OrderState.PAID);
			ObjectMessage msg = session.createObjectMessage(order);
			msg.setStringProperty(JMSConstants.Property.ORDER_STATE, order.getState().toString());
			orderProducer.send(msg);
			notifiyObserver(msg, false);
			return true;
		} catch (JMSException e) {
			return false;
		}
	}

}
