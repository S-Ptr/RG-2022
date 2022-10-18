package com.example.rollingball.player;

import com.example.rollingball.arena.Arena;
import com.example.rollingball.timer.Updatable;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Translate;

public class Compass extends Group implements Updatable {

    private Line needle;
    private double maxAngleOffset;
    private Arena arena;
    private double width;

    public Compass(double width, Translate position, Arena arena, double maxAngleOffset) {
        Rectangle backdrop = new Rectangle(width, width);
        this.width = width;
        backdrop.setFill(Color.GREEN);
        backdrop.setStroke(Color.RED);
        backdrop.setStrokeWidth(5);
        needle = new Line();
        needle.setFill(Color.RED);
        needle.setStroke(Color.RED);
        needle.setStrokeWidth(2);
        needle.setStrokeLineCap(StrokeLineCap.ROUND);
        needle.setStartX(width / 2);
        needle.setEndX(width / 2);
        needle.setStartY(width/ 2);
        needle.setEndY(width / 2);
        this.getChildren().addAll(backdrop,needle);
        this.getTransforms().add(position);
        this.maxAngleOffset = maxAngleOffset;
        this.arena = arena;
    }



    @Override
    public void update(double deltaSeconds) {
        double[] angle = this.arena.getAngle();
        this.needle.setEndX(width*(angle[1]/maxAngleOffset +1)/2);
        this.needle.setEndY(width*(angle[0]/maxAngleOffset +1)/2);
    }
}
