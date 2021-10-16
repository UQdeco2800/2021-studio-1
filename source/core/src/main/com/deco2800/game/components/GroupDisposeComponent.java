package com.deco2800.game.components;

import com.deco2800.game.entities.Entity;

/**
 * When this entity is disposed of, it disposes of a list of other entities.
 *
 * This component is used in terrain generation, it holds a reference to a selection of platforms
 * or floors that have no collision component and acts as the collision for them. When this is
 * disposed of, so are all its platforms or floors.
 */
public class GroupDisposeComponent extends Component {
    private Entity[] entities;

    /**
     * Create a component which disposes all entities on its disposal.
     *
     * @param entities entities to dispose of with this component's entity
     */
    public GroupDisposeComponent(Entity[] entities) {
        this.entities = entities;
    }

    /**
     * Dispose of all entities held in this component.
     */
    @Override
    public void dispose() {
        for (Entity entity : entities) {
            if (entity != null) entity.flagDelete();
        }
    }
}
