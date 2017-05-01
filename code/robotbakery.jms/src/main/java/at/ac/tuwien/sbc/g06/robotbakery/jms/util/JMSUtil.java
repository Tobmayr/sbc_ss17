package at.ac.tuwien.sbc.g06.robotbakery.jms.util;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.TopicConnection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSUtil {
	private static Logger logger = LoggerFactory.getLogger(JMSUtil.class);

	public static Connection createAndConnection() throws JMSException {
		Connection connection;
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMSConstants.SERVER_ADDRESS);
		connection = connectionFactory.createConnection();
		connection.start();
		return connection;

	}

	public static TopicConnection createAndTopicConnection() throws JMSException {
		TopicConnection connection;
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMSConstants.SERVER_ADDRESS);
		connection = connectionFactory.createTopicConnection();
		connection.start();
		return connection;

	}

	public static int size(QueueBrowser browser) throws JMSException {
		int count = 0;
		Enumeration<?> messages = browser.getEnumeration();
		while (messages.hasMoreElements()) {
			count++;
			messages.nextElement();

		}
		return count;
	}

	private JMSUtil() {
	};
}
