package com.example.rollingball.arena;

import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

public class Floodlight extends Group {

    Box lightBox;
    PointLight light;
    Material inactiveMaterial;

    public Floodlight(){
        super();
        double x = 150;
        double y = 100;
        double z = 100;
        lightBox = new Box();
        lightBox.setHeight(y);
        lightBox.setWidth(x);
        lightBox.setDepth(z);
        //lightBox.getTransforms().add(new Translate(-x/2,-y/2,-z/2));
        PhongMaterial boxMaterial = new PhongMaterial();
        boxMaterial.setSelfIlluminationMap(new Image("selfIllumination.png"));
        PhongMaterial tempinactiveMaterial = new PhongMaterial();
        tempinactiveMaterial.setDiffuseColor(Color.GRAY);
        inactiveMaterial = tempinactiveMaterial;
        lightBox.setMaterial(boxMaterial);
        light = new PointLight(Color.WHITE);
        light.getTransforms().add(new Translate(0,0,0));
        this.getChildren().add(lightBox);
        light.setLightOn(true);
        this.getChildren().add(light);

    }

    public void lightSwitch(){
        Material temp = lightBox.getMaterial();
        lightBox.setMaterial(inactiveMaterial);
        inactiveMaterial = temp;
        if(this.getChildren().contains(light)){
            this.getChildren().remove(light);
            //light = null;
        }else{
            //light = new PointLight(Color.WHITE);
            this.getChildren().add(light);
        }
        light.setLightOn(!light.isLightOn());
    }
}
