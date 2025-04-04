package ecs.Systems;

import ecs.Components.Appearance;

public class AnimatedSprite extends System{
    public AnimatedSprite(){
        super(ecs.Components.Appearance.class);
    }

    @Override
    public void update(double elapsedTime) {
        for (var entity: entities.values()){
            Appearance appearance = entity.get(ecs.Components.Appearance.class);

            appearance.animationTime += elapsedTime;
            if (appearance.animationTime >= appearance.spriteTime[appearance.subImageIndex]) {
                appearance.animationTime -= appearance.spriteTime[appearance.subImageIndex];
                appearance.subImageIndex++;
                appearance.subImageIndex = appearance.subImageIndex % appearance.spriteTime.length;
            }
        }
    }
}
