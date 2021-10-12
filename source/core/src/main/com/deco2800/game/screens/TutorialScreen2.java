package com.deco2800.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.deco2800.game.GdxGame;
import com.deco2800.game.components.tutorial.Tutorial2Actions;
import com.deco2800.game.components.tutorial.Tutorial2Display;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.entities.factories.RenderFactory;
import com.deco2800.game.input.InputDecorator;
import com.deco2800.game.input.InputService;
import com.deco2800.game.rendering.RenderService;
import com.deco2800.game.rendering.Renderer;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.services.SoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game screen containing the main menu.
 */
public class TutorialScreen2 extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(TutorialScreen2.class);
  private final GdxGame game;
  private final Renderer renderer;
  private static final String[] tutorialTextures2 = {"images/tutorial/instruct2.png"};
  public TutorialScreen2(GdxGame game) {
    this.game = game;

    logger.debug("Initialising tutorial screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerSoundService(new SoundService("storyScreen"));

    renderer = RenderFactory.createRenderer();

    loadAssets();
    createUI();

    ServiceLocator.getSoundService().playMusic("intro");
    ServiceLocator.getSoundService().setMusicLoop(true);
  }

  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
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
    logger.debug("Disposing tutorial screen");

    renderer.dispose();
    unloadAssets();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    ServiceLocator.clear();
  }

  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(tutorialTextures2);
    ServiceLocator.getSoundService().loadAssets();
    ServiceLocator.getResourceService().loadAll();
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(tutorialTextures2);
    ServiceLocator.getSoundService().unloadAssets();
  }

  /**
   * Creates the main menu's ui including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    ui.addComponent(new Tutorial2Display())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new Tutorial2Actions(game));
    ServiceLocator.getEntityService().register(ui);
  }
}
