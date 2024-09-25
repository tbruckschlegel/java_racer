/*
 * Created on 12.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.jmex.physics.vehicle;


/**
 * @author tommy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */



public class Clutch
{
    private float currentPercent; // current position
    private float clutchTime; // how long it takes, to close the clutch
    
    Clutch()
    {
        this.currentPercent=0;
        this.clutchTime=0.25f;
    }
    
    float getClutchTime()
    {
        return clutchTime;
    }
    
    void addToClutch(float percent)
    {  
        this.currentPercent += percent;
        
        if(this.currentPercent>1)
            this.currentPercent=1;
        else
            if(this.currentPercent<0)
                this.currentPercent=0;

    }

    void setclutchTime(float t)
    {
        clutchTime=t;       
    }
    
    boolean isOpen()
    {
        return currentPercent < 1.0f;
    }

    void openCutch()
    {
        currentPercent=0;
    }

    void closeCutch()
    {
        currentPercent=1;
    }
    
    void update(float tpf)
    {        
        if(currentPercent<1.0f)
        {                         
            currentPercent+=tpf*clutchTime;  
            if(currentPercent>1.0f)
                currentPercent=1.0f;
        }
    }

    float getPosition()
    {
        return currentPercent;
    }
  
}
