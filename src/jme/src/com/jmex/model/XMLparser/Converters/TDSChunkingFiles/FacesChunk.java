package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent == OBJ_TRIMESH == 4100<br>
 * type == FACES_ARRAY == 4120<br>
 *
 * @author Jack Lindamood
 */
class FacesChunk extends ChunkerClass{
    int nFaces;
    int[][] faces;
    int [] smoothingGroups;
    ArrayList materialNames;
    ArrayList materialIndexes;


    public FacesChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected void initializeVariables() {
        try
        {
            nFaces=myIn.readUnsignedShort();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading faces #=" + nFaces);
        faces=new int[nFaces][];
        smoothingGroups=new int[nFaces];
        materialNames=new ArrayList();
        materialIndexes=new ArrayList();

        for (int i=0;i<nFaces;i++){
            try
            {
                faces[i]=new int[]{myIn.readUnsignedShort(),myIn.readUnsignedShort(),myIn.readUnsignedShort()};
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try
            {
                short flag=myIn.readShort();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        decrHeaderLen(2 + nFaces*(3*2+2));
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case SMOOTH_GROUP:
                readSmoothing();
                return true;
            case MESH_MAT_GROUP:
                readMeshMaterialGroup();
                return true;
            default:
                return false;
            }
    }

    private void readMeshMaterialGroup() throws IOException {
        String name=readcStr();
        int numFace=myIn.readUnsignedShort();
        int[] appliedFacesIndexes=new int[numFace];
        if (DEBUG || DEBUG_LIGHT) System.out.println("Material " + name + " is applied to " + numFace + " faces");
        for (int i=0;i<numFace;i++){
            appliedFacesIndexes[i]=myIn.readUnsignedShort();
        }
        materialIndexes.add(appliedFacesIndexes);
        materialNames.add(name);
    }

    private void readSmoothing() throws IOException {
        for (int i=0;i<nFaces;i++)
            smoothingGroups[i]=myIn.readInt();
    }
}
