/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author mb
 */
public class FXMLSettingsController implements Initializable {
    /**
     * Copy main text to clipboard.
     */
    @FXML
    public Button btClipboard;
    /**
     * Choices regarding Bing search:
     * Filetypes txt or pdf.
     */
    @FXML
    public ListView<String> lvFiletype;
    /**
     * Choices regarding Bing search:
     * SPO, SO, subject (S), predicate (P), object (O).
     */
    @FXML
    public ListView<String> lvGram;
    /**
     * Choices regarding Bing search:
     * CSE, CS, SE
     * most common (C), significant (S), extraordinary (E).
     */
    @FXML
    public ListView<String> lvPref;
    /**
     * Saving settings on close.
     * Called in MbFXWords.
     */
    /*
    public void onStageClose() {
        System.out.println("close");
    }    
    */
    /**
     * To select whether to show statistics
     * in main window and whether to show
     * advanced settings.
     */
    @FXML
    CheckBox cbStatistics;
    /**
     * To select whether to show
     * only completely populated clauses
     * in main window.
     */
    @FXML
    CheckBox cbOnlyPopulated;
    /**
     * To select whether to show
     * only significant clauses
     * in main window.
     */
    @FXML
    CheckBox cbOnlySignificant;    
    /**
     * To select whether to show all clauses
     * in main window or only completly populated.
     */
    @FXML
    CheckBox cbOnlyPoplated;
    /**
     * Label to show active filename
     * and path.
     */
    @FXML
    Label lblFile;
    /**
     * ToggleGroup for language
     * RadioButtons.
     */
    @FXML
    ToggleGroup tgLanguage;
    /**
     * referring to {@link tgLanguage}
     */
    @FXML
    RadioButton rbAuto;
    /**
     * referring to {@link tgLanguage}
     */
    @FXML
    RadioButton rbEnglish;
    /**
     * referring to {@link tgLanguage}
     */
    @FXML
    RadioButton rbFrench;
    /**
     * referring to {@link #tgLanguage}
     */
    @FXML
    RadioButton rbGerman;
    /**
     * Sets all statistics objects of main window
     * to invisible or visible.
     * @param event action
     */
    @FXML
    public void onStatisticsCheckBox(ActionEvent event) {
        Boolean bShown;
        bShown=MbFXWords.c.lblPopulated.visibleProperty().getValue();
        MbFXWords.c.lblPopulated.visibleProperty().setValue(!bShown);
        MbFXWords.c.lblCaptionStems.visibleProperty().setValue(!bShown);
        MbFXWords.c.lblSentencesCount.visibleProperty().setValue(!bShown);
        MbFXWords.c.lblStems.visibleProperty().setValue(!bShown);
        lvGram.visibleProperty().setValue(!bShown);
        lvFiletype.visibleProperty().setValue(!bShown);
        lvPref.visibleProperty().setValue(!bShown);
        //cbStatistics.setSelected(!bShown);        
    }
    /**
     * Sets to show in main window only completly populated
     * clauses (checked) or all clauses (unchecked).
     * The feature is controlled by {@link MbFXWords#bOnlyPopulated}.
     * @param event action
     */
    @FXML
    public void onOnlyPopulatedCheckBox(ActionEvent event) {
        //cbOnlyPopulated.setSelected(!cbOnlyPopulated.isSelected());        
        MbFXWords.bOnlyPopulated = cbOnlyPopulated.isSelected();
        MbFXWords.c.spPagesSetRange(MbFXWords.c.iSignificance);
        MbFXWords.c.populate(MbFXWords.c.iSignificance);
    }
    /**
     * Sets to show in main window only significant
     * clauses (checked) or all clauses (unchecked).
     * The extend of significance is automatically
     * adjusted according to text length.
     * The feature is controlled by {@link MbFXWords#bOnlySignificant}.
     * @param event action
     */
    @FXML
    public void onOnlySignificantCheckBox(ActionEvent event) {
        MbFXWords.bOnlySignificant = cbOnlySignificant.isSelected();
        MbFXWords.c.spPagesSetRange(MbFXWords.c.iSignificance);
        MbFXWords.c.populate(MbFXWords.c.iSignificance);
    }
    /**
     * Sets {@link MbFXWords#bAutoLanguage}
     * and {@link OpenNLP#sLanguage}
     * immediately without save.
     * @param event mouse
     */
    @FXML
    private void onLanguageOption(MouseEvent event) {
        //mbfxwords.OpenNLP.sArray.length;
        //event.getSource()
        RadioButton selectedRadioButton = (RadioButton) tgLanguage.getSelectedToggle();
        String toggleGroupValue = selectedRadioButton.getText();
        switch (toggleGroupValue) {                                
            case "auto": MbFXWords.bAutoLanguage=true; break;
            case "English": MbFXWords.bAutoLanguage=false; OpenNLP.sLanguage="eng"; break;
            case "French": MbFXWords.bAutoLanguage=false; OpenNLP.sLanguage="fra"; break;
            case "German": MbFXWords.bAutoLanguage=false; OpenNLP.sLanguage="deu"; break;
        }
    }
    /**
     * Copy plain text of selected file
     * (pdf or txt) to clipboard.
     * @param event action
     */
    @FXML
    private void onClipboardButton(ActionEvent event) {
        String[] asFileTemp;
        asFileTemp=MbFXWords.asDetermineFile();
        try {
            OpenNLP.ReadIn(asFileTemp);
        } catch (FileNotFoundException e){
            System.out.println("File ("+asFileTemp[0]+ ") not found!");
            OpenNLP.sArray = new String[1][][];
            return;
        } catch (Exception e){
            System.err.println("Caught MbFXWords Exception: ");
            e.printStackTrace();
        }
        OccurrenceOfWords.sInput=OpenNLP.textPostProcessing(OccurrenceOfWords.sInput);
        ClipboardContent content = new ClipboardContent();
        content.putString(OccurrenceOfWords.sInput);
        //content.putHtml("<b>Bold</b> text");
        Clipboard.getSystemClipboard().setContent(content);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btClipboard.setTooltip(new Tooltip("copy main text to clipboard"));
        if (MbFXWords.bAutoLanguage) {
            rbAuto.setSelected(true);rbAuto.requestFocus();
        } else
        switch (OpenNLP.sLanguage) {                                
            case "eng": rbEnglish.setSelected(true);rbEnglish.requestFocus();break;
            case "fra": rbFrench.setSelected(true);rbFrench.requestFocus();break;
            case "deu": rbGerman.setSelected(true);rbGerman.requestFocus();break;
        }        
        lblFile.setText(MbFXWords.f.getPath());
        lblFile.setTooltip(new Tooltip(MbFXWords.f.getPath()));
        Boolean bShown;
        bShown=MbFXWords.c.lblPopulated.visibleProperty().getValue();
        cbStatistics.setSelected(bShown);
        cbOnlyPopulated.setSelected(MbFXWords.bOnlyPopulated);
        cbOnlySignificant.setSelected(MbFXWords.bOnlySignificant);
        //
        ObservableList<String> items = FXCollections.observableArrayList (
                "pdf", "txt");
        lvFiletype.setItems(items);
        items = FXCollections.observableArrayList (
                "CSE", "CS", "SE", "most common (C)", "significant (S)", "extraordinary (E)");
        lvPref.setItems(items);
        items = FXCollections.observableArrayList (
                "SPO", "SO", "subject (S)", "predicate (P)", "object (O)");
        lvGram.setItems(items);
        lvFiletype.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, 
                        String old_val, String new_val) {
                        OccurrenceOfWords.sFiletype=new_val;
                        //lblFile.setText(OccurrenceOfWords.sFiletype);
            }
        });
        lvGram.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, 
                        String old_val, String new_val) {
                        OccurrenceOfWords.sGram=new_val;
                        //lblFile.setText(OccurrenceOfWords.sGram);
            }
        });
        lvPref.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, 
                        String old_val, String new_val) {
                        OccurrenceOfWords.sPref=new_val;
                        //lblFile.setText(OccurrenceOfWords.sPref);
            }
        });
        lvFiletype.getSelectionModel().select(0);
        lvFiletype.getFocusModel().focus(0);
        lvFiletype.scrollTo(0);
        lvGram.getSelectionModel().select(1);
        lvGram.getFocusModel().focus(1);
        lvGram.scrollTo(1);
        lvPref.getSelectionModel().select(3);
        lvPref.getFocusModel().focus(3);
        lvPref.scrollTo(3);
        //
        Tooltip t = new Tooltip("S-subject\nP-predicate\nO-object");
        lvGram.setTooltip(t);
        t = new Tooltip("C-most common\nS-significant\nE-extraordinary");
        lvPref.setTooltip(t);        
    }    
}
