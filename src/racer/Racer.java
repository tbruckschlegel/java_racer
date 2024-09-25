/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of
 * the Mojo Monkey Coding, jME, jMonkey Engine, nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import camera.CameraHandler;

import com.jme.app.MultiThreadingGameApp;
import com.jme.app.GlobalLock;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.OBB2;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.intersection.BoundingCollisionResults;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.light.DirectionalLight;
import com.jme.light.LightNode;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.CloneCreator;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;
import com.jmex.effects.ParticleManager;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsWorld;
import com.jmex.physics.StaticPhysicsObject;
import com.jmex.physics.types.PhysicsCylinder;
import com.jmex.physics.vehicle.Car;

/**
 * @author Ahmed
 */
                           
public class Racer extends MultiThreadingGameApp
{
    private LightState lightState = null;

    private LightNode lightNode = null;

    private LensFlare flare = null;

    private RoundGauge RPMGauge = null;

    private RoundGauge SpeedGauge = null;

    private RectGauge ClutchGauge = null;

    private CarPhysicsCallback carPhysicsHandler = null;

    private Node smokeNode[] = new Node[4];

    private ParticleManager smoke[] = new ParticleManager[4];

    private Node chassisNode = null;

    private Node carNode = null;

    private Box startLine = null;

    private Box finishLine = null;

    private Logo tagHeuer = null;

    private Logo speedDays = null;

    private Node wheelNode[] = new Node[4];

    private Camera cam = null;

    private CameraHandler cameraHandler = null;

    private InputHandler input = null;

    private Node textNode = null;

    private Node rootNode = null;

    private Node uiNode = null;

    private Node guiNode = null;

    private Text speedT = null;

    private Car car = null;

    private Skybox skybox = null;

    private Spatial road = null;

    private Text timerText = null;

    private Text bestTimerText = null;

    private Text gear = null;

    private float raceTime = 0;

    private float bestRaceTime = Float.MAX_VALUE;

    private TextureState font = null;

    private Node gameOverNode = null, startGameNode = null, instructNode = null;

    private CollisionResults collisionResults = new BoundingCollisionResults();

    private boolean raceReset = false;

    private boolean raceStart = false;

    private boolean raceFinished = false;

    static long startTime_logic = 0;
    static long stopTime_logic = 0;
    static int fps_logic_tmp = 0;
    static int fps_logic = 0;

    static long startTime_render = 0;
    static long stopTime_render = 0;
    static int fps_render_tmp = 0;
    static int fps_render = 0;

    private boolean lensFlareVisible = false;

    static boolean particleRestart[] = new boolean[4];

    private Vector3f tmpVector = new Vector3f();

    private Vector3f tmpVector2 = new Vector3f();

    private Vector3f tmpVelocity = new Vector3f();

    private float tpf_logic = 0;

    int mySpecialConsoleCounter=0;
    
    private boolean physicsUpdateOccured=false;
    
    @Override
    protected void updatePhysics(float tpf)
    {
        GlobalLock.writeLock.lock();
        rootNode.updateGeometricState(tpf, true);
        GlobalLock.writeLock.unlock();
        GlobalLock.writeLock.lock();
        lightNode.updateGeometricState(tpf, true);
        GlobalLock.writeLock.unlock();
        GlobalLock.writeLock.lock();
        input.update(tpf);
        GlobalLock.writeLock.unlock();
        
        //GlobalLock.writeLock.lock();
        car.update(tpf);
  //      GlobalLock.writeLock.unlock();
        
     //   GlobalLock.writeLock.lock();
        PhysicsWorld.getInstance().update(tpf);
       // GlobalLock.writeLock.unlock();
        physicsUpdateOccured = true;
    }
    
    @Override
    protected void update(float tpf)
    {
        fps_logic_tmp++;
        stopTime_logic = System.nanoTime();
        if ((stopTime_logic - startTime_logic) >= (1000000000l / 60l))
        {
            stopTime_logic = startTime_logic = System.nanoTime();
            fps_logic = fps_logic_tmp*60;
            fps_logic_tmp = 0;
            
            if (fps_logic != 0)
                tpf_logic = 1.0f / fps_logic;
            //System.out.println("game logic updates per second: "+fps_logic);
        }

        initResetLogic(tpf_logic);

        updateTimer(tpf_logic * 1000f*60f);

        updateSkidding(tpf_logic);

        updateStartFinishLogic();        

        updateTimerLabel();

        updateLensFlare();

        updateGauges();
        
        //speedT.print("pos:" + chassisNode.getLocalTranslation() + " rot:" + chassisNode.getLocalRotation().toString());
        speedT.print("fps+logic/speed/rpm/gear: " + fps_logic + " / "
                + car.getChassis().getLinearVelocity().length()/* * 3.6 * 4*/+ " / " + car.getCurrentRPM() + " / "
                + car.getCurrentGear());

        //cameraHandler.setSpeed(car.getChassis().getLinearVelocity().length()<5.5f ? 5.5f :car.getChassis().getLinearVelocity().length());

        //input.update(tpf_logic);

        cameraHandler.updateOpenGL();
                
    }

