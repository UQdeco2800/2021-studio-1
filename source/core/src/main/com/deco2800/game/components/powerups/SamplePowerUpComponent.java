package com.deco2800.game.components.powerups;

import com.badlogic.gdx.physics.box2d.*;
import com.deco2800.game.components.Component;
import com.deco2800.game.physics.PhysicsContactListener;
import com.deco2800.game.physics.components.PhysicsComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

public class SamplePowerUpComponent extends Component {

    private boolean colliding;

    public void setColliding() {
        colliding = true;
    }

    @Override
    public void create() {
        entity.getEvents().addListener("startCollision", this::setColliding);
        colliding = false;
    }

    @Override
    public void update() {
        if (colliding) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        entity.dispose();
    }

    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();
        if (entity == null) {
            return className;
        }
        return entity + "." + className;
    }
}
