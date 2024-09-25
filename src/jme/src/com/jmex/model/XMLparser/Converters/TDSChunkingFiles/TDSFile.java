package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;



import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.math.*;
import com.jme.animation.SpatialTransformer;
import com.jme.light.Light;
import com.jme.light.SpotLight;
import com.jme.light.PointLight;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

import java.io.IOException;
import java.io.DataInput;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Started Date: Jul 2, 2004<br><br>
 * 
 * type=4d4d=MAIN_3DS
 * parent=nothing
 * @author Jack Lindamood
 */
public class TDSFile extends ChunkerClass{
    private EditableObjectChunk objects=null;
    private KeyframeChunk keyframes=null;
    private ArrayList spatialNodes;
    private ArrayList spatialNodesNames;
    private SpatialTransformer st;
    private ArrayList spatialLights;

    public TDSFile(DataInput myIn) throws IOException {
        super(myIn);
        ChunkHeader c=new ChunkHeader(myIn);
        if (c.type!=MAIN_3DS)
            throw new IOException("Header doesn't match 0x4D4D; Header=" + Integer.toHexString(c.type));
        c.length-=6;
        setHeader(c);

        chunk();
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch(i.type){
            case TDS_VERSION:
                readVersion();
                return true;
            case EDIT_3DS:
                objects=new EditableObjectChunk(myIn,i);
                return true;
            case KEYFRAMES:
                keyframes=new KeyframeChunk(myIn,i);
                return true;
            default:
                return false;
            }
    }


    private void readVersion() throws IOException{
        int version=myIn.readInt();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Version:" + version);
    }

    public Node buildScene() throws IOException {
        buildObject();
        putTranslations();
        Node uberNode=new Node("TDS Scene");
        LightState ls=null;
        for (int i=0;i<spatialNodes.size();i++){
            if (spatialNodes.get(i) instanceof Spatial){
                Spatial toAttach=(Spatial)spatialNodes.get(i);
                uberNode.attachChild(toAttach);
            }
        }
        for (int i=0;i<spatialLights.size();i++){
            if (ls==null){
                    ls=DisplaySystem.getDisplaySystem().getRenderer().createLightState();
                    ls.setEnabled(true);
            }
            ls.attach((Light) spatialLights.get(i));
        }
        if (ls!=null)
            uberNode.setRenderState(ls);
        if (keyframes!=null){
            if (st.keyframes.size()==1){
                st.interpolateMissing();
                st.update(0);
            }
            else
                uberNode.addController(st);
            st.setActive(true);
        }
        return uberNode;
    }

    private void putTranslations() {
        if (keyframes==null) return;
        int spatialCount=0;
        for (int i=0;i<spatialNodes.size();i++)
            if (spatialNodes.get(i) instanceof Spatial) spatialCount++;
        st=new SpatialTransformer(spatialCount);
        spatialCount=0;
        for (int i=0;i<spatialNodes.size();i++){
            if (spatialNodes.get(i) instanceof Spatial){
                st.setObject((Spatial) spatialNodes.get(i),spatialCount++,getParentIndex(i));
            }
        }
        Object[] keysetKeyframe=keyframes.objKeyframes.keySet().toArray();
        for (int i=0;i<keysetKeyframe.length;i++){
            KeyframeInfoChunk thisOne=(KeyframeInfoChunk) keyframes.objKeyframes.get(keysetKeyframe[i]);
            if ("$$$DUMMY".equals(thisOne.name)) continue;
            int indexInST=findIndex(thisOne.name);
            for (int j=0;j<thisOne.track.size();j++){
                KeyframeInfoChunk.KeyPointInTime thisTime=(KeyframeInfoChunk.KeyPointInTime) thisOne.track.get(j);
                if (thisTime.rot!=null)
                    st.setRotation(indexInST,thisTime.frame,thisTime.rot);
                if (thisTime.position!=null)
                    st.setPosition(indexInST,thisTime.frame,thisTime.position);
                if (thisTime.scale!=null)
                    st.setScale(indexInST,thisTime.frame,thisTime.scale);
            }
        }
        st.setSpeed(10);
    }


