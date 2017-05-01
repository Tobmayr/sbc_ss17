package at.ac.tuwien.sbc.g06.robotbakery.jms.util;

public class JMSConstants {

	public static final String SERVER_ADDRESS = "tcp://localhost:5557";

	public static final String ORDER_QUEUE = "queue/order";
	public static final String ORDER_TOPIC = "topic/order";
	public static final String BAKEROOM_QUEUE = "queue/bakeroom";
	public static final String TOPIC_BAKERROM = "topic/bakeroom";
	public static final String TOPIC_TERMINAL = "topic/terminal";
	public static final String TOPIC_STORAGE = "topic/storage";

	private JMSConstants() {
	};
}
