package com.deco2800.game.components.powerups;

import com.deco2800.game.entities.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShieldPowerUpComponentTest {

    @Test
    void shouldHaveAllBlocks() {
        Entity entity = entity();
        entity.getComponent(ShieldPowerUpComponent.class).pickedUpShield();
        assertEquals(3, entity.getComponent(ShieldPowerUpComponent.class).getBlocks());
    }

    @Test
    void shouldBlock() {
        Entity entity = entity();
        entity.getComponent(ShieldPowerUpComponent.class).pickedUpShield();
        entity.getComponent(ShieldPowerUpComponent.class).activate();
        assertEquals(2, entity.getComponent(ShieldPowerUpComponent.class).getBlocks());
    }

    @Test
    void shouldNotBlock() {
        Entity entity = entity();
        entity.getComponent(ShieldPowerUpComponent.class).activate();
        assertEquals(0, entity.getComponent(ShieldPowerUpComponent.class).getBlocks());
    }

    Entity entity() {
        Entity entity = new Entity().addComponent(new ShieldPowerUpComponent());
        return entity;
    }
}