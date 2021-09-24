package com.deco2800.game.entities.factories;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.rendering.TextureRenderComponent;
import com.deco2800.game.services.ServiceLocator;

public class PowerUpFactory {

    /**
     * Create a lightning power up entity.
     *
     * @return lightning power up entity
     */
    public static Entity createLightningPowerUp() {
        Entity powerUp = createBasePowerUp();

        AnimationRenderComponent animator =
            new AnimationRenderComponent(ServiceLocator.getResourceService()
                .getAsset("images/lightning-animation.atlas", TextureAtlas.class));

        animator.addAnimation("icon", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("blank", 1f, Animation.PlayMode.LOOP);
        animator.addAnimation("float", 0.1f, Animation.PlayMode.NORMAL);

        powerUp.addComponent(animator);
        powerUp.getComponent(AnimationRenderComponent.class).startAnimation("icon");

        powerUp.getComponent(HitboxComponent.class).setAsCircleAligned(
                0.2f, PhysicsComponent.AlignX.CENTER,
                PhysicsComponent.AlignY.BOTTOM);
        powerUp.getComponent(ColliderComponent.class).setAsCircleAligned(
                0.2f, PhysicsComponent.AlignX.CENTER,
                PhysicsComponent.AlignY.BOTTOM);
        powerUp.getComponent(ColliderComponent.class).setSensor(true);
        powerUp.getComponent(PhysicsComponent.class).setBodyType
                (BodyDef.BodyType.StaticBody);

        powerUp.setType(EntityTypes.LIGHTNINGPOWERUP);

        return powerUp;
    }

    /**
     * Create a shield power up entity.
     *
     * @return shield power up entity.
     */
    public static Entity createShieldPowerUp() {
        Entity powerUp = createBasePowerUp();
        powerUp.addComponent(new TextureRenderComponent(
                "images/powerup-shield.png"));

        Vector2[] shieldPoints = new Vector2[6];
        shieldPoints[0] = new Vector2(0.3f, 0.2f);
        shieldPoints[1] = new Vector2(0.1f, 0.3f);
        shieldPoints[2] = new Vector2(0.1f, 0.8f);
        shieldPoints[3] = new Vector2(0.8f, 0.8f);
        shieldPoints[4] = new Vector2(0.8f, 0.3f);
        shieldPoints[5] = new Vector2(0.6f, 0.2f);

        PolygonShape shield = new PolygonShape();
        shield.set(shieldPoints);
        powerUp.getComponent(ColliderComponent.class).setShape(shield);
        powerUp.getComponent(ColliderComponent.class).setSensor(true);
        powerUp.getComponent(PhysicsComponent.class).setBodyType
                (BodyDef.BodyType.StaticBody);
        powerUp.setType(EntityTypes.SHIELDPOWERUP);

        return powerUp;
    }

    /**
     * Creates a spear power up entity.
     *
     * @return spear power up entity.
     */
    public static Entity createSpearPowerUp() {
        Entity powerUp = createBasePowerUp();
        powerUp.addComponent(new TextureRenderComponent("images/powerup-spear" +
                ".png"));

        powerUp.setScale(1.1f, 1.1f);
        PhysicsUtils.setScaledCollider(powerUp, 1f, 1f);
        powerUp.getComponent(ColliderComponent.class).setSensor(true);
        powerUp.getComponent(PhysicsComponent.class).setBodyType
                (BodyDef.BodyType.StaticBody);

        powerUp.setType(EntityTypes.SPEARPOWERUP);
        return powerUp;
    }

    /**
     * Creates a base power up to be extended by more specific power ups.
     *
     * @return base power up
     */
    public static Entity createBasePowerUp() {
        Entity powerUp =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(new ColliderComponent())
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.POWERUP));

        powerUp.getComponent(PhysicsComponent.class).setGravityScale(5.0f);

        return powerUp;
    }

    private PowerUpFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
