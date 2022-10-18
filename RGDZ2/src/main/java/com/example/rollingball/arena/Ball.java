package com.example.rollingball.arena;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.effect.Light;
import javafx.scene.paint.Material;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

import java.util.Collection;

public class Ball extends Sphere {
	private Translate position;
	private Point3D speed;

	private double maxSpeed;
	
	public Ball ( double radius, Material material, Translate position , double maxSpeed) {
		super ( radius );
		super.setMaterial ( material );
		
		this.position = position;
		
		super.getTransforms ( ).add ( this.position );
		
		this.speed = new Point3D ( 0, 0, 0 );

		this.maxSpeed = maxSpeed;
	}
	
	public boolean update (
			double deltaSeconds,
			double top,
			double bottom,
			double left,
			double right,
			double xAngle,
			double zAngle,
			double maxAngleOffset,
			double maxAcceleration,
			double damp,
			Collection<Obstacle> obstacles,
			Collection<Wall> walls
	) {
		double newPositionX = this.position.getX ( ) + this.speed.getX ( ) * deltaSeconds;
		double newPositionZ = this.position.getZ ( ) + this.speed.getZ ( ) * deltaSeconds;

		//**************OBSTACLES*************
		for(Obstacle obstacle: obstacles){
			Bounds bounds = obstacle.getBoundsInParent();
			double distance = Math.sqrt((bounds.getCenterX()-newPositionX)*(bounds.getCenterX()-newPositionX) + (bounds.getCenterZ()-newPositionZ)*(bounds.getCenterZ()-newPositionZ));
			//System.out.println(distance);
			if(distance<=(obstacle.getRadius()+this.getRadius())){
				System.out.println("old speed: "+this.speed);
				//jednacina prave y=kx+n
				// k = (y2-y1)/(x2-x1); n = y1-kx1;
				double k = (bounds.getCenterZ() - position.getZ())/(bounds.getCenterX() - position.getX());
				double n = position.getZ() - k*position.getX();
				double k_norm = -(1/k);

				double norm_x = (bounds.getCenterX() - position.getX())/distance;
				double norm_z = (bounds.getCenterZ() - position.getZ())/distance;

				double tan_x = norm_z;
				double tan_z = -norm_x;

				double scalarTan = this.speed.getX()*tan_x + this.speed.getZ()*tan_z;

				//double scalarNorm = this.speed.getX()*norm_x + this.speed.getZ()*norm_z;

				//double momentum = ()

				//this.speed = new Point3D(tan_x*scalarTan + norm_x,0,tan_z*scalarTan + norm_x);

				double charge = 1;
				if(obstacle instanceof SpecialObstacle){
					charge = ((SpecialObstacle) obstacle).getChargeFactor();
				}

				double vx=this.speed.getX()*charge;
				double vz=this.speed.getZ()*charge;
				this.speed = new Point3D(vx-1.5*(norm_x*vx+norm_z*vz)*norm_x, 0, vz-1.5*(norm_x*vx+norm_z*vz)*norm_z);


				System.out.println("new speed: "+this.speed);

				newPositionX = this.position.getX ( ) + this.speed.getX ( ) * deltaSeconds;
				newPositionZ = this.position.getZ ( ) + this.speed.getZ ( ) * deltaSeconds;
				this.position.setX ( newPositionX );
				this.position.setZ ( newPositionZ );

				Point3D currentPosition = new Point3D(this.position.getX(), this.position.getY(), this.position.getZ());
				Point3D speedPosition = currentPosition.add(speed);
				return false;

			}
		}

		//*************WALLS*************

		for(Wall wall: walls){
			if(this.getBoundsInParent().getMaxX()+this.speed.getX()*deltaSeconds > wall.getBoundsInParent().getMinX() && this.getBoundsInParent().getMinX() + this.speed.getX()*deltaSeconds < wall.getBoundsInParent().getMaxX()){
				if(this.getBoundsInParent().getMaxZ()>wall.getBoundsInParent().getMinZ() && this.getBoundsInParent().getMinZ()<wall.getBoundsInParent().getMaxZ()){
					this.speed = new Point3D( -this.speed.getX ( ), 0,this.speed.getZ ( ) );
					newPositionX = this.position.getX();
				}
			}
			if(this.getBoundsInParent().getMaxZ()+this.speed.getZ()*deltaSeconds > wall.getBoundsInParent().getMinZ() && this.getBoundsInParent().getMinZ()+this.speed.getZ()*deltaSeconds < wall.getBoundsInParent().getMaxZ()){
				if(this.getBoundsInParent().getMaxX()>wall.getBoundsInParent().getMinX() && this.getBoundsInParent().getMinX()<wall.getBoundsInParent().getMaxX()){
					this.speed = new Point3D ( this.speed.getX ( ),0, -this.speed.getZ ( ) );
					newPositionZ = this.position.getZ();
				}
			}
		}
		this.position.setX ( newPositionX );
		this.position.setZ ( newPositionZ );
		
		double accelerationX = maxSpeed * zAngle / maxAngleOffset;
		double accelerationZ = -maxSpeed * xAngle / maxAngleOffset;
		
		double newSpeedX = ( this.speed.getX ( ) + accelerationX * deltaSeconds ) * damp;
		double newSpeedZ = ( this.speed.getZ ( ) + accelerationZ * deltaSeconds ) * damp;
		
		this.speed = new Point3D ( newSpeedX, 0, newSpeedZ );
		
		boolean xOutOfBounds = ( newPositionX > right ) || ( newPositionX < left );
		boolean zOutOfBounds = ( newPositionZ > top ) || ( newPositionZ < bottom );
		
		return xOutOfBounds || zOutOfBounds;
	}
	
}
