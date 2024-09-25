/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import com.jme.image.BitmapHeader;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * 
 * <code>TextureManager</code> provides static methods for building a
 * <code>Texture</code> object. Typically, the information supplied is the
 * filename and the texture properties.
 * 
 * @author Mark Powell
 * @author Joshua Slack -- cache code and enhancements
 * @version $Id: TextureManager.java,v 1.39 2005/06/20 15:44:22 renanse Exp $
 */
final public class TextureManager {

    private static HashMap m_tCache = new HashMap();
    public static boolean COMPRESS_BY_DEFAULT = true;
    
    private static int maxTextureSize = -1;

    private TextureManager() {
    }
    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. Filter parameters are used to define the filtering of the
     * texture. If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the filename of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * 
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(String file, int minFilter,
            int magFilter) {
        return loadTexture(file, minFilter, magFilter, 1.0f, true);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. Filter parameters are used to define the filtering of the
     * texture. If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the filename of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * @param flipped
     *            If true, the images Y values are flipped.
     * 
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(String file, int minFilter,
            int magFilter, float anisoLevel, boolean flipped) {
        URL url = null;
        try {
            url = new URL("file:" + file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return loadTexture(url, minFilter, magFilter, (COMPRESS_BY_DEFAULT ? Image.GUESS_FORMAT : Image.GUESS_FORMAT_NO_S3TC), anisoLevel, flipped);
    }

    public static com.jme.image.Texture loadTexture(String file, int minFilter,
            int magFilter, int imageType, float anisoLevel, boolean flipped) {
        URL url = null;
        try {
            url = new URL("file:" + file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return loadTexture(url, minFilter, magFilter, imageType, anisoLevel,
                flipped);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * url. Filter parameters are used to define the filtering of the texture.
     * If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the url of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * 
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(URL file, int minFilter,
            int magFilter) {
        return loadTexture(file, minFilter, magFilter, (COMPRESS_BY_DEFAULT ? Image.GUESS_FORMAT : Image.GUESS_FORMAT_NO_S3TC), 1.0f, true);
    }

    public static com.jme.image.Texture loadTexture(URL file, int minFilter,
            int magFilter, float anisoLevel, boolean flipped) {
        return loadTexture(file, minFilter, magFilter, (COMPRESS_BY_DEFAULT ? Image.GUESS_FORMAT : Image.GUESS_FORMAT_NO_S3TC), anisoLevel, true);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * url. Filter parameters are used to define the filtering of the texture.
     * If there is an error loading the file, null is returned.
     * 
     * @param file
     *            the url of the texture image.
     * @param minFilter
     *            the filter for the near values.
     * @param magFilter
     *            the filter for the far values.
     * @param imageType
     *            the image type to use. if -1, the type is determined by jME.
     *            If S3TC/DXT1[A] is available we use that. if -2, the type is
     *            determined by jME without using S3TC, even if available. See
     *            com.jme.image.Image for possible types.
     * @param flipped
     *            If true, the images Y values are flipped.
     * 
     * @return the loaded texture. If there is a problem loading the texture,
     *         null is returned.
     */
    public static com.jme.image.Texture loadTexture(URL file, int minFilter,
            int magFilter, int imageType, float anisoLevel, boolean flipped) {
        if (null == file) {
            System.err.println("Could not load image...  URL was null.");
            return null;
        }
        String fileName = file.getFile();
        if (fileName == null)
            return null;

        TextureKey tkey = new TextureKey(file, minFilter, magFilter,
                anisoLevel, flipped);
        Texture texture = (Texture) m_tCache.get(tkey);

        if (texture != null) {
            // Uncomment if you want to see when this occurs.
            // System.err.println("******** REUSING TEXTURE ********");
            Texture tClone = texture.createSimpleClone();
            return tClone;
        }

        // TODO: Some types currently require making a java.awt.Image object as
        // an intermediate step. Rewrite each type to avoid AWT at all costs.
        com.jme.image.Image imageData = null;
        try {
            /*
            String fileExt = fileName.substring(fileName.lastIndexOf('.'));
            if (".TGA".equalsIgnoreCase(fileExt)) { // TGA, direct to imageData
                imageData = TGALoader.loadImage(file.openStream());
            } else if (".DDS".equalsIgnoreCase(fileExt)) { // DDS, direct to
                                                            // imageData
                imageData = DDSLoader.loadImage(file.openStream());
            } else if (".BMP".equalsIgnoreCase(fileExt)) { // BMP, awtImage to
                                                            // imageData
                java.awt.Image image = loadBMPImage(file.openStream());
                imageData = loadImage(image, flipped);
            } else { 
                */
                // Anything else
                java.awt.Image image = ImageIO.read(file);
                imageData = loadImage(image, flipped);
           // }
        } catch (IOException e) {
            // e.printStackTrace();
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Could not load: " + file + " (" + e.getClass() + ")");
            return null;
        }
        if (null == imageData) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "(image null) Could not load: " + file);
            return null;
        }

        // apply new texture in a state so it will setup the OpenGL id.
        // If we ever need to use two+ display systems at once, this line
        // will need to change.
        TextureState state = DisplaySystem.getDisplaySystem().getRenderer()
                .createTextureState();

        // we've already guessed the format. override if given.
        if (imageType != Image.GUESS_FORMAT_NO_S3TC
                && imageType != Image.GUESS_FORMAT)
            imageData.setType(imageType);
        else if (imageType == Image.GUESS_FORMAT && state.isS3TCAvailable()) {
            // Enable S3TC DXT1 compression if available and we're guessing
            // format.
            
            if (imageData.getType() == com.jme.image.Image.RGB888)
                imageData.setType(com.jme.image.Image.RGB888_DXT1);
            else if (imageData.getType() == com.jme.image.Image.RGBA8888)
                imageData.setType(com.jme.image.Image.RGBA8888_DXT5);
                
        }

        texture = new Texture(anisoLevel);
        texture.setCorrection(Texture.CM_PERSPECTIVE);
        texture.setFilter(magFilter);
        texture.setImage(imageData);
        texture.setMipmapState(minFilter);
        texture.setImageLocation(file.toString());

        state.setTexture(texture);
        state.apply();

        m_tCache.put(tkey, texture);
        return texture;
    }

    public static com.jme.image.Texture loadTexture(java.awt.Image image,
            int minFilter, int magFilter, boolean flipped) {
        com.jme.image.Image imageData = loadImage(image, flipped);
        Texture texture = new Texture();
        texture.setCorrection(Texture.CM_PERSPECTIVE);
        texture.setFilter(magFilter);
        texture.setImage(imageData);
        texture.setMipmapState(minFilter);
        return texture;
    }

    public static com.jme.image.Texture loadTexture(java.awt.Image image,
            int minFilter, int magFilter, float anisoLevel, boolean flipped) {
        com.jme.image.Image imageData = loadImage(image, flipped);
        Texture texture = new Texture(anisoLevel);
        texture.setCorrection(Texture.CM_PERSPECTIVE);
        texture.setFilter(magFilter);
        texture.setImage(imageData);
        texture.setMipmapState(minFilter);
        return texture;
    }

    /**
     * Method nearestPower.
     * <p/>
     * Compute the nearest power of 2 number.  This algorithm is a little strange, but it works quite well.
     *
     * @param value
     *
     * @return int
     */
    protected static int nearestPower(int value) {
        int i;

        i = 1;

        /* Error! */
        if ( value == 0 )
            return -1;

        for ( ; ; ) {
            if ( value == 1 ) {
                return i;
            } else if ( value == 3 ) {
                return i << 2;
            }
            value >>= 1;
            i <<= 1;
        }
    }
    
    public static int getMaxSupportedTxtSize()
    {
        return maxTextureSize;
    }
    
    public static void setMaxSupportedTxtSize(int max)
    {
        maxTextureSize=max;
    }

    public static com.jme.image.Image loadImage(java.awt.Image image, boolean flipImage)
    {
        boolean hasAlpha = hasAlpha(image);
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        // scale textures to it to max supported size and make it power of 2
        int maxSize = getMaxSupportedTxtSize();
        if(maxSize==-1)
        {
            System.out.println(/*throw new JmeException(*/"TextureManager->maxSupportedTextureSize not set! Using 128x128!\n");
            maxSize=128;
        }
        // System.out.println("max texture size: "+maxSize);

        // resize the image if the actual texture size is larger than the hw support
        height = nearestPower(height);
        width = nearestPower(width);

        int newWidth = (maxSize < width ? (maxSize) : (width));
        int newHeight = (maxSize < height ? (maxSize) : (height));

        // Obtain the image data.
        BufferedImage tex = null;
        try
        {
            tex = new BufferedImage(newWidth, newHeight, hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR
                    : BufferedImage.TYPE_3BYTE_BGR);
        }
        catch (IllegalArgumentException e)
        {
            LoggingSystem.getLogger().log(Level.WARNING, "Problem creating buffered Image: " + e.getMessage());
            return null;
        }

        tex.setAccelerationPriority(0.0f);
        image.setAccelerationPriority(0.0f);

        Graphics2D g = (Graphics2D) tex.getGraphics();

        if (newHeight == height && newWidth == width)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        else
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        g.drawImage(image, 0, 0, newWidth, newHeight, null);

        image.flush();
        image = null;

        g.dispose();
        g = null;

        // Get a pointer to the image memory

        int components = (hasAlpha ? 4 : 3);
        byte data[] = (byte[]) tex.getRaster().getDataElements(0, 0, tex.getWidth(), tex.getHeight(), null);
        ByteBuffer scratch = ByteBuffer.allocateDirect(components * tex.getWidth() * tex.getHeight()).order(
                ByteOrder.nativeOrder());

        // flip horizontally

        for (int y = 0; y < newHeight; y++)
            for (int x = 0; x < newWidth * components; x++)
            {
                scratch.put((x + ((newHeight - 1) - y) * newWidth * components), data[(x + y * (newWidth)
                        * components)]);
            }

        data = null;

        com.jme.image.Image textureImage = new com.jme.image.Image();

        textureImage.setType(hasAlpha ? com.jme.image.Image.RGBA8888 : com.jme.image.Image.RGB888);

        textureImage.setWidth(tex.getWidth());
        textureImage.setHeight(tex.getHeight());
        textureImage.setData(scratch);
        return textureImage;
    }
   
    /**
     * <code>loadBMPImage</code> because bitmap is not directly supported by Java, we must load it manually. The
     * requires opening a stream to the file and reading in each byte. After the image data is read, it is used to
     * create a new <code>Image</code> object. This object is returned to be used for normal use.
     * 
     * @param fs
     *            The bitmap file stream.
     * @return <code>Image</code> object that contains the bitmap information.
     */
    private static java.awt.Image loadBMPImage(InputStream fs) {
        try {
            DataInputStream dis = new DataInputStream(fs);
            BitmapHeader bh = new BitmapHeader();
            byte[] data = new byte[dis.available()];
            dis.readFully(data);
            dis.close();
            bh.read(data);
            if (bh.bitcount == 24) {
                return (bh.readMap24(data));
            }
            if (bh.bitcount == 32) {
                return (bh.readMap32(data));
            }
            if (bh.bitcount == 8) {
                return (bh.readMap8(data));
            }
        } catch (IOException e) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Error while loading bitmap texture.");
        }
        return null;
    }

    /**
     * <code>hasAlpha</code> returns true if the specified image has
     * transparent pixels
     * 
     * @param image
     *            Image to check
     * @return true if the specified image has transparent pixels
     */
    public static boolean hasAlpha(java.awt.Image image) {
        if (null == image) {
            return false;
        }
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            return bufferedImage.getColorModel().hasAlpha();
        }
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pixelGrabber.grabPixels();
            ColorModel colorModel = pixelGrabber.getColorModel();
            if (colorModel != null) {
                return colorModel.hasAlpha();
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            System.err.println("Unable to determine alpha of image: " + image);
        }
        return false;
    }

    public static boolean releaseTexture(Texture texture) {
        Collection c = m_tCache.keySet();
        Iterator it = c.iterator();
        TextureKey key;
        Texture next;
        while (it.hasNext()) {
            key = (TextureKey) it.next();
            next = (Texture) m_tCache.get(key);
            if (texture.equals(next)) {
                return releaseTexture(key);
            }
        }
        return false;
    }

    public static boolean releaseTexture(TextureKey tKey) {
        return m_tCache.remove(tKey) != null;
    }

    public static void clearCache() {
        m_tCache.clear();
    }
}
