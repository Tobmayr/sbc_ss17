package at.ac.tuwien.sbc.g06.robotbakery.core.model;
/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class Product extends Entity {
	public static final int DOUGH_IN_STORAGE = 1;
	public static final int DOUGH_IN_BAKEROOM = 2;
	public static final int PRODUCT_IN_STORAGE = 3;
	public static final int PRODUCT_IN_COUNTER = 4;
	public static final int PRODUCT_IN_TERMINAL = 5;
	public static final int PRODUCT_SOLD = 6;

	public Product() {
		super();
	}

	private String productName;
	private Recipe recipe;
	private int state;
	//TODO : add Contributions

	public Product(String productName) {
		super();
		this.productName = productName;
		recipe = RecipeRegistry.INSTANCE.getRecipeForProduct(this);

	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getName() {
		return productName;
	}

}
