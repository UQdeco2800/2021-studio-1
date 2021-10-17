package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.services.ServiceLocator;

public class CameraShakeComponent extends Component {
    private Entity target;
    private CameraComponent cameraComponent;
    private float toggle;
    private int active;
    private Entity sfx;

    /**
     * Class to shake the camera and start event listener for animation
     *
     * @param target          the target of the camera
     * @param cameraComponent current camera to shake
     * @param sfx             screen effects entity
     */
    public CameraShakeComponent(Entity target, CameraComponent cameraComponent, Entity sfx) {
        this.target = target;
        this.cameraComponent = cameraComponent;
        this.toggle = 0.1f;
        this.active = 0;
        this.sfx = sfx;
    }

    /**
     * Create event listener for collision
     */
    @Override
    public void create() {
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
    }

    /**
     * Handles collision
     *
     * @param me    first collision object
     * @param other second collision object
     */
    private void onCollisionStart(Fixture me, Fixture other) {
        float distance = entity.getPosition().dst(target.getPosition());

        active = active + 1;
        if (this.toggle == 0.1f && active % 3 == 0) {
            this.toggle = -0.1f;
        } else {
            this.toggle = 0.1f;
        }

        entity.getEvents().trigger("moveRightAngry");
        this.sfx.getEvents().trigger("dark");

        // TODO: refactor all of this to be contained in a call to the sound service
        if (distance < 32f) {
            cameraComponent.setOffset(this.toggle);
            cameraComponent.update();
            cameraComponent.resetLastPosition();
        } else {
            entity.getEvents().trigger("moveRight");
            this.sfx.getEvents().trigger("normal");
            cameraComponent.setOffset(0);
            cameraComponent.update();
        }
    }
}