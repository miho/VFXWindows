/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DefaultWindowSkin extends SkinBase<Window, BehaviorBase<Window>> {

    private double mouseX;
    private double mouseY;
    private double nodeX = 0;
    private double nodeY = 0;
    private boolean dragging = false;
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
    private Window control;
    private Pane root = new Pane();
    private double contentScale = 1.0;

    public DefaultWindowSkin(Window w) {
        super(w, new BehaviorBase<Window>(w));
        this.control = w;
        titleBar.setTitle("");
        titleBar.setPrefHeight(30);
        init();
    }

    private void init() {

        getChildren().add(root);
        root.getChildren().add(titleBar);

        for (WindowIcon i : control.getLeftIcons()) {
            titleBar.addLeftIcon(i);
        }

        for (WindowIcon i : control.getRightIcons()) {
            titleBar.addRightIcon(i);
        }

        control.getLeftIcons().addListener(new ListChangeListener<WindowIcon>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends WindowIcon> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else {
                        if (change.wasRemoved()) {
                            for (WindowIcon i : change.getRemoved()) {
                                titleBar.removeLeftIcon(i);
                            }
                        } else if (change.wasAdded()) {
                            for (WindowIcon i : change.getAddedSubList()) {
                                titleBar.addLeftIcon(i);
                            }
                        }
                    }
                }
            }
        });

        control.getRightIcons().addListener(new ListChangeListener<WindowIcon>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends WindowIcon> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else {
                        if (change.wasRemoved()) {
                            for (WindowIcon i : change.getRemoved()) {
                                titleBar.removeRightIcon(i);
                            }
                        } else if (change.wasAdded()) {
                            for (WindowIcon i : change.getAddedSubList()) {
                                titleBar.addRightIcon(i);
                            }
                        }
                    }
                }
            }
        });

        initMouseEventHandlers();

        titleBar.setTitle(control.getTitle());

        control.titleProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
                titleBar.setTitle(newValue);
                control.autosize();
            }
        });

        root.getChildren().add(control.getContentPane());
        control.getContentPane().setManaged(false);

        control.contentPaneProperty().addListener(new ChangeListener<Pane>() {
            @Override
            public void changed(ObservableValue<? extends Pane> ov, Pane oldValue, Pane newValue) {
                root.getChildren().remove(oldValue);
                root.getChildren().add(newValue);
                newValue.setManaged(false);
            }
        });
    }

    private void initMouseEventHandlers() {
        control.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                final Node n = control;

                final double parentScaleX = n.getParent().
                        localToSceneTransformProperty().getValue().getMxx();
                final double parentScaleY = n.getParent().
                        localToSceneTransformProperty().getValue().getMyy();

                mouseX = event.getSceneX();
                mouseY = event.getSceneY();

                nodeX = n.getLayoutX() * parentScaleX;
                nodeY = n.getLayoutY() * parentScaleY;

                if (control.isMoveToFront()) {
                    control.toFront();
                }
            }
        });

        //Event Listener for MouseDragged
        control.onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                final Node n = control;

                final double parentScaleX = n.getParent().
                        localToSceneTransformProperty().getValue().getMxx();
                final double parentScaleY = n.getParent().
                        localToSceneTransformProperty().getValue().getMyy();

                final double scaleX = n.localToSceneTransformProperty().
                        getValue().getMxx();
                final double scaleY = n.localToSceneTransformProperty().
                        getValue().getMyy();

                Bounds boundsInScene =
                        control.localToScene(control.getBoundsInLocal());

                double sceneX = boundsInScene.getMinX();
                double sceneY = boundsInScene.getMinY();

                double offsetX = event.getSceneX() - mouseX;
                double offsetY = event.getSceneY() - mouseY;

                if (resizeMode == ResizeMode.NONE) {

                    nodeX += offsetX;
                    nodeY += offsetY;

                    double scaledX = nodeX * 1 / parentScaleX;
                    double scaledY = nodeY * 1 / parentScaleY;

                    n.setLayoutX(scaledX);
                    n.setLayoutY(scaledY);

                    dragging = true;

                } else {

                    double width = n.getBoundsInLocal().getMaxX()
                            - n.getBoundsInLocal().getMinX();
                    double height = n.getBoundsInLocal().getMaxY()
                            - n.getBoundsInLocal().getMinY();

                    if (RESIZE_TOP) {
//                        System.out.println("TOP");

                        double insetOffset = getInsets().getTop() / 2;

                        double yDiff =
                                sceneY / parentScaleY
                                + insetOffset
                                - event.getSceneY() / parentScaleY;

                        double newHeight = control.getPrefHeight() + yDiff;


                        if (newHeight > control.minHeight(0)) {
                            control.setLayoutY(control.getLayoutY() - yDiff);
                            control.setPrefHeight(newHeight);
                        }
                    }
                    if (RESIZE_LEFT) {
//                        System.out.println("LEFT");

                        double insetOffset = getInsets().getLeft() / 2;

                        double xDiff = sceneX / parentScaleX
                                + insetOffset
                                - event.getSceneX() / parentScaleX;

                        double newWidth = control.getPrefWidth() + xDiff;

                        if (newWidth > control.minWidth(0)) {
                            control.setLayoutX(control.getLayoutX() - xDiff);
                            control.setPrefWidth(newWidth);
                        }
                    }

                    if (RESIZE_BOTTOM) {
//                        System.out.println("BOTTOM");

                        double insetOffset = getInsets().getBottom() / 2;

                        double yDiff = event.getSceneY() / parentScaleY
                                - sceneY / parentScaleY - insetOffset;

                        double newHeight = yDiff;

                        if (newHeight < control.maxHeight(0)) {
                            control.setPrefHeight(newHeight);
                        }
                    }
                    if (RESIZE_RIGHT) {

                        double insetOffset = getInsets().getRight() / 2;

                        double xDiff = event.getSceneX() / parentScaleX
                                - sceneX / parentScaleY - insetOffset;

                        double newWidth = xDiff;

                        if (newWidth < control.maxWidth(0) 
                                && newWidth > control.minWidth(0)) {
                            control.setPrefWidth(newWidth);
                        }
                    }
                }

                mouseX = event.getSceneX();
                mouseY = event.getSceneY();


                event.consume();
            }
        });

        control.onMouseClickedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                dragging = false;
            }
        });

        control.onMouseMovedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                final Node n = control;

                final double parentScaleX = n.getParent().localToSceneTransformProperty().getValue().getMxx();
                final double parentScaleY = n.getParent().localToSceneTransformProperty().getValue().getMyy();

                final double scaleX = n.localToSceneTransformProperty().getValue().getMxx();
                final double scaleY = n.localToSceneTransformProperty().getValue().getMyy();

                final double border = 10 * scaleX;

                double diffMinX = Math.abs(n.getBoundsInLocal().getMinX() - t.getX() + getInsets().getLeft());
                double diffMinY = Math.abs(n.getBoundsInLocal().getMinY() - t.getY() + getInsets().getTop());
                double diffMaxX = Math.abs(n.getBoundsInLocal().getMaxX() - t.getX() - getInsets().getRight());
                double diffMaxY = Math.abs(n.getBoundsInLocal().getMaxY() - t.getY() - getInsets().getBottom());

                boolean left = diffMinX * scaleX < Math.max(border, getInsets().getLeft() / 2 * scaleX);
                boolean top = diffMinY * scaleY < Math.max(border, getInsets().getTop() / 2 * scaleY);
                boolean right = diffMaxX * scaleX < Math.max(border, getInsets().getRight() / 2 * scaleX);
                boolean bottom = diffMaxY * scaleY < Math.max(border, getInsets().getBottom() / 2 * scaleY);

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

                control.autosize();
            }
        });

