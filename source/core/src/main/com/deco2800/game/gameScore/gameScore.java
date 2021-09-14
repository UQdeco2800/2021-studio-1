package com.deco2800.game.gameScore;


import com.deco2800.game.components.Component;
import com.deco2800.game.services.GameTime;


/**
 * The class handles the scoring of the player the score is increased linearly on the basis of time
 * elapsed since the beginning of the start.
 */
public class gameScore extends Component {

    GameTime time = new GameTime();
    private long score;
    private long current_time;

    /**
     * Returns the current score to anywhere in the game
     *
     * @return current score
     **/
    public long getCurrentScore() {
        if (entity != null) {
            entity.getEvents().trigger("updateScore", score);
        }
        getCurrentTime();
        setScore();
        return score;
    }

    public void getCurrentTime() {
        current_time = (time.getTime() + 100);
    }

    /**
     * Sets the current score on the basis of timeelapsed since the start of the game
     **/
    public void setScore() {
        getCurrentTime();
        score = current_time / 100;
        if (entity != null) {
            entity.getEvents().trigger("updateScore", score);
        }
    }

    /**
     * Score depletion on the basis of object being hit
     */
    public void obstacleOneHit() {
        score -= 50;
    }

    /**
     * Score depletion on the basis of object being hit
     */

    public void obstacleTwoHit() {
        score -= 100;

    }

}