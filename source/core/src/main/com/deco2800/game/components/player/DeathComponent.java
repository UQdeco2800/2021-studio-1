package com.deco2800.game.components.player;

import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.rendering.AnimationRenderComponent;

public class DeathComponent extends Component {
    Entity gameUI;

    public DeathComponent (Entity gameUI) {
        this.gameUI = gameUI;
    }

    /**
     * Checks if the death animation has finished and triggers the game over sequence to start
     */
    @Override
    public void update() {
        if (entity.getComponent(AnimationRenderComponent.class).isFinished()) {
            entity.getComponent(AnimationRenderComponent.class).stopAnimation();
            gameUI.getEvents().trigger("Game Over");
        }
    }
}
