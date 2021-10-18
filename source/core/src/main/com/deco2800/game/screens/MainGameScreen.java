package com.deco2800.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.deco2800.game.GdxGame;
import com.deco2800.game.areas.AreaManager;
import com.deco2800.game.areas.AreaService;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.maingame.MainGameActions;
import com.deco2800.game.components.mainmenu.MainMenuDisplay;
import com.deco2800.game.components.player.KeyboardPlayerInputComponent;
import com.deco2800.game.components.player.PlayerStatsDisplay;
import com.deco2800.game.components.powerups.PowerUpGUIComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.entities.factories.DeathFactory;
import com.deco2800.game.entities.factories.ObstacleFactory;
import com.deco2800.game.entities.factories.PlayerFactory;
import com.deco2800.game.entities.factories.RenderFactory;
import com.deco2800.game.input.InputComponent;
import com.deco2800.game.input.InputDecorator;
import com.deco2800.game.input.InputService;
import com.deco2800.game.physics.PhysicsEngine;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.rendering.RenderService;
import com.deco2800.game.rendering.Renderer;
import com.deco2800.game.services.GameTime;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.services.SoundService;
import com.deco2800.game.ui.terminal.Terminal;
import com.deco2800.game.ui.terminal.TerminalDisplay;
import com.deco2800.game.components.maingame.MainGamePannelDisplay;
import com.deco2800.game.components.gamearea.PerformanceDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

/**
 * The game screen containing the main game.
 *
 * <p>Details on libGDX screens: https://happycoding.io/tutorials/libgdx/game-screens
 */
