package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import org.mozartspaces.capi3.Matchmakers;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;

/**
 * Created by Matthias Höllthaler on 01.06.2017.
 */
public class XVSMDeliveryTabletUIService extends XVSMTabletUIService implements IDeliveryTabletUIService {

    private ContainerReference counterContainer;
    private ContainerReference terminalContainer;

    public XVSMDeliveryTabletUIService() {
        counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
    }

}
