package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import com.deco2800.game.ai.tasks.AITaskComponent;

import com.deco2800.game.components.CameraComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.player.PlayerActions;
import com.deco2800.game.components.player.PlayerStatsDisplay;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;
import com.deco2800.game.components.tasks.WanderTask;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.configs.PlayerConfig;

import com.deco2800.game.files.FileLoader;
import com.deco2800.game.input.InputComponent;

import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.*;
import com.deco2800.game.rendering.AnimationRenderComponent;
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
            .addComponent(inputComponent)
            .addComponent(new PlayerStatsDisplay())
            .addComponent(new CameraComponent())
            .addComponent(new LightningPowerUpComponent())
            .addComponent(new ShieldPowerUpComponent())
            .addComponent(new SpearPowerUpComponent());

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
    animator.addAnimation("crouch-left", 0.2f,
        Animation.PlayMode.LOOP);
    animator.addAnimation("crouch-right", 0.2f,
        Animation.PlayMode.LOOP);

    animator.addAnimation("jump-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("jump-right", 1f,
            Animation.PlayMode.LOOP);

    animator.addAnimation("run-left", 0.2f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("run-right", 0.2f,
              Animation.PlayMode.LOOP);

    animator.addAnimation("spear-still-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-still-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-crouch-still-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-crouch-still-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-jump-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-jump-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-run-left", 0.2f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-run-right", 0.2f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-crouch-left", 0.2f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("spear-crouch-right", 0.2f,
            Animation.PlayMode.LOOP);

    animator.addAnimation("shield-still-right", 1f,
                    Animation.PlayMode.LOOP);
    animator.addAnimation("shield-still-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-crouch-still-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-crouch-still-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-jump-left", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-jump-right", 1f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-run-left", 0.2f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-run-right", 0.2f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-crouch-left", 0.2f,
            Animation.PlayMode.LOOP);
    animator.addAnimation("shield-crouch-right", 0.2f,
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

    // custom player collision boxes
    // create head collision box
    PolygonShape head = new PolygonShape();
    Vector2 headOffset = new Vector2(player.getCenterPosition().x,
            player.getCenterPosition().y + 0.35f);
    head.setAsBox(0.1f,0.15f,headOffset,0f);
    player.getComponent(PhysicsComponent.class).getBody().createFixture(head,1.0f);

    // create leg circle collision box
    CircleShape legs = new CircleShape();
    legs.setRadius(0.2f);
    Vector2 circleOffset = new Vector2(player.getCenterPosition().x,
            player.getCenterPosition().y - 0.3f);
    legs.setPosition(circleOffset);
    player.getComponent(PhysicsComponent.class).getBody().createFixture(legs,1.0f);
    player.getComponent(ColliderComponent.class).setAsBox(new Vector2(0.3f, 0.9f));

    // create body collision box set using the collider component
    Vector2 boxOffset = new Vector2(player.getCenterPosition().x,
            player.getCenterPosition().y + 0.02f);
    Vector2 boxSize = new Vector2(0.35f,0.4f);
    player.getComponent(ColliderComponent.class).setAsBox(boxSize,boxOffset);
    player.getComponent(ColliderComponent.class).setDensity(1.0f);

    player.getComponent(AnimationRenderComponent.class).scaleEntity();
    player.getComponent(AnimationRenderComponent.class).startAnimation("spear-still-right");

    // gravity scalar used to multiply gravity from physics engine, used 5 for
    // base character vary based on how heavy we want characters to look
    player.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

    player.setType(EntityTypes.PLAYER);

    return player;
  }

/**
 * Relative player is basically an object that abstracts the illusion of forward movement.
 * Its x value is always going up, and obstacles are generated and moved based on its movement.
 */
  public static Entity createAbstractPlayer() {
    AITaskComponent aiComponent =
            new AITaskComponent()
                    .addTask(new WanderTask(new Vector2(10f, 0f), 5f));

    Entity abstractPlayer =
            new Entity()
                    .addComponent(new PhysicsComponent())
                    .addComponent(new AbstractPlayerMovementComponent());

    abstractPlayer.setPosition(0f, 0f);
    return abstractPlayer;
  }

  private PlayerFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
