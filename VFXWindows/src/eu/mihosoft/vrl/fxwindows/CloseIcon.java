/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CloseIcon extends WindowIcon {

    public static final String DEFAULT_STYLE_CLASS = "window-close-icon";

    public CloseIcon(final Window w) {

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                ScaleTransition st = new ScaleTransition();
                st.setNode(w);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(0);
                st.setToY(0);
                st.setDuration(Duration.seconds(0.2));
                st.statusProperty().addListener(new ChangeListener<Animation.Status>() {
                    @Override
                    public void changed(ObservableValue<? extends Animation.Status> observableValue,
                            Animation.Status oldValue, Animation.Status newValue) {
                        if (newValue == Animation.Status.STOPPED) {
                            VFXNodeUtils.removeFromParent(w);
                        }
                    }
                });
                st.play();
            }
        });
    }
}
