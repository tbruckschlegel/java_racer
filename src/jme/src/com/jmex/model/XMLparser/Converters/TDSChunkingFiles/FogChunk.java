package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.renderer.ColorRGBA;

import java.io.IOException;
import java.io.DataInput;

/**
 * Started Date: Jul 3, 2004<br><br>
 *
 * parent == 3d3d == EDIT_3DS
 * type == FOG_FLAG == 2200
 *
 * @author Jack Lindamood
 */
class FogChunk extends ChunkerClass{
    float nearPlane;
    float nearDensity;
    float farPlane;
    float farDensity;
    private boolean useBackGround;
    private ColorRGBA background;

    public FogChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected void initializeVariables(){
        try
        {
            nearPlane=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            nearDensity=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            farPlane=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            farDensity=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        decrHeaderLen(4*4);
        if (DEBUG ||DEBUG_LIGHT)
            System.out.println("Near plane:" + nearPlane + " Near Density:" + nearDensity + " Far Plane:" + farPlane + " Far Density:"+ farDensity);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case COLOR_FLOAT:
                background=new ColorRGBA(myIn.readFloat(), myIn.readFloat(), myIn.readFloat(), 1);
                if (DEBUG) System.out.println("Background Color:" + background);
                return true;
            case FOG_BACKGROUND:
                useBackGround=true;
                if (DEBUG) System.out.println("use background true");
                return true;
            default:
                return false;
            }
    }
}
