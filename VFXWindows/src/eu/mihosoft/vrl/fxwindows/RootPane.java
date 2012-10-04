/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class RootPane extends Pane {

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        for (Node n : getManagedChildren()) {
            if (n instanceof Region) {
                Region p = (Region) n;

                double width = Math.max(p.getMinWidth(), p.getPrefWidth());
                double height = Math.max(p.getMinHeight(), p.getPrefHeight());

                n.resize(width, height);
            }
        }
    }
}