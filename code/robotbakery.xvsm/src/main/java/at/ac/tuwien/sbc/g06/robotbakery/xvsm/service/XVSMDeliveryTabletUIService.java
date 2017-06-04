package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

/**
 * Created by Matthias Höllthaler on 01.06.2017.
 */
public class XVSMDeliveryTabletUIService extends XVSMTabletUIService implements IDeliveryTabletUIService {

    private ContainerReference counterContainer;
    private ContainerReference terminalContainer;

    public XVSMDeliveryTabletUIService() {
        super(new Capi(DefaultMzsCore.newInstance()));
        counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
        terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.DELIVERY_CONTAINER_NAME);
    }
    
   

}
