package at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier;

import java.io.Serializable;
import java.util.List;

import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.Operation;

/**
 * Created by Matthias Höllthaler on 03.06.2017.
 */
public class XVSMDeliveryTabletUIChangeNotifier extends XVSMTabletUIChangeNotifier  {

    @Override
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> list) {

    }

}
