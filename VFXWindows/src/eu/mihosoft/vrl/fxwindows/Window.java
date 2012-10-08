/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
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
    private StackPane view = new StackPane();
    private boolean moveToFront = true;
    private Scale contentScaleTransform;
    private StringProperty title = new SimpleStringProperty("Title");

    public Window() {
        init();
    }

    public Window(String title) {
        setTitle(title);
        init();
    }

    private void init() {

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        contentScaleTransform = new Scale(1, 1);
        getContentScaleTransform().setPivotX(0);
        getContentScaleTransform().setPivotY(0);
        getContentScaleTransform().setPivotZ(0);

        view.getTransforms().add(getContentScaleTransform());
    }

    @Override
    protected String getUserAgentStylesheet() {
        return DEFAULT_STYLE;
    }

    /**
     * @return the view
     */
    public Pane getView() {
        return view;
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

//    public void removeNode(Node n) {
//        getChildren().remove(n);
//    }
//
//    /**
//     * @return the minScale
//     */
//    public double getMinScale() {
//        return minScale;
//    }
//
//    /**
//     * @param minScale the minScale to set
//     */
//    public void setMinScale(double minScale) {
//        this.minScale = minScale;
//    }
//
//    /**
//     * @return the maxScale
//     */
//    public double getMaxScale() {
//        return maxScale;
//    }
//
//    /**
//     * @param maxScale the maxScale to set
//     */
//    public void setMaxScale(double maxScale) {
//        this.maxScale = maxScale;
//    }
//    /**
//     * @return the scaleIncrement
//     */
//    public double getScaleIncrement() {
//        return scaleIncrement;
//    }
//
//    /**
//     * @param scaleIncrement the scaleIncrement to set
//     */
//    public void setScaleIncrement(double scaleIncrement) {
//        this.scaleIncrement = scaleIncrement;
//    }
//
//    void scale(double scaleValue) {
//        scaleTransform.setX(scaleValue);
//        scaleTransform.setY(scaleValue);
//        scaleTransform.setZ(1.0);
//
//        scaleTransform.setPivotX(0);
//        scaleTransform.setPivotX(0);
//        scaleTransform.setPivotZ(0);
//    }
//    
//    Scale getScaleTransform() {
//        return scaleTransform;
//    }
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
        return title.get();
    }

    /**
     * @param title the title to set
     */
    public final void setTitle(String title) {
        this.title.set(title);
    }

    public final StringProperty titleProperty() {
        return title;
    }
}
