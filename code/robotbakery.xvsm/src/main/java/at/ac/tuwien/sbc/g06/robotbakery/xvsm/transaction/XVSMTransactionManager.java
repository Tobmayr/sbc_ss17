package at.ac.tuwien.sbc.g06.robotbakery.xvsm.transaction;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMTransactionManager implements ITransactionManager {
	private static Logger logger = LoggerFactory.getLogger(XVSMTransactionManager.class);

	private Capi capi;

	public XVSMTransactionManager() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
	}

	@Override
	public ITransaction createTransaction() {
		try {
			TransactionReference ref = capi.createTransaction(XVSMConstants.MAX_TRANSACTION_TIMEOUT,
					XVSMConstants.BAKERY_SPACE_URI);
			return new XVSMTransaction(ref);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean commitTransaction(ITransaction transaction) {
		TransactionReference ref = XVSMUtil.unwrap(transaction);
		if (ref == null)
			return false;
		try {
			capi.commitTransaction(ref);
			return true;
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			rollback(transaction);
			return false;

		}
	}

	@Override
	public boolean rollback(ITransaction transaction) {
		TransactionReference ref = XVSMUtil.unwrap(transaction);
		if (ref == null)
			return false;
		try {
			capi.rollbackTransaction(ref);
			return true;
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return false;
		}

	}

}
