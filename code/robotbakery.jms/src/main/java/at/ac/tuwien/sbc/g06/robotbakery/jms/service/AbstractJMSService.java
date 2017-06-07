package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class AbstractJMSService {
	private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);

	protected Connection connection;

	protected Topic notificationTopic;
	protected MessageProducer notifier;
	protected Session session;

	public AbstractJMSService(boolean transacted, int ackMode, String address) {
		try {
			connection = JMSUtil.createAndConnection(address);
			session = connection.createSession(transacted, ackMode);
			this.notificationTopic = session.createTopic(JMSConstants.Topic.NOTIFICATION);
			notifier = session.createProducer(notificationTopic);
			connection.start();

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				if (session != null)
					session.close();
				if (connection != null)
					connection.close();
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}

		}));

	}

	/**
	 * Helper method which creates an ObjectMessage containing modelObject which
	 * is passed as input argument. In addition properties which are required
	 * for this type of model object get attached to the message;
	 * 
	 * @param modelObject
	 *            Object which should be wrapped in a message
	 * @throws JMSException
	 */
	public <T extends Serializable> Message createMessage(T modelObject) throws JMSException {
		Message msg = session.createObjectMessage(modelObject);
		if (modelObject instanceof PackedOrder) {
			PackedOrder packedOrder = (PackedOrder) modelObject;
			msg.setStringProperty(JMSConstants.Property.CLASS, PackedOrder.class.getSimpleName());
			msg.setStringProperty(JMSConstants.Property.CUSTOMER_ID, packedOrder.getCustomerId().toString());
			msg.setStringProperty(JMSConstants.Property.ORDER_ID, packedOrder.getOrderID().toString());
			msg.setStringProperty(JMSConstants.Property.STATE, packedOrder.getState().toString());
			msg.setStringProperty(JMSConstants.Property.DELIVERY, "" + packedOrder.isDelivery());
		} else if (modelObject instanceof Order) {
			Order order = (Order) modelObject;
			msg.setStringProperty(JMSConstants.Property.STATE, order.getState().toString());
			msg.setStringProperty(JMSConstants.Property.CLASS, Order.class.getSimpleName());
			msg.setStringProperty(JMSConstants.Property.HIGH_PRIORITY, "" + order.isHighPriority());
		} else if (modelObject instanceof Ingredient) {
			Ingredient ingredient = (Ingredient) modelObject;
			msg.setStringProperty(JMSConstants.Property.CLASS, Ingredient.class.getSimpleName());
			msg.setStringProperty(JMSConstants.Property.TYPE, ingredient.getType().toString());
			msg.setStringProperty(JMSConstants.Property.ID, ingredient.getId().toString());
		} else if (modelObject instanceof Product) {
			Product product = (Product) modelObject;
			msg.setStringProperty(JMSConstants.Property.CLASS, Product.class.getSimpleName());
			msg.setStringProperty(JMSConstants.Property.TYPE, product.getProductName());
			msg.setStringProperty(JMSConstants.Property.STATE, product.getType().toString());
			msg.setStringProperty(JMSConstants.Property.ID, product.getId().toString());
		} else if (modelObject instanceof WaterPipe) {
			msg.setStringProperty(JMSConstants.Property.CLASS, WaterPipe.class.getSimpleName());
		} else if (modelObject instanceof Prepackage) {
			msg.setStringProperty(JMSConstants.Property.CLASS, Prepackage.class.getSimpleName());
		}
		return msg;
	}

	/**
	 * sends message to producer
	 * @param producer producer which should consume message
	 * @param messageObject object that should be a message
	 * @param <T> serializable class
	 * @return true if success, else false
	 */
	public <T extends Serializable> boolean send(MessageProducer producer, T messageObject) {
		try {
			Message msg = createMessage(messageObject);
			producer.send(msg);
			notify(messageObject, false, producer.getDestination());
			return true;
		} catch (JMSException e) {
			return false;
		}

	}

	/**
	 * sends multiple messages to producer
	 * @param producer producer which should consume message
	 * @param messageObjects list of objects that should be a message
	 * @param <T> serializable class
	 * @return true if success, else false
	 */
	public <T extends Serializable> boolean send(MessageProducer producer, List<T> messageObjects) {
		for (Serializable ser : messageObjects) {
			if (!send(producer, ser))
				return false;
		}
		return true;

	}

	public boolean notify(Serializable messageObject, boolean remove, String originalDestination) {
		try {
			Message msg = createMessage(messageObject);
			msg.setBooleanProperty(JMSConstants.Property.REMOVED, remove);
			msg.setStringProperty(JMSConstants.Property.ORIGINAL_DESTINATION, originalDestination);
			notifier.send(notificationTopic, msg);
			return true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public boolean notify(Serializable messageObject, boolean remove, Destination originalDestination) {
		return notify(messageObject, remove,
				originalDestination != null ? originalDestination.toString() : "unavailable");
	}

	public <T extends Serializable> T receive(MessageConsumer consumer) {
		try {
			Message msg = consumer.receiveNoWait();
			if (msg instanceof ObjectMessage) {
				@SuppressWarnings("unchecked")
				T cast = (T) ((ObjectMessage) msg).getObject();
				notify(cast, true, msg.getJMSDestination());
				return cast;
			}

		} catch (JMSException | ClassCastException e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	/**
	 * receive multiple elements
	 * @param consumer consumer which receives messages
	 * @param amount amount of messages
	 * @param <T> serializable class
	 * @return list with objects
	 */
	public <T extends Serializable> List<T> receive(MessageConsumer consumer, int amount) {
		List<T> list = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			T element = receive(consumer);
			if (element == null)
				return null;
			list.add(element);
		}
		return list;

	}

	public Session getSession() {
		return session;
	}

}
