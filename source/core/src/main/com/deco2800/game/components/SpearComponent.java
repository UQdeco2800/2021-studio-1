package com.deco2800.game.components;

public class SpearComponent extends Component{
    boolean flying;
    boolean hit;
    /**
     * Create the spear component - used to check if the spear collides with an enemy
     */
    public void create() {
        hit = false;
        flying = false;
    }

    /**
     * Called when the spear is thrown by the player
     */
    public void startFlying() {
        flying = true;
    }

    /**
     * Returns whether the spear is flying
     *
     * @return true if it is flying, false otherwise
     */
    public boolean isFlying() {
        return flying;
    }

    /**
     * Called when the spear collides with an enemy
     */
    public void hitEnemy() {
        hit = true;
    }

    /**
     * Returns whether the spear has hit an enemy
     *
     * @return true if it has hit an enemy, false otherwise
     */
    public boolean hasHit() {
        return hit;
    }

    /**
     * Resets the spear triggers
     */
    public void resetSpear() {
        flying = false;
        hit = false;
    }
}
