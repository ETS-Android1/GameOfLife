package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.zmachsoft.gameoflife.world.boids.Boid;
import com.zmachsoft.gameoflife.world.boids.Vector;
import com.zmachsoft.gameoflife.world.setting.BoidsSetting;
import com.zmachsoft.gameoflife.world.setting.ExcitableMediaSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

import java.util.Arrays;
import java.util.Random;

/**
 * Details in
 *
 * @author Master
 */
public class WorldBoids extends GameWorld {

    private Boid[] boids;
    private Random random = new Random(System.currentTimeMillis());

    private double cohesionCoefficient;
    private int alignmentCoefficient;
    private double separationCoefficient;
    private int distance;
    private static final Paint BOID_PAINT = new Paint();

    public WorldBoids() {
        super(new ExcitableMediaSetting());
    }

    public WorldBoids(WorldSetting setting) {
        super(setting);
    }

    @Override
    public void initContent() {
        Log.i("GOL", "World init data");
        Log.i("GOL", "Nb tiles : " + setting.getNbTiles());
        Log.i("GOL", "Nb of boids : " + ((BoidsSetting) setting).getNbBoids());
        Log.i("GOL", "size of board for boids : " + getBoardWidth() + " / " + getBoardheight());

        Random random = new Random(System.currentTimeMillis());
        int nbBoids = ((BoidsSetting) setting).getNbBoids();
        boids = new Boid[nbBoids];
        for (int i = 0; i < nbBoids; i++) {
            double x = random.nextInt(getBoardWidth());
            double y = random.nextInt(getBoardheight());
            boids[i] = new Boid(new Vector(x, y), new Vector(0, 0)); // no initial velocity
        }

        this.cohesionCoefficient = 100.0;
        this.alignmentCoefficient = 8;
        this.separationCoefficient = 10.0;
        this.distance = 50;
        BOID_PAINT.setColor(Color.BLACK);
    }

    @Override
    public void nextStep() throws NoChangeException {
        Log.i("GD", "World next step");

        Arrays.stream(boids)
                .forEach(boid -> {
                    Boid[] neighbours = findNeighbours(boid, distance);
                    boid.updateVelocity(neighbours, cohesionCoefficient, alignmentCoefficient, separationCoefficient);
                    boid.updatePosition();
                });
    }

    private Boid[] findNeighbours(Boid boid, int distance) {
        return boids;
    }

    @Override
    /**
     * Render the world to an off-screen bitmap
     */
    public void render(Canvas canvas) {
        Log.i("GD", "Surface onDraw");
        // compute shifts to center the drawing
        int leftShift = (getBoardWidth() - setting.getNbTiles() * setting.getTileSize()) / 2;
        int topShift = (getBoardheight() - setting.getNbTiles() * setting.getTileSize()) / 2;

        Arrays.stream(boids)
                .forEach(boid -> render(boid, canvas, leftShift, topShift));
    }

    private void render(Boid boid, Canvas canvas, int leftShift, int topShift) {
        // project boid's coordinates into display referential (limited to width / height)
        int x = boid.getX() % getBoardWidth();
        int y = boid.getY() % getBoardheight();
        System.out.println("Boid at " + boid.getX() + "," + boid.getY() + " rendered at " + x + "," + y);

//        canvas.drawPoint(x, y, BOID_PAINT);
        canvas.drawCircle(x, y, 2, BOID_PAINT);
    }

    @Override
    public String toString() {
        return "Boids media - id=" + uniqueId;
    }
}

