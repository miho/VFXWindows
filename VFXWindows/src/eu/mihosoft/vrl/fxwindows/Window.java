/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Window extends Pane {

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
    private TitleBar titleBar = new TitleBar();

    public Window() {
        init();
    }

    public Window(String title) {
        titleBar.setTitle(title);
        init();
    }

    private void init() {
        setStyle(CSS_STYLE);
        getChildren().add(titleBar);

        setMinSize(10, 10);

        scaleTransform = new Scale(1, 1);
        scaleTransform.setPivotX(0);
        scaleTransform.setPivotY(0);
        scaleTransform.setPivotZ(0);

        getTransforms().add(scaleTransform);

        autosize();

        initMouseEventHandlers();

        titleBar.addLeftIcon(new TestIcon());
        titleBar.addRightIcon(new TestIcon());
    }

    static class TestIcon extends StackPane {

        public TestIcon() {
            setMinSize(20, 20);
            setMaxSize(30, 30);
            Rectangle rect = new Rectangle(22, 22);
            rect.setFill(new Color(0.1, 0.1, 0.1, 1.0));
            getChildren().add(rect);
        }
    }

    private void initMouseEventHandlers() {
        onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                final Node n = Window.this;

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

                autosize();
            }
        });

        //Event Listener for MouseDragged
        onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                final Node n = Window.this;

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
                    autosize();

                } else {

                    double width = n.getBoundsInLocal().getMaxX()
                            - n.getBoundsInLocal().getMinX();
                    double height = n.getBoundsInLocal().getMaxY()
                            - n.getBoundsInLocal().getMinY();

                    if (RESIZE_TOP) {

                        layout();

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

                            autosize();

                            setPrefWidth(newWidth);
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

                final Node n = Window.this;

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

                autosize();
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

    @Override
    protected void layoutChildren() {

        super.layoutChildren();

        titleBar.relocate(0, 0);
        double titleBarWidth = titleBar.prefWidth(0);
        double windowWidth = getWidth();

//        System.out.println("t: " + titleBarWidth + ", w: " + windowWidth);

        if (titleBarWidth > windowWidth) {

            setWidth(titleBarWidth);
        }

        double newTitleBar = Math.max(titleBarWidth, windowWidth);
        titleBar.resize(newTitleBar, 30);
    }

    /**
     * @return the titlebar
     */
    Pane getTitlebar() {
        return titleBar;
    }
//    @Override
//    protected double computePrefWidth(double d) {
//
//        double result = super.computePrefWidth(d);
//        result = Math.max(result, titleBar.computePrefWidth(0));
//
//        return result;
//    }
//    
//    @Override
//    protected double computeMinWidth(double d) {
//
//        double result = super.computeMinWidth(d);
//        result = Math.max(result, titleBar.computeMinWidth(d));
//
//        System.out.println("MinW: " + result);
//
//        return result;
//    }
}

class TitleBar extends HBox {

    private HBox leftIconPane = new HBox();
    private HBox rightIconPane = new HBox();
    private Text label = new Text();
    public static final String CSS_STYLE =
            "  -fx-glass-color: rgba(42, 42, 42, 0.9);\n"
            + "  -fx-alignment: center;\n"
            + "  -fx-font-size: 20;\n"
            + "  -fx-background-color: linear-gradient(to bottom, derive(-fx-glass-color, 30%), -fx-glass-color);\n"
            + "  -fx-border-color: derive(-fx-glass-color, -60%);\n"
            + "  -fx-border-width: 2;\n"
            + "  -fx-background-insets: 1;\n"
            + "  -fx-border-radius: 3;\n"
            + "  -fx-background-radius: 3;\n";

    public TitleBar() {
        setSpacing(8);
        setStyle(CSS_STYLE);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-stroke: rgba(255,255,255,50); -fx-fill: rgba(255,255,255,50);");

        VBox leftIconOuterBox = new VBox();
        leftIconOuterBox.getChildren().add(VFXLayoutUtil.createVBoxFiller());
        leftIconOuterBox.getChildren().add(leftIconPane);
        leftIconOuterBox.getChildren().add(VFXLayoutUtil.createVBoxFiller());

        VBox rightIconOuterBox = new VBox();
        rightIconOuterBox.getChildren().add(VFXLayoutUtil.createVBoxFiller());
        rightIconOuterBox.getChildren().add(rightIconPane);
        rightIconOuterBox.getChildren().add(VFXLayoutUtil.createVBoxFiller());

        setAlignment(Pos.CENTER);
        HBox.setHgrow(leftIconOuterBox, Priority.NEVER);
        HBox.setHgrow(rightIconOuterBox, Priority.NEVER);
        getChildren().add(leftIconOuterBox);
        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(label);
        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(rightIconOuterBox);
    }

    public void setTitle(String title) {
        label.setText(title);
    }

    public String getTitle() {
        return label.getText();
    }

    public void addLeftIcon(Node n) {
        leftIconPane.getChildren().add(n);
    }

    public void addRightIcon(Node n) {
        rightIconPane.getChildren().add(n);
    }
//    @Override
//    protected double computePrefWidth(double d) {
//        double result = prefWidth(d) + leftIconPane.getPrefWidth() + label.getBoundsInLocal().getWidth();
//
//        result = Math.max(result, super.computePrefWidth(d));
//
//        return result;
//    }
//    
//    @Override
//    protected double computeMinWidth(double d) {
//        return computePrefWidth(d);
//    }
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
