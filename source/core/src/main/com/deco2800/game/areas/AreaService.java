package com.deco2800.game.areas;

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
 decided to make AreaService actually AreaManger so people won't call functions on it directly
 and must interface through the terminal using only a handful of commands
 the point of this is to avoid people shooting themselves in the foot...
 */

/*
current idea for implementation:
    make AreaManger which handles such with protected functions (can't be called publicly)
    make an AreaTerminalInterface which extends AreaManger (so can call protected functions)
    that is nested in the Terminal, is passed a reference to the static AreaService...
    AreaService just need not have any public calls, otherwise people will change levels midupdate
    and other fuck shit
 */
public class AreaService {

    private GameArea mainArea; // gameArea is abstract so idk...
    //private RacerArea mainRacerArea;

    public AreaService() {

    }

    public void setMainArea(GameArea area) {
        mainArea = area;
    }

    /*public void setMainRacerArea(RacerArea area) {
        mainRacerArea = area;
    }*/

    public GameArea getMainArea() {
        return mainArea;
    }

    /*public RacerArea getMainRacerArea() {
        return mainRacerArea;
    }*/

}
