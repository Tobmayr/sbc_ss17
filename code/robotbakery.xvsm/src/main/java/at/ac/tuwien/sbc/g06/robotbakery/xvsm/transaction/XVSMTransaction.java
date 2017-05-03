package at.ac.tuwien.sbc.g06.robotbakery.xvsm.transaction;

import org.mozartspaces.core.TransactionReference;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

/**
 * Wrapper implementation so that the Transaction-class used in XVSM is
 * compatible with the ITransaction-interface
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public class XVSMTransaction implements ITransaction {

	private final TransactionReference transactionReference;

	public XVSMTransaction(TransactionReference transactionReference) {
		super();
		this.transactionReference = transactionReference;
	}

	public TransactionReference unwrap() {
		// TODO Auto-generated method stub
		return transactionReference;
	}

}
