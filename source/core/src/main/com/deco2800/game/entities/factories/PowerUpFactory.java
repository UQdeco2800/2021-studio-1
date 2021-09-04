package com.deco2800.game.entities.factories;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.tasks.WanderTask;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.TextureRenderComponent;

public class PowerUpFactory {
    public static Entity createBasePowerUp() {
        Entity powerUp =
                new Entity()
                        .addComponent(new TextureRenderComponent("images/powerup.png"))
                        .addComponent(new PhysicsComponent())
                        .addComponent(new ColliderComponent());

        PhysicsUtils.setScaledCollider(powerUp, 0.5f, 0.4f);
        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

        return powerUp;
    }

    private PowerUpFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
