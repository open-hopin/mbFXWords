/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;
//import java.awt.Desktop;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.HostServices;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
//import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
//import javafx.scene.control.TooltipBuilder;
//import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static javafx.scene.text.FontPosture.ITALIC;
import static javafx.scene.text.FontPosture.REGULAR;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;
//import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mbfxwords.test.Test;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
//import static mbfxwords.MbFXWords.c;
//import javafx.stage.Stage;
//import opennlp.tools.util.InvalidFormatException;
//import mbfxwords.OpenNLP;

/**
 * Controls FXMLDocument.fxml.
 * @author MB
 */
public class FXMLDocumentController implements Initializable {
    /**
     * Show manual pdf.
     */
    @FXML
    public Button BtHelp;
    /**
     * Number of clauses to be displayed in grid.
     * Determined in {@link #spPagesSetRange(int)}. 
     */
    int iSpinnerSentencesMax = 0;
    /**
     * Significance value in ratio to iNumberOfPages
     * to determine if clause is included in result grid.
     * Calculated in {@link #handleBtAnalyzeAction(javafx.event.ActionEvent)}.
     */
    public int iSignificance = 0;
    /**
     * Spinner of clauses pages.
     */
    @FXML
    public Spinner<Integer> spPages;
    //public final Spinner<Integer> spPages = new Spinner<Integer>();
    /**
     * Label of most common words.
     */
    @FXML
    public Label lblWords;
    /**
     * Shape around most common words.
     */
    @FXML
    public Rectangle rctWords;
    /**
     * Progress on click BtAnalyze method
     * {@link MbFXWords#Analyze()}.
     */
    @FXML
    public ProgressBar pbMain;
    /**
     * Task on click BtAnalyze method
     * {@link MbFXWords#Analyze()}.
     */
    public Task<Void> taskAnalyze;
    /**
     * Analyze the text to be loaded
     * for statistics and diagonal reading.
     */
    @FXML
    public Button BtAnalyze;
    /**
     * Copy abstract plain text
     * to clipboard.
     */
    @FXML
    public Button btCopyAbstract;
    /**
     * rectangle around
     * summary of document's content
     */
    @FXML
    public Rectangle rctAbstract;
    /**
     * summary of document's content
     * filled after analyse
     */
    @FXML
    public Label lblAbstract;
    /**
     * static explanation
     */
    @FXML
    public Label lblSentencesCount;
    /**
     * static explanation
     */
    @FXML
    public Label lblCaptionStems;
    /**
     * Button for search for similar.
     */
    @FXML
    public Button BtSearch;
    /**
     * Button for clearance of table.
     */
    @FXML
    public Button BtClear;
    /**
     * Label for population percentage of table.
     */
    @FXML
    public Label lblPopulated;
    /**
     * GridPane for text analysis.
     */
    @FXML
    public GridPane grid;
    /**
     * ActionEvent of Button with fx:id="LkSFmbFXWords".
     * @param event ActionEvent
     */
    /**
     * Label to dislay number of word stems.
     */
    @FXML
    public Label lblStems;
    /**
     * ActionEvent of Button BtHelp.
     * @param event ActionEvent
     */
    @FXML
    private void handleBtHelp(ActionEvent event) {
        MbFXWords.bWin=System.getProperty("os.name").toLowerCase().contains("windows");
        String sDelimiter;
        if (MbFXWords.bWin) sDelimiter="\\"; else sDelimiter="/";
        
        HostServices hostServices = (HostServices)MbFXWords.gStage.getProperties().get("hostServices");
        hostServices.showDocument(fActiveJarPathWithoutFilename().toString()+sDelimiter+"mbFXWords-manual.pdf");         
    }
    /**
     * ActionEvent of Button with fx:id="btCopyAbstract".
     * @param event ActionEvent
     */
    @FXML
    private void handleBtCopyAbstract(ActionEvent event) {
        ClipboardContent content = new ClipboardContent();
        content.putString(lblAbstract.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }
    /**
     * ActionEvent of Button with fx:id="BtAbout".
     * @param event ActionEvent
     */
    @FXML
    private void handleBTAboutAction(ActionEvent event) {
            //stageAbout = MbFXWords.getStageAbout();        
            MbFXWords.stageAbout.setResizable(true);            
            MbFXWords.stageAbout.show();
            // Hide this current window (if this is what you want)
            //((Node)(event.getSource())).getScene().getWindow().hide();
    }
    /**
     * ActionEvent of Button with fx:id="BtSettings".
     * @param event ActionEvent
     */
    @FXML
    private void handleBtSettings(ActionEvent event) {
            //stageAbout = MbFXWords.getStageAbout();        
            MbFXWords.stageSettings.setResizable(false);            
            MbFXWords.stageSettings.show();
    }
    /**
     * ActionEvent of Button with fx:id="BtSearch".
     * @param event ActionEvent
     */
    @FXML
    private void handleBtSearchAction(ActionEvent event) {
        if (OpenNLP.sSPO==null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Message of mbFXWords");
                alert.setHeaderText("Please press Analyze first.");
                alert.setContentText("No text found.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResourceAsStream("FlatXoreo.png")));
                alert.showAndWait();
            return;
        }
        String s="";
        String [][] sArray = new String[3][3]; //[C|Si|E] [S|P|O]
        final int C = 0; //most common
        final int Si = 1; //significant
        final int E = 2; //extraordinary
        final int S = 0; //subject
        final int P = 1; //predicate
        final int O = 2; //object        
        sArray[C][S] = OpenNLP.sSPO[OccurrenceOfWords.iMostCommon][OpenNLP.SUBJECT]+"+";
        sArray[C][P] = OpenNLP.sSPO[OccurrenceOfWords.iMostCommon][OpenNLP.PREDICATE]+"+";
        sArray[C][O] = OpenNLP.sSPO[OccurrenceOfWords.iMostCommon][OpenNLP.OBJECT]+"+";
        sArray[Si][S] = OpenNLP.sSPO[OccurrenceOfWords.iSignificant][OpenNLP.SUBJECT]+"+";
        sArray[Si][P] = OpenNLP.sSPO[OccurrenceOfWords.iSignificant][OpenNLP.PREDICATE]+"+";
        sArray[Si][O] = OpenNLP.sSPO[OccurrenceOfWords.iSignificant][OpenNLP.OBJECT]+"+";
        sArray[E][S] = OpenNLP.sSPO[OccurrenceOfWords.iExtraordinary][OpenNLP.SUBJECT]+"+";
        sArray[E][P] = OpenNLP.sSPO[OccurrenceOfWords.iExtraordinary][OpenNLP.PREDICATE]+"+";
        sArray[E][O] = OpenNLP.sSPO[OccurrenceOfWords.iExtraordinary][OpenNLP.OBJECT]+"+";
        switch (OccurrenceOfWords.sGram) {
            case "SPO":break;
            case "SO":sArray[C][P]="";sArray[Si][P]="";sArray[E][P]="";break;
            case "subject (S)":sArray[C][P]="";sArray[Si][P]="";sArray[E][P]="";
                    sArray[C][O]="";sArray[Si][O]="";sArray[E][O]="";break;
            case "object (O)":sArray[C][P]="";sArray[Si][P]="";sArray[E][P]="";
                    sArray[C][S]="";sArray[Si][S]="";sArray[E][S]="";break;
            case "predicate (P)":sArray[C][O]="";sArray[Si][O]="";sArray[E][O]="";
                    sArray[C][S]="";sArray[Si][S]="";sArray[E][S]="";break;
        }        
        switch (OccurrenceOfWords.sPref) {                                
            case "most common (C)": s="http://www.bing.com/search?q="+
                sArray[C][S]+
                sArray[C][P]+
                sArray[C][O]+"filetype%3D"+OccurrenceOfWords.sFiletype;break;
            case "significant (S)": s="http://www.bing.com/search?q="+
                sArray[Si][S]+
                sArray[Si][P]+
                sArray[Si][O]+"filetype%3D"+OccurrenceOfWords.sFiletype;break;
            case "extraordinary (E)": s="http://www.bing.com/search?q="+
                sArray[E][S]+
                sArray[E][P]+
                sArray[E][O]+"filetype%3D"+OccurrenceOfWords.sFiletype;break;
            case "CSE": s="http://www.bing.com/search?q="+
                sArray[C][S]+
                sArray[C][P]+
                sArray[C][O]+
                sArray[Si][S]+
                sArray[Si][P]+
                sArray[Si][O]+
                sArray[E][S]+
                sArray[E][P]+
                sArray[E][O]+"filetype%3D"+OccurrenceOfWords.sFiletype;break;
            case "CS": s="http://www.bing.com/search?q="+
                sArray[C][S]+
                sArray[C][P]+
                sArray[C][O]+
                sArray[Si][S]+
                sArray[Si][P]+
                sArray[Si][O]+"filetype%3D"+OccurrenceOfWords.sFiletype;break;
            case "SE": s="http://www.bing.com/search?q="+
                sArray[Si][S]+
                sArray[Si][P]+
                sArray[Si][O]+
                sArray[E][S]+
                sArray[E][P]+
                sArray[E][O]+"filetype%3D"+OccurrenceOfWords.sFiletype;break;
        }        
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        s=s.replaceAll("\\++", "+");
        hostServices.showDocument(s);
        //lblAbstract.setText(s);
        Tooltip tpTemp = new Tooltip(s);
        tpTemp.setPrefWidth(300);
        tpTemp.setWrapText(true);
        lblAbstract.setTooltip(tpTemp);
    }
    /**
     * ActionEvent of Button with fx:id="BtClear".
     * @param event ActionEvent
     */
    @FXML
    private void handleBtClearAction(ActionEvent event) {
        //System.out.println("You clicked me!");
        //label.setText("Hello World!");
        //label2.setText("Hello User!");
        //OpenNLP.sLanguageDetection();
        
        /*try {
            OpenNLP.SentenceDetect();
        }
        catch (InvalidFormatException e) {
            System.err.println("Caught MbFXWords Exception: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
        catch (IOException e) {
            System.err.println("Caught MbFXWords Exception: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }*/
        
        grid.getChildren().clear();
        lblPopulated.setText("populated 0%");
        lblSentencesCount.setText("clauses 0");        
        lblStems.setText("0(0)0");
        lblAbstract.setText(" abstract");
        lblAbstract.setTooltip(null);        
        OpenNLP.Progress((double)0);
        lblWords.setText("most common words");
        lblWords.setTooltip(null);        
        pbMain.setVisible(false);
        rctWords.setVisible(true);
        //lblStems.setText("test");
        //Test.testParse("Nice is easy and bad is evil."); //from package mbfxwords.test
        //Test.testPDFBox();
        OpenNLP.sArray = null;
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0,0,0);
        spPages.setValueFactory(valueFactory);
        String sTemp="of 0 pages";
        Tooltip tp = new Tooltip(sTemp);
        spPages.setTooltip(tp);
    }
    /**
     * ActionEvent of Button with fx:id="BtAnalyse".
     * @param event ActionEvent 
     */
    @FXML
    private void handleBtAnalyzeAction(ActionEvent event) {
        //MbFXWords.setCursorWait(MbFXWords.gStage.getScene());
        //MbFXWords.gStage.getScene().setCursor(Cursor.WAIT);
        //MbFXWords.gStage.getScene().getCursor();
        //grid.getChildren().clear();
        if ((taskAnalyze==null) || !taskAnalyze.isRunning()) 
            handleBtClearAction(new ActionEvent());
        pbMain.setVisible(true);
        rctWords.setVisible(false);
        if ((taskAnalyze==null) || !taskAnalyze.isRunning()) {
            BtAnalyze.setText("Stop");
            MbFXWords.gStage.getScene().setCursor(Cursor.WAIT);
            //BtAnalyze.setCursor(Cursor.DEFAULT);
            taskAnalyze = new Task<Void>() {
                @Override
                public Void call() {
                    // long running task here...
                    try {
                        mbfxwords.MbFXWords.Analyze();
                    }
                    catch (InterruptedException ex) {
                        System.out.println("Exiting analyze task!");
                        MbFXWords.gStage.getScene().setCursor(Cursor.DEFAULT);
                        return null;
                    }
                    return null ;
                }
            };
            taskAnalyze.setOnSucceeded(e -> { //an interrupted task does not reach succeeded
                MbFXWords.gStage.getScene().setCursor(Cursor.DEFAULT);
                BtAnalyze.setText("Analyze");
                if ("wrongzulu88..".equals(OccurrenceOfWords.sInput)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message of mbFXWords");
                    alert.setHeaderText("Document is encrypted.");
                    alert.setContentText("Can't open content.");
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(this.getClass().getResourceAsStream("FlatXoreo.png")));
                    alert.showAndWait();
                }
                if (OpenNLP.sArray[0] != null) {
                    /*
                    grid.addRow(0, new Text("words"),
                                    new Text("subject"),
                                    new Text("predicate"),
                                    new Text("object"),
                                    new Text("sentence"));
                    */
                    //int imEffectiveStems=OpenNLP.iEffectiveStems();

                    OccurrenceOfWords.iMostCommon=OccurrenceOfWords.iSPOSearchMostCommon();
                    OccurrenceOfWords.iSignificant=OccurrenceOfWords.iSPOSearchSignificant();
                    OccurrenceOfWords.iExtraordinary=OccurrenceOfWords.iSPOSearchExtraordinary();
                    // Concatenate abstract of analyzed text:
                    String sTemp=""; String sTempC=""; String sTempS=""; String sTempE="";
                    if (OccurrenceOfWords.iSignificant == OccurrenceOfWords.iMostCommon) OccurrenceOfWords.iSignificant = -1;
                    if ((OccurrenceOfWords.iExtraordinary == OccurrenceOfWords.iMostCommon) || 
                            (OccurrenceOfWords.iExtraordinary == OccurrenceOfWords.iSignificant))
                        OccurrenceOfWords.iExtraordinary = -1;
                    //
                    if (OccurrenceOfWords.iMostCommon != -1) sTempC = OpenNLP.sSentences[OccurrenceOfWords.iMostCommon];
                    if (OccurrenceOfWords.iSignificant != -1) sTempS = OpenNLP.sSentences[OccurrenceOfWords.iSignificant];
                    if (OccurrenceOfWords.iExtraordinary != -1) sTempE = OpenNLP.sSentences[OccurrenceOfWords.iExtraordinary];
                    //
                    if (sTempC.length()>0) sTemp+=sTempC.substring(0,1).toUpperCase();
                    if (sTempC.length()>1) sTemp+=sTempC.substring(1,sTempC.length());
                    if (",".equals(sTemp.substring(sTemp.length() -1)) || ".".equals(sTemp.substring(sTemp.length() -1)))
                        sTemp=sTemp.substring(0,sTemp.length() -1);
                    if (!"".equals(sTempC)) sTemp += ". ";
                    //
                    if (sTempS.length()>0) sTemp+=sTempS.substring(0,1).toUpperCase();
                    if (sTempS.length()>1) sTemp+=sTempS.substring(1,sTempS.length());
                    if (",".equals(sTemp.substring(sTemp.length() -1)) || ".".equals(sTemp.substring(sTemp.length() -1)))
                        sTemp=sTemp.substring(0,sTemp.length() -1);
                    if (!"".equals(sTempS)) sTemp += ". ";
                    if (sTempE.length()>0) sTemp+=sTempE.substring(0,1).toUpperCase();
                    if (sTempE.length()>1) sTemp+=sTempE.substring(1,sTempE.length());
                    if (",".equals(sTemp.substring(sTemp.length() -1)) || ".".equals(sTemp.substring(sTemp.length() -1)))
                        sTemp=sTemp.substring(0,sTemp.length() -1);
                    if (!"".equals(sTempC)) sTemp += ". ";
                    if (" ".equals(sTemp.substring(sTemp.length()-1))) sTemp = sTemp.substring(0, sTemp.length()-1);
                    lblAbstract.setText(sTemp);
                    Tooltip tpTemp = new Tooltip(sTemp);
                    tpTemp.setPrefWidth(300);
                    tpTemp.setWrapText(true);
                    lblAbstract.setTooltip(tpTemp);

                    int iNumberOfPages = (int) Math.round(OccurrenceOfWords.sInput.length() / (40*60));
                    //int iSignificance = 0;                   
                    //iSignificance = (int) Math.round(0+(10*iNumberOfPages)/400);
                    switch((0 <= iNumberOfPages && iNumberOfPages <= 5 ) ? 5 :
                            (6 <= iNumberOfPages && iNumberOfPages <= 10 ) ? 10 :
                            (11 <= iNumberOfPages && iNumberOfPages <= 30 ) ? 30 :
                            (31 <= iNumberOfPages && iNumberOfPages <= 100 ) ? 100 :
                            (101 <= iNumberOfPages && iNumberOfPages <= 200 ) ? 200 :
                            (201 <= iNumberOfPages && iNumberOfPages <= 300 ) ? 300 :
                            (301 <= iNumberOfPages && iNumberOfPages <= 400 ) ? 400 :
                            (401 <= iNumberOfPages && iNumberOfPages <= 600) ? 600 : 601) {
                        case 5: iSignificance=2;break;
                        case 10: iSignificance=3;break;
                        case 30: iSignificance=4;break;
                        case 100: iSignificance=5;break;
                        case 200: iSignificance=6;break;
                        case 300: iSignificance=7;break;
                        case 400: iSignificance=8;break;
                        case 600: iSignificance=9;break;
                        case 601: iSignificance=10;break;
                    }
                    System.out.println("Significance: " + iSignificance);
                    //spPages = new Spinner<>(1,Math.ceil(OpenNLP.sArray.length/100),1);
                    //spPages.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
                    spPagesSetRange(iSignificance);
                    populate(iSignificance);

                    //MbFXWords.c.pbMain.setProgress(1);
                    OpenNLP.Progress((double)1);
                    //Determine most common words:
                    pbMain.setVisible(false);
                    rctWords.setVisible(true);
                    for (int a=0; a<20; a++) OccurrenceOfWords.sCommonWords[a]="";
                    for (int a=0; a<20; a++) OccurrenceOfWords.determineCommonWord();
                    String sWords="";
                    for (int a=0; a<20; a++) sWords+=OccurrenceOfWords.sCommonWords[a]+" ";
                    //Following label is always displayed with initial text 
                    //of fxml-file if covered by progress bar. Covering
                    //the progress bar both elements display correctly:
                    lblWords.setText(sWords.trim());
                    Tooltip tp = new Tooltip(sWords.trim());
                    tp.setPrefWidth(300);
                    tp.setWrapText(true);
                    lblWords.setTooltip(tp);                    
                    //addGridEvent();

                    int counter=0;
                    Integer rows=0;
                    /*
                    try {
                        Method method = grid.getClass().getDeclaredMethod("getNumberOfRows");
                        method.setAccessible(true);
                        //Results in wrong number if (MbFXWords.bOnlyPopulated):
                        rows = (Integer) method.invoke(grid);
                    }
                    catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                        System.err.println("Caught MbFXWords Exception: ");
                        ex.printStackTrace();
                    }
                    ObservableList<Node> observableList = grid.getChildren();
                    for (Node n:observableList) {
                        if (n instanceof Label) {
                            if (""!=((Label) n).getText()) counter++;
                        }
                    }
                    lblPopulated.setText("populated " + String.format("%.0f",((float)(counter-rows*2)/(rows*3))*100) + "%");
                    */
                    rows=OpenNLP.sArray.length;
                    for (int a = 0; a<mbfxwords.OpenNLP.sArray.length;a++) {
                        if (!"".equals(OpenNLP.sSPO[a][OpenNLP.SUBJECT])) counter++;
                        if (!"".equals(OpenNLP.sSPO[a][OpenNLP.PREDICATE])) counter++;
                        if (!"".equals(OpenNLP.sSPO[a][OpenNLP.OBJECT])) counter++;                        
                    }
                    lblPopulated.setText("populated " + String.format("%.0f",((float)(counter)/(rows*3))*100) + "%");
                    lblSentencesCount.setText("clauses " + Integer.toString(OpenNLP.sArray.length));
                    lblStems.setText(OccurrenceOfWords.mapStems.size()+"("+/*OpenNLP.iArrayNoOfWords()*/OccurrenceOfWords.map.size()+")"+OpenNLP.imEffectiveStems);
                }
            });
            new Thread(taskAnalyze).start();
        }
        else {
            taskAnalyze.cancel(true);
            BtAnalyze.setText("Analyze after Stop");
        }       
        //MbFXWords.gStage.getScene().setCursor(Cursor.DEFAULT);
        //MbFXWords.setCursorNormal(MbFXWords.gStage.getScene());
    }
    /**
     * To set the actual range of values possible for spPages.
     * @param imSignificance significance value in ratio to iNumberOfPages
     * to determine if clause is included in result grid
     */
    public void spPagesSetRange(int imSignificance) {
        if (OpenNLP.sArray/*[0]*/ != null) {    
            int b = 0;
            for (int a = 0; a < OpenNLP.sArray.length; a++) {
                if (!MbFXWords.bOnlySignificant ||
                        OccurrenceOfWords.bSPOSignificant(a, imSignificance)) {
                    if (!MbFXWords.bOnlyPopulated || (
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.SUBJECT])) &&
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.PREDICATE])) &&
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.OBJECT])))) { 
                        b++;
                    }
                }
            }
            iSpinnerSentencesMax=b;
            SpinnerValueFactory<Integer> valueFactory = //
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1,(int)Math.ceil(b/100)+1, 1);
            spPages.setValueFactory(valueFactory);
            String sTemp="of "+(int)(Math.ceil(b/100)+1)+" pages";
            Tooltip tp = new Tooltip(sTemp);
            spPages.setTooltip(tp); 
        }
    }
    /**
     * To populate result grid after analyze task
     * according to pagination and use of Spinner spPages.
     * @param imSignificance significance value in ratio to iNumberOfPages
     * to determine if clause is included in result grid
     */
    public void populate(int imSignificance) {
        //System.out.println("hello");
        /*
        Text tWords = new Text("words");
        tWords.setStyle("-fx-font-weight: bold;");
        */
        if (OpenNLP.sArray/*[0]*/ != null) {
            //spPagesSetRange(iSignificance);
            grid.getChildren().clear();
            int iBegin = (Integer.valueOf(spPages.getValue().toString())-1)*100;
            int iEnd =iBegin+99;
            if (iEnd > iSpinnerSentencesMax/*OpenNLP.sArray.length*/) iEnd = iSpinnerSentencesMax/*OpenNLP.sArray.length*/;
            //for (int a = 0; a<mbfxwords.OpenNLP.sArray.length;a++) {
            int b=0;
            int c=0;
            int d=0;
            int e=0;
            for (int a=0; a<OpenNLP.sArray.length; a++) {
                if (!MbFXWords.bOnlySignificant ||
                        OccurrenceOfWords.bSPOSignificant(a, imSignificance)) {
                    if (!MbFXWords.bOnlyPopulated || (
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.SUBJECT])) &&
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.PREDICATE])) &&
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.OBJECT])))) { 
                        if (d==iBegin) {b=a;}
                        if (e==iEnd) {c=a+1; break;}
                        d++;
                        e++;
                    }
                }    
            }
            if (c==0) c=OpenNLP.sArray.length; 
            for (int a = b; a < c; a++) {
                if (!MbFXWords.bOnlySignificant ||
                        OccurrenceOfWords.bSPOSignificant(a, imSignificance)) {
                    if (!MbFXWords.bOnlyPopulated || (
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.SUBJECT])) &&
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.PREDICATE])) &&
                            (!"".equals(OpenNLP.sSPO[a][OpenNLP.OBJECT])))) {   
                        Label lblButton = new Label(String.valueOf(a+1));
                        //lblButton.getStyleClass().add("background-label");
                        lblButton.setStyle(" -fx-background-color:#D0D0D0; -fx-background-radius:4; ");
                        if (a==OccurrenceOfWords.iMostCommon) {
                            lblButton = new Label(String.valueOf(a+1)+"C");
                            Font fValue=lblButton.getFont();                        
                            lblButton.setFont(Font.font ("SansSerif",NORMAL,ITALIC,fValue.getSize()));
                        }
                        if (a==OccurrenceOfWords.iExtraordinary) {
                            lblButton = new Label(String.valueOf(a+1)+"E");
                            Font fValue=lblButton.getFont();                        
                            lblButton.setFont(Font.font ("SansSerif",NORMAL,ITALIC,fValue.getSize()));
                        }
                        if (a==OccurrenceOfWords.iSignificant) {
                            lblButton = new Label(String.valueOf(a+1)+"S");
                            Font fValue=lblButton.getFont();                        
                            lblButton.setFont(Font.font ("SansSerif",NORMAL,ITALIC,fValue.getSize()));
                        }
                        Label labelSubject = new Label(mbfxwords.OpenNLP.sSPO[a][mbfxwords.OpenNLP.SUBJECT]);
                        Label labelPredicate = new Label(mbfxwords.OpenNLP.sSPO[a][mbfxwords.OpenNLP.PREDICATE]);
                        Label labelObject = new Label(mbfxwords.OpenNLP.sSPO[a][mbfxwords.OpenNLP.OBJECT]);
                        labelSubject.setWrapText(true);
                        labelPredicate.setWrapText(true);
                        labelObject.setWrapText(true);
                        grid.addRow(a, new Label(String.valueOf(mbfxwords.OpenNLP.sArray[a].length)),
                                    labelSubject,
                                    labelPredicate,
                                    labelObject,
                                    lblButton);
                    }
                }
            }
            addGridEvent();
        }
    }
    /**
     * Path for related jar-file, i.e. java program
     * currently executed, is determined and stored
     * in a new file object. This path does not
     * include the jar file name. Works correctly under
     * Windows and Linux. Path can contain spaces.
     * @return new file object with working program's path
     */
    public File fActiveJarPathWithoutFilename() {
        String sMainJar = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            
        sMainJar=sMainJar.substring(0,sMainJar.length()-14);//minus "/mbFXWords.jar"
        //sMainJar=Paths.get(sMainJar).toAbsolutePath().normalize().toString();
        //sMainJar=sMainJar.replace("%20", "\\ ");
        sMainJar=sMainJar.replace("%20", " ");
        //URL url;
        //URI uri;
        
        return new File(sMainJar);
    }
    /**
     * ActionEvent of Button with fx:id="BtChoose".
     * Internal remark:
     * Spaces in path or file names are unusual
     * in Linux but can be processed correctly
     * by this method. They are shown as escaped
     * under Linux. In this method escaping them is
     * not necessary.
     * @param event ActionEvent 
     */
    @FXML
    private void handleBtChooseAction(ActionEvent event) {
        //File path;
        
        FileChooser fc = new FileChooser();
        fc.setTitle("mbFXWords File Chooser");
        FileChooser.ExtensionFilter extFilterPDF =
            new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter extFilterTXT =
            new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        //FileChooser.ExtensionFilter extFilterAll =
        //    new FileChooser.ExtensionFilter("all files (*.*)", "*.*");
        fc.getExtensionFilters().add(extFilterPDF);
        fc.getExtensionFilters().add(extFilterTXT);
        if (MbFXWords.gFileForPathOnly == null){
            
            MbFXWords.gFileForPathOnly = fActiveJarPathWithoutFilename(); //("."/*/resources"*/);//.getCanonicalPath();    
            //try {
            //    uri = MbFXWords.gFileForPathOnly.toURI();
            //    url = uri.toURL();
            //} catch (Exception e) {
            //    System.err.println("Caught MbFXWords Exception: ");
            //    e.printStackTrace();
            //}            
        }
        fc.setInitialDirectory(MbFXWords.gFileForPathOnly);
        File fTemp = fc.showOpenDialog(MbFXWords.gStage);
        if (fTemp != null) { //Cancel of dialog means no change.
            MbFXWords.f=fTemp;
            try {
                MbFXWords.gFileForPathOnly = new File(MbFXWords.f.getAbsoluteFile().getParentFile().getAbsolutePath());
            }   catch (SecurityException e){
                System.err.println("Caught FXMLDocumentController SecurityException: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }   catch (Exception e){
                System.err.println("Caught FXMLDocumentController Exception: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
            MbFXWords.s.lblFile.setText(MbFXWords.f.getPath());
            MbFXWords.s.lblFile.setTooltip(new Tooltip(MbFXWords.f.getPath()));
            System.out.println(MbFXWords.f);            
        }
    }
    /*
    //The following does not function, because colIndex and rowIndex
    //both are null, because source is the GridPane itself and not its children.
    @FXML
    private void handleOnMouseEntered(MouseEvent e) {
        Node source = (Node)e.getSource() ;
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        System.out.printf("Mouse entered cell [%d, %d]%n", colIndex.intValue(), rowIndex.intValue());        
    }
    */    
    /**
     * Initialization of FXMLDocument.fxml.
     * Used to add listener of Spinner spPages.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //rctAbstract.setStroke(Color.BLACK);
        //Shape sh=new Shape();
        //Rectangle rc = new Rectangle();
        //rc.setStroke(Color.BLACK);
        
        // When spinner change value.
        spPages.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            //@SuppressWarnings("unchecked") 
            public void changed(ObservableValue<? extends Integer> observable,//
                    Integer oldValue, Integer newValue) {
                 
                if (oldValue!=0 && newValue<=(int)Math.ceil(iSpinnerSentencesMax/100)+1) populate(iSignificance);
            }
        });
        
    }
    /**
     * Called in {@link FXMLDocumentController#handleBtAnalyzeAction}.
     * For every click in a cell in the subject, predicate, object grid
     * the method shows a dialog box with the count of occurrences
     * of the respective word in the underlying text.
     * A click in the sentence count label on the right of the grid
     * displays the sentence.
     */    
    private void addGridEvent() {
        System.out.println("Number of children of grid: " + grid.getChildren().size());
        grid.getChildren().forEach(item -> {
            item.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 1) {
                    Node source = (Node)event.getSource() ;
                    Integer colIndex = GridPane.getColumnIndex(source);
                    Integer rowIndex = GridPane.getRowIndex(source);
                    if (colIndex != 0 && colIndex != 4) {
                        String sStem="";
                        String sWord="";
                        String sTemp;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Message of mbFXWords");
                        alert.setHeaderText("Count of occurrences:\nstem (word) effective");
                        /*                        
                        sTemp += OccurrenceOfWords.map.get(mbfxwords.OpenNLP.sSPO[rowIndex][colIndex-1].toLowerCase()).occurrences;
                        sTemp += ")";
                        sTemp += "\n";
                        sTemp += "assumed word stem is: " + sStem +
                                " (" + OccurrenceOfWords.mapStems.get(sStem.toLowerCase()).occurrences +")"+
                                OccurrenceOfWords.mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
                        */
                        sWord = mbfxwords.OpenNLP.sSPO[rowIndex][colIndex-1];
                        /*
                        if (sWord.length()>4) {
                            sStem = sWord.substring(0, sWord.length()-2)+"..";
                        }
                        else {
                            sStem = sWord;
                        }
                        */
                        sStem=OccurrenceOfWords.sGetStem(sWord);
                        sTemp="for word: "+sWord+" and stem: "+sStem+"\n";
                        sTemp+=OccurrenceOfWords.mapStems.get(sStem.toLowerCase()).occurrences;
                        sTemp+=" ("+OccurrenceOfWords.map.get(mbfxwords.OpenNLP.sSPO[rowIndex][colIndex-1].toLowerCase()).occurrences+") ";
                        sTemp+=OccurrenceOfWords.mapStems.get(sStem.toLowerCase()).effectiveOccurrences;
                        alert.setContentText(sTemp);
                        //alert.setResizable(true);
                        //Adjust size automatically to ContentText:
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("FlatXoreo.png")));
                        //stage.getIcons().add(new Image("file:resources/FlatXoreo.png"));
                        alert.showAndWait();
                    }
                    if (colIndex == 4) {
                        /*
                        String sTempA = ((Button)(ee.getSource())).getText(); //sTemp is used in static context and therefore a variable is needed.
                        if (("C".equals(sTempA.substring(sTempA.length()-1))) ||
                                ("E".equals(sTempA.substring(sTempA.length()-1)))||
                                ("S".equals(sTempA.substring(sTempA.length()-1))))
                            sTempA=sTempA.substring(0,sTempA.length()-1);
                        */
                        String sTempB;
                        System.out.println("Button: "+((Integer)(rowIndex+1)).toString());
                        /*
                        for (String[] s: mbfxwords.OpenNLP.sArray[Integer.valueOf(sTempA)-1]) {
                            sTempB += s[mbfxwords.OpenNLP.WORD] + " ";
                        }
                        */
                        sTempB = OpenNLP.sSentences[rowIndex/*Integer.valueOf(sTempA)-1*/];
                        //String sTempC = sTempB; //.trim();

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Message of mbFXWords");
                        alert.setHeaderText("The underlying sentence is:");
                        //alert.setContentText(mbfxwords.OpenNLP.sArray[Integer.valueOf(sTempA)-1][0][mbfxwords.OpenNLP.WORD]);

                        Text text = new Text(sTempB);
                        text.setWrappingWidth(300);
                        alert.getDialogPane().setContent(text);

                        //alert.setContentText(sTempC);
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("FlatXoreo.png")));
                        //stage.getIcons().add(new Image("file:resources/FlatXoreo.png"));
                        alert.showAndWait();
                    }
                }
            });
        });
    }
}
