package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.factories.EntityTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpearPowerUpComponentTest {
    @Test
    public void obtainSpearTest(){
        Entity player = new Entity();
        Entity powerUp = new Entity();
        powerUp.setType(EntityTypes.SPEARPOWERUP);

        player.addComponent(new SpearPowerUpComponent());
        player.getComponent(SpearPowerUpComponent.class).obtainSpear(powerUp);

        assertEquals(EntityTypes.SPEARPOWERUP, player.getComponent(SpearPowerUpComponent.class).getSpear().getType());
    }
    @Test
    public void activateSpearTest(){
        Entity player = new Entity();
        Entity powerUp = new Entity();
        powerUp.setType(EntityTypes.SPEARPOWERUP);

        player.addComponent(new SpearPowerUpComponent());
        player.getComponent(SpearPowerUpComponent.class).obtainSpear(powerUp);
        player.getComponent(SpearPowerUpComponent.class).activate();

        assertTrue(player.getComponent(SpearPowerUpComponent.class).getActive());
    }

}
