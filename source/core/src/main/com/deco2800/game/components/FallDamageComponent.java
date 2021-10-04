package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;

public class FallDamageComponent extends Component {
    Entity target;


    /**
     * Class to cause the player to die when below a certain height position
     */
    public FallDamageComponent(Entity target) {
        this.target = target;
    }

    /**
     * Create event listener for collision
     */
    @Override
    public void create() {
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        target.getEvents().addListener("collisionStart", this::onCollisionStart);

    }

    /**
     * Handles collision
     *
     * @param me    first collision object
     * @param other second collision object
     */
    private void onCollisionStart(Fixture me, Fixture other) {

        float yPos = target.getPosition().y;
        // System.out.println(yPos);


        //if the y_position is less than -1, than set the health of the target to 0.
        if (yPos < -1) {
            CombatStatsComponent targetStats = target.getComponent(CombatStatsComponent.class);
            targetStats.setHealth(0);
        }

    }
}