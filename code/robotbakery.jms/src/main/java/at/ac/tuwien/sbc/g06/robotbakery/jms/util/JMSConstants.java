package at.ac.tuwien.sbc.g06.robotbakery.jms.util;

public class JMSConstants {

	public static final class Property {
		public static final String TYPE = "type";
		public static final String CLASS = "class";
		public static final String CUSTOMER_ID = "customerID";
		public static final String ORDER_ID = "orderID";
		public static final String REMOVED = "removed";
		public static final String STATE = "orderState";
		public static final String ORIGINAL_DESTINATION = "originalDestination";
		public static final String ID = "uuid";
		public static final String DELIVERY = "WAITING";

		private Property() {
		};

	}

	public static final class Queue {
		public static final String ORDER = "queue/order";
		public static final String COUNTER = "queue/counter";
		public static final String TERMINAL = "queue/terminal";
		public static final String STORAGE = "queue/storage";
		public static final String BAKEROOM = "queue/bakeroom";
		public static final String DELIVERY = "queue/delivery";

		private Queue() {
		};
	}

	public static final class Topic {
		public static final String NOTIFICATION = "topic/notifier";

		private Topic() {
		};
	}

	public static final String DELIVERY_ADDRESS = "tcp://localhost:";
	public static final String SERVER_ADDRESS = "tcp://localhost:5557";
	public static final long MAX_TIMEOUT = 2000;

	private JMSConstants() {
	};
}
