package com.example.golfer.objects;

import com.example.golfer.Utilities;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Path;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class PowerUp extends Group {
     private static final double size = 15;

    public PowerUpType powerUpType;

    public double timeLeft;
    private Path shape;

    ParallelTransition animation;



    public PowerUp(PowerUpType powerUpType, Color color, Translate position){
        this.powerUpType = powerUpType;
        timeLeft = 20;
        shape = Utilities.createStar(0,0,0.6*size, size, 10,0);
        Stop[] stops = new Stop[] {new Stop(0, Color.WHITE), new Stop(0.2, color)};
        RadialGradient rg = new RadialGradient(0,0,0.5,0.5,1,true, CycleMethod.NO_CYCLE, stops);
        shape.setFill(rg);
        shape.setStroke(Color.TRANSPARENT);

        ScaleTransition scaleAnim = new ScaleTransition(Duration.seconds(0.5));
        scaleAnim.setToX(0.7); scaleAnim.setToY(0.7);
        scaleAnim.setInterpolator(Interpolator.EASE_BOTH);
        scaleAnim.setNode(this.shape);
        scaleAnim.setAutoReverse(true);
        scaleAnim.setCycleCount(Timeline.INDEFINITE);

        TranslateTransition moveAnim = new TranslateTransition(Duration.seconds(0.5));
        moveAnim.setInterpolator(Interpolator.EASE_BOTH);
        moveAnim.setNode(this.shape);
        moveAnim.setToX(position.getX()*0.3); moveAnim.setToY(position.getY()*0.3);
        moveAnim.setAutoReverse(true);
        moveAnim.setCycleCount(Timeline.INDEFINITE);

        this.shape.getTransforms().add(position);
        this.getChildren().add(shape);

        animation = new ParallelTransition(scaleAnim,moveAnim);
        animation.play();
    }

    public Bounds getBounds(){
        return this.shape.getBoundsInParent();
    }

    public static double getSize(){
        return size;
    }


}
