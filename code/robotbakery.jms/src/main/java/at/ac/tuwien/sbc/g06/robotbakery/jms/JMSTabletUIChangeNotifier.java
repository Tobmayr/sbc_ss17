package at.ac.tuwien.sbc.g06.robotbakery.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JMSTabletUIChangeNotifier extends TabletUIChangeNotifer implements MessageListener {
	private static Logger logger = LoggerFactory.getLogger(JMSTabletUIChangeNotifier.class);
	private TopicConnection connection;
	private TopicSession session;

	public JMSTabletUIChangeNotifier() {
		try {
			connection = JMSUtil.createAndTopicConnection();
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic notificationTopic = session.createTopic(JMSConstants.Topic.NOTIFICATION);
			TopicSubscriber subscriber = session.createSubscriber(notificationTopic);
			subscriber.setMessageListener(this);
			connection.start();
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				Serializable ser = ((ObjectMessage) message).getObject();
				boolean removed = message.getBooleanProperty(JMSConstants.Property.REMOVED);
				String priginalDest = message.getStringProperty(JMSConstants.Property.ORIGINAL_DESTINATION);
				if (ser instanceof Order) {
					if (!removed)
						registeredChangeListeners.forEach(ls -> ls.onOrderUpdated((Order) ser));
				} else if (ser instanceof Product) {
					if (priginalDest.equals(getQueueAdress(JMSConstants.Queue.COUNTER))) {
						if (removed)
							registeredChangeListeners.forEach(ls -> ls.onProductRemovedFromCounter((Product) ser));
						else
							registeredChangeListeners.forEach(ls -> ls.onProductsAddedToCounter((Product) ser));
					}
					;
				}
			}

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}

	}

	private String getQueueAdress(String queueName) {
		return "queue://" + queueName;
	}

}
