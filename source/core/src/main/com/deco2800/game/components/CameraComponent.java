package com.deco2800.game.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class CameraComponent extends Component {
    private final Camera camera;
    private Vector2 lastPosition;
    private float offset = 0;

    public CameraComponent() {
        this(new OrthographicCamera());
    }

    public CameraComponent(Camera camera) {
        this.camera = camera;
        lastPosition = Vector2.Zero.cpy();
    }

    @Override
    public void update() {
        Vector2 position = entity.getPosition();
        if (!lastPosition.epsilonEquals(entity.getPosition())) {
            camera.position.set(position.x + offset, position.y + offset, 0f);
            lastPosition = position;
            camera.update();

        }
    }

    /**
     * Sets the offset
     *
     * @param offset offset for the camera
     */
    public void setOffset(float offset) {
        this.offset = offset;
    }

    /**
     * Gets the offset
     *
     * @return offset
     */
    public float getOffset() {
        return this.offset;
    }

    /**
     * Resets the last position
     */
    public void resetLastPosition() {
        lastPosition = Vector2.Zero.cpy();
    }

    public Matrix4 getProjectionMatrix() {
        return camera.combined;
    }

    public Camera getCamera() {
        return camera;
    }

    public void resize(int screenWidth, int screenHeight, float gameWidth) {
        float ratio = (float) screenHeight / screenWidth;
        camera.viewportWidth = gameWidth;
        camera.viewportHeight = gameWidth * ratio;
        camera.update();
    }
}
