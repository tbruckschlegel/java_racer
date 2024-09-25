// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name: ModelLoader.java

import java.io.IOException;
import java.net.URL;

import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.model.XMLparser.JmeBinaryReader;


public class ModelLoader
{

    public ModelLoader()
    {
    }

    public static Node loadModel(Object owner, String name, String location, String texture, int bound)
    {
        URL modelLocation = owner.getClass().getClassLoader().getResource(
                "jmextest/data/model/" + location);
        Node model = null;
        JmeBinaryReader jbr = new JmeBinaryReader();
        if (bound == BOUNDING_BOX)
            jbr.setProperty("bound", "box");
        try
        {
            model = jbr.loadBinaryFormat(modelLocation.openStream());
        }
        catch (IOException e)
        {
            System.out.println("Model could not be loaded:" + e.getMessage());
        }

        TextureState ts = com.jme.system.DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        Texture t = TextureManager.loadTexture(owner.getClass().getClassLoader().getResource(
                "jmextest/data/model/" + texture), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
        ts.setTexture(t, 0);
        model.setRenderState(ts);
        return model;
    }

    public static final int BOUNDING_BOX = 0;

    public static final int BOUNDING_SPHERE = 1;
}
