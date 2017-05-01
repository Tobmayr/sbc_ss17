package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class XVSMBakeRobotService implements IBakeRobotService {

    private static Logger logger = LoggerFactory.getLogger(XVSMKneadRobotService.class);
    private final ContainerReference storageContainer;
    private final ContainerReference bakeroomContainer;
    private Capi capi;

    public XVSMBakeRobotService() {
        this.capi = new Capi(DefaultMzsCore.newInstance());
        bakeroomContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.BAKEROOM_CONTAINER_NAME);
        storageContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
    }

    @Override
    public List<Product> getUnbakedProducts(ITransaction tx) {
        Query query = new Query().sortup(ComparableProperty.forName("*", "timestamp"));
        try {
            return capi.read(bakeroomContainer,
                    QueryCoordinator.newSelector(query, SBCConstants.BAKE_SIZE),
                    SBCConstants.BAKE_WAIT, XVSMUtil.unwrap(tx));
        } catch (MzsCoreException e) {

            try {
                return capi.read(bakeroomContainer,
                        QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
                        MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
            } catch (MzsCoreException ex) {
                logger.error(ex.getMessage());
                return null;
            }
        }
    }

    @Override
    public boolean putBakedProductsInStorage(Product nextProduct, ITransaction tx) {
        try {
            Entry entry = new Entry(nextProduct);
            capi.write(entry, storageContainer, MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
            return true;
        } catch (MzsCoreException ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }
}
