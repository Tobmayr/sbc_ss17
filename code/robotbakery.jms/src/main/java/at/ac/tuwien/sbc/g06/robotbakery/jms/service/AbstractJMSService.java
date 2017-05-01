package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class AbstractJMSService {
	private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);

	protected Connection connection;
	protected Session session;

	public AbstractJMSService() {

		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMSConstants.SERVER_ADDRESS);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Queue getOrCreateQueue(String queueName, Session session, Queue nullableQueue) {
		try {
			return nullableQueue == null ? session.createQueue(queueName) : nullableQueue;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
