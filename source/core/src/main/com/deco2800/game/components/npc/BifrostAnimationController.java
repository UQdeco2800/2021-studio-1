package com.deco2800.game.components.npc;

import com.deco2800.game.components.Component;
import com.deco2800.game.rendering.BifrostAnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class BifrostAnimationController extends Component {
  BifrostAnimationRenderComponent animator;

  @Override
  public void create() {
    super.create();
    animator = this.entity.getComponent(BifrostAnimationRenderComponent.class);
    entity.getEvents().addListener("burn", this::animateBurn);
    animateBurn();

  }

  /**
   * Starts the "walk" animation
   */
  void animateBurn() {
    animator.startAnimation("burn");
  }
}
