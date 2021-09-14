package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LightningPowerUpComponentTest {
    @Test
    public void obtainLightningTest(){
        Entity player = new Entity();
        Entity powerUp = new Entity();
        powerUp.setType(EntityTypes.LIGHTNINGPOWERUP);

        player.addComponent(new LightningPowerUpComponent());
        player.getComponent(LightningPowerUpComponent.class).obtainPowerUp(powerUp);

        assertEquals(EntityTypes.LIGHTNINGPOWERUP, player.getComponent(LightningPowerUpComponent.class).getPowerUp().getType());
    }

    @Test
    public void activateLightningTest(){
        Entity player = new Entity();
        Entity powerUp = new Entity();
        powerUp.setType(EntityTypes.LIGHTNINGPOWERUP);

        player.addComponent(new LightningPowerUpComponent());
        player.getComponent(LightningPowerUpComponent.class).obtainPowerUp(powerUp);

        player.getComponent(LightningPowerUpComponent.class).setActive(true);

        assertTrue(player.getComponent(LightningPowerUpComponent.class).getActive());
    }
}
