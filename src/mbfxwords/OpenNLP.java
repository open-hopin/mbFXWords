/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import javafx.scene.control.Alert;
import opennlp.tools.cmdline.langdetect.LanguageDetectorModelLoader;
//import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * The class provides the methods to use Apache OpenNLP.
 * @author MB
 */
public class OpenNLP {
    
    /**
     * Accesses a word in sArray[][][~].
     */
    final static int WORD = 0;
    /**
     * Accesses a POS tag in sArray[][][~].
     */
    final static int TAG = 1;
    /**
     * Accesses a part-of-speech in sArray[][][~] in according language.
     */
    final static int WORD_TYPE = 2;
    /**
     * String array with indices [sentences][word][WORD|TAG|WORD_TYPE].
     */
    static String[][][] sArray; // = new String[1][][];//[sentences][word][WORD|TAG|WORD_TYPE]    
    
    /**
     * Accesses a subject in sSPO[][~].
     */
    final static int SUBJECT = 0;
    /**
     * Accesses a predicate in sSPO[][~].
     */
    final static int PREDICATE = 1;
    /**
     * Accesses a object in sSPO[][~].
     */
    final static int OBJECT = 2;
    /**
     * String array with indices [sentences][SUBJECT|OBJECT|PREDICATE].
     */
    static String[][] sSPO; // = new String[1000][3];//[sentences][SUBJECT|OBJECT|PREDICATE]
    /**
     * All sentences of
     * {@link OccurrenceOfWords#sInput}
     * after call of
     * {@link OpenNLP#SentenceDetect}.
     * Sentences including punctuation
     * except for where they are divided
     * and segmented.
     */
    static String[] sSentences;
    /**
     * Language code abbreviation
     * of text in String
     * {@link OccurrenceOfWords#sInput}
     * after call of
     * {@link OpenNLP#sLanguageDetection}:
     * "eng" - English, "deu" - German, "fra" - French.
     */
    static String sLanguage;
    /**
     * Not used so far.
     */
    public static void main() {
    //System.out.println("hello");
    }
    
