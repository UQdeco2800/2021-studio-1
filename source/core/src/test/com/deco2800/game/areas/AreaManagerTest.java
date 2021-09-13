package com.deco2800.game.areas;

import com.deco2800.game.areas.terrain.TerrainComponent;
import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.services.ServiceLocator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

@ExtendWith(GameExtension.class)
class AreaManagerTest {
    static final int GRID_SCALE = 3;
    int x;
    int y;

    @Test
    void shouldSpawnEntities() {
        TerrainFactory factory = mock(TerrainFactory.class);

        GameArea gameArea =
                new GameArea() {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        ServiceLocator.registerEntityService(new EntityService());
        Entity entity = mock(Entity.class);

        gameArea.spawnEntity(entity);
        verify(entity).create();

        gameArea.dispose();
        verify(entity).dispose();
    }

    @Test
    void shouldPlaceFloor() {
        TerrainFactory factory = mock(TerrainFactory.class);

        AreaManager manager =
                new AreaManager(factory) {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        RagnarokArea area = mock(RagnarokArea.class);

        manager.place(area, x, y, "floor");
        verify(area).spawnFloor(x * GRID_SCALE,y * GRID_SCALE);
    }

    @Test
    void shouldPlacePlatform() {
        TerrainFactory factory = mock(TerrainFactory.class);

        AreaManager manager =
                new AreaManager(factory) {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        RagnarokArea area = mock(RagnarokArea.class);

        manager.place(area, x, y, "platform");
        verify(area).spawnPlatform(x * GRID_SCALE,y * GRID_SCALE);
    }

    @Test
    void shouldPlaceRocks() {
        TerrainFactory factory = mock(TerrainFactory.class);

        AreaManager manager =
                new AreaManager(factory) {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        RagnarokArea area = mock(RagnarokArea.class);

        manager.place(area, x, y, "rocks");
        verify(area).spawnRocks(x * GRID_SCALE,y * GRID_SCALE);
    }

    @Test
    void shouldPlaceSpikes() {
        TerrainFactory factory = mock(TerrainFactory.class);

        AreaManager manager =
                new AreaManager(factory) {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        RagnarokArea area = mock(RagnarokArea.class);

        manager.place(area, x, y, "spikes");
        verify(area).spawnSpikes(x * GRID_SCALE,y * GRID_SCALE);
    }

    @Test
    void shouldClearEntityAt() {
        TerrainFactory factory = mock(TerrainFactory.class);

        AreaManager manager =
                new AreaManager(factory) {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        RagnarokArea area = mock(RagnarokArea.class);

        manager.place(area, x, y, "null");
        verify(area).clearEntitiesAt(x * GRID_SCALE,y * GRID_SCALE);
    }

    @Test
    void placeShouldNotCallSpawn() {
        TerrainFactory factory = mock(TerrainFactory.class);

        AreaManager manager =
                new AreaManager(factory) {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        RagnarokArea area = mock(RagnarokArea.class);

        manager.place(area, x, y, "invalid");
        verifyNoInteractions(area);
    }

    @Test
    void shouldLoadLevel() {
        TerrainFactory factory = mock(TerrainFactory.class);

        AreaManager manager =
                new AreaManager(factory) {
                    @Override
                    public void create(int xOffset) {
                    }
                };

        RagnarokArea area = mock(RagnarokArea.class);

        manager.place(area, 5, 10, "platform");
        verify(area).spawnPlatform(5 * GRID_SCALE,5 * GRID_SCALE);
    }
}