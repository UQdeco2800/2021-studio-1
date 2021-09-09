package com.deco2800.game.entities.factories;

import com.deco2800.game.components.powerups.SamplePowerUpComponent;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;

import com.deco2800.game.entities.Entity;

import com.deco2800.game.events.listeners.EventListener1;
import com.deco2800.game.physics.PhysicsContactListener;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;

import com.deco2800.game.rendering.TextureRenderComponent;

public class PowerUpFactory {
    public static Entity createPowerUp() {
        Entity powerUp =
                new Entity()
                        .addComponent(new TextureRenderComponent("images/powerup.png"))
                        .addComponent(new PhysicsComponent())
                        .addComponent(new ColliderComponent())
                        .addComponent(new SamplePowerUpComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);
        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        powerUp.setType(EntityTypes.SAMPLEPOWERUP);

        return powerUp;
    }

    // Edit for lightning power up.
    public static Entity createLightningPowerUp() {
        Entity powerUp =
            new Entity()
                .addComponent(new TextureRenderComponent("images/powerup.png"))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new LightningPowerUpComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);
        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

        return powerUp;
    }

    // Edit for shield power up.
    public static Entity createShieldPowerUp() {
        Entity powerUp =
            new Entity()
                .addComponent(new TextureRenderComponent("images/powerup.png"))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new ShieldPowerUpComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);
        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

        return powerUp;
    }

    // Edit for spear power up.
    public static Entity createSpearPowerUp() {
        Entity powerUp =
            new Entity()
                .addComponent(new TextureRenderComponent("images/powerup.png"))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new SpearPowerUpComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);
        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

        return powerUp;
    }

    private PowerUpFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
