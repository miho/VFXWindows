/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class OptimizableContentPane extends StackPane {

    private DoubleProperty minScale = new SimpleDoubleProperty(0.5);
    private boolean contentInvisible;

    public OptimizableContentPane() {

        localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
            @Override
            public void changed(ObservableValue<? extends Transform> ov, Transform oldVal, Transform newVal) {

                double scale = Math.min(newVal.getMxx(), newVal.getMyy());

                if (scale < getMinScale() && !contentInvisible) {
                    contentInvisible = true;
                    for (Node n : getChildrenUnmodifiable()) {
                        n.setVisible(false);
                    }
                }

                if (scale >= getMinScale() && contentInvisible) {
                    contentInvisible = false;
                    for (Node n : getChildrenUnmodifiable()) {
                        n.setVisible(true);
                    }
                }
            }
        });
    }
    
    public DoubleProperty minScaleProperty() {
        return minScale;
    }
    
    public void setMinScale(double s) {
        minScale.set(s);
    }
    
    public double getMinScale() {
        return minScale.get();
    }
}
