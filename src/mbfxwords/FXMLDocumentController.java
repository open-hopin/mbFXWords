/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

//import java.awt.Desktop;
import java.io.File;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import javafx.stage.Stage;
//import opennlp.tools.util.InvalidFormatException;
//import mbfxwords.OpenNLP;

/**
 * Controls FXMLDocument.fxml.
 * @author MB
 */
public class FXMLDocumentController implements Initializable {
    
    /**
     * Label for Hello World.
     */
    @FXML
    private Label label;
    /**
     * Label for Hello User.
     */
    @FXML
    private Label label2;
    /**
     * GridPane for text analysis.
     */
    @FXML
    public GridPane grid;
    /**
     * ActionEvent of Button with fx:id="LkSFmbFXWords".
     * @param event ActionEvent
     */
    @FXML
    private void handleBTAboutAction(ActionEvent event) {
            //stageAbout = MbFXWords.getStageAbout();        
            MbFXWords.stageAbout.show();
            // Hide this current window (if this is what you want)
            //((Node)(event.getSource())).getScene().getWindow().hide();
    }
    /**
     * ActionEvent of Link.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkSFmbFXWords(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://sourceforge.net/projects/hopin/");
        /*
        String url = "http://google.com";
        if (Desktop.isDesktopSupported()) { // for windows
            try {
                Desktop.getDesktop().browse(new URI(url));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else { // for linux
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("/usr/bin/firefox -new-window " + url);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
    }
    /**
     * ActionEvent of Link.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkAPOpenNLP(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://opennlp.apache.org/");
    }
    /**
     * ActionEvent of Link.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkIFChamestudio(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://www.iconfinder.com/icons/2109139/data_gallery_google_image_photo_picture_services_icon");
    }
    /**
     * ActionEvent of Link.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkCC3Unported(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://creativecommons.org/licenses/by/3.0/");
    }
    /**
     * ActionEvent of Button with fx:id="button".
     * @param event ActionEvent
     */
    @FXML
    private void handleButtonAction(ActionEvent event) {
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
    }
    /**
     * ActionEvent of Button with fx:id="BtAnalyse".
     * @param event ActionEvent 
     */
    @FXML
    private void handleBtAnalyzeAction(ActionEvent event) {
        //System.out.println("You clicked analyze!");
        grid.getChildren().clear();
        mbfxwords.MbFXWords.Analyze();
                
        if (OpenNLP.sArray[0] != null) {
            for (int a = 0; a<mbfxwords.OpenNLP.sArray.length;a++) {
                Button bButton = new Button(String.valueOf(a+1));
                grid.addRow(a, new Text(String.valueOf(mbfxwords.OpenNLP.sArray[a].length)),
                            new Text(mbfxwords.OpenNLP.sSPO[a][mbfxwords.OpenNLP.SUBJECT]),
                            new Text(mbfxwords.OpenNLP.sSPO[a][mbfxwords.OpenNLP.PREDICATE]),
                            new Text(mbfxwords.OpenNLP.sSPO[a][mbfxwords.OpenNLP.OBJECT]),
                            bButton);
                bButton.setOnAction( e -> {
                    String sTempA = ((Button)(e.getSource())).getText(); //sTemp is used in static context and therefore a variable is needed.
                    String sTempB = "";

                    System.out.println("Button: "+((Button)(e.getSource())).getText());
                    
                    /*
                    for (String[] s: mbfxwords.OpenNLP.sArray[Integer.valueOf(sTempA)-1]) {
                        sTempB += s[mbfxwords.OpenNLP.WORD] + " ";
                    }
                    */
                    sTempB = OpenNLP.sSentences[Integer.valueOf(sTempA)-1];
                    String sTempC = sTempB; //.trim();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message of mbFXWords");
                    alert.setHeaderText("The underlying sentence is:");
                    //alert.setContentText(mbfxwords.OpenNLP.sArray[Integer.valueOf(sTempA)-1][0][mbfxwords.OpenNLP.WORD]);
                    alert.setContentText(sTempC);
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image("file:FlatXoreo.png"));
                    alert.showAndWait();
                });
            }
            addGridEvent();
        }
    }
    
    /**
     * ActionEvent of Button with fx:id="BtChoose".
     * @param event ActionEvent 
     */
    @FXML
    private void handleBtChooseAction(ActionEvent event) {
        //File path;
        
        FileChooser fc = new FileChooser();
        fc.setTitle("mbFXWords File Chooser");
        FileChooser.ExtensionFilter extFilter =
            new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fc.getExtensionFilters().add(extFilter);
        if (MbFXWords.gFileForPathOnly == null){
            MbFXWords.gFileForPathOnly = new File(".");//.getCanonicalPath();    
        }
        fc.setInitialDirectory(MbFXWords.gFileForPathOnly);
        MbFXWords.f = fc.showOpenDialog(MbFXWords.gStage);
        if (MbFXWords.f != null) { //Cancel of dialog means no change.
            try {
                MbFXWords.gFileForPathOnly = new File(MbFXWords.f.getAbsoluteFile().getParentFile().getAbsolutePath());
            }   catch (SecurityException e){
                System.err.println("Caught FXMLDocumentController SecurityException: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }   catch (Exception e){
                System.err.println("Caught FXMLDocumentController Exception: " + e.toString() + ". " + e.getStackTrace()[2].getClassName() + ": " + e.getStackTrace()[2].getLineNumber());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
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
     * Not used so far.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    /**
     * Called in {@link FXMLDocumentController#handleBtAnalyzeAction}.
     * For every click in a cell in the subject, predicate, object grid
     * the method shows a dialog box with the count of occurrences
     * of the respective word in the underlying text.
     */    
    private void addGridEvent() {
        System.out.println("Size: " + grid.getChildren().size());
        grid.getChildren().forEach(item -> {
            item.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 1) {
                    Node source = (Node)event.getSource() ;
                    Integer colIndex = GridPane.getColumnIndex(source);
                    Integer rowIndex = GridPane.getRowIndex(source);
                    if (colIndex != 0) {
                        String sTemp;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Message of mbFXWords");
                        alert.setHeaderText("The word (count of occurrences) is:");
                        sTemp = mbfxwords.OpenNLP.sSPO[rowIndex][colIndex-1];
                        sTemp += " (";
                        sTemp += OccurrenceOfWords.map.get(mbfxwords.OpenNLP.
                                sSPO[rowIndex][colIndex-1].toLowerCase()).occurrences;
                        sTemp += ")";
                        alert.setContentText(sTemp);
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        stage.getIcons().add(new Image("file:FlatXoreo.png"));
                        alert.showAndWait();
                    }
                }
            });

        });
    }
}
