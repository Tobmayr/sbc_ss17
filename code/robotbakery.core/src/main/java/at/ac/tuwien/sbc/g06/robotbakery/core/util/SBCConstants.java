package at.ac.tuwien.sbc.g06.robotbakery.core.util;

import java.util.Arrays;
import java.util.List;

public final class SBCConstants {

	public static final String PRODUCT1_NAME = "Kaisersemmel";
	public static final String PRODUCT2_NAME = "Bauernbrot";
	public static final String PRODUCT3_NAME = "Marmorkuchen";
	public static final String PRODUCT4_NAME = "Fladenbrot";
	public static final String PRODUCT5_NAME = "Croissant";

	public static final List<String> PRODUCTS_NAMES = Arrays.asList(PRODUCT1_NAME, PRODUCT2_NAME, PRODUCT3_NAME,
			PRODUCT4_NAME, PRODUCT5_NAME);
	public static final Integer FLOUR_PACK_SIZE = 500;
	public static final Integer COUNTER_MAX_CAPACITY = 10;
	public static final Integer BAKE_SIZE = 5;

	public static final Long BAKE_WAIT = 10000L;
	public static final long DELIVER_DURATION =5000L;
	public static final Integer PREPACKAGE_SIZE = 3;
	public static final Integer PREPACKAGE_MAX_AMOUNT = 5;

	public static final String COORDINATION_ROOM_STORAGE = "Storage";
	public static final String COORDINATION_ROOM_COUNTER = "Counter";
	public static final String COORDINATION_ROOM_TERMINAL = "Terminal";
	public static final String COORDINATION_ROOM_BAKEROOM = "Bakeroom";


	public static final class Keys {
		public static final String FLOUR = "flour";
		public static final String EGGS = "eggs";
		public static final String BAKEMIX_SWEET = "bakemix.sweet";
		public static final String BAKEMIX_SPICY = "bakemix.spicy";

		public static final String STORAGE_FINAL_PRODUCT1 = "storage.final.product1";
		public static final String STORAGE_FINAL_PRODUCT2 = "storage.final.product2";
		public static final String STORAGE_FINAL_PRODUCT3 = "storage.final.product3";
		public static final String STORAGE_FINAL_PRODUCT4 = "storage.final.product4";
		public static final String STORAGE_FINAL_PRODUCT5 = "storage.final.product5";

		public static final String STORAGE_BASE_PRODUCT1 = "storage.base.product1";
		public static final String STORAGE_BASE_PRODUCT2 = "storage.base.product2";
		public static final String STORAGE_BASE_PRODUCT3 = "storage.base.product3";
		public static final String STORAGE_BASE_PRODUCT4 = "storage.base.product4";
		public static final String STORAGE_BASE_PRODUCT5 = "storage.base.product5";

		public static final String COUNTER_PRODUCT1 = "counter.product1";
		public static final String COUNTER_PRODUCT2 = "counter.product2";
		public static final String COUNTER_PRODUCT3 = "counter.product3";
		public static final String COUNTER_PRODUCT4 = "counter.product4";
		public static final String COUNTER_PRODUCT5 = "counter.product5";

		public static final String BAKEROOM_PRODUCT1 = "bakeroom.product1";
		public static final String BAKEROOM_PRODUCT2 = "bakeroom.product2";
		public static final String BAKEROOM_PRODUCT3 = "bakeroom.product3";
		public static final String BAKEROOM_PRODUCT4 = "bakeroom.product4";
		public static final String BAKEROOM_PRODUCT5 = "bakeroom.product5";

		public static final List<String> ALL_STORAGE_BASE = Arrays.asList(STORAGE_BASE_PRODUCT1, STORAGE_BASE_PRODUCT2,
				STORAGE_BASE_PRODUCT3, STORAGE_BASE_PRODUCT4, STORAGE_BASE_PRODUCT5);

		public static final List<String> ALL_INGREDIENTS = Arrays.asList(FLOUR, EGGS, BAKEMIX_SWEET, BAKEMIX_SPICY);
		public static final List<String> ALL_STORAGE_FINAL = Arrays.asList(STORAGE_FINAL_PRODUCT1,
				STORAGE_FINAL_PRODUCT2, STORAGE_FINAL_PRODUCT3, STORAGE_FINAL_PRODUCT4, STORAGE_FINAL_PRODUCT5);
		public static final List<String> ALL_COUNTER = Arrays.asList(COUNTER_PRODUCT1, COUNTER_PRODUCT2,
				COUNTER_PRODUCT3, COUNTER_PRODUCT4, COUNTER_PRODUCT5);
		public static final List<String> ALL_BAKEROOM = Arrays.asList(BAKEROOM_PRODUCT1, BAKEROOM_PRODUCT2,
				BAKEROOM_PRODUCT3, BAKEROOM_PRODUCT4, BAKEROOM_PRODUCT5);

	}

	private SBCConstants() {
	}
}
