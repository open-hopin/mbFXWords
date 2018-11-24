/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

/**
 * Constitutes the value object of a collection 
 * with the members
 * number of occurrences of words,
 * number of effective occurrences of words,
 * original word
 * and key of related map.
 * WordCountMapValue is used by
 * {@link OccurrenceOfWords#mapStems},
 * key is the stem.
 * @author MB
 */
public class StemCountMapValue {
    /**
    * Number of occurrences of words.
    */ 
   Integer occurrences;
   /**
    * Number of effective occurrences of words.
    */ 
   Integer effectiveOccurrences;
   /**
    * Key of related Map. For package mbfxwords
    * the key is the stem to count.
    */
   String key;
   /**
    * Original word related to the key.
    */
   String word;
}
