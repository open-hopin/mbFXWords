/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * {@link OccurrenceOfWords#main}
 * @author MB
 */
public class OccurrenceOfWords {
    /**
     * String of file content read in by class OccurrenceOfWords. 
     */
    static String sInput; // = "";
    /**
     * Map with key of exact word and mapValue
     * according to definition.
     */
    static Map<String, mapValue> map;
    /**
     * Integer array with index according to different words to hold
     * the number of occurrences of a word.
     */
    //static Integer[] iNumber = new Integer[10000];//[number of word occurrences]
    /**
     * String array of German Umlaute and their international representations.
     */
    private static String[][] UMLAUT_REPLACEMENTS = { { new String("Ä"), "Ae" }, { new String("Ü"), "Ue" }, { new String("Ö"), "Oe" }, { new String("ä"), "ae" }, { new String("ü"), "ue" }, { new String("ö"), "oe" }, { new String("ß"), "ss" } };
    /**
     * Replace German Umlaute by their international representations.
     * @param orig original String
     * @return the string with replaced letters
     */
    public static String replaceUmlaute(String orig) {
        String result = orig;

        for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
            result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
            }

        return result;
    }
    
    /**
     * Determines the sequential arrangement number
     * of every word and the number of occurences in the text
     * of every word.
     * @param file disk txt-file to be analyzed
     * @throws FileNotFoundException to caller
     * @throws IOException to caller
     */
    public static void main(String[] file) throws FileNotFoundException, IOException {
        sInput = "";
        if (file.length == 0) {
            System.out.println("Usage: java WordCounter targetfile");
            System.exit(0);
        }
        
        /*
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(new FileReader(file[0]));
        */
         
        Reader reader = new InputStreamReader(new FileInputStream(file[0]),"UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String inputLine = null;
        String outputLine = null;
        //String finalLine = null;
        
        map = new TreeMap<String, mapValue>();
        
        try {
            int wordTotalCounter = 0;
            while ((inputLine = bufferedReader.readLine()) != null) {
                if (!"".equals(sInput)) sInput += "\n"; sInput += inputLine;
                outputLine = inputLine.toLowerCase();
                //finalLine = replaceUmlaute(outputLine);
                String[] words = outputLine.split("[ \"\n\t\r.,;:!?(){}]");
                for (int wordCounter = 0; wordCounter < words.length; wordCounter++) {
                    mapValue value = new mapValue();
                    String key = words[wordCounter];
                    if (key.length() > 0) {
                        wordTotalCounter++;
                        value.sequence = wordTotalCounter;
                        value.occurrences = 0;
                        if (map.get(key) == null) {
                            value.occurrences = 1;
                            map.put(key, value);
                        }
                        else {
                            value.occurrences = map.get(key).occurrences;
                            value.sequence = map.get(key).sequence;
                            value.occurrences++;
                            map.put(key, value);
                        }
                    }
                }
                //wordTotalCounter = wordTotalCounter+words.length;            
            }
            //map = MapUtil.sortByValue(map,false);
            Set<Map.Entry<String, mapValue>> entrySet = map.entrySet();
            for (Map.Entry<String, mapValue> entry : entrySet) {
                //A text file saved in UTF-8 is displayed
                //correctly with German Umlaute in NB-console:
                //System.out.printf(entry.getValue() + "\t" + entry.getKey() + "\n");
                System.out.println(entry.getValue().sequence + "\t" +
                        entry.getValue().occurrences + "\t" + entry.getKey());
            }
        }
        catch (IOException error) {
            System.out.println("Invalid File");  
        }
        finally {
            bufferedReader.close();


        }
    }
}
