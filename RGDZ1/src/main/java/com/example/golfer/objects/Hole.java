package com.example.golfer.objects;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public class Hole extends Circle {

	public int getPoints() {
		return points;
	}

	int points;
	
	public Hole ( double radius, Translate position, Color color, int points ) {
		super ( radius, color );
		Stop[] stops = new Stop[] {new Stop(0, Color.BLACK), new Stop(0.5, color)};
		RadialGradient rg = new RadialGradient(0,0,0.5,0.5,1,true, CycleMethod.NO_CYCLE, stops);
		super.setFill(rg);
		super.getTransforms ( ).addAll ( position );
		this.points = points;

	}
	
	public boolean handleCollision ( Ball ball, double maxSpeed ) {
		Bounds ballBounds = ball.getBoundsInParent ( );
		
		double ballX      = ballBounds.getCenterX ( );
		double ballY      = ballBounds.getCenterY ( );
		double ballRadius = ball.getRadius ( );
		Point2D ballSpeed = ball.getSpeed();
		if(Math.sqrt(ballSpeed.getX()*ballSpeed.getX() + ballSpeed.getY()*ballSpeed.getY()) > maxSpeed) return false;
		
		Bounds holeBounds = super.getBoundsInParent ( );
		
		double holeX      = holeBounds.getCenterX ( );
		double holeY      = holeBounds.getCenterY ( );
		double holeRadius = super.getRadius ( );
		
		double distanceX = holeX - ballX;
		double distanceY = holeY - ballY;
		
		double distanceSquared = distanceX * distanceX + distanceY * distanceY;
		
		boolean result = distanceSquared < ( holeRadius * holeRadius );
		
		return result;
	};
}
