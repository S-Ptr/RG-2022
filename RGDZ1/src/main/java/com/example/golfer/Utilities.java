package com.example.golfer;

import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Utilities {
	
	public static double clamp ( double value, double min, double max ) {
		double result = value;
		
		if ( result < min ) {
			result = min;
		}
		if ( result > max ) {
			result = max;
		}
		
		return result;
	}

	public static Path createStar(double centerX, double centerY,
								   double innerRadius, double outerRadius, int numRays,
								   double startAngleRad)
	{
		Path path = new Path();
		double deltaAngleRad = Math.PI / numRays;
		for (int i = 0; i < numRays * 2; i++)
		{
			double angleRad = startAngleRad + i * deltaAngleRad;
			double ca = Math.cos(angleRad);
			double sa = Math.sin(angleRad);
			double relX = ca;
			double relY = sa;
			if ((i & 1) == 0)
			{
				relX *= outerRadius;
				relY *= outerRadius;
			}
			else
			{
				relX *= innerRadius;
				relY *= innerRadius;
			}
			if (i == 0)
			{
				path.getElements().add(new MoveTo(centerX + relX, centerY + relY));
			}
			else
			{
				path.getElements().add(new LineTo(centerX + relX, centerY + relY));
			}
		}
		path.getElements().add(new ClosePath());
		return path;
	}

	public static double gammaRandom(){
		return 0;
	}
}