    void initResetLogic(float tpf)
    {
        if (raceReset || KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_smoothing", false))
        {
            //System.out.println("start: "+car.getChassis().getLinearVelocity().length());

            raceReset = true;
            raceStart = false;
            raceTime = 0;

            car.getChassis().resetForces();
            car.getWheels()[0].resetForces();
            car.getWheels()[1].resetForces();
            car.getWheels()[2].resetForces();
            car.getWheels()[3].resetForces();

            car.setCurrentGear(0);
            car.setBrakePedal(1);
            car.setSteeringWheel(0);
            car.setGasPedal(0);
            car.setNeutrall();
            car.update(tpf);

            car.getChassis().getSpatial().setLocalRotation(new Quaternion(car.getStartRotation()));
            car.getChassis().getSpatial().setLocalTranslation(new Vector3f(car.getStartPosition()));

            car.getChassis().resetForces();
            car.getWheels()[0].resetForces();
            car.getWheels()[1].resetForces();
            car.getWheels()[2].resetForces();
            car.getWheels()[3].resetForces();
            
            car.getChassis().syncWithGraphical();
            car.getWheels()[0].syncWithGraphical();
            car.getWheels()[1].syncWithGraphical();
            car.getWheels()[2].syncWithGraphical();
            car.getWheels()[3].syncWithGraphical();
            car.update(tpf);

            GlobalLock.readLock.unlock();

            physicsUpdateOccured=false;
            while(!physicsUpdateOccured)
            {
                car.update(tpf);
                try
                {
                    Thread.sleep(15);
                }
                catch (InterruptedException e)
                {
                    LoggingSystem.getLogger().log(Level.WARNING, "Error sleeping during main loop.");
                }
            }

            GlobalLock.readLock.lock();

            finishResetLogic();

            //carNode.updateGeometricState(1, true);

        }
    }

    void updateSkidding(float tpf)
    {
        // calculate smoke for skidding
        for (int i = 0; i < 4; i++)
        {
            if (car.isSkidding(i))
            {

                if (particleRestart[i])
                {
                    smoke[i].setRepeatType(Controller.RT_CYCLE);
                    smoke[i].forceRespawn();
                    particleRestart[i] = false;
                }

                smokeNode[i].setLocalTranslation(car.getWheels()[i].getSpatial().getLocalTranslation());

                tmpVector.set(car.getWheels()[i].getSpatial().getLocalRotation().getRotationColumn(2));
                tmpVector2.set(0, ((i % 2) == 0) ? -1 : 1, 0);
                tmpVector = tmpVector.crossLocal(tmpVector2);
                tmpVelocity.set(car.getWheels()[i].getLinearVelocity());
                //tmpVelocity.set(new Vector3f(5, 0, 0));
                tmpVector.subtractLocal(tmpVelocity);

                ColorRGBA color = smoke[i].getStartColor();
                color.a = car.getSkiddingValue(i)*0.5f;// * 0.05f;
                //color.a = color.a > 0.1f ? 0.1f : color.a;

                smoke[i].setStartColor(color);
                smoke[i].setEmissionDirection(tmpVector);
                smoke[i].setEndSize(tmpVelocity.length() > 15 ? 15 : tmpVelocity.length());
                smoke[i].update(tpf);

            }
            else
            {
                if (smoke[i].isActive())
                {
                    particleRestart[i] = true;
                    smoke[i].setRepeatType(Controller.RT_CLAMP);
                    smoke[i].update(tpf);
                }
            }
        }

        for (int i = 0; i < 4; i++)
            if (smoke[i].isActive())
                smokeNode[i].updateGeometricState(tpf, true);
    }

    void updateGauges()
    {
        RPMGauge.setPercent(100f * (car.getCurrentRPM() - car.getMinRPM()) / (car.getMaxRPM() - car.getMinRPM()));
        SpeedGauge.setPercent(100f * (car.getChassis().getLinearVelocity().length() * 3.6f * 4f) / (250f));
        ClutchGauge.setPercent(car.getClutchPosition() * 100);
        if (car.getClutchPosition() < 1)
            gear.setTextColor(new ColorRGBA(0f, 1f, 0f, 1f));
        else
            gear.setTextColor(new ColorRGBA(1f, 0f, 0f, 1f));
    }

    void finishResetLogic()
    {
        //System.out.println("end("+physicsUpdateOccured+"): "+car.getChassis().getLinearVelocity().length());
        if (raceReset)
        {
     
            Vector3f pos = new Vector3f(car.getChassis().getSpatial().getLocalTranslation());
            Quaternion rot = new Quaternion(car.getChassis().getSpatial().getLocalRotation());

            if (FastMath.floor(0.5f+FastMath.abs(car.getChassis().getLinearVelocity().length()))<=1f &&
                    pos.x == car.getStartPosition().x &&
                    pos.y == car.getStartPosition().y &&
                    pos.z == car.getStartPosition().z &&
            
            rot.w==car.getStartRotation().w &&
            rot.y==car.getStartRotation().y &&
            rot.z==car.getStartRotation().z &&
            rot.w==car.getStartRotation().w)
            {
                raceReset = false;
            }                        
                //System.out.println("xs:"+pos.x+" ys:"+pos.y+" zs:"+pos.z);
                //System.out.println("xi:"+car.getStartPosition().x+" yi:"+car.getStartPosition().y+" zi:"+car.getStartPosition().z);
                //System.out.println("xs:"+rot.x+" ys:"+rot.y+" zs:"+rot.z+" ws:"+rot.w);
                //System.out.println("xi:"+car.getStartRotation().x+" yi:"+car.getStartRotation().y+" zi:"+car.getStartRotation().z+" wi:"+car.getStartRotation().w);
        }
    }

    Ray lensFlareRay = new Ray();
    PickResults pickResults = new BoundingPickResults();

    void updateLensFlare()
    {

        lensFlareRay.setOrigin(cam.getLocation());
        lensFlareRay.setDirection(lightNode.getLocalTranslation());

        pickResults.clear();
        rootNode.findPick(lensFlareRay, pickResults);
        lensFlareVisible = pickResults.getNumber() > 4 ? false : true;

    }

    void updateStartFinishLogic()
    {
        if (!raceStart && carNode!=null && collisionResults!=null && startLine!=null)
        {
            collisionResults.clear();
            startLine.calculateCollisions(carNode, collisionResults);

            if (collisionResults.getNumber() > 0)
            {
                raceStart = true;
                raceTime = 0;

                //startLine.setSolidColor(new ColorRGBA(1, 0, 0, 1));
                //startLine.updateColorBuffer();
            }
            /*
             else
             {
             startLine.setSolidColor(new ColorRGBA(1, 1, 1, 1));
             startLine.updateColorBuffer();

             }
             */
        }
        else
        if(carNode!=null && collisionResults!=null && startLine!=null)
        {
            collisionResults.clear();
            finishLine.calculateCollisions(carNode, collisionResults);
            if (collisionResults.getNumber() > 0)
            {
                raceStart = false;
                if (raceTime < bestRaceTime)
                {
                    bestRaceTime = raceTime;
                    raceTime = 0;
                    updateBestTimerLabel();
                }

                //finishLine.setSolidColor(new ColorRGBA(0, 1, 0, 1));
                //finishLine.updateColorBuffer();
            }
            /*
             else
             {
             finishLine.setSolidColor(new ColorRGBA(1, 1, 1, 1));
             finishLine.updateColorBuffer();
             
             }
             */
        }
    }

    @Override
    protected void render(float arg0)
    {       
        fps_render_tmp++;
        stopTime_render = System.nanoTime();
        if ((stopTime_render - startTime_render) >= 1000000000l)
        {
            stopTime_render = startTime_render = System.nanoTime();
            fps_render = fps_render_tmp;
            fps_render_tmp = 0;
            
            System.out.println("rendered frames second: "+fps_render);
        }
        display.getRenderer().clearBuffers();

        //TODO: lens flare culled ?
        // draw lens flare
        /*
        if (lensFlareVisible && flare != null)
        {
            flare.setForceView(true);
            flare.setForceCull(false);
        }
        else
        {
            flare.setForceView(false);
            flare.setForceCull(true);

        }
        */
        display.getRenderer().draw(rootNode);

        // draw skidding
        for (int i = 0; i < 4; i++)
        {
            if (smoke[i].isActive())
            {
                if (car.getWheels()[i].getContact())
                {
                    display.getRenderer().draw(smokeNode[i]);
                }
            }
        }

        //display.getRenderer().drawBounds(rootNode);

        //display.getRenderer().draw(textNode);

        //cameraHandler.drawCameraPath();
    }

    protected void initLogos()
    {
        TextureState[] tex = new TextureState[2];
        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/th.png"),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[0].setEnabled(true);
        tex[0].apply();

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/sd.png"),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[1].setEnabled(true);
        tex[1].apply();

        tagHeuer = new Logo("tagHeuer", tex[0], 0.25f, 0.25f);

        tagHeuer.setLocalTranslation(new Vector3f((display.getWidth() * 0.5f) + (timerText.getWidth() * 0.85f), display
                .getHeight()
                - timerText.getHeight(), 0));

        //        timerText.getLocalTranslation().set((display.getWidth() * 0.5f) - (timerText.getWidth() * 0.5f),
        //              display.getHeight() - timerText.getHeight(), 0);

        rootNode.attachChild(tagHeuer);

        
        //FIXME: BETA
        //Sphere s = new Sphere("test", 256, 256, 100);
        //s.setSolidColor(new ColorRGBA(1,0,0,1));
        //s.setLightCombineMode(lightState.OFF);
        //s.setLocalTranslation(new Vector3f(-900, -50, 0));
        
        //VertexUtil.optimizeForCache(s);
        //rootNode.attachChild(s);
        
        
        speedDays = new Logo("speedDays", tex[1], 0.5f, 0.5f);

        speedDays.setLocalTranslation(new Vector3f(display.getWidth() * 0.15f, display.getHeight()
                - timerText.getHeight() * 1.2f, 0));

        rootNode.attachChild(speedDays);

    }

    protected void initGauges()
    {
        TextureState[] tex = new TextureState[2];
        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/rpm.png"),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[0].setEnabled(true);
        tex[0].apply();

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource(
                "gamedata/pointer.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[1].setEnabled(true);
        tex[1].apply();

        RPMGauge = new RoundGauge("rpm", tex, 0.5f, 0.00625f, 0.4f);
        RPMGauge.setLocalTranslation(new Vector3f(display.getWidth() / 3.0f, display.getHeight() / 5f, 0));
        RPMGauge.setRange(265/*start*/, 208/*range*/);
        rootNode.attachChild(RPMGauge);

        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture(
                this.getClass().getClassLoader().getResource("gamedata/speed.png"), Texture.MM_LINEAR_LINEAR,
                Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[0].setEnabled(true);

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource(
                "gamedata/pointer.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[1].setEnabled(true);
        tex[1].apply();

        SpeedGauge = new RoundGauge("speed", tex, 0.5f, 0.00625f, 0.4f);
        SpeedGauge.setLocalTranslation(new Vector3f(display.getWidth() / 1.5f, display.getHeight() / 5f, 0));
        SpeedGauge.setRange(230/*start*/, 255/*range*/);
        rootNode.attachChild(SpeedGauge);

        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource(
                "gamedata/clutch1.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[0].setEnabled(true);
        tex[0].apply();

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource(
                "gamedata/clutch2.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f, true), 0);
        tex[1].setEnabled(true);
        tex[1].apply();

        ClutchGauge = new RectGauge("clutch", tex, 0.25f, 0.25f, 0.25f);
        ClutchGauge.setLocalTranslation(new Vector3f(display.getWidth() / 2.0f, display.getHeight() / 6f, 0));
        rootNode.attachChild(ClutchGauge);
    }

    // profiler www.yourkit.com
    // tripple buffer opengl
    protected void initLensFlare()
    {

        lightState = display.getRenderer().createLightState();
        lightState.setEnabled(true);

        lightState.detachAll();

        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setAttenuate(false);
        dr.setDiffuse(ColorRGBA.white);
        dr.setAmbient(ColorRGBA.gray);
        dr.setSpecular(ColorRGBA.white);
        //dr.setLocation(new Vector3f(0, 0, 0));
        dr.setDirection(new Vector3f(0, 0, 0).subtractLocal(10000f, 5000f, 10000f));
        lightState.setTwoSidedLighting(true);

        lightNode = new LightNode("light", lightState);
        lightNode.setLight(dr);

        //Vector3f min2 = new Vector3f( -0.5f, -0.5f, -0.5f);
        //Vector3f max2 = new Vector3f(0.5f, 0.5f, 0.5f);
        //Sphere lightSphere = new Sphere("sphere", 32, 32, 2.0f);
        //min2, max2);
        //lightSphere.setSolidColor(new ColorRGBA(1,1,1,1));
        //lightSphere.setLightCombineMode(lightState.OFF);
        //        lightBox.setModelBound(new BoundingBox());
        //      lightBox.updateModelBound();
        //lightNode.attachChild(lightSphere);
        lightNode.setTarget(rootNode);
        lightNode.setLocalTranslation(new Vector3f(10000f, 5000f, 10000f));

        // clear the lights from this lightbox so the lightbox itself doesn't
        // get affected by light:
        //lightSphere.setLightCombineMode(LightState.OFF);

        // Setup the lensflare textures.
        TextureState[] tex = new TextureState[4];
        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader()
                .getResource("gamedata/flare1.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f,
                true), 0);
        tex[0].setEnabled(true);
        tex[0].apply();

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader()
                .getResource("gamedata/flare2.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f,
                true), 0);
        tex[1].setEnabled(true);
        tex[1].apply();

        tex[2] = display.getRenderer().createTextureState();
        tex[2].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader()
                .getResource("gamedata/flare3.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f,
                true), 0);
        tex[2].setEnabled(true);
        tex[2].apply();

        tex[3] = display.getRenderer().createTextureState();
        tex[3].setTexture(TextureManager.loadTexture(this.getClass().getClassLoader()
                .getResource("gamedata/flare4.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888, 1.0f,
                true), 0);
        tex[3].setEnabled(true);
        tex[3].apply();

        flare = LensFlareFactory.createBasicLensFlare("flare", tex);
        flare.setLocalTranslation(lightNode.getLocalTranslation());

        rootNode.attachChild(lightNode);

        // notice that it comes at the end
        rootNode.attachChild(flare);

    }

    @Override
    protected void initSystem()
    {
        try
        {
            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            display.createWindow(properties.getWidth(), properties.getHeight(), properties.getDepth(), properties
                    .getFreq(), properties.getFullscreen());
            cam = display.getRenderer().createCamera(display.getWidth(), display.getHeight());

            // get max texture size
            IntBuffer scratch = BufferUtils.createIntBuffer(16);
            scratch.rewind();
            GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, scratch);
            int maxSize = scratch.get();
            scratch.clear();
            scratch = null;

            TextureManager.setMaxSupportedTxtSize(maxSize);//>32?32:maxSize);
            //display.setVSyncEnabled(true);

            super.setFrameRate(120);
        }
        catch (JmeException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 0.5f, 20000);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);

        cam.setFrame(loc, left, up, dir);
        cam.update();
        display.getRenderer().setCamera(cam);

        KeyBindingManager.getKeyBindingManager().set("toggle_smoothing", KeyInput.KEY_R);
        KeyBindingManager.getKeyBindingManager().set("toggle_grabbing", KeyInput.KEY_G);
    }

    private void initCamera()
    {
       

        float theta = 180 * FastMath.DEG_TO_RAD;
        float phi = 77 * FastMath.DEG_TO_RAD;

        cameraHandler = new CameraHandler(theta, phi, 6f);
        cameraHandler.setMaxDistance(400f);
        cameraHandler.setMinDistance(4f);

        cameraHandler.setTarget(car.getChassis().getSpatial());
    }

    @Override
    protected void initGame()
    {
        rootNode = new Node("rootNode");

        initRootStates();

        uiNode = new Node("UI Node");

        PhysicsWorld.create();
        PhysicsWorld.getInstance().setStepSize(1.0f / 60f);//60f);
        PhysicsWorld.getInstance().setUpdateRate(-1);//190);
        PhysicsWorld.getInstance().setStepFunction(PhysicsWorld.SF_STEP_QUICK);//SIMULATION);//.SF_STEP_QUICK);
        carPhysicsHandler = new CarPhysicsCallback();
        PhysicsWorld.getInstance().setPhysicsCallBack(carPhysicsHandler);
/*
         SoundAPIController.getSoundSystem(properties.getRenderer());
         SoundAPIController.getRenderer().setCamera(cam);
         SoundManager.getManager(); // force sound setup.
         guiNode = new Node("gui");
         guiNode.setForceView(true);
         rootNode.attachChild(guiNode);
*/
        initSkyBox();
        initFloor();
        initWalls();

        initCar();

        initCamera();

        initInput();

        initSmoke();

        initUI();

        initGauges();

        initLogos();

        initLensFlare();

        instructNode.setForceCull(false);
        startGameNode.setForceCull(false);
        gameOverNode.setForceCull(false);
        //whoWonText.setForceCull(false);
        
        VertexUtil.optimizeForCache(uiNode);
        VertexUtil.optimizeForCache(textNode);

        // enable vertex buffer objects                
        VBOutil.enableVBOonTree(rootNode);
        VBOutil.enableVBOonTree(uiNode);
        VBOutil.enableVBOonTree(textNode);

         //for(int i=0; i<4; i++)
         //VBOutil.enableVBOonTree(wheelNode[i]);


        rootNode.updateRenderState();
        rootNode.updateGeometricState(0.0f, true);
        
        uiNode.updateRenderState();
        uiNode.updateGeometricState(0.0f, true);

        textNode.updateRenderState();
        textNode.updateGeometricState(0.0f, true);

    }

    @Override
    protected void warmupVM()
    {
        // Setup alpha state
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as1.setEnabled(true);

        // Now setup font texture
        font = display.getRenderer().createTextureState();
        font.setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/font.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 1.0f, true), 0);
        font.setEnabled(true);

        Text miscText = new Text("misc", "");

        miscText.setTextColor(new ColorRGBA(1, 0, 0, 1));
        miscText.setTextureCombineMode(TextureState.REPLACE);
        //miscText.setLocalScale(display.getWidth()/2);
        //miscText.getLocalTranslation().y = display.getWidth()/2;

        Node tmpNode = new Node("tmpNode");

        ZBufferState zEnabled = display.getRenderer().createZBufferState();
        zEnabled.setFunction(ZBufferState.CF_ALWAYS);//CF_LEQUAL);
        zEnabled.setEnabled(true);

        tmpNode.setRenderState(zEnabled);
        //tmpNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);//.QUEUE_INHERIT);//.QUEUE_OPAQUE);
        display.getRenderer().setBackgroundColor(ColorRGBA.black);

        ShadeState ss = display.getRenderer().createShadeState();
        ss.setShade(ShadeState.SM_SMOOTH);
        tmpNode.setRenderState(ss);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_NONE);//.CS_BACK);
        tmpNode.setRenderState(cs);

