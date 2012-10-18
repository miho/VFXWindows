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
 * Window control. A window control is a window node as known from Swing, e.g
 * {@link javax.swing.JInternalFrame}. It can be used to realize MDI based
 * applications.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Window extends Control {

    /**
     * Default style class for css.
     */
    public static final String DEFAULT_STYLE_CLASS = "window";
    /**
     * Defines whether window is moved to front when user clicks on it
     */
    private boolean moveToFront = true;
    /**
     * Window title property (used by titlebar)
     */
    private StringProperty titleProperty = new SimpleStringProperty("Title");
    /**
     * Minimize property (defines whether to minimize the window,performed by
     * skin)
     */
    private BooleanProperty minimizeProperty = new SimpleBooleanProperty();
    /**
     * Content pane property. The content pane is the pane that is responsible
     * for showing user defined nodes/content.
     */
    private Property<Pane> contentPaneProperty =
            new SimpleObjectProperty<Pane>();
    /**
     * List of icons shown on the left. TODO replace left/right with more
     * generic position property?
     */
    private ObservableList<WindowIcon> leftIcons =
            FXCollections.observableArrayList();
    /**
     * List of icons shown on the right. TODO replace left/right with more
     * generic position property?
     */
    private ObservableList<WindowIcon> rightIcons =
            FXCollections.observableArrayList();
    /**
     * Defines the width of the border /area where the user can grab the window
     * and resize it.
     */
    private DoubleProperty resizableBorderWidthProperty = new SimpleDoubleProperty(5);
    /**
     * Defines the titlebar class name. This can be used to define css
     * properties specifically for the titlebar, e.g., background.
     */
    private StringProperty titleBarStyleClassProperty =
            new SimpleStringProperty("window-titlebar");

    /**
     * Constructor.
     */
    public Window() {
        init();
    }

    /**
     * Constructor.
     *
     * @param title
     */
    public Window(String title) {
        setTitle(title);
        init();
    }

    private void init() {

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setContentPane(new StackPane());

        // TODO ugly to do this in control? probably violates pattern rules?
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
     * @return the content pane of this window
     */
    public Pane getContentPane() {
        return contentPaneProperty.getValue();
    }

    /**
     * Defines the content pane of this window.
     *
     * @param contentPane content pane to set
     */
    public void setContentPane(Pane contentPane) {
        contentPaneProperty.setValue(contentPane);
    }

    /**
     * Content pane property.
     *
     * @return content pane property
     */
    public Property<Pane> contentPaneProperty() {
        return contentPaneProperty;
    }

    /**
     * Defines whether this window shall be moved to front when a user clicks on
     * the window.
     *
     * @param moveToFront the state to set
     */
    public void setMoveToFront(boolean moveToFront) {
        this.moveToFront = moveToFront;
    }

    /**
     * Indicates whether the window shall be moved to front when a user clicks
     * on the window.
     *
     * @return <code>true</code> if the window shall be moved to front when a
     * user clicks on the window; <code>false</code> otherwise
     */
    public boolean isMoveToFront() {
        return moveToFront;
    }

    /**
     * Returns the window title.
     *
     * @return the title
     */
    public final String getTitle() {
        return titleProperty.get();
    }

    /**
     * Defines the window title.
     *
     * @param title the title to set
     */
    public final void setTitle(String title) {
        this.titleProperty.set(title);
    }

    /**
     * Returns the window title property.
     *
     * @return the window title property
     */
    public final StringProperty titleProperty() {
        return titleProperty;
    }

    /**
     * Returns a list that contains the icons that are placed on the left side
     * of the titlebar. Add icons to the list to add them to the left side of
     * the window titlebar.
     *
     * @return a list containing the left icons
     *
     * @see #getRightIcons()
     */
    public ObservableList<WindowIcon> getLeftIcons() {
        return leftIcons;
    }

    /**
     * Returns a list that contains the icons that are placed on the right side
     * of the titlebar. Add icons to the list to add them to the right side of
     * the window titlebar.
     *
     * @return a list containing the right icons
     *
     * @see #getLeftIcons()
     */
    public ObservableList<WindowIcon> getRightIcons() {
        return rightIcons;
    }

    /**
     * Defines whether this window shall be minimized.
     *
     * @param v the state to set
     */
    public void setMinimized(Boolean v) {
        minimizeProperty.set(v);
    }

    /**
     * Indicates whether the window is currently minimized.
     *
     * @return <code>true</code> if the window is currently minimized;
     * <code>false</code> otherwise
     */
    public boolean isMinimized() {
        return minimizeProperty.get();
    }

    /**
     * Returns the minimize property.
     *
     * @return the minimize property
     */
    public BooleanProperty minimizedProperty() {
        return minimizeProperty;
    }

    /**
     * Returns the titlebar style class property.
     *
     * @return the titlebar style class property
     */
    public StringProperty titleBarStyleClassProperty() {
        return titleBarStyleClassProperty;
    }

    /**
     * Defines the CSS style class of the titlebar.
     *
     * @param name the CSS style class name
     */
    public void setTitleBarStyleClass(String name) {
        titleBarStyleClassProperty.set(name);
    }

    /**
     * Returns the CSS style class of the titlebar.
     *
     * @return the CSS style class of the titlebar
     */
    public String getTitleBarStyleClass() {
        return titleBarStyleClassProperty.get();
    }

    /**
     * Returns the resizable border width property.
     *
     * @return the resizable border width property
     *
     * @see #setResizableBorderWidth(double)
     */
    public DoubleProperty resizableBorderWidthProperty() {
        return resizableBorderWidthProperty;
    }

    /**
     * Defines the width of the "resizable border" of the window. The resizable
     * border is usually defined as a rectangular border around the layout
     * bounds of the window where the mouse cursor changes to "resizable" and
     * which allows to resize the window by performing a "dragging gesture",
     * i.e., the user can "grab" the window border and change the size of the
     * window.
     *
     * @param v border width
     */
    public void setResizableBorderWidth(double v) {
        resizableBorderWidthProperty.set(v);
    }

    /**
     * Returns the width of the "resizable border" of the window.
     *
     * @return the width of the "resizable border" of the window
     *
     * @see #setResizableBorderWidth(double)
     */
    public double getResizableBorderWidth() {
        return resizableBorderWidthProperty.get();
    }
}