//        setOnScroll(new EventHandler<ScrollEvent>() {
//            @Override
//            public void handle(ScrollEvent event) {
//
//                if (!isZoomable()) {
//                    return;
//                }
//
//                double scaleValue =
//                        control.getScaleTransform().getY() + event.getDeltaY() * getScaleIncrement();
//
//                scaleValue = Math.max(scaleValue, getMinScale());
//                scaleValue = Math.min(scaleValue, getMaxScale());
//
//                control.scale(scaleValue);
//
//                event.consume();
//            }
//        });
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

        root.relocate(0, 0);
        root.resize(root.getWidth()
                + getInsets().getLeft() + getInsets().getRight(),
                root.getHeight()
                + getInsets().getTop() + getInsets().getBottom());

        titleBar.relocate(0, 0);
        double titleBarWidth = titleBar.prefWidth(0);
        double windowWidth = root.getWidth();

        if (titleBarWidth > windowWidth) {
            setWidth(titleBarWidth);
        }

        double newTitleBarWidth =
                Math.max(
                titleBarWidth,
                windowWidth);

        titleBar.resize(newTitleBarWidth, titleBar.prefHeight(0));

        double leftAndRight = getInsets().getLeft() + getInsets().getRight();
        double topAndBottom = getInsets().getTop() + getInsets().getBottom();

        control.getContentPane().relocate(
                getInsets().getLeft(),
                titleBar.prefHeight(0));

        control.getContentPane().resize(
                root.getWidth() - leftAndRight,
                root.getHeight() - getInsets().getBottom() - titleBar.prefHeight(0));

        titleBar.layoutChildren();
    }

    @Override
    protected double computeMinWidth(double d) {

        double result = root.minWidth(d);
        result = Math.max(result,
                titleBar.prefWidth(d));

        return result;
    }

    @Override
    protected double computePrefWidth(double d) {

        return computeMinWidth(d);
    }

    @Override
    protected double computeMinHeight(double d) {

        double result = root.minHeight(d);
        result = Math.max(result,
                titleBar.prefHeight(d)
                + control.getContentPane().minHeight(d)
                + getInsets().getBottom());

        return result;
    }
}

