package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_COUNTER_EMPTY;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_ORDER_AVAILABLE;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_ORDER_PROCESSING_LOCKED;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_PREPACKAGE_LIMIT;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.NO_MORE_PRODUCTS_IN_STORAGE;

import java.util.ArrayList;
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

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
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
	private QueueBrowser storageQueueBrowser;
	private QueueBrowser orderQueueBrowser;
	private QueueBrowser terminalQueueBrowser;

	public JMSServiceRobotService() {
		super(true, Session.SESSION_TRANSACTED, JMSConstants.SERVER_ADDRESS);

		try {
			orderQueue = session.createQueue(JMSConstants.Queue.ORDER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);
			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			counterProducer = session.createProducer(counterQueue);
			orderConsumer = session.createConsumer(orderQueue, String.format("%s='%s' OR %s='%s' ",
					JMSConstants.Property.STATE, OrderState.ORDERED, JMSConstants.Property.STATE, OrderState.WAITING));
			terminalQueueProducer = session.createProducer(terminalQueue);
			storageQueueBrowser = session.createBrowser(storageQueue);
			orderQueueBrowser = session.createBrowser(orderQueue,
					String.format("%s='%s'", JMSConstants.Property.CLASS, Order.class.getSimpleName()));

			terminalQueueBrowser = session.createBrowser(terminalQueue);
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
		return notify(order, false, orderQueue);

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
		MessageConsumer consumer = storageProductTypeConsumers.get(productName);
		List<Product> list = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			Product element = receive(consumer);
			if (element == null)
				return list;
			list.add(element);
		}
		return null;
	}

	@Override
	public List<Product> getProductsFromCounter(String productName, int amount, ITransaction tx) {
		return receive(counterProductTypeConsumers.get(productName), amount);
	}

	public Session getSession() {
		return session;

	}

	@Override
	public boolean addToCounter(Product product, ITransaction tx) {
		return send(counterProducer, product);

	}

	@Override
	public boolean returnOrder(Order currentOrder, ITransaction tx) {
		return send(counterProducer, currentOrder);
	}

	@Override
	public boolean putPrepackageInTerminal(Prepackage prepackage, ITransaction tx) {
		return send(terminalQueueProducer, prepackage);
	}

	@Override
	public List<Product> getProductsFromStorage(int amount, ITransaction tx) {
		List<Product> readProducts = JMSUtil.toList(storageQueueBrowser, JMSConstants.Property.CLASS,
				Product.class.getSimpleName(), amount);
		List<Product> finalList = new ArrayList<>();
		for (Product product : readProducts) {
			Product received = receive(storageProductTypeConsumers.get(product.getProductName()));
			if (received != null) {
				finalList.add(received);
			} else {
				return null;
			}

		}

		return finalList;
	}

	@Override
	public Map<String, Boolean> getInitialState() {
		Map<String, Boolean> notificationState = new HashMap<>();
		notificationState.put(IS_COUNTER_EMPTY, JMSUtil.test(productCounterQueueBrowser, JMSConstants.Property.CLASS,
				Product.class.getSimpleName()) == 0);
		notificationState.put(NO_MORE_PRODUCTS_IN_STORAGE, JMSUtil.test(productCounterQueueBrowser,
				JMSConstants.Property.CLASS, Product.class.getSimpleName()) == 0);
		notificationState.put(IS_ORDER_AVAILABLE,
				JMSUtil.test(orderQueueBrowser, JMSConstants.Property.CLASS, Order.class.getSimpleName()) > 0);
		notificationState.put(IS_PREPACKAGE_LIMIT, JMSUtil.test(terminalQueueBrowser, JMSConstants.Property.CLASS,
				Prepackage.class.getSimpleName()) >= SBCConstants.PREPACKAGE_MAX_AMOUNT);
		notificationState.put(IS_ORDER_PROCESSING_LOCKED,
				JMSUtil.test(orderQueueBrowser, JMSConstants.Property.HIGH_PRIORITY, "true") > 0);
		return notificationState;
	}

	@Override
	public boolean sendNotification(NotificationMessage notification, ITransaction tx) {
		return notify(notification, false, counterQueue);
	}

}
