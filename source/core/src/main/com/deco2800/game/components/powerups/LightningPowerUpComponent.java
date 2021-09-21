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

    /**
     * Creates the lightning component
     */
    @Override
    public void create() {
        setEnabled(false);
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

    /**
     * Called when the player activates the lightning power up by pressing
     * the 'L' key, creates a new lightning power up that plays the lightning
     * animation and kills all enemies (except for the wall of death) that
     * are on the players screen
     */
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

    /**
     * Returns the lightning power up created when activated
     *
     * @return the lightning power up
     */
    public Entity getPowerUp(){
        return powerUp;
    }

    /**
     * Sets the active status of the lightning power up
     *
     * @param active - status the lightning power should be set to
     */
    public void setActive(boolean active){
        this.active = active;
    }

    /**
     * Returns the active status of the lightning power up
     *
     * @return true if the power up is active, false otherwise
     */
    public boolean getActive(){
        return this.active;
    }
}
