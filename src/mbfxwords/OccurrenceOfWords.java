/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * {@link OccurrenceOfWords#main}
 * @author MB
 */
public class OccurrenceOfWords {
    /**
     * Most common 20 words of main text loaded of length
     * greater than 4, determined by calls of
     * {@link #determineCommonWord()}.
     */
    public static String sCommonWords[] = new String [20];
    /**
     * Choices regarding Bing search grammar:
     * "SPO", "SO", "subject (S)", "predicate (P)", "object (O)".
     */
    public static String sGram;
    /**
     * Choices regarding Bing search preferences:
     * "CSE", "CS", "SE",
     * "most common (C)", "significant (S)", "extraordinary (E)".
     */
    public static String sPref;
    /**
     * Choices regarding Bing search filetype:
     * "txt", "pdf".
     */
    public static String sFiletype;
    /**
     * Index of {@link OpenNLP#sSPO}
     * for most significant clause according
     * to {@link #iSPOSearchSignificant()}.
     */
    public static int iSignificant;
    /**
     * Index of {@link OpenNLP#sSPO}
     * for most common clause according
     * to {@link #iSPOSearchMostCommon()}.
     */
    public static int iMostCommon;
    /**
     * Index of {@link OpenNLP#sSPO}
     * for first extraordinary clause according
     * to {@link #iSPOSearchExtraordinary()}.
     */
    public static int iExtraordinary;
    /**
     * String of file content read in by class OccurrenceOfWords. 
     */
    public static String sInput; // = "";
    /**
     * Map with key of exact word and WordCountMapValue
     * according to definition.
     */
    public static Map<String, WordCountMapValue> map;
    /**
     * Map with key of word stem and value
     * int of related occurences.
     */
    public static Map<String, StemCountMapValue> mapStems;
    /**
     * Integer array with index according to different words to hold
     * the number of occurrences of a word.
     */
    //static Integer[] iNumber = new Integer[10000];//[number of word occurrences]
    /**
     * Determines on element of {@link #sCommonWords},
     * so with subsequent calls complete Array is filled.
     * Fills first index that has an empty string.
     */
    public static void determineCommonWord() {
        String sReturn="";
        int iMaxSum=0;
        Set<Map.Entry<String, WordCountMapValue>> entrySet = map.entrySet();
        for (Map.Entry<String, WordCountMapValue> entry : entrySet) {
            WordCountMapValue mapValue = entry.getValue();
            if (mapValue.occurrences > iMaxSum) {
                Boolean bTemp=true;
                for (String sTemp: sCommonWords) {
                    if (mapValue.key.length()<=4) {bTemp=false;break;}
                    if (mapValue.key == sTemp) {bTemp=false;break;}
                }
                if (bTemp) {
                    sReturn=mapValue.key;
                    iMaxSum=mapValue.occurrences;
                }        
            }
        }
        int b=0;
        int c=-1;
        for (String s: sCommonWords) {
            if ("".equals(s)) {c=b; break;}
            b++;
        }
        if (c!=-1) sCommonWords[c]=sReturn;
    }
    /**
     * String array of German Umlaute
     * and their international representations.
     */
    private static final String[][] UMLAUT_REPLACEMENTS = { { new String("Ä"), "Ae" }, { new String("Ü"), "Ue" }, { new String("Ö"), "Oe" }, { new String("ä"), "ae" }, { new String("ü"), "ue" }, { new String("ö"), "oe" }, { new String("ß"), "ss" } };
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
     * Get formal representation of a stem
     * of a given word.
     * @param s word to return the according stem
     * @return stem representation
     */
    public static String sGetStem(String s) {
        //System.out.println(s);
        String sStem;
        if (s.length()>4) {
            sStem = s.substring(0, s.length()-2)+"..";
        }
        else {
            sStem = s;
        }        
        return sStem;
    }
    /**
     * One element in {@link OpenNLP#sSPO} 
     * significance is determined for. Clauses
     * with words with significantly used
     * effective stems are of 'true' result.
     * Maximum differences
     * of frequency of use between words of same
     * clause is searched for.
     * @param index index in {@link OpenNLP#sSPO}
     * significance is determined for
     * @param significance value of significance
     * sufficiant to return true. Values of 0 (no significance)
     * to greater 0.
     * @return true if significant, false otherwise
     */
    public static Boolean bSPOSignificant(int index, int significance) {
        //int iReturn=-1;
        //int a=-1;
        int iRunningSum=0;
        //int iMaxSum=-1;
        String sStem1; String sStem2;
        //for (String[] sSentenceSPO: OpenNLP.sSPO) {
            String[] sSentenceSPO = OpenNLP.sSPO[index];
            iRunningSum=0;
            sStem1=sGetStem(sSentenceSPO[OpenNLP.SUBJECT]);
            sStem2=sGetStem(sSentenceSPO[OpenNLP.PREDICATE]);
            if (!"".equals(sStem1) && !"".equals(sStem2)) {
                iRunningSum += Math.abs(mapStems.get(sStem1.toLowerCase()).effectiveOccurrences - 
                        mapStems.get(sStem2.toLowerCase()).effectiveOccurrences);
            }
            sStem1=sGetStem(sSentenceSPO[OpenNLP.SUBJECT]);
            sStem2=sGetStem(sSentenceSPO[OpenNLP.OBJECT]);
            if (!"".equals(sStem1) && !"".equals(sStem2)) {
                iRunningSum += Math.abs(mapStems.get(sStem1.toLowerCase()).effectiveOccurrences - 
                        mapStems.get(sStem2.toLowerCase()).effectiveOccurrences);
            }
            sStem1=sGetStem(sSentenceSPO[OpenNLP.PREDICATE]);
            sStem2=sGetStem(sSentenceSPO[OpenNLP.OBJECT]);
            if (!"".equals(sStem1) && !"".equals(sStem2)) {
                iRunningSum += Math.abs(mapStems.get(sStem1.toLowerCase()).effectiveOccurrences - 
                        mapStems.get(sStem2.toLowerCase()).effectiveOccurrences);
            }
            //a++;
            if (iRunningSum >= significance) {
                //iReturn=a;
                //iMaxSum=iRunningSum;
                return true;
            }
            else return false;
        //}
        //return iReturn;
    }
    /**
     * Searches in {@link OpenNLP#sSPO} for clauses
     * with words with most significantly used
     * effective stems. Maximum differences
     * of frequency of use between words of same
     * clause is searched for.
     * @return index of according sentence in
     * Array {@link OpenNLP#sSPO}
     */
    public static int iSPOSearchSignificant() {
        int iReturn=-1;
        int a=-1;
        int iRunningSum=0;
        int iMaxSum=-1;
        String sStem1; String sStem2;
        for (String[] sSentenceSPO: OpenNLP.sSPO) {
            iRunningSum=0;
            sStem1=sGetStem(sSentenceSPO[OpenNLP.SUBJECT]);
            sStem2=sGetStem(sSentenceSPO[OpenNLP.PREDICATE]);
            if (!"".equals(sStem1) && !"".equals(sStem2)) {
                iRunningSum += Math.abs(mapStems.get(sStem1.toLowerCase()).effectiveOccurrences - 
                        mapStems.get(sStem2.toLowerCase()).effectiveOccurrences);
            }
            sStem1=sGetStem(sSentenceSPO[OpenNLP.SUBJECT]);
            sStem2=sGetStem(sSentenceSPO[OpenNLP.OBJECT]);
            if (!"".equals(sStem1) && !"".equals(sStem2)) {
                iRunningSum += Math.abs(mapStems.get(sStem1.toLowerCase()).effectiveOccurrences - 
                        mapStems.get(sStem2.toLowerCase()).effectiveOccurrences);
            }
            sStem1=sGetStem(sSentenceSPO[OpenNLP.PREDICATE]);
            sStem2=sGetStem(sSentenceSPO[OpenNLP.OBJECT]);
            if (!"".equals(sStem1) && !"".equals(sStem2)) {
                iRunningSum += Math.abs(mapStems.get(sStem1.toLowerCase()).effectiveOccurrences - 
                        mapStems.get(sStem2.toLowerCase()).effectiveOccurrences);
            }
            a++;
            if (iRunningSum > iMaxSum) {
                iReturn=a;
                iMaxSum=iRunningSum;                
            }
        }
        return iReturn;
    }
    /**
     * Searches in {@link OpenNLP#sSPO} for clauses
     * with words with least frequently
     * used effective stems. The first clause
     * with occurrences of only 1 for each word
     * is the result if found. Otherwise
     * search will go on.
     * @return index of according sentence in
     * Array {@link OpenNLP#sSPO}
     */
    public static int iSPOSearchExtraordinary() {
        int iReturn=-1;
        int a=-1;
        int iRunningSum=0;
        int iMinSum=100000000;//only some big number
        String sStem;
        for (String[] sSentenceSPO: OpenNLP.sSPO) {
            iRunningSum=0;
            sStem=sGetStem(sSentenceSPO[OpenNLP.SUBJECT]);
            if (!"".equals(sStem)) iRunningSum += mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
            sStem=sGetStem(sSentenceSPO[OpenNLP.PREDICATE]);
            if (!"".equals(sStem)) iRunningSum += mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
            sStem=sGetStem(sSentenceSPO[OpenNLP.OBJECT]);
            if (!"".equals(sStem)) iRunningSum += mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
            a++;
            if (iRunningSum < iMinSum && //otherwise 0 would be a solution
                    !"".equals(sSentenceSPO[OpenNLP.SUBJECT]) &&
                    !"".equals(sSentenceSPO[OpenNLP.PREDICATE]) &&
                    !"".equals(sSentenceSPO[OpenNLP.OBJECT])) {
                iReturn=a;
                iMinSum=iRunningSum;
                if (iMinSum==3) break;
            }
        }
        return iReturn;
    }
    /**
     * Searches in {@link OpenNLP#sSPO} for clauses
     * with words with most frequently
     * used effective stems.
     * @return index of according sentence in
     * Array {@link OpenNLP#sSPO}
     */
    public static int iSPOSearchMostCommon() {
        int iReturn=-1;
        int a=-1;
        int iRunningSum=0;
        int iMaxSum=0;
        String sStem;
        for (String[] sSentenceSPO: OpenNLP.sSPO) {
            iRunningSum=0;
            //for (String[][] sSentence: OpenNLP.sArray) {
            sStem=sGetStem(sSentenceSPO[OpenNLP.SUBJECT]);
            if (!"".equals(sStem)) iRunningSum += mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
            sStem=sGetStem(sSentenceSPO[OpenNLP.PREDICATE]);
            if (!"".equals(sStem)) iRunningSum += mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
            sStem=sGetStem(sSentenceSPO[OpenNLP.OBJECT]);
            if (!"".equals(sStem)) iRunningSum += mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
            a++;
            if (iRunningSum > iMaxSum) {
                iReturn=a;
                iMaxSum=iRunningSum;
            }
            //}
        }
        return iReturn; 
    }
    /**
     * Determines the sequential arrangement number
     * of every word and the number of occurences in the text
     * of every word.
     * @throws InterruptedException if Task on click
     * {@link FXMLDocumentController#BtAnalyze}
     * shell be terminated.
     */
    public static void main() throws InterruptedException {
        //for (int wordCounter = 0; wordCounter < words.length; wordCounter++) {
        map = new TreeMap<String, WordCountMapValue>();
        int wordTotalCounter = 0;
        int sentenceCounter = 0;
        for (String sentences[][] : OpenNLP.sArray) {
            for (String words[] : sentences) {
                WordCountMapValue value = new WordCountMapValue();
                String key = words[OpenNLP.WORD].toLowerCase();
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
            sentenceCounter++;
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            OpenNLP.Progress(.4+((double)sentenceCounter/OpenNLP.sArray.length)*.1);
        }
        //map = MapUtil.sortByValue(map,false);
        mapStems = new TreeMap<>();
        Map<String, WordCountMapValue> mapNew = new TreeMap<>();
        //map.entrySet().forEach((Map.Entry<String, WordCountMapValue> entry) -> {
        Set<Map.Entry<String, WordCountMapValue>> entrySet = map.entrySet();
        for (Map.Entry<String, WordCountMapValue> entry : entrySet) {
            WordCountMapValue mV = new WordCountMapValue();
            String sStem="";
            if (entry.getKey().length()>4) {
                sStem = entry.getKey().substring(0, entry.getKey().length()-2)+"..";
            }
            else {
                sStem = entry.getKey();
            }
            mV.key=entry.getKey();
            mV.sStem=sStem;
            mV.occurrences=entry.getValue().occurrences;
            mV.sequence=entry.getValue().sequence;
            //mV.occurencesStem = 0;
            mapNew.put(mV.key, mV);
            
            StemCountMapValue scmv=new StemCountMapValue();
            scmv.occurrences = mV.occurrences;
            scmv.effectiveOccurrences=0;
            scmv.key=mV.sStem;
            scmv.word=mV.key;
            //Operates similar to regular expression
            //by multiple points in each key String:
            if /*(*/(mapStems.get(mV.sStem) == null) /* &&
                (mapStems.get(key)+"." == null) &&
                (true &&
                (mapStems.get(key).toString().substring(mapStems.get(key).toString().length()-1) == null)))*/ {
                    mapStems.put(mV.sStem, scmv);
            }
            else {
                StemCountMapValue scmvTemp = mapStems.get(mV.sStem);
                scmvTemp.occurrences+=mV.occurrences;
                scmvTemp.effectiveOccurrences=0;
                scmvTemp.word+=", "+mV.key;
                scmvTemp.key=mV.sStem;
                mapStems.remove(mV.sStem);
                mapStems.put(mV.sStem, scmvTemp);
            }
        }//);
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
        OpenNLP.Progress(.6);
        map = mapNew;
        /*
        int a=0;        
        int[] integers = new int[map.size()];
        Set<Map.Entry<String, WordCountMapValue>> entrySetTemp = mapNew.entrySet();
        for (Map.Entry<String, WordCountMapValue> entry : entrySetTemp) {
            if (stem==stem) {
                integers[a]++; //entry.getValue().occurencesStem;
            }
            a++;
        }
        */
        Set<Map.Entry<String, WordCountMapValue>> entrySet2 = map.entrySet();
        for (Map.Entry<String, WordCountMapValue> entry : entrySet2) {
            //A text file saved in UTF-8 is displayed
            //correctly with German Umlaute in NB-console:
            //System.out.printf(entry.getValue() + "\t" + entry.getKey() + "\n");
            System.out.println(entry.getValue().sequence + "\t" +
                    entry.getValue().occurrences +
                    "(" + mapStems.get(entry.getValue().sStem).occurrences + ")" +
                    "\t" + entry.getKey()+
                    "[" + mapStems.get(entry.getValue().sStem).word + "]");
        }
    }
}
