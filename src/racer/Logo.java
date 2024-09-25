import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

public class Logo extends Node
{
    private static final long serialVersionUID = 1L;

    private Vector2f midPoint;

    
    public Logo(String name, TextureState tex, float logoWidth, float logoHeight)
    {
        super(name);
        init();
        createLogo(tex, logoWidth, logoHeight);
    }

    /**
     * Init basic params of RoundGauge...
     */
    private void init()
    {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        midPoint = new Vector2f(display.getWidth() >> 1, display.getHeight() >> 1);

        // Set the renderstates for RoundGauge to all defaults...
        for (int i = 0; i < RenderState.RS_MAX_STATE; i++)
        {
            setRenderState(defaultStateList[i]);
        }

        // Set a alpha blending state.
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as1.setEnabled(true);
        setRenderState(as1);

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.REPLACE);
    }

    private void createLogo(TextureState tex, float loogWidth, float logoHeight)
    {
        if (tex == null)
            throw new JmeException("Invalid (null) TextureStates array provided to createLogo");
        
        // default texture for th gauge.
        this.setRenderState(tex);

        Quad logo = new Quad("logo", midPoint.x * loogWidth, midPoint.x * logoHeight);
        
        logo.setSolidColor(new ColorRGBA(1, 1, 1, 1));       
        logo.setRenderState(tex);
        
        logo.setStaticVBOIndexEnabled(true);
        logo.setStaticVBOVertexEnabled(true);
        logo.setStaticVBONormalEnabled(true);
        logo.setStaticVBOTextureEnabled(true);
        logo.setStaticVBOColorEnabled(true);
        
        this.attachChild(logo);
    }
    
    /**
     * Get the flare's reference midpoint, usually the center of the screen.
     * 
     * @return Vector2f
     */
    public Vector2f getMidPoint()
    {
        return midPoint;
    }

    /**
     * Set the flare's reference midpoint, the center of the screen by default. It may be useful to change this if the
     * whole screen is not used for a scene (for example, if part of the screen is taken up by a status bar.)
     * 
     * @param midPoint
     *            Vector2f
     */
    public void setMidPoint(Vector2f _midPoint)
    {
        this.midPoint = _midPoint;
    }

    /**
     * <code>updateWorldData</code> updates all the children maintained by this node. It decides where on the screen
     * the flare reference point should be (by the Gauges <i>worldTranslation</i>) and updates the children
     * accordingly.
     * 
     * @param time
     *            the frame time.
     */
    @Override
    public void updateWorldData(float time)
    {
        super.updateWorldData(time);

    }

    /**
     * Calls Node's attachChild after ensuring child is a FlareQuad.
     * 
     * @see com.jme.scene.Node.attachChild(Spatial)
     * @param spat
     *            Spatial
     * @return int
     */
    @Override
    public int attachChild(Spatial spat)
    {
        if (!(spat instanceof Quad))
            throw new JmeException("Only children of type Quad may be attached to RoundGauge.");
        return super.attachChild(spat);
    }
}
