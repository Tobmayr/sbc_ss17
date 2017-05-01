package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		msg.setStringProperty(JMSConstants.Property.ORIGINAL_DESTINATION, msg.getJMSDestination().toString());
		notifier.send(notificationTopic, msg);
	}

}
