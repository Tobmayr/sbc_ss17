package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class AbstractXVSMService {
	private static Logger logger = LoggerFactory.getLogger(AbstractXVSMService.class);
	protected Capi capi;

	private Map<String, ContainerReference> containers = new HashMap<>();

	public AbstractXVSMService(Capi capi) {
		this.capi = capi;
	}

	public ContainerReference getContainer(String containerName) {
		ContainerReference cref = containers.get(containerName);
		if (cref == null)
			cref = XVSMUtil.getOrCreateContainer(capi, containerName);
		return cref;
	}

	/**
	 * Wrapper method which enables writing elements into a container reference
	 * with one method call.
	 * 
	 * @param modelObjects
	 * @param cref
	 * @param tx
	 * @return
	 */

	public <T extends Serializable> boolean write(List<T> modelObjects, ContainerReference cref, ITransaction tx) {
		try {
			List<Entry> entries = new ArrayList<>();
			modelObjects.forEach(mo -> entries.add(new Entry(mo)));
			capi.write(entries, cref, MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}

	/**
	 * Wrapper method which enables writing one element into a container
	 * reference with one method call.
	 * 
	 * @param modelObject
	 * @param cref
	 * @param tx
	 * @return
	 */
	public <T extends Serializable> boolean write(T modelObject, ContainerReference cref, ITransaction tx) {
		try {
			Entry entry = new Entry(modelObject);
			capi.write(entry, cref, MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> T takeFirst(ContainerReference cref, ITransaction tx, Selector... selectors) {
		try {
			return (T) capi
					.take(cref, Arrays.asList(selectors), MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx))
					.get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public <T extends Serializable> List<T> take(ContainerReference cref, ITransaction tx, Selector... selectors) {
		try {
			return capi.take(cref, Arrays.asList(selectors), MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public <T extends Serializable> List<T> read(ContainerReference cref, ITransaction tx, Selector... selectors) {
		try {
			return capi.read(cref, Arrays.asList(selectors), MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public <T extends Serializable> int test(ContainerReference cref, ITransaction tx, Selector... selectors) {
		try {
			return capi.test(cref, Arrays.asList(selectors), MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return 0;
		}
	}
}
