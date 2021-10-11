package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


class SpearPowerUpComponentTest {

    @Test
    public void obtainsSpearTest(){
        Entity player = new Entity();

        player.addComponent(new SpearPowerUpComponent());
        player.getComponent(SpearPowerUpComponent.class).setEnabled(true);

        assertTrue(player.getComponent(SpearPowerUpComponent.class).getEnabled());
    }
}
