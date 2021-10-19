package com.deco2800.game.components.npc;

import com.deco2800.game.components.Component;
import com.deco2800.game.rendering.AnimationRenderComponent;

/**
 * Class to handle fireball animation
 */
public class FireballAnimationController extends Component {
    AnimationRenderComponent animator;

    /**
     * Creating fireball animation events
     */
    @Override
    public void create() {
        super.create();
        animator = this.entity.getComponent(AnimationRenderComponent.class);
        entity.getEvents().addListener("fireball", this::animateFireball);
        entity.getEvents().addListener("fireball_back", this::animateFireballBack);
        animateFireball();
    }

    /**
     * trigger fireball animation
     */
    void animateFireball() {
        animator.startAnimation("fireball");
    }

    /**
     * trigger right facing fireball animation
     */
    void animateFireballBack() {
        animator.startAnimation("fireball_back");
    }
}
