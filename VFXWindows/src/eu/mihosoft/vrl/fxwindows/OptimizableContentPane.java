/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import java.util.ArrayList;
import java.util.Collection;
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

    private OptimizationRule optimizationRule;
    private Collection<Node> detatched = new ArrayList<Node>();

    public OptimizableContentPane() {
        this.optimizationRule = new OptimizationRuleImpl();

        localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
            @Override
            public void changed(ObservableValue<? extends Transform> ov, Transform oldVal, Transform newVal) {

                boolean visible = getOptimizationRule().visible(OptimizableContentPane.this, newVal);
                boolean attached = getOptimizationRule().attached(OptimizableContentPane.this, newVal);

                if (isVisible() != visible) {
                    setVisible(visible);
                }

                if (attached && !detatched.isEmpty()) {

                    getChildren().addAll(detatched);
                    detatched.clear();

                } else if (!attached && detatched.isEmpty()) {
                    detatched.addAll(getChildren());
                    getChildren().removeAll(detatched);
                    
                }
            }
        });
    }

    /**
     * @return the optimizationRule
     */
    public OptimizationRule getOptimizationRule() {
        return optimizationRule;
    }

    /**
     * @param optimizationRule the optimizationRule to set
     */
    public void setOptimizationRule(OptimizationRule optimizationRule) {
        this.optimizationRule = optimizationRule;
    }
}
class OptimizationRuleImpl implements OptimizationRule {

    private DoubleProperty minScale = new SimpleDoubleProperty(0.5);

    @Override
    public boolean visible(OptimizableContentPane p, Transform t) {

        double scale = Math.min(t.getMxx(), t.getMyy());

        return scale >= getMinScale();
    }

    @Override
    public boolean attached(OptimizableContentPane p, Transform t) {
        return visible(p, t);
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