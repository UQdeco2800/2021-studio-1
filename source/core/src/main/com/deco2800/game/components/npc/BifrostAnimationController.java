package com.deco2800.game.components.npc;

import com.deco2800.game.components.Component;
import com.deco2800.game.rendering.AnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class BifrostAnimationController extends Component {
  AnimationRenderComponent animator;

  @Override
  public void create() {
    super.create();
    animator = this.entity.getComponent(AnimationRenderComponent.class);
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
