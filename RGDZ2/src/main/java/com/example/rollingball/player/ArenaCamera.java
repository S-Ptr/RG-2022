package com.example.rollingball.player;

import com.example.rollingball.Utilities;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ArenaCamera extends PerspectiveCamera {
    private Translate translate;
    private Rotate rotateX;
    private Rotate rotateY;
    private double x, y;
    private final double STEP = 50;

    public ArenaCamera(boolean fixedEyeAtZero, double z, double xAngle,  double farClip) {
        super(fixedEyeAtZero);

        this.translate = new Translate(0, 0, z);
        this.rotateX = new Rotate(xAngle, Rotate.X_AXIS);
        this.rotateY = new Rotate(0, Rotate.Y_AXIS);


        super.getTransforms().addAll(
                this.rotateY,
                this.rotateX,
                this.translate
        );

        super.getTransforms().setAll(this.rotateX, this.rotateY, this.translate);
        super.setFarClip(farClip);
    }

    public void handleMouseEvent(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                this.x = event.getSceneX();
                this.y = event.getSceneY();
            } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {

                double dx = event.getSceneX() - this.x;
                double dy = event.getSceneY() - this.y;

                this.x = event.getSceneX();
                this.y = event.getSceneY();

                this.rotateX.setAngle(Utilities.clamp(this.rotateX.getAngle() + dy * 0.2,270,359.9999));
                this.rotateY.setAngle(Utilities.clamp(this.rotateY.getAngle() - dx * 0.2,-90,90));

            }
        }
    }

    public void handleScrollEvent(ScrollEvent event) {

        double y = event.getDeltaY();
        double step = STEP * (y > 0 ? 1 : -1);
        this.translate.setZ(this.translate.getZ() + step);
    }


}

