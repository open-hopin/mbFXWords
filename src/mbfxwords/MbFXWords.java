/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

//import java.io.File;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javafx.application.Application;
//import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
//import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import javafx.scene.text.Text;
//import mbfxwords.FXMLDocumentController.*;

/**
 * Constitutes the value object of a collection 
 * with the members
 * frequency of occurrence of words and
 * sequence of occuring words.
 * @author MB
 */
class mapValue {
   /**
    * Frequency of occurrence of words.
    */ 
   Integer occurrences;
   /**
    * Sequence of occuring words.
    */
   Integer sequence;
}

/**
 * Class corresponding to package mbfxwords,
 * application class.
 * @author MB
 */
public class MbFXWords extends Application {
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
     * @return the Stage from FXMLAbout.fxml.
     */
    public Stage getStageAbout() {
        if(stageAbout == null) {
            //this.stageAbout = (Stage) sceneAbout.getWindow();
            try {
                rootAbout = FXMLLoader.load(getClass().getResource("FXMLAbout.fxml"));
            }
            catch (IOException e) {
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
        getStageAbout().getProperties().put("hostServices", this.getHostServices());
        
        gStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        try { 
            stage.getIcons().add(new Image("file:FlatXoreo.png"));
            stageAbout.getIcons().add(new Image("file:FlatXoreo.png"));
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
        stage.setTitle("mbFXWords - find subject, predicate, object");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);       
    }
    
    /**
     * Analyzes the text of a txt-file
     * in respect to subject, predicate and object
     * and frequency of occurrence of words. 
     */
    public static void Analyze(){
        String[] file = new String[]{"Bitcoin.txt"};
        String file2;
        
        if (f != null) {
            file = new String[]{f.getPath()};            
        }
        file2 = file[0];
        
        try {            
            OccurrenceOfWords.main(file);
            OpenNLP.POSTag(file2);            
        } catch (IOException e){
            System.err.println("Caught MbFXWords IOException: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
            System.err.println(Arrays.toString(e.getStackTrace()));
        } catch (Exception e){
            System.err.println("Caught MbFXWords Exception: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
