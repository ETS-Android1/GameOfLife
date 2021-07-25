package com.zmachsoft.gameoflife.world.setting;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zmachsoft.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;


public class BoidsSetting extends WorldSetting {

    private int nbBoids = 50;

    public BoidsSetting() {
        super(WorldType.BOIDS, 100, 6);
    }

    public int getNbBoids() {
        return nbBoids;
    }

    public void setNbBoids(int nbBoids) {
        this.nbBoids = nbBoids;
    }

    /**
     * @return true if there is any difference between current setting end given one
     */
    @Override
    public boolean hasChanged(WorldSetting setting) {
        if (!(setting instanceof BoidsSetting))
            return true;
        BoidsSetting sSetting = (BoidsSetting) setting;
        return super.hasChanged(setting)
                || sSetting.nbBoids != this.nbBoids;
    }

    public static void prepareBoidsPanel(Activity activity) {
        final TextView nbText = activity.findViewById(R.id.boids_number_text);
        SeekBar choice = activity.findViewById(R.id.boids_number);
        choice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 1;
                nbText.setText("Number of boids: " + value);
            }
        });
    }

    public static WorldSetting createBoidsSettingFromForm(Activity activity) {
        BoidsSetting currentSetting = new BoidsSetting();

        // length of refractory period
        SeekBar choice = activity.findViewById(R.id.boids_number);
        currentSetting.setNbBoids(choice.getProgress() + 1);

        return currentSetting;
    }

    public static void fillBoidsPanel(WorldSetting tSetting, Activity activity) {
        BoidsSetting setting = (BoidsSetting) tSetting;

        // set seekbar's position
        SeekBar choice = activity.findViewById(R.id.boids_number);
        choice.setProgress(setting.getNbBoids() - 1);

        // set seekbar's label's text
        TextView nbText = activity.findViewById(R.id.boids_number_text);
        nbText.setText("Number of boids: " + setting.getNbBoids());
    }

    @Override
    public String getRulesHelp() {
        return "Rules explanation";
    }

}