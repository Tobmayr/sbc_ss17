package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.RecipeRegistry;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
@SuppressWarnings("serial")
public class Product implements Serializable {

	public enum ProductType {
		DOUGH, FINALPRODUCT;
	}

	public enum ContributionType {
		DOUGH_BASE, DOUGH_FINAL, BAKE, TRANSFER_TO_COUNTER, PACK_UP;
	}

	private final UUID id;
	private final String productName;
	final Recipe recipe;
	private ProductType type=ProductType.DOUGH;
	private List<Contribution> contributions = new ArrayList<>();
	private Timestamp timestamp;

	public Product(String productName) {
		super();
		id = UUID.randomUUID();
		this.productName = productName;
		recipe = RecipeRegistry.getInstance().getRecipeForProduct(this);

	}
	
	

	public Timestamp getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}



	public Product(Recipe recipe) {
		super();
		id = UUID.randomUUID();
		this.productName = recipe.getProductName();
		this.recipe = recipe;
	}

	public void addContribution(UUID contributerId, ContributionType type, Class<? extends Robot> contributor) {
		contributions.add(new Contribution(contributerId, type, contributor));
	}

	public List<Contribution> getContributions() {
		return contributions;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
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


	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Product) {
			Product that = (Product) obj;
			return this.getId().equals(that.getId());
		}
		return super.equals(obj);
	}

	public class Contribution implements Serializable {

		final UUID contributerId;
		final ContributionType type;
		final String contributor;

		private Contribution(UUID contributerId, ContributionType type, Class<? extends Robot> contributor) {
			super();
			this.contributerId = contributerId;
			this.type = type;
			this.contributor = contributor.getSimpleName();
		}

		public UUID getContributerId() {
			return contributerId;
		}

		public ContributionType getType() {
			return type;
		}

		public String getContributor() {
			return contributor;
		}

	}

}
