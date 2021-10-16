package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;

public class FallDamageComponent extends Component {
    Entity target;
    CombatStatsComponent attack;

    

    /**
     * Class to cause the player to die when below a certain height position
     *
     * @param target - the target to take fall damage
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

        attack = new CombatStatsComponent(1, 100);

    }

    /**
     * Handles collision
     *
     * @param me    first collision object
     * @param other second collision object
     */
    private void onCollisionStart(Fixture me, Fixture other) {

        float yPos = target.getPosition().y;


        //if the y_position is less than -1, than set the health of the target to 0.
        if (yPos < -1) {
            CombatStatsComponent targetStats = target.getComponent(CombatStatsComponent.class);
            if (targetStats != null && !targetStats.isInvincible()) {//satisfy SonarCloud
                targetStats.setHealth(0);
            }
        }

    }
}