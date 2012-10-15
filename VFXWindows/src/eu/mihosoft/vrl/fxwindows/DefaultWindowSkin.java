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

        control.getIcons().addListener(new ListChangeListener<WindowIcon>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends WindowIcon> change) {
                //
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

        titleBar.addLeftIcon(new TestIcon(new Color(0, 0, 1.0, 0.1)));
        titleBar.addLeftIcon(new TestIcon(new Color(0, 1.0, 0, 0.1)));
        titleBar.addRightIcon(new TestIcon(new Color(0, 1.0, 0, 0.1)));
        titleBar.addRightIcon(new TestIcon(new Color(0, 0, 1.0, 0.1)));
    }

    static class TestIcon extends Pane {

        public TestIcon(Color c) {
            Rectangle rect = new Rectangle();
            rect.setFill(c);
            getChildren().add(rect);

            rect.widthProperty().bind(this.widthProperty());
            rect.heightProperty().bind(this.heightProperty());
        }

        @Override
        protected double computeMinWidth(double h) {
            return super.computeMaxWidth(h);
        }
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

                        if (newWidth < control.maxWidth(0)) {
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

//        System.out.println("skin: layout " + System.currentTimeMillis());

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

        double newTitleBarWidth = Math.max(
                titleBarWidth,
                windowWidth);

        titleBar.resize(newTitleBarWidth, titleBar.prefHeight(0));

//        double viewWidth = Math.max(control.getContentPane().prefWidth(0),
//                root.getWidth());
//
//        double viewHeight = Math.max(control.getContentPane().prefHeight(0),
//                root.getHeight() - titleBar.getHeight());
//
        double leftAndRight = getInsets().getLeft() + getInsets().getRight();
        double topAndBottom = getInsets().getTop() + getInsets().getBottom();
//
//        double scaleWidth = (root.getWidth() - leftAndRight) / viewWidth;
//        double scaleHeight = (root.getHeight() - topAndBottom) / viewHeight;
//
//        contentScale = Math.min(scaleWidth, scaleHeight);
//
//        control.getContentPane().resize(
//                viewWidth - leftAndRight / contentScale,
//                viewHeight - topAndBottom / contentScale);
//
//        control.getContentScaleTransform().setX(contentScale);
//        control.getContentScaleTransform().setY(contentScale);
//
//        control.getContentPane().relocate(
//                getInsets().getLeft() * 2,
//                titleBar.prefHeight(0) + getInsets().getTop());


        control.getContentPane().relocate(
                getInsets().getLeft(),
                titleBar.prefHeight(0));

        control.getContentPane().resize(
                root.getWidth() - leftAndRight,
                root.getHeight() - getInsets().getBottom() - titleBar.prefHeight(0));
    }

    /**
     * @return the titlebar
     */
    Pane getTitlebar() {
        return titleBar;
    }

    @Override
    protected double computeMinWidth(double d) {

        double result = root.minWidth(d);
        result = Math.max(result, titleBar.prefWidth(d));

        return result;
    }

    @Override
    protected double computePrefWidth(double d) {

        double result = root.minWidth(d);
        result = Math.max(result, titleBar.prefWidth(d));

        return result;
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

    private HBox leftIconPane;
    private HBox rightIconPane;
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

        setManaged(false);

        setSpacing(8);
        setStyle(CSS_STYLE);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-stroke: rgba(255,255,255,50); -fx-fill: rgba(255,255,255,50);");

        leftIconPane = new IconBox(this);
        rightIconPane = new IconBox(this);

        setAlignment(Pos.CENTER);

//        getChildren().add(leftIconPane);
        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(label);
        getChildren().add(VFXLayoutUtil.createHBoxFiller());
//        getChildren().add(rightIconPane);

//        setPrefWidth(USE_COMPUTED_SIZE);
//        setPrefHeight(USE_COMPUTED_SIZE);
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

class IconBox extends HBox {

    public IconBox(final TitleBar titleBar) {
//        minWidthProperty().bind(new DoubleBinding() {
//            {
//                super.bind(minWidthProperty(), titleBar.heightProperty());
//            }
//
//            @Override
//            protected double computeValue() {
//                double v = (titleBar.getHeight() - titleBar.getInsets().getTop()
//                        - titleBar.getInsets().getBottom())
//                        * getManagedChildren().size();
//                return v;
//            }
//        });
//
//        prefWidthProperty().bind(new DoubleBinding() {
//            {
//                super.bind(minWidthProperty(), titleBar.heightProperty());
//            }
//
//            @Override
//            protected double computeValue() {
//                double v = (titleBar.getHeight() - titleBar.getInsets().getTop()
//                        - titleBar.getInsets().getBottom())
//                        * getManagedChildren().size();
//                return v;
//            }
//        });
    }

//    @Override
//    protected void layoutChildren() {
//
//        int childrenCount = getManagedChildren().size();
//        double childWidth = getWidth() / childrenCount;
//
//        for (Node n : getManagedChildren()) {
//            if (n instanceof Region) {
//                Region r = (Region) n;
//                r.setMinSize(childWidth, childWidth);
//                r.setPrefSize(childWidth, childWidth);
//            }
//        }
//
//        super.layoutChildren();
//    }

//    @Override
//    protected double computeMinHeight(double w) {
//        if (!getManagedChildren().isEmpty()) {
//            return getManagedChildren().get(0).minHeight(w);
//        }
//
//        return super.computeMinHeight(w);
//    }
}
