package ecs;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ParticleSystem {
    private final HashMap<Long, Particle> particles = new HashMap<>();
    private final MyRandom random = new MyRandom();

    private float OFFSET_X;
    private float OFFSET_Y;
    private int GRID_SIZE;
    float CELL_SIZE;

    public ParticleSystem(int gridSize) {

        this.OFFSET_X = 0.1f;
        this.OFFSET_Y = 0.1f;
        this.GRID_SIZE = gridSize;
        this.CELL_SIZE = 0.04f;
    }

    public void update(double gameTime) {
        // Update existing particles
        List<Long> removeMe = new ArrayList<>();
        for (Particle p : particles.values()) {
            if (!p.update(gameTime)) {
                removeMe.add(p.name);
            }
        }

        // Remove dead particles
        for (Long key : removeMe) {
            particles.remove(key);
        }
    }

    public void isYouChange(int x, int y){
        float left_edge_x = -0.5f + OFFSET_X + x * CELL_SIZE;
        float top_edge_y = -0.5f + OFFSET_Y + y * CELL_SIZE;

        int particlesPerEdge = 20;

        float sizeMean = 0.005f;
        float sizeStd = 0.002f;

        float speedMean = 0.0005f;
        float speedStd = 0.005f;

        float lifetimeMean = 0.3f;
        float lifetimeStd = 0.05f;


        Vector2f left_direction = new Vector2f(-1,0);
        Vector2f right_direction = new Vector2f(1,0);
        Vector2f up_direction = new Vector2f(0,-1);
        Vector2f down_direction = new Vector2f(0,1);

        //left edge particles
        for (int i = 0; i < particlesPerEdge; i++){
            var particle = create(left_direction,
                    new Vector2f(left_edge_x, top_edge_y  + (i * (CELL_SIZE / 20))),
                    sizeMean, sizeStd,
                    speedMean, speedStd,
                    lifetimeMean, lifetimeStd);
            particles.put(particle.name, particle);
        }

        //top edge particles
        for (int i = 0; i < particlesPerEdge; i++){
            var particle = create(up_direction,
                    new Vector2f(left_edge_x + (i * (CELL_SIZE / 20)), top_edge_y),
                    sizeMean, sizeStd,
                    speedMean, speedStd,
                    lifetimeMean, lifetimeStd);
            particles.put(particle.name, particle);
        }

        //right edge particle
        for (int i = 0; i < particlesPerEdge; i++){
            var particle = create(right_direction,
                    new Vector2f(left_edge_x + CELL_SIZE, top_edge_y + (i * (CELL_SIZE / 20))),
                    sizeMean, sizeStd,
                    speedMean, speedStd,
                    lifetimeMean, lifetimeStd);
            particles.put(particle.name, particle);
        }

        //bottom particles
        for (int i = 0; i < particlesPerEdge; i++){
            var particle = create(down_direction,
                    new Vector2f(left_edge_x + (i * (CELL_SIZE / 20)), top_edge_y + CELL_SIZE),
                    sizeMean, sizeStd,
                    speedMean, speedStd,
                    lifetimeMean, lifetimeStd);

            particles.put(particle.name, particle);
        }
    }

    public void isWinChange(int x, int y){
        isYouChange(x, y);
    }

    public void playerDeath(int x, int y){


    }

    public void objectDeath(int x, int y) {

    }

    public void objectIsWin(int x, int y) {
        float left_edge_x = -0.5f + OFFSET_X + x * CELL_SIZE;
        float top_edge_y = -0.5f + OFFSET_Y + y * CELL_SIZE;

        int particlesPerEdge = 20;

        float sizeMean = 0.01f;
        float sizeStd = 0.005f;

        float speedMean = 0.05f;
        float speedStd = 0.1f;

        float lifetimeMean = 0.5f;
        float lifetimeStd = 0.5f;

        float radius = CELL_SIZE / 2;
        for (int a = 0; a < 360; a++){
            var particle = create(this.random.nextCircleVector(),
                    new Vector2f(left_edge_x + (CELL_SIZE / 2), top_edge_y + (CELL_SIZE/2)),
                    sizeMean, sizeStd, speedMean, speedStd, lifetimeMean, lifetimeStd);
            particles.put(particle.name, particle);
        }    }


    public Collection<Particle> getParticles() {
        return this.particles.values();
    }

    private Particle create(Vector2f direction, Vector2f position, float sizeMean, float sizeStdDev,
                            float speedMean, float speedStdDev, float lifetimeMean, float lifetimeStdDev) {
        float size = (float) this.random.nextGaussian(sizeMean, sizeStdDev);
        var p = new Particle(
                position,
                direction,
                (float) this.random.nextGaussian(speedMean, speedStdDev),
                new Vector2f(size, size),
                this.random.nextGaussian(lifetimeMean, lifetimeStdDev));

        return p;
    }
}

