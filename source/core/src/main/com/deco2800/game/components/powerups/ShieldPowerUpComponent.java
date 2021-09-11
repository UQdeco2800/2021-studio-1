package com.deco2800.game.components.powerups;

public class ShieldPowerUpComponent extends PowerUpComponent {
    private int blocks;
    private boolean active;

    /**
     * Create the shield component - creates a listener that activates when
     * the player blocks an attack
     */
    public void create() {
        blocks = 0;
        active = false;
        entity.getEvents().addListener("block", this::activate);
    }


    public void update() {
        if (enabled) {
            pickedUpShield();
        }
    }

    /**
     * Activated when the player picks up a shield - gives the player 3
     * blocks and sets the active status to true
     */
    public void pickedUpShield() {
        blocks = 3;
        enabled = false;
        active = true;
    }

    /**
     * Called when the player blocks the attack - reduces the amount of
     * blocks remaining by 1 and changes the active status if the player is
     * out of blocks
     */
    public void activate() {
        blocks -= 1;
        if (blocks == 0) {
            active = false;
        }
    }

    /**
     * Returns the active status of the shield
     *
     * @return true if the shield is active (more than 0 blocks), false
     * otherwise
     */
    public boolean getActive() {
        return active;
    }
}
