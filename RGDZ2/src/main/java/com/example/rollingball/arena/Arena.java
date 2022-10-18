package com.example.rollingball.arena;

import com.example.rollingball.Utilities;
import com.example.rollingball.timer.Updatable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;

public class Arena extends Group implements Updatable {

	private static final double elasticity = 4;
	
	private Rotate rotateX;
	private Rotate rotateZ;
	
	public Arena ( Node... children ) {
		super ( children );
		
		this.rotateX = new Rotate ( 0, Rotate.X_AXIS );
		this.rotateZ = new Rotate ( 0, Rotate.Z_AXIS );
		
		super.getTransforms ( ).addAll (
				this.rotateX,
				this.rotateZ
		);
	}
	
	public void handleKeyEvent ( KeyEvent event, double maxOffset, boolean isGameOver ) {
		if(isGameOver) return;
		double dxAngle = 0;
		double dzAngle = 0;
		
		if ( event.getCode ( ).equals ( KeyCode.UP ) ) {
			dxAngle = -1;
		} else if ( event.getCode ( ).equals ( KeyCode.DOWN ) ) {
			dxAngle = 1;
		} else if ( event.getCode ( ).equals ( KeyCode.LEFT ) ) {
			dzAngle = -1;
		} else if ( event.getCode ( ).equals ( KeyCode.RIGHT ) ) {
			dzAngle = 1;
		}
		
		double newXAngle = Utilities.clamp ( this.rotateX.getAngle ( ) + dxAngle, -maxOffset, maxOffset );
		double newZAngle = Utilities.clamp ( this.rotateZ.getAngle ( ) + dzAngle, -maxOffset, maxOffset );
		
		this.rotateX.setAngle ( newXAngle );
		this.rotateZ.setAngle ( newZAngle );
	}



	public double getXAngle ( ) {
		return this.rotateX.getAngle ( );
	}
	
	public double getZAngle ( ) {
		return this.rotateZ.getAngle ( );
	}

	@Override
	public void update(double deltaSeconds) {
		double newXAngle = Math.abs(this.rotateX.getAngle()) - elasticity*deltaSeconds;
		if(newXAngle<0){
			newXAngle=0;
		}
		double newZAngle = Math.abs(this.rotateZ.getAngle()) - elasticity*deltaSeconds;
		if(newZAngle<0){
			newZAngle=0;
		}
		this.rotateX.setAngle((getXAngle()<0) ? (-newXAngle) : (newXAngle));
		this.rotateZ.setAngle((getZAngle()<0) ? (-newZAngle) : (newZAngle));
	}

	public void resetTilt(){
		this.rotateX.setAngle(0);
		this.rotateZ.setAngle(0);
	}

	public double[] getAngle(){
		return new double[]{this.rotateX.getAngle(), this.rotateZ.getAngle()};
	}
}
