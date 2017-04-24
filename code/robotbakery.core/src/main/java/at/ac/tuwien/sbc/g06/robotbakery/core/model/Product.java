package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.RecipeRegistry;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class Product implements Serializable {

	public enum ProductState {
		DOUGH_IN_STORAGE, DOUGH_IN_BAKEROOM, PRODUCT_IN_STORAGE, PRODUCT_IN_COUNTER, PRODUCT_IN_TERMINAL, PRODUCT_SOLD;
	}

	public enum ContributionType {
		DOUGH_BASE, DOUGH_FINAL, BAKE, TRANSFER_TO_COUNTER, PACK_UP;
	}

	private String productName;
	private Recipe recipe;
	private ProductState state;
	private List<Contribution> contributions = new ArrayList<>();

	public Product(String productName) {
		super();
		this.productName = productName;
		recipe = RecipeRegistry.getInstance().getRecipeForProduct(this);

	}

	public void addContribution(UUID contributerId, ContributionType type) {
		contributions.add(new Contribution(contributerId, type));
	}

	public List<Contribution> getContributions() {
		return contributions;
	}

	public ProductState getState() {
		return state;
	}

	public void setState(ProductState state) {
		this.state = state;
	}

	public String getName() {
		return productName;
	}

	@Override
	public String toString() {
		return "Product [productName=" + productName + ", recipe=" + recipe + ", state=" + state + "]";
	}

	class Contribution {

		final UUID contributerId;
		final ContributionType type;

		private Contribution(UUID contributerId, ContributionType type) {
			super();
			this.contributerId = contributerId;
			this.type = type;
		}

		public UUID getContributerId() {
			return contributerId;
		}

		public ContributionType getType() {
			return type;
		}

	}

}
