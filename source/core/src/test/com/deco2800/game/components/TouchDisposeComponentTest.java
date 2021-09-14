package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

@ExtendWith(GameExtension.class)
class TouchDisposeComponentTest {
    private EntityService entityService;

    @BeforeEach
    void beforeEach() {
        ServiceLocator.registerPhysicsService(new PhysicsService());
        entityService = mock(EntityService.class);
        ServiceLocator.registerEntityService(entityService);
    }

    @Test
    void shouldDisposeOBSTACLE() {
        short targetLayer = PhysicsLayer.OBSTACLE;
        Entity entity = createDisposer();
        Entity target = createTarget(targetLayer);
        entity.setPosition(10f, 0f);
        entityService.register(entity);
        entityService.register(target);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionEnd", entityFixture, targetFixture);

        verify(entityService).disposeAfterStep(target);
    }

    @Test
    void shouldDisposeNPC() {
        short targetLayer = PhysicsLayer.NPC;
        Entity entity = createDisposer();
        Entity target = createTarget(targetLayer);
        entity.setPosition(10f, 0f);
        entityService.register(entity);
        entityService.register(target);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionEnd", entityFixture, targetFixture);

        verify(entityService).disposeAfterStep(target);
    }

    @Test
    void shouldNotDisposeNPCToRight() {
        short targetLayer = PhysicsLayer.NPC;
        Entity entity = createDisposer();
        Entity target = createTarget(targetLayer);
        target.setPosition(10f, 0f);
        entityService.register(entity);
        entityService.register(target);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionEnd", entityFixture, targetFixture);

        verify(entityService, never()).disposeAfterStep(target);
    }

    @Test
    void shouldNotDisposePLAYER() {
        short targetLayer = PhysicsLayer.PLAYER;
        Entity entity = createDisposer();
        Entity target = createTarget(targetLayer);
        entityService.register(entity);
        entityService.register(target);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, targetFixture);

        verify(entityService, never()).disposeAfterStep(target);
    }

    @Test
    void shouldNotDisposeNONE() {
        short targetLayer = PhysicsLayer.NONE;
        Entity entity = createDisposer();
        Entity target = createTarget(targetLayer);
        entityService.register(entity);
        entityService.register(target);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, targetFixture);

        verify(entityService, never()).disposeAfterStep(target);
    }

    Entity createDisposer() {
        Entity entity =
                new Entity()
                        .addComponent(new TouchDisposeComponent())
                        .addComponent(new PhysicsComponent())
                        .addComponent(new HitboxComponent());
        entity.create();
        return entity;
    }

    Entity createTarget(short layer) {
        Entity target =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new HitboxComponent().setLayer(layer));
        target.create();
        return target;
    }
}
