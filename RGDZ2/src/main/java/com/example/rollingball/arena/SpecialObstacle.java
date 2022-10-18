package com.example.rollingball.arena;

import javafx.scene.image.Image;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Translate;

public class SpecialObstacle extends Obstacle{

    double chargeFactor;

    public SpecialObstacle(Translate position, double factor) {
        super(position);
        this.chargeFactor = factor;
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image("superObstacle.jpg"));
        this.setMaterial(material);
    }

    public double getChargeFactor(){
        return chargeFactor;
    }
}
