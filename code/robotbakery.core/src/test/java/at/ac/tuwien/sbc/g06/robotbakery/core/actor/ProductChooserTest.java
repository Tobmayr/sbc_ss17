package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ProductState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

public class ProductChooserTest {

	private ProductChooser chooser;
	private IKneadRobotService kneadService = mock(IKneadRobotService.class);
	private Product baseDough1;
	private Product baseDough2;
	private TreeMap<IngredientType, Integer> ingredientStock;


	@Before
	public void setUp() {
		baseDough1 = new Product(SBCConstants.PRODUCT1_NAME);
		baseDough1.setState(ProductState.DOUGH_IN_STORAGE);
		baseDough2= new Product(SBCConstants.PRODUCT2_NAME);
		baseDough2.setState(ProductState.DOUGH_IN_STORAGE);
		ingredientStock = new TreeMap<IngredientType, Integer>();
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 2);
	}

	@Test
	public void test_getBaseDoughFromStorage_canFinish() {
		when(kneadService.getBaseDoughsFromStorage()).thenReturn(Arrays.asList(baseDough1));
		when(kneadService.getIngredientStock()).thenReturn(ingredientStock);
		chooser = new ProductChooser(kneadService);
		Product result=chooser.getFinishableBaseDough();
		assertEquals(baseDough1,result);
	
	}
	
	@Test
	public void test_getBaseDoughFromStorage_canNotFinish() {
		when(kneadService.getBaseDoughsFromStorage()).thenReturn(Arrays.asList(baseDough2));
		when(kneadService.getIngredientStock()).thenReturn(ingredientStock);
		chooser = new ProductChooser(kneadService);
		Product result=chooser.getFinishableBaseDough();
		assertEquals(null,result);
	
	}
}