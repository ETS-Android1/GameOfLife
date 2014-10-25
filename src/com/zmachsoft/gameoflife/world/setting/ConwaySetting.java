package com.zmachsoft.gameoflife.world.setting;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;


public class ConwaySetting extends WorldSetting
{
	public ConwaySetting()
	{
		super(WorldType.CONWAY);
	}

	/**
	 * Initial density of board
	 */
	private int density = 50;

	/**
	 * @param setting
	 * @return true if there is any difference between curent setting end given one
	 */
	@Override
	public boolean hasChanged(WorldSetting setting)
	{
		if (!(setting instanceof ConwaySetting))
			return true;
		ConwaySetting sSetting = (ConwaySetting) setting;
		return super.hasChanged(setting) || (sSetting.getDensity()!=density);
	}

	public int getDensity()
	{
		return density;
	}

	public void setDensity(int density)
	{
		this.density = density;
	}	
	
	/**
	 * Prepare CONWAY settings panel's item events bindings 
	 */
	public static void prepareConwayPanel(Activity activity)
	{
		final TextView nbText = (TextView) activity.findViewById(R.id.conway_density_text);
		SeekBar choice = (SeekBar) activity.findViewById(R.id.conway_density);
		choice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				int iValue = progress + 1;
				nbText.setText("Density : " + iValue);
			}
		});
	}
	
	/**
	 * @return a CONWAY setting created from user's options in displayed panels 
	 */
	public static WorldSetting createConwaySettingFromForm(Activity activity)
	{
		WorldSetting currentSetting = null;
		
		currentSetting = new ConwaySetting();

		// density
		SeekBar choice = (SeekBar) activity.findViewById(R.id.conway_density);
		((ConwaySetting)currentSetting).setDensity(choice.getProgress()+1);
		
		return currentSetting;
	}
	
	/**
	 * Fill CONWAY settings panel's item with input settings properties 
	 * @param tSetting
	 */
	public static void fillConwayPanel(WorldSetting tSetting, Activity activity)
	{
		ConwaySetting setting =   (ConwaySetting) tSetting;
		
		// set seekbar's position
		SeekBar choice = (SeekBar) activity.findViewById(R.id.conway_density);
		choice.setProgress(setting.getDensity()-1);

		// set seekbar's label's text
		TextView nbText = (TextView) activity.findViewById(R.id.conway_density_text);
		nbText.setText("Density : " + setting.getDensity());
	}


}