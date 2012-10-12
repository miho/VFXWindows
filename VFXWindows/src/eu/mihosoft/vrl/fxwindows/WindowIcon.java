/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class WindowIcon extends Control {

    public static final String DEFAULT_STYLE_CLASS = "window-icon";
    public static final String DEFAULT_STYLE =
            "/eu/mihosoft/vrl/fxwindows/default.css";
    private ObjectProperty<EventHandler<ActionEvent>> onActionProperty = 
            new SimpleObjectProperty<EventHandler<ActionEvent>>();
    
    public WindowIcon() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }
    
    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onActionProperty;
    }
    
    public EventHandler<ActionEvent> getOnAction() {
        return onActionProperty.get();
    }
    
    public void setOnAction(EventHandler<ActionEvent> handler) {
        onActionProperty.set(handler);
    }
}
