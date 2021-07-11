package com.zmachsoft.gameoflife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zmachsoft.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;
import com.zmachsoft.gameoflife.world.setting.ConwaySetting;
import com.zmachsoft.gameoflife.world.setting.EpidemicSetting;
import com.zmachsoft.gameoflife.world.setting.ShellingSetting;
import com.zmachsoft.gameoflife.world.setting.WarSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

public class SettingsActivity extends Activity implements OnClickListener
{
	/**
	 * Last activated setting panel 
	 */
	private View lastSettingPanel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);

		// Bind event on UI items
		CheckedTextView conwayChoice = (CheckedTextView) findViewById(R.id.checkedConway);
		conwayChoice.setOnClickListener(this);
		CheckedTextView shellingChoice = (CheckedTextView) findViewById(R.id.checkedShelling);
		shellingChoice.setOnClickListener(this);
		CheckedTextView epidemicChoice = (CheckedTextView) findViewById(R.id.checkedEpidemic);
		epidemicChoice.setOnClickListener(this);
		CheckedTextView warChoice = (CheckedTextView) findViewById(R.id.checkedWar);
		warChoice.setOnClickListener(this);

		// help buttons
		ImageButton conwayHelp = (ImageButton) findViewById(R.id.setting_help_conway);
		conwayHelp.setOnClickListener(this);
		ImageButton shellingHelp = (ImageButton) findViewById(R.id.setting_help_shelling);
		shellingHelp.setOnClickListener(this);
		ImageButton epidemicHelp = (ImageButton) findViewById(R.id.setting_help_epidemic);
		epidemicHelp.setOnClickListener(this);
		ImageButton warHelp = (ImageButton) findViewById(R.id.setting_help_war);
		warHelp.setOnClickListener(this);
		
		Button applyButton = (Button) findViewById(R.id.settingsApply);
		applyButton.setOnClickListener(this);
		Button cancelButton = (Button) findViewById(R.id.settingsCancel);
		cancelButton.setOnClickListener(this);
	}
	
	@Override
	protected void onStart()
	{
		// update panel display according to current game's settings
		updateFormFromSetting(GameOflife.getInstance().getWorld().getSetting());
		
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v)
	{
		WorldSetting defaultSetting = null;
		if (v.getId() == R.id.checkedConway)
		{
			Log.i("GOL", "Choose Conway !");
			enableSTateOfSettingsChoice(new boolean[] { true, false, false, false });
			
			// load a default setting
			defaultSetting = WorldSetting.getDefaultSetting(WorldType.CONWAY);
			activateSetting(defaultSetting.getWorldType());
			ConwaySetting.fillConwayPanel(defaultSetting, this);
		}
		else if (v.getId() == R.id.checkedShelling)
		{
			Log.i("GOL", "Choose Shelling !");
			enableSTateOfSettingsChoice(new boolean[] { false, true, false, false });
			
			// load a default setting
			defaultSetting = WorldSetting.getDefaultSetting(WorldType.SHELLING);
			activateSetting(defaultSetting.getWorldType());
			ShellingSetting.fillShellingPanel(defaultSetting, this);
		}
		else if (v.getId() == R.id.checkedEpidemic)
		{
			Log.i("GOL", "Choose Epidemic !");
			enableSTateOfSettingsChoice(new boolean[] { false, false, true, false });
			
			// load a default setting
			defaultSetting = WorldSetting.getDefaultSetting(WorldType.EPIDEMIC);
			activateSetting(defaultSetting.getWorldType());
			EpidemicSetting.fillEpidemicPanel(defaultSetting, this);
		}
		else if (v.getId() == R.id.checkedWar)
		{
			Log.i("GOL", "Choose war !");
			enableSTateOfSettingsChoice(new boolean[] { false, false, false, true });
			
			// load a default setting
			defaultSetting = WorldSetting.getDefaultSetting(WorldType.WAR);
			activateSetting(defaultSetting.getWorldType());
			WarSetting.fillWarPanel(defaultSetting, this);
		}
		else if (v.getId() == R.id.settingsApply)
		{
			// create a world setting according to user options
			WorldSetting setting = createSettingFromForm();
			Intent resultIntent = new Intent((String) null);
			resultIntent.putExtra("setting", setting);

			setResult(MainActivity.ACTIVITY_SETTING_ID, resultIntent);
			finish(); // finishing activity
		}
		else if (v.getId() == R.id.settingsCancel)
		{
			// no change
			setResult(MainActivity.ACTIVITY_SETTING_ID, null);
			finish(); // finishing activity
		}
		else if (v.getId() == R.id.setting_help_conway)
		{
			// display the rules
			String text = getString(R.string.settings_helptext_conway);
			GameUIUtils.displayTextDialog(this, "Conway rules", text, true);
		}
		else if (v.getId() == R.id.setting_help_shelling)
		{
			// display the rules
			GameUIUtils.displayTextDialog(this, "Shelling rules", GameOflife.getInstance().getWorld().getSetting().getRulesHelp(), true);
		}
		else if (v.getId() == R.id.setting_help_epidemic)
		{
			// display the rules
			GameUIUtils.displayTextDialog(this, "Epidemic rules", GameOflife.getInstance().getWorld().getSetting().getRulesHelp(), true);
		}
		else if (v.getId() == R.id.setting_help_war)
		{
			// display the rules
			GameUIUtils.displayTextDialog(this, "War rules", GameOflife.getInstance().getWorld().getSetting().getRulesHelp(), true);
		}
	}

	/**
	 * Manage main checkboxes states
	 * 
	 * @param states
	 */
	private void enableSTateOfSettingsChoice(boolean[] states)
	{
		CheckedTextView conwayChoice = (CheckedTextView) findViewById(R.id.checkedConway);
		conwayChoice.setChecked(states[0]);
		CheckedTextView shellingChoice = (CheckedTextView) findViewById(R.id.checkedShelling);
		shellingChoice.setChecked(states[1]);
		CheckedTextView epidemicChoice = (CheckedTextView) findViewById(R.id.checkedEpidemic);
		epidemicChoice.setChecked(states[2]);
		CheckedTextView warChoice = (CheckedTextView) findViewById(R.id.checkedWar);
		warChoice.setChecked(states[3]);
	}

	/**
	 * Create a world setting according to current panel selected options
	 */
	private WorldSetting createSettingFromForm()
	{
		WorldSetting currentSetting = null;

		CheckedTextView conwayChoice = (CheckedTextView) findViewById(R.id.checkedConway);
		CheckedTextView shellingChoice = (CheckedTextView) findViewById(R.id.checkedShelling);
		CheckedTextView epidemicChoice = (CheckedTextView) findViewById(R.id.checkedEpidemic);
		CheckedTextView warChoice = (CheckedTextView) findViewById(R.id.checkedWar);

		if (conwayChoice.isChecked())
			currentSetting = ConwaySetting.createConwaySettingFromForm(this);
		else if (shellingChoice.isChecked())
			currentSetting = ShellingSetting.createShellingSettingFromForm(this);
		if (epidemicChoice.isChecked())
			currentSetting = EpidemicSetting.createEpidemicSettingFromForm(this);
		if (warChoice.isChecked())
			currentSetting = WarSetting.createWarSettingFromForm(this);

		return currentSetting;
	}

	/**
	 * Update setting panel display according to given setting 
	 * @param setting
	 */
	private void updateFormFromSetting(WorldSetting setting)
	{
		CheckedTextView conwayChoice = (CheckedTextView) findViewById(R.id.checkedConway);
		CheckedTextView shellingChoice = (CheckedTextView) findViewById(R.id.checkedShelling);
		CheckedTextView epidemicChoice = (CheckedTextView) findViewById(R.id.checkedEpidemic);
		CheckedTextView warChoice = (CheckedTextView) findViewById(R.id.checkedWar);
		
		// select the current type
		switch (GameOflife.getInstance().getWorld().getSetting().getWorldType())
		{
		case CONWAY:
			conwayChoice.setChecked(true);
			break;

		case SHELLING:
			shellingChoice.setChecked(true);
			break;

		case EPIDEMIC:
			epidemicChoice.setChecked(true);
			break;

		case WAR:
			warChoice.setChecked(true);
			break;

		default:
			break;
		}
		
		// activate the right panel
		activateSetting(setting.getWorldType());
		
		// fill form with setting's properties
		switch (setting.getWorldType())
		{
		case CONWAY:
			ConwaySetting.fillConwayPanel(setting, this);
			break;
		case SHELLING:
			ShellingSetting.fillShellingPanel(setting, this);
			break;
		case EPIDEMIC:
			EpidemicSetting.fillEpidemicPanel(setting, this);
			break;
		case WAR:
			WarSetting.fillWarPanel(setting, this);
			break;
		}
	}
	
	/**
	 * Activate panel display suitable for given world type
	 * @param type
	 */
	private void activateSetting(WorldType type)
	{
		LinearLayout rulesLayout = (LinearLayout) findViewById(R.id.main_layout);
		
		// remove last setting panel if any
		if (lastSettingPanel!=null)
		{
			rulesLayout.removeView(lastSettingPanel);
		}
		
		// which new panel to display ?
		int newPanelId = 0;
		switch (type)
		{
		case CONWAY:
			newPanelId = R.layout.setting_panel_conway;
			break;
		case SHELLING:
			newPanelId = R.layout.setting_panel_shelling;
			break;
		case EPIDEMIC:
			newPanelId = R.layout.setting_panel_epidemic;
			break;
		case WAR:
			newPanelId = R.layout.setting_panel_war;
			break;
		}
		
		// add new panel
		lastSettingPanel = (LinearLayout) getLayoutInflater().inflate(newPanelId, null);
		lastSettingPanel.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		lastSettingPanel.setTop(30);
		rulesLayout.addView(lastSettingPanel);

		// bind events on panel's item
		switch (type)
		{
		case CONWAY:
			ConwaySetting.prepareConwayPanel(this);
			break;
		case SHELLING:
			ShellingSetting.prepareShellingPanel(this);
			break;
		case EPIDEMIC:
			EpidemicSetting.prepareEpidemicPanel(this);
			break;
		case WAR:
			WarSetting.prepareWarPanel(this);
			break;
		}
	}
	
}
