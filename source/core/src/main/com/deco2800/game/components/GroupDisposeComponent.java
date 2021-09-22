package com.deco2800.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;

/**
 * When this entity is disposed of, it disposes of a list of other entities.
 *
 * This component is used in terrain generation, it holds a reference to a selection of platforms
 * or floors that have no collision component and acts as the collision for them. When this is
 * disposed of, so are all its platforms or floors.
 */
public class GroupDisposeComponent extends Component {
    EntityService entityService;
    Entity[] entities;

    /**
     * Create a component which disposes all entities on its disposal.
     *
     * @param entities entities to dispose of with this component's entity
     */
    public GroupDisposeComponent(Entity[] entities) {
        this.entityService = ServiceLocator.getEntityService();
        this.entities = entities;
    }

    @Override
    public void create() {
    }

    /**
     * Dispose of all entities held in this component.
     */
    public void dispose() {
        for (Entity entity : entities) {
            if (entity != null) entityService.disposeAfterStep(entity);
        }
    }
}
