package com.deco2800.game.components.powerups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.deco2800.game.components.CombatStatsComponent;
import com.deco2800.game.components.TouchAttackComponent;
import com.deco2800.game.components.player.PlayerActions;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.utils.math.Vector2Utils;

/**
 * Controls the behaviour for the spear.
 * The spear may be thrown three times and replenishes on pick up.
 */
public class SpearPowerUpComponent extends PowerUpComponent {
    private Entity spear;

    // Update flags to check
    private boolean active;
    private boolean flown;
    private int thrown;

    public void obtainSpear(Entity spear) {
        this.spear = spear;
    }

    public void reset() {
        active = false;
        flown = false;
        thrown = 0;
    }

    @Override
    public void create() {
        setEnabled(false);
        spear = null;
        reset();
    }

    @Override
    public void earlyUpdate() {
        Body spearBod = spear.getComponent(PhysicsComponent.class).getBody();

        // If after flying, the spear stops or goes below y = 0, deactivate and reset
        if ((flown && active && spearBod.getLinearVelocity().isZero()) || spear.getCenterPosition().y < 0) {
            active = false;
            flown = false;

            // Disposes the spear after three throws
            if (thrown == 3) {
                reset();
                setEnabled(false);
                spear.flagDelete();
            }
        }
    }

    /**
     * Checks the state of the spear and tracks the player's movement.
     */
    @Override
    public void update() {
        PlayerActions playerActions = entity.getComponent(PlayerActions.class);
        Vector2 playerPos = entity.getCenterPosition();
        Vector2 playerDir = playerActions.getRunDirection();

        Vector2 impulse;
        Body spearBod = spear.getComponent(PhysicsComponent.class).getBody();

        Vector2 offset;
        Vector2 extraVert = Vector2.Zero.cpy(); // Offset vector for crouching

        Vector2 spearPos;

        StringBuilder anim = new StringBuilder();

        // Determines which animation to play with precedence
        if (active) {
            anim.append("fly-");
        } else if (playerActions.isJumping() || playerActions.isCrouching()) {
            anim.append("flat-");
        } else if (playerActions.isMoving()) {
            anim.append("swing-");
        } else {
            anim.append("stand-");
        }

        if (!(playerActions.isMoving())) {
            playerDir = playerActions.getPreviousDirection();
        }

        // Determines which direction for the animation needs to play
        if (playerDir.hasSameDirection(Vector2Utils.LEFT)) {
            offset = new Vector2(1.3f, 0.5f);
            spearPos = playerPos.sub(offset);
            impulse = new Vector2(-60f, 15f).scl(spearBod.getMass());
            anim.append("left");
        } else {
            offset = new Vector2(0.3f, -0.5f);
            spearPos = playerPos.add(offset);
            impulse = new Vector2(60f, 15f).scl(spearBod.getMass());
            anim.append("right");
        }

        if (playerActions.isCrouching()) {
            extraVert.sub(new Vector2(0f, 0.3f));
        }

        // If the player throws the spear and hasn't yet flown, apply impulse and increment throws
        if (active && !flown) {
            spear.getComponent(AnimationRenderComponent.class).stopAnimation();

            spearBod.applyLinearImpulse(impulse, spearBod.getWorldCenter(), true);
            spear.getComponent(AnimationRenderComponent.class).startAnimation(anim.toString());

            flown = true;
            thrown++;
        }

        // Default animations while spear is in hand
        if (!active) {
            spear.setPosition(spearPos.add(extraVert));
            spear.getComponent(AnimationRenderComponent.class).stopAnimation();
            spear.getComponent(AnimationRenderComponent.class).startAnimation(anim.toString());
        }
    }

    @Override
    public void activate() {
        active = true;
        spear.getComponent(TouchAttackComponent.class).setEnabled(true);
        spear.getComponent(CombatStatsComponent.class).setEnabled(true);
    }
}