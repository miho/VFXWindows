/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
        
        autosize();
    }
    
    @Override
    protected void layoutChildren() {
        
        super.layoutChildren();
        
        titleBar.relocate(0, 0);
        double titleBarWidth = titleBar.computePrefWidth(0);
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

    @Override
    protected double computePrefWidth(double d) {
        
        double result = super.computePrefWidth(d);
        result = Math.max(result, titleBar.computePrefWidth(0));
        
        System.out.println("W: " + result);

        return result;
    }
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
        getChildren().add(leftIconPane);
        getChildren().add(label);
        HBox.setHgrow(label, Priority.ALWAYS);
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
        leftIconPane.getChildren().add(n);
    }
    

    @Override
    protected double computePrefWidth(double d) {
        double result = getPrefWidth() + leftIconPane.getPrefWidth() + label.getBoundsInLocal().getWidth();

        result = Math.max(result, super.computePrefWidth(d));
        
//        System.out.println("T: " + result);

        return result;
    }
}
