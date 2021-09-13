package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
     * Create a lightning power up
     *
     * @return lightning power up
     */
    public static Entity createLightningPowerUp() {
        Entity powerUp = createBasePowerUp();
        powerUp.addComponent(new TextureRenderComponent(
                        "images/powerup-lightning.png"));

        powerUp.setType(EntityTypes.LIGHTNINGPOWERUP);

        return powerUp;
    }

    /**
     * Create a shield power up
     *
     * @return shield power up
     */
    public static Entity createShieldPowerUp() {
        Entity powerUp = createBasePowerUp();
        powerUp.addComponent(new TextureRenderComponent(
                "images/powerup-shield.png"));

        powerUp.setType(EntityTypes.SHIELDPOWERUP);

        return powerUp;
    }

    /**
     * Creates a spear power up
     *
     * @return spear power up
     */
    public static Entity createSpearPowerUp() {
        Entity powerUp = ProjectileFactory.createSpearEntity();

        powerUp.getComponent(TouchAttackComponent.class).setEnabled(false);
        powerUp.getComponent(CombatStatsComponent.class).setEnabled(false);

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
     * Creates a base power up that other power ups can be built off of
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
