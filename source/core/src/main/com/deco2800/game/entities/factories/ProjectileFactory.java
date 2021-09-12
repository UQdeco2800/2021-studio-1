package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.services.ServiceLocator;

public class ProjectileFactory {
    /**
     * Creates a spear that moves across the screen from right to left finishing at the given
     * height.
     *
     * @param height The height the spear should travel along
     * @return spear entity
     */
    public static Entity createSpearAtHeight(float height) {
        Entity spear = createBaseProjectile();
        spear.getComponent(PhysicsMovementComponent.class).setTarget(new Vector2(0, height));
        return spear;
    }

    public static Entity createPlayerSpear(Vector2 location) {

        Entity playerSpear = new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new PhysicsMovementComponent())
            .addComponent(new ColliderComponent().setLayer(PhysicsLayer.PLAYERSPEAR))
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PLAYERSPEAR))
            .addComponent(new TouchAttackComponent(PhysicsLayer.ALL, 0f))
            .addComponent(new CombatStatsComponent(1, 100));

        playerSpear.setScale(1f, 0.5f);
        PhysicsUtils.setScaledCollider(playerSpear, 1f, 1f);

        AnimationRenderComponent animator =
            new AnimationRenderComponent(ServiceLocator.getResourceService()
                .getAsset("images/playerspear.atlas", TextureAtlas.class));

        animator.addAnimation("fly", 0.2f, Animation.PlayMode.LOOP);

        playerSpear.setType(EntityTypes.PROJECTILE);

        return playerSpear;
    }

    /**
     * Creates the base entity of a projectile
     * @return a base projectile entity
     */
    public static Entity createBaseProjectile() {

        Entity baseProjectile = new Entity()
                .addComponent(new TextureRenderComponent("images/Spear_1.png"))
                .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent())
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE))
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE))
                .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 0f))
                .addComponent(new CombatStatsComponent(1, 100));

        baseProjectile.getComponent(TextureRenderComponent.class).scaleEntity();
        baseProjectile.setScale(1f, 0.5f);
        PhysicsUtils.setScaledCollider(baseProjectile, 1f, 1f);
        baseProjectile.setType(EntityTypes.PROJECTILE);
        baseProjectile.getEvents().addListener("dispose",
                baseProjectile::flagDelete);
        return baseProjectile;
    }
}