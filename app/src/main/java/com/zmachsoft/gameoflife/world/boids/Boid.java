package com.zmachsoft.gameoflife.world.boids;

public class Boid {
    private Vector position;
    private Vector velocity;


    public Boid(Vector position, Vector velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public int getX() {
        return Double.valueOf(position.data[0]).intValue();
    }

    public int getY() {
        return Double.valueOf(position.data[1]).intValue();
    }

    public void updateVelocity(Boid[] neighbours, double cohesionCoefficient, int alignmentCoefficient, double separationCoefficient) {
        velocity = velocity.plus(cohesion(neighbours, cohesionCoefficient))
                .plus(alignment(neighbours, alignmentCoefficient))
                .plus(separation(neighbours, separationCoefficient));
    }

    private Vector cohesion(Boid[] neighbours, double cohesionCoefficient) {
        return new Vector(0, 0);
    }

    private Vector alignment(Boid[] neighbours, double cohesionCoefficient) {
        return new Vector(0, 0);
    }

    private Vector separation(Boid[] neighbours, double cohesionCoefficient) {
        return new Vector(0, 0);
    }

    public void updatePosition() {
        position = position.plus(velocity);
    }
}
