package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.Ingredient;

public interface IUIService {

	void addIngredientsToStorage(Ingredient ingredient, int amount);
}
