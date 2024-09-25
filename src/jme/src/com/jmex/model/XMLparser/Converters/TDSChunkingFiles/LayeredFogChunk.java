package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.renderer.ColorRGBA;

import java.io.IOException;
import java.io.DataInput;

/**
 * Started Date: Jul 3, 2004<br><br>
 *
 * type == 2302 == LAYERED_FOG_OPT
 * parent == 3d3d == EDIT_3DS
 *
 * @author Jack Lindamood
 */
class LayeredFogChunk extends ChunkerClass{
    private float nearZ;
    private float farZ;
    private float density;
    private int type;
    private ColorRGBA fogColor;

    public LayeredFogChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected void initializeVariables(){
        try
        {
            nearZ=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            farZ=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            density=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            type=myIn.readInt();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        decrHeaderLen(4*4);
        if (DEBUG || DEBUG_LIGHT) System.out.println("nearZ:"+nearZ+" farZ:"+farZ+" density:"+density+" type:"+type);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case COLOR_FLOAT:
                fogColor=new ColorRGBA(myIn.readFloat(), myIn.readFloat(), myIn.readFloat(), 1);
                if (DEBUG || DEBUG_LIGHT) System.out.println("Fog color:" + fogColor);
                return true;
            default:
                return false;
        }
    }
}
