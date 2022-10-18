package com.example.golfer.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class Surface extends Rectangle {
    private double dampFactor;
    Translate position;

    public Surface(double dampFactor, Translate position, Color color, double size){
        super(size,size, color);
        this.dampFactor = dampFactor;
        this.position = position;
        super.getTransforms().addAll(position);

    }

    public boolean handleOverlap(Ball ball){
        return ball.getBoundsInParent().intersects(this.getBoundsInParent());
        /*double ballX = ball.getBoundsInParent().getCenterX();
        double ballY = ball.getBoundsInParent().getCenterY();
        boolean isXInRect = ballX < this.getBoundsInParent().getX()+this.getBoundsInParent().getWidth()
                && ballX > this.getBoundsInParent().getCenterX();
        boolean isYInRect = ballY < this.getBoundsInParent().getCenterY()+this.getBoundsInParent().getHeight()
                && ballY > this.getBoundsInParent().getCenterY();
        return isXInRect && isYInRect;*/
    }

    public double getDampFactor(){
        return dampFactor;
    }
}
