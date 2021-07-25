package com.zmachsoft.gameoflife.world.boids;

public class Boid {
    public Vector position;
    public Vector velocity;


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
        Vector pcJ = new Vector(0, 0);
        int length = neighbours.length;
        for (Boid neighbour : neighbours) {
            pcJ = pcJ.plus(neighbour.position);
        }
        pcJ = pcJ.div(length);
        return pcJ.minus(position).div(cohesionCoefficient);
    }

    private Vector alignment(Boid[] neighbours, double alignmentCoefficient) {
        Vector pvJ = new Vector(0, 0);
        int length = neighbours.length;
        for (Boid neighbour : neighbours) {
            pvJ = pvJ.plus(neighbour.velocity);
        }
        pvJ = pvJ.div(length);
        return pvJ.minus(velocity).div(alignmentCoefficient);
    }

    private Vector separation(Boid[] neighbours, double separationCoefficient) {
        Vector c = new Vector(0, 0);
        for (Boid neighbour : neighbours) {
            if ((neighbour.position.minus(position).magnitude()) < separationCoefficient) {
                c = c.minus(neighbour.position.minus(position));
            }
        }
        return c;
    }

    public void updatePosition() {
        position = position.plus(velocity);
    }
}
