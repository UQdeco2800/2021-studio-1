package com.deco2800.game.components.powerups;

import com.deco2800.game.components.Component;

/**
 * Core power up component class to be implemented by specific power ups.
 * This component, in essence, is a controller given to the player to activate power ups.
 */
public class PowerUpComponent extends Component {

    public boolean getEnabled() {
        return enabled;
    }

    public void activate() {
        // Does nothing by default
    }
}
