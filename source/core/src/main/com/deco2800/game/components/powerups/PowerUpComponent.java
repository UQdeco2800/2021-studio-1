package com.deco2800.game.components.powerups;

import com.deco2800.game.components.Component;

public class PowerUpComponent extends Component {

    @Override
    public void create() {
        enabled = false;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void activate() {
        // Does nothing by default
    }
}
