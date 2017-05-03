package at.ac.tuwien.sbc.g06.robotbakery.jms.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.TopicConnection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;

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

	public static int test(QueueBrowser browser, String property, String value) {
		return test(browser, new String[] { property }, new String[] { value });

	}

	/**
	 * A helper method which provides similar functionality as the capi.test()
	 * method from XVSM. It counts the messages in the queue that have
	 * properties with matching values attached
	 * 
	 * @param browser
	 *            Browser for the queue which should be tested
	 * @param properties
	 * @param valueues
	 * @return
	 * @throws JMSException
	 */
	public static int test(QueueBrowser browser, String[] properties, String[] values) {
		List<?> list = toList(browser, properties, values,null);
		return list == null ? -1 : list.size();
	}

	/**
	 * A helper method which is used to filter the elements of a QueueBrowser
	 * with a set of properties-values. All matching elements are return in a
	 * list.
	 *
	 * 
	 * @param browser
	 *            Browser for the queue which should be tested
	 * @param properties
	 * @param maxSize
	 * @param valueues
	 * @return
	 * @throws JMSException
	 */

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> List<T> toList(QueueBrowser browser, String[] properties, String[] values,
			Integer maxSize) {
		List<T> list = new ArrayList<T>();
		try {
			if (properties.length != values.length)
				return null;
			Enumeration<?> messages = browser.getEnumeration();
			while (messages.hasMoreElements()) {
				Object element = messages.nextElement();
				if (element instanceof ObjectMessage) {
					ObjectMessage msg = (ObjectMessage) element;
					boolean keep = true;
					for (int i = 0; i < properties.length; i++) {
						keep = values[i].equals(msg.getStringProperty(properties[i]));
					}
					if (keep)
						list.add((T) msg.getObject());
					if (maxSize != null && maxSize.equals(list.size()))
						return list;

				}

			}

		} catch (JMSException | ClassCastException e) {
			logger.error(e.getMessage());
		}
		return list;

	}



	public static <T extends Serializable> List<T> toList(QueueBrowser browser, String property, String value,
			Integer maxSize) {
		return toList(browser, new String[] { property }, new String[] { value }, maxSize);
	}

	/**
	 * Browses the content of a queue, categorizes the object and then returns a
	 * map contains the information about the object-count in the queue for each
	 * category. IN addition collection of values has to be passed which
	 * represents the different categories and a message-property which is used
	 * for categorization
	 * 
	 * @param browser
	 * @param values
	 * @param property
	 * @return
	 */
	public static <V extends Serializable> Map<V, Integer> getBrowserContentSizeByValues(QueueBrowser browser,
			Collection<V> values, String property) {
		Map<V, Integer> map = new HashMap<>();
		values.forEach(v -> map.put(v, test(browser, property, v.toString())));
		return map;

	}

	private JMSUtil() {
	};
}
