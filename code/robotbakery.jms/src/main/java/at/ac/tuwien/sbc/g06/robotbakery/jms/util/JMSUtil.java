package at.ac.tuwien.sbc.g06.robotbakery.jms.util;

import javax.jms.Connection;
import javax.jms.JMSException;
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

	private JMSUtil() {
	};
}
