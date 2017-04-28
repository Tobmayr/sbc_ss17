package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;

public interface IUIService {

	void addIngredientsToStorage(List<Ingredient> ingredients);
}
