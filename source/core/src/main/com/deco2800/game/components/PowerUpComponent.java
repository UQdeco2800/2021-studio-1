package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;

public class PowerUpComponent extends Component{
    private boolean dispose;

    public void create() {
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        dispose = false;
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
