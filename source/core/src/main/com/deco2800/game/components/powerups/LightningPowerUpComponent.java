package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.entities.factories.PowerUpFactory;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * Controls the behaviour for lightning power up.
 * Lightning may only be activated once per pickup.
 */
public class LightningPowerUpComponent extends PowerUpComponent {
    boolean active;
    Entity powerUp;

    public Entity getPowerUp(){
        return powerUp;
    }

    public boolean getActive(){
        return this.active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public void obtainPowerUp() {
        enabled = true;
    }

    @Override
    public void create() {
        enabled = false;
        active = false;
        powerUp = null;
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
                    powerUp.getComponent(AnimationRenderComponent.class).stopAnimation();
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
        powerUp = PowerUpFactory.createLightningPowerUp();
        ServiceLocator.getEntityService().register(powerUp);
        ServiceLocator.getRenderService().register(
                powerUp.getComponent(AnimationRenderComponent.class));
        powerUp.setPosition(entity.getCenterPosition().sub(9f,4f).x, 0f);
        powerUp.getComponent(ColliderComponent.class).setSensor(true);
        powerUp.setScale(20f, 15f);
        powerUp.getComponent(AnimationRenderComponent.class).startAnimation("float");

        // If the enemy is a wolf or skeleton within 8 metres, dispose
        for (Entity enemy : ServiceLocator.getEntityService().getEntityArray()) {
            if (enemy.getType() == EntityTypes.WOLF
                    ||enemy.getType() == EntityTypes.SKELETON
                    || enemy.getType() == EntityTypes.FIRESPIRIT) {
                if (enemy.getCenterPosition().x - entity.getCenterPosition().x <= 15f) {
                    enemy.flagDelete();
                }
            }
        }
    }
}
