package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.RecipeRegistry;
import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Actor;

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

	private final UUID id;
	private String productName;
	private Recipe recipe;
	private ProductState state = ProductState.DOUGH_IN_STORAGE;
	private List<Contribution> contributions = new ArrayList<>();

	public Product(String productName) {
		super();
		id = UUID.randomUUID();
		this.productName = productName;
		recipe = RecipeRegistry.getInstance().getRecipeForProduct(this);

	}

	public void addContribution(UUID contributerId, ContributionType type, Class<? extends Actor> contributor) {
		contributions.add(new Contribution(contributerId, type, contributor));
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

	public String getProductName() {
		return productName;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Product [productName=" + productName + ", recipe=" + recipe + ", state=" + state + "]";
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

		private Contribution(UUID contributerId, ContributionType type, Class<? extends Actor> contributor) {
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
