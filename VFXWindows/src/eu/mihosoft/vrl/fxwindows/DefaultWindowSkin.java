/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.SkinBase;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
    private TitleBar titleBar;
    private Window control;
    private Pane root = new Pane();
    private double contentScale = 1.0;
    private double oldHeight;
    private Timeline minimizeTimeLine;

    public DefaultWindowSkin(Window w) {
        super(w, new BehaviorBase<Window>(w));
        this.control = w;
        titleBar = new TitleBar(control);
        titleBar.setTitle("");
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

        control.minimizedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, final Boolean oldValue, final Boolean newValue) {

                // TODO is this necessary or does the property handle this 
                // optimization already?
                if (oldValue == newValue) {
                    return;
                }

                boolean storeOldHeight = minimizeTimeLine == null && newValue;

                if (minimizeTimeLine != null) {
                    minimizeTimeLine.stop();
                    minimizeTimeLine = null;
                }

                double newHeight;

                if (newValue) {
                    newHeight = titleBar.getHeight();
                } else {
                    newHeight = oldHeight;
                }

                if (storeOldHeight) {
                    oldHeight = control.getPrefHeight();
                }

                minimizeTimeLine = new Timeline(
                        new KeyFrame(Duration.ZERO,
                        new KeyValue(control.prefHeightProperty(), control.getPrefHeight())),
                        new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(control.prefHeightProperty(), newHeight)));

                minimizeTimeLine.statusProperty().addListener(
                        new ChangeListener<Animation.Status>() {
                            @Override
                            public void changed(ObservableValue<? extends Status> ov, Status oldStatus, Status newStatus) {

                                if (newStatus == Status.STOPPED) {
                                    minimizeTimeLine = null;
                                    if (newValue) {
                                        control.getContentPane().setVisible(false);
                                    }
                                }
                            }
                        });

                minimizeTimeLine.play();
            }
        });

        control.prefHeightProperty().addListener(new MinimizeHeightListener(control, titleBar));

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

        titleBar.setStyle(control.getStyle());

        control.styleProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                titleBar.setStyle(t1);
            }
        });

        titleBar.getStyleClass().setAll(control.getTitleBarStyleClass());
        titleBar.getLabel().getStyleClass().setAll(control.getTitleBarStyleClass());

        control.titleBarStyleClassProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                titleBar.getStyleClass().setAll(t1);
                titleBar.getLabel().getStyleClass().setAll(t1);
            }
        });

        titleBar.getStylesheets().setAll(control.getStylesheets());

        control.getStylesheets().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else {
                        if (change.wasRemoved()) {
                            for (String i : change.getRemoved()) {
                                titleBar.getStylesheets().remove(i);
                            }
                        } else if (change.wasAdded()) {
                            for (String i : change.getAddedSubList()) {
                                titleBar.getStylesheets().add(i);
                            }
                        }
                    }
                }
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

                        newHeight = Math.max(
                                newHeight, control.minHeight(0));

                        if (newHeight < control.maxHeight(0)) {
                            control.setPrefHeight(newHeight);
                        }
                    }
                    if (RESIZE_RIGHT) {

                        double insetOffset = getInsets().getRight() / 2;

                        double xDiff = event.getSceneX() / parentScaleX
                                - sceneX / parentScaleY - insetOffset;

                        double newWidth = xDiff;

                        newWidth = Math.max(
                                newWidth, control.minWidth(0));

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

                if (control.isMinimized()) {

                    RESIZE_TOP = false;
                    RESIZE_LEFT = false;
                    RESIZE_BOTTOM = false;
                    RESIZE_RIGHT = false;

                    resizeMode = ResizeMode.NONE;

                    return;
                }

                final Node n = control;

                final double parentScaleX = n.getParent().localToSceneTransformProperty().getValue().getMxx();
                final double parentScaleY = n.getParent().localToSceneTransformProperty().getValue().getMyy();

                final double scaleX = n.localToSceneTransformProperty().getValue().getMxx();
                final double scaleY = n.localToSceneTransformProperty().getValue().getMyy();

                final double border = control.getResizableBorderWidth() * scaleX;
                
                double diffMinX = Math.abs(n.getLayoutBounds().getMinX() - t.getX() + getInsets().getLeft());
                double diffMinY = Math.abs(n.getLayoutBounds().getMinY() - t.getY() + getInsets().getTop());
                double diffMaxX = Math.abs(n.getLayoutBounds().getMaxX() - t.getX() - getInsets().getRight());
                double diffMaxY = Math.abs(n.getLayoutBounds().getMaxY() - t.getY() - getInsets().getBottom());

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
//                        control.getScaleY() + event.getDeltaY() * getScaleIncrement();
//
//                scaleValue = Math.max(scaleValue, getMinScale());
//                scaleValue = Math.min(scaleValue, getMaxScale());
//
//                control.setScaleX(scaleValue);
//                control.setScaleY(scaleValue);
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

        double minHeight = titleBar.prefHeight(d);

        if (!control.isMinimized() && control.getContentPane().isVisible()) {
            minHeight += control.getContentPane().minHeight(d)
                    + getInsets().getBottom();
        }

        result = Math.max(result, minHeight);

        return result;
    }

    static class MinimizeHeightListener implements ChangeListener<Number> {

        private Window control;
        private TitleBar titleBar;

        public MinimizeHeightListener(Window control, TitleBar titleBar) {
            this.control = control;
            this.titleBar = titleBar;
        }

        @Override
        public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
            if (control.isMinimized()
                    && control.getPrefHeight()
                    < titleBar.minHeight(0) + control.getContentPane().minHeight(0)) {
                control.getContentPane().setVisible(false);
//                System.out.println("v: false");
            } else if (!control.isMinimized()
                    && control.getPrefHeight()
                    >= titleBar.minHeight(0) + control.getContentPane().minHeight(0)) {
                control.getContentPane().setVisible(true);
//                System.out.println("v: true");
            }
        }
    }
}

