package com.example.golfer.objects;

import com.example.golfer.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Player extends Group {
	
	private double width;
	private double height;
	private Translate position;
	private Rotate rotate;
	private double maxSpeed;

	public Player ( double width, double height, Translate position, Cannon cannon) {
		this.width = width;
		this.height = height;
		this.position = position;
		double narrowing = width/4;
		Path cannon2;
		Circle base;
		Path star;
		switch(cannon){
			default:
			case BLUE:

				cannon2 = new Path(new MoveTo(0,0),
						new LineTo(-narrowing*2,height),
						new LineTo(width+narrowing*2, height),
						new LineTo(width,0),
						new ClosePath());
				cannon2.setFill(Color.LIGHTBLUE);
				base = new Circle((width)/2, height, width);
				base.setFill(Color.ORANGE);

				super.getChildren ( ).add ( cannon2 );
				super.getChildren ( ).add ( base );
				this.maxSpeed = 1000;
				break;
			case RED:
				double squeeze = 0.6;
				cannon2 = new Path(new MoveTo(0,0),
						new LineTo(-narrowing,height*squeeze),
						new LineTo(-narrowing*2,height*squeeze),
						new LineTo(-narrowing*2,height),
						new LineTo(width+narrowing*2, height),
						new LineTo(width+narrowing*2,height*squeeze),
						new LineTo(width+narrowing,height*squeeze),
						new LineTo(width,0),
						new ClosePath());
				cannon2.setFill(Color.CRIMSON);
				base = new Circle((width)/2, height, width);
				base.setFill(Color.GOLDENROD);
				star = Utilities.createStar(width/2, height, width/3,2*width / 3,5,Math.toRadians(-18));
				star.setFill(Color.CRIMSON);

				super.getChildren ( ).add ( cannon2 );
				super.getChildren ( ).add ( base );
				super.getChildren().add(star);
				this.maxSpeed= 2000;
				break;
			case BLACK:
				ArcTo right = new ArcTo(-width*2, height, 0, width, 0, false, false);
				ArcTo left = new ArcTo(width*2, height, 0, -narrowing*2,height, false, false);
				cannon2 = new Path(new MoveTo(0,0),
						left, new
						LineTo(width+narrowing*2, height),
						right,
						new ClosePath());
				cannon2.setFill(Color.BLACK);
				cannon2.setStroke(Color.rgb(80,80,80));
				cannon2.setStrokeLineCap(StrokeLineCap.ROUND);
				base = new Circle((width)/2, height, width);
				base.setFill(Color.NAVY);
				base.setStroke(Color.NAVY);
				star = Utilities.createStar(width/2, height, 2*width/3 -2,width-2,12,Math.toRadians(-18));
				star.setFill(Color.GOLD);
				star.setStroke(Color.TRANSPARENT);
				super.getChildren ( ).add ( cannon2 );
				super.getChildren ( ).add ( base );
				super.getChildren ( ).add ( star );
				this.maxSpeed = 2500;
				break;
			case WHITE:
				cannon2 = new Path(new MoveTo(0,0),
						new LineTo(-narrowing*2,height),
						new LineTo(width+narrowing*2, height),
						new LineTo(width,0),
						new LineTo(3*width/4,0),
						new LineTo(3*width / 4,height-2),
						new LineTo(width/4,height-2),
						new LineTo(width/4,0),
						new ClosePath());
				cannon2.setFill(Color.ANTIQUEWHITE);
				cannon2.setStroke(Color.AQUA);
				base = new Circle((width)/2, height, width);
				base.setFill(Color.AQUAMARINE);
				base.setStroke(Color.AQUA);
				star = Utilities.createStar(width/2, height, 2*width/3 -2,width,4,0);
				star.setFill(Color.GOLD);
				star.setStroke(Color.TRANSPARENT);
				super.getChildren ( ).add ( cannon2 );
				super.getChildren ( ).add ( base );
				super.getChildren ( ).add ( star );
				this.maxSpeed = 3000;
				break;
		}

		
		this.rotate = new Rotate ( );
		
		super.getTransforms ( ).addAll (
				position,
				new Translate ( width / 2, height ),
				rotate,
				new Translate ( -width / 2, -height )
		);
	}
	
	public void handleMouseMoved ( MouseEvent mouseEvent, double minAngleOffset, double maxAngleOffset ) {
		Bounds bounds = super.getBoundsInParent ( );
		
		double startX = bounds.getCenterX ( );
		double startY = bounds.getMaxY ( );
		
		double endX = mouseEvent.getX ( );
		double endY = mouseEvent.getY ( );
		
		Point2D direction     = new Point2D ( endX - startX, endY - startY ).normalize ( );
		Point2D startPosition = new Point2D ( 0, -1 );
		
		double angle = ( endX > startX ? 1 : -1 ) * direction.angle ( startPosition );
		
		this.rotate.setAngle ( Utilities.clamp ( angle, minAngleOffset, maxAngleOffset ) );
	}
	
	public Translate getBallPosition ( ) {
		double startX = this.position.getX ( ) + this.width / 2;
		double startY = this.position.getY ( ) + this.height;
		
		double x = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double y = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Translate result = new Translate ( x, y );
		
		return result;
	}
	
	public Point2D getSpeed ( ) {
		double startX = this.position.getX ( ) + this.width / 2;
		double startY = this.position.getY ( ) + this.height;
		
		double endX = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double endY = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Point2D result = new Point2D ( endX - startX, endY - startY );
		
		return result.normalize ( );
	}

	public double getMaxSpeed(){
		return this.maxSpeed;
	}
}
