package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

/**
 * Created by Matthias Höllthaler on 01.06.2017.
 */
public class XVSMDeliveryTabletUIService extends XVSMTabletUIService implements IDeliveryTabletUIService {

    private ContainerReference counterContainer;
    private ContainerReference deliveryContainer;

    public XVSMDeliveryTabletUIService(Capi capi) {
        super(capi);
        counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
        deliveryContainer= getContainer(XVSMConstants.DELIVERY_CONTAINER_NAME,capi.getCore().getConfig().getSpaceUri());
    }
    
   

}
