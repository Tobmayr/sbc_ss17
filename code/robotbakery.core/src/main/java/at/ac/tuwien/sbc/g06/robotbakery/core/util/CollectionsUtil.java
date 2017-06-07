package at.ac.tuwien.sbc.g06.robotbakery.core.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public class CollectionsUtil {

	/**
	 * Sorts a generic map by its values either ascending order descending and
	 * returns them in a new LinkedHashMap
	 * 
	 * @param map
	 *            unsorted map
	 * @param ascending
	 *            sorting type
	 * @return sorted LinkedHashMap
	 */
	public static <K, V extends Comparable<V>> LinkedHashMap<K, V> sortMapByValues(Map<K, V> map, boolean ascending) {
		if (map == null)
			return null;
		Comparator<Map.Entry<K, V>> comparator = ascending ? Entry.comparingByValue()
				: (i, j) -> j.getValue().compareTo(i.getValue());
		return map.entrySet().stream().sorted(comparator)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

	}

	/**
	 * sorts a generic map by its key either ascending or descending
	 * @param map
	 * @param ascending
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K extends Comparable<K>, V> LinkedHashMap<K, V> sortMayByKeys(Map<K, V> map, boolean ascending) {
		if (map == null)
			return null;
		Comparator<Map.Entry<K, V>> comparator = ascending ? Entry.comparingByKey()
				: (i, j) -> j.getKey().compareTo(i.getKey());
		return map.entrySet().stream().sorted(comparator)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

	}

	private CollectionsUtil() {
	}

}
