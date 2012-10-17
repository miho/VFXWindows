/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MinimizeIcon extends WindowIcon {

    public static final String DEFAULT_STYLE_CLASS = "window-minimize-icon";

    public MinimizeIcon(final Window w) {

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

               w.setMinimized(!w.isMinimized());
            }
        });
    }
}
