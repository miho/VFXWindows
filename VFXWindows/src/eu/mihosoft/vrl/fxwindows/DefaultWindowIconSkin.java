/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DefaultWindowIconSkin extends SkinBase<WindowIcon, BehaviorBase<WindowIcon>> {

//    private Pane root = new StackPane();
    public DefaultWindowIconSkin(WindowIcon c) {
        super(c, new BehaviorBase<WindowIcon>(c));



    }
}