    private int findIndex(String name) {
        int j=0;
        for (int i=0;i<spatialNodesNames.size();i++){
            if (spatialNodesNames.get(i).equals(name)) return j;
            if (spatialNodes.get(i) instanceof Spatial) j++;
        }
        throw new JmeException("Logic error.  Unknown keyframe name " + name);
    }

    private int getParentIndex(int objectIndex) {
        if (((KeyframeInfoChunk)keyframes.objKeyframes.get(spatialNodesNames.get(objectIndex)))==null)
            return -2;
        short parentID=((KeyframeInfoChunk)keyframes.objKeyframes.get(spatialNodesNames.get(objectIndex))).parent;
        if (parentID==-1) return -1;
        Object[] objs=keyframes.objKeyframes.keySet().toArray();
        for (int i=0;i<objs.length;i++){
            if (((KeyframeInfoChunk)keyframes.objKeyframes.get(objs[i])).myID==parentID)
                return i;
        }
        throw new JmeException("Logic error.  Unknown parent ID for " + objectIndex);
    }

    private void buildObject() throws IOException {
        spatialNodes=new ArrayList();   // An ArrayList of Nodes
        spatialLights=new ArrayList();
        spatialNodesNames=new ArrayList();   // Their names
        Iterator i=objects.namedObjects.keySet().iterator();
        while (i.hasNext()){
            String objectKey=(String) i.next();
            NamedObjectChunk noc=(NamedObjectChunk) objects.namedObjects.get(objectKey);
            if (noc.whatIAm instanceof TriMeshChunk){
                spatialNodesNames.add(noc.name);
                Node parentNode=new Node(objectKey);
                if (keyframes ==null || keyframes.objKeyframes==null || keyframes.objKeyframes.get(objectKey)==null)
                    putChildMeshes(parentNode,(TriMeshChunk) noc.whatIAm,new Vector3f(0,0,0));
                else
                    putChildMeshes(parentNode,(TriMeshChunk) noc.whatIAm,((KeyframeInfoChunk)keyframes.objKeyframes.get(objectKey)).pivot);


                if (parentNode.getQuantity()==1){
                    spatialNodes.add(parentNode.getChild(0));
                    (parentNode.getChild(0)).setName(parentNode.getName());
                } else
                    spatialNodes.add(parentNode);
            } else if (noc.whatIAm instanceof LightChunk){
                spatialLights.add(createChildLight((LightChunk)noc.whatIAm));
            }
        }
    }

    private Light createChildLight(LightChunk lightChunk) {
        // Light attenuation does not work right.
        if (lightChunk.spotInfo!=null){
            SpotLight toReturn=new SpotLight();
            toReturn.setLocation(lightChunk.myLoc);
            toReturn.setDiffuse(lightChunk.lightColor);
            toReturn.setAmbient(ColorRGBA.black);
            toReturn.setSpecular(ColorRGBA.white);
            Vector3f tempDir=lightChunk.myLoc.subtract(lightChunk.spotInfo.target).multLocal(-1);
            tempDir.normalizeLocal();
            toReturn.setDirection(tempDir);
//            toReturn.setAngle(lightChunk.spotInfo.fallOff);  // Get this working correctly
            toReturn.setAngle(180);  // TODO: Get this working correctly, it's just a hack
            toReturn.setEnabled(true);
            return toReturn;
        } else{
            PointLight toReturn=new PointLight();
            toReturn.setLocation(lightChunk.myLoc);
            toReturn.setDiffuse(lightChunk.lightColor);
            toReturn.setAmbient(ColorRGBA.black);
            toReturn.setSpecular(ColorRGBA.white);
            toReturn.setEnabled(true);
            return toReturn;
        }


    }

