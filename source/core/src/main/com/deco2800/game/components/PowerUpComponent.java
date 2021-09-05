package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Null;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;

public class PowerUpComponent extends Component{
    private boolean dispose;
    Entity player;

    public void create(Entity player) {
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        dispose = false;
        this.player = player;
    }

    @Override
    public void update() {
        if (dispose) {
            disposeEntity();
        }
    }

    private void onCollisionStart(Fixture other, Fixture powerUp) {
        /*
        if (Check if player is colliding){
            dispose = true;
        }
         */
    }

    private void disposeEntity() {
        entity.dispose();
    }
}
