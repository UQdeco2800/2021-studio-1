package com.deco2800.game.rendering;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class ParticleEffectRenderComponentTest {
    @Mock ParticleEffect particleEffect;
    @Mock RenderService renderService;

    Entity particleEntity;
    Entity player;
    static short targetLayer = PhysicsLayer.PLAYER;

    private Entity createParticleEntity() {
        Entity entity = new Entity()
                .addComponent(new ParticleEffectRenderComponent(particleEffect, targetLayer))
                .addComponent(new PhysicsComponent())
                .addComponent(new HitboxComponent());
        entity.create();
        return entity;
    }

    private Entity createPlayer() {
        Entity entity = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new HitboxComponent().setLayer(targetLayer));
        entity.create();
        return entity;
    }

    @BeforeEach
    void beforeEach() {
        ServiceLocator.registerRenderService(renderService);
        ServiceLocator.registerPhysicsService(new PhysicsService());
        ServiceLocator.registerEntityService(new EntityService());
        particleEntity = createParticleEntity();
        player = createPlayer();
    }

    @Test
    void shouldUpdateOnRender() {
        SpriteBatch batch = mock(SpriteBatch.class);
        particleEntity.getComponent(ParticleEffectRenderComponent.class).render(batch);
        verify(particleEffect).update(anyFloat());
    }

    @Test
    void shouldStart() {
        particleEntity.getComponent(ParticleEffectRenderComponent.class).start();
        verify(particleEffect).start();
    }

    @Test
    void shouldDispose() {
        particleEntity.getComponent(ParticleEffectRenderComponent.class).dispose();
        verify(particleEffect).dispose();
    }

    @Test
    void shouldStartOnCollision() {
        particleEntity.getEvents().trigger("collisionStart",
                particleEntity.getComponent(HitboxComponent.class).getFixture(),
                player.getComponent(HitboxComponent.class).getFixture());
        verify(particleEffect).start();
    }

    @Test
    void shouldRenderAtEntityYCoordinate() {
        float x = 5f;
        float y = 8f;
        player.setPosition(x, y);
        particleEntity.setPosition(x, 0);
        particleEntity.getEvents().trigger("collisionStart",
                particleEntity.getComponent(HitboxComponent.class).getFixture(),
                player.getComponent(HitboxComponent.class).getFixture());

        SpriteBatch batch = mock(SpriteBatch.class);
        particleEntity.getComponent(ParticleEffectRenderComponent.class).render(batch);

        verify(particleEffect).setPosition(x, y);
    }
}