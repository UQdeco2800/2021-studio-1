package com.deco2800.game.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.components.HitboxComponent;
import com.deco2800.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * When this entity touches the player, an additional level will be loaded at the end of the current level
 * This entity is then disposed of.
 */
public class LevelLoadTriggerComponent extends Component {
    HitboxComponent hitboxComponent;
    EntityService entityService;
    private static final Logger logger = LoggerFactory.getLogger(LevelLoadTriggerComponent.class);

    List<String> pathList = new ArrayList<>();
    List<String> randomisedList = new ArrayList<>();
    /**
     * Create a component which disposes entities on collision finish
     */
    public LevelLoadTriggerComponent() {
        this.entityService = ServiceLocator.getEntityService();
    }

    @Override
    public void create() {
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        hitboxComponent = entity.getComponent(HitboxComponent.class);
        Path directory = Paths.get("configs/rags/");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.rag")) {
            for (Path path : stream) {
                // Ignore the file if it is part of the start files
                if (path.getFileName().toString().equals("start.rag")) {
                    continue;
                } else {
                    pathList.add(path.getFileName().toString().split(".rag")[0]);
                }

            }
        } catch (IOException e) {
            logger.error("File rags files could not be loaded");
        }
        if (pathList.isEmpty()) {
            logger.error("File rags files could not be loaded");
        }
        
        updateRandomisedList();

    }


    private void onCollisionStart(Fixture me, Fixture other) {
        if (hitboxComponent.getFixture() != me) {
            // Not triggered by hitbox, ignore
            return;
        }

        if (PhysicsLayer.contains(PhysicsLayer.PLAYER, other.getFilterData().categoryBits)) {
            // HAS COLLIDED WITH PLAYER so load in next level
            ServiceLocator.getTerminalService().sendTerminal("-load " + getNextArea());
            // Dispose the load trigger after the physics step
            entity.flagDelete();
        }
    }

    private String getNextArea() {
        if (randomisedList.isEmpty()) {
            updateRandomisedList();
        }
        return randomisedList.remove(0);
    }

    private void updateRandomisedList() {
        randomisedList = new ArrayList<>(pathList);
        Collections.shuffle(randomisedList);
    }
}
