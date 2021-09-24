package com.deco2800.game.components.player;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.entities.factories.EntityTypes;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.physics.PhysicsService;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.GameTime;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.utils.math.Vector2Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(GameExtension.class)
public class PlayerActionsTest {
    Entity player;

    @BeforeEach
    void before() {
        GameTime gameTime = mock(GameTime.class);
        when(gameTime.getDeltaTime()).thenReturn(0.02f);
        ServiceLocator.registerTimeSource(gameTime);
        ServiceLocator.registerPhysicsService(new PhysicsService());
        player = new Entity();
        player.addComponent(mock(PlayerActions.class))
                .addComponent(new PhysicsComponent());
        player.addComponent(new ColliderComponent());
        player.addComponent(new ShieldPowerUpComponent());
        player.addComponent(new SpearPowerUpComponent());
        player.addComponent(new LightningPowerUpComponent());
        //player.addComponent(new AnimationRenderComponent(new TextureAtlas()));

        player.getComponent(ShieldPowerUpComponent.class).setEnabled(false);
        player.getComponent(SpearPowerUpComponent.class).setEnabled(false);
        player.getComponent(LightningPowerUpComponent.class).setEnabled(false);

        player.setType(EntityTypes.PLAYER);
    }

    @Test
    void notRunningTest() {
        assertTrue(player.getComponent(PlayerActions.class).isMoving());
    }

    @Test
    void notJumpingTest() {
        assertFalse(player.getComponent(PlayerActions.class).isJumping());
    }

    @Test
    void runTest() {
        //when(player.getComponent(PlayerActions.class).run(Vector2Utils
        // .RIGHT)).thenCallRealMethod();
        player.getEvents().trigger("run", Vector2Utils.RIGHT);
        player.getComponent(PlayerActions.class).run(Vector2Utils.RIGHT);
        assertTrue(player.getComponent(PlayerActions.class).isMoving());
    }

    @Test
    void jumpTest() {
        player.getComponent(PlayerActions.class).jump();
        assertTrue(player.getComponent(PlayerActions.class).isJumping());
    }

    @Test
    void stopRunTest() {
        player.getComponent(PlayerActions.class).run(Vector2Utils.RIGHT);
        player.getComponent(PlayerActions.class).stopRunning();
        assertFalse(player.getComponent(PlayerActions.class).isMoving());
    }

    @Test
    void shieldBeforeCollision() {
        assertFalse(player.getComponent(LightningPowerUpComponent.class).getEnabled());
    }

    @Test
    void shieldCollision() {
        EntityService entityService = mock(EntityService.class);
        ServiceLocator.registerEntityService(entityService);

        Entity shield = createShield();
        Vector2 pos = new Vector2(0, 0);
        player.create();
        shield.create();
        entityService.register(shield);
        entityService.register(player);
        shield.setPosition(pos);
        player.setPosition(pos);

        Fixture playerFixture =
                player.getComponent(ColliderComponent.class).getFixture();
        Fixture shieldFixture =
                shield.getComponent(ColliderComponent.class).getFixture();
        player.getEvents().trigger("collisionStart", playerFixture,
                shieldFixture);

        ServiceLocator.getPhysicsService().getPhysics().update();
        verify(player.getComponent(PlayerActions.class)).obtainPowerUp(playerFixture, shieldFixture);
    }

    @Test
    void spearBeforeCollision() {
        assertFalse(player.getComponent(LightningPowerUpComponent.class).getEnabled());
    }

    @Test
    void spearCollision() {
        EntityService entityService = mock(EntityService.class);
        ServiceLocator.registerEntityService(entityService);

        Entity spear = createSpear();
        Vector2 pos = new Vector2(0, 0);
        spear.setPosition(pos);
        player.setPosition(pos);
        entityService.register(spear);
        entityService.register(player);

        ServiceLocator.getPhysicsService().getPhysics().update();
        assertTrue(player.getComponent(SpearPowerUpComponent.class).getEnabled());
    }

    @Test
    void lightningBeforeCollision() {
        assertFalse(player.getComponent(LightningPowerUpComponent.class).getEnabled());
    }

    @Test
    void lightningCollision() {
        EntityService entityService = mock(EntityService.class);
        ServiceLocator.registerEntityService(entityService);

        Entity lightning = createLightning();
        Vector2 pos = new Vector2(0, 0);
        lightning.setPosition(pos);
        player.setPosition(pos);
        entityService.register(lightning);
        entityService.register(player);

        ServiceLocator.getPhysicsService().getPhysics().update();
        assertTrue(player.getComponent(LightningPowerUpComponent.class).getEnabled());
    }

    Entity createShield() {
        Entity shield = new Entity().addComponent(new PhysicsComponent());
        shield.addComponent(new ColliderComponent());
        shield.setType(EntityTypes.SHIELDPOWERUP);
        return shield;
    }

    Entity createSpear() {
        Entity shield = new Entity().addComponent(new PhysicsComponent());
        shield.addComponent(new ColliderComponent());
        shield.setType(EntityTypes.SPEARPOWERUP);
        return shield;
    }

    Entity createLightning() {
        Entity shield = new Entity().addComponent(new PhysicsComponent());
        shield.addComponent(new ColliderComponent());
        shield.setType(EntityTypes.LIGHTNINGPOWERUP);
        return shield;
    }
}
