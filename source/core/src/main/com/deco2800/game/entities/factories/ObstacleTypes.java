package com.deco2800.game.entities.factories;

public enum ObstacleTypes {
    ROCKS(3),
    SPIKES(5),
    SKELETON(7),
    WOLF(15);

    private final int difficulty;

    ObstacleTypes(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
