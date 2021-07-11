package com.zmachsoft.gameoflife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameBoard extends SurfaceView implements SurfaceHolder.Callback
{
	/**
	 * The thread that manage the board rendering 
	 */
	private GameThread thread = null;

	public GameBoard(Context context)
	{
		super(context);
		init(context);
	}

	public GameBoard(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public GameBoard(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	private void init(Context context)
	{
		Log.i("GOL", "World init");

		// make this class ass listener of all surface callbacks
		getHolder().addCallback(this);
		
		initWorld();
	}
	
	/**
	 * Init world content
	 */
	public void initWorld()
	{
		// init the world
		GameOflife.getInstance().initWorld(null);
		
		// don't forget to update the world to manage with game thread
		if (thread!=null)
			thread.updateWorld(GameOflife.getInstance().getWorld());
	}
	
	public void renderOnce()
	{
		// only awake the thread - it will refresh the surface once then sleep again
		if (thread!=null)
		{
			thread.setRenderOnce(true);
			thread.interrupt();
		}
	}
	
	public void onDraw(Canvas canvas) 
	{
		Log.i("GOL", "Surface onDraw");
		// erase everything
		canvas.drawColor(Color.WHITE);
		
		// render the world
		GameOflife.getInstance().render(canvas);
	}

	public void createGame()
	{
		if (thread == null)
		{
			thread = new GameThread(this, GameOflife.getInstance().getWorld());
			thread.startThread();
		}
	}

	public void startGame()
	{
		if (thread == null)
			createGame();
		
		thread.setLoop(true);
		thread.interrupt();	// awake the thread
	}

	public void pauseGame()
	{
		if (thread!=null)
			thread.setLoop(false); // will cause th thread to sleep for a long time
	}

	public void stopGame()
	{
		Log.i("GOL", "Stopping thread ...");
		if (thread != null)
		{
			thread.setRenderOnce(true);	// so it will not call next step
			thread.stopThread();

			// Waiting for the thread to die by calling thread.join,
			// repeatedly if necessary
			boolean retry = true;
			while (retry)
			{
				thread.interrupt();
//				try
//				{
//					thread.join();
//					retry = false;
//				} 
//				catch (InterruptedException e)
//				{
//				}
				retry = false;
			}
			thread = null;
			Log.i("GOL", "Thread stopped");
		}
	}

	/**
	 * @param speed : between 0 (slowest) and 100% (fastest)
	 */
	public void setSpeed(int speed)
	{
		// compute a sleep time for game thread
		int sleepMin = 0;
		int sleepMax = 500;	// in ms
		int sleepTime = (100-speed)*(sleepMax-sleepMin)/100;
		if (thread!=null)
			thread.setSleepTime(sleepTime);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
	}

	@SuppressLint("WrongCall")
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.i("GOL", "Surface created");
		createGame();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.i("GOL", "Surface destroyed");
		stopGame();
	}
}
