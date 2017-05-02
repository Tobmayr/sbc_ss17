package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

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
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSServiceRobotService extends AbstractJMSService implements IServiceRobotService {
	private static Logger logger = LoggerFactory.getLogger(JMSTabletUIService.class);
	private Queue orderQueue;
	private Queue counterQueue;
	private Queue terminalQueue;
	private MessageConsumer orderConsumer;
	private MessageProducer counterProducer;
	private MessageProducer terminalQueueProducer;

	public JMSServiceRobotService() {

		try {
			orderQueue = session.createQueue(JMSConstants.Queue.ORDER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);
			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			counterProducer = session.createProducer(counterQueue);
			orderConsumer = session.createConsumer(orderQueue,
					String.format("%s='%s'", JMSConstants.Property.ORDER_STATE, OrderState.OPEN));
			terminalQueueProducer = session.createProducer(terminalQueue);

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public Order getNextOrder(ITransaction tx) {
		try {
			Message msg = orderConsumer.receive(1000);
			if (msg instanceof ObjectMessage) {
				Order order = (Order) ((ObjectMessage) msg).getObject();
				return order;
			}
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public boolean addToCounter(List<Product> products, ITransaction tx) {
		try {
			for (Product product : products) {
				Message msg = session.createObjectMessage(product);
				msg.setStringProperty(JMSConstants.Property.TYPE, product.getProductName());
				counterProducer.send(msg);
			}
			return true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return false;
		}

	}

	@Override
	public boolean updateOrder(Order order, ITransaction tx) {
		try {
			ObjectMessage msg = session.createObjectMessage(order);
			notifiyObserver(msg, false);
			return true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return false;
		}

	}

	@Override
	public boolean putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx) {
		try {
			Message msg = session.createObjectMessage(packedOrder);
			msg.setStringProperty(JMSConstants.Property.CUSTOMER_ID, packedOrder.getCustomerID().toString());
			msg.setStringProperty(JMSConstants.Property.ORDER_ID, packedOrder.getOrderID().toString());
			terminalQueueProducer.send(msg);
			return true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return false;
		}

	}

	@Override
	public Map<String, Integer> getCounterStock() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Product> getProductsFromStorage(String productType,int amount, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProductsFromCounter(String productName, int amount, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startRobot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdownRobot() {
		// TODO Auto-generated method stub
		
	}

}
