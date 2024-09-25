package com.jme.app;

public final class AsyncThread extends Thread
{
    private static MultiThreadingGameApp game;
    public void run()
    {
        while(game.isRunning())
        {
            game.renderFrame();
        }      
        game=null;
    }
    
    public AsyncThread(MultiThreadingGameApp game)
    {
        this.game=game;
    }

}
