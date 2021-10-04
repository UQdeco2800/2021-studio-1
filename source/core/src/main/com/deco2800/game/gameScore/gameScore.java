package com.deco2800.game.gameScore;

import com.deco2800.game.GdxGame;
import com.deco2800.game.components.Component;
import com.deco2800.game.components.player.PlayerStatsDisplay;

import com.deco2800.game.components.powerups.LightningPowerUpComponent;
import com.deco2800.game.components.powerups.SpearPowerUpComponent;
import com.deco2800.game.services.ServiceLocator;
import net.dermetfan.gdx.physics.box2d.PositionController;

import javax.swing.*;


/**
 * The class handles the scoring of the player the score is increased linearly on the basis of time
 * elapsed since the beginning of the start.
 */
public class gameScore extends Component  {
    private GdxGame gdxGame = new GdxGame();


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
            accurateSpearThrow();
            lightningComponent();
            previous_score += (long) (previous_score * 0.001);
            score += (long) Math.floor(previous_score);
        }
        return score;
        // return score;

    }

    /**
     * Score depletion on the basis of object being hit
     */
    public void accurateSpearThrow() {
        SpearPowerUpComponent spear = new SpearPowerUpComponent();
        if (spear.getActive()) {
            score += 1000;
        }
    }

    public void lightningComponent() {
        LightningPowerUpComponent lightning = new LightningPowerUpComponent();
        if (lightning.getActive()) {
            score -= 1000;
        }
    }


    /**
     * Score depletion on the basis of object being hit
     */

    public void obstacleTwoHit() {
        score -= 100;

    }
}

