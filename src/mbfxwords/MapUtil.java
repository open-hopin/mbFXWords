/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

import java.util.*;

/**
 * Provides a collection of key value pairs and makes
 * possible to sort according to ascending or descending values.
 * @author MB
 */
public class MapUtil {
    /**
     * Sorts a collection of key value pairs according to
     * ascending or descending values.
     * @param ascending true if ascending, false if descending
     * @param map Map of key and value
     * @param <K> key type
     * @param <V> value type
     * @return sorted Map of key and value
     * @author MB
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> 
        sortByValue(Map<K, V> map, Boolean ascending ) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (ascending) {
                    return (o1.getValue()).compareTo( o2.getValue() );
                } else
                {
                    return (o2.getValue()).compareTo( o1.getValue() );
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
