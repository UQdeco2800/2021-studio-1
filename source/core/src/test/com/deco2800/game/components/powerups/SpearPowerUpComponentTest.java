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
        player.getComponent(SpearPowerUpComponent.class).obtainSpear();

        assertEquals(0,
                player.getComponent(SpearPowerUpComponent.class).getThrown());
        assertTrue(player.getComponent(SpearPowerUpComponent.class).getEnabled());
    }
    @Test
    public void activateSpearTest(){
        Entity player = new Entity();
        Entity powerUp = new Entity();
        powerUp.setType(EntityTypes.SPEARPOWERUP);

        player.addComponent(new SpearPowerUpComponent());
        player.getComponent(SpearPowerUpComponent.class).obtainSpear();
        player.getComponent(SpearPowerUpComponent.class).setActive(true);

        assertTrue(player.getComponent(SpearPowerUpComponent.class).getActive());
    }

}
