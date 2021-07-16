package com.zmachsoft.gameoflife.world.setting;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zmachsoft.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;


public class ExcitableMediaSetting extends WorldSetting {
    /**
     * Length of refractory period
     */
    private int lengthRefractoryPeriod = 4;
    /**
     * Probability of excitation in case all neighbours are excited
     */
    private int excitationProbability = 50;    // from 0= never to 100=always

    public ExcitableMediaSetting() {
        super(WorldType.EXCITABLE_MEDIA);
    }

    /**
     * @return true if there is any difference between current setting end given one
     */
    @Override
    public boolean hasChanged(WorldSetting setting) {
        if (!(setting instanceof ExcitableMediaSetting))
            return true;
        ExcitableMediaSetting sSetting = (ExcitableMediaSetting) setting;
        return super.hasChanged(setting) ||
                (sSetting.getLengthRefractoryPeriod() != lengthRefractoryPeriod) ||
                (sSetting.getExcitationProbability() != excitationProbability)
                ;
    }

    public int getLengthRefractoryPeriod() {
        return lengthRefractoryPeriod;
    }

    public void setLengthRefractoryPeriod(int lengthRefractoryPeriod) {
        this.lengthRefractoryPeriod = lengthRefractoryPeriod;
    }

    public int getExcitationProbability() {
        return excitationProbability;
    }

    public void setExcitationProbability(int excitationProbability) {
        this.excitationProbability = excitationProbability;
    }

    public static void prepareExcitableMediaPanel(Activity activity) {
        final TextView nbText = (TextView) activity.findViewById(R.id.excitable_media_length_refractory_period_text);
        SeekBar choice = (SeekBar) activity.findViewById(R.id.excitable_media_length_refractory_period);
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
                nbText.setText("Length of refractory period: " + value);
            }
        });
    }

    public static WorldSetting createExcitableMediaSettingFromForm(Activity activity) {
        WorldSetting currentSetting = null;

        currentSetting = new ExcitableMediaSetting();

        // length of refractory period
        SeekBar choice = (SeekBar) activity.findViewById(R.id.excitable_media_length_refractory_period);
        ((ExcitableMediaSetting) currentSetting).setLengthRefractoryPeriod(choice.getProgress() + 1);

        // Excitation probability
        SeekBar excitationProbability = (SeekBar) activity.findViewById(R.id.excitable_media_setting_excitation_probability_progress);
        ((ExcitableMediaSetting) currentSetting).setExcitationProbability(excitationProbability.getProgress() + 1);

        return currentSetting;
    }

    public static void fillExcitableMediaPanel(WorldSetting tSetting, Activity activity) {
        ExcitableMediaSetting setting = (ExcitableMediaSetting) tSetting;

        // set seekbar's position
        SeekBar choice = (SeekBar) activity.findViewById(R.id.excitable_media_length_refractory_period);
        choice.setProgress(setting.getLengthRefractoryPeriod() - 1);

        // set seekbar's label's text
        TextView nbText = (TextView) activity.findViewById(R.id.excitable_media_length_refractory_period_text);
        nbText.setText("Length of refractory period: " + setting.getLengthRefractoryPeriod());

        // set Excitation probability
        SeekBar occupationProbability = (SeekBar) activity.findViewById(R.id.excitable_media_setting_excitation_probability_progress);
        occupationProbability.setProgress(setting.getExcitationProbability() - 1);
    }

    @Override
    public String getRulesHelp() {
        return "Rules explanation";
    }

}