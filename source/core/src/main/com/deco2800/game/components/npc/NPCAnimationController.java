package com.deco2800.game.components.npc;

import com.deco2800.game.components.Component;
import com.deco2800.game.rendering.AnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class NPCAnimationController extends Component {
  private boolean toDie;
  AnimationRenderComponent animator;

  /**
   * creating the animation events
   */
  @Override
  public void create() {
    super.create();
    animator = this.entity.getComponent(AnimationRenderComponent.class);
    entity.getEvents().addListener("wanderStart", this::animateWander);
    entity.getEvents().addListener("chaseStart", this::animateChase);
    entity.getEvents().addListener("chaseStart_right", this::animateChase_right);
    entity.getEvents().addListener("move_right", this::animateWander_right);
    entity.getEvents().addListener("death", this::animateDeath);
  }

  /**
   * trigger death animation
   */
  void animateDeath() {
    if (!entity.getDeath()) {
      animator.stopAnimation();
      animator.startAnimation("death");
    }
  }

  /**
   * trigger run animation
   */
  void animateWander() {
    if (!entity.getDeath()) {
      animator.stopAnimation();
      animator.startAnimation("run");
    }
  }

  /**
   * trigger right facing run animation
   */
  void animateWander_right() {
    if (!entity.getDeath()) {
      animator.stopAnimation();
      animator.startAnimation("run_back");
    }
  }

  /**
   * trigger run animation
   */
  void animateChase() {
    if (!entity.getDeath()) {
      animator.stopAnimation();
      animator.startAnimation("run");
    }
  }

  /**
   * trigger right facing run animation
   */
  void animateChase_right() {
    if (!entity.getDeath()) {
      animator.stopAnimation();
      animator.startAnimation("run_back");
    }
  }
}
