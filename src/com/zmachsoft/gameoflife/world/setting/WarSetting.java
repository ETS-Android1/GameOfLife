package com.zmachsoft.gameoflife.world.setting;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;


public class WarSetting extends WorldSetting
{
	/**
	 * Number of communities
	 */
	private int nbArmy = 3;
	/**
	 * Occupation rule in case of equality around an empty cell
	 */
	private int emptyCellOccupationMode = 50;	// from 0= never to 100=always	
	/**
	 * Dying rule in case of equality around an occupied cell
	 */
	private int occupiedCellDyingMode = 50;	// from 0= never to 100=always

	public WarSetting()
	{
		super(WorldType.WAR);
	}

	/**
	 * @param setting
	 * @return true if there is any difference between curent setting end given one
	 */
	@Override
	public boolean hasChanged(WorldSetting setting)
	{
		if (!(setting instanceof WarSetting))
			return true;
		WarSetting sSetting = (WarSetting) setting;
		return super.hasChanged(setting) || 
				(sSetting.getNbArmy()!=nbArmy) || 
				(sSetting.getEmptyCellOccupationMode()!=emptyCellOccupationMode) ||
				(sSetting.getOccupiedCellDyingMode()!=occupiedCellDyingMode)
				;
	}

	public int getNbArmy()
	{
		return nbArmy;
	}

	public void setNbArmy(int nbArmy)
	{
		this.nbArmy = nbArmy;
	}

	public int getEmptyCellOccupationMode()
	{
		return emptyCellOccupationMode;
	}

	public void setEmptyCellOccupationMode(int emptyCellOccupationMode)
	{
		this.emptyCellOccupationMode = emptyCellOccupationMode;
	}

	public int getOccupiedCellDyingMode()
	{
		return occupiedCellDyingMode;
	}

	public void setOccupiedCellDyingMode(int occupiedCellDyingMode)
	{
		this.occupiedCellDyingMode = occupiedCellDyingMode;
	}

	/**
	 * Prepare WAR settings panel's item events bindings 
	 */
	public static void prepareWarPanel(Activity activity)
	{
		final TextView nbText = (TextView) activity.findViewById(R.id.war_nb_armies_text);
		SeekBar choice = (SeekBar) activity.findViewById(R.id.war_nb_armies);
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
				nbText.setText("Number of armies : " + value);
			}
		});
	}

	/**
	 * @return a WAR setting created from user's options in displayed panels 
	 */
	public static WorldSetting createWarSettingFromForm(Activity activity)
	{
		WorldSetting currentSetting = null;
		
		currentSetting = new WarSetting();

		// number of armies
		SeekBar choice = (SeekBar) activity.findViewById(R.id.war_nb_armies);
		((WarSetting)currentSetting).setNbArmy(choice.getProgress()+1);
	
		// Occupation rule around empty cells
		SeekBar occupationProbability = (SeekBar) activity.findViewById(R.id.war_setting_occupation_progress);
		((WarSetting)currentSetting).setEmptyCellOccupationMode(occupationProbability.getProgress()+1);
		
		// Dying rule around occupied cells
		SeekBar dyingProbability = (SeekBar) activity.findViewById(R.id.war_setting_dying_progress);
		((WarSetting)currentSetting).setOccupiedCellDyingMode(dyingProbability.getProgress()+1);
		
		return currentSetting;
	}

	/**
	 * Fill WAR settings panel's item with input settings properties 
	 * @param tSetting
	 */
	public static void fillWarPanel(WorldSetting tSetting, Activity activity)
	{
		WarSetting setting =   (WarSetting) tSetting;
		
		// set seekbar's position
		SeekBar choice = (SeekBar) activity.findViewById(R.id.war_nb_armies);
		choice.setProgress(setting.getNbArmy()-1);

		// set seekbar's label's text
		TextView nbText = (TextView) activity.findViewById(R.id.war_nb_armies_text);
		nbText.setText("Number of armies : " + setting.getNbArmy());
		
		// set Occupation rule around empty cells seekbar's position
		SeekBar occupationProbability = (SeekBar) activity.findViewById(R.id.war_setting_occupation_progress);
		occupationProbability.setProgress(setting.getEmptyCellOccupationMode()-1);
		
		// set Occupation rule around empty cells seekbar's position
		SeekBar dyingProbability = (SeekBar) activity.findViewById(R.id.war_setting_dying_progress);
		dyingProbability.setProgress(setting.getOccupiedCellDyingMode()-1);
	}

	@Override
	public String getRulesHelp()
	{
		return "Rules explanation";
	}

}