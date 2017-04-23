package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.UUID;
/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public abstract class Entity implements Serializable {
	private UUID id;
	
	public Entity(){
		this.id=UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}
}
