package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchDisposeComponent;
import com.deco2800.game.components.npc.DeathGiantAnimationController;
import com.deco2800.game.components.npc.GhostAnimationController;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.tasks.MoveRightTask;
import com.deco2800.game.components.tasks.WanderTask;
import com.deco2800.game.components.tasks.MoveLeftTask;
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
 * Factory to create non-playable character (NPC) entities with predefined components.
 *
 * <p>Each NPC entity type should have a creation method that returns a corresponding entity.
 * Predefined entity properties can be loaded from configs stored as json files which are defined in
 * "NPCConfigs".
 *
 * <p>If needed, this factory can be separated into more specific factories for entities with
 * similar characteristics.
 */
public class NPCFactory {
  private static final NPCConfigs configs =
      FileLoader.readClass(NPCConfigs.class, "configs/NPCs.json");

  /**
   * Creates a skeleton entity.
   *
   * @param target entity to chase
   * @return entity
   */
  public static Entity createSkeleton(Entity target) {
    Entity skeleton = createBaseNPC(target);
    BaseEntityConfig config = configs.skeleton;

    AnimationRenderComponent animator =
            new AnimationRenderComponent(
                    ServiceLocator.getResourceService().getAsset("images/skeleton.atlas", TextureAtlas.class));
//    animator.addAnimation("angry_float", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("float", 0.4f, Animation.PlayMode.LOOP);

    skeleton
//            .addComponent(new TextureRenderComponent("images/skeleton.png"))
            .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
            .addComponent(animator)
            .addComponent(new GhostAnimationController());
    skeleton.getComponent(AnimationRenderComponent.class).scaleEntity();
    skeleton.setScale(1f, 1.2f);
    return skeleton;
  }

  /**
   * Creates a wolf entity.
   *
   * @param target entity to chase
   * @return entity
   */

  public static Entity createWolf(Entity target) {
    Entity wolf = createBaseNPC(target);
    BaseEntityConfig config = configs.wolf;

    AnimationRenderComponent animator =
            new AnimationRenderComponent(
                    ServiceLocator.getResourceService().getAsset("images/ghostKing.atlas", TextureAtlas.class));
//    animator.addAnimation("angry_float", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("float", 0.1f, Animation.PlayMode.LOOP);

    wolf
//            .addComponent(new TextureRenderComponent("images/skeleton.png"))
            .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
            .addComponent(animator)
            .addComponent(new GhostAnimationController());
    wolf.getComponent(AnimationRenderComponent.class).scaleEntity();

    wolf.setScale(0.8f, 0.8f);
    return wolf;
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

    wallOfDeath
            .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
            .addComponent(animator)
            .addComponent(new DeathGiantAnimationController())
            .addComponent(new TouchDisposeComponent());

    wallOfDeath.getComponent(AnimationRenderComponent.class).scaleEntity();
    wallOfDeath.setScale(10.5f,10.5f);

    wallOfDeath.getComponent(PhysicsMovementComponent.class).setMaxSpeed(2);

    return wallOfDeath;
  }

  /**
   * Creates a generic NPC to be used as a base entity by more specific NPC creation methods.
   * @return entity
   */
  private static Entity createBaseNPC(Entity target) {
    AITaskComponent aiComponent =
        new AITaskComponent()
            .addTask(new WanderTask(new Vector2(10f, 0f), 5f));
            //.addTask(new MoveLeftTask());
            //.addTask(new AttackTask(new Vector2(10f, 0f), 5f));
            //.addTask(new ChaseTask(target, 10, 3f, 4f));
    Entity npc =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new PhysicsMovementComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
            .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
            .addComponent(aiComponent);
    PhysicsUtils.setScaledCollider(npc, 0.5f, 0.7f);
    npc.getComponent(PhysicsComponent.class).setGravityScale(5.0f);
    npc.setType(EntityTypes.ENEMY);
    return npc;
  }

  private static Entity createWallNPC(Entity target) {
    AITaskComponent aiComponent =
            new AITaskComponent()
                    //task to continuously move to the right
                    .addTask(new MoveRightTask());
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
