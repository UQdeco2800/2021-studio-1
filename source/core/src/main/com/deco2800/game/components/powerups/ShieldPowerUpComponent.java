package com.deco2800.game.components.powerups;

public class ShieldPowerUpComponent extends PowerUpComponent {
    private int blocks;

    /**
     * Create the shield component - creates a listener that activates when
     * the player blocks an attack
     */
    public void create() {
        setEnabled(false);
        blocks = 0;
        entity.getEvents().addListener("block", this::activate);
        entity.getEvents().addListener("pickUpShield", this::pickedUpShield);
    }


    /**
     * Activated when the player picks up a shield - gives the player 3
     * blocks and sets the active status to true
     */
    public void pickedUpShield() {
        blocks = 3;
        enabled = true;
        entity.getEvents().trigger("updatePowerUps");
    }

    /**
     * Called when the player blocks the attack - reduces the amount of
     * blocks remaining by 1 and changes the active status if the player is
     * out of blocks
     */
    public void activate() {
        if (blocks > 0) {
            blocks -= 1;
            if (blocks == 0) {
                enabled = false;
            }
        }
        entity.getEvents().trigger("updatePowerUps");
    }

    /**
     * Returns the number of blocks remaining on the shield
     *
     * @return the number of blocks
     */
    public int getBlocks() {
        return blocks;
    }

    /**
     * Returns the active status of the shield
     *
     * @return true if the shield is active (more than 0 blocks), false
     * otherwise
     */
    public boolean getActive() {
        return enabled;
    }
}
