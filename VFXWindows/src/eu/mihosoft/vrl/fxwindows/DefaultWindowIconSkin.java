/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DefaultWindowIconSkin extends SkinBase<WindowIcon, BehaviorBase<WindowIcon>> {

//    private Pane root = new StackPane();
    public DefaultWindowIconSkin(final WindowIcon c) {
        super(c, new BehaviorBase<WindowIcon>(c));

        setCursor(Cursor.DEFAULT);
        
        onMouseClickedProperty().set(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (c.getOnAction()!=null) {
                    c.getOnAction().handle(new ActionEvent(t, c));
                }
            }
        });
    }
}
