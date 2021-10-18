package com.deco2800.game.components.npc;

import com.deco2800.game.components.Component;
import com.deco2800.game.rendering.AnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class GhostAnimationController extends Component {
  private boolean toDie;
  AnimationRenderComponent animator;

  @Override
  public void create() {
    super.create();
    animator = this.entity.getComponent(AnimationRenderComponent.class);
    entity.getEvents().addListener("wanderStart", this::animateWander);
    entity.getEvents().addListener("chaseStart", this::animateChase);
    entity.getEvents().addListener("chaseStart_right", this::animateChase_right);
    entity.getEvents().addListener("move_right", this::animateWander_right);
    entity.getEvents().addListener("death", this::animateDeath);
    this.toDie = false;

          //test for NPC to face other direction when moving
  }

  void animateDeath() {
    animator.startAnimation("death");
    this.toDie = true;
  }

  @Override
  public void update() {
    if (this.toDie && this.animator.isFinished()) {
      this.entity.flagDelete();
    }
  }

  void animateWander() {
    animator.startAnimation("run");
  }

  void animateWander_right() {
    animator.startAnimation("run_back");
  } //test for NPC to face other direction when moving

  void animateChase() {
    animator.startAnimation("run");
  }

  void animateChase_right() {
    animator.startAnimation("run_back");
  }
}
