package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.npc.GhostAnimationController;
import com.deco2800.game.components.tasks.ObstacleTask;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.configs.BaseEntityConfig;
import com.deco2800.game.entities.configs.NPCConfigs;
import com.deco2800.game.files.FileLoader;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * Factory to create all obstacles that are associated with the GeneratorComponent class.
 *
 * <p>Each obstacle has a method associated with it that returns an entity
 * Of particular note, because this game has no planning, all entities in this class
 * Are returned with an AIComponent w/ the Obstacle Task, a physics component, and a physics movement component
 *
 * <p> Listen to the Police
 */
public class GeneratorFactory {
    private static final String FLOAT_ANIMATION = "float";

    private static final NPCConfigs NPC_CONFIGS =
            FileLoader.readClass(NPCConfigs.class, "configs/NPCs.json");

    public static Entity createRock() {
        Entity rock = createNPC();
        BaseEntityConfig config = NPC_CONFIGS.rock;
        rock
                .addComponent(new TextureRenderComponent("images/Rock_1.png"))
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack));
        rock.setScale(1.1f, 0.6f);
        return rock;
    }

    public static Entity createSpikes() {
        Entity spikes = createNPC();
        BaseEntityConfig config = NPC_CONFIGS.spikes;
        spikes
                .addComponent(new TextureRenderComponent("images/Spike_1.png"))
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack));
        spikes.setScale(1.1f, 0.5f);
        return spikes;
    }

    public static Entity createSkeleton() {
        Entity skeleton = createNPC();
        BaseEntityConfig config = NPC_CONFIGS.skeleton;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService().getAsset("images/skeleton.atlas", TextureAtlas.class));
//    animator.addAnimation("angry_float", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation(FLOAT_ANIMATION, 0.4f, Animation.PlayMode.LOOP);

        skeleton
//            .addComponent(new TextureRenderComponent("images/skeleton.png"))
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new GhostAnimationController());
        skeleton.getComponent(AnimationRenderComponent.class).scaleEntity();
        skeleton.setScale(1f, 1.2f);
        return skeleton;
    }

    public static Entity createWolf() {
        Entity wolf = createNPC();
        BaseEntityConfig config = NPC_CONFIGS.wolf;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService().getAsset("images/ghostKing.atlas", TextureAtlas.class));
        animator.addAnimation(FLOAT_ANIMATION, 0.1f, Animation.PlayMode.LOOP);

        wolf
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new GhostAnimationController());
        wolf.getComponent(AnimationRenderComponent.class).scaleEntity();

        wolf.setScale(0.8f, 0.8f);
        return wolf;
    }

    public static Entity createFireSpirit() {
        Entity fireSpirit = createNPC();
        BaseEntityConfig config = NPC_CONFIGS.fireSpirit;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService().getAsset("images/ghostKing.atlas", TextureAtlas.class));
        animator.addAnimation(FLOAT_ANIMATION, 0.1f, Animation.PlayMode.LOOP);

        fireSpirit
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new GhostAnimationController());
        fireSpirit.getComponent(AnimationRenderComponent.class).scaleEntity();

        fireSpirit.setScale(0.8f, 0.8f);
        return fireSpirit;
    }

    private static Entity createNPC() {
        AITaskComponent aiComponent =
                new AITaskComponent()
                        .addTask(new ObstacleTask());
        Entity npc =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new PhysicsMovementComponent())
                        .addComponent(new ColliderComponent())
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                        .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
                        .addComponent(aiComponent);

        PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
        npc.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

        return npc;
    }

}
