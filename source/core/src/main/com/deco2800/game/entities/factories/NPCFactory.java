package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.deco2800.game.ai.tasks.AITaskComponent;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.npc.GhostAnimationController;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.tasks.ChaseTask;
import com.deco2800.game.components.tasks.WanderTask;
import com.deco2800.game.components.tasks.AttackTask;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.configs.BaseEntityConfig;
import com.deco2800.game.entities.configs.GhostKingConfig;
import com.deco2800.game.entities.configs.NPCConfigs;
import com.deco2800.game.files.FileLoader;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.physics.components.PhysicsMovementComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.rendering.TextureRenderComponent;

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

  public static Entity createSkeleton(Entity target) {
    Entity skeleton = createBaseNPC(target);
    BaseEntityConfig config = configs.skeleton;
    skeleton
            .addComponent(new TextureRenderComponent("images/skeleton.png"))
            .addComponent(new CombatStatsComponent(config.health, config.baseAttack));
    skeleton.setScale(0.8f, 1f);
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
    wolf
            .addComponent(new TextureRenderComponent("images/wolf_1.png"))
            .addComponent(new CombatStatsComponent(config.health, config.baseAttack));
    wolf.setScale(1.5f, 1f);
    return wolf;
  }

  /**
   * Creates a generic NPC to be used as a base entity by more specific NPC creation methods.
   *
   * @return entity
   */
  private static Entity createBaseNPC(Entity target) {
    AITaskComponent aiComponent =
        new AITaskComponent()
            .addTask(new WanderTask(new Vector2(10f, 0f), 5f));
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

    PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
    npc.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

    return npc;
  }

  private NPCFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
