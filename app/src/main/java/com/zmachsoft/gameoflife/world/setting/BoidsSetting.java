package com.zmachsoft.gameoflife.world.setting;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zmachsoft.gameoflife.R;
import com.zmachsoft.gameoflife.world.GameWorld.WorldType;


public class BoidsSetting extends WorldSetting {

    private int nbBoids = 50;
    private double cohesionCoefficient = 10.0;
    private int alignmentCoefficient = 8;
    private double separationCoefficient = 10.0;
    private int distance = 100;
    private int velocityMax = 25;

    public BoidsSetting() {
        super(WorldType.BOIDS, 100, 6);
    }

    public int getNbBoids() {
        return nbBoids;
    }

    public void setNbBoids(int nbBoids) {
        this.nbBoids = nbBoids;
    }

    public double getCohesionCoefficient() {
        return cohesionCoefficient;
    }

    public void setCohesionCoefficient(double cohesionCoefficient) {
        this.cohesionCoefficient = cohesionCoefficient;
    }

    public int getAlignmentCoefficient() {
        return alignmentCoefficient;
    }

    public void setAlignmentCoefficient(int alignmentCoefficient) {
        this.alignmentCoefficient = alignmentCoefficient;
    }

    public double getSeparationCoefficient() {
        return separationCoefficient;
    }

    public void setSeparationCoefficient(double separationCoefficient) {
        this.separationCoefficient = separationCoefficient;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getVelocityMax() {
        return velocityMax;
    }

    public void setVelocityMax(int velocityMax) {
        this.velocityMax = velocityMax;
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
                || sSetting.nbBoids != this.nbBoids
                || sSetting.cohesionCoefficient != this.cohesionCoefficient
                || sSetting.alignmentCoefficient != this.alignmentCoefficient
                || sSetting.separationCoefficient != this.separationCoefficient
                || sSetting.distance != this.distance
                || sSetting.velocityMax != this.velocityMax
                ;
    }

    public static void prepareBoidsPanel(Activity activity) {
        activateTriggersOnNumberChange(activity, R.id.boids_number, R.id.boids_number_text, "Number of boids: ");
        activateTriggersOnNumberChange(activity, R.id.boids_cohesion, R.id.boids_cohesion_text, "Cohesion coefficient: ");
        activateTriggersOnNumberChange(activity, R.id.boids_alignment, R.id.boids_alignment_text, "Alignment coefficient: ");
        activateTriggersOnNumberChange(activity, R.id.boids_separation, R.id.boids_separation_text, "Separation coefficient: ");
        activateTriggersOnNumberChange(activity, R.id.boids_distance, R.id.boids_distance_text, "Neighbours distance: ");
        activateTriggersOnNumberChange(activity, R.id.boids_max_velocity, R.id.boids_max_velocity_text, "Max velocity: ");
    }

    private static void activateTriggersOnNumberChange(Activity activity, int itemId, int labelId, String textPrefix) {
        SeekBar seekBar = activity.findViewById(itemId);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 1;
                final TextView nbText = activity.findViewById(labelId);
                nbText.setText(textPrefix + value);
            }
        });
    }

    public static WorldSetting createBoidsSettingFromForm(Activity activity) {
        BoidsSetting currentSetting = new BoidsSetting();

        SeekBar choice = activity.findViewById(R.id.boids_number);
        currentSetting.setNbBoids(choice.getProgress() + 1);
        choice = activity.findViewById(R.id.boids_cohesion);
        currentSetting.setCohesionCoefficient(choice.getProgress() + 1);
        choice = activity.findViewById(R.id.boids_alignment);
        currentSetting.setAlignmentCoefficient(choice.getProgress() + 1);
        choice = activity.findViewById(R.id.boids_separation);
        currentSetting.setSeparationCoefficient(choice.getProgress() + 1);
        choice = activity.findViewById(R.id.boids_distance);
        currentSetting.setDistance(choice.getProgress() + 1);
        choice = activity.findViewById(R.id.boids_max_velocity);
        currentSetting.setVelocityMax(choice.getProgress() + 1);

        return currentSetting;
    }

    public static void fillBoidsPanel(WorldSetting tSetting, Activity activity) {
        BoidsSetting setting = (BoidsSetting) tSetting;

        fillSeekBar(activity, setting, R.id.boids_number, R.id.boids_number_text, setting.getNbBoids(), "Number of boids: ");
        fillSeekBar(activity, setting, R.id.boids_cohesion, R.id.boids_cohesion_text, Double.valueOf(setting.getCohesionCoefficient()).intValue(), "Cohesion coefficient: ");
        fillSeekBar(activity, setting, R.id.boids_alignment, R.id.boids_alignment_text, setting.getAlignmentCoefficient(), "Alignment coefficient: ");
        fillSeekBar(activity, setting, R.id.boids_separation, R.id.boids_separation_text, Double.valueOf(setting.getSeparationCoefficient()).intValue(), "Separation coefficient: ");
        fillSeekBar(activity, setting, R.id.boids_distance, R.id.boids_distance_text, setting.getDistance(), "Neighbours distance: ");
        fillSeekBar(activity, setting, R.id.boids_max_velocity, R.id.boids_max_velocity_text, setting.getVelocityMax(), "Max velocity: ");
    }

    private static void fillSeekBar(Activity activity, BoidsSetting setting, int itemId, int labelId, int value, String textPrefix) {
        // set seekbar's position
        SeekBar choice = activity.findViewById(itemId);
        choice.setProgress(value - 1);

        // set seekbar's label's text
        TextView nbText = activity.findViewById(labelId);
        nbText.setText(textPrefix + value);
    }

    @Override
    public String getRulesHelp() {
        return "Rules explanation";
    }

}