class TitleBar extends HBox {

    private Pane leftIconPane;
    private Pane rightIconPane;
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
    
    private double spacing = 3;

    public TitleBar() {

        setManaged(false);

        setSpacing(8);
        setStyle(CSS_STYLE);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-stroke: rgba(255,255,255,50); -fx-fill: rgba(255,255,255,50);");

        leftIconPane = new IconPane();
        rightIconPane = new IconPane();

        getChildren().add(leftIconPane);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(label);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(rightIconPane);

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

    public void removeLeftIcon(Node n) {
        leftIconPane.getChildren().remove(n);
    }

    public void removeRightIcon(Node n) {
        rightIconPane.getChildren().remove(n);
    }

    @Override
    protected double computeMinWidth(double h) {
        double result = super.computeMinWidth(h);

        double iconWidth =
                Math.max(
                leftIconPane.prefWidth(h),
                rightIconPane.prefWidth(h)) * 2;

        result = Math.max(result,
                iconWidth
                + label.prefWidth(h)
                + getInsets().getLeft()
                + getInsets().getRight());

        return result + spacing*2;
    }

    @Override
    protected double computePrefWidth(double h) {
        return computeMinWidth(h);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        leftIconPane.resizeRelocate(getInsets().getLeft(), getInsets().getTop(),
                leftIconPane.prefWidth(USE_PREF_SIZE),
                getHeight() - getInsets().getTop() - getInsets().getBottom());

        rightIconPane.resize(rightIconPane.prefWidth(USE_PREF_SIZE),
                getHeight() - getInsets().getTop() - getInsets().getBottom());
        rightIconPane.relocate(getWidth() - rightIconPane.getWidth() - getInsets().getRight(),
                getInsets().getTop());
    }

    private static class IconPane extends Pane {

        public IconPane() {
            setManaged(false);
            //
            setPrefWidth(USE_COMPUTED_SIZE);
            setMinWidth(USE_COMPUTED_SIZE);
        }

        @Override
        protected void layoutChildren() {

            int count = 0;

            double width = getHeight();
            double height = getHeight();

            for (Node n : getManagedChildren()) {

                double x = width * count;

                n.resizeRelocate(x, 0, width, height);

                count++;
            }
        }

        @Override
        protected double computeMinWidth(double h) {
            return getHeight() * getChildren().size();
        }

        @Override
        protected double computeMaxWidth(double h) {
            return computeMinWidth(h);
        }

        @Override
        protected double computePrefWidth(double h) {
            return computeMinWidth(h);
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