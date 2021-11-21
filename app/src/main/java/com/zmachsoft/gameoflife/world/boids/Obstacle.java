package com.zmachsoft.gameoflife.world.boids;

public class Obstacle {
    public Vector position;

    public Obstacle(Vector position) {
        this.position = position;
    }

    public int getX() {
        return Double.valueOf(position.data[0]).intValue();
    }

    public int getY() {
        return Double.valueOf(position.data[1]).intValue();
    }

}
