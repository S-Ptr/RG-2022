package com.example.golfer.objects;

import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class Barrier extends Rectangle {

    public Barrier(Translate position, double width, double height){
        super(width, height);
        this.getTransforms().add(position);
    }
}
