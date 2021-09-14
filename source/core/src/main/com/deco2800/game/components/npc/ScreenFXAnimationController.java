package com.deco2800.game.components.npc;

import com.deco2800.game.components.Component;
import com.deco2800.game.rendering.AnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class ScreenFXAnimationController extends Component {
  AnimationRenderComponent animator;

  @Override
  public void create() {
    super.create();
    animator = this.entity.getComponent(AnimationRenderComponent.class);
    entity.getEvents().addListener("default", this::animateNormal);
    entity.getEvents().addListener("dark", this::animateDark);

  }
  /**
   * Starts the "walk" animation
   */
  void animateNormal() {
    animator.startAnimation("default");
  }

  void animateDark() {
    animator.startAnimation("dark");
  }

}
