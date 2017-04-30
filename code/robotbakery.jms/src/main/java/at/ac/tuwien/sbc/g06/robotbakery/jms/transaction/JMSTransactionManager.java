package at.ac.tuwien.sbc.g06.robotbakery.jms.transaction;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;

public class JMSTransactionManager implements ITransactionManager {

	@Override
	public ITransaction createTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean commitTransaction(ITransaction transaction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rollback(ITransaction transaction) {
		// TODO Auto-generated method stub
		return false;
	}

}
