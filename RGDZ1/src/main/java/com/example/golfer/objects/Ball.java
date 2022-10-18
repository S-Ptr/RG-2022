package com.example.golfer.objects;

import com.example.golfer.Utilities;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

import java.util.ArrayList;

public class Ball extends Circle {
	private Translate position;
	private Point2D speed;
	
	public Ball ( double radius, Translate position, Point2D speed ) {
		super ( radius, Color.RED );
		this.position = position;
		this.speed = speed;
		
		super.getTransforms ( ).addAll ( this.position );
	}

	public void dontMove(){
		speed = new Point2D(0, 0);
	}

	public Point2D getSpeed() {
		return speed;
	}

	public boolean update (double ds, double left, double right, double top, double bottom, double dampFactor, double minBallSpeed, ArrayList<Barrier> obstacles) {
		boolean result = false;
		
		double newX = this.position.getX ( ) + this.speed.getX ( ) * ds;
		double newY = this.position.getY ( ) + this.speed.getY ( ) * ds;
		
		double radius = super.getRadius ( );
		
		double minX = left + radius;
		double maxX = right - radius;
		double minY = top + radius;
		double maxY = bottom - radius;

		for(Barrier barrier: obstacles){
			if(this.getBoundsInParent().getMaxX()+this.speed.getX()*ds > barrier.getBoundsInParent().getMinX() && this.getBoundsInParent().getMinX()+this.speed.getX()*ds < barrier.getBoundsInParent().getMaxX()){
				if(this.getBoundsInParent().getMaxY()>barrier.getBoundsInParent().getMinY() && this.getBoundsInParent().getMinY()<barrier.getBoundsInParent().getMaxY()){
					this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
					newX = this.position.getX();
				}
			}
			if(this.getBoundsInParent().getMaxY()+this.speed.getY()*ds > barrier.getBoundsInParent().getMinY() && this.getBoundsInParent().getMinY()+this.speed.getY()*ds < barrier.getBoundsInParent().getMaxY()){
				if(this.getBoundsInParent().getMaxX()>barrier.getBoundsInParent().getMinX() && this.getBoundsInParent().getMinX()<barrier.getBoundsInParent().getMaxX()){
					this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
					newY = this.position.getY();
				}
			}

		}
		
		this.position.setX ( Utilities.clamp ( newX, minX, maxX ) );
		this.position.setY ( Utilities.clamp ( newY, minY, maxY ) );


	
		if ( newX < minX || newX > maxX ) {
			this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
		}
		
		if ( newY < minY || newY > maxY ) {
			this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
		}


		
		this.speed = this.speed.multiply ( dampFactor );
		
		double ballSpeed = this.speed.magnitude ( );
		//System.out.println(ballSpeed);
		
		if ( ballSpeed < minBallSpeed ) {
			result = true;
		}
		
		return result;
	}

	public void setLocation(double x, double y){
		this.position.setX(x);
		this.position.setY(y);
	}
}
