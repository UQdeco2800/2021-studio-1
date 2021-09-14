package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * Controls the behaviour for lightning power up.
 * Lightning may only be activated once per pickup.
 */
public class LightningPowerUpComponent extends PowerUpComponent {
    boolean active;
    Entity powerUp;

    public void obtainPowerUp(Entity lightning) {
        powerUp = lightning;
    }

    @Override
    public void create() {
        enabled = false;
        active = false;
    }

    /**
     * Checks if the lightning power up is active and starts the animation.
     * The power up is then disposed after use.
     */
    @Override
    public void update() {
        if (active) {
            if (powerUp.getComponent(AnimationRenderComponent.class).getCurrentAnimation().equals("float")) {
                if (powerUp.getComponent(AnimationRenderComponent.class).isFinished()) {
                    active = false;
                    enabled = false;
                    powerUp.flagDelete();
                }
            }
        }
    }

    @Override
    public void activate() {
        active = true;

        powerUp.setPosition(entity.getCenterPosition().sub(9f,4f).x, 0f);
        powerUp.setScale(20f, 15f);
        powerUp.getComponent(AnimationRenderComponent.class).stopAnimation();
        powerUp.getComponent(AnimationRenderComponent.class).startAnimation("float");

        // If the enemy is a wolf or skeleton within 8 metres, dispose
        for (Entity enemy : ServiceLocator.getEntityService().getEntityArray()) {
            if (enemy.getType() == EntityTypes.WOLF ||enemy.getType() == EntityTypes.SKELETON) {
                if (enemy.getCenterPosition().x - entity.getCenterPosition().x <= 8f) {
                    enemy.flagDelete();
                }
            }
        }
    }
}
