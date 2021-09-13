package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.services.ServiceLocator;

/**
 * Controls the behaviour for lightning power up.
 * Lightning may only be activated once per pickup.
 */
public class LightningPowerUpComponent extends PowerUpComponent {

    @Override
    public void activate() {
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
