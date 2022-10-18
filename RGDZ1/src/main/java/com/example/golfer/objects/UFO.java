package com.example.golfer.objects;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;


public class UFO extends Group {

    private Ellipse body;
    private Ellipse head;

    private Timeline patrolAnim;
    private Translate position;

    public double time;

    Text text;
    public UFO(){
        body = new Ellipse(0,0, 20,12);
        head = new Ellipse(0,-10, 10,7);
        body.setFill(Color.DARKSLATEGRAY);
        head.setFill(Color.MEDIUMSEAGREEN);
        head.setStroke(Color.BLACK);
        body.setStroke(Color.BLACK);
        this.getChildren().add(body);
        this.getChildren().add(head);
        position = new Translate(-30, 0);
        this.getTransforms().add(position);
    }

    public void patrol(double fromHeight, double fromWidth, double toWidth, double time){
        this.time = time;
        this.position.setY(fromHeight);
        patrolAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(this.translateXProperty(), fromWidth)),
                new KeyFrame(Duration.seconds(time), new KeyValue(this.translateXProperty(),toWidth))
        );
        patrolAnim.play();
    }



}
