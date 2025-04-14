import ecs.Entities.*;
import ecs.Systems.*;
import ecs.Systems.AnimatedSprite;
import ecs.Systems.KeyboardInput;
import edu.usu.graphics.*;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final int GRID_SIZE = 50;

    private final List<Entity> removeThese = new ArrayList<>();
    private final List<Entity> addThese = new ArrayList<>();

    private ecs.Systems.Renderer sysRenderer;
    private ecs.Systems.Collision sysCollision;
    private ecs.Systems.Movement sysMovement;
    private ecs.Systems.KeyboardInput sysKeyboardInput;
    private ecs.Systems.RuleManager sysRuleManager;
    private ecs.Systems.AnimatedSprite sysAnimatedSprites;
    private ecs.Systems.ParticleSystem sysParticles;

    private LevelMapper levelMapper = new LevelMapper("levels-all.bbiy");
    private Level level;

    public void initialize(Graphics2D graphics, int levelId) {
        level = levelMapper.levels.get(levelId);

        sysRenderer = new Renderer(graphics, level.width);
        sysRuleManager = new RuleManager(level.width, level.height, GRID_SIZE, graphics.getWindow());
        sysMovement = new Movement();
        sysKeyboardInput = new KeyboardInput(graphics.getWindow());
        sysAnimatedSprites = new AnimatedSprite();
        sysParticles = new ParticleSystem(new Vector2f(0, 0), graphics);


        // initialize all entities with textures
        initializeLevelEntities(level);
    }

    public void update(double elapsedTime) {
        sysKeyboardInput.update(elapsedTime);
        sysMovement.update(elapsedTime);
        sysRuleManager.update(elapsedTime);
        sysAnimatedSprites.update(elapsedTime);
        sysParticles.update(elapsedTime);

        for (var entity : removeThese) {
            removeEntity(entity);
        }
        removeThese.clear();

        for (var entity : addThese) {
            addEntity(entity);
        }
        addThese.clear();

        sysRenderer.update(elapsedTime);
    }

    private void initializeLevelEntities(Level level){
        var texSquare = new Texture("resources/images/wall.png");
        var wallTexture = new Texture("resources/images/wall.png");
        var rockTexture = new Texture("resources/images/rock.png");
        var flagTexture = new Texture("resources/images/flag.png");
        var babaTexture = new Texture("resources/images/word-baba.png");
        var floorTexture = new Texture("resources/images/floor.png");
        var grassTexture = new Texture("resources/images/grass.png");
        var waterTexture = new Texture("resources/images/water.png");
        var lavaTexture = new Texture("resources/images/lava.png");
        var hedgeTexture = new Texture("resources/images/hedge.png");

        var wallTextTexture = new Texture("resources/images/word-wall.png");
        var rockTextTexture = new Texture("resources/images/word-rock.png");
        var flagTextTexture = new Texture("resources/images/word-flag.png");
        var babaTextTexture = new Texture("resources/images/word-baba.png");
        var isTextTexture = new Texture("resources/images/word-is.png");
        var stopTextTexture = new Texture("resources/images/word-stop.png");
        var pushTextTexture = new Texture("resources/images/word-push.png");
        var lavaTextTexture = new Texture("resources/images/word-lava.png");
        var waterTextTexture = new Texture("resources/images/word-water.png");
        var youTextTexture = new Texture("resources/images/word-you.png");
        var winTextTexture = new Texture("resources/images/word-win.png");
        var sinkTextTexture = new Texture("resources/images/word-sink.png");
        var killTextTexture = new Texture("resources/images/word-kill.png");

        for (String[][] layer: level.content) {
            for (int row = 0; row < layer[0].length; row++) {
                for (int col = 0; col < layer.length; col++) {
                    String object = layer[row][col];

                    if (object != null) {
                        // non text objects
                        if (object.equals("w")) {
                            var wall = Wall.create(wallTexture, col, row);
                            addThese.add(wall);
                        } else if (object.equals("r")) {
                            var rock = Rock.create(rockTexture, col, row);
                            addThese.add(rock);
                        } else if (object.equals("f")) {
                            var flag = Flag.create(flagTexture, col, row);
                            addThese.add(flag);
                        } else if (object.equals("b")) {
                            var baba = Baba.create(babaTexture, col, row);
                            addThese.add(baba);
                        } else if (object.equals("l")) {
                            var floor = Floor.create(floorTexture, col, row);
                            addThese.add(floor);
                        } else if (object.equals("g")) {
                            var grass = Grass.create(grassTexture, col, row);
                            addThese.add(grass);
                        } else if (object.equals("a")) {
                            var water = Water.create(waterTexture, col, row);
                            addThese.add(water);
                        } else if (object.equals("v")) {
                            var lava = Lava.create(lavaTexture, col, row);
                            addThese.add(lava);
                        } else if (object.equals("h")) {
                            var hedge = Hedge.create(hedgeTexture, col, row);
                            addThese.add(hedge);
                        }

                        //textual objects
                        else if (object.equals("W")) {
                            var wallText = WallText.create(wallTextTexture, col, row);
                            addThese.add(wallText);
                        } else if (object.equals("R")) {
                            var rockText = RockText.create(rockTextTexture, col, row);
                            addThese.add(rockText);
                        } else if (object.equals("F")) {
                            var flagText = FlagText.create(flagTextTexture, col, row);
                            addThese.add(flagText);
                        } else if (object.equals("B")) {
                            var babaText = BabaText.create(babaTextTexture, col, row);
                            addThese.add(babaText);
                        } else if (object.equals("I")) {
                            var isText = IsText.create(isTextTexture, col, row);
                            addThese.add(isText);
                        } else if (object.equals("S")) {
                            var stopText = StopText.create(stopTextTexture, col, row);
                            addThese.add(stopText);
                        } else if (object.equals("P")) {
                            var pushText = PushText.create(pushTextTexture, col, row);
                            addThese.add(pushText);
                        } else if (object.equals("V")) {
                            var lavaText = LavaText.create(lavaTextTexture, col, row);
                            addThese.add(lavaText);
                        } else if (object.equals("A")) {
                            var waterText = WaterText.create(waterTextTexture, col, row);
                            addThese.add(waterText);
                        } else if (object.equals("Y")) {
                            var youText = YouText.create(youTextTexture, col, row);
                            addThese.add(youText);
                        } else if (object.equals("X")) {
                            var winText = WinText.create(winTextTexture, col, row);
                            addThese.add(winText);
                        } else if (object.equals("N")) {
                            var sinkText = SinkText.create(sinkTextTexture, col, row);
                            addThese.add(sinkText);
                        } else if (object.equals("K")) {
                            var killText = KillText.create(killTextTexture, col, row);
                            addThese.add(killText);
                        }
                    }
                }
            }
        }
    }

    private void addEntity(Entity entity) {
        sysKeyboardInput.add(entity);
        sysMovement.add(entity);
        sysRenderer.add(entity);
        sysRuleManager.add(entity);
        sysAnimatedSprites.add(entity);
        sysParticles.add(entity);
    }

    private void removeEntity(Entity entity) {
        sysKeyboardInput.remove(entity.getId());
        sysMovement.remove(entity.getId());
        sysRenderer.remove(entity.getId());
        sysRuleManager.remove(entity.getId());
        sysAnimatedSprites.remove(entity.getId());
        sysParticles.remove(entity.getId());
    }
}
