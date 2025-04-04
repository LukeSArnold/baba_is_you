package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Rock {
    public static Entity create(Texture square, int x, int y) {
        var rock = new Entity();

        rock.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        rock.add(new ecs.Components.Position(x, y));
        rock.add(new ecs.Components.IsRock());

        return rock;
    }
}
