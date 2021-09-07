package com.deco2800.game.components.powerups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.deco2800.game.components.Component;
import com.deco2800.game.physics.PhysicsContactListener;
import com.deco2800.game.physics.components.PhysicsComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

public class SamplePowerUpComponent extends Component {

    boolean colliding;

    public void colliding() {
        colliding = true;
    }

    @Override
    public void create() {
        entity.getEvents().addListener("disposePowerUp", this::colliding);
        colliding = false;
    }

    @Override
    public void update() {
        if (colliding) {
            entity.flagDelete();
        }

        colliding = false;
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
