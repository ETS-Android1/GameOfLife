package com.zmachsoft.gameoflife;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import com.zmachsoft.gameoflife.world.GameWorld;
import com.zmachsoft.gameoflife.world.NoChangeException;

public class GameThread extends Thread
{
	private final static int INFINITY = 3600000;
	
	private boolean running = false;
	private GameBoard canvas = null;
	private GameWorld world = null;
	private SurfaceHolder surfaceHolder = null;
	private boolean loop = false;
	private boolean renderOnce = false; 
	private int sleepTime = 0;

	public GameThread(GameBoard canvas, GameWorld world)
	{
		super();
		this.sleepTime = 500;
		this.canvas = canvas;
		this.world = world;
		this.surfaceHolder = canvas.getHolder();
	}
	
	public void updateWorld(GameWorld newWorld)
	{
		this.world = newWorld;
	}

	public void startThread()
	{
		running = true;
		super.start();
	}

	public void stopThread()
	{
		running = false;
	}

	@SuppressLint("WrongCall")
	public void run()
	{
		Canvas c = null;
		while (running)
		{
			c = null;
			try
			{
				c = surfaceHolder.lockCanvas();
				synchronized (surfaceHolder)
				{
					if (c != null)
					{
						// draw game board
						canvas.onDraw(c);
					}
				}
			}
			finally 
			{
				// do this in a finally so that if an exception is thrown
				// we don't leave the Surface in an inconsistent state
				if (c != null) 
				{
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
			
			// sleep
			try
			{
				if (loop)
				{
					Log.i("GOL", "Thread sleeps for " + sleepTime);
					sleep(sleepTime);
				}
				else
					sleep(INFINITY);
			} 
			catch (InterruptedException e)
			{
			}
			
			// compute world's next step
			if (!renderOnce)
			{
				try
				{
					world.nextStep();
				} 
				catch (NoChangeException e)
				{
					Log.i("GOL", "No more change detected - stop thread");
					stopThread();
				}
			}
			renderOnce = false;
		}
	}

	public boolean isLoop()
	{
		return loop;
	}

	public void setLoop(boolean loop)
	{
		this.loop = loop;
	}

	public void setRenderOnce(boolean renderOnce)
	{
		this.renderOnce = renderOnce;
	}

	public int getSleepTime()
	{
		return sleepTime;
	}

	public void setSleepTime(int sleepTime)
	{
		this.sleepTime = sleepTime;
	}

}