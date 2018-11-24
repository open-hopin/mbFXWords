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
 * sequence of occuring words,
 * word stem and key of related map.
 * WordCountMapValue is used by {@link OccurrenceOfWords#map}.
 * @author MB
 */
public class WordCountMapValue {
   /**
    * Number of occurrences of words.
    */ 
   Integer occurrences;
   /**
    * Sequence of occuring words.
    */
   Integer sequence;
   /**
    * Frequency of occurrence or word stem.
    */
   //Integer occurencesStem;
   /**
    * String representation of word stem.
    */
   String sStem;
   /**
    * Key of related Map. For package mbfxwords
    * the key is the word to count.
    */
   String key;
}
