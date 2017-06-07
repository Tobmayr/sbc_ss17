package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.RecipeRegistry;

/**
 * product class
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
@SuppressWarnings("serial")
public class Product implements Serializable {

	public static final String DOUGH_BASE = "Creation base dough";
	public static final String DOUGH_FINAL = "Creation final dough";
	public static final String BAKE = "Baking";
	public static final String TRANSFER_TO_COUNTER = "Counter transfer";
	public static final String PACK_UP = "Packing";
	public static final String DELIVERD = "Delivery";

	public enum BakeState {
		DOUGH, FINALPRODUCT;
	}

	private final UUID id;
	private final String productName;
	final Recipe recipe;
	final double price;
	final Integer baketime;
	private BakeState type = BakeState.DOUGH;
	private List<Contribution> contributions = new ArrayList<>();
	private Timestamp timestamp;

	public Product(String productName) {
		super();
		id = UUID.randomUUID();
		this.productName = productName;
		recipe = RecipeRegistry.getInstance().getRecipeForProduct(this);
		this.price = recipe.getPricePerUnit();
		this.baketime = recipe.getBakeTime();

	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getBaketime() {
		return baketime;
	}

	public Product(Recipe recipe) {
		super();
		id = UUID.randomUUID();
		this.productName = recipe.getProductName();
		this.recipe = recipe;
		this.price = recipe.getPricePerUnit();
		this.baketime = recipe.getBakeTime();

	}

	/**
	 * history: which robot worked on the product
	 * 
	 * @param contributerId
	 *            id of the robot
	 * @param type
	 *            which type of contribution
	 * @param contributor
	 *            robot class
	 */
	public void addContribution(UUID contributerId, String type, Class<? extends Robot> contributor) {
		contributions.add(new Contribution(contributerId, type, contributor));
	}

	public List<Contribution> getContributions() {
		return contributions;
	}

	public BakeState getType() {
		return type;
	}

	public void setType(BakeState type) {
		this.type = type;
	}

	public String getProductName() {
		return productName;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public UUID getId() {
		return id;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, productName, recipe, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Product) {
			Product that = (Product) obj;
			return this.getId().toString().equals(that.getId().toString())
					&& this.getProductName().equals(that.getProductName())
					&& this.getType().toString().equals(that.getType().toString());
		}
		return super.equals(obj);
	}

	/**
	 * contribution subclass
	 */
	public class Contribution implements Serializable {

		final UUID contributerId;
		final String type;
		final String contributor;

		private Contribution(UUID contributerId, String type, Class<? extends Robot> contributor) {
			super();
			this.contributerId = contributerId;
			this.type = type;
			this.contributor = contributor.getSimpleName();
		}

		public UUID getContributerId() {
			return contributerId;
		}

		public String getType() {
			return type;
		}

		public String getContributor() {
			return contributor;
		}

	}

}
