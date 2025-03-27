package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class YouText {
    public static Entity create(Texture square, int x, int y) {
        var youText = new Entity();

        youText.add(new ecs.Components.IsYouText());
        youText.add(new ecs.Components.Appearance(square, Color.WHITE));
        youText.add(new ecs.Components.Position(x, y));
        youText.add(new ecs.Components.Collision());
        youText.add(new ecs.Components.Pushable());

        return youText;
    }
}
