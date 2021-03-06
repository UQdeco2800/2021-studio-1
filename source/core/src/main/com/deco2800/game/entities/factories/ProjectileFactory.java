package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.FireballComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.npc.BifrostAnimationController;
import com.deco2800.game.components.npc.FireballAnimationController;
import com.deco2800.game.components.powerups.SpearComponent;
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

    public static Entity fireBall() {
        Entity fireBall = createBaseProjectile();
        fireBall.addComponent(new FireballComponent());
        fireBall.getComponent(PhysicsMovementComponent.class);
        fireBall.setType(EntityTypes.FIREBALL);
        return fireBall;
    }

    /**
     * Creates the base entity of a projectile
     * @return a base projectile entity
     */
    public static Entity createBaseProjectile() {
        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService().getAsset("images/fireball.atlas", TextureAtlas.class));
        animator.addAnimation("fireball", 0.04f, Animation.PlayMode.LOOP);
        animator.addAnimation("fireball_back", 0.4f, Animation.PlayMode.LOOP);

        Entity baseProjectile = new Entity()
                .addComponent(animator)
                .addComponent(new FireballAnimationController())
                .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent())
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE))
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE))
                .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 7.5f))
                .addComponent(new CombatStatsComponent(1, 20));

        baseProjectile.getComponent(PhysicsComponent.class).setGravityScale(5f);
        baseProjectile.getComponent(AnimationRenderComponent.class).scaleEntity();
        baseProjectile.setScale(0.6f, 0.6f);
        PhysicsUtils.setScaledCollider(baseProjectile, 0.8f, 0.8f);
        baseProjectile.setType(EntityTypes.PROJECTILE);
        return baseProjectile;
    }

    public static Entity createSpearEntity() {
        Entity spear =
                new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent())
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE))
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE))
                .addComponent(new SpearComponent());

        spear.setScale(1.1f, 1.1f);
        spear.getComponent(ColliderComponent.class).setAsBox(new Vector2(1.3f,
                0.1f), spear.getCenterPosition());
        spear.getComponent(PhysicsComponent.class).setGravityScale(3f);

        AnimationRenderComponent animator =
                new AnimationRenderComponent(ServiceLocator.getResourceService()
                        .getAsset("images/player-spear.atlas", TextureAtlas.class));

        animator.addAnimation("flat-left", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("flat-right", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("fly-left", 0.2f, Animation.PlayMode.LOOP);
        animator.addAnimation("fly-right", 0.2f, Animation.PlayMode.LOOP);
        animator.addAnimation("stand-left", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("stand-right", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("swing-left", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("swing-right", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("static", 1f, Animation.PlayMode.LOOP);

        spear.addComponent(animator);
        spear.getComponent(AnimationRenderComponent.class).startAnimation(
                "static");

        spear.setType(EntityTypes.PLAYERSPEAR);

        return spear;
    }
}