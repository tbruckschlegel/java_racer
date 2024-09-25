package com.jmex.physics.vehicle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

/*
 * Created on 19.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author tommy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NmCurve
{
    HashMap RPM2Torque = null;
    float maxRPM, minRPM;

    float getNm(String pic, float rpm, float minrpm, float maxrpm, float minnm, float maxnm) throws IOException
    {
        int w, h;
        float Nm = Float.MIN_VALUE;
        BufferedImage inputBuffer = ImageIO.read(new File(pic));
       
        w = inputBuffer.getWidth();
        h = inputBuffer.getHeight();
        /* xmin, xmax, ymin and ymax of rpm and Nm */
        int rpm_x_min = Integer.MAX_VALUE;
        int rpm_y_min = Integer.MAX_VALUE;
        int rpm_x_max = Integer.MIN_VALUE;
        int rpm_y_max = Integer.MIN_VALUE;
        int nm_x_min = Integer.MAX_VALUE;
        int nm_y_min = Integer.MAX_VALUE;
        int nm_x_max = Integer.MIN_VALUE;
        int nm_y_max = Integer.MIN_VALUE;

        for (int y = h - 1; y >= 0; y--)
        {
            for (int x = 0; x < w; x++)
            {
                int color = inputBuffer.getRGB(x, y);
                int a = ((color >> 24) & 255);
                int r = ((color >> 16) & 255);
                int g = ((color >> 8) & 255);
                int b = ((color) & 255);

                if (r == 255 && g == 255 && b == 0) /* Nm - YELLOW */
                {
                    int y_flip = h - y;

                    if (nm_x_min > x)
                        nm_x_min = x;

                    if (nm_y_min > y_flip)
                        nm_y_min = y_flip;

                    if (nm_x_max < x)
                        nm_x_max = x;

                    if (nm_y_max < y_flip)
                        nm_y_max = y_flip;
                }
            }

        }
        float rpmRange = maxrpm - minrpm;

        float nmXRange = nm_x_max - nm_x_min;
        float nmYRange = nm_y_max - nm_y_min;

        float rpm2pixel;

        //convert rpm into pixel range
        rpm2pixel = nm_x_min
                + ((nm_x_max - nm_x_min) / (maxrpm - minrpm) * ((rpm - minrpm) < 0 ? 0 : (rpm - minrpm)));

        for (int y = h - 1; y >= 0; y--)
        {
            int color = inputBuffer.getRGB((int) rpm2pixel, y);
            int a = ((color >> 24) & 255);
            int r = ((color >> 16) & 255);
            int g = ((color >> 8) & 255);
            int b = ((color) & 255);

            if (r == 255 && g == 255 && b == 0) // Nm - YELLOW
            {
                float tmp = ((minnm + (((maxnm - minnm) / (nm_y_max - nm_y_min) * (((h - y) - nm_y_min))))));
                if (tmp > Nm)
                    Nm = tmp;
            }
        }

        return Nm;
    }
    
    void buildLookUpTable(String pic, float minrpm, float maxrpm, float minnm, float maxnm) throws IOException
    {
        maxRPM = maxrpm;
        minRPM = minrpm;
        
        if(!(new File(pic+".oos").exists()))
        {
	        RPM2Torque = new HashMap();
	        for(int i=(int)minrpm; i<=(int)maxrpm; i+=100 )
	        {
	            RPM2Torque.put(Integer.valueOf(i), Float.valueOf(getNm(pic, i, minrpm, maxrpm, minnm, maxnm)));
	        }   
	        
	        // save it to stream for faster loading next time
	        FileOutputStream fos = null;
	        try{
	            fos=new FileOutputStream(pic+".oos");
	            ObjectOutputStream oos = new ObjectOutputStream(fos);
	            oos.writeObject(RPM2Torque);
	            oos.flush();
	            fos.close();
	            //System.out.println(RPM2Torque.toString());

	        }
	        catch(IOException e)
	        {
	            e.printStackTrace();
	        }
        }
        else
        {
	        FileInputStream fis = null;
	        try{
	            RPM2Torque = null;
	            fis=new FileInputStream(pic+".oos");
	            ObjectInputStream ois = new ObjectInputStream(fis);
	            RPM2Torque = (HashMap)ois.readObject();
	            fis.close();
	            
	            //System.out.println(RPM2Torque.toString());
	        }
	        catch(IOException e)
	        {
	            e.printStackTrace();
	        }
	        catch(ClassNotFoundException e)
	        {
	            e.printStackTrace();
	        }
           
        }
        
    }
    
    float covertRPM2Nm(int rpm)
    {
        if(rpm<minRPM)
            return 0f;
        else
            if(rpm>maxRPM)
                return 0f;
        
        if(RPM2Torque.containsKey(Integer.valueOf(rpm)))
            return ((Float)RPM2Torque.get(Integer.valueOf(rpm))).floatValue();
        //else
        //{

            int irpm=Math.round(rpm*0.01f);
            irpm*=100; 
            //System.out.println("irpm: "+irpm);
            return ((Float)RPM2Torque.get(Integer.valueOf(irpm))).floatValue();

        //}
    }
}
