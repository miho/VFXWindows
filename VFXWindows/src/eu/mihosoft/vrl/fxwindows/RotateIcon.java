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
public class RotateIcon extends WindowIcon {

    public static final String DEFAULT_STYLE_CLASS = "window-rotate-icon";

    public RotateIcon(final Window w) {

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                RotateTransition rotationY = new RotateTransition();
                rotationY.setAxis(Rotate.Y_AXIS);
                rotationY.setDuration(Duration.seconds(10));
                rotationY.setByAngle(-360);
                rotationY.setNode(w);
                rotationY.setCycleCount(1);
                rotationY.play();
            }
        });
    }
}
