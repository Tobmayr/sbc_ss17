package at.ac.tuwien.sbc.g06.robotbakery.xvsm;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Matthias Höllthaler on 22.04.2017.
 */
public class Space {

    private static MzsCore core = DefaultMzsCore.newInstance();
    private static Capi capi = new Capi(core);
    private ArrayList<ContainerReference> containers = new ArrayList<>();

    private void createContainer() {
        try {
            ContainerReference container = null;
            container = capi.createContainer("orders",null, MzsConstants.Container.UNBOUNDED, null, new FifoCoordinator());
            containers.add(container);
        } catch (org.mozartspaces.core.MzsCoreException e) {
            e.printStackTrace();
        }
    }

    private void destroyContainer() throws MzsCoreException {
        for (ContainerReference container: containers) {
            capi.destroyContainer(container, null);
        }
    }

    private void shutdown() {
        core.shutdown(true);
    }

    public void write(String containerName, Object object) throws MzsCoreException {
        ContainerReference container = capi.lookupContainer(containerName);
        capi.write(new Entry((Serializable) object), container);
    }
}
