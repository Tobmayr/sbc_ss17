package at.ac.tuwien.sbc.g06.robotbakery.core.util;

import java.util.Arrays;
import java.util.List;

public class SBCConstants {

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

	public static final Long BAKE_WAIT = 5L;

	private SBCConstants() {
	}
}