    private void putChildMeshes(Node parentNode, TriMeshChunk whatIAm,Vector3f pivotLoc) throws IOException {
        FacesChunk myFace=whatIAm.face;
        if (myFace==null) return;
        boolean[] faceHasMaterial=new boolean[myFace.nFaces];
        int noMaterialCount=myFace.nFaces;
        ArrayList normals=new ArrayList(myFace.nFaces);
        ArrayList vertexes=new ArrayList(myFace.nFaces);
        Vector3f tempNormal=new Vector3f();
        ArrayList texCoords=new ArrayList(myFace.nFaces);
        if (whatIAm.coordSystem==null)
            whatIAm.coordSystem=new TransformMatrix();
        whatIAm.coordSystem.inverse();
        for (int i=0;i<whatIAm.vertexes.length;i++){
            whatIAm.coordSystem.multPoint(whatIAm.vertexes[i]);
            whatIAm.vertexes[i].subtractLocal(pivotLoc);
        }
        Vector3f[] faceNormals=new Vector3f[myFace.nFaces];
        calculateFaceNormals(faceNormals,whatIAm.vertexes,whatIAm.face.faces);

        // Precalculate nextTo[vertex][0...i] <--->
        // whatIAm.vertexes[vertex] is next to face nextTo[vertex][0] & nextTo[vertex][i]
        if (DEBUG || DEBUG_LIGHT) System.out.println("Precaching");
        int[] vertexCount=new int[whatIAm.vertexes.length];
        int vertexIndex;
        for (int i=0;i<myFace.nFaces;i++){
            for (int j=0;j<3;j++){
                vertexCount[myFace.faces[i][j]]++;
            }
        }
        int[][] realNextFaces=new int[whatIAm.vertexes.length][];
        for (int i=0;i<realNextFaces.length;i++)
            realNextFaces[i]=new int[vertexCount[i]];
        for (int i=0;i<myFace.nFaces;i++){
            for (int j=0;j<3;j++){
                vertexIndex=myFace.faces[i][j];
                realNextFaces[vertexIndex][--vertexCount[vertexIndex]]=i;
            }
        }


        if (DEBUG || DEBUG_LIGHT) System.out.println("Precaching done");



        int[] indexes=new int[myFace.nFaces*3];
        int curPosition;

        for (int i=0;i<myFace.materialIndexes.size();i++){  // For every original material
            String matName=(String) myFace.materialNames.get(i);
            int[] appliedFacesIndexes=(int[])myFace.materialIndexes.get(i);
            if (DEBUG_LIGHT || DEBUG) System.out.println("On material " + matName + " with " + appliedFacesIndexes.length + " faces.");
            if (appliedFacesIndexes.length!=0){ // If it's got something make a new trimesh for it
                TriMesh part=new TriMesh(parentNode.getName()+i);
                normals.clear();
                curPosition=0;
                vertexes.clear();
                texCoords.clear();
                for (int j=0;j<appliedFacesIndexes.length;j++){ // Look thru every face in that new TriMesh
                    if (DEBUG) if (j%500==0) System.out.println("Face:" + j);
                    int actuallFace=appliedFacesIndexes[j];
                    if (faceHasMaterial[actuallFace]==false){
                        faceHasMaterial[actuallFace]=true;
                        noMaterialCount--;
                    }
                    for (int k=0;k<3;k++){                      //   and every vertex in that face
                        // what faces contain this vertex index? If they do and are in the same SG, average
                        vertexIndex=myFace.faces[actuallFace][k];
                        tempNormal.set(faceNormals[actuallFace]);
                        calcFacesWithVertexAndSmoothGroup(realNextFaces[vertexIndex],faceNormals,myFace,tempNormal,actuallFace);
                        // Now can I just index this Vertex/tempNormal combination?
                        int l=0;
                        Vector3f vertexValue=whatIAm.vertexes[vertexIndex];
                        for (l=0;l<normals.size();l++)
                            if (normals.get(l).equals(tempNormal) && vertexes.get(l).equals(vertexValue))
                                break;
                        if (l==normals.size()){ // if new
                            normals.add(new Vector3f(tempNormal));
                            vertexes.add(whatIAm.vertexes[vertexIndex]);
                            if (whatIAm.texCoords!=null)
                                texCoords.add(whatIAm.texCoords[vertexIndex]);
                            indexes[curPosition++]=l;
                        } else { // if old
                            indexes[curPosition++]=l;
                        }
                    }
                }
                Vector3f[] newVerts=new Vector3f[vertexes.size()];
                for (int indexV=0;indexV<newVerts.length;indexV++)
                    newVerts[indexV]=(Vector3f) vertexes.get(indexV);
                part.setVertices(newVerts);
                part.setNormals((Vector3f[]) normals.toArray(new Vector3f[]{}));
                if (whatIAm.texCoords!=null) part.setTextures((Vector2f[]) texCoords.toArray(new Vector2f[]{}));
                int[] intIndexes=new int[curPosition];
                System.arraycopy(indexes,0,intIndexes,0,curPosition);
                part.setIndices(intIndexes);

                MaterialBlock myMaterials=(MaterialBlock) objects.materialBlocks.get(matName);
                if (matName==null)
                    throw new IOException("Couldn't find the correct name of " + myMaterials);
                if (myMaterials.myMatState.isEnabled())
                    part.setRenderState(myMaterials.myMatState);
                if (myMaterials.myTexState.isEnabled())
                    part.setRenderState(myMaterials.myTexState);
                if (myMaterials.myWireState.isEnabled())
                    part.setRenderState(myMaterials.myWireState);
                parentNode.attachChild(part);
            }
        }
        if (noMaterialCount!=0){    // attach materialless parts
            int[] noMaterialIndexes=new int[noMaterialCount*3];
            int partCount=0;
            for (int i=0;i<whatIAm.face.nFaces;i++){
                if (!faceHasMaterial[i]){
                    noMaterialIndexes[partCount++]=myFace.faces[i][0];
                    noMaterialIndexes[partCount++]=myFace.faces[i][1];
                    noMaterialIndexes[partCount++]=myFace.faces[i][2];
                }
            }
            TriMesh noMaterials=new TriMesh(parentNode.getName()+"-1");
            noMaterials.setVertices(whatIAm.vertexes);
            noMaterials.setIndices(noMaterialIndexes);
            parentNode.attachChild(noMaterials);
        }
    }

