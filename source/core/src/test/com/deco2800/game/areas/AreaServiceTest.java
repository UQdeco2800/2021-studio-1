package com.deco2800.game.areas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {
    AreaService area;

    @Mock
    AreaManager manager;

    @BeforeEach
    void beforeEach() {
        area = new AreaService();
        area.setManager(manager);
    }

    @Test
    void shouldSetManager() {
        assertEquals(manager, area.getManager(), "Manager does not equal that which was set");
    }

    @Test
    void shouldRunManager() {
        area.run();
        verify(manager).create();
    }

    @Test
    void shouldPlace() {
        int x = 98;
        int y = 7;
        String type = "platform";
        area.place(x, y, type);
        verify(manager).place(x, y, type);
    }

    @Test
    void shouldSpawn() {
        int x = 65;
        int y = 3;
        String type = "wolf";
        area.spawn(x, y, type);
        verify(manager).spawn(x, y, type);
    }

    @Test
    void shouldLoad() {
        String expected = "ragnorok";
        area.load(expected);
        verify(manager).load(expected);
    }

    @Test
    void shouldConfig() {
        String argument = "close";
        String value = "init";
        area.config(argument, value);
        verify(manager).config(argument, value);
    }

    @Test
    void shouldQueue() {
        String line = "....P..F";
        area.queue(line);
        verify(manager).queue(line);
    }
}