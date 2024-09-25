import com.jme.math.FastMath;
import com.jme.math.Quaternion;
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

public class RoundGauge extends Node
{

    private static final long serialVersionUID = 1L;
    
    private Vector2f midPoint;
    private float correctionAngle = 360 * FastMath.DEG_TO_RAD;
    private float startPoint = 0;
    private float gaugeRange = 0;
    private float percent = 0;
    
    private Quad pointer;

    public RoundGauge(String name, TextureState tex[], float gaugeSize, float pointerWidth, float pointerHeight)
    {
        super(name);
        init();
        createGauge(tex, gaugeSize, pointerWidth, pointerHeight);
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
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);
        setRenderState(as1);

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.REPLACE);
    }

    private void createGauge(TextureState tex[], float gaugeSize, float pointerWidth, float pointerHeight)
    {
        if (tex == null)
            throw new JmeException("Invalid (null) TextureStates array provided to createGauge");
        else
            if(tex.length!=2)
                throw new JmeException("RoundGauge requires 2 textures!");
        
        // default texture for th gauge.
        this.setRenderState(tex[0]);

        Quad gauge = new Quad("gauge", midPoint.x * gaugeSize, midPoint.x * gaugeSize);
        
        gauge.setSolidColor(new ColorRGBA(128f, 128f, 128f, 1f));       
        gauge.setRenderState(tex[0]);
        
        gauge.setStaticVBOIndexEnabled(true);
        gauge.setStaticVBOVertexEnabled(true);
        gauge.setStaticVBONormalEnabled(true);
        gauge.setStaticVBOTextureEnabled(true);
        gauge.setStaticVBOColorEnabled(true);
        
        this.attachChild(gauge);
        
        
//      default texture for the pointer.
        this.setRenderState(tex[1]);
        pointer = new Quad("pointer", midPoint.x * pointerWidth, midPoint.x * pointerHeight);
        
        pointer.setSolidColor(new ColorRGBA(128f, 128f, 128f, 1f));
        pointer.setRenderState(tex[1]);
        
        pointer.setStaticVBOIndexEnabled(true);
        pointer.setStaticVBOVertexEnabled(true);
        pointer.setStaticVBONormalEnabled(true);
        pointer.setStaticVBOTextureEnabled(true);
        pointer.setStaticVBOColorEnabled(true);
        
        this.attachChild(pointer);
        
    }
    
    public void setRange(float startAngle, float endAngle)
    {
        startPoint=startAngle;
        gaugeRange=endAngle;
        
        pointer.setLocalRotation(new Quaternion(new float[] { 0, 0, correctionAngle - startPoint * FastMath.DEG_TO_RAD }));        
    }
    
    Quaternion rotation = new Quaternion();
    float[] angles = new float[3];
    public void setPercent(float percent)
    {
        if(percent<0f)
            percent=0f;
        else
            if(percent>100f)
                percent=100f;
        
        this.percent=percent;
        
        angles[0]=0f;
        angles[1]=0f;
        angles[2]=correctionAngle - (startPoint + gaugeRange * (0.01f * percent)) * FastMath.DEG_TO_RAD;
        
        rotation.fromAngles(angles);
        pointer.setLocalRotation(rotation);
    }
    
    public float getPercent()
    {
        return percent;
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
    public void setMidPoint(Vector2f midPoint)
    {
        this.midPoint = midPoint;
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