        tmpNode.attachChild(miscText);
        tmpNode.setRenderState(font);
        tmpNode.setRenderState(as1);
        tmpNode.setForceView(true);
        tmpNode.setForceCull(false);
        tmpNode.updateRenderState();
        
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        
        //warmup the vm
        while((endTime-startTime)<5000000000l)
        {
            endTime = System.nanoTime();
            GlobalLock.readLock.lock();
            update(-1);
            render(-1);

            display.getRenderer().displayBackBufferWarmUp();
            display.getRenderer().clearBuffers();

            miscText.print("warming up VM and caches..." + ((int) ((endTime-startTime) / 50000000.00f)) + "%");

            display.getRenderer().draw(tmpNode);
            display.getRenderer().displayBackBufferAndUnlock();
        }
        display.getRenderer().clearBuffers();
       
    }

    private void initSkyBox()
    {
        // Create a skybox
        skybox = new Skybox("skybox", 10000, 5000, 10000);

        Texture north = TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/north.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture south = TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/south.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture east = TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/east.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture west = TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/west.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture up = TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/up.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture down = TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/down.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);

        skybox.setTexture(Skybox.NORTH, north);
        skybox.setTexture(Skybox.WEST, west);
        skybox.setTexture(Skybox.SOUTH, south);
        skybox.setTexture(Skybox.EAST, east);
        skybox.setTexture(Skybox.UP, up);
        skybox.setTexture(Skybox.DOWN, down);
        skybox.preloadTextures();
        skybox.setLocalRotation(new Quaternion(new float[] { 0, 145 * FastMath.DEG_TO_RAD, 0 }));
        rootNode.attachChild(skybox);
    }

    private void initFloor()
    {

        try
        {
            TextureState ts = display.getRenderer().createTextureState();
            ts.setEnabled(true);
/*
            MilkToJme C1 = new MilkToJme();
            MaxToJme C2 = new MaxToJme();
            ByteArrayOutputStream BO = new ByteArrayOutputStream();
            C2.convert(new BufferedInputStream(new URL("file:///ftp/road.3ds").openStream()), BO);
            
            
            JmeBinaryReader jbr = new JmeBinaryReader();
            //jbr.setProperty("texurl", this.getClass().getClassLoader().getResource("gamedata/"));
            jbr.setProperty("tex_mm", Integer.valueOf(Texture.MM_LINEAR_LINEAR));
            jbr.setProperty("tex_aniso", Float.valueOf(ts.getMaxAnisotropic()/2f));
            jbr.setProperty("tex_fm", Integer.valueOf(Texture.FM_LINEAR));
            jbr.setProperty("tex_type", Integer.valueOf(Image.GUESS_FORMAT));
            jbr.setProperty("tex_flip", Boolean.valueOf(false));            
            jbr.setProperty("texurl", this.getClass().getClassLoader().getResource("gamedata/"));
            jbr.setProperty("texdir", this.getClass().getClassLoader().getResource("gamedata/"));
            jbr.setProperty("bound", "box");

            // System.out.println(this.getClass().getClassLoader().getResource("gamedata/"));

            Node tmpNode = jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            VertexUtil.smoothNormals(tmpNode, true, true);
            VertexUtil.scaleTextureCoords(tmpNode, new float[] { 1f, 1f }, new float[] { 24f, 1f },
                    new String[] { "backway" });

            Texture t2 = TextureManager.loadTexture(
                    this.getClass().getClassLoader().getResource("gamedata/detail.png"), Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR);
            ts.setTexture(t2, 1);
            t2.setWrap(Texture.WM_WRAP_S_WRAP_T);

            t2.setApply(Texture.AM_COMBINE);
            t2.setCombineFuncRGB(Texture.ACF_ADD_SIGNED);
            t2.setCombineSrc0RGB(Texture.ACS_TEXTURE);
            t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
            t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineScaleRGB(1.0f);

            tmpNode.setRenderState(ts);

            tmpNode.updateGeometricState(0, true);
            tmpNode.updateRenderState();

            tmpNode.setLightCombineMode(LightState.OFF);

            // savt to JME
            JmeBinaryWriter jbw = new JmeBinaryWriter();
            ByteArrayOutputStream BO2 = new ByteArrayOutputStream();
            
       
            jbw.writeScene(tmpNode, BO2);
            FileOutputStream fo = new FileOutputStream("gamedata/road.jme");
            fo.write(BO2.toByteArray());
            fo.flush();
            fo.close();
*/
            JmeBinaryReader jbr = new JmeBinaryReader();
            jbr.setProperty("tex_mm", Integer.valueOf(Texture.MM_LINEAR_LINEAR));
            jbr.setProperty("tex_aniso", Float.valueOf(ts.getMaxAnisotropic()/2f));
            jbr.setProperty("tex_fm", Integer.valueOf(Texture.FM_LINEAR));
            jbr.setProperty("tex_type", Integer.valueOf(Image.RGB888));
            jbr.setProperty("tex_flip", Boolean.valueOf(false));            
            jbr.setProperty("texurl", this.getClass().getClassLoader().getResource("gamedata/"));
            //jbr.setProperty("texdir", this.getClass().getClassLoader().getResource("gamedata/"));
            Node tmpNode = jbr.loadBinaryFormat(new URL(this.getClass().getClassLoader().getResource(
                    "gamedata/road.jme").toString()).openStream());

            VertexUtil.setSolidColor(tmpNode, new ColorRGBA(1,1,1,1));
            VertexUtil.changeTextureState(tmpNode, Texture.AM_COMBINE);
            tmpNode.updateGeometricState(1, true);
            tmpNode.setZOffset(1);
            tmpNode.setLightCombineMode(LightState.OFF);

            
/*
            Texture t2 = TextureManager.loadTexture(
                    this.getClass().getClassLoader().getResource("gamedata/detail.png"), Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR);
            ts.setTexture(t2, 1);
            t2.setWrap(Texture.WM_WRAP_S_WRAP_T);

            t2.setApply(Texture.AM_COMBINE);
            t2.setCombineFuncRGB(Texture.ACF_ADD_SIGNED);
            t2.setCombineSrc0RGB(Texture.ACS_TEXTURE);
            t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
            t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineScaleRGB(1.0f);

            tmpNode.setRenderState(ts);
            */
                       
            tmpNode.updateRenderState();
            
            rootNode.attachChild(tmpNode);

            //tmpNode.setLocalTranslation(new Vector3f(948.8203f,0,70.915f));

            //0.4022500000 1/4 meile
            PhysicsWorld.getInstance().addObject(new StaticPhysicsObject(tmpNode));
            
            /*
            //          This grayscale image will be our terrain
            URL grayScale = this.getClass().getClassLoader().getResource("gamedata/terrain.png");
            //System.out.println(grayScale.toString());

            //  Create an image height map based on the gray scale of our image.
            ImageBasedHeightMap ib = new ImageBasedHeightMap(new ImageIcon(grayScale).getImage());

            TerrainBlock tb = new TerrainBlock("terrain", ib.getSize(), new Vector3f(256f, 2.0f, 256f), ib
                    .getHeightMap(), new Vector3f(-9000, -120.10f, -7592), true);
                    
            tmpNode = new Node("terrain");
            tmpNode.attachChild(tb);
            //tmpNode.clearCurrentStates();

            System.out.println("JmeBinaryWriter start");
            // savt to JME
            JmeBinaryWriter jbw = new JmeBinaryWriter();
            ByteArrayOutputStream BO2 = new ByteArrayOutputStream();
            jbw.writeScene(tmpNode, BO2);
            FileOutputStream fo = new FileOutputStream("gamedata/terrain.jme");
            fo.write(BO2.toByteArray());
            fo.flush();
            fo.close();
            //BinaryToXML btx=new BinaryToXML();
            // Send the .jme binary to System.out as XML
            //btx.sendBinarytoXML(new ByteArrayInputStream(BO2.toByteArray()), new OutputStreamWriter(System.out));
            //System.out.println("JmeBinaryWriter end");
            */
                       

            jbr = new JmeBinaryReader();
            jbr.setProperty("tex_mm", Integer.valueOf(Texture.MM_LINEAR_LINEAR));
            jbr.setProperty("tex_aniso", Float.valueOf(ts.getMaxAnisotropic()/2f));
            jbr.setProperty("tex_fm", Integer.valueOf(Texture.FM_LINEAR));
            jbr.setProperty("tex_type", Integer.valueOf(Image.GUESS_FORMAT));
            jbr.setProperty("tex_flip", Boolean.valueOf(false));            
            jbr.setProperty("texurl", this.getClass().getClassLoader().getResource("gamedata/"));
            //jbr.setProperty("texdir", this.getClass().getClassLoader().getResource("gamedata/"));
            tmpNode = jbr.loadBinaryFormat(new URL(this.getClass().getClassLoader().getResource("gamedata/terrain.jme")
                    .toString()).openStream());

            VertexUtil.optimizeForCache(tmpNode);
            VertexUtil.duplicateTextureCoordinates(tmpNode, 2);
            VertexUtil.scaleTextureCoords(tmpNode, new float[]{1024, 16}, new float[]{1024, 16}, null);
            //VertexUtil.setSolidColor(tmpNode, new ColorRGBA(1,1,1,1));
            
            ts = display.getRenderer().createTextureState();
            ts.setEnabled(true);

            Texture t1 = TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/GRASS.PNG"),
                    Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, ts.getMaxAnisotropic()/2, true);
            ts.setTexture(t1, 0);
            t1.setWrap(Texture.WM_WRAP_S_WRAP_T);

            Texture t2 = TextureManager.loadTexture(this.getClass().getClassLoader().getResource(
                    "gamedata/detail.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
            ts.setTexture(t2, 1);
            t2.setWrap(Texture.WM_WRAP_S_WRAP_T);

            t1.setApply(Texture.AM_COMBINE);
            t1.setCombineFuncRGB(Texture.ACF_MODULATE);
            t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
            t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
            t1.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
            t1.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
            t1.setCombineScaleRGB(1.0f);

            t2.setApply(Texture.AM_COMBINE);
            t2.setCombineFuncRGB(Texture.ACF_MODULATE);
            t2.setCombineSrc0RGB(Texture.ACS_TEXTURE);
            t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
            t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineScaleRGB(1.0f);

            tmpNode.setRenderState(ts);
            tmpNode.updateRenderState();
                

            tmpNode.setLightCombineMode(LightState.OFF);
            PhysicsWorld.getInstance().addObject(new StaticPhysicsObject(tmpNode));

            rootNode.attachChild(tmpNode);

            startLine = new Box("startline", new Vector3f(0, 0, 0), 0.0125f, 5, 10);
            startLine.setModelBound(new BoundingBox());
            startLine.updateModelBound();
            startLine.setLocalTranslation(new Vector3f(-946.44f, -20f, -68.915f));
            startLine.setLightCombineMode(LightState.OFF);
            rootNode.attachChild(startLine);
            startLine.setForceCull(true);
            startLine.setForceView(false);

            finishLine = new Box("finishline", new Vector3f(0, 0, 0), 0.0125f, 5, 10);
            finishLine.setModelBound(new BoundingBox());
            finishLine.updateModelBound();
            finishLine.setLocalTranslation(new Vector3f(-423.8f, -20f, -68.915f));
            finishLine.setLightCombineMode(LightState.OFF);
            rootNode.attachChild(finishLine);
            finishLine.setForceCull(true);
            finishLine.setForceView(false);
/*

            Box d = new Box("referenceline", new Vector3f(0, 0, 0), 2000, 0.05f, 10);
            d.setModelBound(new BoundingBox());
            d.updateModelBound();
            d.setLocalTranslation(new Vector3f(-946.44f+2000, -20f, -68.915f));
            d.setLightCombineMode(LightState.OFF);
            rootNode.attachChild(d);
            d.setForceCull(false);
            d.setForceView(true);
*/

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initWalls()
    {
        /*
         * Box b1 = new Box("Left", new Vector3f(), 1, 800, 640); b1.getLocalTranslation().x = -310f;
         * StaticPhysicsObject obj1 = new StaticPhysicsObject(b1); PhysicsWorld.getInstance().addObject(obj1); Box b2 =
         * new Box("Right", new Vector3f(), 1, 800, 640); b2.getLocalTranslation().x = 310f; StaticPhysicsObject obj2 =
         * new StaticPhysicsObject(b2); PhysicsWorld.getInstance().addObject(obj2); Box b3 = new Box("Top", new
         * Vector3f(), 640, 800, 1); b3.getLocalTranslation().z = 310f; StaticPhysicsObject obj3 = new
         * StaticPhysicsObject(b3); PhysicsWorld.getInstance().addObject(obj3); Box b4 = new Box("Bottom", new
         * Vector3f(), 640, 800, 1); b4.getLocalTranslation().z = -310f; StaticPhysicsObject obj4 = new
         * StaticPhysicsObject(b4); PhysicsWorld.getInstance().addObject(obj4);
         */
    }

    private void initCar()
    {
        carNode = new Node("car");

        /*
         * Cylinder wheelModel_Bound = new Cylinder("wheel", 16, 16, 0.14f, 0.2f); wheelModel_Bound.setModelBound(new
         * OrientedBoundingBox()); wheelModel_Bound.updateModelBound();
         */
        // Create a clone creator so that we can apply the same model to all
        // four wheels.
        // Cylinder c = new Cylinder("", 4, 4, 1.4f, 1.2f);
        // Sphere c = new Sphere("Wheel", 32, 32, 1.6f);
        // //////////////////////////////////////////////
        try
        {

            /*
            MilkToJme C1 = new MilkToJme();
            ByteArrayOutputStream BO = new ByteArrayOutputStream();
            C1.convert(new BufferedInputStream(new URL("file:///ftp/wheel.ms3d").openStream()), BO);
            JmeBinaryReader jbr = new JmeBinaryReader();
            jbr.setProperty("bound", "box");//obb"); // trottel!!!
            jbr.setProperty("texurl", this.getClass().getClassLoader().getResource("gamedata/"));
            //jbr.setProperty("texdir", this.getClass().getClassLoader().getResource("test/"));

            jbr = new JmeBinaryReader();
            Node tmpNode = jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));

            //          save to jme
            JmeBinaryWriter jbw = new JmeBinaryWriter();
            ByteArrayOutputStream BO2 = new ByteArrayOutputStream();
            jbw.writeScene(tmpNode, BO2);
            FileOutputStream fo = new FileOutputStream("gamedata/wheel.jme");
            fo.write(BO2.toByteArray());
            fo.close();
            */
            JmeBinaryReader jbr = new JmeBinaryReader();
            Node tmpNode = jbr.loadBinaryFormat(new URL(this.getClass().getClassLoader().getResource(
            "gamedata/wheel.jme").toString()).openStream());

            
            CloneCreator wheel = new CloneCreator(tmpNode);

            wheel.addProperty("colors");
            wheel.addProperty("texcoords");
            wheel.addProperty("vertices");
            wheel.addProperty("normals");
            wheel.addProperty("indices");

            TextureState ts = display.getRenderer().createTextureState();
            ts.setEnabled(true);

            // Create the four wheels that we will attach to the car.
            DynamicPhysicsObject[] wheels = new DynamicPhysicsObject[4];
            for (int i = 0; i < wheels.length; i++)
            {

                // Create the wheel graphics.
                Spatial wheelGraphics = wheel.createCopy();
                wheelGraphics.setName("Wheel " + i);

                if (i == 2 || i == 0)
                    wheelGraphics.setLocalRotation(new Quaternion(new float[] { 180 * FastMath.DEG_TO_RAD, 0, 0 }));

                // Create the wheel physics.
                wheelNode[i] = new Node("Wheel " + i);
                wheelNode[i].attachChild(wheelGraphics);
                VertexUtil.optimizeForCache(wheelNode[i]);


                wheels[i] = new DynamicPhysicsObject(wheelGraphics, new PhysicsCylinder(0.32f, 0.19f), 2.5f); // 15kg
                carNode.attachChild(wheelNode[i]);
                //rootNode.attachChild(wheelNode[i]);
            }

            // Load the model that makes out the chassis.

            // try{
            /*
            BO = new ByteArrayOutputStream();
            C1 = new MilkToJme();
            C1.convert(new BufferedInputStream(new URL("file:///ftp/car.ms3d").openStream()), BO);
            jbr = new JmeBinaryReader();
            jbr.setProperty("bound", "box");//obb"); // trottel!!!
            jbr.setProperty("texurl", this.getClass().getClassLoader().getResource("gamedata/"));
            //jbr.setProperty("texdir", this.getClass().getClassLoader().getResource("test/"));

            chassisNode = jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            VertexUtil.smoothNormals(chassisNode, true, false);

            // save to jme
            jbw = new JmeBinaryWriter();
            BO2 = new ByteArrayOutputStream();
            jbw.writeScene(chassisNode, BO2);
            fo = new FileOutputStream("gamedata/golf4.jme");
            fo.write(BO2.toByteArray());
            fo.close();
            */
            jbr = new JmeBinaryReader();
            chassisNode = jbr.loadBinaryFormat(new URL(this.getClass().getClassLoader().getResource(
                    "gamedata/golf4.jme").toString()).openStream());
            VertexUtil.optimizeForCache(chassisNode);


            chassisNode.setLocalRotation(new Quaternion(new float[] { 0, 90f * FastMath.DEG_TO_RAD, 0 }));

            Vector3f tmp = new Vector3f(chassisNode.getLocalTranslation());
            //tmpNode.setLocalTranslation(new Vector3f(948.8203f,0,70.915f));

            tmp.x += -948.8203f;
            tmp.z += -70.915f;
            tmp.y += -15f;
            //tmp.y-=15;//25f;//.y-=20;//tmp);
            chassisNode.setLocalTranslation(new Vector3f(tmp));
            chassisNode.setName("Chassis");

            carNode.attachChild(chassisNode);

            carNode.setWorldBound(new OBB2());
            carNode.updateWorldBound();

            DynamicPhysicsObject chassis = new DynamicPhysicsObject(chassisNode, 140f);//350);//140f); // 1400 kg

            // restore save golf4
            //float enginePower = 0; // The power of the cars engine.
            float suspention = 0.00025f;// 0805f; // The suspention constant. A lower value
            boolean fourWheeled = false;// true;
            boolean frontDriven = true;//true;
            Vector3f frontAxleCenter = new Vector3f(1.16f, -0.84f, 0); // The center of
            Vector3f backAxleCenter = new Vector3f(-1.33f, -0.82f, 0); // The center of
            float axleLength = 1.5f; // The length of the axles. They are assumed to
            float brakePower = 50;
            float brakeBalance = 0.75f;

            // System.out.println("fps: ");
            car = new Car(chassis, wheels, brakePower, brakeBalance, suspention, frontDriven, fourWheeled,
                    frontAxleCenter, backAxleCenter, axleLength, (this.getClass().getClassLoader()
                            .getResource("gamedata/golf_engine.png")).getFile(), 1000f, 7000f, 120f, 280f);

            chassisNode.setWorldBound(new OrientedBoundingBox());
            chassisNode.updateWorldBound();

            // Environmental Map (reflection of clouds)
            Texture t1 = TextureManager.loadTexture(
                    this.getClass().getClassLoader().getResource("gamedata/clouds.png"), Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR, ts.getMaxAnisotropic(), true);

            ts.setTexture(t1, 0);

            Texture t2 = TextureManager.loadTexture(
                    this.getClass().getClassLoader().getResource("gamedata/clouds.png"), Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR, ts.getMaxAnisotropic(), true);

            ts.setTexture(t2, 1);

            t1.setApply(Texture.AM_COMBINE);
            t1.setCombineFuncRGB(Texture.ACF_MODULATE);
            t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
            t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
            t1.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
            t1.setCombineOp1RGB(Texture.ACO_SRC_ALPHA);
            t1.setCombineScaleRGB(1f);
            t1.setEnvironmentalMapMode(Texture.EM_SPHERE);

            t2.setApply(Texture.AM_COMBINE);
            t2.setCombineFuncRGB(Texture.ACF_MODULATE);
            t2.setCombineSrc0RGB(Texture.ACS_PRIMARY_COLOR);
            t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
            t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
            t2.setCombineScaleRGB(1f);

            chassisNode.setRenderState(ts);
            chassisNode.updateRenderState();

            rootNode.attachChild(carNode);

            // Send a jME SceneGraph to jME Binary
            
            car.addToWorld();

            car.getChassis().resetForces();
            car.getWheels()[0].resetForces();
            car.getWheels()[1].resetForces();
            car.getWheels()[2].resetForces();
            car.getWheels()[3].resetForces();
            car.getChassis().syncWithGraphical();

            car.setStartPosition(new Vector3f(car.getChassis().getSpatial().getLocalTranslation()));
            car.setStartRotation(new Quaternion(car.getChassis().getSpatial().getLocalRotation()));

            /*          
             Node carNode = new Node("car");
             carNode.attachChild(chassisNode);
             
             for(int i=0; i<4; i++)
             carNode.attachChild(wheelNode[i]);
             
             carNode.setLocalRotation(new Quaternion(new float[] { 90 * FastMath.DEG_TO_RAD, 0, 0 }));
             */

            //.addForce(new Vector3f(0.0F, 50000F, 0.0F));
            //obj.addRelativeTorque(new Vector3f(500F, 0.0F, 0.0F));
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void initInput()
    {
        input = new CarHandler(car, cameraHandler);

        input.setKeySpeed(10f);
    }

    private void initRootStates()
    {

        ZBufferState zEnabled = display.getRenderer().createZBufferState();
        zEnabled.setFunction(ZBufferState.CF_LEQUAL);
        zEnabled.setEnabled(true);

        rootNode.setRenderState(zEnabled);
        //rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

        /*
         DirectionalLight light = new DirectionalLight();// . PointLight();
         light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
         light.setAmbient(new ColorRGBA(0.15f, 0.15f, 0.15f, 1.0f));
         light.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));

         light.setDirection(new Vector3f(100, 100, 100));
         light.setEnabled(true);

         lightState = display.getRenderer().createLightState();
         lightState.setEnabled(true);
         lightState.attach(light);
         rootNode.setRenderState(lightState);
         */
        display.getRenderer().setBackgroundColor(ColorRGBA.black);

        ShadeState ss = display.getRenderer().createShadeState();
        ss.setShade(ShadeState.SM_SMOOTH);
        rootNode.setRenderState(ss);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        rootNode.setRenderState(cs);

        /*
         * FogState fs = display.getRenderer().createFogState(); fs.setDensity(0.0005f); fs.setEnabled(true);
         * fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.55f, 0.0015f)); fs.setEnd(50000); fs.setStart(7500);
         * fs.setDensityFunction(FogState.DF_LINEAR); fs.setApplyFunction(FogState.AF_PER_PIXEL);//.AF_PER_VERTEX);
         * rootNode.setRenderState(fs);
         */

    }

    private void initUI()
    {
        // Setup alpha state
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);

        //NE);//_MINUS_SRC_ALPHA);

        //as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        //as1.setDstFunction(AlphaState.DB_ZERO);//.DB_ONE);
        //as1.setTestEnabled(true);
        //as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);
        /*
         * // Now setup font texture TextureState font = display.getRenderer().createTextureState(); // The texture is
         * loaded from fontLocation
         * font.setTexture(TextureManager.loadTexture(SimpleGame.class.getClassLoader().getResource(
         * "com/jme/app/defaultfont.tga"), Texture.MM_LINEAR, Texture.FM_LINEAR)); font.setEnabled(true);
         */
        // Now setup font texture
        font = display.getRenderer().createTextureState();
        font.setTexture(TextureManager.loadTexture(this.getClass().getClassLoader().getResource("gamedata/font.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 1.0f, true), 0);
        font.setEnabled(true);

        /*
         * // ADD hitpoint bars: float statSize = display.getWidth() * .20f; StatPane sp1 = new StatPane("stats1",
         * ship1, 1, statSize); sp1.setLocalTranslation(new Vector3f((statSize/2)+10, display.getHeight() - (statSize/2) -
         * 50, 0)); guiNode.attachChild(sp1); StatPane sp2 = new StatPane("stats2", ship2, 2, statSize);
         * sp2.setLocalTranslation(new Vector3f(display.getWidth() - (statSize/2) - 10, display.getHeight() -
         * (statSize/2) - 50, 0)); guiNode.attachChild(sp2);
         */
        // Add Player names:
        // Setup font texture
        textNode = new Node("gui text");
        textNode.setRenderState(font);
          textNode.setRenderState(as1);
        textNode.updateRenderState();
         textNode.setLightCombineMode(LightState.OFF);
         textNode.setTextureCombineMode(TextureState.REPLACE);
         rootNode.attachChild(textNode);
        textNode.setForceView(true);

        instructNode = new Node("instructions");
        instructNode.setLightCombineMode(LightState.OFF);
        //rootNode.attachChild(instructNode);
        startGameNode = new Node("start");
        startGameNode.setLightCombineMode(LightState.OFF);
        //rootNode.attachChild(startGameNode);
        gameOverNode = new Node("gameOver");
        gameOverNode.setLightCombineMode(LightState.OFF);
        //rootNode.attachChild(gameOverNode);

        apngtartGameGui();
        addInstructGui();
        addGameOverGui();
        /*
         Text p1Name = new Text("p1name", "tommy1");// ship1.getPlayerName());
         p1Name.setLocalScale(display.getWidth()*0.0015625f);
         p1Name.getLocalTranslation().set(7, display.getHeight() - p1Name.getHeight(), 0);
         p1Name.setForceView(true);
         p1Name.setTextureCombineMode(TextureState.REPLACE);
         textNode.attachChild(p1Name);

         Text p2Name = new Text("p2name", "tommy2");// ship2.getPlayerName());
         p2Name.setLocalScale(display.getWidth()*0.0015625f);
         p2Name.getLocalTranslation().set(display.getWidth() - p2Name.getWidth() - 13,
         display.getHeight() - p2Name.getHeight(), 0);
         p2Name.setForceView(true);
         p2Name.setTextureCombineMode(TextureState.REPLACE);
         textNode.attachChild(p2Name);
         */
        // ScoreManager.getManager().apngcoreFields(textNode);
        gear = new Text("gear", "0");
        //gear.setLocalScale(display.getWidth() * 0.005f);
        gear.setLocalScale(new Vector3f(display.getWidth() * 0.005f, display.getHeight() * 0.006666666f, 1));
        gear.setTextColor(new ColorRGBA(1f, 0f, 0.0f, 1f));
        gear.setSolidColor(new ColorRGBA(1f, 0f, 0.0f, 1f));
        gear.getLocalTranslation().set((display.getWidth() * 0.5f) - (gear.getWidth() * 0.8f),
                display.getHeight() - gear.getHeight() * 8.25f, 0);
        textNode.setTextureCombineMode(TextureState.REPLACE);
        textNode.attachChild(gear);

        bestTimerText = new Text("bestTimerText", "best 00.00:000");
        //bestTimerText.setLocalScale(display.getWidth()*0.00390625f);
        bestTimerText.setTextColor(new ColorRGBA(1f, 1f, 0.27f, 1f));
        bestTimerText.setSolidColor(new ColorRGBA(1f, 1f, 0.27f, 1f));
        bestTimerText.getLocalTranslation().set((display.getWidth() * 0.5f) - (bestTimerText.getWidth() * 0.7f),
                display.getHeight() - bestTimerText.getHeight(), 0);
        textNode.setTextureCombineMode(TextureState.REPLACE);
        textNode.attachChild(bestTimerText);

        timerText = new Text("timerText", "00.00:000");
        //timerText.setLocalScale(display.getWidth()*0.00390625f);
        timerText.setTextColor(new ColorRGBA(1f, 1f, 1f, 1f));
        timerText.getLocalTranslation().set((display.getWidth() * 0.5f) - (timerText.getWidth() * 0.7f),
                display.getHeight() - bestTimerText.getHeight() - timerText.getHeight(), 0);
        textNode.setTextureCombineMode(TextureState.REPLACE);
        textNode.attachChild(timerText);

        updateTimerLabel();

        bestTimerText.setLocalScale((timerText.getWidth() / bestTimerText.getWidth()));

        bestTimerText.setLocalScale(bestTimerText.getLocalScale().mult(display.getWidth() * 0.005f));

        timerText.setLocalScale(timerText.getLocalScale().mult(display.getWidth() * 0.005f));

        bestTimerText.getLocalTranslation().set((display.getWidth() * 0.5f) - (bestTimerText.getWidth() * 0.5f),
                display.getHeight() - bestTimerText.getHeight(), 0);

        timerText.getLocalTranslation().set((display.getWidth() * 0.5f) - (timerText.getWidth() * 0.5f),
                display.getHeight() - bestTimerText.getHeight() - timerText.getHeight(), 0);

        // Then our font Text object.
        // This is what will actually have the text at the bottom.
        Text fps = new Text("FPS label", "Arrow Keys: Move car | Space: Handbrake | Backspace: Flip car ");
        fps.setForceView(true);
        fps.setTextureCombineMode(TextureState.REPLACE);

        speedT = new Text("fps/Speed", "");
        speedT.setLocalScale(display.getWidth() * 0.000732421875f * 2);
        speedT.getLocalTranslation().y = display.getWidth() * 0.01953125f;
        speedT.setForceView(true);
        speedT.setTextureCombineMode(TextureState.REPLACE);

        //textNode.attachChild(fps);
        textNode.attachChild(speedT);
        textNode.setRenderState(as1);
        textNode.setRenderState(font);
    }

    void initSmoke()
    {
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);//.SB_ONE);//_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);//.DB_DST_ALPHA);//.DB_ONE);
        //as1.setTestEnabled(true);
        //as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(this.getClass().getClassLoader()
                .getResource("gamedata/flaresmall.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR), 0);
        ts.setEnabled(true);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_NONE);//NONE);//NONE);//BACK);

        for (int i = 0; i < 4; i++)
        {
            smokeNode[i] = new Node("smoke" + i);
            smokeNode[i].setRenderQueueMode(Renderer.QUEUE_OPAQUE);
            smokeNode[i].setRenderState(cs);
            smokeNode[i].updateRenderState();

            smoke[i] = new ParticleManager(3);
            smoke[i].setRepeatType(Controller.RT_CYCLE);
            smoke[i].setGravityForce(new Vector3f(0.0f, 0.000002f, 0.0f));
            smoke[i].setEmissionDirection(new Vector3f(0.0f, 0.0f, 0.0f));
            smoke[i].setEmissionMaximumAngle(0.0000125f);
            smoke[i].setSpeed(1.0f);

            smoke[i].setParticlesMinimumLifeTime(750.0f);
            smoke[i].setStartSize(0.05f);//0.0025f);
            smoke[i].setEndSize(15f);//1.5f);
            smoke[i].setStartColor(new ColorRGBA(1f, 1f, 1f, 1.0f));//.1f));
            smoke[i].setEndColor(new ColorRGBA(1f, 1f, 1f, 0.0f));
            smoke[i].setRandomMod(0.03f);
            smoke[i].setInitialVelocity(0.0025f);
            // smoke[i].setGeometry(emitDisc);
            smoke[i].setReleaseVariance(0);
            // smoke[i].setReleaseRate(100);

            smoke[i].warmUp(30);
            TriMesh tri1 = smoke[i].getParticles();
            tri1.addController(smoke[i]);

            ZBufferState zbuf = display.getRenderer().createZBufferState();
            zbuf.setWritable(false);
            zbuf.setEnabled(true);
            zbuf.setFunction(ZBufferState.CF_LEQUAL);
            tri1.setRenderState(ts);
            tri1.setRenderState(as1);
            tri1.setRenderState(zbuf);

            smokeNode[i].attachChild(tri1);
            smokeNode[i].updateRenderState();
        }
    }

    @Override
    protected void reinit()
    {
    }

    @Override
    protected void cleanup()
    {
    }

    private void updateTimer(float dt)
    {
        if (raceStart)
            raceTime += dt;
    }

    StringBuffer timeBuf = new StringBuffer();

    StringBuffer bestTimeBuf = null;

    private void updateBestTimerLabel()
    {
        bestTimeBuf = new StringBuffer("best " + timeBuf);

    }

    StringBuffer tmpString = new StringBuffer().append(" ");

    private void updateTimerLabel()
    {
        timeBuf.setLength(0);
        float timeCopy = raceTime;// /1000f;//=1000000;
        int minutes = (int) (timeCopy / (60f * 1000f));

        if (minutes < 10)
            timeBuf.append("0");
        timeBuf.append(minutes);

        timeCopy -= (60f * 1000f * minutes);
        int secs = (int) (timeCopy / (1000f));
        timeBuf.append(".");

        if (secs < 10)
            timeBuf.append("0");
        timeBuf.append(secs);

        timeCopy -= (1000f * secs);

        int milsecs = (int) (timeCopy);
        timeBuf.append(":");

        if (milsecs < 10)
            timeBuf.append("00");
        else
            if (milsecs < 100)
                timeBuf.append("0");
        timeBuf.append(milsecs);

        // System.out.println(timeBuf);
        timerText.print(timeBuf);
        if (bestTimeBuf == null)
            bestTimerText.print("best " + timeBuf);
        else
            bestTimerText.print(bestTimeBuf);

        if (car.getCurrentGear() == 0)
            gear.print("N");
        else
            if (car.getCurrentGear() == -1)
                gear.print("R");
            else
            {
                tmpString.setCharAt(0, Character.forDigit(car.getCurrentGear(), 10));
                gear.print(tmpString);
            }
    }

    private void apngtartGameGui()
    {
        float size = display.getWidth() * .6f;
        Quad playQuad = new Quad("playQuad", size, size);
        playQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        playQuad.setLocalTranslation(new Vector3f((display.getWidth() * .5f), (display.getHeight() * .5f), 0));

        TextureState start = display.getRenderer().createTextureState();
        start.setTexture(TextureManager.loadTexture(FileUtils.loadGUIImage("start_quad.png"), Texture.MM_LINEAR,
                Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 1.0f, true), 0);
        playQuad.setRenderState(start);
        startGameNode.attachChild(playQuad);
    }

    private void addInstructGui()
    {
        float size = display.getWidth() * .6f;
        Quad iQuad = new Quad("playQuad", size, size);
        iQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        iQuad.setLocalTranslation(new Vector3f((display.getWidth() * .5f), (display.getHeight() * .5f), 0));
        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(FileUtils.loadGUIImage("font_rgba.png"), Texture.MM_LINEAR,
                Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 1.0f, true), 0);
        iQuad.setRenderState(ts);
        instructNode.attachChild(iQuad);
    }

    private void addGameOverGui()
    {
        float size = display.getWidth() * .6f;
        Quad iQuad = new Quad("Quad", size, size);
        iQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        iQuad.setLocalTranslation(new Vector3f((display.getWidth() * .5f), (display.getHeight() * .5f), 0));
        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(FileUtils.loadGUIImage("gameover_quad.png"), Texture.MM_LINEAR,
                Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 1.0f, true), 0);
        iQuad.setRenderState(ts);
        iQuad.setZOrder(0);
        gameOverNode.attachChild(iQuad);

         //whoWonText = new Text("whoWon", ""); whoWonText.setLocalScale(1.5f); whoWonText.setZOrder(-10);
         //textNode.attachChild(whoWonText);

    }

    public static void main(String[] args)
    {
        Racer app = new Racer();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG, Racer.class.getClassLoader().getResource("gamedata/info.png"));

        app.start();
    }

}
