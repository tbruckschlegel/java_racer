import java.util.ArrayList;

import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.JmeException;
import com.jme.util.geom.GeometryInfo;

public class VertexUtil
{

    public static void addToPhysicsWorld(Node rootNode, Spatial treeRoot, String[] names)
    {
        
        if (treeRoot instanceof Node)
        {
            Node treeNode = (Node) treeRoot;
            ArrayList children = treeNode.getChildren();
            for (int x = children.size(); --x >= 0;)
            {
                addToPhysicsWorld(rootNode, (Spatial) children.get(x), names);
            }
        }
        else
        {
            boolean containsName=false;
            for(int ni=0; ni<names.length; ni++)
                if (treeRoot.getName().contains(names[ni]))
                {
                    containsName=true;
                    break;
                }
            
            if(!containsName)
                return;
            
            System.out.println("added to physics world: "+treeRoot.getName());
            treeRoot.setWorldBound(new OrientedBoundingBox());
            treeRoot.updateWorldBound();
            
            treeRoot.setLocalTranslation(new Vector3f(475f, -15.3f, 83f));
            
            if(rootNode!=null)
                rootNode.attachChild(treeRoot);
        
        }
    }
    
    public static void duplicateTextureCoordinates(Spatial treeRoot, int numUnits)
    {
        if (treeRoot instanceof Node)
        {
            Node treeNode = (Node) treeRoot;
            ArrayList children = treeNode.getChildren();
            for (int x = children.size(); --x >= 0;)
            {
                duplicateTextureCoordinates((Spatial) children.get(x), numUnits);
            }
        }
        else
        {

            // change the txt coordinates
            if (treeRoot instanceof TriMesh)
            {
                for (int ti = 1; ti < numUnits; ti++)
                {
                    Vector2f[] t_old = ((TriMesh) treeRoot).getTextures(0);
                    if (t_old != null)
                    {
                        Vector2f t[] = new Vector2f[ t_old.length ];

                        for (int i = 0; i < t_old.length; i++)
                        {
                            t[i] = new Vector2f(t_old[i]);
      
                        }
                        if (t_old.length > 0)
                        {
                            ((TriMesh) treeRoot).setTextures(t, ti);
                            ((TriMesh) treeRoot).updateTextureBuffer(ti);
                        }
                    }
                }
                
            }
            else
                System.out.println("treeRoot: " + treeRoot != null ? treeRoot.toString() : "null");

        }
    }
    
    public static void setSolidColor(Spatial treeRoot, ColorRGBA c)
    {
        if (treeRoot instanceof Node)
        {
            Node treeNode = (Node) treeRoot;
            ArrayList children = treeNode.getChildren();
            for (int x = children.size(); --x >= 0;)
            {
                setSolidColor((Spatial) children.get(x), c);
            }
        }
        else
        {
            if (treeRoot instanceof Geometry)
            {
                ((Geometry) treeRoot).setSolidColor(new ColorRGBA(c));
                ((Geometry) treeRoot).updateColorBuffer();
            }
        }        
    }
    
    
    public static void changeTextureState(Spatial treeRoot, int mode)
    {
        if (treeRoot instanceof Node)
        {
            Node treeNode = (Node) treeRoot;
            ArrayList children = treeNode.getChildren();
            for (int x = children.size(); --x >= 0;)
            {
                changeTextureState((Spatial) children.get(x), mode);
            }
        }
        else
        {
            if (treeRoot instanceof Geometry)
            {
                for ( int i=0; i<((Geometry) treeRoot).getRenderStateList().length; i++)
                {
                    if(((Geometry) treeRoot).getRenderStateList()[i]!=null)
                    {
                        if(((Geometry) treeRoot).getRenderStateList()[i].getType() == RenderState.RS_TEXTURE)
                        {
                            TextureState ts = (TextureState) ((Geometry) treeRoot).getRenderStateList()[i];
                            Texture t = ts.getTexture(0);
                            
                            if(t!=null)
                            {
                                t.setApply(Texture.AM_COMBINE);
                                t.setCombineFuncRGB(Texture.ACF_MODULATE);
                                t.setCombineSrc0RGB(Texture.ACS_TEXTURE);
                                t.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
                                t.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
                                t.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
                                t.setCombineScaleRGB(1.0f);
                            }                                
                            //System.out.println("texture state changed to multitexture");
                        }
                        ((Geometry) treeRoot).updateRenderState();
                    }
                }
            }
            //else
              //  System.out.println("treeRoot: " + treeRoot != null ? treeRoot.toString() : "null");

        }        
    }

