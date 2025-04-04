package ecs.Systems;

import ecs.Components.Particles;
import ecs.Particle;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ParticleSystem extends System {
    private final MyRandom random = new MyRandom();

    private Graphics2D graphics;
    private Vector2f center;
    private Texture texParticle;

    public ParticleSystem(Vector2f center, Graphics2D graphics) {
        super(ecs.Components.Appearance.class);


        this.texParticle = new Texture("resources/images/flag/flag-1.png");
        this.graphics = graphics;
        this.center = center;
    }

    public void update(double gameTime) {
        for (var entity: entities.values()){
            if (entity.contains(ecs.Components.Particles.class)) {
                Particles particles_component = entity.get(ecs.Components.Particles.class);
                HashMap<Long, Particle> particles = particles_component.particles;
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

                if (particles.isEmpty()) {
                    entity.remove(ecs.Components.Particles.class);
                } else {
                    for (Particle particle : particles.values()) {
                        graphics.draw(texParticle, particle.area, particle.rotation, particle.center, Color.WHITE);
                    }
                }
            }
        }
    }

//    public void sparkleSquare(int gridSize, int x_pos, int y_pos, float xOffset, float yOffset, float cellSize){
//
//        //Vector2f particleCenter = new Vector2f(x_pos * cellSize + xOffset, y_pos * cellSize + yOffset);
//
//        float topLeftCornerX = x_pos - cellSize/2;
//        float topLeftCornerY = y_pos - cellSize/2;
//
//        int sparklesPerBorner = 10;
//
//        float increment = cellSize / sparklesPerBorner;
//        for (int i = 0; i < sparklesPerBorner ; i++){
//            var particle = create(topLeftCornerX + (increment * i), topLeftCornerY, 0);
//            particles.put(particle.name, particle);
//        }
//    }

//    private Particle create() {
//        float size = (float) this.random.nextGaussian(this.sizeMean, this.sizeStdDev);
//        var p = new Particle(
//                new Vector2f(this.center.x, this.center.y),
//                this.random.nextCircleVector(),
//                (float) this.random.nextGaussian(this.speedMean, this.speedStdDev),
//                new Vector2f(size, size),
//                this.random.nextGaussian(this.lifetimeMean, this.lifetimeStdDev) / 10);
//
//        return p;
//    }
//
//    private Particle create(float x_mag, float y_mag, float rotation) {
//        float size = (float) this.random.nextGaussian(this.sizeMean, this.sizeStdDev);
//        var p = new Particle(
//                new Vector2f(this.center.x, this.center.y),
//                new Vector2f(x_mag , y_mag ),
//                rotation,
//                new Vector2f(size, size),
//                this.random.nextGaussian(this.lifetimeMean, this.lifetimeStdDev) / 10);
//
//        return p;
//    }
}

