package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchDisposeComponent;
import com.deco2800.game.components.npc.DeathGiantAnimationController;
import com.deco2800.game.components.npc.GhostAnimationController;
import com.deco2800.game.components.npc.ScreenFXAnimationController;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.tasks.*;
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
 * Factory to create non-playable character (NPC) entities with predefined
 * components.
 *
 * <p>
 * Each NPC entity type should have a creation method that returns a
 * corresponding entity. Predefined entity properties can be loaded from configs
 * stored as json files which are defined in "NPCConfigs".
 *
 * <p>
 * If needed, this factory can be separated into more specific factories for
 * entities with similar characteristics.
 */
public class NPCFactory {
    private static final NPCConfigs configs =
            FileLoader.readClass(NPCConfigs.class, "configs/NPCs.json");

    /**
     * Creates a skeleton entity.
     *
     * @param target entity to chase* @return entity
     * @return skeleton entity
     */
    public static Entity createSkeleton(Entity target) {
        Entity skeleton = createSkeletonNPC(target);
        BaseEntityConfig config = configs.skeleton;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService().getAsset("images/skeleton.atlas", TextureAtlas.class));
        animator.addAnimation("run", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("run_back", 0.1f, Animation.PlayMode.LOOP);

        skeleton
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new GhostAnimationController());
        skeleton.getComponent(AnimationRenderComponent.class).scaleEntity();
        skeleton.setScale(1f, 1.2f);

        // NEED TO CHANGE COLLISION BOXES -> RUN THROUGH ENEMIES
        //set body collision box
        skeleton.getComponent(HitboxComponent.class).setAsBoxAligned(new Vector2(0.6f,
            0.7f), PhysicsComponent.AlignX.RIGHT, PhysicsComponent.AlignY.BOTTOM);
        //create head circle colilision box
        CircleShape head = new CircleShape();
        head.setRadius(0.2f);
        Vector2 circleOffset = new Vector2(skeleton.getCenterPosition().x + 0.15f,
            skeleton.getCenterPosition().y + 0.3f);
        head.setPosition(circleOffset);
        skeleton.getComponent(PhysicsComponent.class).getBody().createFixture(head,1.0f);

        for(Fixture fixture : skeleton.getComponent(PhysicsComponent.class).getBody().getFixtureList()) {
            fixture.setSensor(true);
        }

        skeleton.setType(EntityTypes.SKELETON);

