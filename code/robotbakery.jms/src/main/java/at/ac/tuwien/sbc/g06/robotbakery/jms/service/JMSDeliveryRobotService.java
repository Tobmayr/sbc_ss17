package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import javax.jms.*;

/**
 * Created by Matthias Höllthaler on 06.06.2017.
 */
public class JMSDeliveryRobotService extends AbstractJMSService implements IDeliveryRobotService {

    private static Logger logger = LoggerFactory.getLogger(JMSServiceRobotService.class);
    private Queue terminalQueue;
    private Queue orderQueue;
    private MessageConsumer deliveryOrderConsumer;
    private MessageProducer destination;
    private Connection connection;

    public JMSDeliveryRobotService() {
        super(false, Session.AUTO_ACKNOWLEDGE,JMSConstants.SERVER_ADDRESS);

        try {
            orderQueue = session.createQueue(JMSConstants.Queue.ORDER);
            terminalQueue = session.createQueue(JMSConstants.Queue.TERMINAL);

            deliveryOrderConsumer = session.createConsumer(terminalQueue,
                    String.format("%s='%s'", JMSConstants.Property.DELIVERY, Order.OrderState.WAITING));

        } catch (JMSException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public PackedOrder getPackedDeliveryOrder() {
        return receive(deliveryOrderConsumer);
    }

    @Override
    public boolean checkDestination(PackedOrder order) {

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(order.getDeliveryAddress());
            connection = connectionFactory.createConnection();
            connection.start();

            Queue queue = session.createQueue(JMSConstants.Queue.DELIVERY);
            destination = session.createProducer(queue);
            destination.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            return true;
        } catch (JMSException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deliverOrder(PackedOrder order) {
        boolean success = send(destination, order);
        try {
            connection.close();
        } catch (JMSException e) {
            logger.error(e.getMessage());
            return success;
        }

        return success;
    }

    @Override
    public boolean updateOrder(Order order) {
        return notify(order, false, orderQueue);
    }


	@Override
	public Map<String, Boolean> getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}
}
