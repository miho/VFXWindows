/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Window extends Control {

    public static final String DEFAULT_STYLE_CLASS = "window";
    private boolean moveToFront = true;
    private StringProperty titleProperty = new SimpleStringProperty("Title");
    private BooleanProperty minimizeProperty = new SimpleBooleanProperty();
    private Property<Pane> contentPaneProperty =
            new SimpleObjectProperty<Pane>();
    private ObservableList<WindowIcon> leftIcons =
            FXCollections.observableArrayList();
    private ObservableList<WindowIcon> rightIcons =
            FXCollections.observableArrayList();

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
        return Constants.DEFAULT_STYLE;
    }

    /**
     * @return the view
     */
    public Pane getContentPane() {
        return contentPaneProperty.getValue();
    }

    public void setContentPane(Pane contentPane) {
        contentPaneProperty.setValue(contentPane);
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

    /**
     * @return the icons
     */
    public ObservableList<WindowIcon> getLeftIcons() {
        return leftIcons;
    }

    /**
     * @return the icons
     */
    public ObservableList<WindowIcon> getRightIcons() {
        return rightIcons;
    }

    public void setMinimized(Boolean v) {
        minimizeProperty.set(v);
    }
    
    public boolean getMinimized() {
        return minimizeProperty.get();
    }
    
    public BooleanProperty minimizedProperty() {
        return minimizeProperty;
    }
}
