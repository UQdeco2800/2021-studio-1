package com.deco2800.game.score;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.deco2800.game.extensions.GameExtension;
import com.deco2800.game.score.GameScore;
import com.deco2800.game.services.GameTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(GameExtension.class)
public class GameScoretest {
    GameTime gameTime;
    GameScore gameScore;

    @BeforeEach
    void beforeEach() {
        gameTime = new GameTime();
        gameScore = new GameScore();
    }

    @Test
    void startTimeNotNull(){
        gameTime = new GameTime();
        gameTime.getTime();
        assertEquals(gameTime.getTime(),(0));
    }
}



