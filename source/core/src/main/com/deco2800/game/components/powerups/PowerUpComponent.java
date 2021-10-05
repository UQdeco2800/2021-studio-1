package com.deco2800.game.components.powerups;

import com.deco2800.game.components.Component;

/**
 * Core power up component class to be implemented by specific power ups.
 * This component, in essence, is a controller given to the player to activate power ups.
 */
public abstract class PowerUpComponent extends Component {

    /**
     * Returns whether or not power up is enabled
     *
     * @return true if power up enabled
     */
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * Activates power up, is different depending on power up
     */
    public void activate() {
        // Does nothing by default
    }
}