class TitleBar extends HBox {

    public static final String DEFAULT_STYLE_CLASS = "window-titlebar";
    private Pane leftIconPane;
    private Pane rightIconPane;
    private Text label = new Text();
    private double iconSpacing = 3;
    Window control;
    // estimated size of "...",
    // is there a way to find out text dimension without rendering it
    private double offset = 40;
    private double originalTitleWidth;

    public TitleBar(Window w) {

        this.control = w;

        setManaged(false);

        getStylesheets().setAll(w.getStylesheets());
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setSpacing(8);

//        label.setTextAlignment(TextAlignment.CENTER);
//        label.getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        leftIconPane = new IconPane();
        rightIconPane = new IconPane();

        getChildren().add(leftIconPane);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(label);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(rightIconPane);


        control.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {

                if (control.getTitle() == null
                        || getLabel().getText() == null
                        || getLabel().getText().isEmpty()) {
                    return;
                }

                double maxIconWidth = Math.max(
                        leftIconPane.getWidth(), rightIconPane.getWidth());

                if (!control.getTitle().equals(getLabel().getText())) {
                    if (originalTitleWidth
                            + maxIconWidth * 2 + offset < getWidth()) {
                        getLabel().setText(control.getTitle());
                    }
                } else if (!"...".equals(getLabel().getText())) {
                    if (originalTitleWidth
                            + maxIconWidth * 2 + offset >= getWidth()) {
                        getLabel().setText("...");
                    }
                }
            }
        });

    }

    public void setTitle(String title) {
        getLabel().setText(title);

        originalTitleWidth = getLabel().getBoundsInParent().getWidth();

        double maxIconWidth = Math.max(
                        leftIconPane.getWidth(), rightIconPane.getWidth());
        
        if (originalTitleWidth
                + maxIconWidth * 2 + offset >= getWidth()) {
            getLabel().setText("...");
        }
    }

    public String getTitle() {
        return getLabel().getText();
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
                //                + getLabel().prefWidth(h)
                + getInsets().getLeft()
                + getInsets().getRight());

        return result + iconSpacing * 2 + offset;
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

    /**
     * @return the label
     */
    public Text getLabel() {
        return label;
    }

    private static class IconPane extends Pane {

        private double spacing = 2;

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

                double x = (width + spacing) * count;

                n.resizeRelocate(x, 0, width, height);

                count++;
            }
        }

        @Override
        protected double computeMinWidth(double h) {
            return getHeight() * getChildren().size()
                    + spacing * (getChildren().size() - 1);
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

// TODO do we still need this enum?
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