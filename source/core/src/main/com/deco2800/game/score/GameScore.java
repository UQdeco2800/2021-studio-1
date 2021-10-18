package com.deco2800.game.score;

import com.deco2800.game.components.Component;
import com.deco2800.game.components.player.PlayerStatsDisplay;
import com.deco2800.game.services.ServiceLocator;


/**
 * The class handles the scoring of the player the score is increased linearly in a loop and variations
 * are made on the scoring on basis of various interactions
 */
public class GameScore extends Component  {
    private  long score = 0;
    private long previous_score = 1;

    /**
     * Returns the current score to anywhere in the game
     *
     * @return current score
     **/
    public long getCurrentScore() {

        if (entity != null) {
            entity.getEvents().trigger("updateScore", score);
        }

        // if the game is not paused increment the score and pauses the scoring on player being dead
        if (ServiceLocator.getTimeSource().getDeltaTime() != 0
                && PlayerStatsDisplay.deadFlag == false
        ) {
            // checks if spear being thrown
            lightningComponent();

            //Incremental on the score
            previous_score += (long) (previous_score * 0.001);
            //keeping the score to a non-decimal value
            score += (long) Math.floor(previous_score);
        }
        return score;
    }
    /**
     * Score increases slightly on the basis of lightning power up used
     */
    public void lightningComponent() {
        if (PlayerStatsDisplay.lightningActive) {
            score += 10;
            PlayerStatsDisplay.lightningActive = false;
        }
    }

}

