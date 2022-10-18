package com.example.rollingball.arena;

import com.example.rollingball.Main;
import javafx.animation.*;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Coin extends Cylinder {


    TranslateTransition floaty;

    Timeline spinny;

    public Coin(double radius, Translate position){
        super(radius, 5);
        this.setMaterial(new PhongMaterial(Color.GOLD));
        this.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));


        floaty = new TranslateTransition(Duration.seconds(1), this);
        floaty.setToY(-50);
        floaty.setAutoReverse(true);
        floaty.setCycleCount(Timeline.INDEFINITE);
        Rotate spin = new Rotate(0,position.getX(),position.getY(),position.getZ(),
                new Point3D(1,0,0)
                );
        this.getTransforms().add(spin);
        spinny = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(spin.angleProperty(),0)),
                new KeyFrame(Duration.seconds(5), new KeyValue(spin.angleProperty(), 360))
        );
        spinny.setCycleCount(Timeline.INDEFINITE);
        floaty.play();
        spinny.play();
        this.getTransforms().addAll (position);
    }

    public boolean handleCollision(Ball ball){
        return ball.getBoundsInParent().intersects(this.getBoundsInParent());
    }

}
