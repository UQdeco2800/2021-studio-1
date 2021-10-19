package com.deco2800.game.components;

import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.terminal.Terminal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class BifrostFXComponentTest {
    @Mock Terminal terminal;

    Entity bifrost;
    Entity player;

    @BeforeEach
    void beforeEach() {
        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerPhysicsService(new PhysicsService());
        ServiceLocator.registerTerminalService(terminal);

        bifrost = new Entity();
        bifrost.addComponent(new BifrostFXComponent());
        bifrost.addComponent(new PhysicsComponent());
        bifrost.addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE));

        player = new Entity();
        player.addComponent(new PhysicsComponent());
        player.addComponent(new HitboxComponent().setLayer(PhysicsLayer.PLAYER));

        bifrost.create();
        player.create();
    }

    @Test
    void shouldSendSpawn() {
        int y = 5;
        player.setPosition(0, y);

        bifrost.getEvents().trigger("collisionStart",
                bifrost.getComponent(HitboxComponent.class).getFixture(),
                player.getComponent(HitboxComponent.class).getFixture());

        verify(terminal).sendTerminal(String.format("-spawn [%d,%d] (bifrostFX)", -1, y - 3));
    }

    @Test
    void shouldSendSpawnAtDifferentY() {
        int y = 9;
        player.setPosition(0, y);

        bifrost.getEvents().trigger("collisionStart",
                bifrost.getComponent(HitboxComponent.class).getFixture(),
                player.getComponent(HitboxComponent.class).getFixture());

        verify(terminal).sendTerminal(String.format("-spawn [%d,%d] (bifrostFX)", -1, y - 3));
    }
}