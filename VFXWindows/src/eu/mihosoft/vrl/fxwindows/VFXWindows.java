/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;


/**
 * Draggable node sample.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFXWindows extends Application {

    public static final String CSS_STYLE =
            "  -fx-glass-color: rgba(85, 132, 160, 0.9);\n"
            + "  -fx-alignment: center;\n"
            + "  -fx-font-size: 20;\n"
            + "  -fx-background-color: linear-gradient(to bottom, derive(-fx-glass-color, 50%), -fx-glass-color);\n"
            + "  -fx-border-color: derive(-fx-glass-color, -60%);\n"
            + "  -fx-border-width: 2;\n"
            + "  -fx-background-insets: 1;\n"
            + "  -fx-border-radius: 3;\n"
            + "  -fx-background-radius: 3;\n";

    @Override
    public void start(Stage primaryStage) {

        // we use a default pane without layout such as HBox, VBox etc.
        final Pane root = new RootPane();

        final Scene scene = new Scene(root, 800, 700, Color.rgb(160, 160, 160));

        for (int j = 0; j < 6; j++) {

            final int numNodes = 6; // number of nodes to add
            final double spacing = 30; // spacing between nodes

            // add numNodes instances of DraggableNode to the root pane
            for (int i = 0; i < numNodes; i++) {
                DraggableNode node = new DraggableNode();
                node.setPrefSize(98, 80);
                // define the style via css
                node.setStyle(CSS_STYLE);
                // position the node
                node.setLayoutX(spacing * (i + 1) + node.getPrefWidth() * i);
                node.setLayoutY(spacing + (spacing + node.getPrefHeight()) * j);
                // add the node to the root pane 
                root.getChildren().add(node);
            }
        }

        // finally, show the stage
        primaryStage.setTitle("Draggable Node 02");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
/**
 * Simple draggable node.
 *
 * Dragging code based on
 * {@link http://blog.ngopal.com.np/2011/06/09/draggable-node-in-javafx-2-0/}
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class DraggableNode extends RootPane {

    // node position
    private double x = 0;
    private double y = 0;
    // mouse position
    private double mousex = 0;
    private double mousey = 0;
    private Node view;
    private boolean dragging = false;
    private boolean moveToFront = true;
    private Scale scaleTransform;
    private boolean zoomable = true;
    private double minScale = 0.1;
    private double maxScale = 10;
    private double scaleIncrement = 0.001;
    private ResizeMode resizeMode;
    private boolean RESIZE_TOP;
    private boolean RESIZE_LEFT;
    private boolean RESIZE_BOTTOM;
    private boolean RESIZE_RIGHT;

    public DraggableNode() {
        init();
    }

    public DraggableNode(Node view) {
        this.view = view;
        getChildren().add(view);
        init();
    }

    private void init() {

        setMinSize(10, 10);

        scaleTransform = new Scale(1, 1);
        scaleTransform.setPivotX(0);
        scaleTransform.setPivotY(0);
        scaleTransform.setPivotZ(0);

        getTransforms().add(scaleTransform);

        onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                final Node n = DraggableNode.this;

                final double parentScaleX = n.getParent().
                        localToSceneTransformProperty().getValue().getMxx();
                final double parentScaleY = n.getParent().
                        localToSceneTransformProperty().getValue().getMyy();

                final double scaleX = n.localToSceneTransformProperty().
                        getValue().getMxx();
                final double scaleY = n.localToSceneTransformProperty().
                        getValue().getMyy();

                // record the current mouse X and Y position on Node
                mousex = event.getSceneX();
                mousey = event.getSceneY();

                x = n.getLayoutX() * parentScaleX;
                y = n.getLayoutY() * parentScaleY;

                if (isMoveToFront()) {
                    toFront();
                }

            }
        });

        //Event Listener for MouseDragged
        onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                final Node n = DraggableNode.this;

                final double parentScaleX = n.getParent().
                        localToSceneTransformProperty().getValue().getMxx();
                final double parentScaleY = n.getParent().
                        localToSceneTransformProperty().getValue().getMyy();

                final double scaleX = n.localToSceneTransformProperty().
                        getValue().getMxx();
                final double scaleY = n.localToSceneTransformProperty().
                        getValue().getMyy();

                // Get the exact moved X and Y

                double offsetX = event.getSceneX() - mousex;
                double offsetY = event.getSceneY() - mousey;

                if (resizeMode == ResizeMode.NONE) {

                    x += offsetX;
                    y += offsetY;

                    double scaledX = x * 1 / parentScaleX;
                    double scaledY = y * 1 / parentScaleY;

                    n.setLayoutX(scaledX);
                    n.setLayoutY(scaledY);

                    dragging = true;

                } else {

                    double width = n.getBoundsInLocal().getMaxX()
                            - n.getBoundsInLocal().getMinX();
                    double height = n.getBoundsInLocal().getMaxY()
                            - n.getBoundsInLocal().getMinY();

                    if (RESIZE_TOP) {
                        double newHeight =
                                getBoundsInLocal().getHeight()
                                - offsetY / scaleY
                                - getInsets().getTop();
                        if (newHeight >= getMinHeight()) {
                            y += offsetY;
                            double scaledY = y / parentScaleY;

                            setLayoutY(scaledY);
                            setPrefHeight(newHeight);
                            autosize();
                        }
                    }
                    if (RESIZE_LEFT) {
                        double newWidth =
                                getBoundsInLocal().getWidth()
                                - offsetX / scaleX
                                - getInsets().getLeft();
                        if (newWidth >= getMinWidth()) {
                            x += offsetX;
                            double scaledX = x / parentScaleX;

                            setLayoutX(scaledX);
                            setPrefWidth(newWidth);
                            autosize();
                        }
                    }

                    if (RESIZE_BOTTOM) {
                        double newHeight =
                                getBoundsInLocal().getHeight()
                                + offsetY / scaleY
                                - getInsets().getBottom();
                        setPrefHeight(newHeight);
                        autosize();
                    }
                    if (RESIZE_RIGHT) {
                        double newWidth =
                                getBoundsInLocal().getWidth()
                                + offsetX / scaleX
                                - getInsets().getRight();
                        setPrefWidth(newWidth);
                        autosize();
                    }
                }

                // again set current Mouse x AND y position
                mousex = event.getSceneX();
                mousey = event.getSceneY();

                event.consume();
            }
        });

        onMouseClickedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                dragging = false;
            }
        });

        onMouseMovedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                final Node n = DraggableNode.this;

                final double parentScaleX = n.getParent().localToSceneTransformProperty().getValue().getMxx();
                final double parentScaleY = n.getParent().localToSceneTransformProperty().getValue().getMyy();

                final double scaleX = n.localToSceneTransformProperty().getValue().getMxx();
                final double scaleY = n.localToSceneTransformProperty().getValue().getMyy();

                final double border = 10;

                double diffMinX = Math.abs(n.getBoundsInLocal().getMinX() - t.getX());
                double diffMinY = Math.abs(n.getBoundsInLocal().getMinY() - t.getY());
                double diffMaxX = Math.abs(n.getBoundsInLocal().getMaxX() - t.getX());
                double diffMaxY = Math.abs(n.getBoundsInLocal().getMaxY() - t.getY());

                boolean left = diffMinX * scaleX < border;
                boolean top = diffMinY * scaleY < border;
                boolean right = diffMaxX * scaleX < border;
                boolean bottom = diffMaxY * scaleY < border;

                RESIZE_TOP = false;
                RESIZE_LEFT = false;
                RESIZE_BOTTOM = false;
                RESIZE_RIGHT = false;

                if (left && !top && !bottom) {
                    n.setCursor(Cursor.W_RESIZE);
                    resizeMode = ResizeMode.LEFT;
                    RESIZE_LEFT = true;
                } else if (left && top && !bottom) {
                    n.setCursor(Cursor.NW_RESIZE);
                    resizeMode = ResizeMode.TOP_LEFT;
                    RESIZE_LEFT = true;
                    RESIZE_TOP = true;
                } else if (left && !top && bottom) {
                    n.setCursor(Cursor.SW_RESIZE);
                    resizeMode = ResizeMode.BOTTOM_LEFT;
                    RESIZE_LEFT = true;
                    RESIZE_BOTTOM = true;
                } else if (right && !top && !bottom) {
                    n.setCursor(Cursor.E_RESIZE);
                    resizeMode = ResizeMode.RIGHT;
                    RESIZE_RIGHT = true;
                } else if (right && top && !bottom) {
                    n.setCursor(Cursor.NE_RESIZE);
                    resizeMode = ResizeMode.TOP_RIGHT;
                    RESIZE_RIGHT = true;
                    RESIZE_TOP = true;
                } else if (right && !top && bottom) {
                    n.setCursor(Cursor.SE_RESIZE);
                    resizeMode = ResizeMode.BOTTOM_RIGHT;
                    RESIZE_RIGHT = true;
                    RESIZE_BOTTOM = true;
                } else if (top && !left && !right) {
                    n.setCursor(Cursor.N_RESIZE);
                    resizeMode = ResizeMode.TOP;
                    RESIZE_TOP = true;
                } else if (bottom && !left && !right) {
                    n.setCursor(Cursor.S_RESIZE);
                    resizeMode = ResizeMode.BOTTOM;
                    RESIZE_BOTTOM = true;
                } else {
                    n.setCursor(Cursor.DEFAULT);
                    resizeMode = ResizeMode.NONE;
                }
            }
        });

        setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {

                if (!isZoomable()) {
                    return;
                }

                double scaleValue =
                        scaleTransform.getY() + event.getDeltaY() * getScaleIncrement();

                scaleValue = Math.max(scaleValue, getMinScale());
                scaleValue = Math.min(scaleValue, getMaxScale());

                scaleTransform.setX(scaleValue);
                scaleTransform.setY(scaleValue);

                scaleTransform.setPivotX(0);
                scaleTransform.setPivotX(0);
                scaleTransform.setPivotZ(0);

//                setScaleX(scaleValue);
//                setScaleY(scaleValue);

                event.consume();
            }
        });

    }

    /**
     * @return the zoomable
     */
    public boolean isZoomable() {
        return zoomable;
    }

    /**
     * @param zoomable the zoomable to set
     */
    public void setZoomable(boolean zoomable) {
        this.zoomable = zoomable;
    }

    /**
     * @return the dragging
     */
    protected boolean isDragging() {
        return dragging;
    }

    /**
     * @return the view
     */
    public Node getView() {
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

    public void removeNode(Node n) {
        getChildren().remove(n);
    }

    /**
     * @return the minScale
     */
    public double getMinScale() {
        return minScale;
    }

    /**
     * @param minScale the minScale to set
     */
    public void setMinScale(double minScale) {
        this.minScale = minScale;
    }

    /**
     * @return the maxScale
     */
    public double getMaxScale() {
        return maxScale;
    }

    /**
     * @param maxScale the maxScale to set
     */
    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
    }

    /**
     * @return the scaleIncrement
     */
    public double getScaleIncrement() {
        return scaleIncrement;
    }

    /**
     * @param scaleIncrement the scaleIncrement to set
     */
    public void setScaleIncrement(double scaleIncrement) {
        this.scaleIncrement = scaleIncrement;
    }
}

class RootPane extends Pane {

    @Override
    protected void layoutChildren() {
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

enum ResizeMode {

    NONE,
    TOP,
    LEFT,
    BOTTOM,
    RIGHT,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}
