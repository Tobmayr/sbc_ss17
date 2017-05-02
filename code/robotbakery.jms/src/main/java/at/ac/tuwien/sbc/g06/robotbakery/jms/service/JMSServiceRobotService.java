package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JMSServiceRobotService extends AbstractJMSService implements IServiceRobotService {
	private static Logger logger = LoggerFactory.getLogger(JMSTabletUIService.class);
	private Queue orderQueue;
	private Queue counterQueue;
	private Queue terminalQueue;
	private MessageConsumer orderConsumer;
	private MessageProducer counterProducer;
	private MessageProducer terminalQueueProducer;
	private QueueBrowser productCounterQueueBrowser;

	public JMSServiceRobotService() {

		try {
			orderQueue = session.createQueue(JMSConstants.Queue.ORDER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);
			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			counterProducer = session.createProducer(counterQueue);
			orderConsumer = session.createConsumer(orderQueue,
					String.format("%s='%s'", JMSConstants.Property.STATE, OrderState.OPEN));
			terminalQueueProducer = session.createProducer(terminalQueue);
			productCounterQueueBrowser = session.createBrowser(counterQueue,
					String.format("%s='%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public Order getNextOrder(ITransaction tx) {
		return receive(orderConsumer);
	}

	@Override
	public boolean addToCounter(List<Product> products, ITransaction tx) {
		for (Product product : products) {
			if (!send(counterProducer, product))
				return false;
		}
		return true;

	}

	@Override
	public boolean updateOrder(Order order, ITransaction tx) {
		return notify(ServiceRobot.class.getSimpleName(), false);

	}

	@Override
	public boolean putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx) {
		return send(terminalQueueProducer, packedOrder);

	}

	@Override
	public Map<String, Integer> getCounterStock() {
		Map<String, Integer> map = JMSUtil.getBrowserContentSizeByValues(productCounterQueueBrowser,
				SBCConstants.PRODUCTS_NAMES, JMSConstants.Property.TYPE);
		map.forEach((s, i) -> map.replace(s, SBCConstants.COUNTER_MAX_CAPACITY - i));
		return map;
	}

	@Override
	public List<Product> getProductsFromStorage(String productType, int amount, ITransaction tx) {
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
		notify(ServiceRobot.class.getSimpleName(), false);

	}

	@Override
	public void shutdownRobot() {
		notify(ServiceRobot.class.getSimpleName(), true);

	}

}