    private void calculateFaceNormals(Vector3f[] faceNormals,Vector3f[] vertexes,int[][] faces) {
        Vector3f tempa=new Vector3f(),tempb=new Vector3f();
        // Face normals
        for (int i=0;i<faceNormals.length;i++){
            tempa.set(vertexes[faces[i][0]]);  // tempa=a
            tempa.subtractLocal(vertexes[faces[i][1]]);    // tempa-=b (tempa=a-b)
            tempb.set(vertexes[faces[i][0]]);  // tempb=a
            tempb.subtractLocal(vertexes[faces[i][2]]);    // tempb-=c (tempb=a-c)
            faceNormals[i]=tempa.cross(tempb).normalizeLocal();
        }
    }

    // Find all face normals for faces that contain that vertex AND are in that smoothing group.
    private void calcFacesWithVertexAndSmoothGroup(int[] thisVertexTable,Vector3f[] faceNormals,FacesChunk myFace, Vector3f tempNormal, int faceIndex) {
        // tempNormal starts out with the face normal value
        int smoothingGroupValue=myFace.smoothingGroups[faceIndex];
        if (smoothingGroupValue==0)
            return; // 0 smoothing group values don't have smooth edges anywhere
        int arrayFace;
        for (int i=0;i<thisVertexTable.length;i++){
            arrayFace=thisVertexTable[i];
            if (arrayFace==faceIndex) continue;
            if ((myFace.smoothingGroups[arrayFace] & smoothingGroupValue)!=0 )
                tempNormal.addLocal(faceNormals[arrayFace]);
        }
        tempNormal.normalizeLocal();
    }
}