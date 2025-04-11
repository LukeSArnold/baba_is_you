package ecs.Systems;

import ecs.Components.Movable;
import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import org.joml.Vector2i;

import java.lang.reflect.GenericArrayType;

/**
 * This system is responsible for handling the movement of any
 * entity with movable & position components.
 */
public class Movement extends System {
    private SoundManager audio = new SoundManager();
    private Sound popSound;

    public Movement() {
        super(ecs.Components.Appearance.class);
        popSound = audio.load("pop", "resources/audio/Pop.ogg", false);
    }

    @Override
    public void update(double elapsedTime) {
        for (var entity : entities.values()) {
            if (entity.contains(ecs.Components.Movable.class)) {
                moveEntity(entity, elapsedTime);
            }
        }
    }

    private void moveEntity(ecs.Entities.Entity entity, double elapsedTime) {
        var movable = entity.get(ecs.Components.Movable.class);
        movable.elapsedInterval += elapsedTime;
        if (movable.elapsedInterval >= movable.moveInterval) {
            movable.elapsedInterval -= movable.moveInterval;
            switch (movable.input) {
                case Movable.Direction.Up:
                    move(entity, 0, -1);
                    break;
                case Movable.Direction.Down:
                    move(entity, 0, 1);
                    break;
                case Movable.Direction.Left:
                    move(entity, -1, 0);
                    break;
                case Movable.Direction.Right:
                    move(entity, 1, 0);
                    break;
            }
        }
    }

    private boolean movePushableRecursive(ecs.Entities.Entity entity, int xIncrement, int yIncrement){
        var position = entity.get(ecs.Components.Position.class);
        int proposed_x = position.getX() + xIncrement;
        int proposed_y = position.getY() + yIncrement;

        for (var pros_entity: entities.values()){
            var pros_entity_position = pros_entity.get(ecs.Components.Position.class);
            if ((pros_entity_position.getX() == proposed_x) && (pros_entity_position.getY() == proposed_y)) {
                if (pros_entity != entity) {
                    if (!pros_entity.contains(ecs.Components.Stoppable.class)) {
                        if (pros_entity.contains(ecs.Components.Pushable.class)) {
                            if (movePushableRecursive(pros_entity, xIncrement, yIncrement)) {
                                position.setX(position.getX() + xIncrement);
                                position.setY(position.getY() + yIncrement);
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        position.setX(position.getX() + xIncrement);
        position.setY(position.getY() + yIncrement);
        return true;
    }

    private boolean eligiblePosition(ecs.Entities.Entity entity, Movable.Direction direction, int xIncrement, int yIncrement){
        var position = entity.get(ecs.Components.Position.class);
        int proposed_x = position.getX() + xIncrement;
        int proposed_y = position.getY() + yIncrement;

        for (var pros_entity : entities.values()) {
            if (pros_entity.contains(ecs.Components.Stoppable.class)) {
                var pros_entity_position = pros_entity.get(ecs.Components.Position.class);
                if ((pros_entity_position.getX() == proposed_x) && (pros_entity_position.getY() == proposed_y)){
                    return false;
                }
            }
            if (pros_entity.contains(ecs.Components.Pushable.class)) {
                var pros_entity_position = pros_entity.get(ecs.Components.Position.class);
                if ((pros_entity_position.getX() == proposed_x) && (pros_entity_position.getY() == proposed_y)) {
                    return movePushableRecursive(pros_entity, xIncrement, yIncrement);
                }
            }
        }
        return true;
    }

    private void move(ecs.Entities.Entity entity, int xIncrement, int yIncrement) {
        popSound.stop();
        var movable = entity.get(ecs.Components.Movable.class);
        var position = entity.get(ecs.Components.Position.class);

        if (eligiblePosition(entity, movable.input, xIncrement, yIncrement)) {
            position.setX(position.getX() + xIncrement);
            position.setY(position.getY() + yIncrement);
            popSound.play();
        }

        movable.input = Movable.Direction.Stopped;

    }
}
