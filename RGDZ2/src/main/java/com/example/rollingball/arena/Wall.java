package com.example.rollingball.arena;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

public class Wall extends Box {

    public Wall(Translate position, double length){
        super(10, 100, length);
        this.setMaterial(new PhongMaterial(Color.BROWN));
        this.getTransforms().add(position);
    }

    public Wall(Translate position, double length, boolean horizontal){
        super(length, 100, 10);
        this.setMaterial(new PhongMaterial(Color.BROWN));
        this.getTransforms().add(position);
    }

}
