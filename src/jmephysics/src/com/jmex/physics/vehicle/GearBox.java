/*
 * Created on 12.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.jmex.physics.vehicle;

import java.io.IOException;

/**
 * @author tommy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */



public class GearBox
{
    // default torque curve
    float gearRatios[] = new float[7];
    NmCurve curve;
    
    float maxRPM;
    float minRPM;
    
    int currentGear = -1;
    
    GearBox(String RPM_Nm_Map, float minrpm, float maxrpm, float minnm, float maxnm) throws IOException
    {
        curve = new NmCurve();
        
        maxRPM = maxrpm;
        minRPM = minrpm;

                
        // init default torque curve - should be read from a picture or txt file
        curve.buildLookUpTable(RPM_Nm_Map, minrpm, maxrpm, minnm, maxnm);
        
        // init default gear ratios
        gearRatios[0]=3.12f; // reverse
        gearRatios[1]=3.36f;
        gearRatios[2]=2.09f;
        gearRatios[3]=1.47f;
        gearRatios[4]=1.10f;
        gearRatios[5]=0.87f;
        gearRatios[6]=0.73f;
    }
    
    float getMinRPM()
    {
        return minRPM;
    }

    float getMaxRPM()
    {
        return maxRPM;
    }
    
    float getGearRatio(int gear)
    {
        if(gear==0)
            return 0;
        
        if(gear==-1)
            gear=0;        
        if(gear>=0 && gear<=6)
            return gearRatios[gear];
        else
            return 0;
    }

    void setGearRatio(int gear, float ratio)
    {
        if(gear>=0 && gear<=6)
            gearRatios[gear]=ratio;        
    }
    
    float getTorque(int rpm)
    {
        // in 100rpm steps
        //System.out.println("torque/rpm: "+curve.covertRPM2Nm(rpm)+"/"+rpm);
        return 0.005f*curve.covertRPM2Nm(rpm); // divide by 5000
        /*
        int irpm=(int)Math.round(rpm*0.001f);
        int irpm2=irpm;
        
        irpm*=1000;
       
        irpm2-=irpm;
        if(irpm2>250)
            irpm=500;
        else
            irpm2=0;
        
        
        irpm+=irpm2;
                
        //System.out.println("getTorque: "+irpm);
        if(RPM2Torque.containsKey(Integer.valueOf(irpm)))
        {
            Integer i = (Integer)RPM2Torque.get(Integer.valueOf(irpm));
        

            return i.intValue();
        }
        else
            return 0;
        */
    }
    
    
    
    
    
}
