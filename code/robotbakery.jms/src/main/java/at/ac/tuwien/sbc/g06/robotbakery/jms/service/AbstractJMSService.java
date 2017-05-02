package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.activemq.command.ProducerAck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class AbstractJMSService {
	private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);

	private Connection connection;

	protected Topic notificationTopic;
	protected MessageProducer notifier;
	protected Session session;

	public AbstractJMSService() {
		try {
			connection = JMSUtil.createAndConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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

	protected void notifiyObserver(Message msg, boolean remove) throws JMSException {
		msg.setBooleanProperty(JMSConstants.Property.REMOVED, remove);
		msg.setStringProperty(JMSConstants.Property.ORIGINAL_DESTINATION,
				msg.getJMSDestination() != null ? msg.getJMSDestination().toString() : "unavailable");
		notifier.send(notificationTopic, msg);
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
		if (modelObject instanceof Order) {
			Order order = (Order) modelObject;
			msg.setStringProperty(JMSConstants.Property.STATE, order.getState().toString());
		} else if (modelObject instanceof PackedOrder) {
			PackedOrder packedOrder = (PackedOrder) modelObject;
			msg.setStringProperty(JMSConstants.Property.CUSTOMER_ID, packedOrder.getCustomerID().toString());
			msg.setStringProperty(JMSConstants.Property.ORDER_ID, packedOrder.getOrderID().toString());
		} else if (modelObject instanceof Ingredient) {
			Ingredient ingredient = (Ingredient) modelObject;
			msg.setStringProperty(JMSConstants.Property.CLASS, Ingredient.class.getSimpleName());
			msg.setStringProperty(JMSConstants.Property.TYPE, ingredient.getType().toString());
		} else if (modelObject instanceof Product) {
			Product product = (Product) modelObject;
			msg.setStringProperty(JMSConstants.Property.CLASS, Product.class.getSimpleName());
			msg.setStringProperty(JMSConstants.Property.TYPE, product.getProductName());
			msg.setStringProperty(JMSConstants.Property.STATE, product.getType().toString());
		} else if (modelObject instanceof WaterPipe) {
			msg.setStringProperty(JMSConstants.Property.CLASS, WaterPipe.class.getSimpleName());
		}
		return msg;
	}

	public boolean send(MessageProducer producer, Serializable messageObject) {
		try {
			Message msg = createMessage(messageObject);
			producer.send(msg);
			notifiyObserver(msg, false);
			return true;
		} catch (JMSException e) {
			return false;
		}

	}

	public boolean notify(Serializable messageObject, boolean remove) {
		try {
			Message msg = createMessage(messageObject);
			notifiyObserver(msg, remove);
			return true;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public <T extends Serializable> T receive(MessageConsumer consumer) {
		try {
			Message msg = consumer.receiveNoWait();
			if (msg instanceof ObjectMessage) {
				@SuppressWarnings("unchecked")
				T cast = (T) ((ObjectMessage) msg).getObject();
				notify(cast, true);
				return cast;
			}

		} catch (JMSException | ClassCastException e) {
			logger.error(e.getMessage());
		}
		return null;

	}

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

}
