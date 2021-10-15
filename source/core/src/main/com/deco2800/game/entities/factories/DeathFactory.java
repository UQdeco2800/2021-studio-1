package com.deco2800.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.deco2800.game.components.player.DeathComponent;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.rendering.AnimationRenderComponent;
import com.deco2800.game.services.ServiceLocator;

public class DeathFactory {
    public static Entity createPlayerDeath(Entity gameUI) {
        Entity player = new Entity().addComponent(new DeathComponent(gameUI));

        AnimationRenderComponent animator =
                new AnimationRenderComponent(ServiceLocator.getResourceService()
                        .getAsset("images/odin.atlas", TextureAtlas.class));

        animator.addAnimation("death-right", 0.1f,
                Animation.PlayMode.NORMAL);

        player.addComponent(animator);
        ServiceLocator.getSoundService().playSound("death");

        return player;
    }

    public static Entity createFade() {
        Entity fade = new Entity();
        AnimationRenderComponent animator =
                new AnimationRenderComponent(ServiceLocator.getResourceService()
                        .getAsset("images/deathFade.atlas", TextureAtlas.class));

        animator.addAnimation("deathFade", 0.1f,
                Animation.PlayMode.NORMAL);
        fade.addComponent(animator);
        return fade;
    }
}
