/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * Controls FXMLAbout.fxml.
 * @author MB
 */
public class FXMLAboutController  implements Initializable {
    //=====products:=====
    /**
     * ActionEvent of Link mbFXWords.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkSFmbFXWords(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://sourceforge.net/projects/mbfxwords/");         
    }
    /**
     * ActionEvent of Link OpenNLP.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkAPOpenNLP(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://opennlp.apache.org/");
    }
    /**
     * ActionEvent of Link Icon 2109139.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkIFChamestudio(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://www.iconfinder.com/icons/2109139/data_gallery_google_image_photo_picture_services_icon");
    }
    /**
     * ActionEvent of Link Stanbol Launcher.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkGHStanbolLauncher(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://github.com/fusepoolP3/p3-stanbol-launcher/tree/master/data/opennlp-pos/src/main/resources/models");
    }
    /**
     * ActionEvent of Link Wikipedia DE Arabica-Kaffee.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkWPDEArabicaKaffee(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://de.wikipedia.org/wiki/Arabica-Kaffee");
    }
    /**
     * ActionEvent of Link Wikipedia FR Coffea arabica.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkWPFRCoffeaArabica(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://fr.wikipedia.org/wiki/Coffea_arabica");
    }
    /**
     * ActionEvent of Link Wikipedia EN Coffea arabica.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkWPENCoffeaArabica(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://en.wikipedia.org/wiki/Coffea_arabica");
    }
    /**
     * ActionEvent of Link PDFBox.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkAPPDFBox(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://pdfbox.apache.org/");
    }
    //=====licenses:=====
    /**
     * ActionEvent of Link license CC BY-SA 3.0.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkCCSA3Unported(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://creativecommons.org/licenses/by-sa/3.0/deed.en");
    }
    /**
     * ActionEvent of Link license mbFXWords.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkGNUGPL3(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://github.com/open-hopin/mbFXWords/blob/master/LICENSE");
    }
    /**
     * ActionEvent of Link license Apache 2.0.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkAP2(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://www.apache.org/licenses/LICENSE-2.0");
    }
    /**
     * ActionEvent of Link license CC BY 3.0.
     * @param event ActionEvent
     */
    @FXML
    private void handleLkCC3Unported(ActionEvent event) {
        HostServices hostServices = (HostServices)MbFXWords.stageAbout.getProperties().get("hostServices");
        hostServices.showDocument("https://creativecommons.org/licenses/by/3.0/");
    }
    /**
     * Initialization of FXMLAbout.fxml.
     * Not used so far.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
}
