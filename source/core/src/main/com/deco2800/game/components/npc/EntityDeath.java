package com.deco2800.game.components.npc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.Component;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.tasks.MovementTask;
import com.deco2800.game.components.tasks.WanderTask;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;

public class EntityDeath extends Component {
    private boolean triggered;
    private Vector2 position;

    @Override
    public void create() {
        triggered = false;
        entity.getEvents().addListener("death", this::deathTriggered);
    }
    @Override
    public void update() {
        if (triggered) {
            if (entity.getComponent(AnimationRenderComponent.class).isFinished()) {
                entity.flagDelete();
            }
        }
    }

    private void deathTriggered() {
        triggered = true;
        entity.getComponent(TouchAttackComponent.class).setKnockbackForce(0f);
        entity.getComponent(CombatStatsComponent.class).setBaseAttack(0);
        entity.getComponent(AnimationRenderComponent.class).stopAnimation();
        entity.getComponent(AnimationRenderComponent.class).startAnimation("death");
    }
}
