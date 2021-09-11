package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.services.ServiceLocator;

public class LightningPowerUpComponent extends PowerUpComponent {

    @Override
    public void activate() {
        for (Entity enemy : ServiceLocator.getEntityService().getEntityArray()) {
            if (enemy.getType() == EntityTypes.WOLF ||enemy.getType() == EntityTypes.SKELETON) {
                if (enemy.getCenterPosition().x - entity.getCenterPosition().x <= 10f) {
                    enemy.flagDelete();
                }
            }
        }
    }
}
