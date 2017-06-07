package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_ORDER_AVAILABLE;

/**
 * 
 * @author Matthias Höllthaler
 *
 */
public class JMSDeliveryRobotService extends AbstractJMSService implements IDeliveryRobotService {

	private static Logger logger = LoggerFactory.getLogger(JMSServiceRobotService.class);
	private Queue terminalQueue;
	private MessageConsumer terminalConsumer;
	private MessageProducer destination;
	private Connection connection;
	private Queue orderQueue;
	private QueueBrowser orderQueueBrowser;

	public JMSDeliveryRobotService() {
		super(false, Session.AUTO_ACKNOWLEDGE, JMSConstants.SERVER_ADDRESS);

		try {
			orderQueue = session.createQueue(JMSConstants.Queue.ORDER);
			terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);

			orderQueueBrowser = session.createBrowser(orderQueue);
			terminalConsumer = session.createConsumer(terminalQueue,
					String.format("%s='%s'", JMSConstants.Property.DELIVERY, Order.OrderState.WAITING));

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public PackedOrder getPackedDeliveryOrder() {
		return receive(terminalConsumer);
	}

	@Override
	public boolean checkDestination(PackedOrder order) {

		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(order.getDeliveryAddress());
			connection = connectionFactory.createConnection();
			connection.start();

			Queue queue = session.createQueue(JMSConstants.Queue.DELIVERY);
			destination = session.createProducer(queue);
			destination.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			return true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean deliverOrder(PackedOrder order) {
		if (destination == null)
			return false;
		boolean success = send(destination, order);
		try {
			connection.close();
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return success;
		}

		return success;
	}

	@Override
	public boolean updateOrder(Order order) {
		return notify(order, false, orderQueue);
	}

	@Override
	public Map<String, Boolean> getInitialState() {
		Map<String, Boolean> map = new HashMap<>();
		map.put(SBCConstants.NotificationKeys.IS_DELIVERY_ORDER_AVAILABLE,
				JMSUtil.test(orderQueueBrowser, JMSConstants.Property.DELIVERY, "true") > 0);
		return map;
	}
}
