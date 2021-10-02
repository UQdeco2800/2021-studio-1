package com.deco2800.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.deco2800.game.GdxGame;
import com.deco2800.game.areas.AreaManager;
import com.deco2800.game.areas.AreaService;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.maingame.MainGameActions;
import com.deco2800.game.components.mainmenu.MainMenuDisplay;
import com.deco2800.game.components.player.PlayerStatsDisplay;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.entities.factories.RenderFactory;
import com.deco2800.game.input.InputComponent;
import com.deco2800.game.input.InputDecorator;
import com.deco2800.game.input.InputService;
import com.deco2800.game.physics.PhysicsEngine;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.rendering.RenderService;
import com.deco2800.game.rendering.Renderer;
import com.deco2800.game.services.GameTime;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
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
  private static final String[] mainGameTextures = {};

  private static final Vector2 CAMERA_POSITION = new Vector2(7.5f, 6f);

  private final GdxGame game;
  private final Renderer renderer;
  private final PhysicsEngine physicsEngine;
  private AreaManager ragnarokManager;

  public MainGameScreen(GdxGame game) {
    this.game = game;
    logger.debug("Initialising main game screen services");
    ServiceLocator.registerTimeSource(new GameTime());

    PhysicsService physicsService = new PhysicsService();
    ServiceLocator.registerPhysicsService(physicsService);
    physicsEngine = physicsService.getPhysics();

    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());

    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerAreaService(new AreaService());

    renderer = RenderFactory.createRenderer();
    renderer.getCamera().getEntity().setPosition(CAMERA_POSITION);
    renderer.getDebug().renderPhysicsWorld(physicsEngine.getWorld());

    loadAssets();
    createUI();

    logger.debug("Initialising main game screen entities");
    TerrainFactory terrainFactory = new TerrainFactory(renderer.getCamera());

    ragnarokManager = new AreaManager(terrainFactory);

    ServiceLocator.getAreaService().setManager(ragnarokManager);
    ServiceLocator.getAreaService().run(); //TODO: call run on manager from terminal line

    //ragnarokManager.create();

    //boolean isObstacle = false;
    //if (isObstacle) {
      //ObstacleArea obstacleArea = new ObstacleArea(terrainFactory);
      //obstacleArea.create();
    //} else if (!isObstacle) {

      //ObstacleArea obstacleArea = new ObstacleArea(terrainFactory);
      //obstacleArea.create()

      //ragnarokArea = new RagnarokArea("the og", terrainFactory);

      //TODO: abstract commands from RacerArea to GameArea
      //TODO: so that they can be called from any GameArea
      //ServiceLocator.getAreaService().setMainRacerArea(ragnarokArea);

      //ragnarokArea.create();
    //}
  }

  @Override
  public void render(float delta) {
    
    ServiceLocator.getTerminalService().processMessageBuffer();
    ServiceLocator.getEntityService().update();

    physicsEngine.update();
    renderer.updateCameraPosition(ragnarokManager.getPlayer());

    if (ragnarokManager.getPlayer() != null) {
        Entity player = ragnarokManager.getPlayer();

        if (player.getComponent(CombatStatsComponent.class).getHealth() == 0) {

        long currentScore = player.getComponent(PlayerStatsDisplay.class).getPlayerScore();

        if (currentScore > MainMenuDisplay.getHighScore()) {

            recordHighScore("" + currentScore);

        }
        game.setScreen(GdxGame.ScreenType.MAIN_MENU);
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
    ServiceLocator.getResourceService().loadAll();
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
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

    Entity ui = new Entity();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(new PerformanceDisplay())
        .addComponent(new MainGameActions(this.game))
        .addComponent(new MainGamePannelDisplay())
        .addComponent(theOg)
        .addComponent(inputComponent)
        .addComponent(new TerminalDisplay());

    ServiceLocator.getEntityService().register(ui);
  }

    private void recordHighScore(String currentScore) {

      String[] availableNames = {"Zebra", "Fox", "Hound", "Lion", "Puma", "Kitten", "Mouse", "Orca", "Dragonfly", "Unicorn"};
      char[] scoreIntegers = currentScore.toCharArray();
      String randValue = "" + scoreIntegers[scoreIntegers.length - 1];
      String name = availableNames[Integer.parseInt(randValue)];

      try (FileWriter highScoreFile = new FileWriter("gameinfo/highScores.txt")){
          highScoreFile.write(name + "," + currentScore);
      } catch (IOException e) {
          logger.info("Could not record high score");
      }
  }
}
