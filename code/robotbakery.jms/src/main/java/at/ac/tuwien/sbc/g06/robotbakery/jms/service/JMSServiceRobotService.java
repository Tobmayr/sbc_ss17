package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;
import java.util.SortedMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSServiceRobotService extends AbstractJMSService implements IServiceRobotService {

	private Queue orderQueue;
	private MessageConsumer orderConsumer;

	public JMSServiceRobotService() {
		orderQueue = getOrCreateQueue(JMSConstants.ORDER_QUEUE, session, orderQueue);
		try {
			orderConsumer = session.createConsumer(orderQueue);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Order getNextOrder(ITransaction tx) {
		try {
			Message msg = orderConsumer.receive(1000);
			if (msg instanceof ObjectMessage) {
				System.out.println("Order received");
				System.out.println(((ObjectMessage) msg).getObject().toString());
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void addToCounter(List<Product> products, ITransaction tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateOrder(Order order, ITransaction tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public SortedMap<String, Integer> getCounterStock(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> checkCounter(Order order, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProductFromStorage(SortedMap<String, Integer> missingProducts, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

}
