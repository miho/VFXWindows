/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFXNodeUtils {
    // no instanciation allowed

    private VFXNodeUtils() {
        throw new AssertionError(); // not in this class either!
    }

    // we probably don't need this scale stuff anymore (since 2.2)
//    /**
//     * Returns the global scale of the specified node.
//     *
//     * @param n node
//     * @return the global scale of the specified node
//     */
//    public static Scale getGlobalScale(Node n) {
//        return _getGlobalScale(n,
//                new Scale(n.getScaleX(), n.getScaleY(), n.getScaleZ()));
//    }
//
//    /**
//     * Returns the global scale of the ancestors of the specified node.
//     *
//     * @param n node
//     * @return the global scale of the ancestors of the specified node
//     */
//    public static Scale getGlobalParentScale(Node n) {
//        return _getGlobalScale(n,
//                new Scale(1, 1, 1));
//    }
//
//    private static Scale _getGlobalScale(Node n, Scale scale) {
//        Node parent = n.getParent();
//
//        if (parent != null) {
//            scale = new Scale(
//                    parent.getScaleX() * scale.getScaleX(),
//                    parent.getScaleY() * scale.getScaleY(),
//                    parent.getScaleZ() * scale.getScaleZ());
//
//            scale = _getGlobalScale(parent, scale);
//        }
//
//        return scale;
//    }
    public static void removeFromParent(Node n) {
        if (n.getParent() instanceof Group) {
            ((Group) n.getParent()).getChildren().remove(n);
        } else if (n.getParent() instanceof Pane) {
            ((Pane) n.getParent()).getChildren().remove(n);
        }
    }

    public static void addToParent(Parent p, Node n) {
        if (p instanceof Group) {
            ((Group) p).getChildren().add(n);
        } else if (p instanceof Pane) {
            ((Pane) p).getChildren().add(n);
        }
    }

    public static Node getNode(Parent p, double sceneX, double sceneY, Class<?> nodeClass) {
//        Node result = null;

        for (Node n : p.getChildrenUnmodifiable()) {
            boolean contains = n.contains(n.sceneToLocal(sceneX, sceneY));

            if (contains) {
                
                if (nodeClass.isAssignableFrom(n.getClass())) {
                    return n;
                }

                if (n instanceof Parent) {
                    return getNode((Parent) n, sceneX, sceneY, nodeClass);
                }
            }
        }

        return null;
    }
}
