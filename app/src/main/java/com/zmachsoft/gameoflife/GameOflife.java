package com.zmachsoft.gameoflife;

import android.graphics.Canvas;

import com.zmachsoft.gameoflife.world.GameWorld;
import com.zmachsoft.gameoflife.world.NoChangeException;
import com.zmachsoft.gameoflife.world.WorldBoids;
import com.zmachsoft.gameoflife.world.WorldConway;
import com.zmachsoft.gameoflife.world.WorldEpidemic;
import com.zmachsoft.gameoflife.world.WorldExcitableMedia;
import com.zmachsoft.gameoflife.world.WorldShelling;
import com.zmachsoft.gameoflife.world.WorldWar;
import com.zmachsoft.gameoflife.world.setting.ConwaySetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

public class GameOflife {
    /**
     * Singleton
     */
    private static GameOflife _instance;
    /**
     * Game's world
     */
    private GameWorld world = null;

    /**
     * @return pattern singleton
     */
    public static GameOflife getInstance() {
        if (_instance == null)
            _instance = new GameOflife();

        return _instance;
    }

    /**
     * Init world content
     */
    public void initWorld(WorldSetting setting) {
        Integer boardWidth = null;
        Integer boardHeight = null;
        // register world's board's size if already defined
        if (world != null) {
            boardWidth = world.getBoardWidth();
            boardHeight = world.getBoardheight();
        }

        // no settings, then pick up a default one
        if (setting == null) {
            if (world == null || world.getSetting() == null)
                setting = new ConwaySetting();
            else
                setting = world.getSetting();
        }

        // create the world
        switch (setting.getWorldType()) {
            case CONWAY:
                world = new WorldConway(setting);
                break;

            case SHELLING:
                world = new WorldShelling(setting);
                break;

            case EPIDEMIC:
                world = new WorldEpidemic(setting);
                break;

            case WAR:
                world = new WorldWar(setting);
                break;

            case EXCITABLE_MEDIA:
                world = new WorldExcitableMedia(setting);
                break;

            case BOIDS:
                world = new WorldBoids(setting);
                break;

            default:
                break;
        }

        // update world's board's size
        if (boardWidth != null) {
            world.setBoardWidth(boardWidth);
            world.setBoardheight(boardHeight);
        }

        // world initial state
        world.initContent();
    }

    public void nextStep() throws NoChangeException {
        if (world != null)
            world.nextStep();
    }

    public void render(Canvas canvas) {
        if (world != null) {
            world.render(canvas);
        }
    }

    public GameWorld getWorld() {
        return world;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

}