    public static void optimizeForCache(Spatial treeRoot)
    {
        if (treeRoot instanceof Node)
        {
            Node treeNode = (Node) treeRoot;
            ArrayList children = treeNode.getChildren();
            for (int x = children.size(); --x >= 0;)
            {
                optimizeForCache((Spatial) children.get(x));
            }
        }
        else
        {
            if (treeRoot instanceof TriMesh)
            {
                TriMesh tm3 = new GeometryInfo((TriMesh)treeRoot).optimizeTrianglesForCache().createTrimesh(treeRoot.getName());

                ((TriMesh)treeRoot).clearBuffers();
                ((TriMesh)treeRoot).reconstruct(tm3.getVertices(), tm3.getNormals(), tm3.getColors(), tm3.getTextures(), tm3.getIndices());
                tm3.clearBuffers();
                tm3=null;

            }
            else
                System.out.println("treeRoot: " + treeRoot != null ? treeRoot.toString() : "null");

        }
    }
    
    

    public static void scaleTextureCoords(final Spatial treeRoot, float tx[], float ty[], String[] names)
    {
        if (treeRoot instanceof Node)
        {
            Node treeNode = (Node) treeRoot;
            ArrayList children = treeNode.getChildren();
            for (int x = children.size(); --x >= 0;)
            {
                scaleTextureCoords((Spatial) children.get(x), tx, ty, names);
            }
        }
        else
        {

            // change the txt coordinates
            if (treeRoot instanceof TriMesh)
            {
                boolean containsName=false;
                if(names!=null)
                {
                    for(int ni=0; ni<names.length; ni++)
                        if (treeRoot.getName().contains(names[ni]))
                        {
                            containsName=true;
                            break;
                        }
                    
                    if(!containsName)
                        return;
                }
                for (int ti = 0; ti < ((TriMesh) treeRoot).getNumberOfUnits(); ti++)
                {
                    Vector2f[] t_old = ((TriMesh) treeRoot).getTextures(ti);
                    if (t_old != null)
                    {
                        Vector2f t[] = new Vector2f[ t_old.length ];

                        for (int i = 0; i < t_old.length; i++)
                        {
                            float _tx;
                            float _ty;
                            
                            if(i>=tx.length)
                                _tx=tx[0];
                            else
                                _tx=tx[i];
                            
                            if(i>=ty.length)
                                _ty=ty[0];
                            else
                                _ty=ty[i];

                            t[i] = new Vector2f();
                            
                            t[i].x = t_old[i].x * _tx;
                            t[i].y = t_old[i].y * _ty;
                        }
                        if (t_old.length > 0)
                        {
                            ((TriMesh) treeRoot).setTextures(t, ti);
                            ((TriMesh) treeRoot).updateTextureBuffer(ti);
                        }
                    }
                }
                
            }
            else
                System.out.println("treeRoot: " + treeRoot != null ? treeRoot.toString() : "null");

        }

    }

