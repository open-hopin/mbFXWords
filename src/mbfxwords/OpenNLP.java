/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import static mbfxwords.OccurrenceOfWords.sInput;
import opennlp.tools.cmdline.langdetect.LanguageDetectorModelLoader;
import opennlp.tools.cmdline.parser.ParserTool;
//import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * The class provides the methods to use Apache OpenNLP.
 * @author MB
 */
public class OpenNLP {
    /**
     * Number of effective stems in text
     * analyzed.
     */
    public static int imEffectiveStems;
    /**
     * Needed global for OpenNLP POS tagging.
     */
    static POSTaggerME tagger;
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
    static String[][][] sArray; // = new String[1][][];//[sentence][word][WORD|TAG|WORD_TYPE]    
    
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
     * {@link OpenNLP#OpenNLPMainSentenceDetect}.
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
     * Serves color change of ProgressBar
     * in main window, used by {@link #Progress(java.lang.Double) }.
     */
    private static Boolean bBlink=false;
    /**
     * Serves color change of ProgressBar
     * in main window, used by {@link #Progress(double) }.
     */
    //private static final String BLUE1 = "blue1-bar";
    /**
     * Serves color change of ProgressBar
     * in main window, used by {@link #Progress(double) }.
     */
    //private static final String BLUE2  = "blue2-bar";
    /**
     * Serves color change of ProgressBar
     * in main window, used by {@link #Progress(double) }.
     */
    //private static final String[] barColorStyleClasses = { BLUE1, BLUE2 };
    /**
     * Used by {@link #Progress(java.lang.Double)}
     * to avoid unnecessary progress bar updates,
     * that can cause ConcurrentModificationException
     * in {@link #iEffectiveStems()}.
     */
    private static int iProgressDiff=0;
    /**
     * Access the controller and set
     * progress bar of main window.
     * Used as every progress setting.
     * @param d progress between 0 and 1
     */
    public static void Progress(Double d) {
        Integer iTemp=((int)(Math.round(d*100)));
        if (Math.abs(iTemp-iProgressDiff)>=2) {
            iProgressDiff=iTemp;
            bBlink =! bBlink;
            /* Does not change colors every time because of bug in JRE 1.8:
            Random rand = new Random();
            Integer number = rand.nextInt(25) + 1;//(int)((Math.random()) * 25 + 1);
            MbFXWords.c.pbMain.setStyle("-fx-accent: #"+
                    Integer.toHexString(number)+Integer.toHexString(number)+"ff;");
            */
            //MbFXWords.c.pbMain.getStyleClass().removeAll(barColorStyleClasses);
            try {
                if (bBlink) {
                    MbFXWords.c.pbMain.setStyle("-fx-accent: #1010ff;");
                    //Internal HashMap causes uncaught exceptions===:
                    //MbFXWords.c.pbMain.getStyleClass().remove(BLUE2);
                    //MbFXWords.c.pbMain.getStyleClass().add(BLUE1);
                }
                else {
                    MbFXWords.c.pbMain.setStyle("-fx-accent: #0000ff;");
                    //Internal HashMap causes uncaught exceptions===:
                    //MbFXWords.c.pbMain.getStyleClass().remove(BLUE1);
                    //MbFXWords.c.pbMain.getStyleClass().add(BLUE2);
                }
            } catch (ConcurrentModificationException ex) {
                //Do nothing, if task is running with previous
                //invocation, because this method is not thread safe.
            }
            MbFXWords.c.pbMain.setProgress(d);
            MbFXWords.c.pbMain.setTooltip(new Tooltip(iTemp.toString()+"%"));
            if ("most common words".equals(MbFXWords.c.lblWords.getText()))
                MbFXWords.c.lblWords.setTooltip(new Tooltip(iTemp.toString()+"%"));
        }
    }
    /**
     * Jagged {@link #sArray} first two dimensiones are multiplied.
     * @return number of word or tag entries in {@link #sArray}
     */
    public static int iArrayNoOfWords() {
        int iReturn=0;
        for (String sTemp1[][]: sArray) {
            for (String sTemp2[]: sTemp1) {
                iReturn++; //words
            }
            //sentences
        }
        return iReturn;
    }
    /**
     * The number of effective Stems
     * in {@link OccurrenceOfWords#mapStems}
     * is calculated. Effective stems are
     * those that ar pairwise different according to
     * {@link #bStemsEqual}.
     * Average effectiveness of stems
     * in form of no. of effective stems is given back.
     * This is only a dimensionless number
     * as an estimation for the sum.
     * The real number is a little less.
     * @throws InterruptedException if Task on click
     * {@link FXMLDocumentController#BtAnalyze}
     * shell be terminated.
     * @return estimated number of effective Stems in
     * {@link OccurrenceOfWords#mapStems}
     */
    public static int iEffectiveStems() throws InterruptedException {
        int iReturn=0;
        int a=0; int b=0;
        Set<Map.Entry<String, StemCountMapValue>> entrySet1 = OccurrenceOfWords.mapStems.entrySet();
        for (Map.Entry<String, StemCountMapValue> entryStem1 : entrySet1) {
            Set<Map.Entry<String, StemCountMapValue>> entrySet2 = OccurrenceOfWords.mapStems.entrySet();
            b=0;
            OccurrenceOfWords.mapStems.get(entryStem1.getKey()).effectiveOccurrences+=
                    OccurrenceOfWords.mapStems.get(entryStem1.getKey()).occurrences;
            for (Map.Entry<String, StemCountMapValue> entryStem2 : entrySet2) {
                //if (a==0) OccurrenceOfWords.mapStems.get(entryStem2.getKey()).effectiveOccurrences+=
                //    OccurrenceOfWords.mapStems.get(entryStem2.getKey()).occurrences;
                if (a<b) { //do not count duplicate
                    if (!bStemsEqual(entryStem1.getKey(),entryStem2.getKey())) {
                        iReturn++;
                        //System.out.println("Unequal Stems:");
                        //System.out.println(entryStem1.getKey()+"|"+entryStem2.getKey());
                    }
                    else { //iReturn+=2;
                        OccurrenceOfWords.mapStems.get(entryStem1.getKey()).effectiveOccurrences+=
                                OccurrenceOfWords.mapStems.get(entryStem2.getKey()).occurrences;
                        //System.out.println("Equal Stems:");
                        //System.out.println(entryStem1.getKey()+"|"+entryStem2.getKey());
                        OccurrenceOfWords.mapStems.get(entryStem2.getKey()).effectiveOccurrences+=
                                OccurrenceOfWords.mapStems.get(entryStem1.getKey()).occurrences;
                    }
                }
                b++;
            }
            a++;
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            OpenNLP.Progress(.7+((double)a/entrySet1.size())*.2);
        }
        //iReturn=(int)Math.round(Math.sqrt((double)iReturn-0.5*a));
        double dReturn;
        /*
        dReturn=(iReturn*2+a)/Math.pow(a,2);
        iReturn=(int)Math.round((dReturn-1)*a);
        */
        //dReturn=Math.pow(OccurrenceOfWords.mapStems.size(), 2)/(iReturn*2);
        dReturn=Math.sqrt(iReturn); //average effectiveness of stems in form of no. of effective stems
        
        //dReturn=Math.pow(OccurrenceOfWords.mapStems.size(), 2)/(dReturn*2);
        //dReturn=iReturn/dReturn;
        
        iReturn=(int)Math.round(dReturn);
        return iReturn;
    }
    /**
     * Compares two Strings that are representing word stems
     * and evaluates if these are representing
     * the same word stem.
     * @param sStemA first stem to be compared
     * @param sStemB second stem to be compared
     * @return whether sStemA and sStemB are representing
     * same word stem
     */
    public static Boolean bStemsEqual(String sStemA, String sStemB) {
        //Boolean b = sStemB.equals("Verkehrsb.."); //.equals("Verkehrsb..") && sStemB.equals("Verkehrsbus..");
        Boolean bReturn=false;
        if (Math.abs(sStemA.length()-sStemB.length())>2) {
            bReturn=false;
        } else if (!".".equals(sStemA.substring(sStemA.length()-1, sStemA.length())) ||
                   !".".equals(sStemB.substring(sStemB.length()-1, sStemB.length()))) {
            bReturn=(sStemA.equals(sStemB));
        } else if (Math.abs(sStemA.length()-sStemB.length())<2) {
            if (sStemA.length()-sStemB.length()<0) {
                String sOriginalWord = OccurrenceOfWords.mapStems.get(sStemA).word;
                String[] words=sOriginalWord.split(", "); //new
                Boolean b=false; //new
                for (String word: words) { //new
                    //sStemA+=".";//same length now sStemA and SStemB
                    String sStem = sStemA.substring(0,sStemA.length()-2);
                    if (word.length()>1) sStem+=word.substring(word.length()-2,word.length()-1);
                    else sStem+="_";
                    sStem+="..";
                    //sStemA=sStem;
                    if (sStem.equals(sStemB)) b=true;
                }
                bReturn=b;
            } else {
                String sOriginalWord = OccurrenceOfWords.mapStems.get(sStemB).word;
                String[] words=sOriginalWord.split(", "); //new
                Boolean b=false; //new
                for (String word: words) { //new
                    //sStemB+=".";//same length now sStemA and SStemB
                    String sStem = sStemB.substring(0,sStemB.length()-2);
                    if (word.length()>1) sStem+=word.substring(word.length()-2,word.length()-1);
                    else sStem+="_";
                    sStem+="..";
                    //sStemB=sStem;
                    if (sStem.equals(sStemA)) b=true;
                }
                bReturn=b;
            }
        } else { //length difference is 2
            if (sStemA.length()-sStemB.length()<0) {
                String sOriginalWord = OccurrenceOfWords.mapStems.get(sStemA).word;
                String[] words=sOriginalWord.split(", "); //new
                Boolean b=false; //new
                for (String word: words) { //new
                    //sStemA+="..";//same length now sStemA and SStemB
                    String sStem = sStemA.substring(0,sStemA.length()-2);
                    if (word.length()>1) sStem+=word.substring(word.length()-2,word.length());
                    else sStem+="__";
                    sStem+="..";
                    //sStemA=sStem;
                    if (sStem.equals(sStemB)) b=true;
                }
                bReturn=b;
            } else {
                String sOriginalWord = OccurrenceOfWords.mapStems.get(sStemB).word;
                String[] words=sOriginalWord.split(", "); //new
                Boolean b=false; //new
                for (String word: words) { //new
                    //sStemB+="..";//same length now sStemA and SStemB
                    String sStem = sStemB.substring(0,sStemB.length()-2);
                    if (word.length()>1) sStem+=word.substring(word.length()-2,word.length());
                    else sStem+="__";
                    sStem+="..";
                    //sStemB=sStem;
                    if (sStem.equals(sStemA)) b=true;
                }
                bReturn=b;
            }
        }            
        return bReturn;
    }
    /**
     * Not used so far.
     */
    public static void main() {
        //todo
    }
    //@throws InvalidFormatException
    /**
     * For test purposes only.
     * @param sTemp String to parse
     * @throws IOException by FileInputStream(), ParserModel()
     */
    public static void Parse(String sTemp) throws /*InvalidFormatException,*/ IOException {
	InputStream is;
        if (MbFXWords.bRunResourcesFromHome)
            is = new FileInputStream("resources/en-parser-chunking.bin");
        else
            is = new FileInputStream(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/en-parser-chunking.bin");
	ParserModel model = new ParserModel(is);
 
	Parser parser = ParserFactory.create(model);
 
	String sentence = sTemp;
	Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
 
	for (Parse p : topParses)
		p.show();
 
	is.close();
 
	/*
	 * (TOP (S (NP (NN Programcreek) ) (VP (VBZ is) (NP (DT a) (ADJP (RB
	 * very) (JJ huge) (CC and) (JJ useful) ) ) ) (. website.) ) )
	 */
    }
    /**
     * Makes sure a readable plain text is achieved
     * after {@link #ReadIn }.
     * @param text to be improved to plain text
     * @return plain text
     */
    public static String textPostProcessing (String text) {
        text=text.replaceAll("\\.+",Matcher.quoteReplacement("."));
        text=text.replaceAll("\\_+",Matcher.quoteReplacement("_"));
        text=text.replaceAll("\\=+",Matcher.quoteReplacement("="));
        text=text.replaceAll("\\-+",Matcher.quoteReplacement("-"));
        text=text.replaceAll("\\#+",Matcher.quoteReplacement("#"));
        text=text.replaceAll("\\(+",Matcher.quoteReplacement("("));
        text=text.replaceAll("\\)+",Matcher.quoteReplacement(")"));
        text=text.replaceAll("\\++",Matcher.quoteReplacement("+"));
        text=text.replaceAll("\\,+",Matcher.quoteReplacement(","));
        text=text.replaceAll("\\!+",Matcher.quoteReplacement("!"));
        //
        text=text.replaceAll("[\\!]+",Matcher.quoteReplacement(" "));
        /*
        String pattern = "(\\.)+";        
        Pattern p = Pattern.compile(pattern);        
        Matcher m = p.matcher(text);
        //text=m.replaceAll(Matcher.quoteReplacement("."));
        if (m.find()) text=m.replaceAll(m.group(0).substring(m.group(0).length()-1));
        */
        return text;
    }
    /**
     * Reads in file and stores UTF-8 content in
     * String {@link OccurrenceOfWords#sInput}.
     * @param file Disk txt or pdf-file to be analyzed.
     * The related path is relative to application path.
     * @throws FileNotFoundException to caller
     * @throws IOException to caller
     */
    public static void ReadIn(String[] file) throws FileNotFoundException, IOException {
        sInput = "";
        String sDelimiter;
        if (MbFXWords.bWin) sDelimiter="\\"; else sDelimiter="/";
        if (file.length == 0) {
            System.out.println("Usage: ReadIn targetfile");
            System.exit(0);
        }
        if (file[0].endsWith("txt")) {
            /*
            BufferedReader bufferedReader = null;
            bufferedReader = new BufferedReader(new FileReader(file[0]));
            */
            Reader reader;
            if (sDelimiter.equals(file[0].substring(0, 1)) || 
                    (MbFXWords.bWin && (sDelimiter.equals(file[0].substring(0, 1)) || ":".equals(file[0].substring(1, 2)))))
                    reader = new InputStreamReader(new FileInputStream(file[0]),"UTF-8"); //absolute path at hand here
            else { //relative path
                if (MbFXWords.bRunResourcesFromHome)
                    reader = new InputStreamReader(new FileInputStream(file[0]),"UTF-8");
                else
                    reader = new InputStreamReader(new FileInputStream(
                            MbFXWords.c.fActiveJarPathWithoutFilename() + sDelimiter+file[0]),"UTF-8");
            }
            BufferedReader bufferedReader = new BufferedReader(reader);

            String inputLine = null;
            //String outputLine = null;
            //String finalLine = null;
            try {
                while ((inputLine = bufferedReader.readLine()) != null) {
                    if (!"".equals(sInput)) sInput += "\n"; sInput += inputLine;
                    /*
                    outputLine = inputLine.toLowerCase();
                    String[] words = outputLine.split("[ \"\n\t\r.,;:!?(){}]");
                    */
                }
            }
            catch (IOException error) {
                System.out.println("Invalid File");  
            }
            finally {
                bufferedReader.close();
            }
        }
        else { //pdf-file
            
            java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
            File fPDF;
            if (sDelimiter.equals(file[0].substring(0, 1)) || 
                    (MbFXWords.bWin && (sDelimiter.equals(file[0].substring(0, 1)) || ":".equals(file[0].substring(1, 2)))))
                fPDF = new File(file[0]); //absolute path at hand here
            else { //relative path
                if (MbFXWords.bRunResourcesFromHome)
                    fPDF = new File(file[0]);
                else
                    fPDF = new File(MbFXWords.c.fActiveJarPathWithoutFilename() + sDelimiter+file[0]);
            }
            PDDocument document = PDDocument.load(fPDF);
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                sInput = stripper.getText(document);
                //System.out.println("=======Text:\n" + sInput + "\n\n");
                document.close();                
            }
            else {
                sInput = "wrongzulu88..";
                sArray = new String[1][][];
                return;
            }
        }    
    }
    /**
     * Long sentences are checked for subclauses
     * conected by conjunctions. These sentences
     * are divided accordingly.
     * @param sentences source to be further divided in more elements
     * @param tagger by OpenNLP
     * @return modified result sentences
     */
    public static String[] SentenceCorrectTooLong(String[] sentences, POSTaggerME tagger){
        List<String> sentencesResult = new ArrayList<>();
        int a=0;
        for (String sentence:sentences){
            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                .tokenize(sentence);
            String[] tags = tagger.tag(whitespaceTokenizerLine);

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

            String[] Words = sample.getSentence();
            if (Words.length>6) { //10
                String[] Tags = sample.getTags();
                String[] POS = new String[Tags.length];
                String s = "";
                int b=0;
                int c=0;
                Boolean b1=false;
                Boolean b2=false;
                Boolean b3=false;
                for (String tag:Tags) {
                    if ("eng".equals(sLanguage)) POS[b] = sPartOfSpeechEN(tag);
                    else if ("deu".equals(sLanguage)) POS[b] = sPartOfSpeechDE(tag);
                    else POS[b] = sPartOfSpeechFR(tag);
                    if (POS[b]=="verb" && !b1) b1=true;
                    if ((POS[b]=="conjunction" && b1 && !b2)||(POS[b]=="interjection" && b1 && !b2)) {
                        b2=true;
                        c=b;
                    }
                    if (POS[b]=="verb" && b1 && b2) b3=true;
                    b++;
                }
                if (!b3) {
                    sentencesResult.add(sentences[a]);
                    a++;
                }
                else {
                    s=wordPostProcessing(Words[c])+".{1,2}"+
                            wordPostProcessing(Words[c+1]); //assumed only one character is missing or 2
                    String[] sArrayTemp;
                    //sArrayTemp=sentences[a].split(Words[c-1]+".(?="+s+")");
                    //sArrayTemp=sentences[a].split("(?="+Words[c-1]+"."+s+")");
                    sArrayTemp=sentences[a].split("(?="+s+")");
                    for (String part: sArrayTemp) {
                        sentencesResult.add(part.trim());
                    }
                    a++;
                }
            }
            else {
                sentencesResult.add(sentences[a]);
                a++;
            }
                
        }
        return sentencesResult.toArray(new String[sentencesResult.size()]);
    }
    /**
     * Sentence detection invoked according to OpenNLP
     * applied for own needs.
     * @param sText text to be divided in main clauses
     * @return main sentences
     * @throws InvalidFormatException to caller
     * @throws IOException to caller
     */
    public static String[] OpenNLPMainSentenceDetect(String sText) throws InvalidFormatException,IOException {
        //String[] sentences;
        // always start with a model, a model is learned from training data
        InputStream is;
        if ("eng".equals(sLanguage)) {
            if (MbFXWords.bRunResourcesFromHome)
                is = new FileInputStream("resources/en-sent.bin");
            else
                is = new FileInputStream(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/en-sent.bin");                
        }    
        else if ("deu".equals(sLanguage)) {
            if (MbFXWords.bRunResourcesFromHome)
                is = new FileInputStream("resources/de-sent.bin");
            else
                is = new FileInputStream(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/de-sent.bin"); 
        }
        else { //if ("fra".equals(sLanguage))
            if (MbFXWords.bRunResourcesFromHome)
                is = new FileInputStream("resources/fr-sent.bin");
            else
                is = new FileInputStream(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/fr-sent.bin"); 
        }
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);
        String[] sentences1 = sdetector.sentDetect(sText);
        return sentences1;
    }
    /**
     * Divides a main clause in subclauses.
     * Assures minimum length of a subclause to be 4 words.
     * @param sentencesInput main clauses
     * @return subclauses
     */
    public static String[] Segmentation(String[] sentencesInput){
        //sentences = new String[sentences1.length*50];//*5];
        List<String> sentences = new ArrayList<>();
        //Segmentation in subclauses:
        String[] sentences2; //= new String[10];
        String[] sentences3; //= new String[100];
        int a=0;
        Boolean b = false;
        for (String sentence : sentencesInput) {
            System.out.println("[" + sentence + "]");
            if ("eng".equals(sLanguage))
                //sentences2 = sentence.split(", | - |\n");
                //
                //If three words are following, split at comma,
                //hyphen and the words 'whether' or 'if',
                //in any case split at '\n':
                sentences2 = sentence.split(""
                        + ", (?=\\S+\\s+\\S+\\s+\\S+)|"
                        + " - (?=\\S+\\s+\\S+\\s+\\S+)|"
                        + "\n|"
                        + "(?= whether \\S+\\s+\\S+\\s+\\S+)|"
                        + "(?= if \\S+\\s+\\S+\\s+\\S+)");
            else
                sentences2 = sentence.split(""
                        + ", (?=\\S+\\s+\\S+\\s+\\S+)|"
                        + " - (?=\\S+\\s+\\S+\\s+\\S+)|"
                        + "\n");
            for (String s2 : sentences2) {
                s2=s2.trim();
                //Assure minimum length of a subclause to be 4 words:
                if (!"".equals(s2)) {// s2 != "" doesn't do here,
                    // for it's the same immutable String "" every time
                    sentences3 = s2.split(" ");
                    if (sentences3.length < 4 && !b) {
                        //sentences[a] = s2;
                        sentences.add(s2);
                        b= true;
                    } else if (b) {
                        //sentences[a] = sentences[a] + ", " + s2;
                        String s = sentences.get(a);
                        sentences.remove(a);
                        sentences.add(s + ", " + s2);
                        //String sTemp[]=sentences[a].split(" ");
                        String sTemp[]=sentences.get(a).split(" ");
                        if (sTemp.length >= 4) { 
                            b=false; a++;
                        }
                    } else {
                        //sentences[a] = s2;
                         sentences.add(s2);
                        //System.out.println(sentences[a]);
                        a++;
                    }
                }
            }
            if (b) {a++; b = false; /*System.out.println(sentences[a-1]);*/}
        }
        /*
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
        */
        return sentences.toArray(new String[sentences.size()]);
    }
    /**
     * Detects language of text in String
     * {@link OccurrenceOfWords#sInput}
     * @return language code abbreviation
     */
    public static String sLanguageDetection() {
        //String inputText = "Hallo Mike, das ist Marc. Ein Auto hat 2 Lichter.";
        LanguageDetectorModel m;
        if (MbFXWords.bRunResourcesFromHome)
            m = new LanguageDetectorModelLoader().load(new File("resources/langdetect-183.bin"));
        else
            m = new LanguageDetectorModelLoader().load(new File(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/langdetect-183.bin"));
        LanguageDetector myCategorizer = new LanguageDetectorME(m);
        //Some file contents begin with format strings:
        String sInputSelection;
        if (sInput.length()>50) {
            sInputSelection=sInput.substring(25);
        } else {
            sInputSelection=sInput;
        }
        if (sInputSelection.length()>40*60*10) { //ca. 10 pages
            sInputSelection=sInputSelection.substring(0,40*60*10);
        }
        // Get the most probable language:
        Language bestLanguage = myCategorizer.predictLanguage(sInputSelection);
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
            case "PDT": sTemp="predeterminer"; break;
            case "POS": sTemp="possessive"; break;
            case "PRP": sTemp="pronoun"; break;
            case "PRP$": sTemp="pronoun"; break;
            case "WP": sTemp="pronoun"; break;
            case "WP$": sTemp="pronoun"; break;
            case "RP": sTemp="particle"; break; 
            case "SYM": sTemp="symbol"; break;
            case "TO": sTemp="to"; break;
            case "UH": sTemp="interjection"; break;
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
        switch (sEntry) { //no particle for French                               
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
            case "$": sTemp="verb";break;
            case "$.": sTemp="verb";break;
            case "ADJA": sTemp="adjective"; break;
            case "ADJD": sTemp="DEPrädikativumAdverb"; break;
            case "ADV": sTemp="adverb"; break;
            case "APPR": sTemp="preposition"; break;
            case "APPRART": sTemp="preposition"; break;
            case "APPO": sTemp="preposition"; break;
            case "APZR": sTemp="preposition"; break;
            case "ART": sTemp="article"; break; //"determiner"
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
            case "PIAT": sTemp="numeral"; break; //"determiner"
            case "PIS": sTemp="pronoun"; break; //"determiner"
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
     * Detects sentences and does segmentation
     * in subclauses.
     * @param sText input text
     * @param tagger from OpenNLP
     * @return sentences or subclauses respectively
     */
    public static String[] SentenceAndSegmentationDetect(String sText, POSTaggerME tagger){
        String[] sTempSentences=null;
        try {
            sTempSentences = OpenNLPMainSentenceDetect(sText);
        } catch (IOException e){
            System.err.println("Caught MbFXWords IOException: " + e.getStackTrace());
        }        
        String[] sentences2 = Segmentation(sTempSentences);
        String[] sentences3 = SentenceCorrectTooLong(sentences2, tagger);
        return sentences3;
    }
    /**
     * Improve division in words of OpenNLP
     * to get a readable plain text word in difficult cases.
     * @param word single OpenNLP word to be postprocessed
     * @return postprocessed word
     */
    public static String wordPostProcessing (String word) {
        //word=word.replace("%20", " ");
        /*
        final String regExSpecialChars = "<([{\\^-=$!|]})?*+.,;:”>";
        final String regExSpecialCharsRE = regExSpecialChars.replaceAll( ".", "\\\\$0");
        final Pattern reCharsREP = Pattern.compile( "[" + regExSpecialCharsRE + "]");
        */
        //word.substring(1,word.length()-1).matches(word);
        if (",".equals(word.substring(word.length()-1))||
                ":".equals(word.substring(word.length()-1))||
                ")".equals(word.substring(word.length()-1))||
                "]".equals(word.substring(word.length()-1))||
                "\"".equals(word.substring(word.length()-1))||
                "”".equals(word.substring(word.length()-1))||
                "“".equals(word.substring(word.length()-1))||
                "?".equals(word.substring(word.length()-1))||
                "#".equals(word.substring(word.length()-1))||
                "-".equals(word.substring(word.length()-1))||
                "_".equals(word.substring(word.length()-1))||
                "•".equals(word.substring(word.length()-1))||
                "=".equals(word.substring(word.length()-1))||
                "".equals(word.substring(word.length()-1))||
                "~".equals(word.substring(word.length()-1))||
                "+".equals(word.substring(word.length()-1))||
                ";".equals(word.substring(word.length()-1)))
            word = word.substring(0, word.length()-1);
        if (!"".equals(word) && (",".equals(word.substring(0,1)))||
                (!"".equals(word) && ":".equals(word.substring(0,1)))||
                (!"".equals(word) && "(".equals(word.substring(0,1)))||
                (!"".equals(word) && "[".equals(word.substring(0,1)))||
                (!"".equals(word) && "\"".equals(word.substring(0,1)))||
                (!"".equals(word) && "“".equals(word.substring(0,1)))||
                (!"".equals(word) && "”".equals(word.substring(0,1)))||
                (!"".equals(word) && "?".equals(word.substring(0,1)))||
                (!"".equals(word) && "#".equals(word.substring(0,1)))||
                (!"".equals(word) && "-".equals(word.substring(0,1)))||
                (!"".equals(word) && "_".equals(word.substring(0,1)))||
                (!"".equals(word) && "•".equals(word.substring(0,1)))||
                (!"".equals(word) && "=".equals(word.substring(0,1)))||
                (!"".equals(word) && "".equals(word.substring(0,1)))||
                (!"".equals(word) && "~".equals(word.substring(0,1)))||
                (!"".equals(word) && "+".equals(word.substring(0,1)))||
                (!"".equals(word) && ";".equals(word.substring(0,1))))
            word = word.substring(1, word.length());
        boolean isUpperCaseInWord = false;
        Character cTemp;
        for (int a=0; a < word.length()-1;a++) {
            cTemp = word.charAt(a);
            if (Character.isUpperCase(cTemp) && a!=0) //beginning with uppercase is ok
                isUpperCaseInWord = true;
        }
        if (!"".equals(word) && ".".equals(word.substring(word.length()-1))
                && !word.substring(0,word.length()-1).contains(".")
                && !isUpperCaseInWord
                )
            word = word.substring(0, word.length()-1);
        // If still one of these [,:()[]"?”“#-_•=~+;] is included in a
        // word, return empty string instead. So ignore the word,
        // because it is not plain text:
        
        String pattern = "[\\,\\:\\(\\)\\[\\]\\\"\\?\\”\\“\\#\\-\\_\\•\\=\\\\~\\+\\;]";       
        Pattern p = Pattern.compile(pattern);        
        Matcher m = p.matcher(word);
        if (m.find()) word="";
            
        return word;
    }
    /**
     * Part-of-speech Tagging invoked according to OpenNLP.
     * @param file disk txt-file to be analized
     * @throws IOException to caller
     * @throws InterruptedException if Task on click
     * {@link FXMLDocumentController#BtAnalyze}
     * shell be terminated.
     */
    public static void POSTag(String file) throws IOException, InterruptedException {
        //sArray = new String[1][][];
        
        if (MbFXWords.bAutoLanguage) sLanguage = OpenNLP.sLanguageDetection();
        if (!"eng".equals(sLanguage) && !"deu".equals(sLanguage) && !"fra".equals(sLanguage)) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message of mbFXWords");
                    alert.setHeaderText("The language used seems not supported.");
                    alert.setContentText("You can use English, French or German so far.\n"
                            + "Select under settings if one of these is appropriate.");
                    alert.showAndWait();                                     
                }
            });
            sLanguage = "wrong";
            sArray = new String[1][][];   
            return;
        }
        POSModel model;
        if ("eng".equals(sLanguage)) {
            if (MbFXWords.bRunResourcesFromHome)
                model = new POSModelLoader().load(new File("resources/en-pos-maxent.bin"));
            else
                model = new POSModelLoader().load(new File(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/en-pos-maxent.bin"));
        }        
        else if ("deu".equals(sLanguage)) {
            if (MbFXWords.bRunResourcesFromHome)
                model = new POSModelLoader().load(new File("resources/de-pos-maxent.bin"));
            else
                model = new POSModelLoader().load(new File(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/de-pos-maxent.bin"));
        }        
        else { //if ("fra".equals(sLanguage))
            if (MbFXWords.bRunResourcesFromHome)
                model = new POSModelLoader().load(new File("resources/fr-pos-maxent.bin"));
            else
                model = new POSModelLoader().load(new File(MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/fr-pos-maxent.bin"));
        }        
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
        Progress(.2);
        //System.out.println(model.toString());
        //PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
	tagger = new POSTaggerME(model);
 
        /*
	//String input = "Hi. How are you? This is Mike.";
        Charset charset = Charset.forName("UTF-8");
        //InputStreamFactory fa = InputStreamFactory(input);
        InputStreamFactory fa = new MarkableFileInputStreamFactory(
        new File(file));
        ObjectStream<String> lineStream = new PlainTextByLineStream(
            fa,charset);
	//new StringReader(input));
        */
                        
        //perfMon.start();
	                
        //String line;
        sSentences=SentenceAndSegmentationDetect(sInput, tagger);
        int iAllSentences = sSentences.length;//counter of all sentences
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
        Progress(.3);
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
        sSPO = new String[sArray.length][3];//[1000][3]
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
                Words[c] = wordPostProcessing(Words[c]);
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
            CharSequence sTemp="Subject";
            //Boolean bPronoun=false;
            //for ( String word : sample.getSentence() ) {
            sSPO[b][SUBJECT]="";
            sSPO[b][OBJECT]="";
            sSPO[b][PREDICATE]="";
            
            int a=0;
            for (String[] sIterator : sArray[b]){
                if ("noun".equals(sIterator[WORD_TYPE])
                        /*||"pronoun".equals(sIterator[WORD_TYPE])*/){
                    if ("Subject".contains(sTemp)) {
                        System.out.println("Subject: " + Words[a]);
                        sSPO[b][SUBJECT]=Words[a];
                    }
                    if ("Object".contains(sTemp)) {
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
                    //sTemp="Object";
                }
                if (!"".equals(sSPO[b][PREDICATE])) sTemp="Object";
                a++;//word counter
            }
            //sTemp="";
            System.out.println();
            b++;//sentence counter
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            Progress(.3+((double)b/sSentences.length)*.1);
        }
    }
}