    /**
     * Sentence detection invoked according to OpenNLP
     * and extended for own needs.
     * @return array of segmented sentences divided by subclauses
     * @throws InvalidFormatException to caller
     * @throws IOException to caller
     */
    public static String[] SentenceDetect() throws InvalidFormatException,IOException {
        String[] sentences;
        // always start with a model, a model is learned from training data
        InputStream is;
        if ("eng".equals(sLanguage)) 
            is = new FileInputStream("en-sent.bin");
        else if ("deu".equals(sLanguage))
            is = new FileInputStream("de-sent.bin");
        else //if ("fra".equals(sLanguage))
            is = new FileInputStream("fr-sent.bin");
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);
        String[] sentences1 = sdetector.sentDetect(OccurrenceOfWords.sInput);
        sentences = new String[sentences1.length*5];
        //Segmentation in subclauses:
        String[] sentences2; //= new String[10];
        String[] sentences3; //= new String[100];
        int a=0;
        Boolean b = false;
        for (String sentence : sentences1) {
            System.out.println("[" + sentence + "]");
            if ("eng".equals(sLanguage))
                //sentences2 = sentence.split(", | - |\n");
                sentences2 = sentence.split(", | - |\n|(?=( whether ))|(?=( if ))");
            else
                sentences2 = sentence.split(", | - |\n");
            for (String s2 : sentences2) {
                s2=s2.trim();
                if (!"".equals(s2)) {// s2 != "" doesn't do here,
                    // for it's the same String "" every time
                    sentences3 = s2.split(" ");
                    if (sentences3.length < 3 && !b) {
                        sentences[a] = s2;
                        b= true;
                    } else if (b) {
                        sentences[a] = sentences[a] + ", " + s2;
                        //System.out.println(sentences[a]);
                        b= false; a++;
                    } else {
                        sentences[a] = s2;
                        //System.out.println(sentences[a]);
                        a++;
                    }
                }
            }
            if (b) {a++; b = false; /*System.out.println(sentences[a-1]);*/}
        }
        int d = 0;
        for (String sentence : sentences) {
            if (sentence == null) d++;
        }
        String[] sentences4 = new String [sentences.length-d];
        d = 0;
        for (String sentence : sentences) {
            if (sentence != null) {
                sentences4[d] = sentence;
                d++;
            }
        }
        return sentences4;
    }
    /**
     * Detects language of text in String
     * {@link OccurrenceOfWords#sInput}
     * @return language code abbreviation
     */
    public static String sLanguageDetection() {
        //String inputText = "Hallo Mike, das ist Marc. Ein Auto hat 2 Lichter.";
        LanguageDetectorModel m = new LanguageDetectorModelLoader()	
		.load(new File("langdetect-183.bin"));
        LanguageDetector myCategorizer = new LanguageDetectorME(m);
        // Get the most probable language
        Language bestLanguage = myCategorizer.predictLanguage(OccurrenceOfWords.sInput);
        System.out.println("Best language: " + bestLanguage.getLang()); //eng - English, deu - German, fra - French
        System.out.println("Best language confidence: " + bestLanguage.getConfidence());
        return bestLanguage.getLang();
    }
    /**
     * Part of speech tags are assigned a part of speech,
     * so information is reduced.
     * @param sEntry English Penn Treebank P.O.S. Tags
     * @return part of speech
     */
    public static String sPartOfSpeechEN(String sEntry) {
        String sTemp = "";
        switch (sEntry) {                                
            case "CC": sTemp="conjunction";break;
            case "CD": sTemp="cardinal";break;
            case "DT": sTemp="determiner";break;
            case "WDT": sTemp="determiner";break;
            case "EX": sTemp="ENthere";break;
            case "FW": sTemp="loanword";break;
            case "IN": sTemp="preposition";break;
            case "JJR": sTemp="adjective";break;
            case "JJS": sTemp="adjective";break;
            case "JJ": sTemp="adjective";break;
            case "NN": sTemp="noun";break;
            case "NNS": sTemp="noun";break;
            case "NNP": sTemp="noun";break;
            case "NNPS": sTemp="noun";break;
            case "WRB": sTemp="adverb";break;
            case "RB": sTemp="adverb";break;
            case "RBR": sTemp="adverb";break;
            case "RBS": sTemp="adverb";break;
            case "VB": sTemp="verb"; break;
            case "VBZ": sTemp="verb"; break;
            case "VBP": sTemp="verb"; break;
            case "VBD": sTemp="verb"; break;
            case "VBN": sTemp="verb"; break; //past participle
            case "VBG": sTemp="verb"; break;
            case "LS": sTemp="list"; break;
            case "MD": sTemp="modal"; break;
            case "PDT": sTemp="Predeterminer"; break;
            case "POS": sTemp="Possessive"; break;
            case "PRP": sTemp="pronoun"; break;
            case "PRP$": sTemp="pronoun"; break;
            case "WP": sTemp="pronoun"; break;
            case "WP$": sTemp="pronoun"; break;
            case "RP": sTemp="Particle"; break; 
            case "SYM": sTemp="Symbol"; break;
            case "TO": sTemp="to"; break;
            case "UH": sTemp="Interjection"; break;
            default: sTemp=sEntry;
        }
        return sTemp;
    }
    /**
     * Part of speech tags are assigned a part of speech,
     * so information is reduced.
     * Manually adapted for use with model:
     * <a href="https://github.com/fusepoolP3/p3-stanbol-launcher/tree/master/data/opennlp-pos/src/main/resources/models" target="_blank">github.com fr-pos-maxent.bin</a>.
     * @param sEntry Corpus arboré pour le français / French Treebank P.O.S. Tags
     * @return part of speech
     */
    public static String sPartOfSpeechFR(String sEntry) {
        String sTemp = "";
        switch (sEntry) {                                
            case "A": sTemp="adjective";break;
            case "ADV": sTemp="adverb";break;
            case "CC": sTemp="conjunction";break;
            case "CI": sTemp="pronoun";break;
            case "CS": sTemp="conjunction";break;
            case "D": sTemp="determiner";break;
            case "ET": sTemp="loanword";break;
            case "I": sTemp="interjection";break;
            case "NC": sTemp="noun";break;
            case "NP": sTemp="noun";break;
            case "P": sTemp="preposition";break;
            case "PREF": sTemp="prefix";break;
            case "PRO": sTemp="pronoun";break;
            case "V": sTemp="verb";break;
            case "PONCT": sTemp="punctuation";break;
            //
            case "PINF": sTemp="verb";break;
            case "DET": sTemp="determiner";break;
            case "ADJ": sTemp="adjective";break;
            case "VINF": sTemp="verb"; break;
            case "P+D": sTemp="determiner"; break; //pronoun+determiner
            case "NPP": sTemp="noun"; break;
            case "CLR": sTemp="pronoun"; break;
            case "VPP": sTemp="verb"; break; //past participle
            case "PROREL": sTemp="pronoun"; break;
            case "VPR": sTemp="verb"; break;
            case "CLO": sTemp="preposition"; break;
            case "PROWH": sTemp="pronoun"; break;
            case "CLS": sTemp="pronoun"; break;
            default: sTemp=sEntry;
        }
        return sTemp;
    }
    /**
     * Part of speech tags are assigned a part of speech,
     * so information is reduced.
     * @param sEntry German STTS (Stuttgart–Tubingen Tagset) part-of-speech tagset
     * @return part of speech
     */
    public static String sPartOfSpeechDE(String sEntry) {
        String sTemp = "";
        switch(sEntry){
            case "ADJA": sTemp="adjective"; break;
            case "ADJD": sTemp="DEPrädikativumAdverb"; break;
            case "ADV": sTemp="adverb"; break;
            case "APPR": sTemp="preposition"; break;
            case "APPRART": sTemp="preposition"; break;
            case "APPO": sTemp="preposition"; break;
            case "APZR": sTemp="preposition"; break;
            case "ART": sTemp="article"; break;
            case "CARD": sTemp="cardinal"; break;
            case "FM": sTemp="loanword"; break;
            case "ITJ": sTemp="interjection"; break;
            case "KON": sTemp="conjunction"; break; //Komma
            case "KOKOM": sTemp="particle"; break;
            case "KOUI": sTemp="DEum_zu"; break; //Komma
            case "KOUS": sTemp="conjunction"; break; //Komma
            case "NA": sTemp="noun"; break;
            case "NE": sTemp="name"; break;
            case "NN": sTemp="noun"; break;
            case "PAV": sTemp="pronoun"; break;
            case "PROAV": sTemp="pronoun"; break;
            case "PAVREL": sTemp="pronoun"; break; //Komma
            case "PDAT": sTemp="pronoun"; break;
            case "PDS": sTemp="pronoun"; break;
            case "PIAT": sTemp="numeral"; break;
            case "PIS": sTemp="pronoun"; break;
            case "PPER": sTemp="pronoun"; break;
            case "PRF": sTemp="pronoun"; break;
            case "PPOSS": sTemp="pronoun"; break;
            case "PPOSAT": sTemp="pronoun"; break;
            case "PRELAT": sTemp="pronoun"; break; //Komma
            case "PRELS": sTemp="pronoun"; break; //Komma
            case "PTKA": sTemp="particle"; break;
            case "PTKANT": sTemp="particle"; break;
            case "PTKNEG": sTemp="particle"; break;
            case "PTKREL": sTemp="particle"; break;
            case "PTKVZ": sTemp="preposition"; break;
            case "PTKZU": sTemp="particle"; break;
            case "PWS": sTemp="pronoun"; break;
            case "PWAT": sTemp="pronoun"; break;
            case "PWAV": sTemp="pronoun"; break;
            case "PWAVREL": sTemp="pronoun"; break; //Komma
            case "PWREL": sTemp="pronoun"; break; //Komma
            case "TRUNC": sTemp="noun"; break;
            case "VAFIN": sTemp="verb"; break;
            case "VAIMP": sTemp="verb"; break;
            case "VAINF": sTemp="verb"; break;
            case "VAPP": sTemp="verb"; break;
            case "VMFIN": sTemp="verb"; break;
            case "VMINF": sTemp="verb"; break;
            case "VMPP": sTemp="verb"; break;
            case "VVFIN": sTemp="verb"; break;
            case "VVIMP": sTemp="verb"; break;
            case "VVINF": sTemp="verb"; break;
            case "VVIZU": sTemp="verb"; break;
            case "VVPP": sTemp="verb"; break; //past participle
            default: if ("FM".equals(sEntry.substring(0, 2))) {sTemp="loanword";}
                     else sTemp=sEntry;
        }
        return sTemp;
    }
    /**
     * Part-of-speech Tagging invoked according to OpenNLP.
     * @param file disk txt-file to be analized
     * @throws IOException to caller
     */
    public static void POSTag(String file) throws IOException {
        //sArray = new String[1][][];
        sSPO = new String[1000][3];
        sLanguage = OpenNLP.sLanguageDetection();
        if (!"eng".equals(sLanguage) && !"deu".equals(sLanguage) && !"fra".equals(sLanguage)) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Message of mbFXWords");
                                alert.setHeaderText("The language used is not supported.");
                                alert.setContentText("You can use English, German or French so far.");
                                alert.showAndWait();
                                return;
        }
        POSModel model;
        if ("eng".equals(sLanguage)) 
            model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
        else if ("deu".equals(sLanguage))
            model = new POSModelLoader().load(new File("de-pos-maxent.bin"));
        else //if ("fra".equals(sLanguage))
            model = new POSModelLoader().load(new File("fr-pos-maxent.bin"));
        //System.out.println(model.toString());
        //PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
	POSTaggerME tagger = new POSTaggerME(model);
 
	//String input = "Hi. How are you? This is Mike.";
        
        Charset charset = Charset.forName("UTF-8");
        //InputStreamFactory fa = InputStreamFactory(input);
        
        InputStreamFactory fa = new MarkableFileInputStreamFactory(
        new File(file));
        
        ObjectStream<String> lineStream = new PlainTextByLineStream(
                fa,charset);
		//new StringReader(input));
 
                        
        //perfMon.start();
	                
        //String line;
        sSentences = SentenceDetect();
        int iAllSentences = sSentences.length;//counter of all sentences
        /*
        int iAllSentences = 0;//counter of all sentences
        while ((line = lineStream.read()) != null) {
            if (!"".equals(line)) {
                String[] sentences = line.split("[.,;:!?(){}\\[\\]]"); //",|\\."
                for ( String sentence : sentences ) {
                    if (!"".equals(sentence)) {
                        iAllSentences++;
                    }
                }
            }
        }
        */
        sArray = new String[iAllSentences][][];//[sentences][words][word;tag;word_type]
        int b=0;//sentence counter
        for (String sentence : sSentences) {
            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                .tokenize(sentence);
            String[] tags = tagger.tag(whitespaceTokenizerLine);

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

            String[] Words = sample.getSentence();
            String[] Tags = sample.getTags();
            System.out.println(Arrays.toString(Words));
            System.out.println(Arrays.toString(Tags));

            String temp="";
            int c=0;//word counter
            sArray[b] = new String [tags.length][3];
            for ( String entry : tags ) {
                sArray[b][c][TAG]=entry;
                if (",".equals(Words[c].substring(Words[c].length()-1))||
                        ":".equals(Words[c].substring(Words[c].length()-1))||
                        ")".equals(Words[c].substring(Words[c].length()-1))||
                        "\"".equals(Words[c].substring(Words[c].length()-1))||
                        "?".equals(Words[c].substring(Words[c].length()-1))||
                        ";".equals(Words[c].substring(Words[c].length()-1)))
                    Words[c] = Words[c].substring(0, Words[c].length()-1);
                if (!"".equals(Words[c]) && "(".equals(Words[c].substring(0,1)) ||
                        !"".equals(Words[c]) && "\"".equals(Words[c].substring(0,1)))
                    Words[c] = Words[c].substring(1, Words[c].length());
                boolean isUpperCaseInWord = false;
                Character cTemp;
                for (int a=0; a < Words[c].length()-1;a++) {
                    cTemp = Words[c].charAt(a);
                    if (Character.isUpperCase(cTemp))
                        isUpperCaseInWord = true;
                }
                if (!"".equals(Words[c]) && ".".equals(Words[c].substring(Words[c].length()-1))
                        && !Words[c].substring(0,Words[c].length()-1).contains(".")
                        && !isUpperCaseInWord
                        )
                    Words[c] = Words[c].substring(0, Words[c].length()-1);
                sArray[b][c][WORD]=Words[c];
                if ("eng".equals(sLanguage)) temp = sPartOfSpeechEN(entry);
                else if ("deu".equals(sLanguage)) temp = sPartOfSpeechDE(entry);
                else temp = sPartOfSpeechFR(entry);
                sArray[b][c][WORD_TYPE]=temp;
                if (!"".equals(temp)) System.out.print(sArray[b][c][WORD_TYPE] + " ");
                c++;//word counter
            }
            System.out.println();
            //perfMon.incrementCounter();
            //SPO-Normalform:
            System.out.println(sample.getSentence().length);
            String sTemp="Subject";
            //for ( String word : sample.getSentence() ) {
            sSPO[b][SUBJECT]="";
            sSPO[b][OBJECT]="";
            sSPO[b][PREDICATE]="";
            
            int a=0;
            for (String[] sIterator : sArray[b]){
                if ("noun".equals(sIterator[WORD_TYPE])){
                    if ("Subject".equals(sTemp)) {
                        System.out.println("Subject: " + Words[a]);
                        sSPO[b][SUBJECT]=Words[a];
                    }
                    if ("Object".equals(sTemp)) {
                        System.out.println("Object: " + Words[a]);
                        if ("".equals(sSPO[b][OBJECT]) &&
                            !"".equals(sSPO[b][PREDICATE])) sSPO[b][OBJECT]=Words[a];
                    }
                    sTemp="Object";
                }
                if ("verb".equals(sIterator[WORD_TYPE])){
                    System.out.println("Predicate: " + Words[a]);
                    if ("".equals(sSPO[b][PREDICATE])) sSPO[b][PREDICATE]=Words[a];
                    // Prefer past participle:
                    if ("".equals(sSPO[b][OBJECT]) && "eng".equals(sLanguage) && "VBN".equals(sIterator[TAG])) 
                        sSPO[b][PREDICATE]=Words[a];
                    else if ("".equals(sSPO[b][OBJECT]) && "fra".equals(sLanguage) && "VPP".equals(sIterator[TAG])) 
                        sSPO[b][PREDICATE]=Words[a];
                    else if ("".equals(sSPO[b][OBJECT]) && "deu".equals(sLanguage) && "VVPP".equals(sIterator[TAG])) 
                        sSPO[b][PREDICATE]=Words[a];
                }
                a++;//word counter
            }
            //
            System.out.println();
            b++;//sentence counter
        }
    }
}
