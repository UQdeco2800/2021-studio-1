package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.CameraComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.player.InventoryComponent;
import com.deco2800.game.components.player.PlayerActions;
import com.deco2800.game.components.player.PlayerStatsDisplay;
import com.deco2800.game.components.tasks.WanderTask;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.configs.PlayerConfig;
import com.deco2800.game.files.FileLoader;
import com.deco2800.game.input.InputComponent;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.*;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * Factory to create a player entity.
 *
 * <p>Predefined player properties are loaded from a config stored as a json file and should have
 * the properties stores in 'PlayerConfig'.
 */
public class PlayerFactory {
  private static final PlayerConfig stats =
      FileLoader.readClass(PlayerConfig.class, "configs/player.json");

  /**
   * Create a player entity.
   * @return entity
   */
  public static Entity createPlayer() {
    InputComponent inputComponent =
        ServiceLocator.getInputService().getInputFactory().createForPlayer();

    Entity player =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PLAYER))
            .addComponent(new PlayerActions())
            .addComponent(new CombatStatsComponent(stats.health, stats.baseAttack))
            .addComponent(new InventoryComponent(stats.gold))
            .addComponent(inputComponent)
            .addComponent(new PlayerStatsDisplay())
            .addComponent(new CameraComponent());

    AnimationRenderComponent animator =
            new AnimationRenderComponent(ServiceLocator.getResourceService()
                    .getAsset("images/odin.atlas", TextureAtlas.class));

    animator.addAnimation("still-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("still-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("crouch-still-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("crouch-still-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("jump-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("jump-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("run-left", 0.1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("run-right", 0.1f,
              Animation.PlayMode.LOOP);
    animator.addAnimation("crouch-left", 0.1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("crouch-right", 0.1f,
            Animation.PlayMode.LOOP);

    animator.addAnimation("attack-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("attack-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("power-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("power-left", 1f,
            Animation.PlayMode.LOOP);

    player.addComponent(animator);
    PhysicsUtils.setScaledCircleCollider(player, 0.3f);
    player.getComponent(ColliderComponent.class).setDensity(1.0f);
    player.getComponent(AnimationRenderComponent.class).scaleEntity();
    //gravity scalar used to multiply gravity from physics engine, used 5 for
    // base character
    //vary based on how heavy we want characters to look
    player.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

    return player;
  }

  // relative player is basically an object that abstracts the illusion of forward movement.
  // its x value is always going up, and obstacles r generated and moved based on its movement.
  // all else is meh
  public static Entity createAbstractPlayer() {
    AITaskComponent aiComponent =
            new AITaskComponent()
                    .addTask(new WanderTask(new Vector2(10f, 0f), 5f));
    //.addTask(new AttackTask(new Vector2(10f, 0f), 5f));
    //.addTask(new ChaseTask(target, 10, 3f, 4f));
    Entity abstractPlayer =
            new Entity()
                    .addComponent(new PhysicsComponent())
                    .addComponent(new AbstractPlayerMovementComponent());
                    //.addComponent(new ColliderComponent())
                    //.addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                    //.addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
                    //.addComponent(aiComponent);

    //abstractPlayer.getComponent(PhysicsComponent.class).getBody().
    //PhysicsUtils.setScaledCollider(abstractPlayer, 0.9f, 0.4f);
    //abstractPlayer.getComponent(PhysicsComponent.class).setGravityScale(0f);

    abstractPlayer.setPosition(0f, 0f);
    return abstractPlayer;
  }

  private PlayerFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
