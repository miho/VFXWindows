/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFXLayoutUtil {

    public static Node createHBoxFiller() {
        Node filler = new Region();
        
        HBox.setHgrow(filler, Priority.ALWAYS);
        
        return filler;
    }
    
    public static Node createVBoxFiller() {
        Node filler = new Region();
        
        VBox.setVgrow(filler, Priority.ALWAYS);
        
        return filler;
    }
}
