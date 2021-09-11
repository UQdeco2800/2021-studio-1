package com.deco2800.game.entities.factories;

import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;

import com.deco2800.game.entities.Entity;

import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;

import com.deco2800.game.rendering.TextureRenderComponent;

public class PowerUpFactory {
    // Edit for lightning power up.
    public static Entity createLightningPowerUp() {
        Entity powerUp =
            new Entity()
                .addComponent(new TextureRenderComponent("images/powerup-lightning.png"))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new LightningPowerUpComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);

        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        powerUp.setType(EntityTypes.LIGHTNINGPOWERUP);
        powerUp.getEvents().addListener("dispose",
            powerUp::flagDelete);
        powerUp.getComponent(LightningPowerUpComponent.class).setEnabled(true);

        return powerUp;
    }

    // Edit for shield power up.
    public static Entity createShieldPowerUp() {
        Entity powerUp =
            new Entity()
                .addComponent(new TextureRenderComponent("images/powerup-shield.png"))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new ShieldPowerUpComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);

        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        powerUp.setType(EntityTypes.SHIELDPOWERUP);
        powerUp.getEvents().addListener("dispose",
            powerUp::flagDelete);
        powerUp.getComponent(ShieldPowerUpComponent.class).setEnabled(true);

        return powerUp;
    }

    // Edit for spear power up.
    public static Entity createSpearPowerUp() {
        Entity powerUp =
            new Entity()
                .addComponent(new TextureRenderComponent("images/powerup-spear.png"))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new SpearPowerUpComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);

        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        powerUp.setType(EntityTypes.SPEARPOWERUP);
        powerUp.getEvents().addListener("dispose",
            powerUp::flagDelete);
        powerUp.getComponent(SpearPowerUpComponent.class).setEnabled(true);

        return powerUp;
    }

    private PowerUpFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
