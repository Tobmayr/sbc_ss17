package at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Matthias Höllthaler on 03.06.2017.
 */
public class XVSMDeliveryTabletUIChangeNotifier extends TabletUIChangeNotifer implements NotificationListener {

    @Override
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> list) {

    }

}
