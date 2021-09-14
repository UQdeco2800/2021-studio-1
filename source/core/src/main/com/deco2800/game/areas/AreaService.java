package com.deco2800.game.areas;

import java.awt.geom.Area;

/**
 * Facilitates the identitfying, changing, and modifying of areas.
 * Stores the current GameArea (the terminal thus then calls command statically on it)
 * Also faciliates changing the level by safely disposing of the current one and instantiating a new one
 * To be displayed.
 *
 * Ayo also monster idea : possible to instantiated multiple game areas at the same time?
 * Can multiple be created at once?
 * bruuuh , could be so easy to have the cycling levels then, if ones can keep being made...
 * An offest has to remain consistent though i gueeseseeses
 */

/*
 decided to make AreaService actually AreaManager so people won't call functions on it directly
 and must interface through the terminal using only a handful of commands
 the point of this is to avoid people shooting themselves in the foot...
 */

/*
current idea for implementation:
    make AreaManager which handles such with protected functions (can't be called publicly)
    make an AreaTerminalInterface which extends AreaManager (so can call protected functions)
    that is nested in the Terminal, is passed a reference to the static AreaService...
    AreaService just need not have any public calls, otherwise people will change levels midupdate
    and other fuck shit
 */
public class AreaService {

    private AreaManager manager;

    public AreaService() {

    }

    public void setManager(AreaManager man) {
        manager = man;
    }

    public AreaManager getManager() {
        return manager;
    }

    public void run() {
        manager.create();
    }

    public void place(int x, int y, String type) {
        manager.place(x, y, type);
    }

    public void spawn(int x, int y, String type) {
        manager.spawn(x, y, type);
    }

    public void load(String ragFile) {
        manager.load(ragFile);
    }

    public void config(String argument, String value) {
        manager.config(argument, value);
    }

    public void queue(String terrainLine) {
        manager.queue(terrainLine);
    }

}
