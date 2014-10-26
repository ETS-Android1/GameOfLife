package com.zmachsoft.gameoflife.world.setting;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;


public class EpidemicSetting extends WorldSetting
{
	/**
	 * Number of initial sick points
	 */
	private int nbSickPoints = 5;
	/**
	 * Initial density of board
	 */
	private double density = 0.5;

	public EpidemicSetting()
	{
		super(WorldType.EPIDEMIC);
	}

	/**
	 * @param setting
	 * @return true if there is any difference between curent setting end given one
	 */
	@Override
	public boolean hasChanged(WorldSetting setting)
	{
		if (!(setting instanceof EpidemicSetting))
			return true;
		EpidemicSetting sSetting = (EpidemicSetting) setting;
		return super.hasChanged(setting) || (sSetting.getDensity()!=density) || (sSetting.getNbSickPoints()!=nbSickPoints);
	}

	public double getDensity()
	{
		return density;
	}

	public void setDensity(double density)
	{
		this.density = density;
	}	

	public int getNbSickPoints()
	{
		return nbSickPoints;
	}

	public void setNbSickPoints(int nbSickPoints)
	{
		this.nbSickPoints = nbSickPoints;
	}
	
	/**
	 * Prepare EPIDEMIC settings panel's item events bindings 
	 */
	public static void prepareEpidemicPanel(Activity activity)
	{
		final TextView nbText = (TextView) activity.findViewById(R.id.epidemic_nb_sickpoints_text);
		SeekBar choice = (SeekBar) activity.findViewById(R.id.epidemic_nb_sickpoints);
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
				nbText.setText("Number of sick points : " + value);
			}
		});
	}

	/**
	 * @return a EPIDEMIC setting created from user's options in displayed panels 
	 */
	public static WorldSetting createEpidemicSettingFromForm(Activity activity)
	{
		WorldSetting currentSetting = null;
		
		currentSetting = new EpidemicSetting();

		// number of sick points
		SeekBar choice = (SeekBar) activity.findViewById(R.id.epidemic_nb_sickpoints);
		((EpidemicSetting)currentSetting).setNbSickPoints(choice.getProgress()+1);
		
		return currentSetting;
	}

	/**
	 * Fill EPIDEMIC settings panel's item with input settings properties 
	 * @param tSetting
	 */
	public static void fillEpidemicPanel(WorldSetting tSetting, Activity activity)
	{
		EpidemicSetting setting =   (EpidemicSetting) tSetting;
		
		// set seekbar's position
		SeekBar choice = (SeekBar) activity.findViewById(R.id.epidemic_nb_sickpoints);
		choice.setProgress(setting.getNbSickPoints()-1);

		// set seekbar's label's text
		TextView nbText = (TextView) activity.findViewById(R.id.epidemic_nb_sickpoints_text);
		nbText.setText("Number of sick points : " + setting.getNbSickPoints());
	}

	@Override
	public String getRulesHelp()
	{
		return "Rules explanation";
	}

}
