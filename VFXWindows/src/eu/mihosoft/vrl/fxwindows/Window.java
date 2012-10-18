/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
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
    private DoubleProperty resizableBorderWidthProperty = new SimpleDoubleProperty(5);
    
    private StringProperty titleBarStyleClassProperty = new SimpleStringProperty("window-titlebar");

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

        boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {

                if (t1.equals(t)) {
                    return;
                }

                getParent().requestLayout();

                double x = Math.max(0, getLayoutX());
                double y = Math.max(0, getLayoutY());

                setLayoutX(x);
                setLayoutY(y);
            }
        });
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

    public boolean isMinimized() {
        return minimizeProperty.get();
    }

    public BooleanProperty minimizedProperty() {
        return minimizeProperty;
    }
    
    public StringProperty titleBarStyleClassProperty() {
        return titleBarStyleClassProperty;
    }
    
    public void setTitleBarStyleClass(String name) {
        titleBarStyleClassProperty.set(name);
    }
    
    public String getTitleBarStyleClass() {
        return titleBarStyleClassProperty.get();
    }
    
    public DoubleProperty resizableBorderWidthProperty() {
        return resizableBorderWidthProperty;
    }
    
    public void setResizableBorderWidth(double v) {
        resizableBorderWidthProperty.set(v);
    }
    
    public double getResizableBorderWidth() {
        return resizableBorderWidthProperty.get();
    }
}
