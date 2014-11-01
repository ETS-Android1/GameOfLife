package com.zmachsoft.gameoflife;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.example.gameoflife.R;
import com.zmachsoft.gameoflife.world.NoChangeException;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

public class MainActivity extends Activity implements OnClickListener, OnSeekBarChangeListener
{
	public static int ACTIVITY_SETTING_ID = 2; 
	private boolean pauseButtonCanPause = true;
	private int renderSpeed = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("GOL", "Activity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create the game itself (singleton pattern to be accessible from any activity)
		GameOflife.getInstance();

		// Trace current locale		
//		Locale current = getResources().getConfiguration().locale;
		
		// bind listener on UI buttons
		Button startButton = (Button) findViewById(R.id.buttonStart);
		startButton.setOnClickListener(this);
		Button pauseButton = (Button) findViewById(R.id.buttonPause);
		pauseButton.setOnClickListener(this);
		pauseButton.setEnabled(false);
		Button resetButton = (Button) findViewById(R.id.buttonReset);
		resetButton.setOnClickListener(this);
		Button stepButton = (Button) findViewById(R.id.ButtonStep);
		stepButton.setOnClickListener(this);
		Button settingsButton = (Button) findViewById(R.id.buttonSettings);
		settingsButton.setOnClickListener(this);
		
		SeekBar settingsSpeed = (SeekBar) findViewById(R.id.settingsSpeed); 
		settingsSpeed.setOnSeekBarChangeListener(this);
	}

	@Override
	protected void onStart()
	{
		Log.i("GOL", "Activity onStart");
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		Log.i("GOL", "Activity onResume");
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	protected void onStop()
	{
		Log.i("GOL", "Activity onStop");
		super.onStop();
	}

	@Override
	protected void onPause()
	{
		Log.i("GOL", "Activity onPause");
		GameBoard gameBoard = (GameBoard) findViewById(R.id.gameWorld);
		if (gameBoard != null)
		{
			gameBoard.stopGame();
			gameBoard = null;
		}
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		Log.i("GOL", "Activity onDestroy");
		GameBoard gameBoard = (GameBoard) findViewById(R.id.gameWorld);
		if (gameBoard != null)
		{
			gameBoard.stopGame();
			gameBoard = null;
		}
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    }
	}
	
	@Override
	public void onClick(View v)
	{
		Log.i("GOL", "Activity onClick");
		GameBoard gameBoard = (GameBoard) findViewById(R.id.gameWorld);
		if (gameBoard == null)
			return;

		// start the service
		if (v.getId() == R.id.buttonStart)
		{
			// update the game speed
			gameBoard.setSpeed(renderSpeed);

			// start the game
			gameBoard.startGame();

			// disable start button / enable pause button
			Button startButton = (Button) findViewById(R.id.buttonStart);
			startButton.setEnabled(false);
			Button pauseButton = (Button) findViewById(R.id.buttonPause);
			pauseButton.setEnabled(true);
		}
		else if (v.getId() == R.id.buttonPause)
		{
			// could this button perform a pause ?
			if (pauseButtonCanPause)
			{
				gameBoard.pauseGame();

				// change the button text
				Button pauseButton = (Button) findViewById(R.id.buttonPause);
				pauseButton.setText("Resume");

				pauseButtonCanPause = false;
			}
			else
			{
				// update the game speed
				gameBoard.setSpeed(renderSpeed);
				
				// start the game
				gameBoard.startGame();

				// change the button text
				Button pauseButton = (Button) findViewById(R.id.buttonPause);
				pauseButton.setText("Pause");

				pauseButtonCanPause = true;
			}
		}
		else if (v.getId() == R.id.buttonReset)
		{
			// Pause
			gameBoard.pauseGame();

			// re-init content
			gameBoard.initWorld();
			gameBoard.renderOnce();

			// enable start button
			Button startButton = (Button) findViewById(R.id.buttonStart);
			startButton.setEnabled(true);

			// reset pause button
			Button pauseButton = (Button) findViewById(R.id.buttonPause);
			pauseButton.setEnabled(false);
			pauseButton.setText("Pause");
			pauseButtonCanPause = true;
		}
		else if (v.getId() == R.id.ButtonStep)
		{
			// next step & render
			try
			{
				GameOflife.getInstance().nextStep();
				gameBoard.renderOnce();
			}
			catch (NoChangeException e)
			{
				// Pause
				Log.i("GOL", "No more change detected - pause game");
				gameBoard.pauseGame();
			}
		}
		else if (v.getId() == R.id.buttonSettings)
		{
			// Pause
			gameBoard.pauseGame();

			// change the button text
			Button pauseButton = (Button) findViewById(R.id.buttonPause);
			pauseButton.setText("Resume");

			pauseButtonCanPause = false;

			// start settings activity with request code 2
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, ACTIVITY_SETTING_ID);// Settings Activity is started with requestCode 2
		}
	}
	
	/*
	 * Called when depending activity is closed. (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		// Is t called after settings activity has been closed ?
		if (requestCode == ACTIVITY_SETTING_ID)
		{
			// retrieve the settings build from user's options
			if (data!=null)
			{
				WorldSetting setting = (WorldSetting) data.getSerializableExtra("setting");
				
				// Get some setting and has it been changed ? Re-init the world and reset the view.
				if (setting!=null && setting.hasChanged(GameOflife.getInstance().getWorld().getSetting()))
				{
					// init the world
					GameOflife.getInstance().initWorld(setting);
					
					// refresh the game board
					GameBoard gameBoard = (GameBoard) findViewById(R.id.gameWorld);
					gameBoard.renderOnce();

					// enable start button
					Button startButton = (Button) findViewById(R.id.buttonStart);
					startButton.setEnabled(true);

					// reset pause button
					Button pauseButton = (Button) findViewById(R.id.buttonPause);
					pauseButton.setEnabled(false);
					pauseButton.setText("Pause");
					pauseButtonCanPause = true;
				}
				// No change, so only do ... nothing !
				else
				{
					Log.i("GOL", "Settings closed without any change");
				}
			}
			// No change, so only do ... nothing !
			else
			{
				Log.i("GOL", "Settings closed without any change");
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		renderSpeed = seekBar.getProgress();
		Log.i("GOL", "Speed : " + renderSpeed);
		GameBoard gameBoard = (GameBoard) findViewById(R.id.gameWorld);
		if (gameBoard!=null)
			gameBoard.setSpeed(renderSpeed);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}
}
