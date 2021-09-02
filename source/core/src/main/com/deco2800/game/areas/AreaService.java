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

public class AreaService {

    private GameArea mainArea;
    private RacerArea mainRacerArea;

    public AreaService() {

    }

    public void setMainArea(GameArea area) {
        mainArea = area;
    }

    public void setMainRacerArea(RacerArea area) {
        mainRacerArea = area;
    }

    public GameArea getMainArea() {
        return mainArea;
    }

    public RacerArea getMainRacerArea() {
        return mainRacerArea;
    }

}
