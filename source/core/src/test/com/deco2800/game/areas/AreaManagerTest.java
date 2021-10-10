package com.deco2800.game.areas;

import com.deco2800.game.areas.terrain.TerrainFactory;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.entities.EntityService;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.terminal.Terminal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Provider;

import static org.mockito.Mockito.*;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class AreaManagerTest {
    private static final int JOT_WIDTH = 15;
    static final int GRID_SCALE = 3;
    int x;
    int y;

    AreaManager manager;

    @Mock
    TerrainFactory terrainFactory;

    @Mock
    RagnarokArea area;

    @BeforeEach
    void beforeEach() {
        manager = spy(new AreaManager(terrainFactory, area));
    }

    @Test
    void shouldPlaceFloor() {
        manager.place(x, y, "floor");
        verify(area).spawnFloor(x * GRID_SCALE, y * GRID_SCALE, null);
    }

    @Test
    void shouldPlacePlatform() {
        manager.place(x, y, "platform");
        verify(area).spawnPlatform(x * GRID_SCALE, y * GRID_SCALE, null);
    }

    @Test
    void shouldPlaceRocks() {
        manager.place(x, y, "rocks");
        verify(area).spawnRocks(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldPlaceSpikes() {
        manager.place(x, y, "spikes");
        verify(area).spawnSpikes(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldNotInteractWithInvalidPlace() {
        manager.place(x, y, "invalid");
        verifyNoInteractions(area);
    }

    @Test
    void shouldNotInteractWithNullPlace() {
        manager.place(x, y, "invalid");
        verifyNoInteractions(area);
    }

    @Test
    void shouldSpawnSkeleton() {
        manager.spawn(x, y, "skeleton");
        verify(area).spawnSkeleton(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldSpawnWolf() {
        manager.spawn(x, y, "wolf");
        verify(area).spawnWolf(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldSpawnLevelTrigger() {
        manager.spawn(x, y, "levelTrigger");
        verify(area).spawnLevelLoadTrigger(x * GRID_SCALE);
    }

    @Test
    void shouldSpawnFireSpirit() {
        manager.spawn(x, y, "fireSpirit");
        verify(area).spawnFireSpirit(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldSpawnShield() {
        manager.spawn(x, y, "shield");
        verify(area).spawnShield(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldSpawnSpear() {
        manager.spawn(x, y, "spear");
        verify(area).spawnSpear(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldSpawnLightning() {
        manager.spawn(x, y, "lightning");
        verify(area).spawnLightning(x * GRID_SCALE, y * GRID_SCALE);
    }

    @Test
    void shouldNotSpawnInvalid() {
        manager.spawn(x, y, "invalid");
        manager.spawn(x, y, "null");
        manager.spawn(x, y, "Nothing");
        manager.spawn(x, y, "");
        verifyNoInteractions(area);
    }

    @Test
    void shouldPlaceOnLoad() {
        Terminal terminal = new Terminal();
        AreaService areaService = new AreaService();
        areaService.setManager(manager);
        ServiceLocator.registerTerminalService(terminal);
        ServiceLocator.registerAreaService(areaService);

        manager.load("ragnorok");

        terminal.processMessageBuffer();

        verify(manager).makeBufferedPlace(area);
    }

    @Test
    void shouldLoadCorrectWorld() {
        Terminal terminal = new Terminal();
        AreaService areaService = new AreaService();
        areaService.setManager(manager);
        ServiceLocator.registerTerminalService(terminal);
        ServiceLocator.registerAreaService(areaService);

        manager.load("jot2");

        terminal.processMessageBuffer();

        verify(area, atLeastOnce()).spawnMapChunk(any(int[].class), anyInt(), any(String.class),
                eq("jotunheimr_2"));
    }

    @Test
    void shouldLoadCorrectWorldTwo() {
        Terminal terminal = new Terminal();
        AreaService areaService = new AreaService();
        areaService.setManager(manager);
        ServiceLocator.registerTerminalService(terminal);
        ServiceLocator.registerAreaService(areaService);

        manager.load("ragnorok");

        terminal.processMessageBuffer();

        verify(area, atLeastOnce()).spawnMapChunk(any(int[].class), anyInt(), any(String.class),
                eq("earth_3"));
    }

    @Test
    void shouldLoadLevelCorrectly() {
        // Verify that every call to area that should happen and nothing more.
        Terminal terminal = new Terminal();
        AreaService areaService = new AreaService();
        areaService.setManager(manager);
        ServiceLocator.registerTerminalService(terminal);
        ServiceLocator.registerAreaService(areaService);

        manager.load("jot2");

        terminal.processMessageBuffer();

        verifyJot2(0);
    }

    @Test
    void shouldLoadLevelsSequentially() {
        // Verify that every call to area that should happen and nothing more.
        Terminal terminal = new Terminal();
        AreaService areaService = new AreaService();
        areaService.setManager(manager);
        ServiceLocator.registerTerminalService(terminal);
        ServiceLocator.registerAreaService(areaService);

        manager.load("jot2");

        terminal.processMessageBuffer();

        verifyJot2(0);

        manager.load("jot2");

        terminal.processMessageBuffer();

        verifyJot2(JOT_WIDTH);
    }

    void verifyJot2(int xOffset) {
        String world = "jotunheimr_2";

        // Chunks of platforms and floors
        int[] x = new int[9];
        for (int i = 0; i < 9; i++) {
            x[i] = (xOffset + i) * GRID_SCALE;
        }
        verify(area).spawnMapChunk(x, 0, "floor", world);

        x = new int[4];
        for (int i = 0; i < 4; i++) {
            x[i] = (xOffset + i + 5) * GRID_SCALE;
        }
        verify(area).spawnMapChunk(x, GRID_SCALE, "floor", world);

        x = new int[3];
        for (int i = 0; i < 3; i++) {
            x[i] = (xOffset + i + 6) * GRID_SCALE;
        }
        verify(area).spawnMapChunk(x, 2 * GRID_SCALE, "floor", world);

        x = new int[2];
        for (int i = 0; i < 2; i++) {
            x[i] = (xOffset + i + 7) * GRID_SCALE;
        }
        verify(area).spawnMapChunk(x, 3 * GRID_SCALE, "floor", world);

        // Singular platforms and floors
        verify(area).spawnMapChunk(new int[]{(xOffset + 12) * GRID_SCALE}, GRID_SCALE,
                "platform", world);
        verify(area).spawnMapChunk(new int[]{(xOffset + 14) * GRID_SCALE}, 0, "floor", world);
        verify(area).spawnMapChunk(new int[]{(xOffset + 14) * GRID_SCALE}, GRID_SCALE, "floor"
                , world);

        // Spikes
        verify(area).spawnSpikes((xOffset + 1) * GRID_SCALE, GRID_SCALE);
        verify(area).spawnSpikes((xOffset + 2) * GRID_SCALE, GRID_SCALE);
        verify(area).spawnSpikes((xOffset + 4) * GRID_SCALE, GRID_SCALE);
        verify(area).spawnSpikes((xOffset + 9) * GRID_SCALE, 0);
        verify(area).spawnSpikes((xOffset + 10) * GRID_SCALE, 0);
        verify(area).spawnSpikes((xOffset + 11) * GRID_SCALE, 0);
        verify(area).spawnSpikes((xOffset + 12) * GRID_SCALE, 0);
        verify(area).spawnSpikes((xOffset + 13) * GRID_SCALE, 0);

        // Rock
        verify(area).spawnRocks((xOffset + 7) * GRID_SCALE, 4 * GRID_SCALE);

        // Entities
        verify(area).spawnSkeleton((xOffset + 6) * GRID_SCALE, 3 * GRID_SCALE);
        verify(area).spawnSkeleton((xOffset + 14) * GRID_SCALE, 2 * GRID_SCALE);
        verify(area).spawnSpear((xOffset + 13) * GRID_SCALE, 3 * GRID_SCALE);
        verify(area).spawnLightning((xOffset + 5) * GRID_SCALE, 2 * GRID_SCALE);
        verify(area).spawnLevelLoadTrigger((xOffset + 10) * GRID_SCALE);

        // Background
        // If this fails, it is likely because you have different backgrounds. Congrats! Change
        // this line to reflect that.
        verify(area).spawnBackground(xOffset * GRID_SCALE, JOT_WIDTH, "asgard");
        verifyNoMoreInteractions(area);
    }
}