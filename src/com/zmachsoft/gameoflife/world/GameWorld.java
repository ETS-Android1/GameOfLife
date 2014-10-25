package com.zmachsoft.gameoflife.world;

import com.zmachsoft.gameoflife.world.setting.WorldSetting;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Master class of all game worlds.
 * 
 * @author Master
 *
 */
public abstract class GameWorld
{
	public enum WorldType
	{
		CONWAY,
		SHELLING,
		EPIDEMIC,
		WAR
	}
	
	protected static int[] allColors = new int[] {Color.GRAY, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.CYAN};
	
	private static int UNIQUE_ID = 1;
	protected int uniqueId = UNIQUE_ID++;
	
	protected WorldSetting setting;
	
	protected GameWorld(WorldSetting setting)
	{
		this.setting = setting;
	}

	/**
	 * A world should be able to init its content
	 */
	public abstract void initContent();
	/**
	 * A world should be able to compute next step of its state
	 * @throws NoChangeException
	 */
	public abstract void nextStep() throws NoChangeException;
	/**
	 * A world should be able to render itself on the input canvas 
	 */
	public abstract void render(Canvas canvas);

	public WorldSetting getSetting()
	{
		return setting;
	}
}
