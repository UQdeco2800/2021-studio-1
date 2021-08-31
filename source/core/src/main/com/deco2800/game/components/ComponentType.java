package com.deco2800.game.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.ObjectMap;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.BodyUserData;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.physics.components.PhysicsComponent;

/**
 * Internal type system for components. Used to generate unique IDs for each component type at
 * runtime, allowing entities to get components by type.
 */
public class ComponentType {
  private static final ObjectMap<Class<? extends Component>, ComponentType> componentTypes =
      new ObjectMap<>();
  private static int nextId = 0;

  private final int id;

  public static ComponentType getFrom(Class<? extends Component> type) {
    ComponentType componentType = componentTypes.get(type);
    if (componentType == null) {
      componentType = new ComponentType();
      componentTypes.put(type, componentType);
    }
    return componentType;
  }

  public int getId() {
    return id;
  }

  private ComponentType() {
    id = nextId;
    nextId++;
  }

}