        return skeleton;
    }

    /**
     * Creates a wolf entity.
     *
     * @param target entity to chase
     * @return entity
     */
    public static Entity createWolf(Entity target) {
        Entity wolf = createWolfNPC(target);
        BaseEntityConfig config = configs.wolf;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService().getAsset("images/wolf.atlas", TextureAtlas.class));
        animator.addAnimation("run", 0.1f, Animation.PlayMode.LOOP);
        //animator.addAnimation("run_back", 0.1f, Animation.PlayMode.LOOP);

        wolf
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new GhostAnimationController());
        wolf.getComponent(AnimationRenderComponent.class).scaleEntity();

        wolf.setScale(1.3f, 1f);

        //create body collision box using collider component
        wolf.getComponent(HitboxComponent.class).setAsBoxAligned(new Vector2(1f,
                0.6f), PhysicsComponent.AlignX.CENTER,
                PhysicsComponent.AlignY.BOTTOM);

        //create head circle collision box
        CircleShape head = new CircleShape();
        head.setRadius(0.15f);
        Vector2 circleOffset = new Vector2(wolf.getCenterPosition().x - 0.4f,
                wolf.getCenterPosition().y + 0.2f);
        head.setPosition(circleOffset);
        wolf.getComponent(PhysicsComponent.class).getBody().createFixture(head,1.0f);

        //create neck circle collision box
        CircleShape neck = new CircleShape();
        neck.setRadius(0.15f);
        Vector2 neckOffset = new Vector2(wolf.getCenterPosition().x - 0.2f,
                wolf.getCenterPosition().y + 0.15f);
        neck.setPosition(neckOffset);
        wolf.getComponent(PhysicsComponent.class).getBody().createFixture(neck,1.0f);

        for(Fixture fixture : wolf.getComponent(PhysicsComponent.class).getBody().getFixtureList()) {
            fixture.setSensor(true);
        }

        wolf.getComponent(AnimationRenderComponent.class).startAnimation("run");
        wolf.setType(EntityTypes.WOLF);

        wolf.setScale(1.3f, 1f);

        wolf.getComponent(AnimationRenderComponent.class).startAnimation("run");
        return wolf;
    }

    public static Entity createFireSpirit(Entity target) {
        Entity fireSpirit = createFireSpiritNPC(target);
        BaseEntityConfig config = configs.fireSpirit;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService().getAsset("images/fire_spirit.atlas", TextureAtlas.class));
        animator.addAnimation("run", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("run_back", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("death", 0.1f, Animation.PlayMode.LOOP);

        fireSpirit
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new GhostAnimationController());
        fireSpirit.getComponent(AnimationRenderComponent.class).scaleEntity();

        fireSpirit.setScale(1.3f, 1f);

        //set body collision box
        fireSpirit.getComponent(HitboxComponent.class).setAsBoxAligned(new Vector2(0.6f,
                0.7f), PhysicsComponent.AlignX.CENTER, PhysicsComponent.AlignY.BOTTOM);
        //create head circle collision box
        CircleShape head = new CircleShape();
        head.setRadius(0.2f);
        Vector2 circleOffset = new Vector2(fireSpirit.getCenterPosition().x + 0.05f,
                fireSpirit.getCenterPosition().y + 0.35f);
        head.setPosition(circleOffset);
        fireSpirit.getComponent(PhysicsComponent.class).getBody().createFixture(head,1.0f);

        for(Fixture fixture : fireSpirit.getComponent(PhysicsComponent.class).getBody().getFixtureList()) {
            fixture.setSensor(true);
        }

        fireSpirit.getComponent(AnimationRenderComponent.class).startAnimation("run");
        fireSpirit.setType(EntityTypes.WOLF);

        fireSpirit.setScale(1.3f, 1f);

        fireSpirit.getComponent(AnimationRenderComponent.class).startAnimation("run");

        fireSpirit.setType(EntityTypes.FIRESPIRIT);
        return fireSpirit;
    }

    /**
     * Creates a Wall of Death entity
     *
     * @param target entity to chase
     * @return entity
     */
    public static Entity createWallOfDeath(Entity target) {
        Entity wallOfDeath = createWallNPC(target);
        BaseEntityConfig config = configs.wallOfDeath;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset("images/wall.atlas", TextureAtlas.class));
        animator.addAnimation("walk", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("walkAngry", 0.1f, Animation.PlayMode.LOOP);

        wallOfDeath
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new DeathGiantAnimationController())
                .addComponent(new TouchDisposeComponent());

        wallOfDeath.getComponent(AnimationRenderComponent.class).scaleEntity();
        wallOfDeath.setScale(25f, 12f);

        wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(2);
        wallOfDeath.setType(EntityTypes.WALL);

        return wallOfDeath;
    }

    /**
     * Creates a screen effects entity
     *
     * @param target target for the screen effects
     * @return Screen FX entity
     */
    public static Entity createScreenFX(Entity target) {
        Entity screenFX = createWallNPC(target);
        BaseEntityConfig config = configs.deathGiant;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset("images/sfx.atlas", TextureAtlas.class));
        animator.addAnimation("normal", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("dark", 0.1f, Animation.PlayMode.LOOP);

        screenFX
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new ScreenFXAnimationController());

        screenFX.getComponent(AnimationRenderComponent.class).scaleEntity();
        screenFX.setScale(30f, 12f);
        screenFX.getComponent(PhysicsMovementComponent.class).setMaxSpeed(2);

        return screenFX;
    }

    public static Entity createDeathGiant(Entity target) {
        Entity deathGiant = createWallNPC(target);
        BaseEntityConfig config = configs.deathGiant;

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset("images/deathGiant.atlas", TextureAtlas.class));
        animator.addAnimation("walk", 0.12f, Animation.PlayMode.LOOP);
        animator.addAnimation("walkAngry", 0.12f, Animation.PlayMode.LOOP);

        deathGiant
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(animator)
                .addComponent(new DeathGiantAnimationController());

        deathGiant.getComponent(AnimationRenderComponent.class).scaleEntity();
        deathGiant.setScale(11f, 11f);

        deathGiant.getComponent(ColliderComponent.class).setAsBoxAligned(
                new Vector2(5f, 30f), PhysicsComponent.AlignX.RIGHT,
                PhysicsComponent.AlignY.BOTTOM);

        deathGiant.getComponent(PhysicsMovementComponent.class).setMaxSpeed(2);
        deathGiant.setType(EntityTypes.GIANT);
        return deathGiant;
    }

    /**
     * Creates a skeleton NPC to be used as a base entity
     *
     * @return entity
     */
    private static Entity createSkeletonNPC(Entity target) {
        AITaskComponent aiComponent =
                new AITaskComponent()
                        .addTask(new WanderTask(new Vector2(50f, 0f), 0f));
        Entity npc =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new PhysicsMovementComponent())
                        .addComponent(new ColliderComponent())
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                        .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
                        .addComponent(aiComponent);
        PhysicsUtils.setScaledCollider(npc, 0f, 0f);
        npc.getComponent(HitboxComponent.class).setAsCircleAligned(0.2f,
                PhysicsComponent.AlignX.CENTER, PhysicsComponent.AlignY.CENTER);
        npc.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        npc.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.ENEMY);
        return npc;
    }

    /**
     * Creates a wolf NPC to be used as a base entity
     *
     * @return entity
     */
    private static Entity createWolfNPC(Entity target) {
        AITaskComponent aiComponent =
                new AITaskComponent()
                        .addTask(new ChaseTask(target, 2, true, 100, 10));
        Entity npc =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new PhysicsMovementComponent())
                        .addComponent(new ColliderComponent())
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                        .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
                        .addComponent(aiComponent);
        PhysicsUtils.setScaledCollider(npc, 0f, 0f);
        npc.getComponent(HitboxComponent.class).setAsCircleAligned(0.2f,
                PhysicsComponent.AlignX.CENTER, PhysicsComponent.AlignY.CENTER);
        npc.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        npc.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.ENEMY);
        npc.getComponent(PhysicsMovementComponent.class).setMaxSpeed(8);
        return npc;
    }

    /**
     * Creates a generic NPC to be used as a base entity by more specific NPC creation methods.
     *
     * @return entity
     */
    private static Entity createFireSpiritNPC(Entity target) {
        AITaskComponent aiComponent =
                new AITaskComponent()
                        .addTask(new ShootTask(target, 5f))
                        .addTask(new WanderTask(new Vector2(0f, 0f), 0f));

        Entity npc =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new PhysicsMovementComponent())
                        .addComponent(new ColliderComponent())
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                        .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
                        .addComponent(aiComponent);

        PhysicsUtils.setScaledCollider(npc, 0f, 0f);
        npc.getComponent(HitboxComponent.class).setAsCircleAligned(0.2f,
                PhysicsComponent.AlignX.CENTER, PhysicsComponent.AlignY.CENTER);
        npc.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
        npc.getComponent(PhysicsComponent.class).getBody().setUserData(EntityTypes.ENEMY);
        npc.getComponent(PhysicsMovementComponent.class).setMaxSpeed(4);
        return npc;
    }

    private static Entity createWallNPC(Entity target) {
        AITaskComponent aiComponent =
                new AITaskComponent()
                        //task to continuously move to the right
                        .addTask(new MoveRightTask(target));
        Entity npc =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new PhysicsMovementComponent())
                        .addComponent(new ColliderComponent())
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                        .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 0))

                        .addComponent(aiComponent);

        //set the NPC as a sensor so other object will not collide
        npc.getComponent(ColliderComponent.class).setSensor(true);
        npc.setType(EntityTypes.ENEMY);
        return npc;
    }

    private NPCFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}

