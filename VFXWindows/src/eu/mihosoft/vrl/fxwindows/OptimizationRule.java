/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface OptimizationRule {
    public boolean visible(OptimizableContentPane p, Transform t);
    public boolean attached(OptimizableContentPane p, Transform t);
}
