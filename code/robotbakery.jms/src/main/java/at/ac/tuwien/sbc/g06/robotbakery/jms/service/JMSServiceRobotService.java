package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

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
	private static Logger logger = LoggerFactory.getLogger(JMSServiceRobotService.class);
	private Queue orderQueue;
	private Queue counterQueue;
	private Queue storageQueue;
	private Queue terminalQueue;
	private MessageConsumer orderConsumer;
	private MessageProducer counterProducer;
	private MessageProducer terminalQueueProducer;
	private QueueBrowser productCounterQueueBrowser;
	private Map<String, MessageConsumer> counterProductTypeConsumers = new HashMap<>();
	private Map<String, MessageConsumer> storageProductTypeConsumers = new HashMap<>();

	public JMSServiceRobotService() {
		super(true, Session.CLIENT_ACKNOWLEDGE);

		try {
			orderQueue = session.createQueue(JMSConstants.Queue.ORDER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);
			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			counterProducer = session.createProducer(counterQueue);
			orderConsumer = session.createConsumer(orderQueue,
					String.format("%s='%s'", JMSConstants.Property.STATE, OrderState.OPEN));
			terminalQueueProducer = session.createProducer(terminalQueue);
			productCounterQueueBrowser = session.createBrowser(counterQueue,
					String.format("%s='%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));

			for (String name : SBCConstants.PRODUCTS_NAMES) {
				counterProductTypeConsumers.put(name,
						session.createConsumer(counterQueue,
								String.format("%s='%s' AND %s='%s' ", JMSConstants.Property.CLASS,
										Product.class.getSimpleName(), JMSConstants.Property.TYPE, name)));
			}

			for (String name : SBCConstants.PRODUCTS_NAMES) {
				storageProductTypeConsumers.put(name,
						session.createConsumer(storageQueue,
								String.format("%s='%s' AND %s='%s' ", JMSConstants.Property.CLASS,
										Product.class.getSimpleName(), JMSConstants.Property.TYPE, name)));
			}
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public Order getNextOrder(ITransaction tx) {
		return receive(orderConsumer);
	}

	@Override
	public boolean updateOrder(Order order, ITransaction tx) {
		return notify(ServiceRobot.class.getSimpleName(), false, orderQueue);

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
	public List<Product> getProductsFromStorage(String productName, int amount, ITransaction tx) {
		return receive(storageProductTypeConsumers.get(productName), amount);

	}

	@Override
	public List<Product> getProductsFromCounter(String productName, int amount, ITransaction tx) {
		return receive(counterProductTypeConsumers.get(productName), amount);
	}

	@Override
	public void startRobot() {
		notify(ServiceRobot.class.getSimpleName(), false, storageQueue);

	}

	@Override
	public void shutdownRobot() {
		notify(ServiceRobot.class.getSimpleName(), true, storageQueue);

	}

	public Session getSession() {
		return session;

	}

	@Override
	public boolean addToCounter(Product product, ITransaction tx) {
		return send(counterProducer, product);

	}

}
