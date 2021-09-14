package com.deco2800.game.gameScore;
//package com.deco2800.game.gameScore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.services.GameTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(GameExtension.class)
public class gameScoretest {
    GameTime gameTime;
    gameScore gameScore;

    @BeforeEach
    void beforeEach() {
        gameTime = new GameTime();
        gameScore = new gameScore();
    }

    @Test
    void getCurrentScoreCorrectTest(){
        long current_score = gameScore.getCurrentScore();
        assertEquals((gameTime.getTime() + 100)/100, current_score);
    }

    @Test
    void startTimeNotNull(){
        gameTime = new GameTime();
        gameTime.getTime();
        assertEquals(gameTime.getTime(),(0));
    }
}

