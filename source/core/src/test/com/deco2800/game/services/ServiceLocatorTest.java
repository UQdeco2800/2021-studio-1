package com.deco2800.game.services;

import com.deco2800.game.entities.EntityService;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.rendering.RenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(GameExtension.class)
class ServiceLocatorTest {
  @Test
  void shouldGetSetServices() {
    EntityService entityService = new EntityService();
    RenderService renderService = new RenderService();
    PhysicsService physicsService = mock(PhysicsService.class);
    GameTime gameTime = new GameTime();
    SoundService soundService = new SoundService("");

    ServiceLocator.registerEntityService(entityService);
    ServiceLocator.registerRenderService(renderService);
    ServiceLocator.registerPhysicsService(physicsService);
    ServiceLocator.registerTimeSource(gameTime);
    ServiceLocator.registerSoundService(soundService);

    assertEquals(ServiceLocator.getEntityService(), entityService);
    assertEquals(ServiceLocator.getRenderService(), renderService);
    assertEquals(ServiceLocator.getPhysicsService(), physicsService);
    assertEquals(ServiceLocator.getTimeSource(), gameTime);
    assertEquals(ServiceLocator.getSoundService(), soundService);

    ServiceLocator.clear();
    assertNull(ServiceLocator.getEntityService());
    assertNull(ServiceLocator.getRenderService());
    assertNull(ServiceLocator.getPhysicsService());
    assertNull(ServiceLocator.getTimeSource());
    assertNull(ServiceLocator.getSoundService());
  }
}