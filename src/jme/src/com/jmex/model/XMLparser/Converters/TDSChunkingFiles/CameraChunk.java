package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent ==  NAMED_OBJECT == 0x4000<br>
 * type == CAMERA_FLAG == 4700 <br>
 * 
 * @author Jack Lindamood
 */
class CameraChunk extends ChunkerClass {
    Vector3f camPos;
    Vector3f targetLoc;
    float bankAngle;
    float focus;
    float nearRange;
    float farRange;
    public CameraChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected void initializeVariables() {
        try
        {
            camPos=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            targetLoc=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            bankAngle=myIn.readFloat();
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try
        {
            focus=myIn.readFloat();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        decrHeaderLen(4*8);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case CAMERA_RANGES:
                readRanges();
                return true;
            default:
                return false;
            }
    }

    private void readRanges() throws IOException {
        nearRange=myIn.readFloat();
        farRange=myIn.readFloat();
    }
}
