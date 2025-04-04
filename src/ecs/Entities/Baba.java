package ecs.Entities;


import edu.usu.graphics.Texture;

public class Baba {
    public static Entity create(Texture spriteSheet, int x, int y) {
        var baba = new Entity();

        baba.add(new ecs.Components.Appearance(spriteSheet, new float[]{0.2f, 0.2f, 0.2f}));
        baba.add(new ecs.Components.Position(x, y));
        baba.add(new ecs.Components.IsBaba());

        return baba;
    }
}
