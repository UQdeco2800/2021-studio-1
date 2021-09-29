package com.deco2800.game.components.player;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.ShieldPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;
import com.deco2800.game.entities.Entity;
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
        player.addComponent(new PlayerActions());
        player.addComponent(new PhysicsComponent());
        player.addComponent(new ColliderComponent());
        player.addComponent(new KeyboardPlayerInputComponent());
        player.addComponent(new ShieldPowerUpComponent());
        player.addComponent(new SpearPowerUpComponent());
        player.addComponent(new LightningPowerUpComponent());
        player.addComponent(new AnimationRenderComponent(new TextureAtlas()));

        player.getComponent(ShieldPowerUpComponent.class).setEnabled(false);
        player.getComponent(SpearPowerUpComponent.class).setEnabled(false);
        player.getComponent(LightningPowerUpComponent.class).setEnabled(false);

        player.setType(EntityTypes.PLAYER);
    }

    @Test
    void notRunningTest() {
        assertFalse(player.getComponent(PlayerActions.class).isMoving());
    }

    @Test
    void notJumpingTest() {
        assertFalse(player.getComponent(PlayerActions.class).isJumping());
    }

    @Test
    void runTest() {
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
    void spearBeforeCollision() {
        assertFalse(player.getComponent(LightningPowerUpComponent.class).getEnabled());
    }

    @Test
    void lightningBeforeCollision() {
        assertFalse(player.getComponent(LightningPowerUpComponent.class).getEnabled());
    }

    Entity createShield() {
        Entity shield = new Entity().addComponent(new PhysicsComponent());
        shield.addComponent(new ColliderComponent());
        shield.setType(EntityTypes.SHIELDPOWERUP);
        return shield;
    }

    Entity createSpear() {
        Entity spear = new Entity().addComponent(new PhysicsComponent());
        spear.addComponent(new ColliderComponent());
        spear.setType(EntityTypes.SPEARPOWERUP);
        return spear;
    }

    Entity createLightning() {
        Entity lightning = new Entity().addComponent(new PhysicsComponent());
        lightning.addComponent(new ColliderComponent());
        lightning.setType(EntityTypes.LIGHTNINGPOWERUP);
        return lightning;
    }
}
