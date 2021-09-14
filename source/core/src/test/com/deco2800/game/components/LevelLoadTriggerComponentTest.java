package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.areas.AreaService;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LevelLoadTriggerComponentTest {
    AreaService areaService;

    @BeforeEach
    void beforeEach() {
        ServiceLocator.registerPhysicsService(new PhysicsService());
        ServiceLocator.registerEntityService(new EntityService());
        areaService = mock(AreaService.class);
        ServiceLocator.registerAreaService(areaService);
    }

    @Test
    void shouldLoadLevel() {
        Entity entity = createLevelLoad();
        Entity player = createPlayer();
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = player.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        verify(areaService).load(anyString());
    }

    @Test
    void shouldNotLoadLevelOBSTACLE() {
        Entity entity = createLevelLoad();
        Entity other = createEntity(PhysicsLayer.OBSTACLE);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = other.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        verify(areaService, never()).load(anyString());
    }

    @Test
    void shouldNotLoadLevelNPC() {
        Entity entity = createLevelLoad();
        Entity other = createEntity(PhysicsLayer.NPC);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = other.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        verify(areaService, never()).load(anyString());
    }

    @Test
    void shouldNotLoadLevelSPEAR() {
        Entity entity = createLevelLoad();
        Entity other = createEntity(PhysicsLayer.PLAYERSPEAR);
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = other.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        verify(areaService, never()).load(anyString());
    }

    @Test
    void shouldOnlyLoadOnCollisionStart() {
        Entity entity = createLevelLoad();
        Entity player = createPlayer();
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = player.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionEnd", entityFixture, playerFixture);

        verify(areaService, never()).load(anyString());
    }

    @Test
    void shouldLoadLevelTwice() {
        Entity entity = createLevelLoad();
        Entity player = createPlayer();
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = player.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        entity = createLevelLoad();
        entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        verify(areaService, times(2)).load(anyString());
    }

    @Test
    void shouldLoadLevelFiveTimes() {
        Entity entity = createLevelLoad();
        Entity player = createPlayer();
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = player.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        for (int i = 1; i < 5; i++) {
            entity = createLevelLoad();
            entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
            entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);
        }

        verify(areaService, times(5)).load(anyString());
    }

    @Test
    void shouldDisposeAfterUse() {
        EntityService entityService = mock(EntityService.class);
        ServiceLocator.registerEntityService(entityService);

        Entity entity = createLevelLoad();
        Entity player = createPlayer();
        Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
        Fixture playerFixture = player.getComponent(HitboxComponent.class).getFixture();
        entity.getEvents().trigger("collisionStart", entityFixture, playerFixture);

        verify(entityService).disposeAfterStep(entity);
    }

    Entity createLevelLoad() {
        Entity entity =
                new Entity()
                        .addComponent(new LevelLoadTriggerComponent())
                        .addComponent(new PhysicsComponent())
                        .addComponent(new HitboxComponent());
        entity.create();
        return entity;
    }

    Entity createPlayer() {
        Entity player =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PLAYER));
        player.create();
        return player;
    }

    Entity createEntity(short layer) {
        Entity entity =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new HitboxComponent().setLayer(layer));
        entity.create();
        return entity;
    }
}
