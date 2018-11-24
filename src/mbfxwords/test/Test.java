/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbfxwords.test;

import java.io.File;
import java.util.ArrayList;
import mbfxwords.OpenNLP;
import mbfxwords.RegexCollection;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 * Test classes not using JUnit.
 * Invoked by button with text "Clear"
 * in application main window.
 * @author mb
 */
public class Test {
    /**
     * Tests class RegexCollection.
     */
    public static void testRegexCollection(){
        RegexCollection<String> rc = new RegexCollection<>();
        rc.elements.put("anm","first");
        rc.elements.put("wmk","second");
        rc.elements.put("gm12","third");
        rc.elements.put("bmjklp","fourth");
        /*
        Object[] s =rc.getValue(".m.+"); //Java 1.8 does not support Arrays of Generics. They are always Objects.
        for (Object sElement:s){
            System.out.println(sElement);
        }
        */
        ArrayList<String> al =rc.getValue(".m.+");
        for (String sElement:al){
            System.out.println(sElement);
        }
    }
    /**
     * Tests OpenNLP.Parse().
     * @param s to be parsed
     */
    public static void testParse(String s){
        try {
            OpenNLP.Parse(s /*"Programcreek is a very huge and useful website."*/);
        } catch (Exception e){
            System.err.println("Caught MbFXWords Exception: ");
            e.printStackTrace();
        }
    }
    /**
     * Tests PDFBox().
     */
    public static void testPDFBox(){
        try {
            java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
            File fTest = new File("test.pdf");
            PDDocument document = PDDocument.load(fTest);
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                System.out.println("Text:" + text);
                document.close();
        }        
        } catch (Exception e){
            System.err.println("Caught MbFXWords Exception: ");
            e.printStackTrace();
        }
    }
}
