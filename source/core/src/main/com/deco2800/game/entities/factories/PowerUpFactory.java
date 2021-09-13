package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.entities.Entity;

import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;

import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.services.ServiceLocator;
import net.dermetfan.gdx.physics.box2d.PositionController;

public class PowerUpFactory {

    /**
     * Create a lightning power up entity.
     *
     * @return lightning power up entity
     */
    public static Entity createLightningPowerUp() {
        Entity powerUp = createBasePowerUp();
        powerUp.addComponent(new TextureRenderComponent(
                        "images/powerup-lightning.png"));

        powerUp.setType(EntityTypes.LIGHTNINGPOWERUP);

        return powerUp;
    }

    /**
     * Create a shield power up entity.
     *
     * @return shield power up entity.
     */
    public static Entity createShieldPowerUp() {
        Entity powerUp = createBasePowerUp();
        powerUp.addComponent(new TextureRenderComponent(
                "images/powerup-shield.png"));

        powerUp.setType(EntityTypes.SHIELDPOWERUP);

        return powerUp;
    }

    /**
     * Creates a spear power up entity.
     *
     * @return spear power up entity.
     */
    public static Entity createSpearPowerUp() {
        Entity powerUp = createBasePowerUp();

        powerUp.addComponent(new PhysicsMovementComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PLAYERSPEAR))
            .addComponent(new TouchAttackComponent(PhysicsLayer.ALL, 1f))
            .addComponent(new CombatStatsComponent(100, 100));

        powerUp.getComponent(TouchAttackComponent.class).setEnabled(false);
        powerUp.getComponent(CombatStatsComponent.class).setEnabled(false);

        powerUp.getComponent(HitboxComponent.class).setAsBox(new Vector2(0.1f, 1f), powerUp.getCenterPosition());

        powerUp.setScale(1f, 1f);
        PhysicsUtils.setScaledCollider(powerUp, 1f, 1f);

        AnimationRenderComponent animator =
            new AnimationRenderComponent(ServiceLocator.getResourceService()
                .getAsset("images/playerspear.atlas", TextureAtlas.class));

        animator.addAnimation("flat-left", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("flat-right", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("fly-left", 0.2f, Animation.PlayMode.LOOP);
        animator.addAnimation("fly-right", 0.2f, Animation.PlayMode.LOOP);
        animator.addAnimation("stand-left", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("stand-right", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("swing-left", 0.09f, Animation.PlayMode.LOOP);
        animator.addAnimation("swing-right", 0.09f, Animation.PlayMode.LOOP);
        animator.addAnimation("static", 1f, Animation.PlayMode.LOOP);

        powerUp.addComponent(animator);

        powerUp.setType(EntityTypes.SPEARPOWERUP);

        return powerUp;
    }

    /**
     * Creates a base power up to be extended by more specific power ups.
     *
     * @return base power up
     */
    public static Entity createBasePowerUp() {
        Entity powerUp =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new ColliderComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);
        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        powerUp.getEvents().addListener("dispose",
                powerUp::flagDelete);

        return powerUp;
    }

    private PowerUpFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
