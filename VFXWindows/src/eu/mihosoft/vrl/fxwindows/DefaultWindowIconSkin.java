/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DefaultWindowIconSkin extends SkinBase<Window, BehaviorBase<Window>> {
    
//    private Pane root = new StackPane();

    public DefaultWindowIconSkin(Window c) {
        super(c, new BehaviorBase<Window>(c));
    }
}
