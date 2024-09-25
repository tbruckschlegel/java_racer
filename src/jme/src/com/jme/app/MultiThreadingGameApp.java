package com.jme.app;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import com.jme.util.LoggingSystem;

public abstract class MultiThreadingGameApp extends AbstractGame
{

    // Frame-rate managing stuff
    // protected Timer timer;

    private static boolean skipFrame = false;

    protected ReentrantReadWriteLock rRWLock = new ReentrantReadWriteLock();

    protected Lock writeLock = rRWLock.writeLock();

    protected Lock readLock = rRWLock.readLock();

    private boolean quit = false;

    private AsyncThread physicsThread;

    private boolean isGameReady = false;

    private long preferredTicksPerFrame;

    private long frameStartTick;

    private long frameDurationTicks;

    /**
     * Set preferred frame rate. The main loop will make every attempt to maintain the given frame rate. This should not
     * be called prior to the application being <code>start()</code> -ed.
     * 
     * @param fps
     *            the desired frame rate in frames per second
     */
    public void setFrameRate(int fps)
    {
        if (fps <= 0)
        {
            throw new IllegalArgumentException("Frames per second cannot be less than one.");
        }

        LoggingSystem.getLogger().log(Level.INFO, "Attempting to run at " + fps + " fps.");
        preferredTicksPerFrame = 1000000000 / fps;
    }

    static long sleepDuration = 1000000;
    static long startTime_logic = 0;

    static long stopTime_logic = 0;

    static long startTime_render = 0;

    static long stopTime_render = 0;

    static int fps_render_tmp = 0;

    static int fps_logic_tmp = 0;

    static int fps_logic = 1;

    static int fps_render = 0;

    public void renderFrame()
    {
        if (isGameReady && !finished && !display.isClosing())
        {
            fps_logic_tmp++;
            stopTime_logic = System.nanoTime() - startTime_logic;

            if ((stopTime_logic - startTime_logic) >= (1000000000l / 60l))
            {
                stopTime_logic = startTime_logic = System.nanoTime();
                fps_logic = fps_logic_tmp * 5;
                fps_logic_tmp = 0;

                System.out.println("physics updates per second: " + fps_logic + "/"+ skipFrame);

                skipFrame=false;

            }

            //GlobalLock.writeLock.lock();
            updatePhysics(1.0f / fps_logic); // 120 fps desired
            //GlobalLock.writeLock.unlock();
            syncFrame();
        }
    }

    private void syncFrame()
    {
        frameDurationTicks = System.nanoTime() - frameStartTick;

        if ((frameDurationTicks < preferredTicksPerFrame))
        {
            long sleepTime = (preferredTicksPerFrame - frameDurationTicks) / sleepDuration;
            //LoggingSystem.getLogger().log(Level.INFO, "sleeping: " + ((float)frameDurationTicks/(float)preferredTicksPerFrame)*100f + "%");
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {
                LoggingSystem.getLogger().log(Level.WARNING, "Error sleeping during main loop.");
            }
        }
        else
        {
            //if((frameDurationTicks > preferredTicksPerFrame))
              //  skipFrame=true;
        }
        frameStartTick = System.nanoTime();
    }

    public boolean isRunning()
    {
        return !quit;
    }

    /**
     * Render and update logic at a specified fixed rate.
     */
    //@Override
    public final void start()
    {
        LoggingSystem.getLogger().log(Level.INFO, "Application started.");
        try
        {
            quit = false;
            getAttributes();
            // timer = Timer.getTimer(properties.getRenderer());
            setFrameRate(120); // default to 30 fps

            initSystem();

            assertDisplayCreated();

            initGame();

            calibrateSleepDuration();
            
            //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            physicsThread = new AsyncThread(this);
            //physicsThread.setPriority(Thread.MAX_PRIORITY);
            physicsThread.start();
            isGameReady = true;
            //warmupVM();

            // main loop
            while (!finished && !display.isClosing())
            {
                if (!skipFrame)
                {
                    if (true == GlobalLock.readLock.tryLock())
                    {
                        update(-1.0f);
                        render(-1.0f);
                        display.getRenderer().displayBackBufferAndUnlock();
                    }
                }                
                else
                {
                    display.getRenderer().clearRenderer();
                    
                    if (true == GlobalLock.readLock.tryLock())
                    {
                        update(-1.0f);
                        GlobalLock.readLock.unlock();
                    }
                }
            }

            quit = true;

        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            cleanup();
        }
        LoggingSystem.getLogger().log(Level.INFO, "Application ending.");

        display.reset();
        quit();
    }

    /**
     * Quits the program abruptly using <code>System.exit</code>.
     * 
     * @see AbstractGame#quit()
     */
    protected void quit()
    {
        quit = true;
        if (display != null)
        {
            display.close();
        }
        System.exit(0);
    }

    protected void calibrateSleepDuration()
    {
        long start = System.nanoTime();
        
        for(int i=0; i<32; i++)
        {
            LoggingSystem.getLogger().log(Level.INFO, "sleeping: " + ((float)frameDurationTicks/(float)preferredTicksPerFrame)*100f + "%");
            try
            {
                LoggingSystem.getLogger().log(Level.INFO, "calibrating sleep counter..."+(i/0.32f)+"%");
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                LoggingSystem.getLogger().log(Level.WARNING, "Error sleeping during main loop.");
            }
        }
        long end = System.nanoTime();

        //System.out.println("non calibrated sleep duration: "+sleepDuration+ " v1: "+((end-start)/(32*1000*1000))+" v2: "+(((end-start)-(32l*1000l*1000l*100l))/1000l)+" v3: "+(end-start) );
        
        
        sleepDuration+=Math.round(((end-start) / 32000000.0f));

        LoggingSystem.getLogger().log(Level.INFO, "calibrated sleep duration: "+sleepDuration);

    }
    protected abstract void updatePhysics(float tpf);

    /**
     * @param interpolation
     *            unused in this implementation
     * @see AbstractGame#update(float interpolation)
     */
    protected abstract void update(float interpolation);

    /**
     * @param interpolation
     *            unused in this implementation
     * @see AbstractGame#render(float interpolation)
     */
    protected abstract void render(float interpolation);

    /**
     * @see AbstractGame#initSystem()
     */
    protected abstract void initSystem();

    /**
     * @see AbstractGame#initGame()
     */
    protected abstract void initGame();

    /**
     * @see AbstractGame#reinit()
     */
    protected abstract void reinit();

    /**
     * @see AbstractGame#cleanup()
     */
    protected abstract void cleanup();

    /**
     * @see AbstractGame#warmupVM()
     */
    protected abstract void warmupVM();
}