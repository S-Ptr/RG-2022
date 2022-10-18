package com.example.rollingball.player;

import com.example.rollingball.arena.Ball;
import com.example.rollingball.timer.Updatable;
import javafx.geometry.Bounds;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class BallCamera extends PerspectiveCamera implements Updatable {
    Ball ball;
    Translate position;
    public BallCamera(Ball ball) {
        super(true);
        this.setNearClip(0);
        this.ball = ball;
        position = new Translate(ball.getBoundsInParent().getCenterX(), ball.getBoundsInParent().getCenterY()-2000, ball.getBoundsInParent().getCenterZ());
        this.getTransforms().add(position);
        this.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
        //this.getTransforms().add(new Rotate(0, Rotate.Y_AXIS));
    }

    @Override
    public void update(double deltaSeconds) {
        Bounds bounds = this.ball.getBoundsInParent();
        this.position.setX(bounds.getCenterX());
        this.position.setY(bounds.getCenterY()-2000);
        this.position.setZ(bounds.getCenterZ());

    }

    public void setBall(Ball ball){
        this.ball = ball;
        Bounds bounds = this.ball.getBoundsInParent();
        this.position.setX(bounds.getCenterX());
        this.position.setY(bounds.getCenterY()-2000);
        this.position.setZ(bounds.getCenterZ());
    }
}
