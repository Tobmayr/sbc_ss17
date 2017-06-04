package at.ac.tuwien.sbc.g06.robotbakery.xvsm.util;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.transaction.XVSMTransaction;

public class XVSMUtil {
	private static Logger logger = LoggerFactory.getLogger(XVSMUtil.class);

	private static Map<String, String> CONTAINER_NAME_TO_ID = new HashMap<>();

	private XVSMUtil() {
	};

	public static ContainerReference getOrCreateContainer(Capi capi, String containerName) {
		return getOrCreateContainer(capi, containerName, XVSMConstants.BASE_SPACE_URI);
	}

	public static ContainerReference getOrCreateContainer(Capi capi, String containerName, URI spaceURI) {
		logger.debug("Lookup container:" + containerName);
		ContainerReference cref = null;
		try {
			cref = capi.lookupContainer(containerName, spaceURI, RequestTimeout.DEFAULT, null);
			logger.debug("Existing container found for: " + containerName);
		} catch (MzsCoreException e) {
			try {
				cref = capi.createContainer(containerName, XVSMConstants.BASE_SPACE_URI,
						MzsConstants.Container.UNBOUNDED, getObligatoryCoordsForContainer(containerName), null, null);
				logger.debug("New container has been created for: " + containerName);
			} catch (MzsCoreException e1) {
				logger.error(e1.getMessage());
			}
		}
		if (cref != null) {
			setId(containerName, cref.getId());
		}
		return cref;

	}

	private static void setId(String containerName, String id) {
		if (!CONTAINER_NAME_TO_ID.containsKey(id)) {
			CONTAINER_NAME_TO_ID.put(id, containerName);
		}
	}

	private static List<Coordinator> getObligatoryCoordsForContainer(String containerName) {
		switch (containerName) {
		case XVSMConstants.COUNTER_CONTAINER_NAME:
			return Arrays.asList(new QueryCoordinator(), new TypeCoordinator());
		case XVSMConstants.STORAGE_CONTAINER_NAME:
			return Arrays.asList(new QueryCoordinator(), new TypeCoordinator(), new FifoCoordinator());
		case XVSMConstants.BAKEROOM_CONTAINER_NAME:
			return Arrays.asList(new FifoCoordinator(), new QueryCoordinator());
		case XVSMConstants.TERMINAL_CONTAINER_NAME:
			return Arrays.asList(new QueryCoordinator(), new TypeCoordinator());

		default:
			return null;
		}
	}

	public static String getName(ContainerReference cref) {
		return CONTAINER_NAME_TO_ID.get(cref.getId());
	}

	public static String getCoordinationRoom(ContainerReference cref) {
		switch (getName(cref)) {
		case XVSMConstants.STORAGE_CONTAINER_NAME:
			return SBCConstants.COORDINATION_ROOM_STORAGE;
		case XVSMConstants.COUNTER_CONTAINER_NAME:
			return SBCConstants.COORDINATION_ROOM_COUNTER;
		case XVSMConstants.TERMINAL_CONTAINER_NAME:
			return SBCConstants.COORDINATION_ROOM_TERMINAL;
		case XVSMConstants.BAKEROOM_CONTAINER_NAME:
			return SBCConstants.COORDINATION_ROOM_BAKEROOM;
		default:
			return null;
		}
	}

	public static TransactionReference unwrap(ITransaction tx) {
		if (tx instanceof XVSMTransaction)
			return ((XVSMTransaction) tx).unwrap();
		return null;
	}

	public static Serializable unwrap(Serializable ser) {
		if (ser instanceof Entry)
			return ((Entry) ser).getValue();
		return ser;
	}

}
