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
import java.util.Random;

/**
 * When this entity touches the player, an additional level will be loaded at the end of the current level
 * This entity is then disposed of.
 */
public class LevelLoadTriggerComponent extends Component {
    HitboxComponent hitboxComponent;
    EntityService entityService;
    private static final Logger logger = LoggerFactory.getLogger(LevelLoadTriggerComponent.class);
    private static final Random RANDOM = new Random();

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
        Path directory = Paths.get("configs/rags/");
        ArrayList<Path> pathList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.rag")) {
            for (Path path : stream) {
                pathList.add(path);
            }
        } catch (IOException e) {
            logger.error("File rags files could not be loaded");
        }

        String RagFile = pathList.get(MathUtils.random(pathList.size())).getFileName().toString();
        logger.debug("Loading Level : " + RagFile);
        return RagFile.substring(0, RagFile.lastIndexOf('.'));
    }
}
