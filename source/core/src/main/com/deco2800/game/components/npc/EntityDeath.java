package com.deco2800.game.components.npc;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.Component;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;

/**
 * Class handling the animations for enemy deaths
 */
public class EntityDeath extends Component {
    private boolean triggered;
    private Vector2 position;

    /**
     * Create the death event
     */
    @Override
    public void create() {
        triggered = false;
        entity.getEvents().addListener("death", this::deathTriggered);
    }

    /**
     * updating the and deleting NPC
     */
    @Override
    public void update() {
        if (triggered) {
            if (entity.getComponent(AnimationRenderComponent.class).isFinished()) {
                entity.flagDelete();
            }
        }
    }

    /**
     * Triggering the death event
     */
    private void deathTriggered() {
        triggered = true;
        entity.getComponent(TouchAttackComponent.class).setKnockbackForce(0f);
        entity.getComponent(CombatStatsComponent.class).setBaseAttack(0);
        entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0f);
        entity.getComponent(AnimationRenderComponent.class).stopAnimation();
        entity.setDeath(true);
        entity.getComponent(AnimationRenderComponent.class).startAnimation("death");
        entity.getComponent(PhysicsMovementComponent.class).setMaxSpeed(0);
    }
}
