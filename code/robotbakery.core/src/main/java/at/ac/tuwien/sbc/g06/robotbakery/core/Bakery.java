package at.ac.tuwien.sbc.g06.robotbakery.core;

import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryService;

public abstract class Bakery {
	
	protected Set<IBakeryChangeListener> registeredChangeListeners;
	protected IBakeryService service;
	
	public Bakery(IBakeryService service) {
		registeredChangeListeners= new HashSet<>();
		this.service=service;
	}		
	
	public void initialize(){
		WaterPipe waterPipe= new WaterPipe();
		service.initializeStorageWaterPipe(waterPipe);
	}
	
	public boolean registerChangeListener(IBakeryChangeListener listener) {
		return registeredChangeListeners.add(listener);
	}

	public boolean removeChangeListener(IBakeryChangeListener listener) {
		return registeredChangeListeners.remove(listener);
	}
}
