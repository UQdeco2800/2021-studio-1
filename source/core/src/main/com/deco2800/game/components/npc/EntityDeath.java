package com.deco2800.game.components.npc;

import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.Component;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.tasks.ShootTask;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * Class handling the animations for enemy deaths
 */
public class EntityDeath extends Component {
    private boolean triggered;

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
        if (!triggered) {
            switch (entity.getType()) {
                case FIRESPIRIT:
                    ServiceLocator.getSoundService().playSound("spirit_death");
                break;
                case SKELETON:
                    ServiceLocator.getSoundService().playSound("skeleton_death");
                    break;
                case WOLF:
                    ServiceLocator.getSoundService().playSound("wolf_death");
                    break;
            }
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
}
