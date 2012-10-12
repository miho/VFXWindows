/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        
        System.out.println("1");

        // we use a default pane without layout such as HBox, VBox etc.
        final Pane root = new Pane();
        final Scene scene = new Scene(root, 1024, 768, Color.rgb(160, 160, 160));

//        if (false)
        for (int j = 0; j < 4; j++) {

            final int numNodes = 4; // number of nodes to add
            final double spacing = 30; // spacing between nodes

            // add numNodes instances of DraggableNode to the root pane
            for (int i = 0; i < numNodes; i++) {
                Window node = createWindowHierarchy(
                        new Window("W (" + (i + 1) + "," + (j + 1) + ")"), 8);

                node.setPrefSize(220, 120);
                node.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                // position the node
                node.setLayoutX(spacing * (i + 1) + node.getPrefWidth() * i);
                node.setLayoutY(spacing + (spacing + node.getPrefHeight()) * j);
                // add the node to the root pane 
                root.getChildren().add(node);
            }
        }

        // finally, show the stage
        primaryStage.setTitle("VFXWindow Demo");
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

    private void addAnimatedScaledPane(Pane root) {
        Button btn = new Button("TestBtn TestBtn TestBtn");
        btn.setMinWidth(500);
        final ScalableContentPane scaledContent = new ScalableContentPane();
        scaledContent.setStyle("-fx-border-color: rgb(255,0,0);\n"
                + "-fx-border-width: 10;\n");
        scaledContent.getContentPane().setStyle("-fx-border-color: rgb(0,255,0);\n"
                + "-fx-border-width: 10;");
        scaledContent.setPrefSize(400, 400);
        scaledContent.getContentPane().getChildren().add(btn);

        Window w = new Window("Window");
        scaledContent.getContentPane().getChildren().add(w);

        root.getChildren().add(scaledContent);

        btn.onActionProperty().set(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Timeline timeLine = new Timeline(
                        new KeyFrame(Duration.ZERO,
                        new KeyValue(scaledContent.prefWidthProperty(), scaledContent.getPrefWidth()),
                        new KeyValue(scaledContent.prefHeightProperty(), scaledContent.getPrefHeight())),
                        new KeyFrame(Duration.seconds(10),
                        new KeyValue(scaledContent.prefWidthProperty(), 600),
                        new KeyValue(scaledContent.prefHeightProperty(), 600)),
                        new KeyFrame(Duration.seconds(20),
                        new KeyValue(scaledContent.prefWidthProperty(), 100),
                        new KeyValue(scaledContent.prefHeightProperty(), 100)));

                timeLine.play();
            }
        });
    }

    private Window createWindowHierarchy(Window node, int max) {
        return _createWindowHierarchy(node, 0, max);
    }

    private Window _createWindowHierarchy(Window node, int c, int max) {

        final ScalableContentPane scaledContent = new ScalableContentPane();
        scaledContent.setStyle("-fx-border-color: rgb(255,255,255);\n"
                + "-fx-background-color: rgba(0,0,0,0.4);"
                + "-fx-padding: 10;"
                + "-fx-border-radius: 3;"
                + "-fx-background-radius: 3;");
//                        + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0.0, 0.0);");

        scaledContent.setPrefSize(100, 100);

        if (c == max) {
            Button btn = new Button("Test Button");
            btn.setMinWidth(400);
            scaledContent.getContentPane().getChildren().add(btn);
        }

        ZoomableContentPane zoomContent = new ZoomableContentPane();
        zoomContent.getChildren().add(scaledContent);
        
        OptimizableContentPane optContent = new OptimizableContentPane();
        
        optContent.getChildren().add(zoomContent);

        zoomContent.setZoomedWidth(500);
        zoomContent.setZoomedHeight(500);

        node.setContentPane(optContent);

        if (c >= max) {
            return node;
        }

        c++;

        Window inner1 = new Window("L" + c);

        inner1.setLayoutX(30);
        inner1.setLayoutY(10);

        inner1.setPrefWidth(160 + (max - c) * 30);
        inner1.setPrefHeight(160);

        _createWindowHierarchy(inner1, c, max);

        scaledContent.getContentPane().getChildren().add(inner1);


        Window inner2 = new Window("L" + c);

        inner2.setLayoutX(30 + inner1.getPrefWidth() + 10);
        inner2.setLayoutY(10);

        inner2.setPrefWidth(160 + (max - c) * 30);
        inner2.setPrefHeight(160);

        _createWindowHierarchy(inner2, c, max);

        scaledContent.getContentPane().getChildren().add(inner2);

        return node;
    }
}
