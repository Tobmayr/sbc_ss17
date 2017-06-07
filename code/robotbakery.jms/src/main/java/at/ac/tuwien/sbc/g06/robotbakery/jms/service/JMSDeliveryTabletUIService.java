package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthias Höllthaler on 07.06.2017.
 */
public class JMSDeliveryTabletUIService extends JMSTabletUIService implements IDeliveryTabletUIService {


    private static Logger logger = LoggerFactory.getLogger(AbstractJMSService.class);
    private Queue terminalQueue;
    private MessageConsumer terminalConsumer;

    public JMSDeliveryTabletUIService() {
        super();
        try {
            terminalQueue = session.createQueue(JMSConstants.Queue.DELIVERY);
        } catch (JMSException e) {
            logger.error(e.getMessage());
        }

    }
}
