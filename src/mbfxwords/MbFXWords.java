/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

//import java.io.File;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import javafx.application.Application;
//import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
//import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
//import javafx.scene.text.Text;
//import mbfxwords.FXMLDocumentController.*;




/**
 * Class corresponding to package mbfxwords,
 * application class.
 * @author MB
 */
public class MbFXWords extends Application {
    /**
     * Indicates to show only completely populated
     * clauses (with SPO) (true) or to show
     * all clauses (false) in main window.
     */
    public static Boolean bOnlyPopulated = true;
    /**
     * Indicates to show only significant
     * clauses (true) or to show
     * all clauses (false) in main window.
     * The extend of significance is automatically
     * adjusted according to text length.
     */
    public static Boolean bOnlySignificant = false;
    /**
     * True determines automatic language
     * detection on each analyze resulting in
     * value for {@link OpenNLP#sLanguage}.
     * False means use old value of {@link OpenNLP#sLanguage}
     * as selected under settings.
     */
    public static Boolean bAutoLanguage = true;
    /**
     * True for Windows, false for Linux,
     * determined in {@link MbFXWords#Analyze()}
     * and {@link FXMLDocumentController#BtHelp}.
     */
    public static Boolean bWin;
    /**
     * Search for resources directory in
     * Linux user home (true) or
     * search for resources directory in
     * application's jar path (false, the default).
     */
    public static Boolean bRunResourcesFromHome;
    /**
     * File object to store the path used currently by the application.
     */
    public static File gFileForPathOnly;
    /**
     * Stage of application.
     */
    public static Stage gStage;
    /**
     * Selected or default txt-file of application.
     */
    public static File f;
    /**
     * Stage from FXMLSettings.fxml.
     */
    public static Stage stageSettings;
    /**
     * Scene from FXMLSettings.fxml.
     */
    private static Scene sceneSettings;
    /**
     * Parent from FXMLSettings.fxml.
     */
    private static Parent rootSettings;
    /**
     * Stage from FXMLAbout.fxml.
     */
    public static Stage stageAbout;
    /**
     * Scene from FXMLAbout.fxml.
     */
    private static Scene sceneAbout;
    /**
     * Parent from FXMLAbout.fxml.
     */
    private static Parent rootAbout;
    /**
     * Instanced controller object
     * for main window.
     */
    public static FXMLDocumentController c;
    /**
     * Instanced controller object
     * for Settings window.
     */
    public static FXMLSettingsController s;
    /**
     * @return the Stage from FXMLAbout.fxml.
     */
    public Stage getStageSettings() {
        if(stageSettings == null) {
            //this.stageAbout = (Stage) sceneAbout.getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLSettings.fxml"));
                rootSettings = loader.load();
                s=loader.getController();                
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            stageSettings = new Stage();
            stageSettings.setTitle("Settings mbFXWords");
            sceneSettings = new Scene(rootSettings, 450, 450);
            stageSettings.setScene(sceneSettings);
        }
        
        MbFXWords.stageSettings.setOnHiding((WindowEvent value) -> { //setOnCloseRequest((WindowEvent value) -> {
            try {
                //FXMLSettingsController.class.getMethod("onStageClose").invoke(FXMLSettingsController.class);//does not work
                //s.onStageClose();//does not work after close                 
            }
            catch (Exception e) {
                System.err.println("Caught MbFXWords Exception: ");
                e.printStackTrace();
            }
        });
        return stageSettings;
    }
    /**
     * @return the Stage from FXMLAbout.fxml.
     */
    public Stage getStageAbout() {
        if(stageAbout == null) {
            //this.stageAbout = (Stage) sceneAbout.getWindow();
            try {
                rootAbout = FXMLLoader.load(getClass().getResource("FXMLAbout.fxml"));
            }
            catch (IOException e) {
                System.err.println("Caught MbFXWords Exception: ");
                e.printStackTrace();
            }
            stageAbout = new Stage();
            stageAbout.setTitle("About+Credits mbFXWords");
            sceneAbout = new Scene(rootAbout, 450, 450);
            stageAbout.setScene(sceneAbout);
        }
        return stageAbout;
    }
    /**
     * @param stage of application
     * @throws Exception of start
     */
    @Override
    public void start(Stage stage) throws Exception {
        gStage = stage;
        //Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();//Error
        c = loader.getController();
        c.rctWords.setVisible(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        asDetermineFile();
        stage.getProperties().put("hostServices", this.getHostServices());
        getStageAbout().getProperties().put("hostServices", this.getHostServices());
        getStageSettings().getProperties().put("hostServices", this.getHostServices());
        MbFXWords.c.lblPopulated.visibleProperty().setValue(false);
        MbFXWords.c.lblCaptionStems.visibleProperty().setValue(false);
        MbFXWords.c.lblSentencesCount.visibleProperty().setValue(false);
        MbFXWords.c.lblStems.visibleProperty().setValue(false);
        MbFXWords.s.lvGram.visibleProperty().setValue(false);
        MbFXWords.s.lvFiletype.visibleProperty().setValue(false);
        MbFXWords.s.lvPref.visibleProperty().setValue(false);
        s.onStatisticsCheckBox(new ActionEvent());        
        /* 
        // The following does find the model if it exists under the src
        // directory in package mbfxwords (no null pointer exception), but exception by OpenNLP states that
        // model does not exist, becauses there it cannot be extracted out of a jar:
        String p=this.getClass().getResource("langdetect-183.bin").getPath();
        System.out.println(p);
        m = new LanguageDetectorModelLoader().load(new File(p));
        */
        URL url = this.getClass().getResource("FXMLDocument.css");
        String css = url.toExternalForm(); 
        scene.getStylesheets().add(css);
        
        try {
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("FlatXoreo.png")));
            //stage.getIcons().add(new Image("file:resources/FlatXoreo.png"));
            stageAbout.getIcons().add(new Image(this.getClass().getResourceAsStream("FlatXoreo.png")));
            stageSettings.getIcons().add(new Image(this.getClass().getResourceAsStream("FlatXoreo.png")));
            //stageAbout.getIcons().add(new Image("file:resources/FlatXoreo.png"));
        } catch (Exception e){
            System.err.println("Caught MbFXWords Exception: " + e.toString());
        }
        scene.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
            //System.out.println("Width: " + newSceneWidth);
            //FXMLDocumentController.
        });
        scene.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) -> {
            //System.out.println("Height: " + newSceneHeight);
            
        });
        //stage.setMaximized(true);
        stage.setTitle("mbFXWords - analyze a text - diagonal read pdf's");
        stage.show();
    }

    /**
     * Starting point for application mbFXWords.
     * @param args the command line arguments:
     * 1. optional parameter: filename with relative or absolute path,
     * "ignore" to leave out, "help" for help
     * 2. optional parameter: {@link MbFXWords#bRunResourcesFromHome}
     * Internal remark:
     * Command line arguments used in NetBeans IDE are compiled
     * in some way with the jar, so delete them before final release,
     * otherwise args will not work in production environment.
     */
    public static void main(String[] args) {
        //if (args != null) {
            bRunResourcesFromHome=false;
            if (args.length > 2) {
                System.out.println("Use no parameter, 1 or 2 parameters, "
                        + "the first is filename including relative or absolute path. "
                        + "Parameter help for help.");
                System.exit(1);
            }
            else if (args.length > 0) {                
                if (!(args[0].endsWith("txt") || args[0].endsWith("pdf") || "ignore".equals(args[0]) || "help".equals(args[0].toLowerCase()))) {
                    System.out.println("Only txt or pdf files can be opened. Parameter help for help.");
                    System.exit(1);
                }
                else if (!"ignore".equals(args[0])){
                    f=new File(args[0]);
                }
            }
            if (args.length > 1) {
                if ("true".equals(args[1])) bRunResourcesFromHome=true;
                else if (!"false".equals(args[1])) {
                    System.out.println("Use true or false for second parameter. "
                        + "Parameter help for help.");
                    System.exit(1);
                }
            }
            if (args.length > 0) {
                if ("help".equals(args[0].toLowerCase())) {
                    System.out.println(
                        "java -jar mbFXWords.jar\n[file with absolute or relative path "
                                + "or ignore for no file]\n"
                                + "[true|false for bRunResourcesFromHome]");
                    System.exit(1);
                }
            }
        //}
        launch(args);       
    }
    /*
    public static void setCursorWait(final Scene scene) {
        Runnable r=new Runnable() {

            @Override
            public void run() {
                 scene.setCursor(Cursor.WAIT);
            }
        };
        Thread t=new Thread(r);
        t.start();
    }
    public static void setCursorNormal(final Scene scene)
    {
            Runnable r=new Runnable() {

            @Override
            public void run() {
                 scene.setCursor(Cursor.DEFAULT);
            }
        };
        Thread t=new Thread(r);
        t.start();
    }    
    */
    /**
     * Determines name of file to be loaded
     * for {@link #Analyze()}, that is default file or
     * file selected in file choose dialog.
     * @return name of file (array with one element)
     * to be loaded including relative or absolute path
     */
    public static String[] asDetermineFile() {
        bWin=System.getProperty("os.name").toLowerCase().contains("windows");
        String[] file;
        if (bWin)
            if (MbFXWords.bRunResourcesFromHome)
                file = new String[]{"resources\\default.txt"};
            else
                file = new String[]{MbFXWords.c.fActiveJarPathWithoutFilename() + "\\resources\\default.txt"};
        else //Linux
            if (MbFXWords.bRunResourcesFromHome)
                file = new String[]{"resources/default.txt"};
            else
                file = new String[]{MbFXWords.c.fActiveJarPathWithoutFilename() + "/resources/default.txt"};
        //String file2;
        if (f != null) {
            file = new String[]{f.getPath()};            
        } else f=new File(file[0]);
        //file2 = file[0];
        return file;
    }
    // @throws NoSuchMethodException grid reflection "getNumberOfRows"
    // @throws IllegalAccessException grid reflection "getNumberOfRows"
    // @throws InvocationTargetException grid reflection "getNumberOfRows"
    /**
     * Analyzes the text of a txt-file or pdf-file
     * in respect to subject, predicate and object
     * and frequency of occurrence of words.
     * @throws InterruptedException Task on click
     * {@link FXMLDocumentController#BtAnalyze}
     * shell be terminated.
     */
    public static void Analyze() throws InterruptedException {
        //gStage.getScene().setCursor(Cursor.WAIT);
        String[] asFileTemp;
        asFileTemp=asDetermineFile();
        try {
            //==================:
            OpenNLP.ReadIn(asFileTemp);
            OccurrenceOfWords.sInput=OpenNLP.textPostProcessing(OccurrenceOfWords.sInput);
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            //c.pbMain.setProgress(.1);
            OpenNLP.Progress(.1);
        } catch (FileNotFoundException e){
            System.out.println("File ("+asFileTemp[0]+ ") not found!");
            OpenNLP.sArray = new String[1][][];
            return;
        }
        catch (InterruptedException e) {throw e;}
        catch (Exception e){
            System.err.println("Caught MbFXWords Exception: ");
            e.printStackTrace();
        }
        try {            
            if (!"wrongzulu88..".equals(OccurrenceOfWords.sInput)) {
                //===================:
                OpenNLP.POSTag(asFileTemp[0]);
                if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                //c.pbMain.setProgress(.4);
                OpenNLP.Progress(.4);
                if (!"wrong".equals(OpenNLP.sLanguage)) {
                    //======================:
                    OccurrenceOfWords.main();
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    //c.pbMain.setProgress(.7);
                    OpenNLP.Progress(.7);
                    //======================:
                    if (OpenNLP.sArray[0] != null) OpenNLP.imEffectiveStems=OpenNLP.iEffectiveStems();
                } 
            } 
        } catch (IOException e){
            //System.err.println("Caught MbFXWords IOException: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
            //System.err.println(Arrays.toString(e.getStackTrace()));
            System.err.println("Caught MbFXWords IOException: ");
            e.printStackTrace();
        } 
        catch (InterruptedException e) {throw e;}
        catch (Exception e){
            //System.err.println("Caught MbFXWords Exception: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
            //System.err.println(Arrays.toString(e.getStackTrace()));
            //System.err.println("Caught MbFXWords Exception: " + e.getStackTrace());
            System.err.println("Caught MbFXWords Exception: ");
            e.printStackTrace();
        }
    }
}