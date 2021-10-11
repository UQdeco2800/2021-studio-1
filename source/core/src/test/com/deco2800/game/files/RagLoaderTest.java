package com.deco2800.game.files;

import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.services.ServiceLocator;
import com.deco2800.game.ui.terminal.Terminal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class RagLoaderTest {
    @Mock
    Terminal service;

    @Captor
    ArgumentCaptor<String> captor;

    // Can't make files purely for testing because they would sit in the actual game and could be
    // spawned in thanks to how we use the levels. So, test using levels created to play in the
    // game.

    @BeforeEach
    void beforeEach() {
        ServiceLocator.registerTerminalService(service);
    }

    @Test
    void shouldNotLoadFakeFile() {
        RagLoader.createFromFile("thisIsNotAFileName.program");

        verify(service, never()).sendTerminal(any());
    }

    @Test
    void shouldSendToTerminal() {
        RagLoader.createFromFile("ragnorok");

        verify(service, atLeastOnce()).sendTerminal(any());
    }

    @Test
    void shouldIgnoreBlankLine() {
        // This functionality should exist in createFromFile, but we can't test that without
        // creating a blank file which could cause problems in the game.
        RagLoader.parseLine("comment line");

        verify(service, never()).sendTerminal(any());
    }

    @Test
    void shouldReadConfigTitle() {
        RagLoader.parseLine("_title title");

        verify(service).sendTerminal("-config title title");
    }

    @Test
    void shouldReadConfigWidth() {
        RagLoader.parseLine("_width 102");

        verify(service).sendTerminal("-config width 102");
    }

    @Test
    void shouldReadStartingInfo() {
        // If this test has failed, the information in ragnarok.rag has most likely been changed.
        RagLoader.createFromFile("ragnorok");

        verify(service).sendTerminal("-config title ragTitle");
        verify(service).sendTerminal("-config width 53");
        verify(service).sendTerminal("-config height 10");
        verify(service).sendTerminal("-config world earth_3");
    }

    @Test
    void shouldSendTerrainLine() {
        String line = "#.......F";
        RagLoader.parseLine(line);

        verify(service).sendTerminal("-queue " + line);
    }

    @Test
    void shouldStripWhitespace() {
        String line = "#.......F";
        RagLoader.parseLine(line + "\n");

        verify(service).sendTerminal("-queue " + line);
    }

    @Test
    void shouldSendTerrain() {
        // If this test fails, it is most likely because you have changed the jot2.rag level.
        // Update the following String[] to reflect the changes to terrain.
        RagLoader.createFromFile("jot2");

        String[] lines = {"$#.........F\n",
                "$#........SF\n",
                "$#........SF\n",
                "$#.........F\n",
                "$#........SF\n",
                "$#........FF\n",
                "$#.......FFF\n",
                "$#.....RFFFF\n",
                "$#......FFFF\n",
                "$#.........S\n",
                "$#.........S\n",
                "$#.........S\n",
                "$#........PS\n",
                "$#.........S\n",
                "$#........FF"};

        verify(service, atLeastOnce()).sendTerminal(captor.capture());

        List<String> commands = captor.getAllValues();
        commands.forEach(System.out::println);

        // Assert that each of the terrain lines made it into the terminal
        for (String line : lines) {
            String expected = "-queue " + line.replace("\n", "").replace("$", "");
            assertTrue(commands.contains(expected),
                    "Expected to find the line " + expected + " but didn't.");
        }
    }

    @Test
    void shouldNotCloseInit() {
        RagLoader.parseLine("#.P...F");

        verify(service, never()).sendTerminal("-config close init");
    }

    @Test
    void shouldCloseInit() {
        RagLoader.createFromFile("ragnorok");

        verify(service).sendTerminal("-config close init");
    }

    @Test
    void shouldNotCloseQueue() {
        RagLoader.parseLine("@player set [5,11]");

        verify(service, never()).sendTerminal("-config close queue");
    }

    @Test
    void shouldCloseQueue() {
        RagLoader.createFromFile("ragnorok");

        verify(service).sendTerminal("-config close queue");
    }

    @Test
    void shouldSendCommand() {
        String expected = "-command example";
        RagLoader.parseLine(expected);

        verify(service).sendTerminal(expected);
    }

    @Test
    void shouldLoadFullFile() {
        RagLoader.createFromFile("jot2");

        String[] lines = {"-config title jot2",
                "-config width 15",
                "-config height 10",
                "-config world jotunheimr_2",
                "-config close init",
                "-queue #.........F",
                "-queue #........SF",
                "-queue #........SF",
                "-queue #.........F",
                "-queue #........SF",
                "-queue #........FF",
                "-queue #.......FFF",
                "-queue #.....RFFFF",
                "-queue #......FFFF",
                "-queue #.........S",
                "-queue #.........S",
                "-queue #.........S",
                "-queue #........PS",
                "-queue #.........S",
                "-queue #........FF",
                "-config close queue",
                "-spawn [6,3] (skeleton)",
                "-spawn [14,2] (skeleton)",
                "-spawn [13,3] (spear)",
                "-spawn [5,2] (lightning)",
                "-spawn [10,5] (levelTrigger)"};

        verify(service, atLeastOnce()).sendTerminal(captor.capture());

        List<String> commands = captor.getAllValues();
        commands.forEach(System.out::println);

        // Assert that each of these lines made it into the terminal
        for (String expected : lines) {
            assertTrue(commands.contains(expected),
                    "Expected to find the line " + expected + " but didn't.");
        }

        assertEquals(lines.length, commands.size(),
                "There is an unexpected number of commands send to the terminal");
    }
}