/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.physics;
import ec.gp.*;
import javax.vecmath.Vector3f;

/* 
 * RegressionData.java
 * 
 * Created: Wed Nov  3 18:32:13 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class PhysicsData extends GPData
    {
    // return value
    public double x;

    public void copyTo(final GPData gpd) { 
        ((PhysicsData)gpd).x = x;
        return;
    }
}
