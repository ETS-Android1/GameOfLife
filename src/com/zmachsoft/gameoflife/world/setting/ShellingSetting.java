package com.zmachsoft.gameoflife.world.setting;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;


public class ShellingSetting extends WorldSetting
{
	/**
	 * Number of communities
	 */
	private int nbCommunities = 3;

	public ShellingSetting()
	{
		super(WorldType.SHELLING);
	}

	/**
	 * @param setting
	 * @return true if there is any difference between curent setting end given one
	 */
	@Override
	public boolean hasChanged(WorldSetting setting)
	{
		if (!(setting instanceof ShellingSetting))
			return true;
		ShellingSetting sSetting = (ShellingSetting) setting;
		return super.hasChanged(setting) || (sSetting.getNbCommunities()!=nbCommunities);
	}

	public int getNbCommunities()
	{
		return nbCommunities;
	}

	public void setNbCommunities(int nbCommunities)
	{
		this.nbCommunities = nbCommunities;
	}
	
	/**
	 * Prepare SHELLING settings panel's item events bindings 
	 */
	public static void prepareShellingPanel(Activity activity)
	{
		final TextView nbText = (TextView) activity.findViewById(R.id.shelling_nb_population_text);
		SeekBar choice = (SeekBar) activity.findViewById(R.id.shelling_nb_population);
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
				int value = progress + 1; 
				nbText.setText("Number of populations : " + value);
			}
		});
	}
	
	/**
	 * @return a SHELLING setting created from user's options in displayed panels 
	 */
	public static WorldSetting createShellingSettingFromForm(Activity activity)
	{
		WorldSetting currentSetting = null;
		
		currentSetting = new ShellingSetting();

		// number of populations
		SeekBar choice = (SeekBar) activity.findViewById(R.id.shelling_nb_population);
		((ShellingSetting)currentSetting).setNbCommunities(choice.getProgress()+1);
		
		return currentSetting;
	}

	/**
	 * Fill SHELLING settings panel's item with input settings properties 
	 * @param tSetting
	 */
	public static void fillShellingPanel(WorldSetting tSetting, Activity activity)
	{
		ShellingSetting setting =   (ShellingSetting) tSetting;
		
		// set seekbar's position
		SeekBar choice = (SeekBar) activity.findViewById(R.id.shelling_nb_population);
		choice.setProgress(setting.getNbCommunities()-1);

		// set seekbar's label's text
		TextView nbText = (TextView) activity.findViewById(R.id.shelling_nb_population_text);
		nbText.setText("Number of populations : " + setting.getNbCommunities());
	}


}