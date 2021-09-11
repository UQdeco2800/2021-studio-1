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
        Entity powerUp = createBasePowerUp();
        powerUp.addComponent(new TextureRenderComponent(
                "images/powerup-spear" + ".png"));

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
