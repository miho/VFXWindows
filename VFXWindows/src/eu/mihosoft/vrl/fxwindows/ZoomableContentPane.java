/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxwindows;

import javafx.animation.ScaleTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ZoomableContentPane extends StackPane {

    private boolean zoomed;
    
//    private double zoomedWidth = 500;
//    private double zoomedHeight = 500;
    
    private DoubleProperty zoomedWidthProperty = new SimpleDoubleProperty();
    private DoubleProperty zoomedHeightProperty = new SimpleDoubleProperty();

    public ZoomableContentPane() {

        final Pane scaledContent = this;

        scaledContent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                ScaleTransition st =
                        new ScaleTransition(
                        Duration.seconds(0.2), scaledContent);
                
                double targetZoom = 1.0;
                if (zoomed) {
                    
                    //
                } else {
                    double targetZoomX = getZoomedWidth() / getBoundsInLocal().getWidth();
                    double targetZoomY = getZoomedHeight()/getBoundsInLocal().getHeight();
                    
                    targetZoom = Math.min(targetZoomX, targetZoomY);
                }
                
                st.setFromX(scaledContent.getScaleX());
                    st.setFromY(scaledContent.getScaleY());
                    st.setToX(targetZoom);
                    st.setToY(targetZoom);

                zoomed = !zoomed;

                st.play();
            }
        });
    }

    /**
     * @return the zoomedWidth
     */
    public double getZoomedWidth() {
        return zoomedWidthProperty.get();
    }

    /**
     * @param zoomedWidth the zoomedWidth to set
     */
    public void setZoomedWidth(double zoomedWidth) {
        this.zoomedWidthProperty.set(zoomedWidth);
    }

    /**
     * @return the zoomedHeight
     */
    public double getZoomedHeight() {
        return zoomedHeightProperty.get();
    }

    /**
     * @param zoomedHeight the zoomedHeight to set
     */
    public void setZoomedHeight(double zoomedHeight) {
        this.zoomedHeightProperty.set(zoomedHeight);
    }
    
    public DoubleProperty zoomedWidthProperty() {
        return zoomedWidthProperty;
    }
    
    public DoubleProperty zoomedHeightProperty() {
        return zoomedHeightProperty;
    }
}
