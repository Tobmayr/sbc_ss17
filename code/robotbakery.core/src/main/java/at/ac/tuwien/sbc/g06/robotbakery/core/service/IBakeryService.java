package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface IBakeryService {

	void initializeStorageWaterPipe(WaterPipe waterPipe, ITransaction tx);
}
