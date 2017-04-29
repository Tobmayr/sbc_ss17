package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface IBakeryUIService {

	void addIngredientsToStorage(List<Ingredient> ingredients);
}
