/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Window extends Control {

    public static final String DEFAULT_STYLE_CLASS = "window";
    public static final String DEFAULT_STYLE =
            "/eu/mihosoft/vrl/fxwindows/default.css";
    private boolean moveToFront = true;
    private Scale contentScaleTransform;
    private StringProperty titleProperty = new SimpleStringProperty("Title");
    private Property<Pane> contentPaneProperty =
            new SimpleObjectProperty<Pane>();

    public Window() {
        init();
    }

    public Window(String title) {
        setTitle(title);
        init();
    }

    private void init() {

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setContentPane(new StackPane());
    }

    @Override
    protected String getUserAgentStylesheet() {
        return DEFAULT_STYLE;
    }

    /**
     * @return the view
     */
    public Pane getContentPane() {
        return contentPaneProperty.getValue();
    }

    public void setContentPane(Pane contentPane) {
        contentPaneProperty.setValue(contentPane);
        contentScaleTransform = new Scale(1, 1);
        getContentScaleTransform().setPivotX(0);
        getContentScaleTransform().setPivotY(0);
        getContentScaleTransform().setPivotZ(0);

        getContentPane().getTransforms().add(getContentScaleTransform());
    }

    public Property<Pane> contentPaneProperty() {
        return contentPaneProperty;
    }

    /**
     * @param moveToFront the moveToFront to set
     */
    public void setMoveToFront(boolean moveToFront) {
        this.moveToFront = moveToFront;
    }

    /**
     * @return the moveToFront
     */
    public boolean isMoveToFront() {
        return moveToFront;
    }
    
    /**
     * @return the contentScaleTransform
     */
    public final Scale getContentScaleTransform() {
        return contentScaleTransform;
    }

    /**
     * @return the title
     */
    public final String getTitle() {
        return titleProperty.get();
    }

    /**
     * @param title the title to set
     */
    public final void setTitle(String title) {
        this.titleProperty.set(title);
    }

    public final StringProperty titleProperty() {
        return titleProperty;
    }
}
