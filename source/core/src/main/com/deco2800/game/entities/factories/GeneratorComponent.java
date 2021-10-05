package com.deco2800.game.entities.factories;

import com.badlogic.gdx.math.MathUtils;
import com.deco2800.game.areas.ObstacleArea;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;

import java.util.LinkedList;
import java.util.Random;

public class GeneratorComponent extends Component {

    private ObstacleArea generateIn;
    private Entity abstractPlayer;

    private float lastSpawn;
    private float nextSpawn;
    private float spaceBetween;

    private int currentDifficulty;
    private int maxDifficulty;

    private GeneratorState state;
    private LinkedList<ObstacleTypes> obstacles;

    private static final int SIZE = ObstacleTypes.values().length;

    private int ticks;

    public GeneratorComponent(ObstacleArea gameArea, Entity abstractPlayer) {
        this.generateIn = gameArea;
        this.abstractPlayer = abstractPlayer;


    }

    @Override
    public void create() {
        spaceBetween = 5;
        lastSpawn = abstractPlayer.getPosition().x;
        nextSpawn = lastSpawn + spaceBetween;

        state = GeneratorState.PREPOP;

        obstacles = new LinkedList<>();

        currentDifficulty = 5;
    }

    @Override
    public void update() {
        super.update();

        //testingUpdate();
        implementUpdate();
    }

    private void testingUpdate() {
        ticks++;

        if (abstractPlayer.getPosition().x > nextSpawn) {
           //generateIn.spawnWolf()

            //generateIn.spawnPlatform(1, Lanes.LANE1.getLaneHeight(), 20); // these move rly slow

            lastSpawn = abstractPlayer.getPosition().x;
            nextSpawn = lastSpawn + spaceBetween;
        }

        /*if (ticks > 100) {
            generateIn.spawnWolf();
            ticks = 0;
        }*/
    }

    private void implementUpdate() {

        switch (state) {

            case PREPOP:
                maxDifficulty = (int)Math.ceil(currentDifficulty*1.1f);
                currentDifficulty = 0;
                state = GeneratorState.ABSTRACT;
                break;

            case ABSTRACT:
                if (currentDifficulty < maxDifficulty) {
                    ObstacleTypes toAdd = getRandomObstacle();
                    float difficulty = toAdd.getDifficulty();
                    currentDifficulty += difficulty;
                    obstacles.addLast(toAdd);

                } else {
                    state = GeneratorState.CONSTRUCT;
                }
                break;

            case CONSTRUCT:
                if (obstacles.peek() != null) {
                    if (abstractPlayer.getPosition().x > nextSpawn) {

                        //Entity toSpawn = getGeneratedEntity();

                        generateIn.spawnFromGenerator(obstacles.pop());

                        //obstacles.pop();

                        lastSpawn = abstractPlayer.getPosition().x;
                        nextSpawn = lastSpawn + spaceBetween;
                    }
                } else if (obstacles.peek() == null) {
                    state = GeneratorState.POSTPOP;
                }
                break;
            case POSTPOP:
                nextSpawn += 20f;
                state = GeneratorState.PREPOP;

        }

    }

    private ObstacleTypes getRandomObstacle() {

        int randKey = MathUtils.random(SIZE);
        switch (randKey) {
            case 0:
                return ObstacleTypes.ROCKS;
            case 1:
                return ObstacleTypes.SPIKES;
            case 2:
                return ObstacleTypes.SKELETON;
            case 3:
                return ObstacleTypes.WOLF;
            default:
                return ObstacleTypes.ROCKS;

        }
    }

    public String getState() {
        switch(state) {
            case CONSTRUCT:
                return "Construct";
            case PREPOP:
                return "Prepop";
            case ABSTRACT:
                return "Abstract";
            case POSTPOP:
                return "Postpop";
        }

        return "";
    }

    public int getCurrentDifficulty() {
        return currentDifficulty;
    }

    public int getMaxDifficulty() {
        return maxDifficulty;
    }
}

enum GeneratorState {
    PREPOP,
    ABSTRACT,
    CONSTRUCT,
    POSTPOP;

    GeneratorState() {}
}

enum Lanes {
    LANE1(9),
    LANE2(15),
    LANE3(21);

    private final int laneHeight;

    Lanes(int laneHeight) {
        this.laneHeight = laneHeight;
    }

    public int getLaneHeight() {
        return laneHeight;
    }
}

