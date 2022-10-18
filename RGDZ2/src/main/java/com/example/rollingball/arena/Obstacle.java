package com.example.rollingball.arena;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Translate;

public class Obstacle extends Cylinder {

    public Obstacle(Translate position){
        super(50, 200);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image("obstacle.jpg"));
        this.setMaterial(material);
        this.getTransforms().add(position);

    }

}