public class MainGameScreen extends ScreenAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MainGameScreen.class);
    private static final String[] mainGameTextures = {
            "images/PowerUpGUI/Shield.png",
            "images/PowerUpGUI/Spear.png",
            "images/PowerUpGUI/Lightning.png",
            "images/PowerUpGUI/Empty.png",
            "images/PowerUpGUI/lightning0.png",
            "images/PowerUpGUI/lightning1.png",
            "images/PowerUpGUI/shield0.png",
            "images/PowerUpGUI/shield1.png",
            "images/PowerUpGUI/shield2.png",
            "images/PowerUpGUI/shield3.png",
            "images/PowerUpGUI/spear0.png",
            "images/PowerUpGUI/spear1.png",
            "images/PowerUpGUI/spear2.png",
            "images/PowerUpGUI/spear3.png",
            "images/disp_back.png",
            "images/story/storyline.png",
            "images/healthBar.png",
            "images/healthBarBack.png"
    };

    private static final Vector2 CAMERA_POSITION = new Vector2(7.5f, 6f);

    private final GdxGame game;
    private final Renderer renderer;
    private final PhysicsEngine physicsEngine;
    private AreaManager ragnarokManager;
    private Entity gameUI;
    private boolean gameEnded;
    private boolean deathAnimation;

    public MainGameScreen(GdxGame game) {
        this.game = game;
        logger.debug("Initialising main game screen services");
        ServiceLocator.registerTimeSource(new GameTime());
        this.gameEnded = false;
        this.deathAnimation = false;

        PhysicsService physicsService = new PhysicsService();
        ServiceLocator.registerPhysicsService(physicsService);
        physicsEngine = physicsService.getPhysics();

        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());

        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());
        ServiceLocator.registerAreaService(new AreaService());

        ServiceLocator.registerSoundService(new SoundService("mainGame"));

        renderer = RenderFactory.createRenderer();
        renderer.getCamera().getEntity().setPosition(CAMERA_POSITION);
        renderer.getDebug().renderPhysicsWorld(physicsEngine.getWorld());

        loadAssets();
        createUI();

        logger.debug("Initialising main game screen entities");
        TerrainFactory terrainFactory = new TerrainFactory(renderer.getCamera());

        ragnarokManager = new AreaManager(terrainFactory);

        // plays the music
        ServiceLocator.getSoundService().playMusic("bob");

        // plays fire crackling sound effects
        ServiceLocator.getSoundService().playSound("fire");

        ServiceLocator.getAreaService().setManager(ragnarokManager);
        ServiceLocator.getAreaService().run();
    }

    @Override
    public void render(float delta) {

        ServiceLocator.getTerminalService().processMessageBuffer();
        ServiceLocator.getEntityService().update();
        ServiceLocator.getSoundService().update();

        physicsEngine.update();
        renderer.updateCameraPosition(ragnarokManager.getPlayer());

        if (ragnarokManager.getPlayer() != null) {
            Entity player = ragnarokManager.getPlayer();

            if (player.getComponent(CombatStatsComponent.class).getHealth() == 0) {

                long currentScore = player.getComponent(PlayerStatsDisplay.class).getPlayerScore();
                if (currentScore > MainMenuDisplay.getHighScoreValues()[4]) {

                    recordHighScore("" + currentScore);

                }
                if (!gameEnded) {
                    Entity death;
                    if (!deathAnimation) {
                        death = DeathFactory.createPlayerDeath(gameUI);
                        death.setPosition(player.getPosition());
                        player.getComponent(AnimationRenderComponent.class).stopAnimation();
                        player.getComponent(KeyboardPlayerInputComponent.class).dispose();
                        player.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.StaticBody);
                        ServiceLocator.getEntityService().register(death);
                        Entity fade = DeathFactory.createFade();
                        fade.setPosition(player.getPosition().x - 20, -5);
                        fade.setScale(40f, 20f);
                        ServiceLocator.getEntityService().register(fade);
                        fade.getComponent(AnimationRenderComponent.class).startAnimation("deathFade");
                        death.getComponent(AnimationRenderComponent.class).startAnimation("death-right");
                        ServiceLocator.getSoundService().playMusic("death");
                        deathAnimation = true;
                    }

                    gameEnded = true;

                }
            }
        }

        renderer.render();

        //TODO: do terminal requests

    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        logger.trace("Resized renderer: ({} x {})", width, height);
    }

    @Override
    public void pause() {
        logger.info("Game paused");
    }

    @Override
    public void resume() {
        logger.info("Game resumed");
    }

    @Override
    public void dispose() {
        logger.debug("Disposing main game screen");

        renderer.dispose();

        unloadAssets();

        ServiceLocator.getEntityService().dispose();
        ServiceLocator.getRenderService().dispose();
        ServiceLocator.getResourceService().dispose();

        ServiceLocator.clear();
    }

    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(mainGameTextures);

        ServiceLocator.getSoundService().loadAssets();

        // because SoundService calls the resource service, this call
        // must be last in the loadAssets
        ServiceLocator.getResourceService().loadAll();
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        ServiceLocator.getSoundService().unloadAssets();
        resourceService.unloadAssets(mainGameTextures);
    }

    /**
     * Creates the main game's ui including components for rendering ui elements to the screen and
     * capturing and handling ui input.
     */
    private void createUI() {
        logger.debug("Creating ui");
        Stage stage = ServiceLocator.getRenderService().getStage();
        InputComponent inputComponent =
                ServiceLocator.getInputService().getInputFactory().createForTerminal();

        Terminal theOg = new Terminal();
        ServiceLocator.registerTerminalService(theOg);

        gameUI = new Entity();
        gameUI.addComponent(new InputDecorator(stage, 10))
                .addComponent(new PerformanceDisplay())
                .addComponent(new MainGameActions(this.game))
                .addComponent(new MainGamePannelDisplay())
                .addComponent(theOg)
                .addComponent(inputComponent)
                .addComponent(new TerminalDisplay())
                .addComponent(new PowerUpGUIComponent());

        ServiceLocator.getEntityService().register(gameUI);
    }

    private void recordHighScore(String currentScore) {

        String[] availableNames = {"Zebra", "Fox", "Dire Wolf", "Lion", "Puma", "Kitten", "Mouse", "Orca", "Dragonfly", "Unicorn"};
        String[] availableAdjectives = {"Tiny", "Spirited", "Curious", "Colourful", "Trained", "Triumphant", "Extraordinary", "Fierce", "Unbeatable", "Unique"};
        char[] scoreIntegers = currentScore.toCharArray();
        String firstCharacter = scoreIntegers[0] + "";
        String randValue = "" + scoreIntegers[scoreIntegers.length - 1];
        String adjective = "Wandering";

        int lengthScore = scoreIntegers.length;
        if (lengthScore < availableAdjectives.length) {

            int section = 0;

            if (Integer.parseInt(firstCharacter) > 5) {
                section = 1;
            }
            adjective = availableAdjectives[((scoreIntegers.length - 1) * 2) + section];
        }

        String selectedName = MainMenuDisplay.getPlayerName();
        String name;

        if (!selectedName.equals("Random") && !selectedName.equals("Select Name")) {
            name = adjective + " " + selectedName;
        } else {
            name = adjective + " " + availableNames[Integer.parseInt(randValue)] + "";
        }

        int[] highScoreValues = MainMenuDisplay.getHighScoreValues();
        String[] highScoreNames = MainMenuDisplay.getHighScoreNames();

        highScoreNames[4] = name;
        highScoreValues[4] = Integer.parseInt(currentScore);

        try (FileWriter highScoreFile = new FileWriter("gameinfo/highScores.txt")) {
            for (int i = 0; i < highScoreNames.length; i++) {
                highScoreFile.write(highScoreNames[i] + "," + highScoreValues[i] + "\n");
            }
        } catch (IOException e) {
            logger.info("Could not record high score");
        }
    }
}
