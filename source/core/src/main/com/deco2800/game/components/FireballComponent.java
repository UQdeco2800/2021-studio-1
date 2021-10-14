package com.deco2800.game.components;

import com.deco2800.game.physics.components.PhysicsComponent;

public class FireballComponent extends Component{

    public void update() {
        if (entity.getComponent(PhysicsComponent.class).getBody().getLinearVelocity().x == 0) {
            entity.flagDelete();
        }
    }
}
