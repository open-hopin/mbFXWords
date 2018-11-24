/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a collection with a key
 * evaluated against a regular expression
 * to get an ArrayList of resulting
 * value objects. An ArrayList contains its
 * objects in the order the elements
 * had been added, the last element added
 * is the first element in the list.
 * Not used so far.
 * @author MB
 * @param <T> type of object stored
 * under the key String
 */
public class RegexCollection<T> {
    public TreeMap<String, T> elements = new TreeMap<>();
    public ArrayList<T> getValue(String regexKey) {
        Pattern pat = Pattern.compile(regexKey); 
        ArrayList<T> returnValue = new ArrayList<>();
        Set<Map.Entry<String, T>> entrySet = elements.entrySet();
        for (Map.Entry<String, T> element : entrySet) {
            Matcher m = pat.matcher(element.getKey()); 
            while(m.find()) { 
                //System.out.println(element.getValue().toString());
                returnValue.add(element.getValue());
            }
        }
        //System.out.println(returnValue.size());
        
        //Overhauled:
        //return returnValue.toArray((T[]) new Object[elements.size()]);
        //return (T[])returnValue.toArray();
        //array = (T[])Array.newInstance(clazz, capacity);
        //Class<T/*[]*/> clazz = null;
        //T[] t;
        //t = (T[])Array.newInstance(clazz, elements.size());
        //t=clazz.cast(Array.newInstance(clazz.getComponentType(), elements.size()));
        
        /*
        @SuppressWarnings("unchecked")     //Would be much shorter and
        T[] t=(T[]) returnValue.toArray(); //can be the solution.
        */
        
        /*
        @SuppressWarnings("unchecked")
        T[] t = newArray(returnValue.size(), (T[]) returnValue.toArray());
        */
        
        //Collections.reverse(returnValue);
        return returnValue;
    }
    /**
     * Creates a new generic array out of Varargs
     * elements with size as requested.
     * Empty elements are null.
     * Not used so far.
     * Not tested so far.
     * @param <E> type of returned array
     * @param length size of returned array
     * @param array elements of array
     * @return generic array
     */
    @SafeVarargs
    static <E> E[] newArray(int length, E... array)
    {
        return Arrays.copyOf(array, length);
    }
    
}
