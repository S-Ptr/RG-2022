package com.example.golfer.objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Translate;



public class Teleporter extends Group {
    private static final double size = 30;
    private boolean teleporterUsed;

    Rectangle firstPad;
    Rectangle secondPad;

    public Teleporter(Color color, Translate position1, Translate position2){
        teleporterUsed = false;
        final double outlineFactor = 1.4;
        final double outlineWidth = 3;

        firstPad = new Rectangle(position1.getX() -size/2, position1.getY() -size/2, size, size);
        firstPad.setFill(color);
        firstPad.setStroke(color);
        Rectangle firstPadOutline = new Rectangle(position1.getX() -outlineFactor*size/2, position1.getY() -outlineFactor*size/2, size*outlineFactor, size*outlineFactor);
        firstPadOutline.setFill(Color.TRANSPARENT);
        firstPadOutline.setStroke(color);
        firstPadOutline.setStrokeWidth(outlineWidth);
        firstPadOutline.setStrokeLineCap(StrokeLineCap.ROUND);
        Line firstPadLine1 = new Line(position1.getX() -outlineFactor*size/2, position1.getY() -outlineFactor*size/2, position1.getX() +outlineFactor*size/2, position1.getY() +outlineFactor*size/2);
        firstPadLine1.setStroke(color);
        firstPadLine1.setStrokeWidth(outlineWidth);
        Line firstPadLine2 = new Line(position1.getX() +outlineFactor*size/2, position1.getY() -outlineFactor*size/2, position1.getX() -outlineFactor*size/2, position1.getY() +outlineFactor*size/2);
        firstPadLine2.setStroke(color);
        firstPadLine2.setStrokeWidth(outlineWidth);
        this.getChildren().add(firstPadLine1);
        this.getChildren().add(firstPadLine2);
        this.getChildren().add(firstPadOutline);
        this.getChildren().add(firstPad);


        secondPad = new Rectangle(position2.getX() - size / 2, position2.getY() - size / 2, size, size);
        secondPad.setFill(color);
        secondPad.setStroke(color);
        Rectangle secondPadOutline = new Rectangle(position2.getX() -outlineFactor*size/2, position2.getY() -outlineFactor*size/2, size*outlineFactor, size*outlineFactor);
        secondPadOutline.setFill(Color.TRANSPARENT);
        secondPadOutline.setStroke(color);
        secondPadOutline.setStrokeWidth(outlineWidth);
        secondPadOutline.setStrokeLineCap(StrokeLineCap.ROUND);
        Line secondPadLine1 = new Line(position2.getX() -outlineFactor*size/2, position2.getY() -outlineFactor*size/2, position2.getX() +outlineFactor*size/2, position2.getY() +outlineFactor*size/2);
        secondPadLine1.setStroke(color);
        secondPadLine1.setStrokeWidth(outlineWidth);
        Line secondPadLine2 = new Line(position2.getX() +outlineFactor*size/2, position2.getY() -outlineFactor*size/2, position2.getX() -outlineFactor*size/2, position2.getY() +outlineFactor*size/2);
        secondPadLine2.setStroke(color);
        secondPadLine2.setStrokeWidth(outlineWidth);
        this.getChildren().add(secondPadLine1);
        this.getChildren().add(secondPadLine2);
        this.getChildren().add(secondPadOutline);
        this.getChildren().add(secondPad);


    }

    public void handleTeleport(Ball ball ){
        if(teleporterUsed){
            if(!(ball.getBoundsInParent().intersects(firstPad.getBoundsInParent()) || ball.getBoundsInParent().intersects(secondPad.getBoundsInParent()))){
                teleporterUsed = false;
                firstPad.setStroke(firstPad.getFill());
                secondPad.setStroke(firstPad.getFill());
            }else return;
        }
        if(ball.getBoundsInParent().intersects(firstPad.getBoundsInParent())){
            teleporterUsed = true;
            ball.setLocation(secondPad.getBoundsInParent().getCenterX(), secondPad.getBoundsInParent().getCenterY());
            firstPad.setStroke(Color.WHITE);
            secondPad.setStroke(Color.WHITE);
            return;


        }
        if(ball.getBoundsInParent().intersects(secondPad.getBoundsInParent())){
            teleporterUsed = true;
            ball.setLocation(firstPad.getBoundsInParent().getCenterX(), firstPad.getBoundsInParent().getCenterY());
            firstPad.setStroke(Color.WHITE);
            secondPad.setStroke(Color.WHITE);
            return;
        }

    }

    public boolean isOnPad(double x, double y, double w, double h){
        return firstPad.getBoundsInParent().intersects(x,y,w,h) || secondPad.getBoundsInParent().intersects(x,y,w,h);
    }


}
