package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSTabletUIService extends AbstractJMSService implements ITabletUIService {

	private Queue orderQueue;
	private MessageProducer orderProducer;

	public JMSTabletUIService() {
		orderQueue = getOrCreateQueue(JMSConstants.ORDER_QUEUE, session, orderQueue);
		try {
			orderProducer = session.createProducer(orderQueue);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean addOrderToCounter(Order order) {
		try {
			ObjectMessage msg = session.createObjectMessage(order);
			orderProducer.send(msg);
			return true;
		} catch (JMSException e) {
			return false;
		}
	}

	@Override
	public PackedOrder getOrderPackage(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean payOrder(Order order) {
		// TODO Auto-generated method stub
		return false;
	}

}
