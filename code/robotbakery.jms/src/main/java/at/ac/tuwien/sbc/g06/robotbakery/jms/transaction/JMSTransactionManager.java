package at.ac.tuwien.sbc.g06.robotbakery.jms.transaction;

import javax.jms.JMSException;
import javax.jms.Session;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;

public class JMSTransactionManager implements ITransactionManager {

	private Session session;

	public JMSTransactionManager(Session session) {
		this.session = session;
	}

	@Override
	public ITransaction createTransaction() {
		return new JMSTransaction();
	}

	@Override
	public boolean commitTransaction(ITransaction transaction) {
		if (transaction == null)
			return true;
		try {
			session.commit();
			return true;
		} catch (JMSException e) {
			throw new RuntimeException("Error when trying to commit a transaction");
		}

	}

	@Override
	public boolean rollback(ITransaction transaction) {
		if (transaction == null)
			return true;
		try {
			session.rollback();
			return true;
		} catch (JMSException e) {
			throw new RuntimeException("Error when trying to commit a transaction");
		}
	}

}
