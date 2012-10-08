/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
        final Pane root = new Pane();

        final Scene scene = new Scene(root, 800, 700, Color.rgb(160, 160, 160));

        for (int j = 0; j < 4; j++) {

            final int numNodes = 4; // number of nodes to add
            final double spacing = 30; // spacing between nodes

            // add numNodes instances of DraggableNode to the root pane
            for (int i = 0; i < numNodes; i++) {
                Window node = new Window("W (" + (i+1) + "," + (j+1) + ")");
                
                Button btn = new Button("TestBtn TestBtn TestBtn");
                
                btn.setMinWidth(400);
                
//                StackPane.setAlignment(btn, Pos.CENTER);
                
                node.setContentPane(new RootPane());
                
                Window innerWindow =  new Window("---------- Subwindow 1 ----------");
                Window innerInnerWindow = new Window("---------- Subwindow 2 ----------");
                
                innerWindow.getContentPane().getChildren().add(innerInnerWindow);

                node.getContentPane().getChildren().add(innerWindow);
                
                innerInnerWindow.setLayoutX(600);
                innerInnerWindow.setLayoutY(600);
                
                innerWindow.setLayoutX(600);
                innerWindow.setLayoutY(600);
                
//                WindowUtil.makeDraggable(node);
//                WindowUtil.makeResizable(node);
                node.setPrefSize(240, 120);
                node.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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