    public static void smoothNormals(Spatial treeRoot, boolean weight, boolean replicateTxt)
    {
        if (treeRoot instanceof Node)
        {
            Node treeNode = (Node) treeRoot;
            ArrayList children = treeNode.getChildren();
            for (int x = children.size(); --x >= 0;)
            {
                smoothNormals((Spatial) children.get(x), weight, replicateTxt);
            }
        }
        else
        {
            if (treeRoot instanceof TriMesh)
            {
                TriMesh g = (TriMesh) treeRoot;
                if (replicateTxt)
                {
                    g.copyTextureCoords(0, 1);
                    g.copyTextureCoords(0, 2);
                    g.copyTextureCoords(0, 3);
                    g.updateTextureBuffer();
                }
                // System.out.println("old vertex number: +"+g.getVertices().length);
                // System.out.println("old index number: +"+g.getIndices().length);

                if (g.getIndices().length % 3 != 0)
                    throw new JmeException("No trinagle mesh!");

                // vertices
                Vector3f vertices[] = g.getVertices();

                // textures
                int numTextures = g.getNumberOfUnits();
                Vector2f tex[][] = g.getAllTextures();

                // colors
                boolean hasColors = g.getColors() != null ? true : false;
                ColorRGBA colors[] = hasColors ? g.getColors() : null;

                // indices
                int indices[] = g.getIndices();
                Vector3f normals[] = g.getNormals();

                int numTriVertices = g.getIndices().length;

                // allocate space for new triangles

                // vertices
                Vector3f new_vertices[] = new Vector3f[numTriVertices];

                // textures
                Vector2f new_tex[][] = numTextures > 0 ? new Vector2f[numTextures][] : null;

                // colors
                ColorRGBA new_colors[] = hasColors ? new ColorRGBA[numTriVertices] : null;

                // indices
                int new_indices[] = new int[numTriVertices];
                Vector3f new_normals[] = new Vector3f[numTriVertices];

                // System.out.println("new vertex number: +"+numTriVertices);
                // System.out.println("new index number: +"+numTriVertices);
                // 0,1,2 == one triangle
                // 3,4,5 == second triangle
                // ...

                if (numTextures > 0)
                {
                    for (int t = 0; t < numTextures; t++)
                    {
                        if (tex[t].length > 0)
                        {
                            new_tex[t] = new Vector2f[numTriVertices];
                        }
                        else
                            new_tex[t] = new Vector2f[0];
                    }
                }

                for (int index = indices.length-1, tri = 0; index >=0; index -= 3)//< indices.length; index += 3)
                {
                    int tmp_index = index + 2;

                    // 0

                    // vertex
                    new_vertices[tri] = new Vector3f();
                    new_vertices[tri].set(vertices[indices[index]]);
                    // normals
                    new_normals[tri] = new Vector3f();
                    // new_normals[tri].set(normals[indices[index]]);
                    new_normals[tri].set(normals[indices[index]]);

                    // normals
                    if (hasColors)
                    {
                        // new_colors[tri] = new ColorRGBA(0.5f*(new_normals[tri].x+1), 0.5f*(new_normals[tri].y+1),
                        // 0.5f*(new_normals[tri].z+1), 1f);
                        new_colors[tri] = new ColorRGBA();
                        new_colors[tri].set(colors[indices[index]]);
                    }
                    // indices
                    new_indices[tri] = index;

                    // texture(s)
                    if (numTextures > 0)
                        for (int t = 0; t < numTextures; t++)
                        {
                            // System.out.println(t+"/"+tex[t].length);
                            if (tex[t].length > 0)
                            {
                                new_tex[t][tri] = new Vector2f();
                                new_tex[t][tri].set(tex[t][indices[index]]);
                            }
                        }

                    tri++;

                    // 1

                    // vertex
                    new_vertices[tri] = new Vector3f();
                    new_vertices[tri].set(vertices[indices[index - 1]]);
                    // normals
                    new_normals[tri] = new Vector3f();
                    new_normals[tri].set(normals[indices[index - 1]]);
                    // normals
                    if (hasColors)
                    {
                        // new_colors[tri] = new ColorRGBA(0.5f*(new_normals[tri].x+1), 0.5f*(new_normals[tri].y+1),
                        // 0.5f*(new_normals[tri].z+1), 1f);
                        new_colors[tri] = new ColorRGBA();
                        new_colors[tri].set(colors[indices[index - 1]]);
                    }
                    // indices
                    new_indices[tri] = index - 1;

                    // texture(s)
                    if (numTextures > 0)
                        for (int t = 0; t < numTextures; t++)
                        {
                            if (tex[t].length > 0)
                            {
                                new_tex[t][tri] = new Vector2f();
                                new_tex[t][tri].set(tex[t][indices[index - 1]]);
                            }
                        }

                    tri++;

                    // 2

                    // vertex
                    new_vertices[tri] = new Vector3f();
                    new_vertices[tri].set(vertices[indices[index - 2]]);
                    // normals
                    new_normals[tri] = new Vector3f();
                    new_normals[tri].set(normals[indices[index - 2]]);
                    // new_normals[tri].set(normals[indices[index + 2]]);
                    // normals
                    if (hasColors)
                    {
                        // new_colors[tri] = new ColorRGBA(0.5f*(new_normals[tri].x+1), 0.5f*(new_normals[tri].y+1),
                        // 0.5f*(new_normals[tri].z+1), 1f);
                        new_colors[tri] = new ColorRGBA();
                        new_colors[tri].set(colors[indices[index - 2]]);
                    }
                    // indices
                    new_indices[tri] = index - 2;   

                    // texture(s)
                    if (numTextures > 0)
                        for (int t = 0; t < numTextures; t++)
                        {
                            if (tex[t].length > 0)
                            {
                                new_tex[t][tri] = new Vector2f();
                                new_tex[t][tri].set(tex[t][indices[index - 2]]);
                            }
                        }

                    tri++;
/*
                    // calculate face normals and set them
                    Vector3f tmp = new Vector3f();
                    tmp.set(new_normals[tri - 1]);
                    tmp.addLocal(new_normals[tri - 2]);
                    tmp.addLocal(new_normals[tri - 3]);
                    tmp.normalizeLocal();
*/
                    Vector3f p21 = new Vector3f(new_vertices[tri - 3]);
                    p21.subtractLocal(new_vertices[tri - 2]);

                    Vector3f p31 = new Vector3f(new_vertices[tri - 3]);
                    p31.subtractLocal(new_vertices[tri - 1]);

                    Vector3f tmpVec = new Vector3f(p21);
                    tmpVec.crossLocal(p31);
                    tmpVec = tmpVec.normalizeLocal();
                    new_normals[tri - 3].set(tmpVec);
                    new_normals[tri - 2].set(tmpVec);
                    new_normals[tri - 1].set(tmpVec);
                }

                double cosAngle = java.lang.Math.cos(89.0 * Math.PI / 180.0);
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;

                float size[] = new float[new_vertices.length];
                for (int tri = 0; tri < new_vertices.length; tri += 3) // calc size of each triangle
                {
                    Vector3f AB = new_vertices[tri].mult(new_vertices[tri + 1]);
                    Vector3f AC = new_vertices[tri].mult(new_vertices[tri + 2]);
                    Vector3f area = AB.cross(AC);

                    size[tri] = area.length() * 0.5f;
                    size[tri + 1] = size[tri];
                    size[tri + 2] = size[tri];

                    if (min > size[tri])
                        min = size[tri];

                    if (max < size[tri])
                        max = size[tri];
                }

                for (int tri = 0; tri < new_vertices.length; tri++)// =3) // for each triangle
                {
                    boolean averaged = false;
                    Vector3f averageNormal = new Vector3f(0, 0, 0);

                    for (int tri2 = 0; tri2 < new_vertices.length; tri2++) // for each vertex
                    {
                        if (tri2 != tri && new_vertices[tri].equals(new_vertices[tri2])) // average all normals of
                                                                                            // the triangle
                        // which contains this vertex
                        {
                            // check for treshold angle - edge conservation
                            float dot = new_normals[tri].dot(new_normals[tri2]);
                            // System.out.println("min: "+min+"max: "+max+"size["+tri+"]="+size[tri]);
                            if (dot > cosAngle /* && size[tri]>16 */)
                            {

                                averaged = true;
                                averageNormal = averageNormal.add(((weight == true) ? new_normals[tri2]
                                        .mult(size[tri2]) : new_normals[tri2]) /* face normal */);
                            }
                        }
                    }
                    if (averaged)
                    {
                        averageNormal.normalizeLocal(); // vertex normal

                        new_normals[tri].set(averageNormal);
                        // set the averaged normal in each triangle it is in

                        for (int tri2 = 0; tri2 < new_vertices.length; tri2++) // for each vertex
                        {
                            if (tri2 != tri && new_vertices[tri].equals(new_vertices[tri2])) // average all normals
                                                                                                // of the triangle
                            // which contains this vertex
                            {
                                new_normals[tri2].set(new_normals[tri]);
                            }
                        }

                    }
                }

                g.clearBuffers();

                g.setVertices(new_vertices);
                g.updateVertexBuffer();

                g.setIndices(new_indices);
                g.updateIndexBuffer();

                g.setNormals(new_normals);
                g.updateNormalBuffer();

                if (hasColors)
                {
                    g.setColors(new_colors);
                    g.updateColorBuffer();
                }

                if (numTextures > 0)
                    for (int t = 0; t < numTextures; t++)
                        if (new_tex[t].length > 0)
                        {
                            // System.out.println("tex: " + new_tex[t].length);
                            g.setTextures(new_tex[t], t);
                        }

                g.updateTextureBuffer();

                g.updateModelBound();

            }
        }
    }

}
